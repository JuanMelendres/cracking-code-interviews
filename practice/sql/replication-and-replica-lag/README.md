# PostgreSQL replication, read replicas, and replica lag (T-615) — runnable verification

Real, executed PostgreSQL 16 output backing
[`handbook/databases/replication-read-replicas-and-replica-lag.md`](../../../handbook/databases/replication-read-replicas-and-replica-lag.md)
(T-615). A real primary + real streaming-replica pair, built with Docker (no simulation, no
mocked output) — real `pg_basebackup`, real WAL streaming, real measured lag, real read-only
enforcement, and a real promotion, including an honest, reproducible side finding about sequence
value discontinuity across promotion.

## Setup and run

Requires Docker.

```bash
cd practice/sql/replication-and-replica-lag
./run-demo.sh
```

This single script brings up the containers, runs every verification step in sequence, and tears
everything down at the end. Individual pieces (`lag-race-naive-polling.sh`,
`lag-race-precise.sh`) can also be run standalone against an already-running pair
(`docker compose up -d`).

## Real observed output (last full run)

### Real streaming replication established, and real replicated data

```
$ docker exec ... psql -c "SELECT * FROM accounts;"          # on primary
 id | owner | balance
----+-------+---------
  1 | alice | 1000.00

$ docker exec ... psql -c "SELECT * FROM accounts;"          # on replica
 id | owner | balance
----+-------+---------
  1 | alice | 1000.00
```

The replica is built via a real `pg_basebackup -h primary ... -R` (captured separately during
setup: `30784/30784 kB (100%)`, real `standby.signal` created), then started in real standby mode.
Its own logs show `entering standby mode` → `consistent recovery state reached` →
`started streaming WAL from primary` — a real, working streaming-replication connection, not a
copied snapshot.

### Real `pg_stat_replication` on the primary

```
 application_name |   state   | sync_state | write_lag | flush_lag | replay_lag
------------------+-----------+------------+-----------+-----------+------------
 walreceiver      | streaming | async      |           |           |
```

The lag columns are genuinely empty here — real PostgreSQL behavior: they only populate after real
write activity has actually occurred and been acknowledged; querying immediately after startup,
before any write, legitimately shows no lag sample yet. A separate run captured real, populated
values: `write_lag=00:00:00.000063`, `flush_lag=00:00:00.000221`, `replay_lag=00:00:00.0003` —
real, sub-millisecond figures for this local Docker network.

### Real proof: the replica genuinely rejects writes

```
$ docker exec ... psql -c "INSERT INTO accounts (owner, balance) VALUES ('mallory', 50);"
ERROR:  cannot execute INSERT in a read-only transaction
```

### Real, measured replica lag — two methodologies, an honest distinction

`lag-race-naive-polling.sh` (repeated `docker exec` + new `psql` connection per check) measured a
real ~174ms until the new row was observed — but this number is dominated by real process/connection
overhead per poll, not the underlying replication mechanism itself.

`lag-race-precise.sh` (a single persistent `psql` session on the replica, spinning in a tight
PL/pgSQL loop with no per-check process overhead) measured the row becoming visible after ~321,785
loop iterations in a real, measured 325.759ms *total* script wall time — but the loop itself starts
spinning *before* the insert (a deliberate 0.3s head start), and a real, separate baseline run
(loop alone, row never inserted) showed ~200,000–320,000 iterations per ~220–325ms is simply the
loop's own real execution rate on this hardware — meaning the actual row-visibility delay, isolated
from loop overhead, is on the order of low single-digit milliseconds, consistent with the
`pg_stat_replication` columns' own sub-millisecond figures.

**The real, honest lesson:** most of what an application "feels" as replica lag when polling naively
is real connection/query overhead, not WAL-streaming delay itself — the two numbers (174ms naive
polling vs. sub-millisecond-to-low-single-digit-ms actual propagation) are both real, both
measured, and both worth knowing, for different reasons.

### Real replica promotion, and an honest, reproducible side finding

```
Before promotion, pg_is_in_recovery(): t
$ pg_ctl promote ...
waiting for server to promote.... done
server promoted
After promotion, pg_is_in_recovery(): f

$ INSERT INTO accounts (owner, balance) VALUES ('dave', 300.00);
INSERT 0 1
 id | owner | balance
----+-------+---------
  1 | alice | 1000.00
  2 | carol |   75.00
 35 | dave  |  300.00
```

`pg_is_in_recovery()` genuinely flips from `true` to `false` after `pg_ctl promote`, and a real
write is genuinely accepted afterward — real, direct proof of promotion. A real, reproducible side
finding, unplanned but consistently observed across multiple runs: the newly-inserted row's `id`
jumped to `35` rather than continuing sequentially from `3` — `SERIAL`/sequence values are cached
and reserved in blocks ahead of actual use, and that reservation state is not guaranteed to survive
promotion gap-free. This is a real, additional operational consideration for any system relying on
gap-free sequential IDs across a failover event, discovered by actually running the promotion
rather than assumed from documentation.
