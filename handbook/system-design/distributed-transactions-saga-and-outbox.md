---
title: "Distributed Transactions: Saga, Outbox, and 2PC"
slug: distributed-transactions-saga-and-outbox
document_type: handbook-chapter
domain: system-design
status: draft
version: 1.0
last_updated: 2026-07-30
difficulty:
  - advanced
target_levels:
  - senior
  - staff
estimated_reading_minutes: 40
prerequisites:
  - idempotency.md
  - ../spring/transactional-proxy-mechanics-and-propagation.md
  - ../kafka/delivery-semantics-and-exactly-once.md
related:
  - idempotency.md
  - messaging-patterns-and-change-data-capture.md
  - ../spring/transactional-proxy-mechanics-and-propagation.md
  - ../kafka/delivery-semantics-and-exactly-once.md
  - ../architecture/cqrs-read-write-separation.md
  - ../architecture/event-driven-architecture-integration-styles.md
  - ../architecture/event-sourcing-and-its-real-costs.md
  - ../../study-packs/week-10/01-saga-outbox-and-distributed-transactions.md
official_references:
  - https://microservices.io/patterns/data/transactional-outbox.html
  - https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html
---

# Distributed Transactions: Saga, Outbox, and 2PC

> **Topic register:** T-618 · IWI 7.65 (top-25 of 198) · Staff tier · High interview frequency [H] — the convergence point of three earlier threads
> **Why this is a convergence chapter:** this topic sits directly downstream of three others already covered — Spring `@Transactional` semantics (what a single-database transaction actually guarantees), idempotency (what makes a redelivered operation safe to repeat), and Kafka's delivery semantics (whose own named gap this chapter closes: "Kafka's exactly-once does not extend to an external database write").
> **Provenance:** every trace in this chapter is real, executed output from [`practice/java/week-10/outbox-publisher/`](../../practice/java/week-10/outbox-publisher/) against a live PostgreSQL 16 (Docker) and single-broker KRaft Kafka cluster — a genuine dual-write failure, and a genuine working transactional outbox with crash recovery.

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Mental Model](#mental-model)
4. [Definition and Purpose](#definition-and-purpose)
5. [Core Concepts](#core-concepts)
6. [Internal Implementation](#internal-implementation)
7. [Diagrams](#diagrams)
8. [Java Examples](#java-examples)
9. [Production Scenarios](#production-scenarios)
10. [Failure Modes and Debugging](#failure-modes-and-debugging)
11. [Trade-offs](#trade-offs)
12. [Decision Framework](#decision-framework)
13. [Comparisons](#comparisons)
14. [Common Mistakes](#common-mistakes)
15. [Anti-Patterns](#anti-patterns)
16. [Best Practices](#best-practices)
17. [Interview Answer Framework](#interview-answer-framework)
18. [Interview Questions](#interview-questions)
19. [Summary](#summary)
20. [Key Takeaways](#key-takeaways)
21. [Cheat Sheet](#cheat-sheet)
22. [Flashcards](#flashcards)
23. [Practice Exercises](#practice-exercises)
24. [Solutions](#solutions)
25. [Additional Reading](#additional-reading)
26. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can:

- Explain and reproduce the dual-write hazard — why a database write and a message publish cannot be made atomic without extra machinery.
- Design a transactional outbox: the atomicity mechanism, the poller, and precisely what guarantee (at-least-once, not exactly-once) it provides.
- Distinguish Saga orchestration from choreography and correctly frame a compensating action as a forward-moving business operation, never a rollback.
- Explain precisely why 2PC is avoided in practice, in terms of availability under coordinator/participant failure, not just "it's slow."

## Why This Matters in Interviews

This topic is where three of the handbook's other threads converge into one concrete, buildable mechanism, which makes it a strong signal of whether a candidate can synthesize rather than recite: Spring's transaction guarantees, idempotency's redelivery-safety mechanism, and Kafka's exactly-once scope all get tested at once. It's High-frequency specifically because "you wrote to the database and published to a queue — prove no message is lost" is a question with no correct answer for a plain dual write, and the candidate's ability to recognize that — rather than proposing "just add retries" — is exactly the signal this topic is built to surface.

## Mental Model

**A dual write is a promise made to two systems that don't know about each other, and a crash between them breaks the promise silently.** The transactional outbox, Saga, and 2PC are three different answers to the same underlying question: how do you get atomicity-like guarantees across systems that don't share a transaction manager? The outbox answer: make the promise to only *one* system (the database), and have that system carry the intent forward reliably. The Saga answer: don't promise atomicity at all — promise a sequence of individually-committed steps, each with an undo. The 2PC answer: actually coordinate a real cross-system commit, and pay for it in availability.

## Definition and Purpose

A **dual write** is any operation that must update two independent systems (a database and a message broker, two separate databases, a database and an external API) as one logical unit, with no shared transaction spanning both. The transactional **outbox** pattern, **Saga**, and **2PC (two-phase commit)** are three different answers to the same underlying question: how do you get atomicity-like guarantees across systems that don't share a transaction manager? This topic exists because a crash landing between the two writes of an uncoordinated dual write silently loses one side's effect with no mechanism to detect or retry it — and each of the three patterns closes that gap with a different trade-off between guarantee strength and availability cost.

## Core Concepts

### The dual-write hazard

Writing a business row to a database and separately calling a message broker to publish an event about it are two independent operations. If the process crashes between them — even if the database write fully committed — nothing anywhere records that the publish was owed. No amount of message-broker producer configuration helps, because the failure happens *before the producer is ever called*.

### The transactional outbox

The fix: write the business row **and** an outbox row describing the event to publish, in the **same** database transaction. By construction, either both exist or neither does — the database's own transaction guarantee, not application-level coordination, is what makes this atomic. A separate poller then reads unpublished outbox rows and relays them to the message broker, marking each published only after the send is confirmed.

### The outbox's actual guarantee: at-least-once, not exactly-once

A crash between "the broker confirmed the send" and "the database marked the row published" produces a real, measured duplicate — the outbox pattern gives **at-least-once** delivery, never exactly-once, which is why the downstream consumer must be idempotent. What it *does* guarantee: the event is never silently lost, no matter where in the poller's cycle a crash lands, because the outbox row persists, unpublished, until a poller successfully relays it.

### Saga: the multi-service version of the same problem

A **Saga** extends the same idea across multiple services, each with its own local transaction, coordinated by a sequence of steps with **compensating actions** for rollback — there is no cross-service `ROLLBACK`; a Saga undoes a completed step by running another forward-moving action that reverses its effect (e.g., issuing a refund rather than "un-charging"). Two coordination styles: **orchestration** (a central coordinator explicitly calls each service and invokes compensations on failure — easier to debug, but the coordinator is a structural dependency for every step) and **choreography** (each service reacts to events from the previous step and emits its own — no central coordinator, but the flow is implicit and genuinely harder to trace). The outbox pattern is frequently the mechanism a Saga step uses to reliably publish "my step succeeded, here's the next event" — Sagas and the outbox aren't competing solutions; the outbox is often literally inside a Saga step.

### Why 2PC is avoided

Two-phase commit achieves genuine atomicity across two systems via a coordinator that asks all participants to "prepare" (lock resources, promise to commit) before telling them all to actually commit. It's avoided in practice because every participant must hold its lock for the entire duration of the coordinator's round trip, including if the coordinator itself crashes mid-protocol — a participant that's "prepared" but never receives the final decision is stuck holding its lock indefinitely (the "in-doubt transaction" problem). This makes 2PC fundamentally unsuited to systems that need to stay available under partial failure.

## Internal Implementation

**The dual-write hazard, reproduced** — writing an order to Postgres, then simulating a crash before the Kafka publish call is even attempted:

```
== dual write, no outbox: DB commit succeeds, then "crash" before the Kafka publish ==
Order 1 COMMITTED to Postgres, durable, visible to any other reader right now.
Simulating a crash HERE -- before any Kafka publish call is even attempted.

== verifying the order exists but no event was ever published anywhere ==
orders rows for this customer: 1 (the business write DID survive)
Kafka topic 'order-events': 0 messages with key=1 (actually queried Kafka, not asserted)
```

The order row is durable and correct, but the event that was supposed to notify the rest of the system simply never existed anywhere retryable.

**The transactional outbox, working** — the full crash-recovery sequence: write 3 orders atomically, run the poller with a simulated crash right after publishing row 1 (but before marking it published), then restart the poller:

```
== 1. write 3 orders atomically ==
Committed order 1 + its outbox row, atomically, in one transaction.
Committed order 2 + its outbox row, atomically, in one transaction.
Committed order 3 + its outbox row, atomically, in one transaction.

== 2. poller run 1: crashes right after publishing row 1 ==
Published outbox row 1 (OrderCreated, aggregate=1) to Kafka.
Simulating a crash HERE -- Kafka publish confirmed, but BEFORE marking outbox row 1 as published.

== 3. poller run 2 (restart): redelivers row 1, publishes 2 and 3 ==
Published outbox row 1 (OrderCreated, aggregate=1) to Kafka.
Published outbox row 2 (OrderCreated, aggregate=2) to Kafka.
Published outbox row 3 (OrderCreated, aggregate=3) to Kafka.

== 5. verify Kafka: what actually landed ==
  [1] key=1 value={"orderId":1,"customerId":"outbox-customer-0"}
  [2] key=1 value={"orderId":1,"customerId":"outbox-customer-0"}
  [3] key=2 value={"orderId":2,"customerId":"outbox-customer-1"}
  [4] key=3 value={"orderId":3,"customerId":"outbox-customer-2"}
Total messages ever published to order-events: 4
```

**3 orders, 4 messages, zero lost.** Order 1's event was published twice — a real, measured duplicate — because the crash landed in the one window the outbox pattern does NOT make atomic: between "the broker confirmed the send" and "the database marked the row published." This is precisely why the outbox pattern gives at-least-once delivery, and precisely why the downstream consumer of `order-events` must be idempotent (see [Idempotency at System Edges](idempotency.md)) to handle that duplicate safely.

## Diagrams

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

## Java Examples

```java
// Java 21 / Spring. Writing the business row and outbox row atomically —
// the atomicity comes from @Transactional wrapping BOTH inserts in one
// database transaction, not from any application-level coordination.

@Service
public class OrderService {

    @Transactional
    public void placeOrder(Order order) {
        orderRepository.save(order);

        OutboxEvent event = new OutboxEvent(
            "OrderCreated",
            order.getId().toString(),
            serialize(new OrderCreatedPayload(order))
        );
        outboxRepository.save(event); // same transaction as the order insert —
                                        // either both commit or neither does
    }
}

// The poller: reads unpublished rows, publishes, marks published only after
// the broker confirms — the window between confirm and mark-published is
// exactly where the measured duplicate in this chapter comes from.
@Component
public class OutboxPoller {

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void poll() {
        List<OutboxEvent> unpublished = outboxRepository.findUnpublished();
        for (OutboxEvent event : unpublished) {
            kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                .get(); // block for ack — confirmed send
            // If a crash happens between the line above and the line below,
            // this event will be republished on the next poller run: at-least-once.
            outboxRepository.markPublished(event.getId());
        }
    }
}
```

**Complexity note:** all operations are `O(1)` per event; the entire value of this chapter is correctness under partial failure, not algorithmic cost.

## Production Scenarios

### Scenario: silent order-notification loss traced to an uncoordinated dual write

**Symptoms.** A small, unpredictable fraction of successfully-placed orders never trigger a confirmation email or downstream fulfillment event, discovered only via customer complaints of "I ordered but got no confirmation," not via any internal alert.

**Impact.** Silent, intermittent business-process failure with no corresponding error signal — the order itself always succeeded.

**Initial hypotheses.** An email-service outage (checked — the email service's own logs show no corresponding failed sends, because it never received the trigger event at all); a Kafka producer misconfiguration (checked — producer logs show no send failures for the affected orders, because no send was ever attempted); an uncoordinated dual write (correct).

**Evidence.** Application deployment logs show a pattern of restarts (routine deploys, occasional crashes) correlating with the missing-notification incidents; the order-placement code path writes to the orders table, then makes a separate, non-transactional call to publish a Kafka event afterward.

**Diagnosis.** Exactly this chapter's dual-write hazard: on the small fraction of order placements where a deploy or crash landed between the database commit and the Kafka publish call, the order row committed successfully but the publish call was never made and nothing recorded that it was owed.

**Immediate mitigation.** Manually identify affected orders via a reconciliation query (orders with no corresponding published event) and manually trigger their notifications.

**Permanent remediation.** Implement the transactional outbox pattern from this chapter: write the order and an outbox row in the same transaction, and add a poller that reliably relays outbox rows to Kafka — eliminating the possibility of the gap entirely, and making the downstream email/fulfillment consumers idempotent to handle the outbox's at-least-once duplicates safely.

**Alternatives considered.** Wrapping the Kafka publish call in a retry loop at the point of failure — rejected, since a crash before the call is even reached leaves nothing to retry; the retry loop only helps if the process survives long enough to attempt and fail the call, which is not the failure mode actually observed.

**Trade-offs.** The outbox pattern introduces new infrastructure (the poller, or a CDC-based alternative) and requires the downstream consumer to handle duplicates — accepted, since the alternative is unrecoverable, silent event loss.

**Prevention.** Any code path that writes to a database and separately calls an external system (a message broker, another service) as part of the same logical operation should be reviewed for this exact hazard — the fix is always either an outbox, or an idempotent design that tolerates the dual-write's own failure mode by some other means.

**Interview lesson.** This is Interview Question 1 (§ Interview Questions) — "you wrote to the DB and published to Kafka, prove no message is lost" — arriving as a real incident: the honest answer for a plain dual write is that it cannot be proven, and this incident is the proof of that claim landing in production.

## Failure Modes and Debugging

| Symptom | Likely cause | Debugging step |
|---|---|---|
| A business operation succeeds but a downstream notification/event never arrives, intermittently | Uncoordinated dual write — a crash between the database commit and the separate publish call | Check for a code path writing to a database and separately, non-transactionally, calling an external system |
| The same event appears published twice in the message broker | The outbox poller crashed between confirming the send and marking the row published — expected, at-least-once behavior | Verify the downstream consumer is idempotent (keyed by a stable event/aggregate ID); this is not a bug to eliminate, but a condition to design for |
| A Saga step's compensation itself fails, leaving the workflow in an inconsistent state | Compensations were assumed to be automatically reliable, with no retry/idempotency treatment of their own | Treat every compensating action as its own dual-write-shaped problem, needing the same retry/idempotency discipline as the forward path |
| A 2PC-coordinated transaction is stuck, holding locks indefinitely | Coordinator crashed mid-protocol, leaving a participant "prepared" with no final decision (an in-doubt transaction) | This is the structural reason 2PC is avoided; recovery typically requires manual intervention or a heuristic decision, both of which risk correctness |

## Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| Dual write, no coordination | Simplest to write | Real, measured event loss on any crash between the two writes |
| Transactional outbox | No event loss, ever — measured directly | At-least-once, not exactly-once; needs an idempotent consumer downstream; adds a poller (or CDC) as new infrastructure |
| Saga (orchestrated) | Central visibility into the flow; easier debugging | Coordinator is a structural dependency for every step |
| Saga (choreographed) | No central dependency | Flow is implicit — genuinely harder to trace and debug |
| 2PC | True cross-system atomicity | Locks held across a coordinator round-trip; in-doubt transactions on coordinator failure; poor availability under partial failure |

## Decision Framework

1. **Does this operation need to atomically update a database and notify another system** (a message broker, another service)? If yes, a plain dual write is unsafe — use an outbox or equivalent.
2. **Is the downstream consumer of the outbox's events idempotent?** It must be, since the outbox guarantees at-least-once, never exactly-once.
3. **Does this workflow span multiple services, each with its own local transaction?** If so, design it as a Saga with explicit compensating actions — never assume a cross-service rollback exists.
4. **Is a compensating action itself reliable under retry?** Treat every compensation as its own dual-write-shaped problem requiring the same idempotency discipline as the forward path.
5. **Is true cross-system atomicity genuinely required, and is the resulting availability cost acceptable?** Only then consider 2PC — and even then, expect to justify why an outbox/Saga approach wasn't sufficient.

## Comparisons

| Mechanism | Guarantee | Availability under partial failure |
|---|---|---|
| Dual write, uncoordinated | None — can silently lose one side | N/A (the guarantee itself is broken) |
| Transactional outbox | At-least-once, never lost | High — no cross-system lock held |
| Saga | At-least-once per step, with compensation for rollback-like behavior | High — each step is a local transaction |
| 2PC | Exactly-once/true atomicity | Low — locks held across the coordinator round-trip; in-doubt transactions possible |

## Common Mistakes

- Believing a DB write plus a message publish can be made atomic without an outbox or equivalent mechanism.
- Treating Saga compensation as a rollback rather than a new forward-moving business action.
- Forgetting that the outbox pattern's guarantee is at-least-once, and skipping the idempotent-consumer requirement that creates downstream.

## Anti-Patterns

- **A "just add retries around the Kafka call" fix** for the dual-write hazard, which does nothing for the case where the crash happens before the call is ever reached.
- **Assuming Saga compensating actions are automatically reliable** without their own retry/idempotency treatment.
- **Reaching for 2PC by default** for cross-system coordination without first evaluating whether an outbox or Saga's weaker-but-available guarantee is actually sufficient — which it usually is.
- **Building an outbox without making the downstream consumer idempotent**, silently reintroducing a duplicate-processing bug the pattern's own guarantee requires handling.

## Best Practices

- Default to the transactional outbox pattern (or a CDC-based equivalent like Debezium) for any operation that must atomically update a database and notify another system.
- Always pair an outbox with an explicitly idempotent downstream consumer — the at-least-once guarantee requires it.
- Design Saga compensating actions as forward-moving business operations with their own retry/idempotency treatment, never as an assumed-reliable rollback.
- Reserve 2PC for cases where true cross-system atomicity is a hard requirement and the availability cost has been explicitly evaluated and accepted.

## Interview Answer Framework

### 30-Second Answer

A dual write (a database write plus a separate message publish) can silently lose the message on a crash between the two — measured directly. The transactional outbox fixes this by writing the business row and an outbox row in one database transaction, then a poller reliably relays it — giving at-least-once delivery, never exactly-once, which requires an idempotent downstream consumer.

### 2-Minute Answer

Definition: a dual write updates two independent systems with no shared transaction; the outbox, Saga, and 2PC are three answers to making that safe. Why it exists: a crash between the two writes of an uncoordinated dual write silently loses one side, with nothing to retry. How it works: the outbox writes the business row and an event-intent row atomically in one database transaction, then a poller relays unpublished rows to the message broker. One important trade-off: the outbox gives at-least-once, not exactly-once — a crash between broker-confirm and mark-published produces a real duplicate. Production example: a measured demo writing 3 orders and producing exactly 4 published messages — one genuine duplicate from a simulated poller crash, and zero losses, proving the pattern's guarantee directly rather than asserting it.

### 10-Minute Deep Dive

Cover, in order: the dual-write hazard as the unifying problem across three earlier chapters — transactions, idempotency, Kafka delivery semantics (mental model + convergence framing); the measured dual-write failure, a genuinely lost event (internals, real evidence); the transactional outbox's atomicity mechanism and the measured crash-recovery run producing one duplicate and zero losses (internals); Saga orchestration vs. choreography and compensating actions as forward-moving operations, not rollbacks (edge case); why 2PC is avoided — the in-doubt transaction problem, not just "it's slow" (failure mode + Staff framing); and close with the production scenario — a real silent-notification-loss incident traced to exactly the uncoordinated dual write this chapter measures directly.

### Whiteboard Explanation

Draw the [§ Diagrams](#diagrams) sequence: App → DB (one transaction, order + outbox row) → Poller reads → Poller publishes to Kafka → Poller marks published. Circle the gap between "Poller publishes" and "Poller marks published" and label it: "a crash here produces a duplicate — this is the only window not made atomic." This is the exact detail that makes "at-least-once, not exactly-once" concrete rather than asserted.

### Production Example

The silent-notification-loss incident in [§ Production Scenarios](#production-scenarios): an uncoordinated dual write silently dropped order-confirmation events on a small fraction of deploys/crashes, discovered only via customer complaints — fixed by implementing the transactional outbox pattern with an idempotent downstream consumer.

### Trade-offs to Mention

State unprompted: the outbox pattern gives at-least-once, never exactly-once — the downstream consumer must be idempotent; Saga compensations are forward-moving business actions, never a cross-service rollback; 2PC is avoided primarily for its availability cost under coordinator/participant failure, not because atomicity itself is undesirable.

### Common Candidate Mistakes

Proposing "just retry the Kafka call" without addressing that a crash before the call means there's nothing to retry; describing Saga compensation as "undoing" the original operation like a database rollback; forgetting the outbox's at-least-once nature and skipping the idempotent-consumer requirement.

### Typical Follow-Up Questions

1. "Your poller crashed after publishing but before marking published. What happens on restart?"
2. "What if the refund itself fails?"
3. "Why is 2PC avoided despite offering true atomicity?"

### Senior-Level Expectations

Names the outbox pattern and the poller mechanism; correctly frames compensation as a new forward action, not a rollback.

### Staff-Level Discussion

This chapter's real measurement — 3 writes, 4 deliveries, zero losses — is the concrete version of an architectural principle that recurs constantly at Staff scope: **prefer at-least-once with idempotency over attempting exactly-once through coordination.** 2PC attempts the latter and pays for it in availability; the outbox achieves a strictly weaker guarantee (at-least-once) with a strictly better availability profile, and pushes the remaining correctness requirement (handling duplicates) to a place — an idempotent consumer — that's individually simple to build. A Staff engineer recognizes this trade as the same shape appearing across distributed systems generally: exactly-once coordination is expensive and fragile; at-least-once plus idempotency is cheap and robust, and is preferred by default unless a specific requirement rules it out.

## Interview Questions

### Question 1 — You wrote to the DB and published to Kafka. Prove no message is lost.

**Why interviewers ask it.** Cannot be answered correctly for a plain dual write — the honest response ("it can't be proven this way") is itself the signal being tested.

**Expected answer.** Cannot be proven for a plain dual write — the measured dual-write demo is the proof it fails. The outbox pattern is what makes the claim provable: the event only exists as "not yet published," never as "gone," because it's written atomically with the business row.

**Minimum acceptable answer.** Recognizes a plain dual write is unsafe, even without proposing the specific fix.

**Strong Senior answer.** Names the outbox pattern and the poller mechanism.

**Staff-level extension.** States the at-least-once guarantee precisely (not exactly-once) and names the idempotent-consumer requirement this creates downstream, unprompted.

**Common mistakes.** Proposing "just retry the Kafka call" without addressing that a crash before the call means there's nothing to retry.

**Likely follow-ups.** "Your poller crashed after publishing but before marking published. What happens on restart?"

**Evaluation criteria (1–5).** 1: "just add retries." 3: names the outbox pattern correctly. 5: outbox pattern plus precise at-least-once guarantee plus the idempotent-consumer requirement.

**Related references.** [§ Internal Implementation](#internal-implementation); [Idempotency at System Edges](idempotency.md).

---

### Question 2 — Compensate a charged payment.

**Why interviewers ask it.** Tests whether the candidate understands Saga compensation as fundamentally different from a database rollback.

**Expected answer.** A Saga compensating action is a forward-moving business operation (issue a refund), not a rollback of the original charge — there is no cross-service transaction to roll back.

**Minimum acceptable answer.** Proposes a refund, even without framing it explicitly as "forward-moving, not a rollback."

**Strong Senior answer.** Correctly frames compensation as a new forward action.

**Staff-level extension.** Discusses compensations needing their own retry/idempotency treatment (a failed refund is itself a dual-write-shaped problem) rather than assuming compensating actions are automatically reliable.

**Common mistakes.** Describing compensation as "undoing" the original operation as if it were a database rollback.

**Likely follow-ups.** "What if the refund itself fails?"

**Evaluation criteria (1–5).** 1: describes an "undo" as if it were a rollback. 3: correctly frames it as a forward-moving refund. 5: forward-moving refund plus its own idempotency/retry treatment discussed.

**Related references.** [§ Core Concepts](#core-concepts).

## Summary

A plain dual write measurably loses events on a crash between the two writes. The transactional outbox eliminates that loss by making the business write and the event-intent write one atomic database transaction, at the cost of at-least-once (not exactly-once) delivery — demonstrated directly with a real crash-recovery run producing one genuine, measured duplicate and zero losses. Saga extends the same at-least-once-plus-idempotency philosophy across multiple services with compensating actions instead of rollback; 2PC offers true atomicity but is avoided for its availability cost under coordinator or participant failure.

## Key Takeaways

- A dual write with no coordination has no way to guarantee the second write happens if the first succeeds and a crash follows — measured, not theoretical.
- The outbox pattern's atomicity comes from a single database transaction, not application-level coordination.
- The outbox pattern is at-least-once, not exactly-once — the downstream consumer must be idempotent.
- Saga compensations are forward-moving business actions, never a cross-service rollback.
- 2PC is avoided primarily for its availability cost (locks held across a coordinator round-trip), not because atomicity itself is undesirable.

## Cheat Sheet

| Need | Mechanism |
|---|---|
| Atomic business write + event-intent, one system | Transactional outbox |
| Multi-service workflow with rollback-like behavior | Saga with compensating actions |
| True cross-system atomicity, availability cost acceptable | 2PC (rare in practice) |
| Downstream safety against outbox's at-least-once duplicates | Idempotent consumer |

## Flashcards

### Card: What a dual write fails to guarantee

**Prompt:**
What specifically does a dual write (DB write + separate message publish) fail to guarantee?

**Answer:**
That the message gets published if the DB write succeeds and a crash follows before the publish call — there's no shared transaction, so nothing records that a publish is owed.

**Why it matters:**
The precise mechanism, not just "dual writes are risky."

**Common trap:**
Assuming a retry loop around the publish call fixes this.

**Related:**
[Internal Implementation](#internal-implementation)

### Card: What makes the outbox atomic

**Prompt:**
What makes the transactional outbox's atomicity possible?

**Answer:**
Writing the business row and the outbox row in the SAME database transaction — the database's own transaction guarantee, not application coordination.

**Why it matters:**
The core mechanism the entire pattern depends on.

**Common trap:**
Believing application-level coordination (two separate writes with manual retry logic) achieves the same atomicity.

**Related:**
[Core Concepts](#core-concepts)

### Card: Outbox's actual guarantee

**Prompt:**
Is the transactional outbox exactly-once or at-least-once?

**Answer:**
At-least-once — a crash between "the broker confirms the send" and "the DB marks the row published" causes a real, measured duplicate; the downstream consumer must be idempotent.

**Why it matters:**
Prevents overclaiming exactly-once from a pattern that structurally can't provide it.

**Common trap:**
Assuming the outbox pattern is exactly-once because it eliminates loss.

**Related:**
[Internal Implementation](#internal-implementation)

### Card: Why 2PC is avoided

**Prompt:**
Why is 2PC avoided in practice despite offering true atomicity?

**Answer:**
Locks are held across the coordinator's round-trip; a coordinator crash leaves "prepared" participants stuck indefinitely (in-doubt transactions) — poor availability under partial failure.

**Why it matters:**
The precise, structural reason, not just "2PC is old/slow."

**Common trap:**
Attributing 2PC's unpopularity to performance rather than availability.

**Related:**
[Core Concepts](#core-concepts)

## Practice Exercises

1. Reproduce the full sequence: [`practice/java/week-10/outbox-publisher/`](../../practice/java/week-10/outbox-publisher/).
2. Modify the outbox poller to crash BEFORE the Kafka publish call instead of after, and confirm no duplicate occurs on restart (but also confirm no loss) — what changes about the guarantee?
3. Design a compensating action for a Saga step that reserves inventory, given that "reservations" don't support a native rollback in the inventory service's API.

## Solutions

**Exercise 1.** Expected output matches this chapter's measured trace exactly: 3 orders committed atomically with their outbox rows, a crash after publishing row 1 but before marking it published, and a restart that republishes row 1 (producing the duplicate) plus rows 2 and 3 — 4 total messages for 3 unique orders, zero losses.

**Exercise 2.** Crashing before the Kafka publish call means the outbox row remains unpublished (never sent), so the restart simply attempts to publish it fresh — no duplicate occurs, and the guarantee here is effectively closer to exactly-once for this specific crash point, though the pattern's overall guarantee (accounting for the other crash window) remains at-least-once.

**Exercise 3.** A correct compensating action: rather than "un-reserving" (a rollback), explicitly release the reservation via a forward-moving `ReleaseReservation` operation that the inventory service supports natively, idempotent by reservation ID so a retried release is safe; if the release itself fails, it should be retried with the same idempotency discipline as the original reservation, not assumed to succeed on the first attempt.

## Additional Reading

- [microservices.io — Pattern: Transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html)

## Official References

- [Debezium documentation — Outbox Event Router](https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html) — the CDC-based alternative to the polling publisher built in this chapter
