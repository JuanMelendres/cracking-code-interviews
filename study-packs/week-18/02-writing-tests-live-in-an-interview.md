---
title: "T-1108 · Writing Tests Live in an Interview"
topic_id: T-1108
domain: Testing
tier: Core
iwi: 5.80
prerequisites: [T-1101]
unlocks: []
week: 18
last_reviewed: 2026-08-02
canonical: ../../handbook/testing/writing-tests-live-in-an-interview.md
---

# T-1108 · Writing Tests Live in an Interview

**IWI 5.80 · Core tier · Moderate interview frequency**

**Canonical chapter:** [Writing Tests Live in an Interview](../../syllabus/08-testing/writing-tests-live-in-an-interview.md). This file is the Week 18 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the four-step red-green-refactor cycle behind this summary is real, executed output from `practice/java/week-18/live-coding-tdd/`.

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

Live-coding test-first is a narrated red-green-refactor loop, not a silent one — the process, not just the final code, is what a Senior/Staff live-coding round evaluates. → [Mental Model](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#mental-model).

## 2. Why it exists

The format specifically tests whether testing discipline survives time pressure, since production incidents also happen under time pressure — a take-home assignment has no equivalent signal. → [Why This Matters in Interviews](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#why-this-matters-in-interviews).

## 3. The measured evidence

Real four-step cycle on a run-length-encoding kata: RED (empty-string test, wrong impl) → GREEN (minimal impl) → RED again (new test, old impl insufficient) → GREEN (full impl, all 3 tests pass) — real captured JUnit 5 console output at every step. → [Internal Implementation](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#internal-implementation) has the full trace.

## 4. Trade-offs

Narrating every step costs visible pace under a ticking clock but is what makes the session evaluable at all — a fast, silent session gives an evaluator far less signal than a slower, narrated one. → [Trade-offs](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#trade-offs).

## 5. Interview questions

1. Implement, test-first, a function returning the second-largest distinct value in an array. Narrate each step.
2. Midway through a live kata, your test fails in a way you don't immediately understand. Walk through what you'd do.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#interview-questions).

## 6. Common mistakes

Writing the full implementation first and adding tests afterward; going silent while coding; panicking at an unexpected failure instead of reading the actual message. → [Common Mistakes](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#common-mistakes).

## 7. Staff-level discussion

Explicitly communicates scope trade-offs when time-constrained, mirroring real production trade-off communication under deadline pressure, rather than silently rushing. → [Staff-Level Discussion](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#interview-answer-framework).

## 8. Summary

A narrated, incremental red-green-refactor loop is the actual skill being evaluated. Measured directly: four real steps, each with captured pass/fail console output, building a working RLE implementation entirely test-first. → [Summary](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#practice-exercises) and [Solutions](../../syllabus/08-testing/writing-tests-live-in-an-interview.md#solutions). Reproducible demo: `practice/java/week-18/live-coding-tdd/`.

## 13. Additional Reading

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

## 14. Official References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
