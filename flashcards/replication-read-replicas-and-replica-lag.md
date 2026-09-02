---
title: "Flashcards: Replication, Read Replicas, and Replica Lag"
slug: replication-read-replicas-and-replica-lag
document_type: flashcard-deck
domain: databases
topic_id: T-615
canonical: ../handbook/databases/replication-read-replicas-and-replica-lag.md
last_updated: 2026-09-02
---

# Flashcards: Replication, Read Replicas, and Replica Lag

**Canonical chapter:** [`handbook/databases/replication-read-replicas-and-replica-lag.md`](../handbook/databases/replication-read-replicas-and-replica-lag.md)

## Card: Async replication's real risk

**Prompt:**
Can a client that just wrote to the primary read stale data from a replica immediately afterward?

**Answer:**
Yes — real, verified directly. Asynchronous replication means a genuine, nonzero delay before a write is visible on a replica.

**Why it matters:**
The core, structural read-your-own-writes risk every read/write split must account for.

**Common trap:**
Assuming replicas are "close enough to instant" to be safe for any read.

**Related:**
[Internal Implementation](../handbook/databases/replication-read-replicas-and-replica-lag.md#internal-implementation)

## Card: Two different "lag" numbers

**Prompt:**
Does naive application-level polling measure the same "replica lag" as `pg_stat_replication`'s own columns?

**Answer:**
No — verified directly, naive polling (~174ms) was dominated by connection/query overhead, while `pg_stat_replication`'s own columns showed genuinely sub-millisecond WAL-streaming lag.

**Why it matters:**
Conflating the two leads to badly over- or under-estimating actual replica staleness.

**Common trap:**
Treating any "time until I observed the new row" measurement as pure replication lag.

**Related:**
[Internal Implementation](../handbook/databases/replication-read-replicas-and-replica-lag.md#internal-implementation)

## Card: Promotion's sequence gotcha

**Prompt:**
Are auto-incrementing (`SERIAL`) IDs guaranteed to remain gap-free across a replica promotion?

**Answer:**
No — verified directly by actually performing a promotion; an ID jumped from 3 to 35, a real, reproducible consequence of sequence value caching.

**Why it matters:**
A real operational detail beyond "the replica becomes the new primary."

**Common trap:**
Assuming `SERIAL` columns are always gap-free under any circumstance, including failover.

**Related:**
[Internal Implementation](../handbook/databases/replication-read-replicas-and-replica-lag.md#internal-implementation)
