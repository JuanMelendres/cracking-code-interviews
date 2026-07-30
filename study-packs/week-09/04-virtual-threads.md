---
title: "T-410 · Virtual Threads (Project Loom)"
topic_id: T-410
domain: Concurrency
tier: Advanced
iwi: 6.75
prerequisites: [T-406]
unlocks: []
week: 9
last_reviewed: 2026-07-29
---

# T-410 · Virtual Threads (Project Loom)

**IWI 6.75 · Advanced tier · risen sharply, now standard in 2026-era Senior Java loops**

**Verification note:** both traces in §3 and §4 are real, executed output from `practice/java/week-09/virtual-threads/src/VirtualThreadScaleDemo.java` and `VirtualThreadPinningDemo.java`, on OpenJDK 21.0.12 (virtual threads are stable/final since JDK 21, no preview flags needed).

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [What actually changes for IO-bound workloads, measured](#3-what-actually-changes-for-io-bound-workloads-measured)
4. [Pinning, measured](#4-pinning-measured)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

A **virtual thread** is a JVM-scheduled, cheaply-created thread that is multiplexed onto a small pool of **carrier** platform threads. When a virtual thread blocks on a supported operation (network IO, `Thread.sleep`, blocking queue operations), the JVM **unmounts** it from its carrier, freeing that carrier to run a different virtual thread — the blocking call doesn't tie up an OS thread for its duration, unlike a traditional platform thread.

## 2. Why it exists

Platform threads are expensive (roughly 1MB of stack memory each, real OS scheduling overhead), which is exactly why §`02-executors-and-thread-pool-sizing.md`'s IO-bound sizing problem exists — a pool handling mostly-waiting requests needs many threads to achieve concurrency, but "many" is capped by real memory and OS limits, often in the low thousands. Virtual threads exist specifically to remove that ceiling for IO-bound workloads: millions of virtual threads can exist because most of them are unmounted (not consuming a platform thread) while blocked.

## 3. What actually changes for IO-bound workloads, measured

**Real output**, 5,000 tasks each blocking for 50ms, run two ways:

```
== 200 platform threads, 5000 blocking 50ms tasks ==
platform pool (200 threads): 5000 tasks completed in 1347ms (theoretical minimum if fully parallel: 50ms)

== virtual-thread-per-task executor, same 5000 blocking 50ms tasks ==
virtual threads (one per task): 5000 tasks completed in 75ms (theoretical minimum if fully parallel: 50ms)
```

**1347ms vs. 75ms — an 18x difference, for identical work.** The 200-platform-thread pool must process 5,000 tasks in batches of 200 (25 batches × 50ms ≈ 1250ms, matching the measured 1347ms closely); the virtual-thread executor gives every task its own thread, so all 5,000 block concurrently and the whole batch finishes close to the 50ms theoretical minimum. This is the entire practical value proposition of virtual threads in one measurement: **for IO-bound work, the achievable concurrency stops being capped by platform-thread memory cost.**

## 4. Pinning, measured

Not every blocking operation on a virtual thread unmounts cleanly. Blocking **inside a `synchronized` block** pins the virtual thread to its carrier — the carrier cannot run anything else until the blocking call returns, defeating the entire mechanism for that stretch of code.

**Real output**, 20 tasks each blocking for 200ms, run with the carrier pool forced down to 2 threads (`-Djdk.virtualThreadScheduler.parallelism=2`), each task locking its own independent object (isolating the pinning effect from ordinary lock contention):

```
carrier parallelism = 2

== blocking INSIDE synchronized -- pins the carrier thread ==
20 tasks x 200ms blocking each, synchronized (pins): 2044ms wall time (unpinned lower bound with a small carrier pool is roughly (tasks/carriers)*blockMs)

== blocking INSIDE a ReentrantLock -- does NOT pin ==
20 tasks x 200ms blocking each, ReentrantLock (no pin): 206ms wall time (unpinned lower bound with a small carrier pool is roughly (tasks/carriers)*blockMs)
```

**2044ms vs. 206ms — roughly a 10x difference, from swapping `synchronized` for `ReentrantLock` alone**, nothing else changed. 2044ms matches `(20 tasks / 2 carriers) × 200ms = 2000ms` almost exactly — with `synchronized`, the virtual threads are effectively fully serialized onto the 2 carriers, exactly as if they were platform threads, because pinning defeats the unmount mechanism entirely for the pinned duration. `ReentrantLock`'s 206ms is close to the 200ms unpinned lower bound — the blocking call unmounts normally, so the carrier count barely matters.

## 5. Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| Virtual threads for IO-bound work | Concurrency no longer capped by platform-thread memory cost (§3: 18x measured) | Existing `synchronized`-heavy code pins and gets none of the benefit (§4: 10x regression) |
| `synchronized` (legacy code, unchanged) | Simple, well-understood | Pins virtual threads — a silent performance cliff when migrating old code onto virtual threads |
| `ReentrantLock` | Doesn't pin, same mutual-exclusion guarantee | Requires an explicit `lock()`/`unlock()` (or try/finally) migration from `synchronized` |
| Pooling virtual threads (anti-pattern) | — | Virtual threads are meant to be created per-task, cheaply, and discarded — pooling them defeats the design and adds needless complexity for no benefit |

## 6. Interview questions

### Q1. What actually changes for IO-bound workloads under virtual threads?

- **Expected answer:** the achievable concurrency for blocking-IO-heavy work is no longer capped by platform-thread memory cost — many virtual threads can be blocked simultaneously because unmounted ones don't occupy a carrier.
- **Common mistakes:** claiming virtual threads make CPU-bound code faster (they don't — CPU-bound work still needs an actual core; virtual threads help by increasing achievable IO concurrency, not compute throughput).
- **Follow-up questions:** "What's the catch — is there any code that doesn't benefit, or actively regresses?"
- **Senior-level expectations:** correctly scopes the benefit to IO-bound/blocking workloads specifically.
- **Staff-level expectations:** names pinning unprompted as the regression case, and can state roughly how severe it is from having seen or reasoned through a measurement like §4's.

### Q2. Why is pooling virtual threads an anti-pattern?

- **Expected answer:** virtual threads are designed to be cheap and disposable — creating millions is the intended usage pattern; pooling reintroduces the platform-thread-style "limited resource, must be reused" mental model that virtual threads exist specifically to eliminate, adding complexity for a cost virtual threads don't actually have.
- **Common mistakes:** applying platform-thread pooling instincts by default without questioning whether they still apply.
- **Follow-up questions:** "So how DO you limit concurrency to a downstream system if not with a pool size?"
- **Senior-level expectations:** states that virtual threads are meant to be created per-task, not pooled.
- **Staff-level expectations:** names an actual alternative for the underlying need (limiting concurrent load on a downstream system) — a semaphore or rate limiter bounding concurrent *in-flight requests*, decoupled from thread count entirely.

## 7. Common mistakes

- Expecting virtual threads to speed up CPU-bound work — they don't; the bottleneck there is cores, not thread creation cost.
- Migrating IO-heavy code to virtual threads without auditing for `synchronized` blocks around blocking calls, then being surprised by a 10x-class regression.
- Pooling virtual threads out of habit from platform-thread practice.

## 8. Staff-level discussion

Virtual threads are a case where a framework-level change (the JVM's threading model) can silently invalidate an existing codebase's performance characteristics without any compile-time signal — `synchronized` still compiles and runs correctly under virtual threads, it just quietly stops delivering the concurrency benefit the migration was for. A Staff-level engineer treats "we're moving to virtual threads" as requiring an actual audit of blocking-call-inside-`synchronized` sites, not just a drop-in executor swap — because the failure mode here (§4's 10x regression) is a performance cliff with no compiler error, exception, or obvious symptom beyond "this got slower than expected after the migration."

## 9. Summary

Virtual threads remove the memory-cost ceiling that caps achievable concurrency for IO-bound workloads under platform threads — measured at 18x for a mostly-waiting workload (§3). That benefit depends entirely on the blocking call actually unmounting from its carrier; blocking inside `synchronized` pins instead, measured at roughly 10x worse than the unpinned case (§4), turning virtual threads into de facto platform threads for that code path. Migrating existing code requires auditing for this specific hazard, not just swapping the executor.

## 10. Key Takeaways

- Virtual threads help IO-bound (blocking) workloads by removing the platform-thread memory ceiling on concurrency — measured 18x here.
- They do not help CPU-bound work — the bottleneck there is cores, not thread cost.
- Blocking inside `synchronized` pins the carrier thread — measured ~10x regression versus the unpinned case.
- Don't pool virtual threads — create them per-task; use a semaphore/rate limiter to bound concurrent load on a downstream system instead.

## 11. Cheat Sheet

| Situation | Guidance |
|---|---|
| IO-bound workload, want more concurrency | Virtual-thread-per-task executor |
| CPU-bound workload | Virtual threads won't help — size a platform pool near `N_cores` instead |
| Existing code uses `synchronized` around blocking calls | Audit and migrate to `ReentrantLock` before moving to virtual threads, or accept the pinning cost |
| Need to limit concurrent load on a downstream system | A semaphore/rate limiter, not a thread pool size |

## 12. Flashcards

1. **Q: What does a virtual thread's carrier do when the virtual thread blocks on supported IO?** A: Unmounts the virtual thread, freeing the carrier to run a different virtual thread — the blocking call doesn't tie up a platform thread.
2. **Q: What causes a virtual thread to pin its carrier?** A: Blocking inside a `synchronized` block (or a few other cases, e.g. native calls) — the carrier can't run anything else until the call returns.
3. **Q: Why is pooling virtual threads considered an anti-pattern?** A: They're designed to be cheap and disposable, created per-task; pooling reimposes platform-thread-style resource-limiting thinking that virtual threads exist to eliminate.

(Full week-level deck: `06-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce both demos: `practice/java/week-09/virtual-threads/src/VirtualThreadScaleDemo.java` and `VirtualThreadPinningDemo.java` (the latter needs `-Djdk.virtualThreadScheduler.parallelism=2`).
2. Modify `VirtualThreadPinningDemo` to use 4 carriers instead of 2 and predict the new wall-clock time for the pinned case before running it — does the arithmetic (`tasks/carriers * blockMs`) still hold?
3. Find one real hazard beyond `synchronized` that also pins a virtual thread's carrier (check the JDK's virtual thread documentation) and explain why it has the same effect.

## 14. Additional Reading

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)

## 15. Official References

- [Java SE documentation — Virtual Threads guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)
