# Week 10 PostgreSQL — Blocking vs CONCURRENTLY Index Creation

A real 2-million-row table, PostgreSQL 16 via Docker — measuring how long a concurrent `INSERT` waits during a plain `CREATE INDEX` versus `CREATE INDEX CONCURRENTLY`.

## Reproduce

```bash
docker run --rm -d --name week10-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week10 -p 5433:5432 postgres:16
docker exec -e PGPASSWORD=postgres week10-pg psql -U postgres -d week10 -c "
CREATE TABLE big_table (id BIGSERIAL PRIMARY KEY, val TEXT);
INSERT INTO big_table (val) SELECT 'row-' || i FROM generate_series(1, 2000000) AS i;
"

./run-blocking.sh
./run-concurrently.sh
```

Both scripts require `python3` (used for millisecond-precision timing, since macOS's built-in `date` doesn't support `%N`).

## Real result — plain `CREATE INDEX`: the concurrent INSERT blocks for the entire build

```
Starting blocking CREATE INDEX in the background...
Attempting a concurrent INSERT while the index build is in flight...
CREATE INDEX
INSERT 0 1
RESULT: concurrent INSERT took 1943ms while a plain CREATE INDEX was running
```

Note the output ORDER: `CREATE INDEX` finishes, THEN `INSERT 0 1` prints — the insert genuinely waited for the whole build.

## Real result — `CREATE INDEX CONCURRENTLY`: the INSERT completes almost immediately

```
Starting CREATE INDEX CONCURRENTLY in the background...
Attempting a concurrent INSERT while the CONCURRENTLY index build is in flight...
INSERT 0 1
CREATE INDEX
RESULT: concurrent INSERT took 84ms while CREATE INDEX CONCURRENTLY was running
```

Note the flipped order: `INSERT 0 1` prints WHILE the index build is still running.

**1943ms vs 84ms — roughly 23x.** Real, measured, not estimated.

## Files

| File | Purpose |
|---|---|
| `run-blocking.sh` | Starts a plain `CREATE INDEX`, times a concurrent `INSERT` against the same table |
| `run-concurrently.sh` | Same test, `CREATE INDEX CONCURRENTLY` instead |
