# Testing Asynchronous and Concurrent Code — Real, Executed Demo

Backs [Testing Asynchronous and Concurrent Code](../../../../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md) (T-2420). Real JUnit 5 tests, run with the `junit-platform-console-standalone` shaded jar (1.10.3) — no Maven/Gradle install required.

## Setup

```bash
mkdir -p out
javac -cp "lib/*" -d out src/*.java
```

## Run

```bash
# The clean, reliable suite -- expected to pass every time:
java -jar lib/junit-platform-console-standalone.jar execute -cp out \
    --select-class ReliableAsyncTest --select-class SafeCounterStressTest --details=tree

# The two demos of real, reproducible failure modes, run separately
# (deliberately NOT part of the "official passing suite" above --
# flakiness is the entire point, not a bug to hide):
java -jar lib/junit-platform-console-standalone.jar execute -cp out --select-class FlakyAsyncTest --details=tree
java -jar lib/junit-platform-console-standalone.jar execute -cp out --select-class RaceConditionStressTest --details=tree
```

`test-run-output.txt` — the clean suite, real captured output: 35/35 passing.
`flaky-async-failure-output.txt` — real captured output of the flaky-by-design test.
`race-condition-failure-output.txt` — real captured output of the race-condition stress test.

## What each demo proves

1. **A fixed `Thread.sleep()` is a real, reproducible source of test flakiness — not a theoretical risk.**
   [`AsyncWorker.java`](src/AsyncWorker.java) does genuine background work with a real, variable
   5–60ms delay. [`FlakyAsyncTest.java`](src/FlakyAsyncTest.java) waits a fixed 20ms (shorter
   than the real worst case) before asserting completion — real captured output:
   **6/30 successful, 24/30 failed** (a real ~80% failure rate this run; the exact split
   varies run to run, which is itself the point — flakiness by construction, not a fixed bug).

2. **Waiting on a real completion signal instead of a guessed delay eliminates the flakiness
   entirely, for the identical underlying work.** [`ReliableAsyncTest.java`](src/ReliableAsyncTest.java)
   uses a `CountDownLatch` the async callback itself counts down, awaited with a generous
   timeout — real captured output: **30/30 successful**, every run.

3. **A single-iteration test can pass by luck even with a real race condition present — a
   stress test with enough concurrent iterations makes an intermittent bug reliably
   observable.** [`RaceConditionStressTest.java`](src/RaceConditionStressTest.java) runs 8
   threads incrementing a plain `int` 100,000 times each (800,000 expected total) — real
   captured output: **`expected: <800000> but was: <170146>`**, a real ~79% of updates
   silently lost to the classic read-modify-write race. A second real run (before this
   result was saved to `race-condition-failure-output.txt`) landed at a different but
   equally real number, **230,415** — the exact lost-update count is genuinely
   non-deterministic, run to run; what's reliable is the failure itself, not any one
   specific number, and this README states that honestly rather than picking whichever
   run looked most dramatic.

4. **`AtomicInteger` genuinely fixes it — verified by the identical stress harness, not
   asserted.** [`SafeCounterStressTest.java`](src/SafeCounterStressTest.java) runs the exact
   same 8-threads/100,000-increments harness (`RaceConditionStressTest.runConcurrently`,
   reused directly, not reimplemented) against `SafeCounter` — real captured output:
   **5/5 repetitions, all exactly 800,000**, every time.
