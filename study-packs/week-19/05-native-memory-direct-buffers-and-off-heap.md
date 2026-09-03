---
title: "T-311 · Native Memory, Direct Buffers, and Off-Heap"
topic_id: T-311
domain: JVM
tier: Advanced
iwi: 4.70
prerequisites: [T-301]
unlocks: []
week: 19
last_reviewed: 2026-08-02
canonical: ../../handbook/jvm/native-memory-direct-buffers-and-off-heap.md
---

# T-311 · Native Memory, Direct Buffers, and Off-Heap

**IWI 4.70 · Advanced tier · Occasional interview frequency**

**Canonical chapter:** [Native Memory, Direct Buffers, and Off-Heap](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md). This file is the Week 19 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-19/native-memory/` — a real, distinct direct-buffer OOM and real NMT category evidence.

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

`-Xmx` bounds only the Java heap — thread stacks, metaspace, code cache, and direct buffers all live outside it, each with their own separate budget. → [Mental Model](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#mental-model).

## 2. Why it exists

Direct buffers eliminate a heap-to-native copy for OS-level I/O, since I/O calls need a fixed address a movable heap object can't directly provide. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#definition-and-purpose).

## 3. The measured evidence

Real: a process with `-Xmx32m` allocated a full 256MB of direct memory (8x the heap limit) before a distinct `OutOfMemoryError: Direct buffer memory` at exactly the configured `-XX:MaxDirectMemorySize`. Real NMT evidence: `Java Heap` reported exactly 64MB (matching `-Xmx64m`), `Other` reported exactly 100MB with 10 allocations (matching 10 direct buffers exactly) — tracked entirely separately. → [Internal Implementation](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#internal-implementation) has the full trace.

## 4. Trade-offs

Direct buffers' I/O benefit is real and specific; the cost is real operational complexity — a separate budget, separate monitoring, and a distinct OOM failure mode invisible to heap tooling. → [Trade-offs](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#trade-offs).

## 5. Interview questions

1. A container with limit = `-Xmx` gets OOMKilled despite heap usage never approaching the max. What's the likely issue?
2. Why are direct `ByteBuffer`s faster for I/O specifically?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#interview-questions).

## 6. Common mistakes

Assuming `-Xmx` bounds total process memory; using direct buffers for non-I/O data; diagnosing direct-memory issues with heap-only tools. → [Common Mistakes](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#common-mistakes).

## 7. Staff-level discussion

Treats container memory-limit sizing as requiring explicit, measured accounting for every non-heap region, not a generic rule of thumb. → [Staff-Level Discussion](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#interview-answer-framework).

## 8. Summary

`-Xmx` never bounded total process memory. Measured directly: 256MB direct memory allocated on a 32MB heap before a distinct OOM, and NMT confirming exact separate accounting. → [Summary](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#practice-exercises) and [Solutions](../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md#solutions). Reproducible demo: `practice/java/week-19/native-memory/`.

## 13. Additional Reading

- [`java.nio.ByteBuffer` documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html)

## 14. Official References

- [`java.nio.ByteBuffer` documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html)
