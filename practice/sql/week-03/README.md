# Week 3 PostgreSQL — Write Skew Demonstration

A real, reproduced write-skew anomaly at `REPEATABLE READ`, and its prevention at `SERIALIZABLE` — PostgreSQL 16 via Docker.

## The scenario

An `on_call` table with an invariant: **at least one doctor must remain on call at all times.** Two doctors, Alice and Bob, are both currently on call. Each independently decides to go off call, first checking that at least one other doctor is on call before doing so.

## Reproduce

```bash
docker run --rm -d --name week3-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week3 postgres:16
docker cp write-skew-setup.sql week3-pg:/tmp/setup.sql
docker exec -e PGPASSWORD=postgres week3-pg psql -U postgres -d week3 -f /tmp/setup.sql

# Generate and run two concurrent transactions at a given isolation level.
# write-skew-tx.sh emits a transaction script that reads the on-call count,
# sleeps 2s (forcing both transactions to overlap), then updates and commits.
./write-skew-tx.sh "REPEATABLE READ" "Alice" > rr-alice.sql
./write-skew-tx.sh "REPEATABLE READ" "Bob"   > rr-bob.sql
docker cp rr-alice.sql week3-pg:/tmp/rr-alice.sql
docker cp rr-bob.sql   week3-pg:/tmp/rr-bob.sql
docker exec -e PGPASSWORD=postgres week3-pg psql -U postgres -d week3 -f /tmp/rr-alice.sql &
docker exec -e PGPASSWORD=postgres week3-pg psql -U postgres -d week3 -f /tmp/rr-bob.sql &
wait

# Repeat with "SERIALIZABLE" instead of "REPEATABLE READ" (after re-running setup.sql).
```

## Real result — `REPEATABLE READ`: the anomaly occurs

Both transactions read `on_call_count = 2` (each sees the other doctor still on call, because neither has committed yet), so both proceed to go off call. **Both commit successfully.** Full output: `output/repeatable-read-alice.txt`, `output/repeatable-read-bob.txt`.

```
Final state: Alice = false, Bob = false
```

**The invariant is violated — zero doctors on call.** Neither transaction did anything individually wrong; `REPEATABLE READ` guarantees each transaction sees a consistent snapshot, but does not detect that the two transactions' combined effect breaks a multi-row invariant neither one alone could see. This is write skew, precisely.

## Real result — `SERIALIZABLE`: the anomaly is prevented

Same scenario, same code, only the isolation level changed:

```
Alice's transaction: ERROR:  could not serialize access due to read/write dependencies among transactions
DETAIL:  Reason code: Canceled on identification as a pivot, during commit attempt.
HINT:  The transaction might succeed if retried.

Bob's transaction: COMMIT (succeeds)

Final state: Alice = true, Bob = false
```

Full output: `output/serializable-alice.txt`, `output/serializable-bob.txt`. PostgreSQL's Serializable Snapshot Isolation (SSI) detected the dangerous read-write dependency structure between the two transactions and aborted one of them at commit time, forcing a retry. **The invariant survives — at least one doctor remains on call.**

## Files

| File | Purpose |
|---|---|
| `write-skew-setup.sql` | Creates and seeds the `on_call` table |
| `write-skew-tx.sh` | Generates a single transaction's SQL script, parameterized by isolation level and doctor name |
| `output/` | Real captured output from the last run of both isolation levels |
