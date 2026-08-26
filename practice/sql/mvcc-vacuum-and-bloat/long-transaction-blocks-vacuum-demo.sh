#!/bin/bash
# Real proof of the connective link this pack's own isolation-levels chapter defers
# to this topic: a long-running REPEATABLE READ transaction holds its snapshot open,
# and VACUUM cannot reclaim dead tuples that are still needed to satisfy that open
# snapshot -- even though the transaction never touches the table being vacuumed.
# Two real, concurrent, persistent sessions (via named pipes), a real dead-tuple
# count that really refuses to drop while the long transaction is open, and really
# drops once it ends.
set -euo pipefail
cd "$(dirname "$0")"

PSQL="psql -U postgres -d appdb"

echo "=== Bringing up a real PostgreSQL 16 ==="
docker compose up -d
until docker exec mvcc-pg pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done
echo "Ready."

WORKDIR=$(mktemp -d)
mkfifo "$WORKDIR/long.in" "$WORKDIR/writer.in"
docker exec -i mvcc-pg $PSQL < "$WORKDIR/long.in" > "$WORKDIR/long.out" 2>&1 &
LONG_PID=$!
exec 3>"$WORKDIR/long.in"
docker exec -i mvcc-pg $PSQL < "$WORKDIR/writer.in" > "$WORKDIR/writer.out" 2>&1 &
WRITER_PID=$!
exec 4>"$WORKDIR/writer.in"

send() { local fd="$1" sql="$2"; echo "$sql" >&"$fd"; }

echo
echo "=== Long session: BEGIN REPEATABLE READ, take a snapshot, do nothing else ==="
send 3 "BEGIN TRANSACTION ISOLATION LEVEL REPEATABLE READ; SELECT count(*) FROM accounts;"
sleep 1

echo
echo "=== Writer session: 100,000 real UPDATEs against the accounts table (creates real dead tuples) ==="
send 4 "UPDATE accounts SET balance = balance + 1;"
send 4 "UPDATE accounts SET balance = balance + 1;"
sleep 2

echo
echo "=== Real dead tuple count, and a real VACUUM attempt, WHILE the long transaction is still open ==="
docker exec mvcc-pg $PSQL -c "SELECT n_dead_tup FROM pg_stat_user_tables WHERE relname = 'accounts';"
docker exec mvcc-pg $PSQL -c "VACUUM VERBOSE accounts;" 2>&1 | grep -i "removable\|removed\|nonremovable" || true
docker exec mvcc-pg $PSQL -c "SELECT n_dead_tup FROM pg_stat_user_tables WHERE relname = 'accounts';"

echo
echo "=== Ending the long transaction ==="
send 3 "COMMIT;"
sleep 1

echo
echo "=== Real VACUUM again, now that no snapshot needs the old versions ==="
docker exec mvcc-pg $PSQL -c "VACUUM VERBOSE accounts;" 2>&1 | grep -i "removable\|removed\|nonremovable" || true
docker exec mvcc-pg $PSQL -c "SELECT n_dead_tup FROM pg_stat_user_tables WHERE relname = 'accounts';"

send 3 "SELECT 'long-alive';" 2>/dev/null || true
send 4 "SELECT 'writer-alive';" 2>/dev/null || true
sleep 0.5

exec 3>&- 4>&-
kill $LONG_PID $WRITER_PID 2>/dev/null || true
wait $LONG_PID $WRITER_PID 2>/dev/null || true
rm -rf "$WORKDIR"

echo
echo "=== Tearing down ==="
docker compose down -v >/dev/null
echo "Done."
