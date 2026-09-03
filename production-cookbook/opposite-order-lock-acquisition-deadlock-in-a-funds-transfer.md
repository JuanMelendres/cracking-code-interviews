---
title: "Deadlock from Opposite-Order Lock Acquisition in a Funds-Transfer Path"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/databases/locks-deadlocks-and-lock-escalation.md
  - ../handbook/databases/isolation-levels-and-concurrency-anomalies.md
source: handbook/databases/locks-deadlocks-and-lock-escalation.md#production-scenarios
---

# Deadlock from Opposite-Order Lock Acquisition in a Funds-Transfer Path

## Context

Two concurrent fund-transfer transactions run against the same pair of account rows: one credits account 1 then debits account 2, the other does the reverse order — debiting account 2 (in effect, locking it) before touching account 1.

## Symptoms

The transfers occasionally fail with a deadlock error instead of completing, rather than simply queuing and eventually succeeding.

## Impact

A fraction of concurrent transfers fail outright with a database error instead of completing, requiring application-level handling (retry) rather than silently waiting their turn.

## Initial Hypotheses

The transfer logic itself was suspected first, but the real evidence pointed directly at lock acquisition order: each transaction acquires its first lock in the opposite order from the other.

## Evidence

[`deadlock-demo.sh`](../../practice/sql/locks-deadlocks-and-lock-escalation/README.md) reproduced this exactly: Session A locks row `id=1`, Session B locks row `id=2` (both real, granted `RowExclusiveLock`s, confirmed via a real `pg_locks` snapshot). Each then reaches for the other's row and blocks. A second real `pg_locks` snapshot, captured mid-deadlock, shows the actual cycle: `pid 94 waits ShareLock on transactionid 736 (granted=f)`, `pid 95 waits ShareLock on transactionid 737 (granted=f)` — a real, live circular wait. After `deadlock_timeout` elapses, PostgreSQL's detector kills one side:

```
ERROR:  deadlock detected
DETAIL:  Process 94 waits for ShareLock on transaction 736; blocked by process 95.
Process 95 waits for ShareLock on transaction 737; blocked by process 94.
```

## Investigation Timeline

1. Deadlock errors observed on a subset of concurrent funds-transfer transactions.
2. Session-level lock state captured via `pg_locks` at the moment of contention, showing Session A holding `id=1` and Session B holding `id=2`, each waiting on the other.
3. A second `pg_locks` snapshot captured mid-deadlock confirmed the circular wait between the two transaction IDs directly.
4. PostgreSQL's own deadlock detector output (`ERROR: deadlock detected`) confirmed which process was killed and why, after `deadlock_timeout` elapsed.
5. Lock acquisition order in both code paths compared, confirming they acquire their first lock on opposite rows.

## Root Cause

Two transactions acquired their first lock in opposite order — the textbook precondition for deadlock. There is no way to make this pattern safe without changing something about lock acquisition order or granularity.

## Immediate Mitigation

The surviving transaction (Session A, in this run) completes normally; the losing transaction's application code must catch the deadlock error and retry — this is expected, correct behavior, not a bug to suppress.

## Permanent Fix

Enforce a consistent lock acquisition order across all code paths touching the same rows — e.g., always lock the lower account ID first — which makes the opposite-order precondition structurally impossible rather than merely unlikely.

## Alternatives Considered

None recorded beyond the ordering fix itself — the scenario treats consistent lock ordering as the direct, sufficient remedy for this specific pattern.

## Trade-offs

A strict ordering rule requires discipline across every code path that touches these rows, including ones written later by engineers unaware of the convention — worth encoding as a single shared data-access function rather than a comment.

## Prevention

Any code path acquiring locks on more than one row of the same kind (accounts, inventory items) should have an explicit, documented, enforced ordering rule.

## Monitoring and Alerts

- Alert on PostgreSQL's `deadlocks` counter (from `pg_stat_database`) trending above a low baseline for the affected database — a nonzero, recurring rate on a specific table pair is the earliest signal that an ordering rule is missing or being violated somewhere in the codebase.
- Log and tag deadlock errors caught by the application's retry handler with the specific row IDs and code path involved, so a recurring pattern against the same table pair is visible in aggregate, not just as isolated retried requests.
- Treat the shared data-access function that enforces lock ordering (the Permanent Fix) as the single place instrumentation and review attention need to focus — any new code path that bypasses it and acquires locks directly is a regression risk worth flagging in code review.

## Interview Story

This maps directly to a "diagnose and fix a database deadlock" question. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** concurrent funds-transfer transactions occasionally failed with a deadlock error instead of completing.
- **Task:** confirm the mechanism with real evidence rather than assuming it was lock contention in general.
- **Action:** captured `pg_locks` snapshots at the moment of contention and mid-deadlock, confirming a genuine circular wait between two sessions locking the same two rows in opposite order; read PostgreSQL's own deadlock-detector output to confirm which side was killed and why.
- **Result:** enforced a consistent lock acquisition order (always lock the lower account ID first) across every code path touching these rows, making the opposite-order precondition structurally impossible rather than merely unlikely.

## Staff-Level Discussion

A deadlock caught and retried correctly is not itself a defect — PostgreSQL's detector and the retry-on-catch pattern are working exactly as designed. The real organizational risk is that "acquire locks on the lower ID first" is a convention, not something the type system or the database enforces, and it only takes one new code path — written by someone unaware of the rule — to reintroduce the opposite-order pattern. A Staff engineer's response should be to centralize lock acquisition behind a single shared data-access function so the ordering rule has exactly one place to be correct, rather than relying on every future author independently remembering a documented convention. Monitoring the deadlock rate as a standing metric, rather than only reacting to user-visible transfer failures, turns "did we break the ordering rule somewhere" into an answerable, continuously-checked question instead of a discovery made during an incident.

## Related Handbook Chapters

- [Locks, Deadlocks, and Lock Escalation](../handbook/databases/locks-deadlocks-and-lock-escalation.md) — canonical mechanics of PostgreSQL locking and deadlock detection this incident reproduces.
- [Isolation Levels and Concurrency Anomalies](../handbook/databases/isolation-levels-and-concurrency-anomalies.md) — the broader concurrency-anomaly context this lock-ordering discipline sits within.
