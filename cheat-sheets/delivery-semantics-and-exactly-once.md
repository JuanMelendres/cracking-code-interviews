---
title: "Cheat Sheet: Kafka Delivery Semantics and Exactly-Once Processing"
slug: delivery-semantics-and-exactly-once
document_type: cheat-sheet
domain: kafka
topic_id: T-704
canonical: ../handbook/kafka/delivery-semantics-and-exactly-once.md
last_updated: 2026-08-03
---

# Kafka Delivery Semantics and Exactly-Once Processing

**Canonical chapter:** [`syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md`](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md)

## Core Mental Model

"Commit the offset" and "process the record" are two separate operations against two different systems, and a crash can land between them — no ordering of those two steps alone avoids both duplication and loss. Commit after processing → crash before commit → redelivery (duplicates). Commit before processing → crash after commit, before processing → silent, permanent loss. Exactly-once isn't a third ordering — it's additional machinery (a transaction, or an idempotency check) that closes the gap those two steps alone cannot close.

## Essential Definitions

- **At-least-once** — commit *after* processing; crash before commit causes redelivery. Failure mode: duplication, never loss. The safe default, provided processing is idempotent.
- **At-most-once** — commit *before* processing; crash after commit, before processing means the record is never retried. Failure mode: silent loss, never duplication. Rarely the right default.
- **Exactly-once (Kafka EOS)** — covers the transactional read-process-write loop entirely *within* Kafka. Does **not** cover writes to systems outside Kafka.
- **Transactional outbox** — write the DB row and the outbound event in the same DB transaction; a separate publisher reads the outbox and produces to Kafka.
- **Idempotent consumer** — a durable dedupe key/table check at the write boundary, so redelivery is safe no matter how many times it happens.

## Decision Table

| Guarantee | Duplicates possible? | Loss possible? | Typical use |
|---|---|---|---|
| At-most-once | No | Yes | Rarely right; genuinely disposable data only |
| At-least-once | Yes | No | The standard default — pair with an idempotent processing step |
| Kafka transactional EOS | No | No | Kafka-to-Kafka pipelines only |
| Idempotent consumer (dedupe key) | Effectively no | No | Any pipeline with an external-system side effect |

**Decision sequence:** Can the side effect be made idempotent? → default to at-least-once + idempotent write. Genuinely non-idempotent (payment, email, provisioning)? → durable dedupe key check. Kafka-to-Kafka only? → native EOS may suffice. Writes to an external system? → EOS alone is insufficient, need outbox or idempotent write.

## Key Numbers (real, executed against a live broker)

- **At-least-once trace:** attempt 1 processed 18 records (crash before commit) + attempt 2 processed 18 records (redelivered) = **36 deliveries for 18 unique records — duplicates observed**
- **At-most-once trace:** attempt 1 committed offsets for 18 records but crashed before processing; attempt 2 processed 0 (backlog already drained by the earlier commit) = **0 of 18 actually processed — loss observed**

The dedupe check itself is O(1) (a keyed lookup) — this is about correctness under redelivery, not algorithmic cost.

## Common Pitfalls

- Believing Kafka provides end-to-end exactly-once by default, including writes to external systems
- Choosing commit-before-processing (at-most-once) without a deliberate reason
- Treating redelivery under at-least-once as a bug to eliminate rather than a condition to design for
- Conflating idempotent *producers* (T-702, a different mechanism at a different layer) with idempotent *consumers*

## Interview Answer Skeleton

**30-sec:** At-most-once = commit-before-process, risks loss. At-least-once = commit-after-process, risks duplicates. Exactly-once = extra machinery. Kafka's own EOS is real but scoped to a transactional Kafka-to-Kafka loop — doesn't cover external-system writes without an outbox or idempotent consumer.

**2-min:** Add the "no ordering avoids both" mechanism + the scoped answer on Kafka EOS + the two real measured traces (36/18 duplicates; 0/18 loss).

**Whiteboard:** Draw two parallel timelines, each with "commit" and "process" boxes; place a crash (lightning bolt) at a different point on each — one after process/before commit ("duplicates"), one after commit/before process ("loss"). Makes "no ordering avoids both" click visually.

**Staff-level framing:** this is a specific instance of the general dual-write problem — any time two systems must update from one logical event with no shared transaction, you're choosing between risking duplication, risking loss, or investing in a coordinating mechanism. Name which choice is being made, explicitly, for every dual-write in a design.

## Production Warning Signs

- Gaps between "committed offset" and "processed record" counts — early signal of at-most-once-style silent loss
- Duplicates appearing only in a downstream system, not visible in Kafka itself — the external write isn't covered by Kafka's own EOS
- **Real incident:** a correctly-configured at-least-once consumer redelivered a record after a crash; the payment-charging side effect had no dedupe check → customer double-charged, discovered via support tickets, not an internal alert. Fix: add a dedupe key check — not changing delivery semantics.

## Related

- `syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md`
- `syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md`
- [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md)
