---
title: "Cheat Sheet: Testing Asynchronous and Concurrent Code"
slug: testing-asynchronous-and-concurrent-code
document_type: cheat-sheet
domain: 08-testing
topic_id: T-2420
canonical: ../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md
last_updated: 2026-09-21
---

# Testing Asynchronous and Concurrent Code

**Canonical chapter:** [`syllabus/08-testing/testing-asynchronous-and-concurrent-code.md`](../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md)

## Core Mental Model

A fixed `Thread.sleep()` before an async assertion is a guess, not knowledge — real, measured ~80% failure rate in this chapter's own demo. A race-condition test needs enough real concurrent load (many threads, many iterations) to make an intermittent bug reliably observable, not a coin flip a low-concurrency test can pass by luck.

## Essential Definitions

- **`CountDownLatch`** — the standard real-completion-signal tool: the async work counts it down when actually done; the test `await()`s it with a generous timeout, replacing a guessed sleep.
- **Stress test** — many threads, many iterations, released simultaneously (a start-gate latch) to maximize real contention and make a probabilistic race reliably observable.
- **Read-modify-write race** — `count++` on a plain `int` is three separate, interruptible operations; two threads can both read the same value before either writes back, silently losing an update.

## Decision Table

| Situation | Use |
|---|---|
| Testing async work completes | `CountDownLatch`/`CompletableFuture.join()`, never a fixed `Thread.sleep()` |
| Testing concurrent correctness (no lost updates, no double-processing) | A stress test: many threads, many iterations, simultaneous release |
| An async API with no completion callback | A bounded, timeout-guarded polling loop — still strictly better than one fixed sleep |
| Fixing a proven race condition | `AtomicInteger`/`synchronized` at the real source, never loosening the test's assertion |

## Common Pitfalls

- Widening a flaky sleep instead of replacing it with a real completion signal — narrows the failure window, never closes it.
- Testing concurrency with too few threads/iterations — a real race can pass by luck, producing false confidence.
- Loosening an assertion to tolerate an "approximately correct" concurrent result instead of fixing the actual synchronization bug.
- Starting stress-test threads staggered (a simple loop) instead of released simultaneously via a start gate — reduces real contention.

## Interview Answer Skeleton

**30-sec:** A fixed sleep before an async assertion is a guess, not knowledge — real, measured ~80% failure rate. Replace it with a `CountDownLatch`-based real completion signal. A race-condition test needs enough concurrent load (many threads, many iterations, released simultaneously) to make an intermittent bug reliably observable, not a coin flip.

**2-min:** Add the mechanism: `AsyncWorker`'s real 5-60ms variable delay vs. a fixed 20ms sleep loses the race ~80% of the time; `CountDownLatch.await()` waits exactly as long as the real work takes, 30/30 successful. A single-threaded race test can pass by luck; 8 threads × 100,000 increments against a plain `int` reliably measures a real ~79% lost-update rate, fixed with `AtomicInteger` (5/5 repetitions, always exact).

**Staff-level framing:** Stress testing at real scale before shipping a concurrent change is the difference between catching a race in CI and discovering it in production under real traffic — the same scale principle that makes an 8-thread test reliably reproduce a bug a 1-thread test never would.

## Production Warning Signs

- A test suite that passed reliably for months starts failing ~1 in N runs after a new test spawning a background thread was added — check its waiting strategy for a fixed sleep first.
- A test passes in isolation but fails only under CI load — consistent with a guessed-sleep failure window that's genuinely a function of real system timing.

## Real Measured Numbers

- `FlakyAsyncTest` (fixed 20ms sleep vs. real 5-60ms work): 6/30 successful, 24/30 failed.
- `ReliableAsyncTest` (`CountDownLatch`): 30/30 successful.
- `RaceConditionStressTest` (plain `int`, 8×100,000): `expected: <800000> but was: <170146>` (~79% lost); a second real run landed at 230,415 — the exact number is non-deterministic, the failure itself is reliable.
- `SafeCounterStressTest` (`AtomicInteger`, identical harness): 5/5 repetitions, all exactly 800,000.

## Related

- syllabus/08-testing/test-strategy-and-test-doubles.md
- syllabus/08-testing/integration-testing-against-real-dependencies.md
- syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
