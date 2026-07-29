---
title: "kafka-guarantees.md Deliverable"
week: 8
last_reviewed: 2026-07-29
---

# `kafka-guarantees.md` Deliverable

**A table of every guarantee this week covered, its configuration, and its precise failure boundary.** Per `00-project/learning-roadmap.md` §4, Week 8. This is the week's primary artifact — synthesizing `01`–`04` into a single reference rather than restating each chapter.

## Table of Contents

1. [The guarantee table](#1-the-guarantee-table)
2. [Two exit-criteria answers, written out in full](#2-two-exit-criteria-answers-written-out-in-full)
3. [Exit check](#3-exit-check)

---

## 1. The guarantee table

| Guarantee | Configuration | Precise failure boundary |
|---|---|---|
| Per-key ordering | Deterministic partition key (e.g., `customerId`) | Only within one partition — no ordering across keys/partitions; broken permanently for existing data if partition count changes |
| Write durability | `acks=all` + `min.insync.replicas ≥ 2` + `replication.factor ≥ 3` | Fails if the ISR shrinks below `min.insync.replicas` (write rejected, loud) — but WITHOUT `min.insync.replicas` set, `acks=all` alone silently tolerates an ISR of one, meaning a single replica loss after ack loses the write |
| Availability during broker outage | `unclean.leader.election.enable` (default `false`) | Leaving it `false`: partition becomes unavailable for writes if the ISR is empty, until an in-sync replica returns. Setting it `true`: partition stays writable, but an out-of-sync replica may become leader, silently dropping recent committed records |
| No duplicate producer writes | `enable.idempotence=true` (default) | Covers producer retries to Kafka only (dedup by producer ID + partition + sequence). Does NOT cover application-level duplicate processing on the consumer side |
| No duplicate consumer processing (at-most-once) | Commit offset BEFORE processing | Any crash between commit and processing loses that batch permanently — no redelivery, because the offset already says "done" |
| No lost consumer processing (at-least-once) | Commit offset AFTER processing | Any crash between processing and commit causes redelivery on restart — duplicates, not loss. Requires the processing step to be idempotent to be safe |
| Exactly-once, Kafka-to-Kafka | `transactional.id` set, downstream consumers use `isolation.level=read_committed` | Covers only the read-process-write loop entirely within Kafka. A crash mid-transaction rolls back atomically — nothing partial is ever visible |
| Exactly-once, including an external system (DB, HTTP call) | Transactional outbox (T-618) OR idempotent consumer with a durable dedupe key | Kafka's transactional guarantee stops at its own boundary; without one of these two mechanisms, a dual-write to Kafka + an external system has no atomicity between the two writes at all |
| Consumer parallelism | One partition assigned to at most one consumer per group | Hard-capped at partition count; extra consumers beyond that sit permanently idle |
| Rebalance blast radius | `CooperativeStickyAssignor` vs. the eager default | Cooperative-incremental only revokes partitions that actually need to move; eager revokes every partition from every member on any membership change |

## 2. Two exit-criteria answers, written out in full

### Why doesn't `acks=all` alone prevent loss?

`acks=all` means the producer waits for every replica **currently in the ISR** to acknowledge the write — not every replica configured by `replication.factor`. The ISR is a dynamic set: a follower that falls behind (slow disk, network partition, GC pause) is dropped from it automatically. If two of three replicas have been dropped from the ISR, `acks=all` is satisfied by a single replica acking — indistinguishable, from the producer's perspective, from `acks=1`. If that single replica then fails before the dropped followers catch back up, the acknowledged write is gone. `min.insync.replicas` closes this gap by making the producer's write **fail loudly** (`NotEnoughReplicasException`) whenever the current ISR is smaller than the configured minimum, converting a silent durability gap into a visible availability trade-off the caller must handle.

### Why doesn't exactly-once extend to an external database write?

Kafka's exactly-once guarantee is implemented via a transaction coordinator that spans Kafka's own consumer-offset topic and Kafka's own output-topic writes — both are Kafka-internal state, so Kafka can make them atomic as one unit. An external database has no part in that transaction protocol; from Kafka's perspective, a write to Postgres inside a consumer's processing loop is just an opaque side effect that happens to occur somewhere between polling and committing. There is no mechanism by which "the Postgres row committed" and "the Kafka offset committed" can be made to succeed or fail together, because they are two independent systems with two independent commit protocols and nothing coordinating between them. The two available fixes both work by removing the need for cross-system atomicity rather than inventing it: the **transactional outbox** writes the DB row and the outbound event to the SAME database, in the SAME local transaction, and a separate publisher relays the outbox to Kafka afterward (moving the coordination problem to a single-system transaction, which databases already solve); an **idempotent consumer** instead accepts that redelivery will happen and makes the external write safe to repeat (e.g., an upsert keyed by record ID), sidestepping the need for exactly-once delivery at all.

## 3. Exit check

- [ ] Every row's failure boundary can be stated without looking at this table, from first principles
- [ ] Can explain, unprompted, why `min.insync.replicas` and `acks=all` are two settings, not one
- [ ] Can walk from "consumer writes to Kafka and Postgres" to "here's exactly why that's not automatically exactly-once" and name both fixes
