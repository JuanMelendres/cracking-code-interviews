---
title: "T-410 · Virtual Threads (Project Loom)"
topic_id: T-410
domain: Concurrency
tier: Advanced
iwi: 6.75
prerequisites: [T-406]
unlocks: []
week: 9
last_reviewed: 2026-07-30
canonical: ../../handbook/concurrency/virtual-threads.md
---

# T-410 · Virtual Threads (Project Loom)

**IWI 6.75 · Advanced tier · risen sharply, now standard in 2026-era Senior Java loops**

**Canonical chapter:** [Virtual Threads (Project Loom)](../../handbook/concurrency/virtual-threads.md). This file is the Week 9 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** both traces behind this summary are real, executed output from `practice/java/week-09/virtual-threads/src/VirtualThreadScaleDemo.java` and `VirtualThreadPinningDemo.java`, on OpenJDK 21.0.12 (virtual threads are stable/final since JDK 21, no preview flags needed).

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

A virtual thread is a JVM-scheduled, cheaply-created thread multiplexed onto a small pool of carrier platform threads. When it blocks on a supported operation, the JVM unmounts it, freeing the carrier for other work — unlike a platform thread, the blocking call doesn't tie up an OS thread for its duration. → [Definition and Purpose](../../handbook/concurrency/virtual-threads.md#definition-and-purpose).

## 2. Why it exists

Platform threads are expensive (~1MB stack each), which is exactly why `02-executors-and-thread-pool-sizing.md`'s IO-bound sizing problem exists. Virtual threads remove that ceiling: millions can exist because most are unmounted while blocked. → [Definition and Purpose](../../handbook/concurrency/virtual-threads.md#definition-and-purpose) and [Historical Context](../../handbook/concurrency/virtual-threads.md#historical-context).

## 3. What actually changes for IO-bound workloads, measured

Measured: 5,000 tasks blocking 50ms each — a 200-platform-thread pool takes 1347ms; a virtual-thread-per-task executor takes 75ms. An 18x difference for identical work, because achievable concurrency stops being capped by platform-thread memory cost. → [Internal Implementation](../../handbook/concurrency/virtual-threads.md#internal-implementation) has the full trace.

## 4. Pinning, measured

Measured: 20 tasks blocking 200ms each on a 2-carrier scheduler — blocking inside `synchronized` pins the carrier (2044ms, fully serialized); the same code with `ReentrantLock` doesn't pin (206ms). A ~10x difference from swapping one lock type for another. → [Internal Implementation](../../handbook/concurrency/virtual-threads.md#internal-implementation) has the full trace.

## 5. Trade-offs

Virtual threads remove the memory ceiling on IO-bound concurrency but existing `synchronized`-heavy code pins and gets none of the benefit; `ReentrantLock` doesn't pin but requires explicit lock/unlock discipline; pooling virtual threads is an anti-pattern. → [Trade-offs](../../handbook/concurrency/virtual-threads.md#trade-offs).

## 6. Interview questions

1. What actually changes for IO-bound workloads under virtual threads?
2. Why is pooling virtual threads an anti-pattern?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/concurrency/virtual-threads.md#interview-questions).

## 7. Common mistakes

Expecting virtual threads to speed up CPU-bound work; migrating without auditing for `synchronized` around blocking calls; pooling virtual threads out of platform-thread habit. → [Common Mistakes](../../handbook/concurrency/virtual-threads.md#common-mistakes).

## 8. Staff-level discussion

A framework-level change can silently invalidate existing performance characteristics with no compile-time signal — `synchronized` still compiles and runs correctly, it just stops delivering the concurrency benefit the migration was for. → [Staff-Level Discussion](../../handbook/concurrency/virtual-threads.md#interview-answer-framework).

## 9. Summary

Virtual threads remove the memory-cost ceiling on IO-bound concurrency — measured 18x — but only if the blocking call actually unmounts; blocking inside `synchronized` pins instead, measured ~10x worse. Migrating existing code requires auditing for this hazard, not just swapping the executor. → [Summary](../../handbook/concurrency/virtual-threads.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/concurrency/virtual-threads.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/concurrency/virtual-threads.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/concurrency/virtual-threads.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/concurrency/virtual-threads.md#practice-exercises) and [Solutions](../../handbook/concurrency/virtual-threads.md#solutions). Reproducible demos: `practice/java/week-09/virtual-threads/src/VirtualThreadScaleDemo.java` and `VirtualThreadPinningDemo.java`.

## 14. Additional Reading

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)

## 15. Official References

- [Java SE documentation — Virtual Threads guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)
