---
title: "T-614 · Partitioning & Sharding Strategies"
topic_id: T-614
domain: DistributedData
tier: Staff
iwi: 7.60
prerequisites: [T-609, T-610]
unlocks: [T-806]
week: 10
last_reviewed: 2026-07-30
canonical: ../../handbook/databases/table-partitioning-and-sharding-strategies.md
---

# T-614 · Partitioning & Sharding Strategies

**IWI 7.60 · Staff tier**

**Canonical chapter:** [Table Partitioning and Sharding Strategies](../../handbook/databases/table-partitioning-and-sharding-strategies.md). This file is the Week 10 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the `EXPLAIN` output behind this summary is real, executed against a live Postgres 16 (Docker), a genuine hash-partitioned table with 40,000 seeded rows across 4 partitions.

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

Partitioning splits one logical table into physically separate storage units by a key, either within one database (declarative partitioning) or across separate database instances entirely (sharding), which additionally requires routing queries to the right physical database. → [Definition and Purpose](../../handbook/databases/table-partitioning-and-sharding-strategies.md#definition-and-purpose).

## 2. Why it exists

A single table/database eventually hits throughput, storage, and index-size limits vertical scaling can't fix. Partitioning/sharding lets data grow past what one machine can hold, at the cost of some query patterns that were trivial in one place. → [Definition and Purpose](../../handbook/databases/table-partitioning-and-sharding-strategies.md#definition-and-purpose).

## 3. Partition pruning, measured

Measured: a query filtered by the partition key (`customer_id`) touches exactly one of four partitions (0.727ms); the identical query filtered by a non-key column touches all four via an `Append` node (2.667ms) — roughly a 4x gap on a small table, widening dramatically at production scale. → [Internal Implementation](../../handbook/databases/table-partitioning-and-sharding-strategies.md#internal-implementation) has the full trace.

## 4. Shard-key selection is a one-way door

Changing the shard/partition key after data exists means physically moving data, not a config change. Wrong key chosen up front means non-key queries fan out forever; cross-shard joins can't be pushed down to one shard. Postgres's own declarative HASH partitioning has the same repartitioning-cost problem as naive `hash % N` sharding. → [Core Concepts](../../handbook/databases/table-partitioning-and-sharding-strategies.md#core-concepts).

## 5. Trade-offs

Shard key = dominant query filter prunes most queries but strands others; range partitioning suits time-series but risks hot partitions; hash partitioning spreads writes but can't prune range queries. → [Trade-offs](../../handbook/databases/table-partitioning-and-sharding-strategies.md#trade-offs).

## 6. Interview questions

1. Chose the wrong shard key. Recovery plan?
2. Add a node to your shard cluster — how much data moves?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/databases/table-partitioning-and-sharding-strategies.md#interview-questions).

## 7. Common mistakes

Treating sharding as a pure performance tweak; choosing a shard key without checking it against the dominant query pattern; assuming Postgres's HASH partitioning avoids the naive-remap problem. → [Common Mistakes](../../handbook/databases/table-partitioning-and-sharding-strategies.md#common-mistakes).

## 8. Staff-level discussion

A shard key chosen to match today's dominant access pattern can become actively wrong as the product evolves, and correcting it at real data volume is a multi-week migration, not a config change. → [Staff-Level Discussion](../../handbook/databases/table-partitioning-and-sharding-strategies.md#interview-answer-framework).

## 9. Summary

Partition pruning is real and measurable — one partition touched vs. all of them, a gap that widens dramatically at scale. Shard/partition key selection is a one-way door deserving the same design-time scrutiny as any irreversible architectural decision. → [Summary](../../handbook/databases/table-partitioning-and-sharding-strategies.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/databases/table-partitioning-and-sharding-strategies.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/databases/table-partitioning-and-sharding-strategies.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/databases/table-partitioning-and-sharding-strategies.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/databases/table-partitioning-and-sharding-strategies.md#practice-exercises) and [Solutions](../../handbook/databases/table-partitioning-and-sharding-strategies.md#solutions). Reproducible demo: `practice/sql/week-10/sharding/setup.sql`.

## 14. Additional Reading

- [PostgreSQL documentation — Table Partitioning](https://www.postgresql.org/docs/16/ddl-partitioning.html)

## 15. Official References

- [PostgreSQL documentation — Partition Pruning](https://www.postgresql.org/docs/16/ddl-partitioning.html#DDL-PARTITION-PRUNING)
