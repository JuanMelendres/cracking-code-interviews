---
title: "T-107 · Streams and Collectors"
topic_id: T-107
domain: JavaCore
tier: Core
iwi: 6.20
prerequisites: []
unlocks: []
week: 13
last_reviewed: 2026-07-30
canonical: ../../handbook/java-core/streams-and-collectors.md
---

# T-107 · Streams and Collectors

**IWI 6.20 · Core tier · Very High interview frequency**

**Canonical chapter:** [Streams and Collectors](../../syllabus/02-java/language-core/streams-and-collectors.md). This file is the Week 13 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-13/streams-collectors/src/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Laziness and short-circuiting, measured](#3-laziness-and-short-circuiting-measured)
4. [The toMap() duplicate-key trap and parallel-stream pitfalls, measured](#4-the-tomap-duplicate-key-trap-and-parallel-stream-pitfalls-measured)
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

A stream chains lazy intermediate operations (`filter`, `map`, `peek`) ending in one terminal operation (`collect`, `forEach`, `count`, `findFirst`). Collectors accumulate a stream's elements into a result. → [Definition and Purpose](../../syllabus/02-java/language-core/streams-and-collectors.md#definition-and-purpose).

## 2. Why it exists

Streams let transformations be expressed declaratively while letting the runtime decide how much work is actually necessary — short-circuiting terminal operations don't need every element. → [Definition and Purpose](../../syllabus/02-java/language-core/streams-and-collectors.md#definition-and-purpose).

## 3. Laziness and short-circuiting, measured

Measured: a pipeline with `peek()` prints nothing until a terminal operation runs; `findFirst()` stops pulling elements the moment it's satisfied (3 of 10 elements evaluated, not all 10). A consumed stream throws `IllegalStateException` on reuse. → [Internal Implementation](../../syllabus/02-java/language-core/streams-and-collectors.md#internal-implementation) has the full trace.

## 4. The toMap() duplicate-key trap and parallel-stream pitfalls, measured

Measured: `Collectors.toMap()` throws `IllegalStateException` on duplicate keys without a merge function. A plain `ArrayList` loses updates under `parallel().forEach()` (100,000 expected, ~24,000 actual). After proper JIT warmup, `parallel()` on a small, cheap-per-element workload measured ~6.6x slower than sequential. → [Internal Implementation](../../syllabus/02-java/language-core/streams-and-collectors.md#internal-implementation) has the full trace.

## 5. Trade-offs

Sequential streams have no coordination overhead; parallel streams can use multiple cores but cost real coordination overhead that can exceed the savings for small/cheap workloads. → [Trade-offs](../../syllabus/02-java/language-core/streams-and-collectors.md#trade-offs).

## 6. Interview questions

1. Your `parallel()` change made things slower. Why might that happen?
2. How would you accumulate results safely from a parallel stream instead of a shared list?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/language-core/streams-and-collectors.md#interview-questions).

## 7. Common mistakes

Assuming a pipeline executes as it's built; using `toMap()` without a merge function on data that can collide; writing to shared, non-thread-safe state inside `parallel().forEach()`. → [Common Mistakes](../../syllabus/02-java/language-core/streams-and-collectors.md#common-mistakes).

## 8. Staff-level discussion

Any concurrency mechanism has a fixed coordination cost that only pays for itself past some workload-size threshold that must be measured, not assumed. → [Staff-Level Discussion](../../syllabus/02-java/language-core/streams-and-collectors.md#interview-answer-framework).

## 9. Summary

Streams are lazy and short-circuiting, measured directly. `toMap()` needs an explicit merge function for duplicate keys. `parallel()` requires thread-safe accumulation and real measurement — not a free performance win. → [Summary](../../syllabus/02-java/language-core/streams-and-collectors.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/language-core/streams-and-collectors.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/language-core/streams-and-collectors.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/language-core/streams-and-collectors.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/language-core/streams-and-collectors.md#practice-exercises) and [Solutions](../../syllabus/02-java/language-core/streams-and-collectors.md#solutions). Reproducible demos: `practice/java/week-13/streams-collectors/src/`.

## 14. Additional Reading

- [The Java Tutorials — Aggregate Operations](https://docs.oracle.com/javase/tutorial/collections/streams/)

## 15. Official References

- [java.util.stream.Stream (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html)
- [java.util.stream.Collectors (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html)
