# Week 3 Java — Spring Transaction Demos — runnable verification

Five real Spring Framework 6.1.14 + JDBC demonstrations, run against real H2 (in-memory) and, for two of them, real PostgreSQL 16. No Spring Boot, no Maven/Gradle — dependencies are plain jars fetched directly from Maven Central and put on the classpath by hand, which is all `@Transactional`, AOP proxying, and `DataSourceTransactionManager` actually need underneath the Spring Boot convenience layer.

## Setup

```bash
cd practice/java/week-03/spring-demos
./fetch-deps.sh          # downloads ~10 jars into lib/ (gitignored)
mkdir -p out
javac -cp "lib/*" -d out src/*.java
```

`ReadOnlyPostgresDemo` additionally needs a running PostgreSQL instance:

```bash
docker run --rm -d --name week3-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week3 -p 55432:5432 postgres:16
```

## Run each demo

```bash
java -cp "out:lib/*" SelfInvocationDemo
java -cp "out:lib/*" CheckedExceptionRollbackDemo
java -cp "out:lib/*" RequiresNewDemo
java -cp "out:lib/*" ReadOnlyDemo
java -cp "out:lib/*" ReadOnlyPostgresDemo   # requires the Postgres container above
java -cp "out:lib/*" PoolExhaustionDemo
```

## Real output (last run of each)

### 1. Self-invocation bypasses the transactional proxy

```
Called through the Spring-managed proxy:      isActualTransactionActive() = true
Called via self-invocation (this.method()):   isActualTransactionActive() = false
RESULT: CONFIRMED -- self-invocation bypasses the transactional proxy.
```

### 2. Checked exception does NOT roll back by default

```
Default rollback rule, checked exception thrown -> row count: 1 (row survived the exception: true)
rollbackFor=Exception.class, checked exception thrown -> row count: 1 (row 2 correctly rolled back: true)
RESULT: CONFIRMED -- default rule does NOT roll back on a checked exception; rollbackFor fixes it.
```

### 3. REQUIRES_NEW commits independently of the outer transaction

```
orders table row count (outer transaction, should be rolled back): 0
audit_log table row count (REQUIRES_NEW, should have survived): 1
RESULT: CONFIRMED -- REQUIRES_NEW committed independently despite the outer rollback.
```

### 4. `readOnly=true` on H2 — a hint, not enforced

```
Write inside @Transactional(readOnly=true) SUCCEEDED (no exception).
On this driver (H2), readOnly=true set connection.setReadOnly(true) but H2 did not reject the write.
RESULT: readOnly is a HINT here, not an enforced constraint -- driver-dependent behavior, exactly as documented.
```

### 5. `readOnly=true` on PostgreSQL — actually enforced

```
Write inside @Transactional(readOnly=true) FAILED on PostgreSQL:
  UncategorizedSQLException: ERROR: cannot execute INSERT in a read-only transaction
RESULT: CONFIRMED -- PostgreSQL's JDBC driver enforces connection.setReadOnly(true) by rejecting the write at the database level.
```

**Demos 4 and 5 are the same Java code and the same Spring annotation, run against two different databases, producing genuinely different real results** — this is the whole point: `readOnly` enforcement is driver-dependent, not a universal Spring guarantee.

### 6. Connection-pool exhaustion from long-held connections

```
Pool size = 2, connectionTimeout = 2000ms
Starting 2 long-running transactions (each holding a connection for 6000ms), then one more request competing for a connection...

Third request FAILED after 2010ms waiting for a connection: CannotCreateTransactionException

Third request waited 2010ms before failing (configured timeout: 2000ms).
RESULT: CONFIRMED -- pool exhaustion under a small pool size with long-held connections causes a real connection-acquisition timeout for a completely unrelated, fast request.
```
