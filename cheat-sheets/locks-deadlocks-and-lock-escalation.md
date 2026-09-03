---
title: "Cheat Sheet: Locks, Deadlocks, and Lock Escalation"
slug: locks-deadlocks-and-lock-escalation
document_type: cheat-sheet
domain: databases
topic_id: T-613
canonical: ../handbook/databases/locks-deadlocks-and-lock-escalation.md
last_updated: 2026-09-02
---

# Locks, Deadlocks, and Lock Escalation

**Canonical chapter:** [`syllabus/06-databases/locks-deadlocks-and-lock-escalation.md`](../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md)

## Core Mental Model

A lock is a promise, a deadlock is two promises that can't both be kept, and escalation is a decision one database makes and another deliberately doesn't. A deadlock is simply two transactions that have each promised something the other one now needs, with no way out except one breaking its promise. Escalation — converting many small row locks into one big table lock — is a separate design decision PostgreSQL deliberately never makes, because of how it represents row locks in the first place.

## Essential Definitions

- **Deadlock** — a cycle of transactions each waiting on a lock held by the next, a genuine circular dependency, not merely slow contention.
- **Wait-for graph** — PostgreSQL's real detection mechanism: an edge from A to B exists if A waits on a lock B holds; a deadlock is a cycle in this graph, checked after `deadlock_timeout` (default 1s).
- **Lock escalation** — converting many fine-grained row locks into one coarser table lock once a threshold is crossed (MySQL/InnoDB, SQL Server); PostgreSQL never does this.
- **Row locks live on the tuple itself** — an ordinary row lock (`UPDATE`/`DELETE`/`SELECT ... FOR UPDATE`) is represented via fields on the heap tuple's own header (`xmax`), not as a lock-manager entry — the reason there is nothing to escalate.
- **`max_locks_per_transaction`** — sizes PostgreSQL's fixed shared lock table (`≈ max_locks_per_transaction × (max_connections + max_prepared_transactions)`); consumed by distinct relations and advisory locks, never by ordinary row locks.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Two code paths lock more than one row of the same kind | Enforce a fixed, shared lock acquisition order |
| Carrying a "row locks escalate under contention" assumption from MySQL/SQL Server | Verify against PostgreSQL specifically — it does not escalate |
| Sizing `max_locks_per_transaction` | Base it on worst-case count of distinct lockable objects (tables, partitions, advisory locks), never row count |
| A bulk `UPDATE` touching huge row counts feels risky | It isn't, from a lock-table standpoint — chunk only to bound lock *hold duration*, not lock-table pressure |

**Engine comparison:**

| | PostgreSQL | MySQL/InnoDB (default) | SQL Server |
|---|---|---|---|
| Row locks tracked in lock manager? | No — tuple header only | Yes | Yes |
| Escalates to table lock? | Never | Configurable, can occur | Yes, configurable |
| Real limiting factor instead | `max_locks_per_transaction`, distinct objects | Escalation threshold | Escalation threshold |

## Key Numbers (real, executed on PostgreSQL)

- `SELECT ... FOR UPDATE` locking 1 row vs. 20,000 rows: identical 5-row `pg_locks` footprint both times — no escalation, nothing to consolidate.
- 300 `pg_advisory_xact_lock()` calls in one transaction succeeded; 5,000 produced a real `ERROR: out of shared memory / HINT: You might need to increase max_locks_per_transaction.`
- Real reproduced deadlock: `DETAIL: Process 94 waits for ShareLock on transaction 736; blocked by process 95. Process 95 waits for ShareLock on transaction 737; blocked by process 94.`

## Common Pitfalls

- Stating that PostgreSQL escalates row locks to table locks under contention — this is false and directly contradicted by measured evidence.
- Describing a deadlock as "the database got confused" instead of a genuine, detectable cycle with a real algorithm behind it.
- Treating `ERROR: deadlock detected` as a bug to eliminate rather than an expected outcome application code must catch and retry.
- Assuming a slow bulk `UPDATE` on PostgreSQL is a locking problem before checking `EXPLAIN ANALYZE` — lock cost is flat regardless of row count.
- Raising `max_locks_per_transaction` reactively in production after the first `out of shared memory` error instead of sizing it deliberately.

## Interview Answer Skeleton

**30-sec:** A deadlock is a real, detectable cycle of transactions waiting on each other; PostgreSQL detects it via a wait-for graph after `deadlock_timeout` and aborts one to break the cycle. PostgreSQL does not escalate row locks to table locks — row locks live on the tuple itself, so there's nothing to escalate.

**2-min:** Add the real verification: locking 1 row and then 20,000 rows produced an identical lock-table footprint. State the real fix for recurring deadlocks (consistent lock ordering) and the real different PostgreSQL limit (`max_locks_per_transaction`, tied to distinct objects, not row count).

**Whiteboard:** Draw two transactions as circles, each holding a row and reaching for the other's — circle the two arrows as a "wait-for cycle." Separately, draw one table row with "lock lives here" pointing directly at the row (not a separate lock-manager box) — this single annotation makes the escalation-myth-correcting point.

**Staff-level framing:** Name the actual detection mechanism and read real `pg_locks` evidence rather than treating it as opaque. Treat "bulk update risks escalation blocking" as a claim to verify against the specific engine, not an assumption carried across database systems.

## Production Warning Signs

- A funds-transfer or similar cross-row operation intermittently fails with `ERROR: deadlock detected` — check for inconsistent lock acquisition order across code paths touching the same rows.
- `ERROR: out of shared memory` mentioning `max_locks_per_transaction` — caused by too many distinct lockable objects (tables, partitions, advisory locks) in one transaction, not row count.
- Two queries appear to hang indefinitely without erroring — ordinary blocking, not a deadlock (no cycle); query `pg_locks` joined to `pg_stat_activity` to find the real blocking relationship.

## Related

- `syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md`
- `syllabus/06-databases/optimistic-vs-pessimistic-locking.md`
- `syllabus/06-databases/mvcc-vacuum-and-bloat.md`
- `syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md`
