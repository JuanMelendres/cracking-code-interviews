#!/bin/bash
set -e

export PGPASSWORD=replicatorpass
DATA_DIR=/var/lib/postgresql/data

echo "Waiting for region-primary to accept connections..."
until pg_isready -h region-primary -p 5432 -d postgres -U postgres 2>/dev/null; do
    sleep 1
done
sleep 3

rm -rf "$DATA_DIR"/* "$DATA_DIR"/.[!.]* 2>/dev/null || true
chown postgres:postgres "$DATA_DIR"
chmod 700 "$DATA_DIR"

echo "Running real pg_basebackup from region-primary..."
su postgres -c "pg_basebackup -h region-primary -p 5432 -U replicator -D $DATA_DIR -Fp -Xs -P -R"

echo "Real pg_basebackup complete. standby.signal present: $(test -f $DATA_DIR/standby.signal && echo yes || echo no)"

exec su postgres -c "postgres -D $DATA_DIR -c hot_standby=on"
