---
title: "Backend Java Specialization, Week 3 — Concurrency Internals"
document_type: study-pack
week: 3
track: backend-java-specialization
status: draft
estimated_hours: 12
---

# Week 3 — Concurrency Internals

## Weekly Outcome

By the end of this week you can explain the Java Memory Model's happens-before guarantees, choose correctly between `synchronized`, `ReentrantLock`, and `StampedLock`, size a thread pool for a stated workload, diagnose a deadlock from a real thread dump, and state precisely what virtual threads and structured concurrency change (and do not change) about that reasoning.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) places `concurrency` directly after `collections` because concurrent collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue`, all covered Week 2) only make sense once the memory-visibility guarantees underneath them are understood. Every later domain in this path — Spring's request handling, Kafka consumer threads, database connection pooling — assumes this week's reasoning is solid.

## Prerequisites

Weeks 1–2 complete, in particular T-103 (Immutability and Defensive Copying) — immutable state is the first and most effective concurrency tool this week's chapters return to repeatedly.

## Schedule

| Day | Focus |
|---|---|
| Mon | Java Memory Model and volatile (T-401/T-402) |
| Tue | ReentrantLock, ReadWriteLock, StampedLock (T-404); Atomics, CAS, and the ABA Problem (T-405) |
| Wed | Executors and Thread Pool Sizing (T-406); Deadlock, Race Conditions, and Thread Diagnostics (T-409) |
| Thu | CompletableFuture (T-407); ForkJoinPool and Work-Stealing (T-408) |
| Fri | Virtual Threads (T-410); Structured Concurrency (T-411) |
| Sat | Scoped Values/ThreadLocal Migration (T-412); ThreadLocal-Mediated Classloader Leaks (T-413) |
| Sun | VarHandles/Unsafe (T-415); Foreign Function & Memory API (T-416/T-414); review checklist below |

## Required Reading

The full `concurrency` subdomain, per [`syllabus/02-java/INDEX.md`](../../../syllabus/02-java/INDEX.md) (the exhaustive, canonical source).

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-401/T-402 — Java Memory Model and volatile | [`java-memory-model-and-volatile.md`](../../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md) |
| 2 | T-404 — ReentrantLock, ReadWriteLock, and StampedLock | [`reentrantlock-readwritelock-and-stampedlock.md`](../../../syllabus/02-java/concurrency/reentrantlock-readwritelock-and-stampedlock.md) |
| 3 | T-405 — Atomics, CAS, and the ABA Problem | [`atomics-cas-and-the-aba-problem.md`](../../../syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md) |
| 4 | T-406 — Executors and Thread Pool Sizing | [`executors-and-thread-pool-sizing.md`](../../../syllabus/02-java/concurrency/executors-and-thread-pool-sizing.md) |
| 5 | T-407 — CompletableFuture and Async Composition | [`completablefuture-and-async-composition.md`](../../../syllabus/02-java/concurrency/completablefuture-and-async-composition.md) |
| 6 | T-408 — ForkJoinPool and Work-Stealing | [`forkjoinpool-and-work-stealing.md`](../../../syllabus/02-java/concurrency/forkjoinpool-and-work-stealing.md) |
| 7 | T-409 — Deadlock, Race Conditions, and Thread Diagnostics | [`deadlock-race-conditions-and-thread-diagnostics.md`](../../../syllabus/02-java/concurrency/deadlock-race-conditions-and-thread-diagnostics.md) |
| 8 | T-410 — Virtual Threads (Project Loom) | [`virtual-threads.md`](../../../syllabus/02-java/concurrency/virtual-threads.md) |
| 9 | T-411 — Structured Concurrency | [`structured-concurrency.md`](../../../syllabus/02-java/concurrency/structured-concurrency.md) |
| 10 | T-412 — Scoped Values and ThreadLocal Migration | [`scoped-values-and-threadlocal-migration.md`](../../../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md) |
| 11 | T-413 — ThreadLocal-Mediated Classloader Leaks | [`threadlocal-mediated-classloader-leaks.md`](../../../syllabus/02-java/concurrency/threadlocal-mediated-classloader-leaks.md) |
| 12 | T-415 — VarHandles, Unsafe, and Their Replacement | [`varhandles-and-unsafe.md`](../../../syllabus/02-java/concurrency/varhandles-and-unsafe.md) |
| 13 | T-416/T-414 — Foreign Function & Memory API | [`foreign-function-and-memory-api.md`](../../../syllabus/02-java/concurrency/foreign-function-and-memory-api.md) |

