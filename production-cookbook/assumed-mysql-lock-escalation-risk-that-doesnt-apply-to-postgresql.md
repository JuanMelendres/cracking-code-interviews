---
title: "Assumed MySQL-Style Lock Escalation Risk That Didn't Transfer to PostgreSQL"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md
  - ../syllabus/06-databases/query-planning-and-explain-analyze.md
source: handbook/databases/locks-deadlocks-and-lock-escalation.md#production-scenarios
---

# Assumed MySQL-Style Lock Escalation Risk That Didn't Transfer to PostgreSQL

## Context

A team migrating from MySQL to PostgreSQL asks whether a bulk `UPDATE` touching hundreds of thousands of rows risks the same lock-escalation-driven blocking they've seen in MySQL/InnoDB, where row locks can escalate to coarser table-level locks under enough contention.

## Symptoms

No incident has actually occurred — the trigger is a design-review question carrying an assumption imported from a different database engine, asked before the bulk `UPDATE` is deployed.

## Impact

Left unverified, the assumption could have driven the team toward unnecessary defensive engineering (batching, throttling, or avoiding bulk updates entirely) to guard against a risk that does not exist in PostgreSQL, or — in the opposite failure mode — left them unprepared for the real, different limit that does.

## Initial Hypotheses

The stated concern was that PostgreSQL might escalate row locks to a table-level lock the same way MySQL/InnoDB does, blocking unrelated queries during a large bulk update.

## Evidence

[`no-escalation-demo.sh`](../../practice/sql/locks-deadlocks-and-lock-escalation/README.md) locked 1 row, then all 20,000 rows in a table, with an identical `pg_locks` query immediately after each. Result: **the exact same 5-row lock footprint both times** — one relation-level `RowShareLock`, regardless of whether 1 or 20,000 rows were touched. The same demo then showed what genuinely can exhaust PostgreSQL's shared lock table: not row count, but the count of *distinct lockable objects* in one transaction. 300 `pg_advisory_xact_lock()` calls in one transaction succeeded; 5,000 produced a real `ERROR: out of shared memory / HINT: You might need to increase max_locks_per_transaction.`

## Investigation Timeline

1. Design-review question raised: does a bulk `UPDATE` on hundreds of thousands of rows risk MySQL-style lock escalation in PostgreSQL.
2. `pg_locks` captured after locking a single row, establishing a baseline lock footprint.
3. Identical `pg_locks` query re-run after locking all 20,000 rows in the same table, showing no growth in the lock footprint.
4. A separate test escalated the number of *distinct lockable objects* per transaction (via `pg_advisory_xact_lock()` calls) rather than row count, to identify what actually does have a limit.
5. The real limit located at 5,000 distinct lockable objects in one transaction, producing `ERROR: out of shared memory`, pointing at `max_locks_per_transaction` as the actual, different constraint.

## Root Cause

PostgreSQL never tracks row locks as individual lock-manager entries the way MySQL/InnoDB does, so there is nothing to escalate — the MySQL-derived concern, while valid on that engine, does not transfer. The real, structurally different limit is the count of distinct lockable objects a single transaction can hold, bounded by `max_locks_per_transaction`.

## Immediate Mitigation

None needed for the bulk `UPDATE` case itself — it was never at risk.

## Permanent Fix

For the actual PostgreSQL risk (a transaction touching many distinct tables/partitions, or making heavy use of advisory locks), raise `max_locks_per_transaction` deliberately, sized to the real worst-case object count, rather than reactively after a production `out of shared memory` error.

## Alternatives Considered

None recorded — the scenario's resolution is verification (the bulk update was never actually at risk) rather than a mitigation for a real problem.

## Trade-offs

`max_locks_per_transaction` is multiplied across `max_connections` at server start — raising it grows a fixed shared-memory allocation whether or not most transactions ever approach the limit.

## Prevention

When migrating a team's intuitions from another RDBMS, verify architecture-specific claims like this one directly (as this chapter's demo does) rather than carrying over an assumption that happens to be false for the new engine.

## Monitoring and Alerts

- Alert on PostgreSQL logging an `out of shared memory` / `max_locks_per_transaction` error — this is the real, concrete failure mode in place of the assumed-but-nonexistent row-lock-escalation risk, and it should page rather than be discovered by a failed transaction in production.
- Track the count of distinct lockable objects touched by the application's largest transactions (tables, partitions, advisory locks) as a capacity metric feeding the `max_locks_per_transaction` sizing decision, rather than sizing that setting once and forgetting it as the schema (partition count, table count) grows.
- Any future "does PostgreSQL behave like `<other database>` here" question raised in design review should be tracked as an explicit action item to verify directly against a real PostgreSQL instance before it is treated as settled.

## Interview Story

This maps directly to a "verify, don't assume, when migrating between database engines" question. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a team migrating from MySQL asked whether a large bulk `UPDATE` risked the same lock-escalation blocking they'd experienced on InnoDB.
- **Task:** answer the question with real evidence rather than carrying the MySQL-derived assumption forward.
- **Action:** measured `pg_locks` before and after locking 1 row versus 20,000 rows in the same table, showing an identical, unchanged lock footprint; separately identified the real, different limit — a cap on distinct lockable objects per transaction, not row count.
- **Result:** confirmed the bulk update was never at risk, and instead sized `max_locks_per_transaction` deliberately for the real risk the team hadn't been asking about.

## Staff-Level Discussion

This scenario is valuable precisely because nothing went wrong — its lesson is about the cost of an unverified cross-engine assumption, in either direction. Had the team accepted the MySQL-derived fear at face value, they might have spent real engineering effort defensively batching or avoiding a bulk operation that was never dangerous on PostgreSQL. Had they instead assumed "PostgreSQL is fine, it doesn't have MySQL's problems" without looking further, they would have missed the real, different constraint (`max_locks_per_transaction`) that a heavy multi-table or advisory-lock transaction can actually hit. The Staff-level habit this demonstrates is treating an engine-migration assumption as a claim to verify with a real, targeted test — not by reading documentation in the abstract, and not by carrying over hard-won intuition from a previous database engine — because the two failure directions (over-defending against a nonexistent risk, and under-defending against a real one) are both expensive, and only direct measurement distinguishes them.

## Related Handbook Chapters

- [Locks, Deadlocks, and Lock Escalation](../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md) — canonical explanation of PostgreSQL's lock-manager model and why escalation does not occur.
- [Query Planning and EXPLAIN ANALYZE](../syllabus/06-databases/query-planning-and-explain-analyze.md) — the broader discipline of verifying database behavior with real, measured evidence rather than assumption.
