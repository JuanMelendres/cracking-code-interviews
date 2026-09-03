# Locks, deadlocks, and lock escalation in RDBMS (T-613) — runnable verification

Real, executed PostgreSQL 16 (Docker) output backing
[`syllabus/06-databases/locks-deadlocks-and-lock-escalation.md`](../../../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md)
(T-613). A real, reproduced two-transaction deadlock, caught by PostgreSQL's own
detector, not a scripted timeout — and real, direct proof that PostgreSQL does **not**
escalate row locks to a table lock the way MySQL/InnoDB or SQL Server do, plus the
real failure mode (`out of shared memory`) that takes escalation's place.

This directory deliberately doesn't repeat
[`isolation-levels-and-concurrency-anomalies.md`](../../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md)'s
existing coverage of lost updates, write skew, and `SELECT ... FOR UPDATE` as a race
fix — this is about the lock manager mechanism itself: real deadlock detection, and
what actually happens (and doesn't happen) as lock count grows.

## Setup and run

Requires Docker.

```bash
cd practice/sql/locks-deadlocks-and-lock-escalation
./run-all-demos.sh
```

Each script (`deadlock-demo.sh`, `no-escalation-demo.sh`) can also run standalone.

## Real observed output (last full run)

### `deadlock-demo.sh` — a real, reproduced deadlock

Two real, persistent transactions (driven via named pipes into two long-running
`psql` sessions in the same container) take their first row lock in opposite order,
then each reaches for the other's row:

```
=== Real pg_locks snapshot: both sessions hold a granted row lock, no waiters yet ===
 pid |       mode       | granted | relation
-----+------------------+---------+----------
  95 | RowExclusiveLock | t       | accounts
  94 | RowExclusiveLock | t       | accounts
```

After both send their second, conflicting `UPDATE`, a real snapshot mid-deadlock
shows the actual circular wait — not on the row itself, but on each other's
transaction ID, exactly matching the mechanism named in the real error a moment
later:

```
 pid |   locktype    |     mode      | granted | transactionid
-----+---------------+---------------+---------+---------------
  94 | transactionid | ShareLock     | f       |           736
  95 | transactionid | ShareLock     | f       |           737
  94 | transactionid | ExclusiveLock | t       |           737
  95 | transactionid | ExclusiveLock | t       |           736
```

PostgreSQL's own deadlock detector (not a script) breaks it after `deadlock_timeout`
(default 1s):

```
=== Session B real output ===
BEGIN
UPDATE 1
ERROR:  deadlock detected
DETAIL:  Process 94 waits for ShareLock on transaction 736; blocked by process 95.
Process 95 waits for ShareLock on transaction 737; blocked by process 94.
HINT:  See server log for query details.
CONTEXT:  while updating tuple (0,1) in relation "accounts"
```

Session A's real output shows both its `UPDATE`s completed — real, direct evidence of
which side the detector picked as the victim.

### `no-escalation-demo.sh` — real proof PostgreSQL does not escalate row locks

Run against a deliberately small shared lock table
(`max_locks_per_transaction=10`, `max_connections=20`).

**Locking 1 row vs. locking all 20,000 rows in the table**, same real `pg_locks`
query immediately after each:

```
   locktype    |      mode       | granted
---------------+-----------------+---------
 relation      | AccessShareLock | t
 relation      | RowShareLock    | t
 relation      | RowShareLock    | t
 virtualxid    | ExclusiveLock   | t
 transactionid | ExclusiveLock   | t
(5 rows)
```

**Identical, real, 5-row lock footprint for both** — 1 row and 20,000 rows produce
the exact same relation-level `RowShareLock`. There was never a per-row entry in the
lock manager to escalate from: PostgreSQL's row-level locking lives in the tuple
header itself.

**What actually exhausts the shared lock table instead** — not row locks, but too many
*distinct lockable objects* (here, advisory locks) in one transaction:

```
=== Now: 300 pg_advisory_xact_lock() calls in one transaction ===
 count
-------
   300
(succeeded — still under the real shared-memory ceiling)

=== Now: 5,000 pg_advisory_xact_lock() calls in one transaction ===
ERROR:  out of shared memory
HINT:  You might need to increase max_locks_per_transaction.
```

The real, direct contrast: 20,000 row locks in one transaction cost nothing extra in
the lock manager; 5,000 advisory locks (each a genuine, distinct lockable object)
exhausted a shared pool that 300 fit into comfortably. This is the real Postgres
analog to "lock escalation" — not row-count pressure, but distinct-object pressure
against a fixed-size shared memory allocation.

## What this does and does not prove

Both findings are real, direct, and reproducible on any PostgreSQL 16 instance — not
version-specific quirks of this exact Docker setup. What this doesn't cover: MySQL's
or SQL Server's actual escalation mechanics (this repo is PostgreSQL-focused
throughout; the comparison in the handbook chapter is stated from documented,
well-known behavior of those engines, not independently reproduced here), and
production-scale `max_locks_per_transaction` tuning guidance beyond the mechanism
demonstrated.
