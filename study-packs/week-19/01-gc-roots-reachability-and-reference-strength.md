---
title: "T-303 · GC Roots, Reachability, and Reference Strength"
topic_id: T-303
domain: JVM
tier: Core
iwi: 6.90
prerequisites: [T-306]
unlocks: []
week: 19
last_reviewed: 2026-08-02
canonical: ../../handbook/jvm/gc-roots-reachability-and-reference-strength.md
---

# T-303 · GC Roots, Reachability, and Reference Strength

**IWI 6.90 · Core tier · Very High interview frequency**

**Canonical chapter:** [GC Roots, Reachability, and Reference Strength](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md). This file is the Week 19 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Complements Week 9's G1-flavored `gc-fundamentals-and-log-analysis.md` chapter (which owns G1's region mechanics) with the theory that chapter doesn't cover: GC roots, formal reachability, and the reference-strength hierarchy.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-19/gc-roots-reachability/`.

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

An object is reachable if a chain of strong references traces from at least one GC root to it, computed by the mark phase's actual graph traversal — not a reference count. → [Mental Model](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#mental-model).

## 2. Why it exists

The reference-strength hierarchy (strong/soft/weak/phantom) lets code hold a reference without necessarily keeping an object strongly alive, each with a distinct, real clearing policy. → [Definition and Purpose](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#definition-and-purpose).

## 3. The measured evidence

Real demo: a strong reference survives `System.gc()`; an identical weak reference is cleared immediately once its only strong path is removed; a soft reference survives the identical operation under no memory pressure; a phantom reference never returns the object at all, only enqueueing to a `ReferenceQueue` after collection. → [Internal Implementation](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#internal-implementation) has the full trace.

## 4. Trade-offs

Soft references give pressure-aware caching for free but with an implementation-defined exact heuristic; weak references give precise, predictable immediate clearing — exactly wrong for pressure-aware caching, exactly right for non-lifecycle-affecting tracking. → [Trade-offs](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#trade-offs).

## 5. Interview questions

1. A `WeakHashMap`-based cache empties much faster than expected with plenty of heap free. What's going on?
2. Why is `finalize()` considered a legacy anti-pattern, and what replaced it?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#interview-questions).

## 6. Common mistakes

Describing GC eligibility as "no longer referenced" without naming roots; using `WeakHashMap` expecting pressure-aware caching; relying on `finalize()` for cleanup timing. → [Common Mistakes](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#common-mistakes).

## 7. Staff-level discussion

Recognizes the generational hypothesis as the general theoretical basis for generational collection, independent of any specific collector's implementation. → [Staff-Level Discussion](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#interview-answer-framework).

## 8. Summary

Reachability is root-traced graph connectivity, not a count. Measured directly: identical post-unreachability `System.gc()` clears a weak reference immediately but leaves a soft reference intact under normal pressure. → [Summary](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#practice-exercises) and [Solutions](../../handbook/jvm/gc-roots-reachability-and-reference-strength.md#solutions). Reproducible demo: `practice/java/week-19/gc-roots-reachability/`.

## 13. Additional Reading

- [`java.lang.ref` package documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ref/package-summary.html)

## 14. Official References

- [`java.lang.ref` package documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ref/package-summary.html)
