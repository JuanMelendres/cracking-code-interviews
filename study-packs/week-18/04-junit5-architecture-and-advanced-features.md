---
title: "T-1102 · JUnit 5 Architecture and Advanced Features"
topic_id: T-1102
domain: Testing
tier: Foundational
iwi: 5.00
prerequisites: [T-1101]
unlocks: []
week: 18
last_reviewed: 2026-08-02
canonical: ../../handbook/testing/junit5-architecture-and-advanced-features.md
---

# T-1102 · JUnit 5 Architecture and Advanced Features

**IWI 5.00 · Foundational tier · Moderate interview frequency**

**Canonical chapter:** [JUnit 5 Architecture and Advanced Features](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md). This file is the Week 18 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-18/junit5-features/`, including real tag-filtered execution counts.

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

JUnit 5 splits into Platform (framework-agnostic launcher), Jupiter (modern model), and Vintage (JUnit 3/4 compatibility) — a deliberate architectural reset from JUnit 4's fragmented Runner/Rule mechanisms into one composable `Extension` interface family. → [Mental Model](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#mental-model).

## 2. Why it exists

The three-module split lets each concern evolve independently and specifically enables incremental, not big-bang, JUnit 4-to-5 migration via Vintage. → [Definition and Purpose](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#definition-and-purpose).

## 3. The measured evidence

Real ten-test class: 5 `@ParameterizedTest` cases, 4 `@TestFactory` dynamic tests, 1 `@Nested` test, plus a custom extension printing real per-test timing — all pass. Real tag-filtering: filtering to `slow` selects exactly 1 of 10 tests; filtering to `fast` selects exactly 9 of 10 — genuine execution partitioning, not just labeling. → [Internal Implementation](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#internal-implementation) has the full trace.

## 4. Trade-offs

`@TestFactory`'s runtime flexibility costs some of `@ParameterizedTest`'s convenient built-in data-source annotations — reserve it for genuinely runtime-computed case sets. → [Trade-offs](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#trade-offs).

## 5. Interview questions

1. When would you choose `@TestFactory` over `@ParameterizedTest`, and why not always use the more flexible option?
2. How would you split fast/slow CI execution from a single test source without duplicating test trees?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#interview-questions).

## 6. Common mistakes

Choosing `@TestFactory` for fixed, known cases; forgetting to register a correct extension via `@ExtendWith`; scattering tag-name string literals with no shared constant. → [Common Mistakes](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#common-mistakes).

## 7. Staff-level discussion

Explains Vintage's practical migration payoff as a deliberate design choice, and recognizes composition-based extensions as directly solving JUnit 4's deep-base-class-hierarchy pain point. → [Staff-Level Discussion](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#interview-answer-framework).

## 8. Summary

A composable extension model and a three-module architecture enabling incremental migration. Measured directly: 10/10 tests passing across parameterized/dynamic/nested/extension features, and real 1-of-10 vs. 9-of-10 tag-filtered execution. → [Summary](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#practice-exercises) and [Solutions](../../syllabus/08-testing/junit5-architecture-and-advanced-features.md#solutions). Reproducible demo: `practice/java/week-18/junit5-features/`.

## 13. Additional Reading

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

## 14. Official References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
