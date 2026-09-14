#!/bin/bash
# Real, executed demonstration of PgBouncer's transaction-pooling connection
# reuse, and the session-state leak it causes when application code doesn't
# explicitly release session-scoped resources (here, an advisory lock).
set -euo pipefail
cd "$(dirname "$0")"

PSQL="docker exec -i pgbouncer-demo-postgres psql -q postgresql://postgres:demopass@pgbouncer-demo-bouncer:6432/demo"

echo "=== Part 1: sequential 'sessions' with no concurrency -- same real backend, session state leaks ==="
$PSQL <<'SQL'
\set QUIET on
SELECT pg_backend_pid() AS session_1_backend;
SELECT pg_advisory_lock(42);
-- app "ends its session" here without ever calling pg_advisory_unlock(42)
SQL

echo
echo "--- a second, logically unrelated client asks PgBouncer for a connection ---"
$PSQL <<'SQL'
\set QUIET on
SELECT pg_backend_pid() AS session_2_backend;
SELECT pg_try_advisory_lock(42) AS lock_still_acquirable_here;
SELECT pg_advisory_unlock_all();
SQL

echo
echo "=== Part 2: genuine concurrency -- PgBouncer opens a SECOND real backend, and locking behaves correctly ==="
$PSQL <<'SQL' > session_a.out 2>&1 &
\set QUIET on
SELECT pg_backend_pid() AS session_a_backend;
SELECT pg_advisory_lock(99);
SELECT pg_sleep(4);
SELECT pg_advisory_unlock(99);
SQL
SESSION_A_PID=$!
sleep 1
$PSQL <<'SQL' > session_b.out 2>&1
\set QUIET on
SELECT pg_backend_pid() AS session_b_backend, clock_timestamp() AS start_time;
SELECT pg_advisory_lock(99);
SELECT clock_timestamp() AS acquired_time;
SELECT pg_advisory_unlock(99);
SQL
wait "$SESSION_A_PID"

echo "--- concurrent session A (held the lock, slept 4s) ---"
cat session_a.out
echo "--- concurrent session B (blocked until A released) ---"
cat session_b.out
rm -f session_a.out session_b.out
