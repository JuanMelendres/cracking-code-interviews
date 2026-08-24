#!/bin/bash
# Real measurement, low overhead: insert on the primary, then have a single
# persistent psql session on the REPLICA spin in a tight PL/pgSQL loop
# checking for the new row -- isolating real WAL-replication propagation
# delay from repeated docker-exec/connection overhead per check.
docker exec -e PGPASSWORD=primarypass repl-primary psql -U postgres -d appdb -c \
  "DELETE FROM accounts WHERE owner='carol';" > /dev/null

(
docker exec repl-replica bash -c '
PGPASSWORD=primarypass psql -U postgres -d appdb -t -c "
DO \$\$
DECLARE
  start_time timestamptz := clock_timestamp();
  attempts int := 0;
  found_row boolean := false;
BEGIN
  WHILE NOT found_row AND attempts < 5000000 LOOP
    attempts := attempts + 1;
    SELECT EXISTS(SELECT 1 FROM accounts WHERE owner='"'"'carol'"'"') INTO found_row;
  END LOOP;
  RAISE NOTICE '"'"'real replica lag: row visible after % attempts, % elapsed'"'"', attempts, clock_timestamp() - start_time;
END \$\$;
"
'
) &
POLL_PID=$!

sleep 0.3
docker exec -e PGPASSWORD=primarypass repl-primary psql -U postgres -d appdb -c \
  "INSERT INTO accounts (owner, balance) VALUES ('carol', 75.00);" > /dev/null

wait $POLL_PID
