---
title: "T-616 · Zero-Downtime Schema Migration"
topic_id: T-616
domain: DistributedData
tier: Staff
iwi: 7.30
prerequisites: [T-609]
unlocks: []
week: 10
last_reviewed: 2026-07-29
---

# T-616 · Zero-Downtime Schema Migration

**IWI 7.30 · Staff tier**

**Verification note:** the blocking-vs-`CONCURRENTLY` timings in §3 are real, measured wall-clock output from `practice/sql/week-10/zero-downtime-migration/` against a live Postgres 16 (Docker), a genuine 2-million-row table, and a real concurrent `INSERT` from a second session.

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

Zero-downtime migration is the discipline of changing a live database's schema (add/rename/retype a column, add an index, change a constraint) without blocking the application's normal reads and writes for the duration — because "take a maintenance window" is rarely an option for a system with real concurrent traffic and no acceptable downtime.

## 2. Why it exists

Many schema changes that look instantaneous in a small local database take real, measurable time against production-scale data, and several of Postgres's default DDL operations hold locks for that entire duration — a `CREATE INDEX` on a large table isn't a millisecond operation at scale, and by default it blocks every write to that table until it finishes.

## 3. Blocking vs CONCURRENTLY, measured

**Real setup**: a 2,000,000-row table, `big_table`. Two real sessions: one running `CREATE INDEX`, the other attempting a concurrent `INSERT` shortly after the index build starts.

**Real output, plain `CREATE INDEX`:**

```
Starting blocking CREATE INDEX in the background...
Attempting a concurrent INSERT while the index build is in flight...
CREATE INDEX
INSERT 0 1
RESULT: concurrent INSERT took 1943ms while a plain CREATE INDEX was running
```

**Real output, `CREATE INDEX CONCURRENTLY`:**

```
Starting CREATE INDEX CONCURRENTLY in the background...
Attempting a concurrent INSERT while the CONCURRENTLY index build is in flight...
INSERT 0 1
CREATE INDEX
RESULT: concurrent INSERT took 84ms while CREATE INDEX CONCURRENTLY was running
```

**1943ms vs 84ms — roughly 23x.** The plain `CREATE INDEX` holds a `SHARE` lock on the table for its entire ~2-second build, which blocks writes (though not reads) — the concurrent `INSERT` in that run had to wait for the ENTIRE index build to finish before it could proceed, visible directly in the output ordering: `CREATE INDEX` completes, THEN `INSERT 0 1` prints. `CREATE INDEX CONCURRENTLY` uses a weaker locking strategy (building the index in multiple passes, each holding only a brief lock) specifically so writes are never blocked for the operation's duration — visible in the output ordering flipping: `INSERT 0 1` completes WHILE the index build is still running, and `CREATE INDEX` finishes afterward.

## 4. Expand-contract for column/type changes

Column renames and type changes need a different technique than index creation, because the hazard isn't lock duration — it's that old application code (mid-deploy, or simply not yet redeployed) and new application code must both work correctly against the schema simultaneously, since a rolling deploy means both versions run concurrently for some period. **Expand-contract** solves this in three phases:

1. **Expand**: add the new column/structure alongside the old one — nothing is removed yet, so both old and new code work unmodified.
2. **Migrate**: backfill the new column from the old one for existing rows, and have the application dual-write to both during the transition (every write updates both old and new columns) — this is exactly the dual-write pattern from `01-saga-outbox-and-distributed-transactions.md`, applied to a single database instead of two systems, and it inherits the same hazard: a crash between the two writes needs the same at-least-once-plus-idempotency treatment, or the backfill has to reconcile the two later.
3. **Contract**: once all application instances are confirmed running the new code and both columns are verified in sync, drop the old column.

**"Rename a column on a live 200M-row table"** — the blueprint's own named follow-up — is answered directly by this: a plain `ALTER TABLE ... RENAME COLUMN` is instant at the catalog level regardless of table size (it doesn't rewrite data), but it breaks any still-running old code referencing the old name immediately, which is why the safe version is expand (add the new column), migrate (dual-write + backfill), contract (drop the old column) — never a single atomic rename against a system with a rolling deploy.

## 5. Trade-offs

| Approach | Benefit | Cost |
|---|---|---|
| Plain `CREATE INDEX` | Simpler, single-pass, generally faster to complete | Blocks writes for the full duration — measured 1943ms of blocked `INSERT` here |
| `CREATE INDEX CONCURRENTLY` | Doesn't block writes — measured 84ms here | Slower overall (multiple passes), can fail and leave an invalid index requiring cleanup, cannot run inside a transaction block |
| Direct column rename/retype | Simple, one statement | Breaks any code still running the old schema assumption during a rolling deploy |
| Expand-contract | Old and new code both work throughout the transition | More steps, more total migration time, and the dual-write phase inherits its own atomicity hazard |

## 6. Interview questions

### Q1. Rename a column on a live 200M-row table.

- **Expected answer:** expand-contract — add the new column, dual-write + backfill, verify, then drop the old column, never a single rename against a system serving live traffic from potentially-mixed application versions.
- **Common mistakes:** proposing a direct `RENAME COLUMN` as if it were safe merely because it's fast at the catalog level, without addressing the mixed-version rolling-deploy hazard.
- **Follow-up questions:** "The backfill crashes halfway through. Now what?"
- **Senior-level expectations:** correctly proposes expand-contract with the three phases named.
- **Staff-level expectations:** names the dual-write phase's own atomicity hazard explicitly and connects it to T-618's dual-write measurement — the backfill/dual-write step needs the same idempotency-or-outbox-style thinking, not a naive assumption that "just write to both columns" is itself safe under a crash.

