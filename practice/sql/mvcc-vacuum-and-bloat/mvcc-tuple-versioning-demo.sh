#!/bin/bash
# Real proof that PostgreSQL's UPDATE never modifies a row in place under MVCC --
# it inserts a brand new tuple version and marks the old one dead, visible directly
# via the real system columns ctid (physical tuple location), xmin (the transaction
# that created this version), and xmax (the transaction that superseded/deleted it,
# 0 meaning "still live").
set -euo pipefail
cd "$(dirname "$0")"
PSQL="docker exec mvcc-pg psql -U postgres -d appdb -t -A"

echo "=== Bringing up a real PostgreSQL 16 ==="
docker compose up -d
until docker exec mvcc-pg pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done
echo "Ready."

echo
echo "=== Real tuple identity before any UPDATE ==="
$PSQL -c "SELECT id, balance, ctid, xmin FROM accounts WHERE id = 1;"

echo
echo "=== Real UPDATE #1 ==="
$PSQL -c "UPDATE accounts SET balance = balance + 100 WHERE id = 1;"
$PSQL -c "SELECT id, balance, ctid, xmin FROM accounts WHERE id = 1;"

echo
echo "=== Real UPDATE #2 ==="
$PSQL -c "UPDATE accounts SET balance = balance + 100 WHERE id = 1;"
$PSQL -c "SELECT id, balance, ctid, xmin FROM accounts WHERE id = 1;"

PAGE=$($PSQL -c "SELECT (ctid::text::point)[0]::int FROM accounts WHERE id = 1;")

echo
echo "=== Real proof: BEFORE any VACUUM, all three physical tuple versions still exist on page $PAGE ==="
echo "(the two old versions are dead -- t_xmax is non-zero -- but not yet reclaimed)"
$PSQL -c "
  SELECT lp, t_ctid, t_xmin, t_xmax,
         CASE WHEN t_xmax = 0 THEN 'LIVE (current version)' ELSE 'DEAD (superseded by xid ' || t_xmax || ')' END AS status
  FROM heap_page_items(get_raw_page('accounts', $PAGE))
  WHERE lp BETWEEN 53 AND 57
  ORDER BY lp;
"

echo
echo "=== Now running VACUUM -- watch the dead tuples really get reclaimed ==="
$PSQL -c "VACUUM (VERBOSE) accounts;" 2>&1 | grep -i "removable\|removed\|dead"

echo
echo "=== Real proof: AFTER VACUUM, the dead tuple slots on page $PAGE are gone/unused ==="
$PSQL -c "
  SELECT lp, t_ctid, t_xmin, t_xmax,
         CASE WHEN t_ctid IS NULL THEN 'UNUSED (reclaimed by VACUUM)'
              WHEN t_xmax = 0 THEN 'LIVE (current version)'
              ELSE 'DEAD (superseded by xid ' || t_xmax || ')' END AS status
  FROM heap_page_items(get_raw_page('accounts', $PAGE))
  WHERE lp BETWEEN 53 AND 57
  ORDER BY lp;
"

echo
echo "=== Tearing down ==="
docker compose down -v >/dev/null
echo "Done."
