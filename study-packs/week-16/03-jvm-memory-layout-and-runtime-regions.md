---
title: "T-301 · JVM Memory Layout and Runtime Regions"
topic_id: T-301
domain: JVM
tier: Foundation
iwi: 6.30
prerequisites: []
unlocks: [T-304, T-307, T-312]
week: 16
last_reviewed: 2026-07-31
canonical: ../../handbook/jvm/jvm-memory-layout-and-runtime-regions.md
---

# T-301 · JVM Memory Layout and Runtime Regions

**IWI 6.30 · Foundation tier · Very High interview frequency**

**Canonical chapter:** [JVM Memory Layout and Runtime Regions](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md). This file is the Week 16 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-16/memory-layout/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The measured evidence](#3-the-measured-evidence)
4. [Trade-offs](#4-trade-offs)
5. [Interview questions](#5-interview-questions)
6. [Common mistakes](#6-common-mistakes)
7. [Staff-level discussion](#7-staff-level-discussion)
8. [Summary](#8-summary)
9. [Key Takeaways](#9-key-takeaways)
10. [Cheat Sheet](#10-cheat-sheet)
11. [Flashcards](#11-flashcards)
12. [Practice Exercises](#12-practice-exercises)
13. [Additional Reading](#13-additional-reading)
14. [Official References](#14-official-references)

---

## 1. The concept

The JVM Specification defines distinct runtime data areas — heap (shared, objects), metaspace (shared, class metadata, native-memory-backed since Java 8), and per-thread JVM stacks — not one memory pool. Each has its own sizing flag and its own specific failure mode. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#definition-and-purpose).

## 2. Why it exists

Object data, class metadata, and call-frame bookkeeping have genuinely different access patterns and lifetimes — treating them as one pool would prevent independent tuning and diagnosis. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#definition-and-purpose).

## 3. The measured evidence

Real metaspace exhaustion: 5,275 dynamically-generated classes triggered `OutOfMemoryError: Metaspace` with heap usage at only 18MB of a 512MB max — proof the two regions are independent. Real stack-depth scaling: recursion depth reached before `StackOverflowError` scaled from 1,479 (Xss=256k) to 413,005 (Xss=8m), heap held constant throughout. → [Internal Implementation](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#internal-implementation) has the full trace.

## 4. Trade-offs

A small `-Xss` conserves memory per thread at the cost of a lower safe-recursion ceiling; total stack reservation scales with `threads × -Xss`, independent of heap size. → [Trade-offs](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#trade-offs).

## 5. Interview questions

1. A process throws `StackOverflowError` on one endpoint, but heap and overall memory look normal. What's happening, and what do you check?
2. A service throws `OutOfMemoryError: Metaspace` after running for a while, but heap occupancy stayed low. Diagnose it.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#interview-questions).

## 6. Common mistakes

Treating `-Xmx` as controlling total JVM memory; raising `-Xmx` in response to a `StackOverflowError` or metaspace OOM (zero effect on either); not accounting for `threads × -Xss` at high thread counts. → [Common Mistakes](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#common-mistakes).

## 7. Staff-level discussion

A migration to a much-higher-thread-count concurrency model has a real, calculable `threads × -Xss` memory cost that should be modeled explicitly, not discovered via an incident. → [Staff-Level Discussion](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#interview-answer-framework).

## 8. Summary

Heap, metaspace, and per-thread stacks are independently exhaustible regions, each with its own flag and failure mode. Measured directly: metaspace OOM at 5,275 classes with heap at 18MB/512MB; stack depth scaling 1,479 → 413,005 across `-Xss` values, heap constant. → [Summary](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#practice-exercises) and [Solutions](../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md#solutions). Reproducible demos: `practice/java/week-16/memory-layout/`.

## 13. Additional Reading

- [Oracle — Troubleshooting Memory Leaks (Native Memory Tracking)](https://docs.oracle.com/en/java/javase/21/troubleshoot/diagnostic-tools.html)

## 14. Official References

- [The Java Virtual Machine Specification, §2.5 — Runtime Data Areas](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5)
