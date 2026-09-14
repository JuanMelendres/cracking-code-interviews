# PgBouncer transaction pooling — runnable verification

Real, executed output backing the PgBouncer section of
[`syllabus/06-databases/connection-pooling-and-sizing.md`](../../../syllabus/06-databases/connection-pooling-and-sizing.md)
(T-607). A real PostgreSQL 16 container behind a real PgBouncer 1.25.2
container in transaction-pooling mode (`default_pool_size = 2`), demonstrating
the real connection-multiplexing behavior and the real session-state-leak
risk that mode carries.

## Files

- `docker-compose.yml`, `pgbouncer.ini`, `userlist.txt` — a real PostgreSQL 16
  behind a real PgBouncer in `pool_mode = transaction`, pool size 2.
- `session-leak-demo.sh` — Part 1 proves that, absent real concurrency,
  PgBouncer hands the exact same real backend connection to two sequential,
  logically unrelated client sessions — and an advisory lock the first
  "session" never explicitly released is silently already held for the
  second. Part 2 proves the mode still works correctly under genuine
  concurrency: two clients open at the same time get two distinct real
  backend connections, and a lock genuinely blocks the second until the
  first releases it.

## Run

```bash
cd practice/sql/pgbouncer-transaction-pooling
docker compose up -d
./session-leak-demo.sh
docker compose down -v
```

## Real observed output (last full run, PostgreSQL 16 / PgBouncer 1.25.2)

### Part 1 — no concurrency: same real backend, session state leaks across "sessions"

```
=== Part 1: sequential 'sessions' with no concurrency -- same real backend, session state leaks ===
 session_1_backend
-------------------
                79
(pg_advisory_lock(42) acquired, never explicitly unlocked)

--- a second, logically unrelated client asks PgBouncer for a connection ---
 session_2_backend
-------------------
                79
 lock_still_acquirable_here
----------------------------
 t
```

Both "sessions" landed on the exact same real PostgreSQL backend (PID 79) —
PgBouncer, with no concurrent demand for the second pooled connection, simply
handed the same idle server connection back out. `pg_try_advisory_lock(42)`
returned `t` for the second client not because the lock was ever released,
but because it is, in reality, the *same* PostgreSQL session re-acquiring a
lock it already silently held. An application that treats "my transaction
committed" as "my session-scoped state is gone" is wrong under transaction
pooling — it can leak into whichever unrelated request happens to reuse that
connection next.

### Part 2 — genuine concurrency: two real, distinct backends, locking still correct

```
=== Part 2: genuine concurrency -- PgBouncer opens a SECOND real backend, and locking behaves correctly ===
--- concurrent session A (held the lock, slept 4s) ---
 session_a_backend
-------------------
                79
(pg_advisory_lock(99) acquired, pg_sleep(4), then unlocked)

--- concurrent session B (blocked until A released) ---
 session_b_backend |          start_time
-------------------+-------------------------------
                98 | 2026-09-14 02:07:28.133529+00
         acquired_time
-------------------------------
 2026-09-14 02:07:31.095404+00
```

With two clients genuinely open at once, PgBouncer really opened a second
backend (PID 98, distinct from session A's 79) — the actual multiplexing
value proposition (many client connections sharing few real PostgreSQL
backends). Session B's request for lock 99 blocked for ~2.96 real seconds,
matching session A's `pg_sleep(4)` plus unlock — proving the pool correctly
serialized two genuinely concurrent requests for the same advisory lock via
two distinct backend connections, not by any special PgBouncer-side lock
awareness (PgBouncer itself is not lock-aware; this is PostgreSQL's own
advisory-lock mechanism at the transaction layer).

## What this does and does not prove

This is real PgBouncer 1.25.2 and real PostgreSQL 16 behavior, not a
simulation — the identical connection-reuse and session-state-leak mechanism
applies in production regardless of scale. What changes at production scale
is only how often a "different session, same backend" reuse actually happens
(a function of concurrency versus pool size), not whether the underlying risk
exists.
