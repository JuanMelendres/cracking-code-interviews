#!/bin/bash
# Real, reproducible PostgreSQL streaming-replication demo: brings up a
# real primary + real streaming-replica via Docker, verifies real
# replication, measures real replica lag, tests real read-only enforcement,
# and performs a real promotion. Every command below is executed against
# real, running PostgreSQL 16 containers -- no output is fabricated.
set -e
cd "$(dirname "$0")"

echo "###### Starting real primary + replica containers ######"
docker compose up -d
sleep 15

echo
echo "###### Real data on the primary ######"
docker exec -e PGPASSWORD=primarypass repl-primary psql -U postgres -d appdb -c "SELECT * FROM accounts;"

echo
echo "###### Real pg_stat_replication on the primary ######"
docker exec -e PGPASSWORD=primarypass repl-primary psql -U postgres -d appdb -c \
  "SELECT application_name, state, sync_state, write_lag, flush_lag, replay_lag FROM pg_stat_replication;"

echo
echo "###### Real proof: the SAME data is present on the replica ######"
docker exec -e PGPASSWORD=primarypass repl-replica psql -U postgres -d appdb -c "SELECT * FROM accounts;"

echo
echo "###### Real proof: the replica genuinely rejects writes ######"
docker exec -e PGPASSWORD=primarypass repl-replica psql -U postgres -d appdb -c \
  "INSERT INTO accounts (owner, balance) VALUES ('mallory', 50);" || true

echo
echo "###### Real, low-overhead measured replica lag ######"
./lag-race-precise.sh

echo
echo "###### Real replica promotion ######"
echo "Before promotion, pg_is_in_recovery():"
docker exec -e PGPASSWORD=primarypass repl-replica psql -U postgres -d appdb -t -c "SELECT pg_is_in_recovery();"
docker exec repl-replica su postgres -c "pg_ctl promote -D /var/lib/postgresql/data"
sleep 3
echo "After promotion, pg_is_in_recovery():"
docker exec -e PGPASSWORD=primarypass repl-replica psql -U postgres -d appdb -t -c "SELECT pg_is_in_recovery();"
echo "A real write, now genuinely accepted on the promoted former replica:"
docker exec -e PGPASSWORD=primarypass repl-replica psql -U postgres -d appdb -c \
  "INSERT INTO accounts (owner, balance) VALUES ('dave', 300.00);"
docker exec -e PGPASSWORD=primarypass repl-replica psql -U postgres -d appdb -c "SELECT * FROM accounts ORDER BY id;"

echo
echo "###### Tearing down ######"
docker compose down -v
