---
title: "Mid → Senior, Week 1 — Concurrency Internals"
document_type: study-pack
week: 1
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 1 — Concurrency Internals

## Weekly Outcome

By the end of this week you can explain the Java Memory Model's happens-before guarantees without hand-waving, size a thread pool correctly for a stated workload with a reason, and diagnose a deadlock from a real thread dump.

## Why This Week Matters

Concurrency is the domain [`syllabus/00-overview/learning-paths/mid-to-senior.md`](../../../syllabus/00-overview/learning-paths/mid-to-senior.md) sequences first — almost every other domain this pack covers (Spring's request handling, Kafka consumers, distributed systems) assumes correct concurrent reasoning underneath it.

## Prerequisites

[Junior → Mid](../../junior-to-mid/README.md) complete, in particular comfortable with basic Java syntax and OOP. No prior concurrency-specific study assumed.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | Java Memory Model and volatile |
| Wed–Thu | Executors and Thread Pool Sizing |
| Fri–Sat | Deadlock, Race Conditions, and Thread Diagnostics |
| Sun | Review checklist below |

## Required Reading

[`syllabus/02-java/INDEX.md`](../../../syllabus/02-java/INDEX.md) — this week's three priority topics (Java Memory Model and volatile; Executors and Thread Pool Sizing; Deadlock, Race Conditions, and Thread Diagnostics), as named in the learning path's own sequence table. Read each chapter through its L3 sections.

## Hands-On Exercises

Real, compiled demos exist under [`practice/java/concurrency/`](../../../practice/java/concurrency/) — `locks-reentrant-rw-stamped/`, `atomics-cas-and-aba/`, `forkjoinpool-and-work-stealing/`, `completablefuture-internals/`, `structured-concurrency/`, `scoped-values-and-threadlocal/`, `threadlocal-classloader-leak/`, `varhandles-and-unsafe/`, `foreign-function-and-memory-api/`. Follow the link from each priority chapter to its own matching demo rather than guessing which directory pairs with which topic.

## Production Cookbook Cross-Reference

- [`opposite-order-lock-acquisition-deadlock-in-a-funds-transfer.md`](../../../production-cookbook/opposite-order-lock-acquisition-deadlock-in-a-funds-transfer.md)
- [`lock-ordering-deadlock-under-peak-load.md`](../../../production-cookbook/lock-ordering-deadlock-under-peak-load.md)

Read both after finishing the Deadlock chapter and confirm you can restate each diagnosis without looking, per the learning path's own completion criteria.

## Interview Answer Drills

Answer, out loud: "what does `volatile` actually guarantee, and what does it not guarantee?" and "how do you size a thread pool for a CPU-bound versus an I/O-bound workload?" before checking the chapters' own expected answers.

## Coding Problems

None dedicated this week — this pack is domain-depth focused, not DSA pattern practice (that track lives in [Junior → Mid](../../junior-to-mid/README.md) Weeks 4–5).

## System Design Exercise

None this week.

## Behavioral Exercise

None this week — this pack is technical-fundamentals-focused; behavioral preparation lives in [`syllabus/20-interview-preparation/behavioral/`](../../../syllabus/20-interview-preparation/behavioral/) as its own track.

## Mock Interview

Self-check: given a real thread dump (use one from the Deadlock chapter's own practice material), identify the deadlocked threads and the lock-acquisition order that caused it, cold, in under 10 minutes.

## Review Checklist

- [ ] Completed all three chapters' own L3 Mastery Checklists.
- [ ] Reproduced at least 3 of the real concurrency demos listed above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can explain happens-before, unprompted, with a correct code example.
- [ ] Can size a thread pool for two different stated workloads with a defensible reason for each.
- [ ] Can read a thread dump and identify a deadlock's root cause.

## Retrospective

Note which of this week's three topics felt least intuitive — concurrency bugs are notoriously invisible until they aren't, so flag anything you're not fully confident explaining under pressure before moving to Week 2.

## Next Week

[Week 2 — JVM Internals](../week-02/README.md).
