---
title: "Cheat Sheet: Kafka Architecture Fundamentals"
slug: kafka-architecture-fundamentals
document_type: cheat-sheet
domain: kafka
topic_id: T-701
canonical: ../handbook/kafka/kafka-architecture-fundamentals.md
last_updated: 2026-08-04
---

# Kafka Architecture Fundamentals

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md`](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md)

## Core Mental Model

A Kafka topic is not one log — it's several independent logs wearing one name. Each partition is its own strictly-ordered, append-only sequence; the "topic" is just the grouping that lets producers and consumers address them together.

## Essential Definitions

- **Topic** — a durable, append-only log, split into an ordered set of partitions for parallelism.
- **Partition** — an independent, strictly-ordered sequence of records, each assigned a monotonically increasing offset.
- **Leader / Follower** — the broker handling all reads/writes for a partition; followers copy the leader's log.
- **In-Sync Replica set (ISR)** — the subset of replicas fully caught up with the leader within `replica.lag.time.max.ms`. A record is committed only once every replica in the *current* ISR has it — not once `replication.factor` replicas have it.
- **Unclean leader election** (`unclean.leader.election.enable=true`) — allows a broker outside the ISR to become leader when no in-sync replica is available, trading data loss for availability.

## Decision Table

| Decision | Benefit | Cost |
|---|---|---|
| More partitions | More parallelism (producers, consumers) | More open file handles per broker, more memory, longer rebalances, higher per-partition metadata-sync latency |
| Higher `replication.factor` | Survives more simultaneous broker failures | More disk, more inter-broker replication traffic |
| `unclean.leader.election.enable=true` | Partition stays available during an ISR outage | Silent data loss on failover |
| Fewer, wider partitions per topic | Simpler operations, less rebalance churn | Caps maximum consumer parallelism at the partition count |

| Situation | What to know |
|---|---|
| Need per-entity ordering | Partition key = the entity's aggregate root ID |
| Considering a partition-count change on a live keyed topic | Don't — it silently remaps every key's assignment |
| Checking `acks=all` durability | Verify current ISR size, not configured `replication.factor` |
| One partition overloaded | Diagnose key skew first; more partitions alone won't fix it |

## Key Numbers (real, executed — `ProducerPartitionKeyDemo.java`, 4-partition `orders` topic, single-broker KRaft)

```
customer-42: 6 records (order-0..order-5), all on partition=1, offsets 0-5, strict order
customer-1 -> partition=1 offset=6   customer-2 -> partition=2 offset=0
customer-3 -> partition=2 offset=1   customer-4 -> partition=1 offset=7
customer-5 -> partition=2 offset=2   customer-6 -> partition=3 offset=0
```
Note: replication/ISR mechanics are described from documented Kafka behavior, not measured directly — a single-broker practice cluster cannot itself demonstrate a multi-broker ISR shrink/expand.

## Common Pitfalls

- Believing Kafka provides global, topic-wide ordering — it provides per-partition ordering only
- Treating "more partitions" as a free scalability lever without accounting for rebalance cost, file-handle pressure, and the one-way-door nature of the key-to-partition mapping
- Assuming `replication.factor=3` alone means three replicas are always available to serve a failover — the ISR can shrink below that at any time

## Interview Answer Skeleton

**30-sec:** Kafka splits a topic into partitions for parallelism, guaranteeing order only within a partition, never across the topic. Partition assignment is deterministic by key, so per-entity ordering is achievable, but partition count is effectively fixed once keyed data is live — changing it remaps every key.

**2-min:** Add why it exists (parallelism) + how it works (`hash(key) % partitionCount`, ISR vs. `acks=all`) + the more-partitions trade-off (parallelism vs. rebalance/file-handle cost, one-way door) + the real customer-42 trace.

**Whiteboard:** Draw the 4-partition diagram; mark customer-42's six records on one partition in strict order; mark two other keys on two other partitions to show no cross-partition ordering guarantee.

**Staff-level framing:** partition count is one of the few genuinely irreversible decisions in a Kafka deployment for a keyed topic — the same category of decision as a database shard key or a public API's URL scheme.

## Production Warning Signs

- **Real incident pattern:** doubling partition count to relieve a hot broker silently breaks per-customer ordering for a subset of customers — discovered only when a reconciliation job flags inconsistent state days later, not a crash. Root cause: `hash(customerId) % oldPartitionCount` vs. `newPartitionCount` maps a subset of keys to different partitions after the resize.
- Prevention: treat partition-count changes on any live keyed topic as a migration requiring explicit planning, never a routine operational tweak.

## Related

- `syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md`
- [Consumer Groups and Rebalancing](consumer-groups-and-rebalancing.md)
- [Kafka Delivery Semantics and Exactly-Once](delivery-semantics-and-exactly-once.md)
- [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md)
