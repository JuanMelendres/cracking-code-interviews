---
title: "T-101 · equals(), hashCode(), and Comparable Contracts"
topic_id: T-101
domain: JavaCore
tier: Foundation
iwi: 5.90
prerequisites: []
unlocks: []
week: 13
last_reviewed: 2026-07-30
canonical: ../../handbook/java-core/equals-hashcode-and-comparable-contracts.md
---

# T-101 · equals(), hashCode(), and Comparable Contracts

**IWI 5.90 · Foundation tier · Very High interview frequency**

**Canonical chapter:** [equals(), hashCode(), and Comparable Contracts](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md). This file is the Week 13 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-13/equality-contracts/src/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Broken equals()/hashCode(), measured](#3-broken-equalshashcode-measured)
4. [Comparable inconsistent with equals(), measured](#4-comparable-inconsistent-with-equals-measured)
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

`equals()` answers "are these the same value"; `hashCode()` answers "which bucket." Every hash-based collection assumes those answers never disagree. `Comparable`'s `compareTo()` governs ordering and, for `TreeSet`/`TreeMap`, storage-level duplicate detection. → [Definition and Purpose](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#definition-and-purpose).

## 2. Why it exists

If two objects are `equals()`, they must have the same `hashCode()` — break that and a collection doesn't error, it just looks in the wrong bucket forever. → [Definition and Purpose](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#definition-and-purpose).

## 3. Broken equals()/hashCode(), measured

Measured: two objects that are `equals()`-equal but have different `hashCode()` (identity hash, since only `equals()` was overridden) cause `HashSet` to fail to detect the duplicate — `contains()` returns false and the set ends up with size 2 instead of 1. The fixed version, both methods overridden consistently, correctly deduplicates. → [Internal Implementation](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#internal-implementation) has the full trace.

## 4. Comparable inconsistent with equals(), measured

Measured: two genuinely different products sharing a price (`equals()` says false, `compareTo()` says 0) — a `TreeSet` silently drops the second product, since `TreeSet`/`TreeMap` use `compareTo()` exclusively for duplicate detection, ignoring `equals()` entirely. → [Internal Implementation](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#internal-implementation) has the full trace.

## 5. Trade-offs

Overriding both `equals()`/`hashCode()` together keeps hash-based collections correct at the cost of keeping them in sync; a `Comparable` inconsistent with `equals()` can simplify a specific sort but must never back a `TreeSet`/`TreeMap` where distinctness matters. → [Trade-offs](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#trade-offs).

## 6. Interview questions

1. Your `HashSet` isn't deduplicating records that look identical. What do you check first?
2. Why would a `TreeSet` silently drop an element that isn't actually a duplicate?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#interview-questions).

## 7. Common mistakes

Overriding `equals()` without `hashCode()` (or via a partial IDE regeneration); implementing `Comparable` inconsistently with `equals()` and storing instances in a sorted collection where distinctness matters. → [Common Mistakes](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#common-mistakes).

## 8. Staff-level discussion

A broken equals/hashCode contract is one of the clearest examples of a violation that fails silently rather than loudly, surfacing only through a downstream symptom. → [Staff-Level Discussion](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#interview-answer-framework).

## 9. Summary

Equal objects must have equal hash codes, measured directly as a silent HashSet failure when violated. TreeSet/TreeMap use compareTo() exclusively, also measured as a silent drop when inconsistent with equals(). → [Summary](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#practice-exercises) and [Solutions](../../syllabus/02-java/language-core/equals-hashcode-and-comparable-contracts.md#solutions). Reproducible demos: `practice/java/week-13/equality-contracts/src/`.

## 14. Additional Reading

- Joshua Bloch, *Effective Java*, Item 10 and Item 11

## 15. Official References

- [java.lang.Object (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html)
- [java.lang.Comparable (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Comparable.html)
