---
title: "Cheat Sheet: Consumer Groups and Rebalancing"
slug: consumer-groups-and-rebalancing
document_type: cheat-sheet
domain: kafka
topic_id: T-703
canonical: ../handbook/kafka/consumer-groups-and-rebalancing.md
last_updated: 2026-08-03
---

# Consumer Groups and Rebalancing

**Canonical chapter:** [`handbook/kafka/consumer-groups-and-rebalancing.md`](../handbook/kafka/consumer-groups-and-rebalancing.md)

## Core Mental Model

A rebalance is Kafka's answer to "who owns what, right now" whenever the answer might have changed. Group membership changing (join, leave, crash, or a missed liveness deadline) is the *only* trigger — a rebalance is not a periodic maintenance event. If one is happening on a schedule, something is causing membership to *appear* to change on that schedule — most often a slow consumer being mistaken for a dead one.

## Essential Definitions

- **Consumer group** — a set of consumer instances sharing a `group.id`, cooperatively consuming a topic such that each partition is assigned to exactly one consumer within the group at a time. Other groups on the same topic are entirely independent, each getting its own full copy of the stream.
- **Rebalance** — the group coordinator (a broker) tracks membership and reassigns partitions whenever it changes.
- **Eager rebalancing** — revokes every partition from every consumer in the group before reassigning any — a full stop-the-world pause on any membership change, however small.
- **Cooperative-incremental rebalancing** — KIP-429 (Kafka 2.4, 2019), via `CooperativeStickyAssignor`: only revokes the specific partitions that need to move, letting unaffected consumers keep processing throughout.

## Decision Table

| Aspect | Eager rebalancing | Cooperative-incremental rebalancing |
|---|---|---|
| Partitions revoked | All, group-wide | Only those actually moving |
| Throughput impact | Full stop-the-world pause | Unaffected consumers keep processing |
| Introduced | Original protocol | KIP-429 / Kafka 2.4 |
| When it still matters | Large, churn-prone groups on the default assignor | — |

| Situation | What to check |
|---|---|
| Repeated periodic rebalances | `max.poll.interval.ms` violations — a slow-but-alive consumer being evicted as dead |
| Adding consumers with no throughput gain | Consumer count at/beyond partition count |
| Need to reduce rebalance blast radius | Switch to `CooperativeStickyAssignor` |
| Newly-joined consumer processes fewer records | Not a bug — resumed from committed offset |

## Key Numbers (real, executed — `ConsumerGroupDemo.java`, live single-broker KRaft, 4 partitions, 18 records)

```
consumer-1 alone:          gets all 4 partitions (orders-0..3)
consumer-2 joins:          consumer-1 -> [orders-0, orders-1], consumer-2 -> [orders-2, orders-3]
                            consumer-1 processed 18 records, consumer-2 processed 0
consumer-3 later, solo:    gets all 4 partitions back, processed 0 NEW records
                            (rest already committed by consumer-1/2)
```

KIP-429 shipped in Kafka 2.4 (2019) — same release cycle as the sticky partitioner.

## Common Pitfalls

- Assuming a rebalance means something is broken, rather than a normal response to membership change
- Adding consumers past the partition count expecting more throughput — extra consumers sit idle, a hard ceiling
- Committing offsets before processing completes without meaning to — accidentally choosing at-most-once (see [Delivery Semantics](delivery-semantics-and-exactly-once.md))

## Interview Answer Skeleton

**30-sec:** A consumer group splits a topic's partitions across its members, one partition per consumer at a time, rebalancing whenever membership changes. The most common real-world cause of *repeated* rebalancing is `max.poll.interval.ms` violations — a slow-but-alive consumer evicted as if dead — not a networking problem.

**2-min:** Add definition + mechanism + eager-vs-cooperative-incremental trade-off + the measured trace (consumer-1 processed 18, consumer-2 processed 0 after joining).

**Whiteboard:** Draw consumer-1 owning all 4 partitions, consumer-2 joining, coordinator revoking/reassigning; annotate "resumes from committed offset, not from zero."

**Staff-level framing:** rebalancing is an instance of "cooperative work assignment under dynamic membership" — the same problem shows up in job schedulers, sharded caches, distributed locks. Three distinct causes to distinguish: scaling, crashes, and `max.poll.interval.ms` violations ("a latency bug wearing a rebalance costume").

## Production Warning Signs

- **Real incident pattern:** a consumer group rebalances roughly every 30 seconds; on-call suspects a networking issue, but `max.poll.interval.ms` warnings precede every rebalance — a recently added synchronous downstream HTTP call inside the poll loop occasionally takes several seconds.
- Immediate mitigation: raise `max.poll.interval.ms` as a stopgap. Permanent fix: move the HTTP call off the poll thread (or reduce `max.poll.records`), and switch to `CooperativeStickyAssignor` to shrink the blast radius regardless.

## Related

- `handbook/kafka/kafka-architecture-fundamentals.md`
- `handbook/kafka/producer-semantics-and-partition-keys.md`
- [Kafka Delivery Semantics and Exactly-Once](delivery-semantics-and-exactly-once.md)
