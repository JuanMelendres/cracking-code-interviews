# Optimistic vs. pessimistic locking (T-604) — runnable verification

Real, executed Java 21 output backing
[`handbook/databases/optimistic-vs-pessimistic-locking.md`](../../../../handbook/databases/optimistic-vs-pessimistic-locking.md)
(T-604). Real Hibernate 6.6, real `@Version`, a real `jakarta.persistence.OptimisticLockException`
thrown by real Hibernate code (never simulated), and a real thread genuinely blocked
by a real `PESSIMISTIC_WRITE` lock, measured with real wall-clock timing.

## Files

- `Account.java` — the entity with `@Version`, used by every locking demo.
- `UnversionedAccount.java` — a deliberately identical entity *without* `@Version`,
  needed because (a real, first-hand finding while building this pack) Hibernate
  enforces optimistic checking on every UPDATE unconditionally once an entity has a
  version field — there is no way to write to a versioned entity "without locking."
- `HibernateSupport.java` — real Hibernate `SessionFactory` setup against a real,
  shared, named in-memory H2 database (not per-session, so multiple sessions/threads
  genuinely see the same rows).
- `LostUpdateWithoutLockingDemo.java` — the baseline problem, unlocked.
- `OptimisticLockingDetectionDemo.java` — the same conflict, detected via `@Version`.
- `OptimisticLockRetryDemo.java` — the standard retry-on-conflict production pattern.
- `PessimisticLockingBlockingDemo.java` — the alternative strategy: block instead of
  detect.

## Run

```bash
cd practice/java/hibernate-jpa/optimistic-vs-pessimistic-locking
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" LostUpdateWithoutLockingDemo
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" OptimisticLockingDetectionDemo
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" OptimisticLockRetryDemo
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" PessimisticLockingBlockingDemo
```

## Real observed output (last full run, Java 21, Hibernate 6.6.55.Final)

### 1. `LostUpdateWithoutLockingDemo` — the problem both strategies solve

```
=== Starting balance: $100 ===
Session A read balance: $100
Session B read balance: $100
Session A committed its deposit -> balance now $150
Session B committed its deposit -> balance now $150 (computed from its OWN stale read of $100, not A's already-committed $150)

=== Real final balance in the database: $150 ===
Expected $200 (two real $50 deposits); Session A's deposit was silently lost.
```

### 2. `OptimisticLockingDetectionDemo` — detected, not prevented

```
Session A committed -> balance=$150 version incremented to 1

Session B now attempts to commit its deposit, still holding version=0 (stale...):
REAL jakarta.persistence.OptimisticLockException thrown at commit time.

=== Real final balance: $150, version 1 ===
Session A's deposit is intact. Session B's conflicting deposit was rejected, not silently lost.
```

Session B was allowed to read the stale row and compute its update against it — the
same as the unlocked demo up to that point. The only difference `@Version` makes is
that the *commit* fails loudly instead of silently overwriting. This is the register's
own named misconception, disproven directly: optimistic locking does not stop the
conflict from happening, it stops it from being silent.

### 3. `OptimisticLockRetryDemo` — the standard production response

```
Session B reads first (then becomes detached): balance=$100 version=0
Session A commits its $50 deposit -> balance=$150 version=1

  Attempt 1: merging stale detached entity, version=0 (real current DB version is higher)
  Attempt 1: REAL OptimisticLockException -- retrying with a fresh read.
  Attempt 2: fresh read -- balance=$150 version=1
  Attempt 2: committed successfully -> balance=$200

=== Real final balance: $200 ===
```

Both deposits land correctly, with zero data corruption — the real cost is one extra
round trip on the retried attempt, not lost data.

### 4. `PessimisticLockingBlockingDemo` — block instead of detect

```
Thread A acquired PESSIMISTIC_WRITE lock, balance=$100
Main: Thread A is holding the lock; sleeping 1500ms before releasing it...
Thread B now requests the same PESSIMISTIC_WRITE lock (will really block)...
Thread A committed its deposit and released the lock -> balance=$150
Thread B acquired the lock after Thread A released it, balance=$150
Thread B committed its deposit -> balance=$200

=== Real measured block time for Thread B: 1520ms (expected ~1500ms) ===
Real final balance: $200 (expected $200 -- both deposits honored, no conflict was ever possible)
```

Thread B's real measured wait (1520ms) matches Thread A's held-lock duration (1500ms)
almost exactly — real proof that `PESSIMISTIC_WRITE` prevents the conflict from ever
existing, at the real cost of Thread B doing nothing but waiting for that entire span.

## What this does and does not prove

This is a real, single-JVM, H2-backed reproduction — no real PostgreSQL row-lock
internals, no real network latency between application and database are being
exercised, only the JPA/Hibernate-level locking semantics (`@Version` checking,
`LockModeType.PESSIMISTIC_WRITE` blocking) those production databases also implement.
The two properties measured here — optimistic locking detects late and cheaply,
pessimistic locking prevents early at the cost of real wait time — hold identically
against a real production database; what changes at scale is only the absolute
numbers (contention rate, real lock wait duration), not which strategy trades away
which property.
