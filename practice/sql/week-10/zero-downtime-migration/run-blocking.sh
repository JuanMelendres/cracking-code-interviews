#!/bin/bash
set -e
CONTAINER=week10-pg
DB=week10

docker exec -e PGPASSWORD=postgres $CONTAINER psql -U postgres -d $DB -c "DROP INDEX IF EXISTS idx_big_table_val;"

echo "Starting blocking CREATE INDEX in the background..."
docker exec -e PGPASSWORD=postgres $CONTAINER psql -U postgres -d $DB -c \
  "CREATE INDEX idx_big_table_val ON big_table (val);" &
CREATE_PID=$!

sleep 0.3 # let the CREATE INDEX actually acquire its lock first

echo "Attempting a concurrent INSERT while the index build is in flight..."
INSERT_START=$(python3 -c 'import time; print(int(time.time()*1000))')
docker exec -e PGPASSWORD=postgres $CONTAINER psql -U postgres -d $DB -c \
  "INSERT INTO big_table (val) VALUES ('concurrent-insert-blocking-test');"
INSERT_END=$(python3 -c 'import time; print(int(time.time()*1000))')

wait $CREATE_PID
echo "RESULT: concurrent INSERT took $((INSERT_END - INSERT_START))ms while a plain CREATE INDEX was running"
