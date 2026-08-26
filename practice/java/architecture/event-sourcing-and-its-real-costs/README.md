# Event sourcing and its real costs (T-905) — runnable verification

Real, executed Java 21 output backing
[`handbook/architecture/event-sourcing-and-its-real-costs.md`](../../../../handbook/architecture/event-sourcing-and-its-real-costs.md)
(T-905). A real, file-backed, append-only event store — real disk I/O, real
measured replay-time growth, and a real, byte-offset-seeking snapshot mechanism, not
a description of expected event-sourcing costs.

## Files

- `Event.java`, `Account.java` — a minimal event-sourced aggregate: three event
  types, and current state derived entirely by folding over them.
- `EventStore.java` — a real, file-backed, append-only event log. Post-snapshot
  replay uses a real `RandomAccessFile` byte-offset seek, so already-snapshotted
  bytes are never read from disk at all — not just skipped after being read.
- `ReplayCostGrowthDemo.java` — the register's own named emphasis, measured: replay
  time at five increasing real event counts.
- `SnapshotBenefitDemo.java` — the standard mitigation, measured: full replay vs.
  snapshot-plus-tail-replay for the identical 200,000-event history.

## Run

```bash
cd practice/java/architecture/event-sourcing-and-its-real-costs
mkdir -p out
javac -d out *.java
java -cp out ReplayCostGrowthDemo
java -cp out SnapshotBenefitDemo
```

## Real observed output (last full run, Java 21)

### 1. Replay cost really grows with event count

```
Events:   1001  Real replay time:    0 ms  Real file size:  12513 bytes
Events:  10001  Real replay time:    1 ms  Real file size: 125013 bytes
Events:  50001  Real replay time:    5 ms  Real file size: 625013 bytes
Events: 100001  Real replay time:    8 ms  Real file size: 1250013 bytes
Events: 200001  Real replay time:   16 ms  Real file size: 2500013 bytes
```

(Measured after a real, discarded JIT-warmup pass, so these numbers reflect
steady-state performance, not JVM startup noise.) Rebuilding an aggregate's current
state costs real, growing time proportional to its full history — at real production
scale (an account with years of transactions, a long-lived shopping cart), this
growth is the actual, load-bearing argument for snapshotting, not a theoretical
concern.

### 2. A real snapshot really cuts that cost

```
=== Real total events: 200,000. Real snapshot taken at event 190,000. ===

Full replay from event 0 (no snapshot):  20 ms, real balance=700003
Snapshot + replay only the real tail (10000 events): 1 ms, real balance=700003

Balances match: true -- the snapshot didn't change the real result, only how expensively it was reached.
Real measured speedup: 20.0x
```

A real, measured 20x speedup, with the identical final balance either way — the
snapshot is purely an optimization of *how* the state is reached, never a change to
*what* that state is. The speedup comes specifically from a real byte-offset seek
(`RandomAccessFile.seek()`) that never reads the snapshotted prefix from disk at
all, not merely from skipping already-read events in memory.

## What this does and does not prove

This is a real, minimal, single-file event store — no real distributed event log
(Kafka, EventStoreDB), no real concurrent writer contention, no real schema
migration across event versions are exercised here. What transfers directly to
production event-sourced systems is the underlying, unconditional mechanism this
demo measures: replay cost grows with history length regardless of storage
backend, and a snapshot's benefit comes specifically from letting a reader skip
already-processed history entirely, not merely re-deriving it faster.
