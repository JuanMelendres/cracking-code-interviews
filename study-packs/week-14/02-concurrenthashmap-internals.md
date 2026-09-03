---
title: "T-205 · ConcurrentHashMap Internals"
topic_id: T-205
domain: Collections
tier: Advanced
iwi: 6.65
prerequisites: [T-201]
unlocks: []
week: 14
last_reviewed: 2026-07-30
canonical: ../../handbook/collections/concurrenthashmap-internals.md
---

# T-205 · ConcurrentHashMap Internals

**IWI 6.65 · Advanced tier · High interview frequency**

**Canonical chapter:** [ConcurrentHashMap Internals](../../syllabus/02-java/collections/concurrenthashmap-internals.md). This file is the Week 14 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-14/concurrenthashmap/src/ConcurrentHashMapDemo.java` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [HashMap corruption vs. ConcurrentHashMap correctness, measured](#3-hashmap-corruption-vs-concurrenthashmap-correctness-measured)
4. [The get-then-put trap and its fix, measured](#4-the-get-then-put-trap-and-its-fix-measured)
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

`ConcurrentHashMap` provides thread-safe concurrent access via fine-grained (per-bucket) internal locking, plus atomic compound operations (`merge`, `compute`, `computeIfAbsent`) for read-modify-write. → [Definition and Purpose](../../syllabus/02-java/collections/concurrenthashmap-internals.md#definition-and-purpose).

## 2. Why it exists

A plain `HashMap` corrupts silently under concurrent structural modification. `ConcurrentHashMap` fixes that AND provides atomic operations for the common counter/aggregation case. → [Definition and Purpose](../../syllabus/02-java/collections/concurrenthashmap-internals.md#definition-and-purpose).

## 3. HashMap corruption vs. ConcurrentHashMap correctness, measured

Measured: 8 threads doing 160,000 total disjoint `put()` calls corrupt a plain `HashMap` to size ~68,683; the identical workload on a `ConcurrentHashMap` always produces the correct size 160,000. → [Internal Implementation](../../syllabus/02-java/collections/concurrenthashmap-internals.md#internal-implementation) has the full trace.

## 4. The get-then-put trap and its fix, measured

Measured: a `get()`-then-`put()` counter increment on a `ConcurrentHashMap` loses updates under real concurrency — 26,212 instead of 160,000 expected. `merge("hits", 1, Integer::sum)` performs the whole read-modify-write atomically, producing the correct 160,000 every time. → [Internal Implementation](../../syllabus/02-java/collections/concurrenthashmap-internals.md#internal-implementation) has the full trace.

## 5. Trade-offs

Per-call thread safety doesn't compose into multi-call atomicity; `merge()`/`compute()` are strictly more correct than a manual get/put pair at equal or better performance. → [Trade-offs](../../syllabus/02-java/collections/concurrenthashmap-internals.md#trade-offs).

## 6. Interview questions

1. Your metrics dashboard undercounts under peak load but matches at low load. Why?
2. How does ConcurrentHashMap achieve thread safety without one lock for the whole map?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/collections/concurrenthashmap-internals.md#interview-questions).

## 7. Common mistakes

Using a plain HashMap under concurrent access; using `get()`+`put()` for a read-modify-write on a ConcurrentHashMap; wrapping ConcurrentHashMap in external synchronization "for safety." → [Common Mistakes](../../syllabus/02-java/collections/concurrenthashmap-internals.md#common-mistakes).

## 8. Staff-level discussion

The gap between "this class is thread-safe" and "this sequence of calls on this class is thread-safe" is one of the most common real production concurrency bugs, because the code compiles and looks reasonable. → [Staff-Level Discussion](../../syllabus/02-java/collections/concurrenthashmap-internals.md#interview-answer-framework).

## 9. Summary

A plain HashMap corrupts under concurrent writes; ConcurrentHashMap's individual operations are safe, but per-call safety doesn't compose — measured directly as lost updates from get()+put(). merge()/compute() fix this by performing the read-modify-write atomically. → [Summary](../../syllabus/02-java/collections/concurrenthashmap-internals.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/collections/concurrenthashmap-internals.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/collections/concurrenthashmap-internals.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/collections/concurrenthashmap-internals.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/collections/concurrenthashmap-internals.md#practice-exercises) and [Solutions](../../syllabus/02-java/collections/concurrenthashmap-internals.md#solutions). Reproducible demo: `practice/java/week-14/concurrenthashmap/src/ConcurrentHashMapDemo.java`.

## 14. Additional Reading

- Brian Goetz et al., *Java Concurrency in Practice*, Ch. 5

## 15. Official References

- [java.util.concurrent.ConcurrentHashMap (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)
