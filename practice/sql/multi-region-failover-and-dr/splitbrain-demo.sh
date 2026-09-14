#!/bin/bash
# Real split-brain reproduction: a naive failover that promotes a standby WITHOUT
# fencing the old primary first, while the old primary is merely network-partitioned
# (not actually dead) — and the real fix, fencing, that prevents it.
#
# `docker exec` talks to a container via the Docker daemon directly, not over the
# container's own network — so it keeps working on the "old primary" even after that
# container is disconnected from the replication network, exactly the way a client
# on the same side of a real network partition as the old primary would still be able
# to reach it and write to it, unaware a failover has already happened elsewhere.
set -euo pipefail
cd "$(dirname "$0")"

PSQL_PRIMARY="docker exec -e PGPASSWORD=primarypass dr-region-primary psql -U postgres -d appdb -tAq"
PSQL_STANDBY="docker exec -e PGPASSWORD=primarypass dr-region-standby psql -U postgres -d appdb -tAq"

echo "=== Bringing up region-primary + region-standby ==="
docker compose up -d

echo "=== Waiting for streaming replication to establish ==="
until $PSQL_PRIMARY -c "SELECT state FROM pg_stat_replication WHERE application_name='walreceiver';" 2>/dev/null | grep -q streaming; do
    sleep 1
done
echo "Streaming replication confirmed."

echo
echo "=== Baseline: write on primary, confirm it replicates to standby ==="
$PSQL_PRIMARY -c "INSERT INTO ledger(payload) VALUES ('baseline');" >/dev/null
sleep 1
echo "Primary ledger:"
$PSQL_PRIMARY -c "SELECT seq, payload FROM ledger;"
echo "Standby ledger:"
$PSQL_STANDBY -c "SELECT seq, payload FROM ledger;"

echo
echo "=== Simulating a real network partition: disconnecting region-primary from dr-net ==="
echo "(the primary is NOT dead — it is alive and fully functional, just unreachable"
echo "from the standby, exactly like a real cross-region network split)"
docker network disconnect multi-region-failover-and-dr_dr-net dr-region-primary

echo
echo "=== A naive DR controller, seeing the primary unreachable, promotes the standby ==="
docker exec dr-region-standby su postgres -c "pg_ctl promote -D /var/lib/postgresql/data" >/dev/null
until $PSQL_STANDBY -c "SELECT pg_is_in_recovery();" 2>/dev/null | grep -q '^f$'; do
    sleep 0.2
done
echo "region-standby promoted. It now believes it is the one true primary."

echo
echo "=== The NEW primary (former standby) accepts a real write ==="
$PSQL_STANDBY -c "INSERT INTO ledger(payload) VALUES ('accepted-by-new-primary-after-failover');" >/dev/null

echo
echo "=== THE BUG: the OLD primary, still alive and still un-fenced, ALSO accepts a real write ==="
echo "(docker exec reaches it directly — exactly like a client on its side of the partition)"
$PSQL_PRIMARY -c "INSERT INTO ledger(payload) VALUES ('accepted-by-old-primary-unaware-of-failover');" >/dev/null
echo "That INSERT genuinely succeeded. Two nodes now both believe they are primary,"
echo "and both have accepted a real write the other one does not have."

echo
echo "=== Healing the partition ==="
docker network connect multi-region-failover-and-dr_dr-net dr-region-primary

echo
echo "=== REAL, OBSERVED SPLIT-BRAIN: the two nodes' ledgers have genuinely diverged ==="
echo "Old primary's ledger:"
$PSQL_PRIMARY -c "SELECT seq, payload FROM ledger ORDER BY seq;"
echo "New primary's ledger:"
$PSQL_STANDBY -c "SELECT seq, payload FROM ledger ORDER BY seq;"

echo
echo "=== Tearing down (unfenced run) ==="
docker compose down -v >/dev/null

echo
echo
echo "########################################################################"
echo "# Second run: the SAME scenario, but with real fencing before promotion #"
echo "########################################################################"
echo

docker compose up -d
until $PSQL_PRIMARY -c "SELECT state FROM pg_stat_replication WHERE application_name='walreceiver';" 2>/dev/null | grep -q streaming; do
    sleep 1
done
echo "Streaming replication confirmed."

echo
echo "=== Simulating the same network partition ==="
docker network disconnect multi-region-failover-and-dr_dr-net dr-region-primary

echo
echo "=== The fix: FENCE the old primary before promoting anything ==="
echo "docker pause is used here as a real, concrete stand-in for STONITH (Shoot The"
echo "Other Node In The Head) — it really freezes every process in the container via"
echo "the cgroup freezer, so it genuinely cannot process any query at all, from anyone,"
echo "including a client on its own side of the partition."
docker pause dr-region-primary >/dev/null
echo "region-primary is now paused (fenced)."

echo
echo "=== Attempting the same write against the fenced old primary (should be refused) ==="
if docker exec -e PGPASSWORD=primarypass dr-region-primary psql -U postgres -d appdb -tAqc \
    "INSERT INTO ledger(payload) VALUES ('should-never-succeed');" 2>&1; then
    echo "UNEXPECTED: the write succeeded — fencing failed."
else
    echo "Confirmed: Docker itself refused to touch the paused container — the write never even reached postgres."
fi

echo
echo "=== NOW promote the standby, safely, with no risk of a second writer ==="
docker exec dr-region-standby su postgres -c "pg_ctl promote -D /var/lib/postgresql/data" >/dev/null
until $PSQL_STANDBY -c "SELECT pg_is_in_recovery();" 2>/dev/null | grep -q '^f$'; do
    sleep 0.2
done
$PSQL_STANDBY -c "INSERT INTO ledger(payload) VALUES ('accepted-by-new-primary-fenced-run');" >/dev/null
echo "New primary accepted the write. Old primary is still fenced and never diverged."

echo
echo "=== Cleanup: unpause so the container can be removed, then tear down ==="
docker unpause dr-region-primary >/dev/null
docker network connect multi-region-failover-and-dr_dr-net dr-region-primary 2>/dev/null || true
docker compose down -v >/dev/null
echo "Done."
