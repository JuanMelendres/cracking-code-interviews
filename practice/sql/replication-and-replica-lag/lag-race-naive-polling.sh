#!/bin/bash
# Real measurement: write to primary, then poll the replica in a tight loop
# until the new row actually appears, timing real wall-clock propagation delay.
START=$(date +%s%N)
docker exec -e PGPASSWORD=primarypass repl-primary psql -U postgres -d appdb -c \
  "INSERT INTO accounts (owner, balance) VALUES ('bob', 250.00);" > /dev/null

ATTEMPTS=0
while true; do
  ATTEMPTS=$((ATTEMPTS+1))
  RESULT=$(docker exec -e PGPASSWORD=primarypass repl-replica psql -U postgres -d appdb -t -c \
    "SELECT count(*) FROM accounts WHERE owner='bob';")
  if [[ "$RESULT" =~ 1 ]]; then
    END=$(date +%s%N)
    ELAPSED_MS=$(( (END - START) / 1000000 ))
    echo "Real replica lag: row visible on replica after $ELAPSED_MS ms (real wall-clock), $ATTEMPTS poll attempt(s)"
    break
  fi
  if [ $ATTEMPTS -gt 500 ]; then
    echo "Row never appeared after 500 attempts (unexpected)"
    break
  fi
done
