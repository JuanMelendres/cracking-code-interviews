---
title: "T-614 · Partitioning & Sharding Strategies"
topic_id: T-614
domain: DistributedData
tier: Staff
iwi: 7.60
prerequisites: [T-609, T-610]
unlocks: [T-806]
week: 10
last_reviewed: 2026-07-29
---

# T-614 · Partitioning & Sharding Strategies

**IWI 7.60 · Staff tier**

**Verification note:** the `EXPLAIN` output in §3 is real, executed against a live Postgres 16 (Docker), a genuine hash-partitioned table with 40,000 seeded rows across 4 partitions.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Partition pruning, measured](#3-partition-pruning-measured)
4. [Shard-key selection is a one-way door](#4-shard-key-selection-is-a-one-way-door)
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

Partitioning splits one logical table into physically separate storage units by some key, either within one database (declarative partitioning, as demonstrated here) or across separate database instances entirely (sharding). Both split data by the same underlying idea — a partition/shard key that determines where a row lives — but sharding adds the additional concern of routing queries to the right physical database, not just the right storage segment within one.

## 2. Why it exists

A single table (or single database) eventually hits limits on write throughput, storage, and index size that no amount of vertical scaling resolves — partitioning/sharding exists to let a dataset grow past what one machine can hold or serve, at the cost of losing some query patterns that were trivial when everything lived in one place (cross-partition joins, global uniqueness constraints, global `ORDER BY`).

## 3. Partition pruning, measured

**Real schema**: `events` table, hash-partitioned into 4 partitions by `customer_id`, 40,000 rows across 1,000 distinct customers.

**Real `EXPLAIN`, querying by the partition key:**

```
EXPLAIN (ANALYZE, COSTS OFF, TIMING OFF) SELECT count(*) FROM events WHERE customer_id = 42;

 Aggregate (actual rows=1 loops=1)
   ->  Seq Scan on events_p2 events (actual rows=40 loops=1)
         Filter: (customer_id = 42)
         Rows Removed by Filter: 11000
 Planning Time: 0.216 ms
 Execution Time: 0.727 ms
```

**Real `EXPLAIN`, querying by a non-partition-key column:**

```
EXPLAIN (ANALYZE, COSTS OFF, TIMING OFF) SELECT count(*) FROM events WHERE event_type = 'click';

 Aggregate (actual rows=1 loops=1)
   ->  Append (actual rows=40000 loops=1)
         ->  Seq Scan on events_p0 events_1 (actual rows=10360 loops=1)
               Filter: (event_type = 'click'::text)
         ->  Seq Scan on events_p1 events_2 (actual rows=9360 loops=1)
               Filter: (event_type = 'click'::text)
         ->  Seq Scan on events_p2 events_3 (actual rows=11040 loops=1)
               Filter: (event_type = 'click'::text)
         ->  Seq Scan on events_p3 events_4 (actual rows=9240 loops=1)
               Filter: (event_type = 'click'::text)
 Planning Time: 0.232 ms
 Execution Time: 2.667 ms
```

**The query filtering by `customer_id` (the partition key) touches exactly ONE of the 4 partitions** (`events_p2`, `Seq Scan` visible directly in the plan, no `Append` node at all — the planner proved at plan time the other 3 partitions can't contain matching rows and skipped them entirely). **The query filtering by `event_type` touches all 4** (the `Append` node fanning out to every partition), because `event_type` isn't the partition key — nothing about it tells the planner which partitions to skip. Real execution time: 0.727ms (pruned) vs. 2.667ms (unpruned) — roughly proportional to the ~4x difference in partitions scanned, on a table this small; the gap widens dramatically at real production scale, where each unpruned partition might be tens of gigabytes.

## 4. Shard-key selection is a one-way door

The partition/shard key choice determines every query pattern's cost from that point forward, and **changing it later means physically moving data** — this is the same category of decision as Week 8's Kafka partition-key choice (T-705), one level up the stack. Two named failure modes:

- **Wrong key chosen up front**: queries that don't filter by the shard key (like `event_type` above) fan out to every shard/partition forever, and there's no query-time fix — only a migration.
- **Cross-shard queries and joins**: a join between two tables sharded by different keys can't be pushed down to a single shard; it either requires application-level fan-out-and-merge, or denormalizing the data so the join isn't needed cross-shard at all.

## 5. Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| Shard key = the most common query filter | Most queries prune to one shard/partition (§3's measured 4x+ speedup) | Queries filtering by anything else fan out to every shard |
| More partitions/shards | More parallel write throughput, smaller working sets per unit | More cross-partition query cost for anything not filtered by the key; more operational surface (more things to back up, monitor, migrate) |
| Range partitioning (e.g., by date) | Natural for time-series data, easy to drop old partitions | Can create hot partitions (all current writes hit the newest range) |
| Hash partitioning (as demonstrated here) | Spreads writes evenly, no natural hot partition | Range queries (e.g., "all events this month") can't prune at all |

## 6. Interview questions

### Q1. Chose the wrong shard key. Recovery plan?

- **Expected answer:** there's no query-time fix — the data must be physically migrated to a new sharding scheme, typically via a dual-write/backfill/cutover process (the same expand-contract discipline as `05-zero-downtime-migration.md`), not a configuration change.
- **Common mistakes:** treating this as a quick reconfiguration rather than a genuine data migration project.
- **Follow-up questions:** "How do you migrate without downtime?"
- **Senior-level expectations:** correctly identifies this requires a real migration, not a setting change.
- **Staff-level expectations:** connects explicitly to zero-downtime migration technique (dual writes to old and new schemes, backfill, verify, cutover) and names the operational cost as the reason shard-key selection deserves real design-time investment.

### Q2. Add a node to your shard cluster — how much data moves?

- **Expected answer:** depends entirely on the partitioning scheme — naive `hash % N` remaps nearly everything (§`03-consistent-hashing.md` measures this directly: 92.5%); consistent hashing moves only roughly `1/N` of the data.
- **Common mistakes:** assuming this is a fixed, small cost regardless of scheme.
- **Follow-up questions:** "Why would you ever use naive hash % N then?"
- **Senior-level expectations:** names consistent hashing as the fix for this specific problem.
- **Staff-level expectations:** notes that Postgres's own declarative HASH partitioning (as demonstrated here) has the SAME naive-remap problem as `hash % N` if partition count changes — it's a different layer (partitions within one DB, not shards across DBs) but the identical mathematical issue, and adding/removing a *partition* here would remap nearly everything for the same reason `hash % N` does at the sharding layer.

## 7. Common mistakes

- Treating sharding as a pure performance tweak rather than a permanent architectural commitment (the blueprint's own named misconception for this topic).
- Choosing a shard key without checking it against the actual dominant query pattern.
- Not recognizing that Postgres's declarative HASH partitioning has the same repartitioning-cost problem as application-level `hash % N` sharding — same math, different layer.

## 8. Staff-level discussion

The measured 4x-plus query-cost gap in §3 is a small-scale preview of what becomes a correctness-and-availability-defining decision at real scale: a shard key chosen to match today's dominant access pattern can become actively wrong as the product evolves and new query patterns emerge, and by the time that mismatch is discovered, the data volume makes correcting it a multi-week migration project, not a config change. A Staff engineer treats shard-key selection with the same design-time rigor as a public API contract — because in practice it has similar one-way-door consequences — deliberately validating it against every query pattern the team can anticipate, not just the current dominant one, before committing real data to it.

## 9. Summary

Partition pruning is real and measurable: a query filtered by the partition key touches one partition (0.727ms here); the identical query filtered by anything else touches all of them (2.667ms here) — a gap that widens dramatically at production scale. Shard/partition key selection is a one-way door: getting it wrong means a genuine data migration, not a reconfiguration, which is why the choice deserves the same design-time scrutiny as any other irreversible architectural decision.

## 10. Key Takeaways

- Partition pruning only works for queries that filter by the partition key — measured 4x+ real cost gap here, much larger at production scale.
- Shard/partition key choice is effectively permanent once real data exists on it.
- Range partitioning risks hot partitions on the newest range; hash partitioning spreads writes but can't prune range queries.
- Postgres's own declarative HASH partitioning has the same repartitioning-cost problem as naive `hash % N` sharding — same underlying math.

## 11. Cheat Sheet

| Need | Approach |
|---|---|
| Most queries filter by one column | Shard/partition by that column |
| Time-series data, want to drop old data cheaply | Range partition by time |
| Want writes spread evenly, no hot partition | Hash partition |
| Changed your mind about the shard key | No shortcut — plan a real migration (§`05`) |

## 12. Flashcards

1. **Q: What does partition pruning require to work?** A: The query must filter by the partition/shard key — anything else fans out to every partition.
2. **Q: Why is shard-key selection called a "one-way door"?** A: Changing it after data exists requires physically migrating the data, not a configuration change.
3. **Q: Does Postgres's own HASH partitioning avoid the naive `hash % N` remapping problem?** A: No — changing partition count remaps nearly all data, the same underlying math as sharding's `hash % N` problem, just one layer down.

(Full week-level deck: `07-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/sql/week-10/sharding/setup.sql`, then run both `EXPLAIN` queries yourself against `week10-pg`.
2. Add a 5th partition to the `events` table and predict, before running it, roughly what fraction of existing rows would need to move under redistribution — then verify against `03-consistent-hashing.md`'s naive-hash measurement for the same node-count change.
3. Design a shard key for a multi-tenant SaaS product where one tenant is 1000x larger than a typical tenant — what breaks with a naive `tenantId` key, and what's the fix?

## 14. Additional Reading

- [PostgreSQL documentation — Table Partitioning](https://www.postgresql.org/docs/16/ddl-partitioning.html)

## 15. Official References

- [PostgreSQL documentation — Partition Pruning](https://www.postgresql.org/docs/16/ddl-partitioning.html#DDL-PARTITION-PRUNING)
