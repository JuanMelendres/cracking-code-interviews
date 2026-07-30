---
title: "T-616 · Zero-Downtime Schema Migration"
topic_id: T-616
domain: DistributedData
tier: Staff
iwi: 7.30
prerequisites: [T-609]
unlocks: []
week: 10
last_reviewed: 2026-07-30
canonical: ../../handbook/databases/zero-downtime-schema-migration.md
---

# T-616 · Zero-Downtime Schema Migration

**IWI 7.30 · Staff tier**

**Canonical chapter:** [Zero-Downtime Schema Migration](../../handbook/databases/zero-downtime-schema-migration.md). This file is the Week 10 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the blocking-vs-`CONCURRENTLY` timings behind this summary are real, measured wall-clock output from `practice/sql/week-10/zero-downtime-migration/` against a live Postgres 16 (Docker), a genuine 2-million-row table, and a real concurrent `INSERT` from a second session.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Blocking vs CONCURRENTLY, measured](#3-blocking-vs-concurrently-measured)
4. [Expand-contract for column/type changes](#4-expand-contract-for-columntype-changes)
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

Zero-downtime migration changes a live database's schema without blocking normal reads/writes, because a maintenance window is rarely an option for a system with real concurrent traffic. → [Definition and Purpose](../../handbook/databases/zero-downtime-schema-migration.md#definition-and-purpose).

## 2. Why it exists

Schema changes that look instant locally take real, lock-holding time against production-scale data — Postgres's default DDL operations hold locks for that entire duration. → [Definition and Purpose](../../handbook/databases/zero-downtime-schema-migration.md#definition-and-purpose).

## 3. Blocking vs CONCURRENTLY, measured

Measured: a plain `CREATE INDEX` on a 2M-row table blocks a concurrent `INSERT` for 1943ms (the full build duration); `CREATE INDEX CONCURRENTLY` lets the same `INSERT` complete in 84ms while the build is still running — roughly 23x. → [Internal Implementation](../../handbook/databases/zero-downtime-schema-migration.md#internal-implementation) has the full trace.

## 4. Expand-contract for column/type changes

A direct rename is instant at the catalog level but breaks old code still running during a rolling deploy. Expand-contract fixes this in three phases: add the new column, dual-write + backfill, then drop the old column once all instances run new code. The dual-write phase inherits the same atomicity hazard as any cross-system dual write. → [Core Concepts](../../handbook/databases/zero-downtime-schema-migration.md#core-concepts).

## 5. Trade-offs

Plain `CREATE INDEX` is simpler but blocks writes; `CONCURRENTLY` doesn't block but is slower and can leave an invalid index on failure; direct rename is simple but breaks mixed-version deploys; expand-contract keeps both versions working at the cost of more steps. → [Trade-offs](../../handbook/databases/zero-downtime-schema-migration.md#trade-offs).

## 6. Interview questions

1. Rename a column on a live 200M-row table.
2. How do you add an index to a 500M-row table in production without downtime?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/databases/zero-downtime-schema-migration.md#interview-questions).

## 7. Common mistakes

Assuming a maintenance window is available; using plain `CREATE INDEX` on a large, actively-written table; performing a direct column rename/retype during a rolling deploy. → [Common Mistakes](../../handbook/databases/zero-downtime-schema-migration.md#common-mistakes).

## 8. Staff-level discussion

A schema change that looks routine in staging (small table, no concurrent load) can cause a real production outage purely from lock duration once the table is large and under real write traffic. → [Staff-Level Discussion](../../handbook/databases/zero-downtime-schema-migration.md#interview-answer-framework).

## 9. Summary

A plain `CREATE INDEX` measurably blocks writes for its full build duration; `CONCURRENTLY` avoids that at the cost of a slower build. Column renames/retypes need expand-contract, not a direct change, to keep old and new code both working during a rolling deploy. → [Summary](../../handbook/databases/zero-downtime-schema-migration.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/databases/zero-downtime-schema-migration.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/databases/zero-downtime-schema-migration.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/databases/zero-downtime-schema-migration.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/databases/zero-downtime-schema-migration.md#practice-exercises) and [Solutions](../../handbook/databases/zero-downtime-schema-migration.md#solutions). Reproducible scripts: `practice/sql/week-10/zero-downtime-migration/run-blocking.sh` and `run-concurrently.sh`.

## 14. Additional Reading

- [PostgreSQL documentation — Building Indexes Concurrently](https://www.postgresql.org/docs/16/sql-createindex.html#SQL-CREATEINDEX-CONCURRENTLY)

## 15. Official References

- [PostgreSQL documentation — Explicit Locking](https://www.postgresql.org/docs/16/explicit-locking.html) — the lock-mode table naming exactly which operations conflict with which
