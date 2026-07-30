---
title: "T-409 · Deadlock, Race Conditions & Thread Diagnostics"
topic_id: T-409
domain: Concurrency
tier: Core
iwi: 6.70
prerequisites: [T-401]
unlocks: []
week: 9
last_reviewed: 2026-07-29
---

# T-409 · Deadlock, Race Conditions & Thread Diagnostics

**IWI 6.70 · Core tier**

**Errata correction, stated explicitly:** the source material's thread-lifecycle diagram invented a "Running" state and omitted `TIMED_WAITING`. `java.lang.Thread.State` has exactly six real values — §3 prints all six from a real running JVM, not from memory.

**Verification note:** every trace in this chapter is real, executed output: `practice/java/week-09/concurrency-fundamentals/src/ThreadStateDemo.java` (thread states), `practice/java/week-09/deadlock-diagnostics/src/DeadlockDemo.java` (a genuine deadlock, detected via `ThreadMXBean`), `practice/java/week-09/deadlock-diagnostics/src/RaceConditionDemo.java` (measured lost updates).

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The real six-state lifecycle, corrected](#3-the-real-six-state-lifecycle-corrected)
4. [A real deadlock, detected](#4-a-real-deadlock-detected)
5. [A race condition, measured](#5-a-race-condition-measured)
6. [Trade-offs](#6-trade-offs)
7. [Interview questions](#7-interview-questions)
8. [Common mistakes](#8-common-mistakes)
9. [Staff-level discussion](#9-staff-level-discussion)
10. [Summary](#10-summary)
11. [Key Takeaways](#11-key-takeaways)
12. [Cheat Sheet](#12-cheat-sheet)
13. [Flashcards](#13-flashcards)
14. [Practice Exercises](#14-practice-exercises)
15. [Additional Reading](#15-additional-reading)
16. [Official References](#16-official-references)

---

## 1. The concept

Deadlock, livelock, starvation, and race conditions are four distinct concurrency failure modes, often lumped together but requiring different diagnosis: **deadlock** is a cycle of threads each waiting on a lock the next one holds (permanent stall); **livelock** is threads actively responding to each other but making no progress (not stalled, just never finishing); **starvation** is a thread perpetually denied a resource by unfair scheduling; a **race condition** is any outcome that depends on unlucky timing of unsynchronized access — deadlock is a race condition's more dramatic cousin, not a separate category.

## 2. Why it exists

Multiple threads sharing mutable state or contending for the same locks is unavoidable in most real systems — this topic exists because these four failure modes are the concrete, diagnosable shapes that sharing state incorrectly actually takes in production, and each has a real detection technique (not just a description).

## 3. The real six-state lifecycle, corrected

**Real output** from `Thread.getState()`, captured at each real lifecycle point:

```
Before start(): NEW
Inside monitor.wait() (no timeout): WAITING
After join() returns: TERMINATED

== TIMED_WAITING, the state the source material's diagram omitted ==
While inside Thread.sleep(2000): TIMED_WAITING
After it wakes and finishes: TERMINATED

Real Thread.State enum, for reference: [NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED]
```

Six real states, printed directly from `Thread.State.values()` on a running JVM — no invented "Running" state, and `TIMED_WAITING` present and demonstrated (a thread inside `Thread.sleep()` or a timed `wait()`/`join()`/lock-acquire). The distinction between `WAITING` and `TIMED_WAITING` matters diagnostically: a thread stuck in `WAITING` forever with nothing to wake it is a real bug (a missed `notify()`), while `TIMED_WAITING` will self-resolve regardless of whether anything wakes it. `BLOCKED` (contending for a monitor another thread holds) is demonstrated separately in §4, since it requires genuine lock contention to observe.

## 4. A real deadlock, detected

**Real output**, two threads acquiring two locks in opposite order (`thread-1` takes `A` then wants `B`; `thread-2` takes `B` then wants `A`):

```
== states while deadlocked ==
thread-1-A-then-B: BLOCKED
thread-2-B-then-A: BLOCKED

== ThreadMXBean.findDeadlockedThreads() -- real detection, not a guess ==
DEADLOCKED: thread-1-A-then-B is BLOCKED, waiting on java.lang.Object@6acbcfc0 held by thread-2-B-then-A
DEADLOCKED: thread-2-B-then-A is BLOCKED, waiting on java.lang.Object@4f3f5b24 held by thread-1-A-then-B
```

This is the actual production diagnostic technique — `ThreadMXBean.findDeadlockedThreads()` is what `jstack` and most APM tools use under the hood, walking the lock-ownership graph to find a real cycle, rather than a human reading a thread dump and guessing. Both threads show `BLOCKED`, and the bean's output names exactly which lock each thread wants and who currently holds it — enough to reconstruct the acquisition-order bug directly from the diagnostic.

**The fix, structurally**: enforce a single global lock-acquisition order everywhere in the codebase (e.g., always acquire the lock with the lower `System.identityHashCode()`, or better, a documented, code-reviewed ordering by design) — deadlock from lock-ordering is entirely preventable by discipline, not detectable-and-fixable after the fact in production.

## 5. A race condition, measured

**Real output**, 10 threads each incrementing a shared counter 100,000 times (expected total: 1,000,000):

```
== plain int, unsynchronized ++ ==
expected=1000000 actual=161906 lost=838094

== AtomicInteger.incrementAndGet() ==
expected=1000000 actual=1000000 lost=0
```

**838,094 lost updates — 83.8% of all increments silently disappeared.** This is the measured version of `count++` not being atomic (§`01-java-memory-model-and-volatile.md` §6 Q2): each increment is read-modify-write, and with 10 threads racing, the vast majority of writes clobber each other rather than accumulating. `AtomicInteger` (compare-and-swap under the hood) loses zero updates under identical load — not because it's "faster," but because each `incrementAndGet()` call is a single indivisible operation with no window for another thread to interleave.

## 6. Trade-offs

| Mechanism | Benefit | Cost |
|---|---|---|
| `synchronized` around the critical section | Prevents both the race AND establishes visibility (happens-before) | Lock contention; deadlock risk if multiple locks are involved |
| `AtomicInteger`/atomic classes | No lock contention, measured zero loss above | Only covers single-variable compound operations, not multi-variable invariants |
| Consistent lock-acquisition ordering | Eliminates deadlock risk structurally, by design | Requires discipline and code review; not enforceable by the compiler |
| `ThreadMXBean` deadlock detection | Finds deadlocks in a running production system, precisely | Detects, doesn't prevent — the process is already stalled when this runs |

## 7. Interview questions

### Q1. Consumer crashes after processing but before committing. What happens, and how do you make that safe?

*(This is deliberately the same question named in Week 8's delivery-semantics chapter — worth recognizing it's the same shape of problem, not a new one: an operation interrupted mid-sequence.)* For this week's framing: **two threads deadlock in production. Walk me through diagnosing it live.**

- **Expected answer:** attach with `jstack` or an equivalent (which internally uses `ThreadMXBean.findDeadlockedThreads()`), identify the `BLOCKED` threads and which locks each wants/holds, reconstruct the acquisition-order bug from that.
- **Common mistakes:** describing deadlock only in the abstract (dining philosophers) without naming a concrete diagnostic tool or technique.
- **Follow-up questions:** "How do you prevent it from happening again?"
- **Senior-level expectations:** names `jstack`/thread dumps and the lock-ordering fix.
- **Staff-level expectations:** proposes a structural prevention (global lock ordering convention, or eliminating the need for multiple locks via a different design) rather than only reactive detection.

### Q2. Your metrics counter is undercounting under load. Why, and how do you fix it — show the numbers.

- **Expected answer:** `count++` isn't atomic; concurrent threads lose updates via interleaved read-modify-write. Fix: `AtomicLong`/`AtomicInteger`, or `LongAdder` for very high contention.
- **Common mistakes:** treating this as "sounds unlikely" rather than recognizing it as a near-certainty under real concurrent load (§5 measured 83.8% loss).
- **Follow-up questions:** "AtomicInteger vs LongAdder — when does it matter?"
- **Senior-level expectations:** names `AtomicInteger`/`AtomicLong` and can state the measured-style consequence.
- **Staff-level expectations:** knows `LongAdder` trades single-value read consistency for higher-throughput writes under heavy contention (multiple internal cells, summed on read) — the right choice specifically for write-heavy, read-rarely counters like metrics.

## 8. Common mistakes

- Believing the thread lifecycle has a distinct "Running" state separate from `RUNNABLE`, or forgetting `TIMED_WAITING` exists.
- Assuming a race condition is a rare, unlucky-timing edge case rather than something that reliably manifests under real concurrent load (measured 83.8% loss above, not 0.1%).
- Debugging a suspected deadlock by reading logs rather than pulling an actual thread dump / using `ThreadMXBean`.

## 9. Staff-level discussion

Deadlock and the race-condition measurement above are both instances of the same underlying lesson: concurrent bugs are not rare edge cases that occasionally slip through — under real load, they manifest reliably and severely (838,094 lost updates out of 1,000,000; a deadlock that occurs 100% of the time given the reproduced lock-ordering bug). A Staff-level engineer treats "this shared-state code has no explicit synchronization strategy" as a near-certain future incident, not a maybe, and reviews for lock-ordering discipline and atomicity requirements as rigorously as for any other correctness property — because empirical testing under light load will not reliably surface either failure mode before production traffic does.

## 10. Summary

The real `Thread.State` enum has six values, not the invented five-state model with a missing `TIMED_WAITING` — corrected directly from a running JVM in §3. Deadlock is detectable in a live system via `ThreadMXBean.findDeadlockedThreads()`, the same mechanism `jstack` uses, and is structurally preventable via consistent lock-ordering. Race conditions from unsynchronized compound operations are not a rare failure mode — measured at 83.8% lost updates under realistic concurrent load, resolved completely by `AtomicInteger`.

## 11. Key Takeaways

- `Thread.State` has exactly six values: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED.
- `ThreadMXBean.findDeadlockedThreads()` is the real production diagnostic, underlying `jstack` and most APM tooling.
- Deadlock is structurally preventable via consistent lock-acquisition ordering.
- Unsynchronized compound operations under real concurrent load lose the vast majority of updates, not a small fraction.

## 12. Cheat Sheet

| Symptom | Diagnostic | Fix |
|---|---|---|
| Threads permanently stuck, CPU idle | `ThreadMXBean.findDeadlockedThreads()` / `jstack` | Consistent lock-acquisition ordering |
| Counter/metric undercounting under load | Code review for `count++`-style compound ops | `AtomicInteger`/`AtomicLong`/`LongAdder` |
| Thread stuck in `WAITING` forever | Missed `notify()`/`notifyAll()` | Ensure every `wait()` has a matching, reachable `notify()` |

## 13. Flashcards

1. **Q: What are the six real `Thread.State` values?** A: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED — no separate "Running" state, and TIMED_WAITING is real (e.g., inside `Thread.sleep()`).
2. **Q: How do you detect a deadlock in a live JVM?** A: `ThreadMXBean.findDeadlockedThreads()` (what `jstack` uses under the hood) — walks the lock-ownership graph for a real cycle.
3. **Q: How much data can an unsynchronized `count++` lose under real concurrent load?** A: Measured: 83.8% of updates lost with 10 threads × 100,000 increments each — not a rare edge case.

(Full week-level deck: `06-flashcards.md`.)

## 14. Practice Exercises

1. Reproduce all three demos: `ThreadStateDemo.java`, `DeadlockDemo.java`, `RaceConditionDemo.java` in `practice/java/week-09/`.
2. Modify `DeadlockDemo` to use a single consistent lock-acquisition order (both threads take `A` then `B`) and confirm no deadlock occurs.
3. Change `RaceConditionDemo`'s thread count and increments-per-thread and observe how the lost-update percentage changes — is it linear in thread count?

## 15. Additional Reading

- [java.lang.management.ThreadMXBean documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.management/java/lang/management/ThreadMXBean.html)

## 16. Official References

- [Java Language Specification §17.1 — Synchronization](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.1)
