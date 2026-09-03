---
title: "T-406 · Executors & Thread Pool Sizing"
topic_id: T-406
domain: Concurrency
tier: Core
iwi: 7.15
prerequisites: [T-401]
unlocks: [T-410]
week: 9
last_reviewed: 2026-07-30
canonical: ../../handbook/concurrency/executors-and-thread-pool-sizing.md
---

# T-406 · Executors & Thread Pool Sizing

**IWI 7.15 · Core tier**

**Canonical chapter:** [Executors and Thread Pool Sizing](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md). This file is the Week 9 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** both traces behind this summary are real, executed output from `practice/java/week-09/executors/src/ExecutorSizingDemo.java`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The unbounded-queue trap, measured](#3-the-unbounded-queue-trap-measured)
4. [Sizing from Little's Law](#4-sizing-from-littles-law)
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

A thread pool decouples task submission from task execution: a fixed set of worker threads pulls tasks from a queue. The two decisions that matter are pool size and what happens to a task that arrives when every worker is busy — and `Executors.newFixedThreadPool()`'s default answer to the second question is a trap. → [Definition and Purpose](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#definition-and-purpose).

## 2. Why it exists

Without pooling, every task either creates an unbounded new thread or blocks the submitter. Pools bound the first cost; queues decide what happens to the second — and `newFixedThreadPool`'s embedded choice (an unbounded queue) quietly trades "never rejected" for "unbounded memory growth under overload." → [Definition and Purpose](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#definition-and-purpose).

## 3. The unbounded-queue trap, measured

Measured: `newFixedThreadPool(2)` fed 500 tasks — 200ms in, 496 sit queued with zero rejection or backpressure signal. A `ThreadPoolExecutor` built with a bounded queue + `AbortPolicy` instead accepts exactly `corePoolSize + queueCapacity` and rejects the rest loudly. → [Internal Implementation](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#internal-implementation) has the full trace.

## 4. Sizing from Little's Law

`L = λ × W`. CPU-bound pools should size near `N_cores`; IO-bound pools scale with `N_cores × (1 + waitTime/computeTime)`, bounded by memory rather than CPU — exactly the asymmetry [virtual threads](../../syllabus/02-java/concurrency/virtual-threads.md) exist to remove. → [Core Concepts](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#core-concepts).

## 5. Trade-offs

Unbounded queue never rejects but grows memory without limit; bounded queue + `AbortPolicy` gives loud backpressure; `CallerRunsPolicy` throttles the producer instead. → [Trade-offs](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#trade-offs).

## 6. Interview questions

1. Size this pool. Show the arithmetic.
2. Queue is unbounded and memory is climbing. Why?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#interview-questions).

## 7. Common mistakes

Using `newFixedThreadPool`/`newCachedThreadPool` without understanding their default queue/thread-creation behavior; sizing by intuition instead of Little's Law; treating "never rejects" as a feature. → [Common Mistakes](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#common-mistakes).

## 8. Staff-level discussion

An unbounded queue is one instance of a general anti-pattern — absorbing overload internally instead of surfacing backpressure — that also governs unbounded caches, retries, and connection pools. → [Staff-Level Discussion](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#interview-answer-framework).

## 9. Summary

`newFixedThreadPool`'s unbounded queue trades rejection for silent, unbounded memory growth — measured at 496/500 tasks queued 200ms in. A bounded queue with an explicit rejection policy converts that into loud backpressure; pool size itself should come from Little's Law, not intuition. → [Summary](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#practice-exercises) and [Solutions](../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md#solutions). Reproducible demo: `practice/java/week-09/executors/src/ExecutorSizingDemo.java`.

## 14. Additional Reading

- [java.util.concurrent.ThreadPoolExecutor documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html)

## 15. Official References

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) — the eventual answer to IO-bound pool sizing pain, covered in `04-virtual-threads.md`
