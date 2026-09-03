---
title: "Cheat Sheet: Kafka Producer Semantics — acks, Idempotence, Partition Keys"
slug: producer-semantics-and-partition-keys
document_type: cheat-sheet
domain: kafka
topic_id: T-702/T-705
canonical: ../handbook/kafka/producer-semantics-and-partition-keys.md
last_updated: 2026-08-05
---

# Kafka Producer Semantics — acks, Idempotence, Partition Keys

**Canonical chapter:** [`syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md`](../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md)

## Core Mental Model

A producer answers two unrelated questions per record: **"where does it go?"** and **"how sure am I it landed?"** Partitioning (the key) answers the first; `acks` plus `min.insync.replicas` answers the second, using replica acknowledgment. Idempotence is a third, narrower guarantee layered on top of the second — it only protects against the producer's *own* retries creating duplicates. It says nothing about where the record went or how many replicas hold it. Conflating any two of these three is the single most common interview mistake on this topic.

## Essential Definitions

- **Partitioner** — routes a record by key (hash mod partition count), or sticky-batches null-key records onto one partition per in-flight batch (KIP-480, not strict round-robin).
- **`acks`** — how many replicas must acknowledge a write before the producer considers it durable: `0` (fire and forget), `1` (leader only), `all`/`-1` (current ISR).
- **Idempotent producer** (`enable.idempotence=true`) — assigns a PID + monotonic per-partition sequence number; broker dedupes by `(PID, partition, sequence)`. Covers **producer-side retries only**.
- **`min.insync.replicas`** — the setting that makes `acks=all` mean something; without it, `acks=all` only waits for whatever's *currently* in the ISR, which can be as small as 1.

## Decision Table

| Need | Setting |
|---|---|
| No data loss on single-broker failure | `acks=all`, `min.insync.replicas=2`, `replication.factor=3` |
| No duplicate writes from retries | `enable.idempotence=true` (modern default) |
| Per-entity ordering | Partition key = aggregate root ID (`customerId`, `orderId`) |
| Maximum throughput, no ordering need | No key (sticky partitioner) |
| One entity dominating a partition | Compound key: `entityId + (seq % N)` — weakens full ordering to ordering-within-bucket |

**Trade-offs:**

| Setting | Benefit | Cost |
|---|---|---|
| `acks=0` | Lowest latency, highest throughput | No durability guarantee at all |
| `acks=all` + `min.insync.replicas≥2` | Survives single-broker failure without data loss | Higher write latency; unavailable if ISR drops below minimum |
| `enable.idempotence=true` | Eliminates duplicate producer-retry writes, negligible cost | None significant |
| Entity-ID partition key | Per-entity ordering, predictable | Skewed entities become hot partitions; choice is a one-way door |

## Key Numbers (real, executed against a live single-broker KRaft cluster)

Null-key sticky-partitioner trace — all 6 unkeyed records land on the *same* partition for one batch, disproving round-robin:

```
key=null value=unkeyed-0 -> partition=2 offset=3
key=null value=unkeyed-1 -> partition=2 offset=4
...
key=null value=unkeyed-5 -> partition=2 offset=8
```

Real producer config dump, idempotence on by default:

```
acks = -1
enable.idempotence = true
max.in.flight.requests.per.connection = 5
retries = 2147483647
```

`retries` is safely near-infinite specifically *because* idempotence is enabled — the broker's `(PID, partition, sequence)` dedup makes unlimited retrying safe.

## Common Pitfalls

- Believing `acks=all` alone guarantees no data loss — it needs `min.insync.replicas` to close the ISR-shrink gap.
- Assuming idempotent producers make the whole pipeline exactly-once — they only dedupe producer retries, not consumer-side duplicate processing (that's T-704, delivery semantics).
- Choosing a partition key for entities that don't actually need relative ordering — pays a throughput and hot-partition cost for an unused guarantee.

## Interview Answer Skeleton

**30-sec:** Two independent decisions per record: which partition (key), how durably (acks). `acks=all` alone only waits for the *current* ISR — pair with `min.insync.replicas` for real durability. Idempotent producers dedupe producer retries only, not consumer-side duplicates.

**2-min:** Add why it exists (no deliberate partitioning → unreconstructable ordering; no durability contract → "success" is meaningless) + the sticky-batching mechanism + the `acks=all`-still-lost-a-message mechanism (ISR shrunk to leader alone, no `min.insync.replicas` enforcement).

**Whiteboard:** Record → partitioner (key-hash or sticky) → send → `acks` branch into 3 outcomes. Next to the `all` branch, draw an "ISR" box with 3 replica circles, cross one out to 2 — `acks=all` waits on whatever's inside the box *right now*, not a fixed replication factor.

**Staff-level framing:** `acks` + `min.insync.replicas` is the same shape of decision as a quorum-write setting in Cassandra/DynamoDB — state it as "we tolerate N broker failures before writes become unavailable," a deliberate SLA choice, not a default left untouched. Partition-key design is the same category of one-way-door choice as a database shard key.

## Production Warning Signs

- Data loss with no corresponding error at write time, discovered only in a downstream reconciliation — check for `acks=all` without `min.insync.replicas`, and broker logs for ISR-shrink events around the loss window.
- Unexpected duplicate records downstream — verify `enable.idempotence=true` first; if already on, duplication is likely consumer-side, not producer-side.
- One partition consistently far busier than others — key skew from a coarse or unevenly distributed key; measure per-partition throughput.
- **Prevention:** treat `acks=all` and `min.insync.replicas` as one inseparable configuration pair in every topic-provisioning checklist.

## Related

- `syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md`
- `syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md`
- `production-cookbook/silent-data-loss-from-a-shrunk-isr-without-min-insync-replicas.md`
