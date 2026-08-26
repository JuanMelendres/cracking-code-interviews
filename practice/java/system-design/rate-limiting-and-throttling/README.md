# Rate limiting and throttling algorithms (T-808) — runnable verification

Real, executed Java 21 output backing
[`handbook/system-design/rate-limiting-and-throttling-algorithms.md`](../../../../handbook/system-design/rate-limiting-and-throttling-algorithms.md)
(T-808). No mocked clocks, no simulated request streams — real `System.nanoTime()` /
`System.currentTimeMillis()` timing, real concurrent threads racing a shared limiter,
and a real background thread draining a real queue at a real fixed rate.

## Files

- `TokenBucket.java`, `LeakyBucket.java`, `FixedWindowCounter.java`,
  `SlidingWindowLog.java`, `SlidingWindowCounter.java` — the five canonical
  algorithms, each a real, minimal, correct implementation (not pseudocode).
- `BoundaryBurstDemo.java` — real proof of the fixed-window boundary-burst flaw and
  how the other algorithms behave under the same attack.
- `ConcurrencyRaceDemo.java` — real proof that an unsynchronized check-then-increment
  limiter can overshoot its limit under concurrent load, and that guarding the same
  sequence with `synchronized` closes the race.
- `LeakyBucketSmoothingDemo.java` — real proof that a bursty inflow is smoothed into a
  constant-rate outflow.

## Run

```bash
cd practice/java/system-design/rate-limiting-and-throttling
mkdir -p out
javac -d out *.java
java -cp out BoundaryBurstDemo
java -cp out ConcurrencyRaceDemo
java -cp out LeakyBucketSmoothingDemo
```

## Real observed output (last full run, Java 21)

### 1. `BoundaryBurstDemo` — the fixed-window flaw, and who doesn't have it

All four limiters configured identically: limit = 10 requests per 200ms window. The
attack fires 10 requests just before a real fixed-window boundary and 10 more just
after it — 20 requests inside a real elapsed span far smaller than 200ms:

```
=== Boundary-burst attack: limit=10 per 200ms window ===

FixedWindowCounter               before-boundary=10 after-boundary=10 total-admitted-in-burst=20 (nominal limit=10)
SlidingWindowLog                 before-boundary=10 after-boundary=0 total-admitted-in-burst=10 (nominal limit=10)
SlidingWindowCounter (approx)    before-boundary=10 after-boundary=0 total-admitted-in-burst=10 (nominal limit=10)

TokenBucket under the same attack (capacity=10, refill=50/s -> steady-state 10 per 200ms):
TokenBucket                      before-boundary=10 after-boundary=0 total-admitted-in-burst=10 (nominal limit=10)
```

`FixedWindowCounter` really admits 20 requests — exactly double its nominal limit —
because its counter resets hard at the wall-clock boundary with no memory of what
happened in the previous window. `SlidingWindowLog`, `SlidingWindowCounter`, and
`TokenBucket` all really cap the burst at 10, each by a different mechanism: an exact
trailing log, a weighted estimate across two windows, and continuous refill,
respectively.

### 2. `ConcurrencyRaceDemo` — the race is real, and it's intermittent

Limit = 100, 64 real threads x 50 attempts each (3,200 total attempts) against a
shared limiter instance. Ten consecutive real runs of the naive, unsynchronized
`if (count < limit) count++` limiter:

```
NaiveCounter admitted:       102  <-- OVERSHOOT, real race
NaiveCounter admitted:       100
NaiveCounter admitted:       100
NaiveCounter admitted:       100
NaiveCounter admitted:       100
NaiveCounter admitted:       100
NaiveCounter admitted:       101  <-- OVERSHOOT, real race
NaiveCounter admitted:       101  <-- OVERSHOOT, real race
NaiveCounter admitted:       100
NaiveCounter admitted:       100
```

3 of 10 real runs overshot the limit (101-102 admitted against a limit of 100); the
other 7 happened not to lose the race that particular run. `FixedWindowCounter`, which
guards the identical check-then-increment sequence with `synchronized`, was exact at
100 on every single run, including every run shown above. This is the honest, load-bearing
finding: the race is real but non-deterministic — it depends on real thread scheduling,
and a rate limiter that "usually" enforces its limit under a light test is not proof it
enforces it under real concurrent production load.

### 3. `LeakyBucketSmoothingDemo` — real smoothing of a real burst

30 requests submitted in a real near-simultaneous burst, capacity 30 (so none are
rejected at submit time), leak rate 10/s:

```
request[ 0] completed at t+ 105ms (gap from previous:    0ms)
request[ 1] completed at t+ 210ms (gap from previous:  105ms)
request[ 2] completed at t+ 312ms (gap from previous:  102ms)
...
request[29] completed at t+3120ms (gap from previous:  104ms)

Average gap between completions: 104.0ms (expected ~100.0ms at 10.0/s)
```

The real completion timestamps land ~104ms apart on average against an expected
100ms — a real burst of 30 simultaneous requests converted into a real, steady,
one-every-~100ms output stream by the background drain thread, with zero rejections
because the burst fit under capacity.

## What this does and does not prove

Every number here is real, single-JVM, single-machine output — no distributed rate
limiter (no shared Redis counter, no network round-trip between limiter replicas) is
being exercised, only the algorithmic decision logic those production limiters also
implement. A distributed deployment adds its own real failure modes (the shared-counter
round-trip becomes the new race surface, and clock skew between nodes changes window
alignment) that a single-JVM demo cannot reproduce. What doesn't change is the shape of
all three findings: fixed windows really do allow a boundary-doubling burst, an
unsynchronized limiter really does have a race window under real concurrent load, and a
leaky bucket really does trade burst tolerance for a smoothed, constant-rate output.
