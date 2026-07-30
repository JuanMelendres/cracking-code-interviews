---
title: "T-618 · Distributed Transactions: Saga, Outbox, 2PC"
topic_id: T-618
domain: DistributedData
tier: Staff
iwi: 7.65
prerequisites: [T-504, T-505, T-809, T-704]
unlocks: []
week: 10
last_reviewed: 2026-07-29
---

# T-618 · Distributed Transactions: Saga, Outbox, 2PC

**IWI 7.65 · Staff tier · the convergence point of three earlier threads (W3 transactions, W5 idempotency, W8 Kafka)**

**Verification note:** every trace in this chapter is real, executed output from `practice/java/week-10/outbox-publisher/` against a live Postgres 16 (Docker) and single-broker KRaft Kafka cluster — a genuine dual-write failure, and a genuine working transactional outbox with crash recovery. The full working implementation is walked through in `08-outbox-implementation-deliverable.md`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The dual-write hazard, reproduced](#3-the-dual-write-hazard-reproduced)
4. [The transactional outbox, working](#4-the-transactional-outbox-working)
5. [Saga: the multi-service version of the same problem](#5-saga-the-multi-service-version-of-the-same-problem)
6. [Why 2PC is avoided](#6-why-2pc-is-avoided)
7. [Trade-offs](#7-trade-offs)
8. [Interview questions](#8-interview-questions)
9. [Common mistakes](#9-common-mistakes)
10. [Staff-level discussion](#10-staff-level-discussion)
11. [Summary](#11-summary)
12. [Key Takeaways](#12-key-takeaways)
13. [Cheat Sheet](#13-cheat-sheet)
14. [Flashcards](#14-flashcards)
15. [Practice Exercises](#15-practice-exercises)
16. [Additional Reading](#16-additional-reading)
17. [Official References](#17-official-references)

---

## 1. The concept

A **dual write** is any operation that must update two independent systems (a database and a message broker, two separate databases, a database and an external API) as one logical unit, with no shared transaction spanning both. The transactional **outbox** pattern, **Saga**, and **2PC (two-phase commit)** are three different answers to the same underlying question: how do you get atomicity-like guarantees across systems that don't share a transaction manager?

## 2. Why it exists

This topic is explicitly the convergence point of three earlier weeks: Week 3's `@Transactional` semantics (what a single-database transaction actually guarantees), Week 5's idempotency (what makes a redelivered operation safe to repeat), and Week 8's Kafka delivery semantics (T-704's own named gap: "Kafka's exactly-once does not extend to an external database write"). This chapter closes that exact gap — T-704 said the fix was "an outbox or an idempotent consumer"; this is the outbox, built and run for real.

## 3. The dual-write hazard, reproduced

**Real output**, writing an order to Postgres, then simulating a crash before the Kafka publish call is even attempted:

```
== dual write, no outbox: DB commit succeeds, then "crash" before the Kafka publish ==
Order 1 COMMITTED to Postgres, durable, visible to any other reader right now.
Simulating a crash HERE -- before any Kafka publish call is even attempted.
(In the no-outbox design, nothing else in the system knows this order needs an event published.
 There is no queue, no retry, no record of the intent -- the event is simply gone.)

== verifying the order exists but no event was ever published anywhere ==
orders rows for this customer: 1 (the business write DID survive)
Kafka topic 'order-events': 0 messages with key=1 (actually queried Kafka, not asserted -- nothing ever published it, there is no mechanism in this design that could have retried it)
```

**This is the entire problem in one measurement**: the order row is durable and correct, but the event that was supposed to notify the rest of the system about it simply never existed anywhere retryable. No amount of Kafka producer configuration (`acks=all`, idempotence, retries) helps here, because the failure happened **before the producer was ever called** — there's nothing to retry because nothing recorded that a retry was owed.

## 4. The transactional outbox, working

The fix: write the business row **and** an outbox row describing the event to publish, in the **same** database transaction. By construction, either both exist or neither does — Postgres's own transaction guarantee, not application-level coordination, is what makes this atomic. A separate poller then reads unpublished outbox rows and relays them to Kafka, marking each published only after the send is confirmed.

```mermaid
sequenceDiagram
    participant App
    participant DB as Postgres (orders + outbox)
    participant Poller
    participant Kafka
    App->>DB: BEGIN; INSERT order; INSERT outbox row; COMMIT (one transaction)
    Note over DB: atomic -- both rows or neither
    Poller->>DB: SELECT unpublished outbox rows
    Poller->>Kafka: publish event
    Kafka-->>Poller: ack
    Poller->>DB: UPDATE outbox SET published=true
```

**Real output**, the full crash-recovery sequence: write 3 orders atomically, run the poller with a simulated crash right after publishing row 1 (but before marking it published), then restart the poller:

```
== 1. write 3 orders atomically ==
Committed order 1 + its outbox row, atomically, in one transaction.
Committed order 2 + its outbox row, atomically, in one transaction.
Committed order 3 + its outbox row, atomically, in one transaction.

== 2. poller run 1: crashes right after publishing row 1 ==
Published outbox row 1 (OrderCreated, aggregate=1) to Kafka.
Simulating a crash HERE -- Kafka publish confirmed, but BEFORE marking outbox row 1 as published in Postgres.

== 3. poller run 2 (restart): redelivers row 1, publishes 2 and 3 ==
Published outbox row 1 (OrderCreated, aggregate=1) to Kafka.
Published outbox row 2 (OrderCreated, aggregate=2) to Kafka.
Published outbox row 3 (OrderCreated, aggregate=3) to Kafka.
Poller pass complete: 3 row(s) published this pass.

== 5. verify Kafka: what actually landed ==
  [1] key=1 value={"orderId":1,"customerId":"outbox-customer-0"}
  [2] key=1 value={"orderId":1,"customerId":"outbox-customer-0"}
  [3] key=2 value={"orderId":2,"customerId":"outbox-customer-1"}
  [4] key=3 value={"orderId":3,"customerId":"outbox-customer-2"}
Total messages ever published to order-events: 4
```

**3 orders, 4 messages, zero lost.** Order 1's event was published twice — a real, measured duplicate — because the crash landed in the one window the outbox pattern does NOT make atomic: between "Kafka confirmed the send" and "Postgres marked the row published." This is precisely why the outbox pattern gives **at-least-once** delivery, not exactly-once (the same distinction T-704 drew for Kafka's own delivery semantics) — and precisely why the downstream consumer of `order-events` must be idempotent (T-809) to handle that duplicate safely. What the pattern *does* guarantee, and what this run proves: **the event is never silently lost**, no matter where in the poller's cycle a crash lands, because the outbox row persists, unpublished, until a poller (this one or a replacement) successfully relays it.

## 5. Saga: the multi-service version of the same problem

A **Saga** extends the same idea across multiple services, each with its own local transaction, coordinated by a sequence of steps with **compensating actions** for rollback (there is no cross-service `ROLLBACK` — a Saga undoes a completed step by running another forward-moving action that reverses its effect, e.g., refunding a charge rather than "un-charging" it). Two coordination styles:

- **Orchestration**: a central coordinator explicitly calls each service and invokes compensations on failure — easier to reason about and debug (one place to look), but the coordinator becomes a structural dependency every step goes through.
- **Choreography**: each service reacts to events from the previous step and emits its own — no central coordinator, but the overall flow is implicit, reconstructed only by tracing events across services, which is genuinely harder to debug ("trace a request across seven services," the exact follow-up the blueprint names for event-driven architecture generally).

The outbox pattern is frequently the mechanism a Saga step uses to reliably publish "my step succeeded, here's the next event" without its own dual-write hazard — Sagas and the outbox aren't competing solutions, the outbox is often literally inside a Saga step.

## 6. Why 2PC is avoided

Two-phase commit achieves genuine atomicity across two systems via a coordinator that asks all participants to "prepare" (lock resources, promise to commit) before telling them all to actually commit. It's avoided in practice for a specific, structural reason: **every participant must hold its lock for the entire duration of the coordinator's round trip**, including if the coordinator itself crashes mid-protocol — a participant that's "prepared" but never receives the final commit/abort decision is stuck holding its lock indefinitely (the "in-doubt transaction" problem). This makes 2PC fundamentally unsuited to systems that need to stay available under partial failure, which is most systems Sagas and outboxes are designed for.

## 7. Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| Dual write, no coordination | Simplest to write | Real, measured event loss on any crash between the two writes (§3) |
| Transactional outbox | No event loss, ever — measured in §4 | At-least-once, not exactly-once; needs an idempotent consumer downstream; adds a poller (or CDC) as new infrastructure |
| Saga (orchestrated) | Central visibility into the flow; easier debugging | Coordinator is a structural dependency for every step |
| Saga (choreographed) | No central dependency | Flow is implicit — genuinely harder to trace and debug |
| 2PC | True cross-system atomicity | Locks held across a coordinator round-trip; in-doubt transactions on coordinator failure; poor availability under partial failure |

## 8. Interview questions

### Q1. You wrote to the DB and published to Kafka. Prove no message is lost.

- **Expected answer:** cannot be proven for a plain dual write — §3's measurement is the proof that it fails. The outbox pattern is what makes the claim provable: the event only exists as "not yet published," never as "gone," because it's written atomically with the business row.
- **Common mistakes:** proposing "just retry the Kafka call" without addressing that a crash before the call means there's nothing to retry.
- **Follow-up questions:** "Your poller crashed after publishing but before marking published. What happens on restart?"
- **Senior-level expectations:** names the outbox pattern and the poller mechanism.
- **Staff-level expectations:** states the at-least-once guarantee precisely (not exactly-once) and names the idempotent-consumer requirement this creates downstream, unprompted — connecting directly to T-704 and T-809.

### Q2. Compensate a charged payment.

- **Expected answer:** a Saga compensating action is a forward-moving business operation (issue a refund), not a rollback of the original charge — there is no cross-service transaction to roll back.
- **Common mistakes:** describing compensation as "undoing" the original operation as if it were a database rollback.
- **Follow-up questions:** "What if the refund itself fails?"
- **Senior-level expectations:** correctly frames compensation as a new forward action.
- **Staff-level expectations:** discusses compensations needing their own retry/idempotency treatment (a failed refund is itself a dual-write-shaped problem) rather than assuming compensating actions are automatically reliable.

## 9. Common mistakes

- Believing a DB write plus a message publish can be made atomic without an outbox or equivalent mechanism (2PC's own availability cost is exactly why it's not the default answer).
- Treating Saga compensation as a rollback rather than a new forward-moving business action.
- Forgetting that the outbox pattern's guarantee is at-least-once, and skipping the idempotent-consumer requirement that creates downstream.

## 10. Staff-level discussion

This chapter's real measurement — 3 writes, 4 deliveries, zero losses — is the concrete version of an architectural principle that recurs constantly at Staff scope: **prefer at-least-once with idempotency over attempting exactly-once through coordination.** 2PC attempts the latter and pays for it in availability; the outbox achieves a strictly weaker guarantee (at-least-once) with a strictly better availability profile, and pushes the remaining correctness requirement (handling duplicates) to a place — an idempotent consumer — that's individually simple to build. A Staff engineer recognizes this trade as the same shape appearing across distributed systems generally: exactly-once coordination is expensive and fragile; at-least-once plus idempotency is cheap and robust, and is preferred by default unless a specific requirement rules it out.

## 11. Summary

A plain dual write measurably loses events on a crash between the two writes (§3). The transactional outbox eliminates that loss by making the business write and the event-intent write one atomic database transaction, at the cost of at-least-once (not exactly-once) delivery — demonstrated directly with a real crash-recovery run producing one genuine, measured duplicate and zero losses (§4). Saga extends the same at-least-once-plus-idempotency philosophy across multiple services with compensating actions instead of rollback; 2PC offers true atomicity but is avoided for its availability cost under coordinator or participant failure.

## 12. Key Takeaways

- A dual write with no coordination has no way to guarantee the second write happens if the first succeeds and a crash follows — measured, not theoretical.
- The outbox pattern's atomicity comes from a single database transaction, not application-level coordination.
- The outbox pattern is at-least-once, not exactly-once — the downstream consumer must be idempotent.
- Saga compensations are forward-moving business actions, never a cross-service rollback.
- 2PC is avoided primarily for its availability cost (locks held across a coordinator round-trip), not because atomicity itself is undesirable.

## 13. Cheat Sheet

| Need | Mechanism |
|---|---|
| Atomic business write + event-intent, one system | Transactional outbox |
| Multi-service workflow with rollback-like behavior | Saga with compensating actions |
| True cross-system atomicity, availability cost acceptable | 2PC (rare in practice) |
| Downstream safety against outbox's at-least-once duplicates | Idempotent consumer (T-809) |

## 14. Flashcards

1. **Q: What specifically does a dual write (DB write + separate message publish) fail to guarantee?** A: That the message gets published if the DB write succeeds and a crash follows before the publish call — there's no shared transaction, so nothing records that a publish is owed.
2. **Q: What makes the transactional outbox's atomicity possible?** A: Writing the business row and the outbox row in the SAME database transaction — Postgres's own transaction guarantee, not application coordination.
3. **Q: Is the transactional outbox exactly-once or at-least-once?** A: At-least-once — a crash between "Kafka confirms the send" and "DB marks the row published" causes a real, measured duplicate; the downstream consumer must be idempotent.
4. **Q: Why is 2PC avoided in practice despite offering true atomicity?** A: Locks are held across the coordinator's round-trip; a coordinator crash leaves "prepared" participants stuck indefinitely (in-doubt transactions) — poor availability under partial failure.

(Full week-level deck: `07-flashcards.md`.)

## 15. Practice Exercises

1. Reproduce the full sequence: `practice/java/week-10/outbox-publisher/` — see `08-outbox-implementation-deliverable.md` for exact commands.
2. Modify `OutboxPoller` to crash BEFORE the Kafka publish call instead of after, and confirm no duplicate occurs on restart (but also confirm no loss) — what changes about the guarantee?
3. Design a compensating action for a Saga step that reserves inventory, given that "reservations" don't support a native rollback in the inventory service's API.

## 16. Additional Reading

- [microservices.io — Pattern: Transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html)

## 17. Official References

- [Debezium documentation — Outbox Event Router](https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html) — the CDC-based alternative to the polling publisher built in this chapter
