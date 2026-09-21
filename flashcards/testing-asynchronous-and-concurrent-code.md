---
title: "Flashcards: Testing Asynchronous and Concurrent Code"
slug: testing-asynchronous-and-concurrent-code
document_type: flashcard-deck
domain: 08-testing
topic_id: T-2420
canonical: ../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md
last_updated: 2026-09-21
---

# Flashcards: Testing Asynchronous and Concurrent Code

**Canonical chapter:** [`syllabus/08-testing/testing-asynchronous-and-concurrent-code.md`](../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md)

## Card: Why a fixed sleep before an async assertion is a real bug

**Prompt:**
Why is a fixed `Thread.sleep()` before an assertion about async work a real, reproducible source of flakiness, not just a theoretical risk?

**Answer:**
It's a guess at how long the work takes, not knowledge of when it's actually done. Measured directly: real async work with a genuine 5-60ms variable delay, tested with a fixed 20ms sleep, failed 24 of 30 repetitions (~80%) — a real, measured failure rate, not a hypothetical one.

**Why it matters:**
Widening the sleep only narrows the failure window; it never closes it, since the guess can always be wrong on a slower run.

**Common trap:**
Treating a longer fixed sleep as a real fix rather than a narrower version of the same guess.

**Related:**
[Foundation](../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md#3-foundation-l1)

## Card: The real fix for async test flakiness

**Prompt:**
What replaces a guessed sleep for testing async work reliably, and how well does it work?

**Answer:**
A `CountDownLatch` the async work itself counts down when actually done, awaited with a generous timeout via `latch.await(timeout, unit)` — the test waits exactly as long as the real work takes. Measured directly against the identical underlying work that failed 80% of the time with a fixed sleep: 30 of 30 repetitions successful.

**Why it matters:**
Same underlying async work, same 5-60ms real delay — only the waiting strategy changed, and flakiness went from ~80% to 0%.

**Common trap:**
Assuming any wait-based fix requires rewriting the async API itself, rather than just the test's own waiting strategy.

**Related:**
[How It Works Internally](../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md#5-how-it-works-internally-l3)

## Card: Why a race-condition test needs real scale

**Prompt:**
Why can't a single-threaded (or low-iteration) test reliably catch a race condition?

**Answer:**
A race condition only exists *between* threads — a single thread, or too few concurrent iterations, can pass by luck even with the bug fully present, providing false confidence. This chapter's real stress test (8 threads × 100,000 increments against a plain `int`) reliably measures a real ~79% lost-update rate; reducing it to 1 thread makes the identical unsynchronized code pass every time.

**Why it matters:**
"Non-deterministic" doesn't mean "untestable" — it means the test needs enough real concurrent load to make the bug statistically likely to surface on every run.

**Common trap:**
Writing a multi-threaded test with too few threads or iterations, mistaking "it passed" for "it's correct."

**Related:**
[Performance Implications](../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md#10-performance-implications)

## Card: Proving a concurrency fix actually works

**Prompt:**
How do you prove a fix for a race condition actually addresses it, rather than just making the bug less frequent?

**Answer:**
Re-run the identical stress-test harness (same thread count, same iteration count, same simultaneous-release mechanism) against the fixed code, not a smaller or easier test. This chapter's real demo: `SafeCounterStressTest` reuses `RaceConditionStressTest`'s exact harness unmodified against `AtomicInteger` instead of a plain `int` — 5 of 5 repetitions, all exactly the expected count.

**Why it matters:**
A fix verified only by a weaker test than the one that caught the bug provides false confidence, the same failure mode as under-scaled race-condition tests generally.

**Common trap:**
Verifying a concurrency fix with a single-threaded or low-iteration test, which would have passed even without the fix.

**Related:**
[Examples](../syllabus/08-testing/testing-asynchronous-and-concurrent-code.md#7-examples)
