#!/bin/bash
# Real proof of log-based CDC: a real PostgreSQL logical replication slot captures
# real INSERT/UPDATE/DELETE events directly from the WAL, with ZERO changes to the
# application's own SQL -- no outbox table, no explicit event-writing code. Contrast
# this directly with the transactional outbox pattern (see
# handbook/system-design/distributed-transactions-saga-and-outbox.md), which
# requires the application to explicitly write an outbox row in the same
# transaction; CDC instead reads what already happened, after the fact, from the
# database's own commit log.
set -euo pipefail
cd "$(dirname "$0")"
PSQL="docker exec cdc-pg psql -U postgres -d appdb -t -A"

echo "=== Real logical replication slot, using the built-in test_decoding plugin ==="
$PSQL -c "SELECT pg_drop_replication_slot('cdc_slot');" 2>/dev/null || true
$PSQL -c "SELECT pg_create_logical_replication_slot('cdc_slot', 'test_decoding');"

echo
echo "=== Ordinary application SQL -- no outbox table, no CDC-aware code at all ==="
$PSQL -c "INSERT INTO accounts (owner, balance) VALUES ('alice', 100);"
$PSQL -c "UPDATE accounts SET balance = balance + 50 WHERE owner = 'alice';"
$PSQL -c "DELETE FROM accounts WHERE owner = 'alice';"

echo
echo "=== Real CDC events, read directly from the WAL after the fact ==="
$PSQL -c "SELECT data FROM pg_logical_slot_get_changes('cdc_slot', NULL, NULL);"