## Hands-On Exercises

Real, compiled, executed demos exist for all 13 chapters:

- [`practice/java/week-09/concurrency-fundamentals/`](../../../practice/java/week-09/concurrency-fundamentals/) (T-401/T-402 — the `VisibilityDemo.java` non-volatile hang)
- [`practice/java/concurrency/locks-reentrant-rw-stamped/`](../../../practice/java/concurrency/locks-reentrant-rw-stamped/) (T-404)
- [`practice/java/concurrency/atomics-cas-and-aba/`](../../../practice/java/concurrency/atomics-cas-and-aba/) (T-405)
- [`practice/java/week-09/executors/`](../../../practice/java/week-09/executors/) (T-406)
- [`practice/java/concurrency/completablefuture-internals/`](../../../practice/java/concurrency/completablefuture-internals/) (T-407)
- [`practice/java/concurrency/forkjoinpool-and-work-stealing/`](../../../practice/java/concurrency/forkjoinpool-and-work-stealing/) (T-408)
- [`practice/java/week-09/deadlock-diagnostics/`](../../../practice/java/week-09/deadlock-diagnostics/) (T-409)
- [`practice/java/week-09/virtual-threads/`](../../../practice/java/week-09/virtual-threads/) (T-410)
- [`practice/java/concurrency/structured-concurrency/`](../../../practice/java/concurrency/structured-concurrency/) (T-411)
- [`practice/java/concurrency/scoped-values-and-threadlocal/`](../../../practice/java/concurrency/scoped-values-and-threadlocal/) (T-412)
- [`practice/java/concurrency/threadlocal-classloader-leak/`](../../../practice/java/concurrency/threadlocal-classloader-leak/) (T-413)
- [`practice/java/concurrency/varhandles-and-unsafe/`](../../../practice/java/concurrency/varhandles-and-unsafe/) (T-415)
- [`practice/java/concurrency/foreign-function-and-memory-api/`](../../../practice/java/concurrency/foreign-function-and-memory-api/) (T-416/T-414)

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "what does `volatile` guarantee and what does it not guarantee?"; "how do you size a thread pool differently for CPU-bound versus I/O-bound work?"; and "what does structured concurrency actually fix that plain `ExecutorService` submission does not?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a real thread dump (use the one from T-409's own practice material), identify the deadlocked threads and the lock-acquisition order that caused it, cold, in under 10 minutes.

## Review Checklist

- [ ] Completed all 13 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 8 of the 13 real demos listed above.
- [ ] Can explain, unprompted, why virtual threads do not eliminate the need to reason about thread-safety, only about thread-count scarcity.

## Completion Criteria

- [ ] Can explain happens-before with a correct code example, from memory.
- [ ] Can size a thread pool for two different stated workloads with a defensible reason for each.
- [ ] Can read a thread dump and identify a deadlock's root cause.
- [ ] Can state what `StructuredTaskScope` guarantees that manual `Future` cancellation does not.

## Retrospective

Note which of this week's 13 topics felt least intuitive — concurrency bugs are notoriously invisible until they aren't, so flag anything you're not fully confident explaining under pressure before Week 4 (JVM internals) builds on the same memory-model reasoning from the garbage collector's side.

## Next Week

[Week 4 — JVM Internals](../week-04/README.md).
