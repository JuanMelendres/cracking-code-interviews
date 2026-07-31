---
title: "T-103 · Immutability and Defensive Copying"
topic_id: T-103
domain: JavaCore
tier: Foundation
iwi: 5.40
prerequisites: []
unlocks: []
week: 13
last_reviewed: 2026-07-30
canonical: ../../handbook/java-core/immutability-and-defensive-copying.md
---

# T-103 · Immutability and Defensive Copying

**IWI 5.40 · Foundation tier · High interview frequency**

**Canonical chapter:** [Immutability and Defensive Copying](../../handbook/java-core/immutability-and-defensive-copying.md). This file is the Week 13 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the trace behind this summary is real, executed output from `practice/java/week-13/immutability/src/MutableLeakDemo.java` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Two leaks, measured](#3-two-leaks-measured)
4. [The fix, measured](#4-the-fix-measured)
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

A class is only as immutable as its most permissive point of entry or exit for mutable state — `final` fields prevent reassignment, not mutation of the object referenced. → [Definition and Purpose](../../handbook/java-core/immutability-and-defensive-copying.md#definition-and-purpose).

## 2. Why it exists

Shared mutable state is a major bug source; immutable objects are automatically thread-safe with no synchronization needed, since there's no mutation to race on. → [Definition and Purpose](../../handbook/java-core/immutability-and-defensive-copying.md#definition-and-purpose).

## 3. Two leaks, measured

Measured: a constructor storing a `Date` reference directly lets the caller mutate it after construction, changing the "immutable" object's state. A getter returning a live `List` reference lets external code mutate the object's own internal list directly. → [Internal Implementation](../../handbook/java-core/immutability-and-defensive-copying.md#internal-implementation) has the full trace.

## 4. The fix, measured

Measured: defensively copying on both construction and retrieval resists the first leak entirely; using `List.copyOf()` on the getter rejects mutation outright with `UnsupportedOperationException`, stronger than a plain copy. → [Internal Implementation](../../handbook/java-core/immutability-and-defensive-copying.md#internal-implementation) has the full trace.

## 5. Trade-offs

Storing a caller's reference directly costs nothing but isn't actually immutable; a defensive copy is independent but can still be mutated if handed out directly; `List.copyOf()` is independent AND rejects mutation, at a small one-time copy cost. → [Trade-offs](../../handbook/java-core/immutability-and-defensive-copying.md#trade-offs).

## 6. Interview questions

1. Your class has only final fields and no setters. Is it immutable? How would you check?
2. What's the difference between defensively copying into a new ArrayList versus using List.copyOf()?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/java-core/immutability-and-defensive-copying.md#interview-questions).

## 7. Common mistakes

Believing final fields alone make a class immutable; copying on construction but not on the getter (or vice versa). → [Common Mistakes](../../handbook/java-core/immutability-and-defensive-copying.md#common-mistakes).

## 8. Staff-level discussion

Genuine immutability requires zero synchronization to share safely across threads, connecting directly to the Java Memory Model's safe-publication guarantee for final fields of properly constructed objects. → [Staff-Level Discussion](../../handbook/java-core/immutability-and-defensive-copying.md#interview-answer-framework).

## 9. Summary

A class with only final fields is not automatically immutable — mutability can leak through a constructor or a getter, both measured directly. Defensive copying at both boundaries, or List.copyOf(), closes the leak. → [Summary](../../handbook/java-core/immutability-and-defensive-copying.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/java-core/immutability-and-defensive-copying.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/java-core/immutability-and-defensive-copying.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/java-core/immutability-and-defensive-copying.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/java-core/immutability-and-defensive-copying.md#practice-exercises) and [Solutions](../../handbook/java-core/immutability-and-defensive-copying.md#solutions). Reproducible demo: `practice/java/week-13/immutability/src/MutableLeakDemo.java`.

## 14. Additional Reading

- Joshua Bloch, *Effective Java*, Item 17 and Item 50

## 15. Official References

- [java.util.List#copyOf (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/List.html#copyOf(java.util.Collection))
