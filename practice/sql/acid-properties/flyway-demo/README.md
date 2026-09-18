# Flyway Migration Tooling — Real Demo

Backs [`syllabus/06-databases/zero-downtime-schema-migration.md`](../../../../syllabus/06-databases/zero-downtime-schema-migration.md) — that chapter covers safe migration *strategy* (expand-contract, `CONCURRENTLY`) in real depth, but never named an actual migration tool. This demo backs the tooling-mechanics gap specifically.

Real Flyway 10.20.1 (Community Edition, Java API — no Maven/Gradle, no Flyway CLI install), real PostgreSQL 16 in a disposable, port-mapped Docker container, real `postgresql` JDBC driver 42.7.4. OpenJDK 21.0.12.

## Run it

```bash
./fetch-deps.sh
docker run --rm -d --name flywaylab-pg -p 5433:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=flywaylab postgres:16
mkdir -p out
javac -cp "lib/*" -d out src/FlywayDemo.java
java -cp "out:lib/*" FlywayDemo
docker stop flywaylab-pg
```

Real output captured in [`flyway-demo-output.txt`](flyway-demo-output.txt).

## What it proves

- **A real `flyway_schema_history` table** — not a description of one.
  After applying `V1__create_accounts.sql` and `V2__add_email_column.sql`,
  the table shows two real rows, each with a real, computed checksum and
  `success = true`.
- **Flyway tracks what's already applied and genuinely skips it** — running
  `migrate()` a second time with no new migration files executes 0
  migrations, verified directly (`result.migrationsExecuted == 0`).
- **A real checksum-mismatch failure** — editing `V1__create_accounts.sql`'s
  content *after* it was already applied (simulating an unauthorized,
  after-the-fact edit to a migration file already run in production) makes
  the next `migrate()` call throw a real `FlywayException`: "Migration
  checksum mismatch for migration version 1," showing the real stored
  checksum versus the real newly-computed one. This is the actual
  mechanism that catches "someone edited an already-shipped migration
  file" before it can silently drift from what other environments already
  applied.
