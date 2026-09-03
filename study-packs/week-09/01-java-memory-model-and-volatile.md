---
title: "T-401 / T-402 · Java Memory Model & volatile"
topic_id: T-401
domain: Concurrency
tier: Advanced
iwi: 7.75
prerequisites: []
unlocks: [T-403, T-405, T-409]
week: 9
last_reviewed: 2026-07-30
canonical: ../../handbook/concurrency/java-memory-model-and-volatile.md
---

# T-401 / T-402 · Java Memory Model & volatile

**IWI 7.75 (T-401) / 6.60 (T-402) · Advanced / Core tier · deepest single technical topic in the handbook**

**Canonical chapter:** [Java Memory Model and volatile](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md). This file is the Week 9 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `08-week-9-checkpoint.md` and `MANIFEST.md`'s errata table cite §3 directly.

**Errata correction, stated explicitly:** the source material described `volatile` as "prevents caching" — a hardware-level framing that is not what the Java Memory Model (JMM) actually specifies. §3 reproduces the real, measured consequence of getting this wrong, then explains the correct model.

**Verification note:** the visibility trace behind this summary is real, executed output from `practice/java/week-09/concurrency-fundamentals/src/VisibilityDemo.java` — a genuine unbounded hang (5+ seconds, self-terminated by a bounded `join()`), reproduced consistently across three runs, not a one-off fluke.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Visibility, measured: the "prevents caching" misconception, killed with data](#3-visibility-measured-the-prevents-caching-misconception-killed-with-data)
4. [Happens-before, not caching](#4-happens-before-not-caching)
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

The JMM specifies what values a thread is guaranteed to observe when it reads memory another thread wrote. `volatile` is one of its tools for establishing a happens-before relationship between a write on one thread and a read on another. → [Definition and Purpose](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#definition-and-purpose).

## 2. Why it exists

Without the JMM, "correct" multi-threaded code would depend on undefined behavior that happens to work on today's specific JIT and CPU. → [Definition and Purpose](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#definition-and-purpose).

## 3. Visibility, measured: the "prevents caching" misconception, killed with data

Measured: a non-volatile flag update was never observed by a spinning worker thread across three runs (5+ second hang each time); the volatile version stopped instantly. The mechanism is a JIT compiler optimization (loop-invariant read hoisting), not a CPU cache-coherence problem. → [Internal Implementation](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#internal-implementation) has the full trace.

## 4. Happens-before, not caching

Five practical rules: program order, monitor lock, volatile variable, thread start/join, final field. Double-checked locking needs `volatile` on the singleton field specifically to prevent observing a partially-constructed object. → [Core Concepts](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#core-concepts).

## 5. Trade-offs

A plain field is cheapest but has no visibility guarantee; `volatile` adds visibility and ordering but not atomicity for compound operations; `synchronized` adds mutual exclusion at the cost of contention and deadlock risk. → [Trade-offs](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#trade-offs).

## 6. Interview questions

1. Why does double-checked locking break without `volatile`?
2. Is `volatile int count; count++;` from multiple threads safe?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#interview-questions).

## 7. Common mistakes

Describing `volatile` as being about CPU/hardware caching; believing it makes multi-step operations atomic; treating happens-before as a total order. → [Common Mistakes](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#common-mistakes).

## 8. Staff-level discussion

The JMM is the reason "it worked on my machine" is a legitimate, dangerous failure mode — an unsynchronized pattern can work under one JIT tier and fail after an upgrade or longer run. → [Staff-Level Discussion](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#interview-answer-framework).

## 9. Summary

`volatile` establishes a happens-before edge — not a caching mechanism. Getting this wrong isn't theoretical: the measured demo reliably reproduces a genuine 5+ second visibility failure caused by a real, common JIT optimization. → [Summary](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#practice-exercises) and [Solutions](../../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#solutions). Reproducible demo: `practice/java/week-09/concurrency-fundamentals/src/VisibilityDemo.java`.

## 14. Additional Reading

- [Java Language Specification §17.4 — Memory Model](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.4)

## 15. Official References

- [JSR-133: JavaTM Memory Model and Thread Specification Revision](https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html) — the FAQ written by the JMM's own authors, still the clearest primary source
