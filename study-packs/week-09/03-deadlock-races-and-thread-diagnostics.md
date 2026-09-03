---
title: "T-409 · Deadlock, Race Conditions & Thread Diagnostics"
topic_id: T-409
domain: Concurrency
tier: Core
iwi: 6.70
prerequisites: [T-401]
unlocks: []
week: 9
last_reviewed: 2026-07-30
canonical: ../../handbook/concurrency/deadlock-race-conditions-and-thread-diagnostics.md
---

# T-409 · Deadlock, Race Conditions & Thread Diagnostics

**IWI 6.70 · Core tier**

**Canonical chapter:** [Deadlock, Race Conditions, and Thread Diagnostics](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md). This file is the Week 9 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `MANIFEST.md`'s errata table cites §3 directly.

**Errata correction, stated explicitly:** the source material's thread-lifecycle diagram invented a "Running" state and omitted `TIMED_WAITING`. `java.lang.Thread.State` has exactly six real values — §3 prints all six from a real running JVM, not from memory.

**Verification note:** every trace behind this summary is real, executed output: `practice/java/week-09/concurrency-fundamentals/src/ThreadStateDemo.java` (thread states), `practice/java/week-09/deadlock-diagnostics/src/DeadlockDemo.java` (a genuine deadlock, detected via `ThreadMXBean`), `practice/java/week-09/deadlock-diagnostics/src/RaceConditionDemo.java` (measured lost updates).

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

Deadlock, livelock, starvation, and race conditions are four distinct concurrency failure modes requiring different diagnosis — deadlock is a race condition's more dramatic cousin, not a separate category. → [Definition and Purpose](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#definition-and-purpose).

## 2. Why it exists

Multiple threads sharing mutable state or contending for locks is unavoidable in real systems; these four failure modes are the concrete, diagnosable shapes that sharing state incorrectly actually takes in production. → [Definition and Purpose](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#definition-and-purpose).

## 3. The real six-state lifecycle, corrected

Measured: `Thread.State.values()` printed directly from a running JVM — six real states (NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED), no invented "Running" state, TIMED_WAITING demonstrated. → [Internal Implementation](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#internal-implementation) has the full trace.

## 4. A real deadlock, detected

Measured: two threads acquiring two locks in opposite order deadlock; `ThreadMXBean.findDeadlockedThreads()` — the same mechanism `jstack` uses — detects the exact cycle. Fix: consistent global lock-acquisition ordering. → [Internal Implementation](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#internal-implementation).

## 5. A race condition, measured

Measured: 10 threads each incrementing a shared counter 100,000 times lost 838,094 updates (83.8%) unsynchronized; `AtomicInteger` lost zero under identical load. → [Internal Implementation](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#internal-implementation).

## 6. Trade-offs

`synchronized` prevents races and gives visibility at the cost of contention/deadlock risk; atomic classes avoid contention but only cover single-variable operations; consistent lock ordering eliminates deadlock structurally but isn't compiler-enforced. → [Trade-offs](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#trade-offs).

## 7. Interview questions

1. Two threads deadlock in production. Walk me through diagnosing it live.
2. Your metrics counter is undercounting under load. Why, and how do you fix it — show the numbers.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#interview-questions).

## 8. Common mistakes

Inventing a "Running" thread state or forgetting `TIMED_WAITING`; assuming race conditions are rare rather than near-certain under load; debugging deadlocks by reading logs instead of pulling a thread dump. → [Common Mistakes](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#common-mistakes).

## 9. Staff-level discussion

Deadlock and the measured race condition are both instances of the same lesson: concurrent bugs are not rare edge cases — under real load, they manifest reliably and severely. → [Staff-Level Discussion](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#interview-answer-framework).

## 10. Summary

The real `Thread.State` enum has six values, corrected directly from a running JVM. Deadlock is detectable via `ThreadMXBean` and structurally preventable via lock ordering. Race conditions from unsynchronized compound operations lose the vast majority of updates under real load. → [Summary](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#summary).

## 11. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#key-takeaways).

## 12. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#cheat-sheet).

## 13. Flashcards

→ [Flashcards](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 14. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#practice-exercises) and [Solutions](../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md#solutions). Reproducible demos in `practice/java/week-09/`.

## 15. Additional Reading

- [java.lang.management.ThreadMXBean documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.management/java/lang/management/ThreadMXBean.html)

## 16. Official References

- [Java Language Specification §17.1 — Synchronization](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.1)
