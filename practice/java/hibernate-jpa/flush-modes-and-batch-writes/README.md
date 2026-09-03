# Hibernate Flush Modes and Batch Writes (T-606) — runnable verification

Real, executed Hibernate 6.6.55.Final output backing
[`syllabus/06-databases/hibernate-flush-modes-and-batch-writes.md`](../../../../syllabus/06-databases/hibernate-flush-modes-and-batch-writes.md)
(T-606). Real H2, real JDBC-level call counting via a dynamic proxy — no
mocked SQL, no assumed batching behavior.

## Files

- `SequenceWidget.java` / `IdentityWidget.java` — identical entities except
  for `@GeneratedValue` strategy (`SEQUENCE` vs `IDENTITY`).
- `CountingConnectionProvider.java` / `BatchCallCounter.java` — a real
  Hibernate `ConnectionProvider` wrapping every `PreparedStatement` in a JDK
  dynamic proxy, counting real `executeBatch()` and `executeUpdate()` calls.
  This is the mechanism used to *prove* batching behavior rather than infer
  it from timing or documentation.
- `BatchInsertDemo.java` — inserts 40 rows with `hibernate.jdbc.batch_size=10`
  for both entity types, reporting the real call counts.
- `FlushModeDemo.java` — renames an entity in-session (unflushed), then runs
  a query for the new name under `FlushMode.AUTO` and `FlushMode.COMMIT`.

## Run

```bash
cd practice/java/hibernate-jpa/flush-modes-and-batch-writes
sh fetch-deps.sh
mkdir -p out
CP=$(ls lib/*.jar | tr '\n' ':')
javac -cp "$CP" -d out src/*.java
java -cp "out:$CP" BatchInsertDemo
java -cp "out:$CP" FlushModeDemo
```

## Real observed output (last full run)

### 1. `BatchInsertDemo` — `hibernate.jdbc.batch_size` is silently ignored for `IDENTITY`

```
=== SEQUENCE-generated entity, hibernate.jdbc.batch_size=10 ===
Rows inserted:            40
Real executeBatch() calls: 5 (rows sent via batching: 40)
Real executeUpdate() calls: 0 (rows sent one at a time)

=== IDENTITY-generated entity, identical hibernate.jdbc.batch_size=10 ===
Rows inserted:            40
Real executeBatch() calls: 0 (rows sent via batching: 0)
Real executeUpdate() calls: 40 (rows sent one at a time)
```

With `GenerationType.SEQUENCE`, Hibernate can pre-allocate identifier values
without a round trip per row, so it genuinely batches inserts into 5 real
`executeBatch()` calls covering all 40 rows. With the *identical*
`hibernate.jdbc.batch_size=10` setting, `GenerationType.IDENTITY` produces
zero `executeBatch()` calls and exactly 40 individual `executeUpdate()`
calls — one per row — because Hibernate must execute each insert
individually to retrieve the database-assigned identity value immediately,
for the persistence context to have a usable primary key. The batch-size
setting is not an error, not ignored due to misconfiguration — it is
structurally inapplicable to `IDENTITY` generation, confirmed here by real,
counted JDBC calls rather than assumed from documentation. Hibernate's own
built-in `Statistics` output corroborates this independently: "5 JDBC
batches" for the `SEQUENCE` run, "0 JDBC batches" for the `IDENTITY` run.

### 2. `FlushModeDemo` — a query can miss its own transaction's own pending change

```
=== FlushMode.AUTO (default): query sees the pending, unflushed change ===
Flush mode:                     AUTO
Query for the new name found:   1 row(s)
Real result: the query found the renamed row -- Hibernate auto-flushed the pending change before running the query.

=== FlushMode.COMMIT: query does NOT see the pending, unflushed change ===
Flush mode:                     COMMIT
Query for the new name found:   0 row(s)
Real result: the query missed its own transaction's own pending rename -- FlushMode.COMMIT genuinely does not auto-flush before a query.
```

Under the default `FlushMode.AUTO`, Hibernate detects that the upcoming
query could be affected by pending changes in the persistence context and
auto-flushes before executing it — the query correctly finds the renamed
row. Under `FlushMode.COMMIT`, no such auto-flush happens, and the same
query against the same in-session change genuinely returns zero rows: the
database still holds the old value at query time, and the query only sees
what's actually committed (or flushed) to it. Neither result is a bug —
both are the documented, real behavior of each mode — but a team relying on
`FlushMode.AUTO`'s convenience without understanding it can be surprised
the first time `FlushMode.COMMIT` is chosen for a performance reason
elsewhere in the same codebase.

## Real discoveries made while building this pack

No bugs were hit while building the demos themselves — both produced
correct, real output on the first run. The one real design decision worth
noting: measuring "was this batched" required building a real JDK dynamic
proxy around Hibernate's JDBC connection (`CountingConnectionProvider`)
rather than relying on H2's own trace logging, which was tried first and
found to log each bound statement identically whether it was sent via
`executeBatch()` or individually via `executeUpdate()` — the trace output
could not distinguish the two cases. The dynamic-proxy approach counts the
actual JDBC method invocations directly, which is the only fully reliable
way to prove batching occurred.
