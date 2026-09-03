---
title: "T-104 · Generics: Erasure, Variance, and PECS"
topic_id: T-104
domain: JavaCore
tier: Core
iwi: 5.85
prerequisites: []
unlocks: []
week: 13
last_reviewed: 2026-07-30
canonical: ../../handbook/java-core/generics-erasure-and-pecs.md
---

# T-104 · Generics: Erasure, Variance, and PECS

**IWI 5.85 · Core tier · High interview frequency**

**Canonical chapter:** [Generics: Erasure, Variance, and PECS](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md). This file is the Week 13 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-13/generics-erasure/src/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Type erasure and heap pollution, measured](#3-type-erasure-and-heap-pollution-measured)
4. [PECS applied and violated, measured](#4-pecs-applied-and-violated-measured)
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

Type erasure removes generic type information after compile time — `List<String>` and `List<Integer>` are literally the same class at runtime. PECS ("Producer Extends, Consumer Super") is the rule for choosing wildcard bounds safely. → [Definition and Purpose](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#definition-and-purpose).

## 2. Why it exists

Erasure gives compile-time type safety with full binary compatibility to pre-generics code, at the cost of no runtime type information. PECS exists because the compiler needs a rule to know when a wildcarded type is safe to read from versus write to. → [Definition and Purpose](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#definition-and-purpose).

## 3. Type erasure and heap pollution, measured

Measured: `List<String>.getClass() == List<Integer>.getClass()` is true. Defeating a generic via an unchecked cast lets an incompatible value in silently; the `ClassCastException` only appears later, at a `get()` call — not at the cast itself. A bridge method, proven via reflection, is the compiler's mechanism for reconciling an erased interface signature with a typed implementation. → [Internal Implementation](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#internal-implementation) has the full trace.

## 4. PECS applied and violated, measured

Measured: a producer-extends method accepts `List<Integer>`, `List<Double>`, `List<Number>` for reading; a consumer-super method accepts `List<Integer>`, `List<Number>`, `List<Object>` for writing. Writing to a `List<? extends Number>` is rejected at compile time — the compiler can't prove it's safe. → [Internal Implementation](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#internal-implementation) has the full trace.

## 5. Trade-offs

A fully generic API enforces type safety throughout but can't bridge to raw-typed legacy APIs without an unchecked cast, which defers any type violation to a later, harder-to-trace failure unless validated immediately. → [Trade-offs](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#trade-offs).

## 6. Interview questions

1. Why does the `ClassCastException` show up at `get()` instead of at the unchecked cast itself?
2. Why can't you write to a `List<? extends Number>`?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#interview-questions).

## 7. Common mistakes

Assuming generic type information is available at runtime; using an unchecked cast without an immediate runtime validation; confusing which side of PECS applies to reading versus writing. → [Common Mistakes](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#common-mistakes).

## 8. Staff-level discussion

Any bypass of the type system (an unchecked cast, a raw type) can let a bug travel arbitrarily far from its actual cause before surfacing — every such boundary needs an immediate, explicit validation. → [Staff-Level Discussion](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#interview-answer-framework).

## 9. Summary

Generics are erased after compile time, measured directly via `getClass()`. A defeated generic fails at read time, not insert time, measured directly. PECS resolves wildcard variance safely — writing to a producer wildcard is rejected at compile time. → [Summary](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#practice-exercises) and [Solutions](../../syllabus/02-java/language-core/generics-erasure-and-pecs.md#solutions). Reproducible demos: `practice/java/week-13/generics-erasure/src/`.

## 14. Additional Reading

- [The Java Tutorials — Generics](https://docs.oracle.com/javase/tutorial/java/generics/index.html)

## 15. Official References

- [Java Language Specification §4.6 — Type Erasure](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.6)
