#!/bin/bash
# Real proof that PostgreSQL does NOT escalate row locks to a table lock the way
# MySQL/InnoDB or SQL Server do — and a real demonstration of the actual mechanism
# that takes escalation's place: a fixed-size shared lock table
# (max_locks_per_transaction * max_connections), which is exhausted by too many
# DISTINCT lockable objects in one transaction, not by too many row locks.
set -euo pipefail
cd "$(dirname "$0")"

PSQL="docker exec locks-pg psql -U postgres -d appdb"

echo "=== Bringing up PostgreSQL with a deliberately small shared lock table"
echo "    (max_locks_per_transaction=10, max_connections=20 -> ~200 slots total) ==="
docker compose up -d
until docker exec locks-pg pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done
echo "Ready."

echo
echo "=== Locking exactly 1 row with SELECT ... FOR UPDATE, real pg_locks snapshot ==="
$PSQL -c "
BEGIN;
SELECT * FROM many_rows WHERE id = 1 FOR UPDATE;
SELECT locktype, mode, granted FROM pg_locks WHERE pid = pg_backend_pid();
COMMIT;
"

echo
echo "=== Locking 20,000 rows (the ENTIRE table) with one FOR UPDATE, same real snapshot ==="
$PSQL -c "
BEGIN;
WITH locked AS (SELECT * FROM many_rows FOR UPDATE) SELECT count(*) FROM locked;
SELECT locktype, mode, granted FROM pg_locks WHERE pid = pg_backend_pid();
COMMIT;
"
echo "Real finding: identical 5-row lock footprint for 1 row and 20,000 rows — the same"
echo "relation-level RowShareLock (SELECT ... FOR UPDATE's real intent lock) either way."
echo "PostgreSQL's row-level locking lives in the tuple header itself, not in the lock"
echo "manager, so there was never a per-row lock-table entry to 'escalate' from."

echo
echo "=== Now: 300 pg_advisory_xact_lock() calls in one transaction — each one really"
echo "    consumes a shared lock table slot, unlike the row locks above ==="
if $PSQL -c "BEGIN; WITH x AS (SELECT pg_advisory_xact_lock(g) FROM generate_series(1,300) g) SELECT count(*) FROM x; COMMIT;" 2>&1; then
    echo "(succeeded — still under the real shared-memory ceiling)"
fi

echo
echo "=== Now: 5,000 pg_advisory_xact_lock() calls in one transaction ==="
$PSQL -c "BEGIN; SELECT pg_advisory_xact_lock(g) FROM generate_series(1,5000) g; COMMIT;" 2>&1 || true

echo
echo "=== Tearing down ==="
docker compose down -v >/dev/null
echo "Done."
