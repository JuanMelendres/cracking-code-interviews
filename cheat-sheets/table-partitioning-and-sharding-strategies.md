---
title: "Cheat Sheet: Table Partitioning and Sharding Strategies"
slug: table-partitioning-and-sharding-strategies
document_type: cheat-sheet
domain: databases
topic_id: T-614
canonical: ../handbook/databases/table-partitioning-and-sharding-strategies.md
last_updated: 2026-08-03
---

# Table Partitioning and Sharding Strategies

**Canonical chapter:** [`handbook/databases/table-partitioning-and-sharding-strategies.md`](../handbook/databases/table-partitioning-and-sharding-strategies.md)

## Core Mental Model

A partition/shard key is a promise about which queries stay cheap forever. Every query filtering by the key skips all data that provably can't match; every query that doesn't must scan (or fan out to) everything. The promise is made once, when the key is chosen — honoring a different query pattern later means moving data, not changing a setting.

## Essential Definitions

- **Partitioning** — splits one logical table into physically separate storage units by a key, either within one DB instance (declarative partitioning) or across separate DB instances (sharding).
- **Sharding** — the cross-database variant; adds the concern of routing queries to the right physical database, not just the right storage segment within one.
- **Wrong key chosen up front** — queries not filtering by the shard key fan out to every shard/partition forever; no query-time fix, only a migration.
- **Cross-shard joins** — a join between tables sharded by different keys can't be pushed to a single shard; requires app-level fan-out-and-merge, or denormalizing to avoid the cross-shard join entirely.

## Decision Table

| Choice | Benefit | Cost |
|---|---|---|
| Shard key = most common query filter | Most queries prune to one shard/partition (measured 4x+ speedup) | Queries filtering by anything else fan out to every shard |
| More partitions/shards | More parallel write throughput, smaller working sets | More cross-partition query cost; more operational surface |
| Range partitioning (e.g., by date) | Natural for time-series, easy to drop old partitions | Hot partitions — all current writes hit the newest range |
| Hash partitioning | Spreads writes evenly, no natural hot partition | Range queries can't prune at all |

| Need | Approach |
|---|---|
| Most queries filter by one column | Shard/partition by that column |
| Time-series, want to drop old data cheaply | Range partition by time |
| Want writes spread evenly, no hot partition | Hash partition |
| Changed your mind about the shard key | No shortcut — plan a real migration |

## Key Numbers (real, PostgreSQL — `events`, hash-partitioned into 4, 40,000 rows / 1,000 customers)

```
Pruned query   (customer_id = 42):    touches events_p2 only, Execution Time: 0.727 ms
Unpruned query (event_type = 'click'): Append across all 4 partitions,   Execution Time: 2.667 ms
```

Roughly proportional to the ~4x difference in partitions scanned. (Cross-reference: naive `hash % N` remaps ~92.5% of keys on a node-count change — same repartitioning math as [Consistent Hashing](data-partitioning-and-consistent-hashing.md).)

## Common Pitfalls

- Treating sharding as a performance tweak rather than a permanent architectural commitment
- Choosing a shard key without checking it against the actual dominant query pattern
- Assuming Postgres's declarative `HASH` partitioning avoids sharding's repartitioning cost just because it's a built-in DB feature — it has the identical repartitioning-cost problem as application-level `hash % N`

## Interview Answer Skeleton

**30-sec:** Partitioning/sharding splits a table by a key so queries filtering on that key touch only the relevant slice — measured here at a 4x+ execution-time gap between a pruned and unpruned query. The key choice is a one-way door: getting it wrong means a real data migration, not a config change.

**2-min:** Add why it exists + how pruning works + range/hash trade-off + the measured 0.727ms (pruned) vs. 2.667ms (unpruned) figure.

**Whiteboard:** Draw the query→router→partitions flow; circle the fan-out branch; annotate "permanent unless the key changes, and changing it means migrating data."

**Staff-level framing:** the measured 4x-plus gap is a small-scale preview of a correctness-and-availability-defining decision at real scale — shard-key selection deserves the same rigor as a public API contract.

## Production Warning Signs

- **Real incident pattern:** a shard key chosen for launch-day query patterns (e.g., `customer_id`) becomes a scaling bottleneck 18 months later when a new cross-customer analytics feature filters on a different column (`event_type`), fanning out to every shard — latency degrades every time a new shard is added.
- Load-testing signature: latency scales linearly with shard count for the mismatched query pattern.
- Remediation is a separate denormalized/CDC-fed analytics store, not re-sharding by a different key on the primary path.

## Related

- [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md)
- [Database Index Structures](index-structures-btree-composite-covering.md)
- [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md)
- `handbook/databases/zero-downtime-schema-migration.md`
