---
title: "kafka-guarantees.md Deliverable"
week: 8
last_reviewed: 2026-07-31
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

`acks=all` waits for every replica **currently in the ISR**, not every replica configured by `replication.factor`. The ISR is dynamic — a follower that falls behind (slow disk, network partition, GC pause) is dropped automatically. If two of three replicas drop out, `acks=all` is satisfied by one replica acking — indistinguishable from `acks=1`. If that replica then fails before the others catch up, the acknowledged write is gone. `min.insync.replicas` closes the gap by making the write **fail loudly** (`NotEnoughReplicasException`) whenever the current ISR is smaller than the configured minimum, converting a silent durability gap into a visible availability trade-off.

### Why doesn't exactly-once extend to an external database write?

Kafka's exactly-once guarantee runs through a transaction coordinator spanning Kafka's own consumer-offset topic and output-topic writes — both Kafka-internal state, atomic as one unit. An external database has no part in that protocol; a write to Postgres inside a consumer's processing loop is just an opaque side effect between polling and committing. Nothing coordinates "the Postgres row committed" with "the Kafka offset committed" — two independent systems, two independent commit protocols. Both fixes work by removing the need for cross-system atomicity rather than inventing it: the **transactional outbox** writes the DB row and the outbound event to the SAME database in the SAME local transaction, with a separate publisher relaying the outbox to Kafka afterward; an **idempotent consumer** instead accepts redelivery and makes the external write safe to repeat (e.g., an upsert keyed by record ID), sidestepping the need for exactly-once delivery entirely.

## 3. Exit check

- [ ] Every row's failure boundary can be stated without looking at this table, from first principles
- [ ] Can explain, unprompted, why `min.insync.replicas` and `acks=all` are two settings, not one
- [ ] Can walk from "consumer writes to Kafka and Postgres" to "here's exactly why that's not automatically exactly-once" and name both fixes
