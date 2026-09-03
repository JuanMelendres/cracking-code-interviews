# Strangler Fig, Anti-Corruption Layer, and migration patterns (T-912) — runnable verification

Real, executed Java 21 output backing
[`syllabus/17-architecture/strangler-fig-and-migration-patterns.md`](../../../../syllabus/17-architecture/strangler-fig-and-migration-patterns.md)
(T-912). No described-but-untested claim about migration safety — a real router with
real, independent stores, a real rollback that either really loses data or really
doesn't, and a real percentage-based facade whose observed traffic split is measured,
not assumed.

## Files

- `Order.java`, `OrderStore.java` — a minimal domain object and a real, independent
  key-value store standing in for either the legacy or the new system.
- `MigrationRouter.java` — the real dual-write and read-cutover logic: `write()`
  always writes to the new system and conditionally to legacy; `read()` serves from
  whichever system is currently the read target.
- `RollbackSafetyDemo.java` — the central demo: two real scenarios showing when a
  mid-migration rollback loses data and when it doesn't.
- `StranglerFacade.java`, `IncrementalCutoverDemo.java` — a real, deterministic
  percentage-based routing facade and proof that it actually controls the observed
  traffic split.

## Run

```bash
cd practice/java/architecture/strangler-fig-and-migration-patterns
mkdir -p out
javac -d out *.java
java -cp out RollbackSafetyDemo
java -cp out IncrementalCutoverDemo
```

## Real observed output (last full run, Java 21)

### 1. `RollbackSafetyDemo` — the register's own question, answered with evidence

The register's follow-up question is "how do you roll back mid-migration?" Both
scenarios below write orders 1-3 before cutover, cut reads over to the new system,
then write orders 4-6, discover a bug, and roll reads back to legacy:

```
=== Scenario A: UNSAFE -- dual-write disabled immediately at cutover ===
[Router] Disabling dual-write to legacy (declaring migration complete)
...
Reading all 6 orders now that reads point back at legacy:
  order-4: MISSING FROM LEGACY -- real data loss on rollback
  order-5: MISSING FROM LEGACY -- real data loss on rollback
  order-6: MISSING FROM LEGACY -- real data loss on rollback
Result: 3 of 6 orders unrecoverable after rollback  <-- UNSAFE rollback, real data loss

=== Scenario B: SAFE -- dual-write kept running through the rollback window ===
[Router] Dual-write to legacy deliberately left ON through the rollback window
...
Reading all 6 orders now that reads point back at legacy:
  order-4: Order{order-4, Dave}
  order-5: Order{order-5, Erin}
  order-6: Order{order-6, Frank}
Result: 0 of 6 orders unrecoverable after rollback  <-- SAFE rollback, zero data loss
```

The only difference between the two scenarios is one boolean flag — whether
dual-write to legacy stayed on through the rollback window. Everything else (the
cutover, the bug, the rollback trigger) is identical. This is the real, concrete,
measured answer: a mid-migration rollback is only safe for as long as the system
you're rolling back to has actually kept receiving every write — the moment dual-write
is turned off, rollback silently stops being a real safety net.

### 2. `IncrementalCutoverDemo` — extraction is gradual, not a switch

```
Configured new-system percentage:   0%  ->  real observed split: new=0 (0.0%) legacy=1000 (100.0%)
Configured new-system percentage:  25%  ->  real observed split: new=251 (25.1%) legacy=749 (74.9%)
Configured new-system percentage: 100%  ->  real observed split: new=1000 (100.0%) legacy=0 (0.0%)
```

The facade's routing percentage really controls the real observed traffic split
(25% configured, 25.1% observed across 1,000 real requests) — proof that Strangler
Fig extraction is a real, continuously-adjustable dial, not an all-or-nothing rewrite
event.

## What this does and does not prove

Both demos are real, single-JVM, in-memory reproductions — no real legacy database,
no real network calls, no real feature-flag service is being exercised, only the
underlying logic those real systems also implement. A production migration adds real
operational concerns this demo doesn't touch (schema drift between the two stores
during the dual-write window, real replication lag, a real feature-flag rollout
mechanism) — but the two properties measured here are exactly the properties that
transfer directly: rollback safety is bounded by how long dual-write stays active past
cutover, and incremental extraction is a real, adjustable percentage, not a
one-time event.
