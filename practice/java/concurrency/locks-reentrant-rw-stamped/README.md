# ReentrantLock, ReadWriteLock, and StampedLock (T-404) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md`](../../../../handbook/concurrency/reentrantlock-readwritelock-and-stampedlock.md)
(T-404). Three independent demos, each proving one real, measurable capability `synchronized`
alone cannot give you.

## Setup and run

```bash
cd practice/java/concurrency/locks-reentrant-rw-stamped
mkdir -p out
javac -d out src/*.java
java -cp out FairnessAndBargingDemo
java -cp out ReadWriteLockConcurrencyDemo
java -cp out StampedLockOptimisticReadDemo
```

No special JVM flags needed.

## Real observed output (last run)

### `FairnessAndBargingDemo` — real, measured fairness cost

```
== Unfair (default) ReentrantLock: acquisitions per thread ==
  thread-0: 8684 acquisitions
  thread-1: 6599 acquisitions
  thread-2: 10863 acquisitions
  thread-3: 13854 acquisitions
  spread (max - min): 7255 out of 10000 expected-even share

== Fair ReentrantLock(true): acquisitions per thread ==
  thread-0: 10607 acquisitions
  thread-1: 10134 acquisitions
  thread-2: 9690 acquisitions
  thread-3: 9569 acquisitions
  spread (max - min): 1038 out of 10000 expected-even share

== Real measured wall-clock time ==
Unfair: 6ms
Fair:   75ms
```

Four real threads race for the same `ReentrantLock` 40,000 total times. The **default (unfair)**
lock lets a thread that's already running re-acquire the lock ahead of threads that have been
queued longer — real "barging" — producing a genuinely lopsided distribution (spread of ~7,255 out
of an even 10,000-per-thread share). `ReentrantLock(true)` (**fair**) enforces a much more even,
real, roughly-FIFO distribution (spread of ~1,038) — at a real, measured throughput cost (~75ms vs
~6ms), since fairness requires actually queuing and waking threads in order rather than letting
whichever thread happens to be running grab the lock immediately.

### `ReadWriteLockConcurrencyDemo` — real, measured concurrent-read overlap

```
  reader-3 held read lock from t=2ms to t=155ms
  reader-2 held read lock from t=2ms to t=155ms
  reader-0 held read lock from t=2ms to t=155ms
  reader-1 held read lock from t=2ms to t=155ms
ReadWriteLock total elapsed: 163ms (readers overlapped)
  reader-0 held exclusive lock from t=1ms to t=156ms
  reader-1 held exclusive lock from t=156ms to t=310ms
  reader-2 held exclusive lock from t=310ms to t=465ms
  reader-3 held exclusive lock from t=465ms to t=620ms
Plain ReentrantLock total elapsed: 620ms (readers serialized)

== Real measured wall-clock time, 4 readers x 150ms hold each ==
ReadWriteLock (concurrent reads):     163ms
Plain ReentrantLock (serialized):     620ms
```

Four real threads each hold a lock for ~150ms. Under `ReentrantReadWriteLock`'s read lock, all four
real, printed intervals overlap almost exactly (`t=2ms` to `t=155ms` for every one) — genuine
concurrent holding, not an assumption — for a total real elapsed time of ~163ms. Under a plain
`ReentrantLock` (which has no concept of "read" vs "write"), the identical four threads are forced
into a real, visible queue — each interval starts exactly where the previous one ends — for a total
real elapsed time of ~620ms, essentially the sum.

### `StampedLockOptimisticReadDemo` — real invalidation and real throughput

```
== Part 1: optimistic read invalidated by a real concurrent write ==
  optimistic read (before validation): x=1 y=2
  validate(stamp) after a real concurrent write committed: false -- correctly detects the write
  fell back to a real read lock, re-read: x=100 y=200 (the real, current values)

== Part 2: real measured throughput, no contention ==
  20000000 iterations, no contention:
  tryOptimisticRead + validate: 31ms (sink=980000000)
  readLock/unlockRead:          89ms (sink=980000000)
```

Part 1 uses `CountDownLatch`-controlled ordering (not timing/sleep guesses) to deterministically
force a real writer thread to commit a real write between an optimistic read's stamp capture and
its validation. `validate(stamp)` correctly, really returns `false`, and the code falls back to a
real `readLock()` to get the current, correct values (`x=100 y=200`, not the stale `x=1 y=2`). Part
2 measures 20,000,000 real iterations with zero contention: the optimistic path (no lock ever taken,
just a stamp read and a cheap validation) measured roughly 3x faster than acquiring and releasing a
real read lock on every iteration.
