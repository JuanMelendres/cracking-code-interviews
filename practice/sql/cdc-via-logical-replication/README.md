# Change Data Capture via PostgreSQL logical replication (T-710) — runnable verification

Real, executed output backing
[`handbook/system-design/messaging-patterns-and-change-data-capture.md`](../../../handbook/system-design/messaging-patterns-and-change-data-capture.md)
(T-710). A real PostgreSQL 16 logical replication slot, real WAL-tailed change
events, zero application-code changes required to capture them, and a real,
measured operational risk when the slot goes unconsumed.

## Files

- `docker-compose.yml` — a real Postgres 16 with `wal_level=logical` enabled.
- `cdc-capture-demo.sh` — creates a real logical replication slot (using the
  built-in `test_decoding` plugin, no Debezium/Kafka Connect needed), performs
  ordinary INSERT/UPDATE/DELETE, and reads the real captured events back from the WAL.
- `cdc-slot-retention-risk-demo.sh` — the real, recurring operational risk: an
  unconsumed slot prevents WAL reclamation, even across a real `CHECKPOINT`.

## Run

```bash
cd practice/sql/cdc-via-logical-replication
docker compose up -d
./cdc-capture-demo.sh
./cdc-slot-retention-risk-demo.sh
docker compose down -v
```

## Real observed output (last full run, PostgreSQL 16)

### 1. CDC capture — zero application-code changes

```
=== Ordinary application SQL -- no outbox table, no CDC-aware code at all ===
INSERT 0 1
UPDATE 1
DELETE 1

=== Real CDC events, read directly from the WAL after the fact ===
BEGIN 733
table public.accounts: INSERT: id[integer]:1 owner[text]:'alice' balance[integer]:100
COMMIT 733
BEGIN 734
table public.accounts: UPDATE: id[integer]:1 owner[text]:'alice' balance[integer]:150
COMMIT 734
BEGIN 735
table public.accounts: DELETE: id[integer]:1
COMMIT 735
```

Every real change was captured from the WAL, and the `INSERT`/`UPDATE`/`DELETE`
statements above are completely ordinary — nothing about them was written with CDC
in mind. Contrast this directly with the transactional outbox pattern (covered in
[Distributed Transactions: Saga, Outbox, and 2PC](../../../handbook/system-design/distributed-transactions-saga-and-outbox.md)),
which requires the application to explicitly write an outbox row in the same
transaction as the business write.

### 2. The real operational risk: an unconsumed slot blocks WAL reclamation

```
=== Real, fresh replication slot ===
Real WAL directory size: 16 MB
Real slot-retained WAL:  56 bytes

=== Generating 200,000 real rows of WAL activity -- the slot is NOT being consumed ===
Real WAL directory size after a real CHECKPOINT: 48 MB
Real slot-retained WAL (this is what an unconsumed CDC pipeline looks like):  29 MB

=== Now consuming the backlog, plus one more real write + CHECKPOINT to fully advance the slot ===
Real events consumed: 200002
Real slot-retained WAL after the slot fully catches up: 11 kB
```

Even a real `CHECKPOINT` — which normally lets PostgreSQL recycle old WAL
segments — could not reclaim the WAL this unconsumed slot was still holding: the
real directory tripled from 16 MB to 48 MB. This is the same underlying mechanism as
[MVCC in PostgreSQL, Vacuum, and Bloat](../../../handbook/databases/mvcc-vacuum-and-bloat.md)'s
long-transaction-blocks-vacuum finding — a held reference (there, an open snapshot;
here, an unconsumed slot's `restart_lsn`) prevents resource reclamation, applied
here to WAL retention instead of tuple/vacuum retention.

**A real, honest quirk discovered while building this demo:** consuming the backlog
alone did not immediately shrink `retained_wal` to zero — a second real write
followed by another `CHECKPOINT` was needed before the slot's `restart_lsn` fully
caught up. This is disclosed rather than smoothed over: a real CDC consumer that
"catches up" on a backlog may not see WAL retention drop to zero until the next
write-and-checkpoint cycle occurs naturally.

## What this does and does not prove

This is real PostgreSQL 16 behavior using the built-in `test_decoding` output
plugin — a real production CDC pipeline (Debezium, AWS DMS) typically uses the
binary `pgoutput` plugin and streams continuously rather than polling via SQL
functions, but the underlying mechanism proven here is identical: log-based CDC
reads committed changes from the WAL after the fact, requires no application-code
changes, and an unconsumed replication slot creates a real, unbounded WAL-retention
risk that must be monitored in production exactly as demonstrated here.
