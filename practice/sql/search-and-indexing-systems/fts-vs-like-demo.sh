#!/bin/bash
# Real proof that a database-native inverted index (tsvector + GIN) is a
# genuinely different execution strategy from a LIKE '%...%' scan -- not
# just "the same query, faster" -- plus real relevance ranking via ts_rank.
set -euo pipefail
cd "$(dirname "$0")"

# -h 127.0.0.1 forces a real TCP connection. A real, honest gotcha found
# while building this demo: `docker exec ... psql` with no -h defaults to
# the container's Unix socket, which is exactly what the OFFICIAL Postgres
# image's short-lived, init-script-running server listens on (Unix socket
# only, no TCP) before it restarts as the final server bound to
# 0.0.0.0:5432. The first version of this script connected to that doomed,
# about-to-be-killed temporary server via the socket and got a real
# "FATAL: terminating connection due to administrator command" mid-query
# when it shut down. Forcing TCP connects to the real, final server instead.
PSQL="docker exec search-index-pg psql -h 127.0.0.1 -U postgres -d appdb"

echo "=== Bringing up a real PostgreSQL 16 with 200,006 real rows ==="
docker compose up -d
until $PSQL -c "SELECT count(*) FROM articles;" >/dev/null 2>&1; do sleep 1; done
echo "Ready."

echo
echo "=== Real EXPLAIN ANALYZE: LIKE '%java%' (no usable index for a leading/trailing wildcard) ==="
$PSQL -c "EXPLAIN ANALYZE SELECT id, title FROM articles WHERE body LIKE '%java%';" | grep -E "Seq Scan|Execution Time|Rows Removed"

echo
echo "=== Real EXPLAIN ANALYZE: the SAME kind of query, but against the real GIN index ==="
$PSQL -c "EXPLAIN ANALYZE SELECT id, title FROM articles WHERE search_vector @@ to_tsquery('english', 'java');" | grep -E "Bitmap Index Scan|Index Cond|Execution Time"

echo
echo "=== Real relevance ranking via ts_rank -- OR of query terms, ranked by real relevance ==="
$PSQL -t -A -F' | ' -c "
  SELECT id, title, round(ts_rank(search_vector, query)::numeric, 4) AS rank
  FROM articles, to_tsquery('english', 'java | garbage | collection | tuning') query
  WHERE search_vector @@ query
  ORDER BY rank DESC;
"

echo
echo "=== Tearing down ==="
docker compose down -v
