---
title: "T-703 · Consumer Groups, Rebalancing & Offset Management"
topic_id: T-703
domain: Kafka
tier: Advanced
iwi: 7.50
prerequisites: [T-701]
unlocks: [T-704]
week: 8
last_reviewed: 2026-07-29
---

# T-703 · Consumer Groups, Rebalancing & Offset Management

**IWI 7.50 · Advanced tier**

**Verification note:** the assignment trace in §3 is real, executed output from `practice/java/week-08/kafka/src/ConsumerGroupDemo.java` against a live single-broker KRaft cluster with a 4-partition topic and 18 records.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A rebalance, traced](#3-a-rebalance-traced)
4. [Rebalance cost: eager vs cooperative-incremental](#4-rebalance-cost-eager-vs-cooperative-incremental)
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

A **consumer group** is a set of consumer instances sharing a `group.id`, cooperatively consuming a topic such that each partition is assigned to exactly one consumer *within the group* at a time (other groups consuming the same topic are independent — every group gets its own copy of the full stream). The group coordinator (a broker) tracks membership and triggers a **rebalance** — reassigning partitions — whenever membership changes: a consumer joins, leaves, crashes, or is judged dead by a missed heartbeat/poll deadline.

## 2. Why it exists

A single consumer caps read throughput at one process. Consumer groups let multiple instances split a topic's partitions for parallel consumption while Kafka handles the bookkeeping of who owns what — and rebalancing exists so that failure or scaling (adding/removing consumers) doesn't require any manual reassignment.

## 3. A rebalance, traced

**Real output** — `consumer-1` joins alone first, consumes everything, then `consumer-2` joins the same group and a rebalance splits the (now-empty) partition assignment:

```
== consumer-1 joins group 'order-processors-...' alone -> gets all 4 partitions ==
[consumer-1] assigned partitions: [orders-0, orders-1, orders-2, orders-3]
== consumer-2 joins the same group -> triggers rebalance, partitions split ==
[consumer-1] assigned partitions: [orders-0, orders-1]
[consumer-2] assigned partitions: [orders-2, orders-3]
consumer-1 processed 18 records, consumer-2 processed 0 records
== both leave; a solo consumer-3 joins the same group -> full rebalance, gets all partitions back ==
[consumer-3] assigned partitions: [orders-0, orders-1, orders-2, orders-3]
consumer-3 alone was assigned: processed 0 NEW records (rest already committed by consumer-1/2)
```

Three real behaviors worth naming explicitly:

1. **Assignment is exclusive within the group** — after the rebalance, `orders-0`/`orders-1` belong to `consumer-1` and `orders-2`/`orders-3` to `consumer-2`; no overlap.
2. **`consumer-2` processed zero *new* records** here specifically because `consumer-1` had already drained and committed the entire backlog before `consumer-2` joined — this is offset commits doing their job, not a bug: a consumer group tracks progress per partition via committed offsets, and a newly-joined member resumes from the committed offset, not from the beginning.
3. **A solo `consumer-3` joining later gets the full 4-partition assignment back** — group membership, not partition count, drives fan-out; with one member, one member gets everything.

## 4. Rebalance cost: eager vs cooperative-incremental

The classic (**eager**) rebalance protocol revokes **every** partition from **every** consumer in the group before reassigning — a brief stop-the-world pause across the whole group on every membership change, even if only one consumer joined or left. **Cooperative-incremental rebalancing** (`CooperativeStickyAssignor`) only revokes the specific partitions that need to move, letting unaffected consumers keep processing through the rebalance. This directly answers the blueprint's named follow-up: *"Your consumer group rebalances every 30 seconds. Diagnose it."* — the two most common root causes are (a) `max.poll.interval.ms` violations (a slow consumer takes longer between `poll()` calls than the group tolerates, so the coordinator declares it dead and rebalances, even though the process is alive but just slow) and (b) autoscaling churn triggering group-membership changes faster than the group can settle.

## 5. Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| More consumers in a group | More parallel throughput | Capped at partition count — consumers beyond that sit idle |
| Cooperative-incremental assignor | Rebalances don't stop the whole group | More complex protocol; still pauses the *moving* partitions |
| Manual offset commit (`enable.auto.commit=false`) | Precise control over at-least-once vs at-most-once (T-704) | More application code; a bug here directly causes duplicate or lost processing |
| Long `max.poll.interval.ms` | Tolerates slower per-batch processing without spurious rebalances | Slower detection of a genuinely dead consumer |

## 6. Interview questions

### Q1. Your consumer group rebalances every 30 seconds. Diagnose it.

- **Expected answer:** Most likely `max.poll.interval.ms` being violated by slow per-batch processing, causing the coordinator to evict a live-but-slow consumer as if it were dead, repeatedly.
- **Common mistakes:** Jumping straight to "network issue" without checking processing time per poll first.
- **Follow-up questions:** "How do you fix it without just increasing the timeout indefinitely?"
- **Senior-level expectations:** Names `max.poll.interval.ms` and `max.poll.records` as the levers, and considers reducing batch size or moving heavy work off the poll thread.
- **Staff-level expectations:** Also considers cooperative-incremental rebalancing to reduce the blast radius while the root cause is fixed, and distinguishes this from a `session.timeout.ms`/heartbeat-thread issue (a genuinely different failure mode).

### Q2. Add consumers beyond the partition count. What happens?

- **Expected answer:** Extra consumers in the group receive zero partitions and sit idle — assignment is capped at partition count, one partition per consumer maximum.
- **Common mistakes:** Assuming more consumers always means more throughput.
- **Follow-up questions:** "So how do you scale consumption further?"
- **Senior-level expectations:** States that partition count is the hard ceiling on consumer parallelism for a given topic.
- **Staff-level expectations:** Connects this back to T-701/T-702 — since partition count is effectively fixed for a keyed topic, consumer-side scalability for that topic is decided at topic-creation time, not at scaling time.

## 7. Common mistakes

- Assuming a rebalance means something is broken, rather than a normal response to membership change.
- Adding consumers past the partition count expecting more throughput.
- Committing offsets before processing completes without meaning to (accidentally choosing at-most-once — T-704).

## 8. Staff-level discussion

Consumer-group rebalancing is a specific instance of a general distributed-systems pattern — cooperative work assignment under dynamic membership — that shows up in job schedulers, sharded caches, and distributed locks alike. The Staff-level distinction in this topic specifically is recognizing that "the group rebalanced" is a symptom, and the actual diagnosis requires separating three different causes with three different fixes: genuine scaling events (expected, benign), consumer crashes (investigate the crash), and `max.poll.interval.ms` violations from slow processing (a latency bug wearing a rebalance costume).

## 9. Summary

Consumer groups split a topic's partitions across member consumers, one partition per consumer at a time, and rebalance automatically on membership change. Rebalance frequency is a diagnostic signal — recurring rebalances almost always trace back to `max.poll.interval.ms` violations from slow processing, not the rebalance mechanism itself being at fault. Consumer parallelism for a topic is hard-capped at its partition count.

## 10. Key Takeaways

- One partition, one consumer, within a group, at a time.
- Consumers beyond partition count sit idle.
- Recurring rebalances usually mean slow processing violating `max.poll.interval.ms`, not a networking problem.
- Cooperative-incremental rebalancing narrows the blast radius of a rebalance but doesn't eliminate the underlying cause.

## 11. Cheat Sheet

See §3's real trace.

## 12. Flashcards

1. **Q: Can two consumers in the same group read the same partition simultaneously?** A: No — exactly one consumer per partition per group at a time.
2. **Q: What's the most common cause of a group rebalancing repeatedly?** A: `max.poll.interval.ms` violations from slow per-batch processing, evicting a live-but-slow consumer.
3. **Q: What caps consumer parallelism for one topic?** A: The partition count — extra consumers beyond it sit idle.

(Full week-level deck: `06-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-08/kafka/src/ConsumerGroupDemo.java`.
2. Add a `Thread.sleep()` inside the poll loop long enough to exceed a shortened `max.poll.interval.ms`, and observe the resulting rebalance in the logs.
3. Explain, in writing, why `consumer-2` in the real trace above processed zero new records — connect it explicitly to committed-offset semantics.

## 14. Additional Reading

- [Kafka documentation — Consumer configs](https://kafka.apache.org/documentation/#consumerconfigs)

## 15. Official References

- [KIP-429 — Kafka Consumer Incremental Rebalance Protocol](https://cwiki.apache.org/confluence/display/KAFKA/KIP-429%3A+Kafka+Consumer+Incremental+Rebalance+Protocol)
