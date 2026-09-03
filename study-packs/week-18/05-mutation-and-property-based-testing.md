---
title: "T-1107 · Mutation and Property-Based Testing"
topic_id: T-1107
domain: Testing
tier: Experimental
iwi: 4.30
prerequisites: [T-1101, T-1102]
unlocks: []
week: 18
last_reviewed: 2026-08-02
canonical: ../../handbook/testing/mutation-and-property-based-testing.md
---

# T-1107 · Mutation and Property-Based Testing

**IWI 4.30 · Experimental tier · Rare interview frequency**

**Canonical chapter:** [Mutation and Property-Based Testing](../../syllabus/08-testing/mutation-and-property-based-testing.md). This file is the Week 18 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. This is also the last of the five topics that closes Testing to 8/8 register coverage this week.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-18/mutation-property/` — a genuine seeded bug found by a hand-rolled property-based test, and a real single-token mutant surviving a weak suite and killed by a strengthened one.

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

Property-based testing finds bugs in *code* via randomized inputs against a stated invariant; mutation testing finds gaps in a *test suite* by introducing real defects and checking whether existing tests notice. → [Mental Model](../../syllabus/08-testing/mutation-and-property-based-testing.md#mental-model).

## 2. Why it exists

Example-based tests are only as good as the examples chosen, and a developer's own unconscious bias in choosing them is real and predictable — both techniques exist to surface exactly what that bias misses. → [Core Concepts](../../syllabus/08-testing/mutation-and-property-based-testing.md#core-concepts).

## 3. The measured evidence

Real property-based test: two hand-picked example tests (both biased toward array `a` being longer) pass despite a real merge-sorted-arrays bug; a randomized property test finds the exact same bug on trial 2 with a concrete counterexample. Real mutation test: a weak suite (no boundary test) survives a real `>=`-to-`>` mutant; a strengthened suite (one added boundary test at exactly 100) kills it. → [Internal Implementation](../../syllabus/08-testing/mutation-and-property-based-testing.md#internal-implementation) has the full trace.

## 4. Trade-offs

Mutation testing's real computational cost argues for scoping it to high-risk modules, not full-codebase-every-commit application; property-based testing needs a genuine, precisely-statable invariant to be effective. → [Trade-offs](../../syllabus/08-testing/mutation-and-property-based-testing.md#trade-offs).

## 5. Interview questions

1. A module has 95% line coverage. Does that tell you the suite would catch a real bug? Why or why not?
2. When is a suggestion to add property-based testing valuable, and when might it not be worth the effort?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/08-testing/mutation-and-property-based-testing.md#interview-questions).

## 6. Common mistakes

Conflating mutation testing and property-based testing as the same technique; treating high line coverage as sufficient evidence of test quality; using an unlogged, freshly-randomized seed. → [Common Mistakes](../../syllabus/08-testing/mutation-and-property-based-testing.md#common-mistakes).

## 7. Staff-level discussion

Proposes deliberate scoping for mutation testing given its real cost, and recognizes the equivalent-mutant recognition problem as a genuine practical concern, not a hypothetical edge case. → [Staff-Level Discussion](../../syllabus/08-testing/mutation-and-property-based-testing.md#interview-answer-framework).

## 8. Summary

Both techniques surface what biased, hand-picked examples miss — one in the code, one in the tests. Measured directly: a real bug found on trial 2 by randomization; a real mutant surviving a weak suite and killed by one added boundary test. → [Summary](../../syllabus/08-testing/mutation-and-property-based-testing.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/08-testing/mutation-and-property-based-testing.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/08-testing/mutation-and-property-based-testing.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/08-testing/mutation-and-property-based-testing.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/08-testing/mutation-and-property-based-testing.md#practice-exercises) and [Solutions](../../syllabus/08-testing/mutation-and-property-based-testing.md#solutions). Reproducible demo: `practice/java/week-18/mutation-property/`.

## 13. Additional Reading

- [jqwik — Property-Based Testing for Java](https://jqwik.net/)

## 14. Official References

- [PIT (PITest) — Mutation Testing for Java](https://pitest.org/)
