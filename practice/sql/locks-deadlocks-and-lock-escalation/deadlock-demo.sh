#!/bin/bash
# Real, reproduced PostgreSQL deadlock: two real, concurrent, persistent transactions
# (via named pipes feeding two long-running psql sessions inside the same container)
# take their first row lock in opposite order, then each blocks trying to take the
# other's row — a real circular wait. PostgreSQL's own deadlock detector, not a
# scripted timeout, is what breaks it.
set -euo pipefail
cd "$(dirname "$0")"

PSQL="psql -U postgres -d appdb"

echo "=== Bringing up a real PostgreSQL 16 ==="
docker compose up -d
until docker exec locks-pg pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done
echo "Ready."

WORKDIR=$(mktemp -d)
mkfifo "$WORKDIR/a.in" "$WORKDIR/b.in"
docker exec -i locks-pg $PSQL < "$WORKDIR/a.in" > "$WORKDIR/a.out" 2>&1 &
A_PID=$!
exec 3>"$WORKDIR/a.in"
docker exec -i locks-pg $PSQL < "$WORKDIR/b.in" > "$WORKDIR/b.out" 2>&1 &
B_PID=$!
exec 4>"$WORKDIR/b.in"

send() { local fd="$1" sql="$2"; echo "$sql" >&"$fd"; }

echo
echo "=== Session A: BEGIN; lock row id=1 ==="
send 3 "BEGIN; UPDATE accounts SET balance = balance - 100 WHERE id = 1;"
sleep 1

echo "=== Session B: BEGIN; lock row id=2 ==="
send 4 "BEGIN; UPDATE accounts SET balance = balance - 100 WHERE id = 2;"
sleep 1

echo
echo "=== Real pg_locks snapshot: both sessions hold a granted row lock, no waiters yet ==="
docker exec locks-pg $PSQL -c \
  "SELECT pid, mode, granted, relation::regclass FROM pg_locks WHERE relation = 'accounts'::regclass;"

echo
echo "=== Session A now wants row id=2 (held by B) — sent, will block ==="
send 3 "UPDATE accounts SET balance = balance + 100 WHERE id = 2;"
sleep 1

echo "=== Session B now wants row id=1 (held by A) — sent, completes the circular wait ==="
send 4 "UPDATE accounts SET balance = balance + 100 WHERE id = 1;"

echo
echo "=== Real pg_locks snapshot mid-deadlock: the actual wait-for cycle ==="
echo "(row-level waits show up as a ShareLock on the BLOCKING transaction's virtual"
echo "transaction id, not as a second row-level lock entry — this IS the mechanism"
echo "named in the real 'waits for ShareLock on transaction ...' error below)"
sleep 0.3
docker exec locks-pg $PSQL -c \
  "SELECT pid, locktype, mode, granted, transactionid FROM pg_locks WHERE locktype = 'transactionid' ORDER BY granted, pid;"

echo
echo "=== Waiting for PostgreSQL's real deadlock detector (deadlock_timeout, default 1s) ==="
sleep 3

send 3 "SELECT 'a-alive';"
send 4 "SELECT 'b-alive';"
sleep 1

echo
echo "=== Session A real output (one of these two sessions was really killed by the detector) ==="
cat "$WORKDIR/a.out"
echo
echo "=== Session B real output ==="
cat "$WORKDIR/b.out"

send 3 "ROLLBACK;" 2>/dev/null || true
send 4 "ROLLBACK;" 2>/dev/null || true
sleep 0.5

exec 3>&- 4>&-
kill $A_PID $B_PID 2>/dev/null || true
wait $A_PID $B_PID 2>/dev/null || true
rm -rf "$WORKDIR"

echo
echo "=== Tearing down ==="
docker compose down -v >/dev/null
echo "Done."