### Q2. How do you add an index to a 500M-row table in production without downtime?

- **Expected answer:** `CREATE INDEX CONCURRENTLY` — measured directly in §3 to not block concurrent writes, at the cost of a slower, multi-pass build.
- **Common mistakes:** not knowing `CONCURRENTLY` exists, or not knowing it can leave an invalid index on failure requiring `DROP INDEX` and a retry.
- **Follow-up questions:** "It failed partway through. What state is the index in, and what do you do?"
- **Senior-level expectations:** names `CONCURRENTLY` and its non-blocking property.
- **Staff-level expectations:** knows the failure mode (an `INVALID` index left behind, consuming disk and requiring explicit cleanup before retrying) and that `CONCURRENTLY` cannot run inside an explicit transaction block, which affects how migration tooling must invoke it.

## 7. Common mistakes

- Assuming a maintenance window is available for schema changes — the blueprint's own named misconception for this topic.
- Using plain `CREATE INDEX` on a large, actively-written table without considering `CONCURRENTLY`.
- Performing a direct column rename/retype against a system with a rolling deploy, breaking whichever application version doesn't match the new schema during the transition window.

## 8. Staff-level discussion

The 23x measured gap between blocking and `CONCURRENTLY` index creation is a small, safe demonstration of a class of production incident that's entirely self-inflicted: a schema change that looks routine in a staging environment (small table, no concurrent load) can cause a real production outage purely from lock duration once the table is large and under real write traffic — the operation itself isn't wrong, its blocking behavior at scale is the hazard. A Staff engineer treats every schema change against a production-scale table as requiring an explicit answer to "what does this lock, and for how long, under real concurrent load" before running it — not assuming a DDL statement that works instantly in development will behave the same way in production.

## 9. Summary

A plain `CREATE INDEX` measurably blocked a concurrent `INSERT` for the index build's full ~2-second duration; the identical operation with `CONCURRENTLY` let the same `INSERT` complete in 84ms while the index was still building — roughly 23x faster for the write path, at the cost of a slower overall index build. Column renames/retypes need expand-contract, not a direct schema change, specifically to keep old and new application code both working during a rolling deploy — and the dual-write phase of expand-contract inherits the same atomicity hazard as any other dual write (T-618), not a free pass just because it's within one database.

## 10. Key Takeaways

- Plain `CREATE INDEX` blocks writes for its full build duration — measured at ~1943ms of blocked INSERT here.
- `CREATE INDEX CONCURRENTLY` avoids that block — measured at 84ms — at the cost of a slower, multi-pass build and the risk of a leftover `INVALID` index on failure.
- Direct column renames/retypes break mixed-version rolling deploys; expand-contract keeps both versions working throughout.
- The dual-write phase of expand-contract has the same atomicity hazard as any cross-system dual write — it isn't automatically safe just because it's one database.

## 11. Cheat Sheet

| Change | Safe approach |
|---|---|
| Add an index to a large, live table | `CREATE INDEX CONCURRENTLY` |
| Rename or retype a column | Expand (add new) → migrate (dual-write + backfill) → contract (drop old) |
| Add a new column with a default | Generally safe directly in modern Postgres (metadata-only default since PG 11) — verify for the specific type/version |
| Drop a column | Only after confirming zero code references it — the "contract" step, not a standalone operation |

## 12. Flashcards

1. **Q: What lock does a plain `CREATE INDEX` hold, and what does it block?** A: A `SHARE` lock on the table, held for the entire build duration — blocks writes (not reads) — measured at ~1943ms here.
2. **Q: What does `CREATE INDEX CONCURRENTLY` trade for not blocking writes?** A: A slower, multi-pass build, and the risk of a leftover `INVALID` index requiring manual cleanup on failure; also can't run inside an explicit transaction block.
3. **Q: Why can't you just directly rename a column during a rolling deploy?** A: Old and new application code versions run concurrently during the rollout; a direct rename breaks whichever version doesn't match — expand-contract keeps both working throughout.

(Full week-level deck: `07-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce both scripts: `practice/sql/week-10/zero-downtime-migration/run-blocking.sh` and `run-concurrently.sh`.
2. Kill a `CREATE INDEX CONCURRENTLY` mid-build (Ctrl+C or a session timeout) and inspect `pg_index` for the resulting `INVALID` index — write out the exact cleanup steps.
3. Design the full expand-contract sequence for renaming `orders.amount` to `orders.amount_cents` (a type-compatible rename) on a live, actively-written table, including what the dual-write phase's crash-safety requirement is.

## 14. Additional Reading

- [PostgreSQL documentation — Building Indexes Concurrently](https://www.postgresql.org/docs/16/sql-createindex.html#SQL-CREATEINDEX-CONCURRENTLY)

## 15. Official References

- [PostgreSQL documentation — Explicit Locking](https://www.postgresql.org/docs/16/explicit-locking.html) — the lock-mode table naming exactly which operations conflict with which
