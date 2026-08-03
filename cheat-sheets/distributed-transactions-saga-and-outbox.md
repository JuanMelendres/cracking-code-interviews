---
title: "Cheat Sheet: Distributed Transactions — Saga and Outbox"
slug: distributed-transactions-saga-and-outbox
document_type: cheat-sheet
domain: system-design
topic_id: T-618
canonical: ../handbook/system-design/distributed-transactions-saga-and-outbox.md
last_updated: 2026-08-03
---

# Distributed Transactions: Saga and Outbox

**Canonical chapter:** [`handbook/system-design/distributed-transactions-saga-and-outbox.md`](../handbook/system-design/distributed-transactions-saga-and-outbox.md)

## Core Mental Model

A dual write is a promise made to two systems that don't know about each other, and a crash between them breaks the promise silently. The outbox pattern's answer: make the promise to only *one* system (the database), and have that system carry the intent forward reliably. The Saga's answer: don't promise atomicity at all — promise a sequence of individually-committed steps, each with an undo. 2PC's answer: actually coordinate a real cross-system commit, and pay for it in availability.

## Essential Definitions

- **Dual write** — an operation updating two independent systems (DB + broker, two DBs, DB + external API) as one logical unit with no shared transaction spanning both.
- **Transactional outbox** — write the business row **and** an outbox row describing the event in the **same** DB transaction, so either both exist or neither does. A poller reads unpublished rows and relays them, marking published only after send confirmation. Guarantee: **at-least-once, never exactly-once** — a crash between broker confirmation and mark-published produces a real duplicate.
- **Saga** — a multi-service transaction as a sequence of local transactions, each with a **compensating action** for rollback. There is no cross-service `ROLLBACK`; a Saga undoes a completed step with a new forward-moving action (e.g., a refund, not an "un-charge").
- **Orchestration vs. choreography** — orchestration: a central coordinator explicitly calls each service and invokes compensations on failure (easier to debug, coordinator is a structural dependency). Choreography: each service reacts to and emits events (no central coordinator, but the flow is implicit and harder to trace).
- **2PC (two-phase commit)** — coordinator asks all participants to prepare (lock, promise to commit) before telling them to actually commit. An "in-doubt transaction" is a prepared participant that never gets the final decision — stuck holding its lock indefinitely.

## Decision Table

| Need | Mechanism |
|---|---|
| Atomic business write + event-intent | Transactional outbox |
| Multi-service workflow with rollback-like behavior | Saga with compensating actions |
| True cross-system atomicity, availability cost acceptable | 2PC (rare in practice) |
| Downstream safety against outbox's at-least-once duplicates | Idempotent consumer |

**Guarantee comparison:**

| Mechanism | Guarantee | Availability under partial failure |
|---|---|---|
| Dual write, uncoordinated | None | N/A |
| Transactional outbox | At-least-once, never lost | High |
| Saga | At-least-once per step, with compensation | High |
| 2PC | Exactly-once / true atomicity | Low |

## Key Numbers (real, executed — PostgreSQL 16 + KRaft Kafka, `outbox-publisher`)

- **3 orders written atomically, 4 total messages published, zero losses.** Poller run 1 publishes row 1, crashes before marking published; poller run 2 (restart) republishes row 1 (duplicate) plus rows 2 and 3.
- Kafka trace: `[1] key=1`, `[2] key=1` (duplicate), `[3] key=2`, `[4] key=3` — total 4 messages published.
- Poller cadence: `@Scheduled(fixedDelay = 500)` — 500ms.
- Complexity: all operations O(1) per event.

## Common Pitfalls

- Believing a DB write plus a message publish can be made atomic without an outbox or equivalent mechanism
- Treating Saga compensation as a rollback rather than a new forward-moving business action
- Skipping the idempotent-consumer requirement that the outbox's at-least-once guarantee creates downstream

## Interview Answer Skeleton

**30-sec:** Outbox: write business row + event row in one DB transaction, poller relays it — at-least-once, never lost, never exactly-once. Saga: multi-service workflow as local transactions with compensating (not rollback) actions. 2PC: real cross-system atomicity, at the cost of availability (in-doubt transactions).

**2-min:** Add why it exists (no shared transaction manager across DB+broker) + the measured 3-writes/4-deliveries/zero-losses trace + orchestration vs. choreography trade-off.

**Whiteboard:** Draw the sequence diagram; circle the gap between "poller publishes" and "poller marks published" — that gap is where the duplicate comes from, and why downstream consumers must be idempotent.

**Staff-level framing:** "This chapter's real measurement — 3 writes, 4 deliveries, zero losses — is the concrete version of an architectural principle that recurs constantly at Staff scope: prefer at-least-once with idempotency over attempting exactly-once through coordination."

## Production Warning Signs

- **Real incident pattern:** a small, unpredictable fraction of orders silently never trigger confirmation email/fulfillment — no internal alert, only customer complaints. Root cause: DB write to `orders` followed by a separate, non-transactional Kafka publish call; incidents correlate with deploy/crash restarts.
- "Just add retries around the Kafka call" does NOT fix this — a crash before the call ever fires means retries never run.
- Prevention: review any code path with a DB write plus a separate external call for exactly this dual-write hazard.

## Related

- [Idempotency at System Edges](idempotency.md)
- `handbook/spring/transactional-proxy-mechanics-and-propagation.md`
- `handbook/kafka/delivery-semantics-and-exactly-once.md`
- [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md)
