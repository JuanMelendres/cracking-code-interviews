---
title: "T-207 · BlockingQueue Family and Producer-Consumer"
topic_id: T-207
domain: Collections
tier: Core
iwi: 5.80
prerequisites: []
unlocks: []
week: 14
last_reviewed: 2026-07-30
canonical: ../../handbook/collections/blockingqueue-family.md
---

# T-207 · BlockingQueue Family and Producer-Consumer

**IWI 5.80 · Core tier · High interview frequency**

**Canonical chapter:** [BlockingQueue Family and Producer-Consumer](../../syllabus/02-java/collections/blockingqueue-family.md). This file is the Week 14 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-14/blockingqueue/src/BlockingQueueDemo.java` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [ArrayBlockingQueue blocking, measured](#3-arrayblockingqueue-blocking-measured)
4. [SynchronousQueue's zero-capacity handoff, measured](#4-synchronousqueues-zero-capacity-handoff-measured)
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

A `BlockingQueue`'s `put()` waits for space when full and `take()` waits for an element when empty — its capacity IS its concurrency-control mechanism. → [Definition and Purpose](../../syllabus/02-java/collections/blockingqueue-family.md#definition-and-purpose).

## 2. Why it exists

Bounded queues provide natural backpressure — a producer that outpaces its consumer blocks rather than growing memory without limit. → [Definition and Purpose](../../syllabus/02-java/collections/blockingqueue-family.md#definition-and-purpose).

## 3. ArrayBlockingQueue blocking, measured

Measured: filling a capacity-2 queue, a third `put()` genuinely blocks (thread state `WAITING`) for ~313ms until a consumer's `take()` frees a slot. → [Internal Implementation](../../syllabus/02-java/collections/blockingqueue-family.md#internal-implementation) has the full trace.

## 4. SynchronousQueue's zero-capacity handoff, measured

Measured: `SynchronousQueue.put()` blocks for ~305ms until a `take()` is already waiting to receive that exact element — no internal storage at all, a direct handoff rather than a buffer. → [Internal Implementation](../../syllabus/02-java/collections/blockingqueue-family.md#internal-implementation) has the full trace.

## 5. Trade-offs

An unbounded queue never blocks the producer but risks unbounded memory growth; a bounded queue provides real backpressure at the cost of the producer sometimes waiting; SynchronousQueue is maximum backpressure with zero buffering. → [Trade-offs](../../syllabus/02-java/collections/blockingqueue-family.md#trade-offs).

## 6. Interview questions

1. Your ingestion service crashed with OutOfMemoryError during a downstream slowdown. What's your first suspect?
2. What's the actual difference between SynchronousQueue and a capacity-1 ArrayBlockingQueue?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/collections/blockingqueue-family.md#interview-questions).

## 7. Common mistakes

Using an unbounded queue by default; assuming SynchronousQueue behaves like a capacity-1 buffer; blocking indefinitely in a latency-sensitive path without a timeout. → [Common Mistakes](../../syllabus/02-java/collections/blockingqueue-family.md#common-mistakes).

## 8. Staff-level discussion

Bounded-queue backpressure is one instance of a general principle: absorbing overload internally converts a visible degradation into a catastrophic one once the implicit limit (memory) is exhausted. → [Staff-Level Discussion](../../syllabus/02-java/collections/blockingqueue-family.md#interview-answer-framework).

## 9. Summary

put() blocks when full, take() blocks when empty, both measured as genuine parking. SynchronousQueue takes this to zero capacity. Bounded capacity is deliberate backpressure; removing it defers failure to an eventual memory crash. → [Summary](../../syllabus/02-java/collections/blockingqueue-family.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/collections/blockingqueue-family.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/collections/blockingqueue-family.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/collections/blockingqueue-family.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/collections/blockingqueue-family.md#practice-exercises) and [Solutions](../../syllabus/02-java/collections/blockingqueue-family.md#solutions). Reproducible demo: `practice/java/week-14/blockingqueue/src/BlockingQueueDemo.java`.

## 14. Additional Reading

- Brian Goetz et al., *Java Concurrency in Practice*, Ch. 5.3

## 15. Official References

- [java.util.concurrent.BlockingQueue (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/BlockingQueue.html)
