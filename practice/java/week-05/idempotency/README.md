# Week 5 Java — Idempotency-Key Mechanism — runnable verification

A real idempotency-key implementation against real PostgreSQL 16: storage (a `UNIQUE` key column), concurrent-duplicate behavior, and TTL-based recovery from a crashed in-progress attempt.

## Setup

```bash
cd practice/java/week-05/idempotency
./fetch-deps.sh
docker run --rm -d --name week5-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week5 -p 55433:5432 postgres:16
mkdir -p out
javac -cp "lib/*" -d out src/*.java
java -cp "out:lib/*" IdempotencyDemo
docker stop week5-pg
```

## Real output (last run)

```
=== Part 1: two concurrent requests, SAME idempotency key ===
Request A result: charged $50.00, confirmation #49940811261291
Request B result: charged $50.00, confirmation #49940811261291
Actual charges performed: 1 (must be exactly 1)
Both requests returned the same result: true

=== Part 2: TTL recovery from a crashed in-progress attempt ===
A stale IN_PROGRESS row (age 10s, TTL 5s) exists for key 'charge-key-crashed'.
New request with the same key result: charged $75.00, confirmation #49941065026041
RESULT: the stale IN_PROGRESS row did not block a fresh attempt -- TTL recovery worked.
```

## How it works

1. **Storage:** an `idempotency_keys` table with `key TEXT PRIMARY KEY` — the database's own unique constraint, not application-level locking, is what guarantees exactly one winner among concurrent attempts to use the same key.
2. **Concurrent-duplicate behavior:** the "losing" concurrent request catches the unique-violation, polls the row briefly, and returns the *original* stored result once the winner completes — it never re-executes the operation.
3. **TTL:** an `IN_PROGRESS` row older than the configured TTL (simulating a crashed process that started work but never finished it) is deleted and the key is reclaimed for a fresh attempt, rather than blocking forever.
