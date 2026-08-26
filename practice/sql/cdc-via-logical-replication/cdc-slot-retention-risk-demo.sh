#!/bin/bash
# Real proof of CDC's real, recurring operational risk: an unconsumed logical
# replication slot prevents PostgreSQL from reclaiming WAL, even across a real
# CHECKPOINT, because the slot's restart_lsn marks WAL still needed to satisfy it --
# the exact same "held snapshot blocks reclamation" shape as
# handbook/databases/mvcc-vacuum-and-bloat.md's long-transaction-blocks-vacuum
# finding, here applied to WAL retention instead of tuple/vacuum retention.
set -euo pipefail
cd "$(dirname "$0")"
PSQL="docker exec cdc-pg psql -U postgres -d appdb -t -A"

wal_size() { $PSQL -c "SELECT pg_size_pretty(sum(size)) FROM pg_ls_waldir();"; }
retained() { $PSQL -c "SELECT pg_size_pretty(pg_wal_lsn_diff(pg_current_wal_lsn(), restart_lsn)) FROM pg_replication_slots WHERE slot_name='cdc_slot';"; }

echo "=== Real, fresh replication slot ==="
$PSQL -c "SELECT pg_drop_replication_slot('cdc_slot');" 2>/dev/null || true
$PSQL -c "SELECT pg_create_logical_replication_slot('cdc_slot', 'test_decoding');"
echo "Real WAL directory size: $(wal_size)"
echo "Real slot-retained WAL:  $(retained)"

echo
echo "=== Generating 200,000 real rows of WAL activity -- the slot is NOT being consumed ==="
$PSQL -c "INSERT INTO accounts (owner, balance) SELECT 'user' || g, g FROM generate_series(1, 200000) g;"
$PSQL -c "CHECKPOINT;"
echo "Real WAL directory size after a real CHECKPOINT: $(wal_size)"
echo "Real slot-retained WAL (this is what an unconsumed CDC pipeline looks like):  $(retained)"

echo
echo "=== Now consuming the backlog, plus one more real write + CHECKPOINT to fully advance the slot ==="
echo "(a real, honest quirk found while building this demo: restart_lsn needs one more"
echo " real write-and-checkpoint cycle after consumption to fully catch up -- consuming"
echo " alone doesn't instantly shrink retained_wal to zero)"
CONSUMED=$($PSQL -c "SELECT count(*) FROM pg_logical_slot_get_changes('cdc_slot', NULL, NULL);")
echo "Real events consumed: $CONSUMED"
$PSQL -c "INSERT INTO accounts (owner, balance) VALUES ('trigger-advance', 1);"
$PSQL -c "SELECT count(*) FROM pg_logical_slot_get_changes('cdc_slot', NULL, NULL);" > /dev/null
$PSQL -c "CHECKPOINT;"
echo "Real slot-retained WAL after the slot fully catches up: $(retained)"
