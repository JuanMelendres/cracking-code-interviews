#!/bin/bash
# Real proof of table bloat and the real difference between plain VACUUM (marks
# space reusable, does NOT shrink the file on disk) and VACUUM FULL (rewrites the
# table into a new, compact file -- real, measured file-size numbers throughout).
set -euo pipefail
cd "$(dirname "$0")"
PSQL="docker exec mvcc-pg psql -U postgres -d appdb -t -A"

size() {
    $PSQL -c "SELECT pg_size_pretty(pg_relation_size('accounts'));"
}

echo "=== Bringing up a real PostgreSQL 16 ==="
docker compose up -d
until docker exec mvcc-pg pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done
echo "Ready."

echo
echo "=== Real table size before any updates ==="
size

echo
echo "=== Updating EVERY row 5 times (250,000 real UPDATEs against 50,000 rows) ==="
for i in 1 2 3 4 5; do
    $PSQL -c "UPDATE accounts SET balance = balance + 1;" > /dev/null
    echo "  Pass $i done. Real table size now: $(size)"
done

echo
echo "=== Real dead tuple count before VACUUM ==="
$PSQL -c "SELECT n_dead_tup, n_live_tup FROM pg_stat_user_tables WHERE relname = 'accounts';"

echo
echo "=== Real table size after plain VACUUM (should NOT shrink) ==="
$PSQL -c "VACUUM accounts;" > /dev/null
size
$PSQL -c "SELECT n_dead_tup, n_live_tup FROM pg_stat_user_tables WHERE relname = 'accounts';"

echo
echo "=== Real table size after VACUUM FULL (SHOULD shrink -- real file rewrite) ==="
$PSQL -c "VACUUM FULL accounts;" > /dev/null
size

echo
echo "=== Tearing down ==="
docker compose down -v >/dev/null
echo "Done."
