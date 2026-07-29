---
title: "T-702 / T-705 · Producer Semantics & Partition Key Design"
topic_id: T-702
domain: Kafka
tier: Advanced
iwi: 7.40
prerequisites: [T-701]
unlocks: [T-704]
week: 8
last_reviewed: 2026-07-29
---

# T-702 / T-705 · Producer Semantics & Partition Key Design

**IWI 7.40 (T-702) / 7.55 (T-705) · Advanced tier**

**Verification note:** the partition-routing behavior in §3 and the idempotent-producer configuration in §4 are real, executed output from `practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java` against a live single-broker KRaft cluster.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Partition key design, traced](#3-partition-key-design-traced)
4. [`acks`, idempotence, and what "durable" actually means](#4-acks-idempotence-and-what-durable-actually-means)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

A Kafka producer decides two independent things per record: **which partition it goes to** (the partitioner, driven by the key) and **how durably it's written before the call returns** (`acks`). Both are configured per-producer, not per-topic, and both are frequently conflated in interview answers — they solve different problems.

## 2. Why it exists

Without a deliberate partitioning strategy, related records (e.g., all events for one customer) could land anywhere, making per-entity ordering impossible to reconstruct downstream. Without a durability contract (`acks`), a producer has no way to know — and no way to tell the caller — whether a "successful" send actually survived a broker crash a moment later.

## 3. Partition key design, traced

**Real output, null key (no key given):**
```
== null key -> sticky partitioner batches onto one partition per batch ==
key=null value=unkeyed-0 -> partition=2 offset=3
key=null value=unkeyed-1 -> partition=2 offset=4
key=null value=unkeyed-2 -> partition=2 offset=5
key=null value=unkeyed-3 -> partition=2 offset=6
key=null value=unkeyed-4 -> partition=2 offset=7
key=null value=unkeyed-5 -> partition=2 offset=8
```

This is the modern **sticky partitioner** (default since KIP-480): with no key, the producer doesn't round-robin per record — it sticks to one partition for the whole in-flight batch, then switches, to maximize batch size (fewer, bigger requests, better throughput). This surprises engineers who expect strict round-robin from older documentation or Kafka versions before 2.4.

**Partition key selection is a permanent ordering commitment** (§`01` §3): pick the entity whose internal ordering actually matters to the consumer — typically the aggregate root ID (e.g., `customerId`, `orderId`), never a random or high-cardinality-but-irrelevant field. Two failure modes to design against explicitly:

- **Too coarse a key** (e.g., `tenantId` for a huge tenant) → hot partition, one consumer instance bottlenecks the whole tenant's throughput.
- **Too fine a key** (e.g., a UUID per event with no shared entity) → no ordering guarantee is actually being purchased, since nothing repeats; a null key would have been simpler and enabled the sticky-batching throughput benefit.

## 4. `acks`, idempotence, and what "durable" actually means

| `acks` value | Waits for | Loses data when |
|---|---|---|
| `0` | Nothing — fire and forget | Any broker issue, network drop, anything |
| `1` | Leader has written to its local log | Leader crashes before followers replicate it |
| `all` (`-1`) | Every replica currently in the ISR | ISR has shrunk to just the leader (§`01` §4) — needs `min.insync.replicas ≥ 2` to actually mean something |

**The interview trap named explicitly in the blueprint:** *"`acks=all` and you still lost a message. How?"* Answer: the ISR had shrunk to just the leader (a follower had fallen behind and been dropped from the ISR) at the moment of the write, so "all of the ISR" acked with a single replica — then that replica died before the follower caught back up. `acks=all` alone is a statement about *how many current replicas acked*, not *how many replicas exist*; `min.insync.replicas` is the setting that makes the producer's write fail loudly (`NotEnoughReplicasException`) rather than silently succeed on a shrunk ISR.

**Idempotent producers** (`enable.idempotence=true`, the default in modern Kafka) solve a different, narrower problem: **producer-side retries creating duplicates**. Every producer instance gets a `PID` (producer ID) and stamps each record with a monotonically increasing sequence number per partition; the broker deduplicates by `(PID, partition, sequence)`, so a retried send (e.g., after a timeout where the original actually succeeded) is dropped rather than double-appended. This is what the config dump in `ProducerPartitionKeyDemo`'s real run shows enabled:

```
acks = -1
enable.idempotence = true
max.in.flight.requests.per.connection = 5
retries = 2147483647
```

Idempotence only covers **producer retries to Kafka**, not application-level duplicate delivery on the consumer side — that's the boundary T-704 (§`04-delivery-semantics-and-exactly-once.md`) draws precisely.

## 5. Trade-offs

| Setting | Benefit | Cost |
|---|---|---|
| `acks=0` | Lowest latency, highest throughput | No durability guarantee at all |
| `acks=all` + `min.insync.replicas≥2` | Survives single-broker failure without data loss | Higher write latency; unavailable if ISR drops below the minimum |
| `enable.idempotence=true` | Eliminates duplicate writes from producer retries, at negligible cost | None significant — this is why it's the modern default |
| Entity-ID partition key | Per-entity ordering, predictable | Skewed entities become hot partitions; key choice is a one-way door |

## 6. Interview questions

### Q1. `acks=all` and you still lost a message. How?

- **Expected answer:** ISR had shrunk to the leader alone at write time; no `min.insync.replicas` enforcement, so the write succeeded with a single copy, which was then lost.
- **Common mistakes:** Insisting `acks=all` is unconditionally durable.
- **Follow-up questions:** "What setting closes this gap?"
- **Senior-level expectations:** Names `min.insync.replicas` correctly.
- **Staff-level expectations:** Explains the resulting availability trade explicitly — enforcing `min.insync.replicas=2` means writes fail during a two-broker outage rather than silently risking data loss.

### Q2. What does the idempotent producer actually prevent, and what does it NOT prevent?

- **Expected answer:** Prevents duplicate writes to Kafka caused by the *producer* retrying a send it isn't sure succeeded. Does NOT prevent the *consumer* from processing the same successfully-written record twice (that's a delivery-semantics/consumer problem, T-704).
- **Common mistakes:** Conflating idempotent producers with "Kafka is exactly-once end to end."
- **Follow-up questions:** "So is Kafka exactly-once or not?"
- **Senior-level expectations:** Draws the producer-side vs consumer-side boundary correctly.
- **Staff-level expectations:** Connects it forward to T-704's transactional read-process-write loop as the mechanism that closes the remaining gap within Kafka-to-Kafka pipelines.

## 7. Common mistakes

- Believing `acks=all` alone guarantees no data loss (needs `min.insync.replicas`).
- Assuming idempotent producers make the whole pipeline exactly-once (they only dedupe producer retries).
- Choosing a partition key for entities that don't actually need relative ordering, sacrificing sticky-batching throughput for a guarantee nobody consumes.

## 8. Staff-level discussion

`acks` and `min.insync.replicas` together express an explicit CAP-style trade: how many replicas must be reachable before the system will accept a write. This is the same shape of decision as a quorum write setting in Cassandra or DynamoDB — a Staff engineer states it as "we tolerate N broker failures before writes become unavailable" as a deliberate SLA decision, not a default left untouched. Partition-key design is the same category of irreversible-by-default choice as a database shard key (T-614) — both should be sized and chosen from projected access patterns before the system is live with real ordering-dependent data on it.

## 9. Summary

Producer durability (`acks`, `min.insync.replicas`) and producer-retry deduplication (idempotence) are two separate mechanisms solving two separate problems, and partition-key choice is a third, independent decision governing per-entity ordering. All three are commonly conflated in interview answers; keeping them distinct is most of what separates a Senior from a Staff answer on this topic.

## 10. Key Takeaways

- `acks=all` durability is bounded by the current ISR, not `replication.factor` — pair it with `min.insync.replicas`.
- Idempotent producers dedupe producer-side retries only; they do not make the pipeline end-to-end exactly-once.
- The sticky partitioner batches null-key records per-partition-per-batch for throughput, not strict round-robin.
- Partition key choice trades ordering granularity against hot-partition risk, and is effectively permanent for existing data.

## 11. Cheat Sheet

| Need | Setting |
|---|---|
| No data loss on single-broker failure | `acks=all`, `min.insync.replicas=2`, `replication.factor=3` |
| No duplicate writes from retries | `enable.idempotence=true` (default) |
| Per-entity ordering | Partition key = aggregate root ID |
| Maximum throughput, no ordering need | No key (sticky partitioner) |

## 12. Flashcards

1. **Q: Why isn't `acks=all` alone sufficient for durability?** A: It only waits for the current ISR, which can shrink to a single replica; pair it with `min.insync.replicas`.
2. **Q: What does an idempotent producer deduplicate?** A: Its own retried sends to Kafka (by PID + partition + sequence number) — not consumer-side duplicate processing.
3. **Q: What does the sticky partitioner do with a null key?** A: Batches records onto one partition per in-flight batch (not strict round-robin) to maximize batch size.

(Full week-level deck: `06-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java`.
2. Change `min.insync.replicas` reasoning on paper: with `replication.factor=3`, `min.insync.replicas=2`, and `acks=all`, how many simultaneous broker failures can the topic survive without becoming write-unavailable?
3. Use this chapter as the producer-side source for `05-kafka-guarantees-deliverable.md`.

## 14. Additional Reading

- [Kafka documentation — Producer configs](https://kafka.apache.org/documentation/#producerconfigs)

## 15. Official References

- [KIP-98 — Exactly Once Delivery and Transactional Messaging](https://cwiki.apache.org/confluence/display/KAFKA/KIP-98+-+Exactly+Once+Delivery+and+Transactional+Messaging) — introduced idempotent + transactional producers
- [KIP-480 — Sticky Partitioner](https://cwiki.apache.org/confluence/display/KAFKA/KIP-480%3A+Sticky+Partitioner)
