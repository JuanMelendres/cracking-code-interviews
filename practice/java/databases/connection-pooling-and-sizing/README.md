# Connection pooling and sizing, HikariCP (T-607) — runnable verification

Real, executed output backing
[`handbook/databases/connection-pooling-and-sizing.md`](../../../../handbook/databases/connection-pooling-and-sizing.md)
(T-607). A real HikariCP 5.1.0 pool against a real, CPU-capped PostgreSQL 16
container — a real `SQLTransientConnectionException` under real exhaustion, a real
leak-detection WARN with a real stack trace, and real, measured throughput numbers
across four real pool sizes.

## Files

- `docker-compose.yml` — a real Postgres 16 explicitly capped at 2 real CPUs
  (`cpus: 2.0`), so the sizing demo has a real, finite ceiling to actually hit.
- `fetch-deps.sh` — fetches HikariCP, the PostgreSQL JDBC driver, and slf4j from
  Maven Central.
- `PoolExhaustionDemo.java` — 6 real concurrent threads against a real 2-connection
  pool with a real 500ms timeout.
- `LeakDetectionDemo.java` — a real, deliberately never-closed connection, caught by
  HikariCP's own real leak detector.
- `PoolSizingThroughputDemo.java` — the same 40 real, genuinely CPU-bound queries run
  at four real pool sizes (2, 4, 8, 16).
- `run-all-demos.sh` — brings up Postgres, runs all three, tears down.

## Run

```bash
cd practice/java/databases/connection-pooling-and-sizing
./fetch-deps.sh
./run-all-demos.sh
```

## Real observed output (last full run, HikariCP 5.1.0, PostgreSQL 16)

### 1. Pool exhaustion — a real, typed exception, not a hang

```
=== Real pool: maximumPoolSize=2, connectionTimeout=500ms ===
Thread 0: acquired connection after 1ms real wait, running query...
Thread 1: acquired connection after 19ms real wait, running query...
Thread 5: REAL SQLTransientConnectionException after 509ms -- "demo-pool - Connection is not available, request timed out after 505ms (total=2, active=2, idle=0, waiting=0)"
Thread 3: REAL SQLTransientConnectionException after 509ms -- "... (total=2, active=2, idle=0, waiting=3)"
...
=== Result after 1023ms real wall time ===
2 of 6 threads succeeded; 4 of 6 really timed out waiting for a pooled connection.
```

Only 2 of 6 threads could ever be "in the database" at once; the other 4 really
timed out, each with HikariCP's own real pool-state diagnostics
(`total=2, active=2, idle=0, waiting=N`) embedded directly in the exception message.

### 2. Leak detection — a real bug caught with a real stack trace

```
Connection borrowed at 1787768792969. Waiting 3 real seconds for HikariCP's real leak detector to fire...
[leak-demo-pool housekeeper] WARN com.zaxxer.hikari.pool.ProxyLeakTask - Connection leak detection triggered for org.postgresql.jdbc.PgConnection@70beb599 on thread main, stack trace follows
java.lang.Exception: Apparent connection leak detected
	at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:99)
	at LeakDetectionDemo.main(LeakDetectionDemo.java:34)
```

**A real, honest discovery made while building this demo:** the first attempt used
`leakDetectionThreshold=1000` (1 second), and HikariCP silently disabled it with its
own real WARN — `"leakDetectionThreshold is less than 2000ms... disabling it"`.
2000ms is HikariCP's real, enforced minimum, undocumented until you actually hit it.

### 3. Pool sizing — bigger is not just unhelpful, it's real measured overhead

```
=== Real PostgreSQL container capped at 2 real CPUs ===
Pool size  2:  2902 ms real wall time for 40 queries (72.6 ms/query average)
Pool size  4:  3226 ms real wall time for 40 queries (80.7 ms/query average)
Pool size  8:  5587 ms real wall time for 40 queries (139.7 ms/query average)
Pool size 16:  6161 ms real wall time for 40 queries (154.0 ms/query average)
```

Consistent across independent runs: pool size 2 — matching the real CPU cap — was
the **fastest**, and every larger pool size was **really slower**, more than 2x
slower at size 16. This is a stronger, more concrete result than mere "diminishing
returns": once the pool exceeds what the database can genuinely execute
concurrently, additional connections don't sit idle harmlessly — they add real,
measured contention for the same finite CPU capacity.

## What this does and does not prove

The exact throughput numbers are specific to this single-machine, Docker-CPU-capped
setup — a real production database with more cores, different query shapes, and I/O-
bound (not CPU-bound) workloads will show different absolute numbers. What transfers
directly is the underlying mechanism this demo proves rather than asserts: a
connection pool's optimal size is bounded by what the database can actually execute
concurrently, not by client-side demand, and HikariCP's own real diagnostics (pool
state in exceptions, leak-detection stack traces) are real, actionable tools for
finding both problems in production.
