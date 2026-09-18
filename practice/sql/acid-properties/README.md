# ACID Properties and Triggers/Stored Procedures — Real Demos

Backs [`syllabus/06-databases/sql-and-relational-database-fundamentals.md`](../../../syllabus/06-databases/sql-and-relational-database-fundamentals.md) (T-2202) — the ACID and trigger/stored-procedure content this chapter previously lacked entirely.

Real PostgreSQL 16, run in disposable Docker containers.

## Reproduce

```bash
docker run --rm -d --name acidlab-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=acidlab postgres:16
sleep 3
docker cp acid-lab-part1.sql acidlab-pg:/tmp/part1.sql
docker exec -e PGPASSWORD=postgres acidlab-pg psql -U postgres -d acidlab -f /tmp/part1.sql

# Real container restart -- not just a new connection -- to genuinely test durability
docker restart acidlab-pg
sleep 4
docker cp acid-lab-part2.sql acidlab-pg:/tmp/part2.sql
docker exec -e PGPASSWORD=postgres acidlab-pg psql -U postgres -d acidlab -f /tmp/part2.sql

docker cp triggers-and-procedures-lab.sql acidlab-pg:/tmp/triggers.sql
docker exec -e PGPASSWORD=postgres acidlab-pg psql -U postgres -d acidlab -f /tmp/triggers.sql

docker stop acidlab-pg
```

Real output captured in [`acid-and-triggers-output.txt`](acid-and-triggers-output.txt).

## What each part proves

**Atomicity** (`acid-lab-part1.sql`, Section A) — a 3-insert transaction where
the third statement hits a real duplicate-key error rolls back *all three*,
not just the failed one: row count is 0 after `ROLLBACK`, even though the
first two inserts individually succeeded. The identical transaction with no
failure commits all its statements as one unit.

**Consistency** (Section B) — a `CHECK (balance >= 0)` constraint rejects a
transaction that would leave the balance negative — a real Postgres
constraint-violation error, and the row's real value is verified unchanged
after `ROLLBACK`.

**Isolation** (Section C) — the real default isolation level
(`read committed`), verified directly via `SHOW transaction_isolation`.
Deep-dive on what each isolation level actually prevents/allows lives in
[`isolation-levels-and-concurrency-anomalies.md`](../../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md)
— not duplicated here.

**Durability** (Section D, split across Part 1 and Part 2) — a row is
committed, the container is genuinely restarted (`docker restart`, not just
a new client connection to the same running server), and Part 2 confirms
all 3 committed rows, including the one committed just before the restart,
survived it. Real WAL/crash-recovery mechanics are covered in
[`replication-read-replicas-and-replica-lag.md`](../../../syllabus/06-databases/replication-read-replicas-and-replica-lag.md).

**Triggers and stored procedures** (`triggers-and-procedures-lab.sql`) — a
real PL/pgSQL trigger function fires automatically on every `UPDATE`, with
no application code calling it: it writes a real audit-log row and stamps
`updated_at`, verified directly. A second real proof: the same trigger
function's `RAISE EXCEPTION` for an invalid new balance aborts the *entire*
`UPDATE` statement — the balance is verified unchanged and no audit row was
written for the rejected attempt.

## `flyway-demo/` — real migration-tool mechanics

See [`flyway-demo/README.md`](flyway-demo/README.md) for the separate real
Flyway 10.20.1 demo (Java API, no Maven/Gradle), backing this same
chapter's migration-tooling content.
