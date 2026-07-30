---
title: "T-1101 / T-1103 · Test Strategy, the Pyramid & Test Doubles"
topic_id: T-1101
domain: Testing
tier: Core
iwi: 7.00
prerequisites: []
unlocks: [T-1104]
week: 11
last_reviewed: 2026-07-30
canonical: ../../handbook/testing/test-strategy-and-test-doubles.md
---

# T-1101 / T-1103 · Test Strategy, the Pyramid & Test Doubles

**IWI 7.00 (T-1101) / 6.40 (T-1103) · Core tier**

**Canonical chapter:** [Test Strategy, the Pyramid, and Test Doubles](../../handbook/testing/test-strategy-and-test-doubles.md). This file is the Week 11 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the test run behind this summary is real, executed output from `practice/java/week-11/testing/src/PaymentServiceUnitTest.java` against a real Mockito mock, via JUnit 5's console launcher (no Maven/Gradle).

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A real mock, verifying behavior not just a return value](#3-a-real-mock-verifying-behavior-not-just-a-return-value)
4. [The pyramid, and the ice-cream-cone anti-pattern](#4-the-pyramid-and-the-ice-cream-cone-anti-pattern)
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

Test strategy decides what to test at which level — unit, integration, end-to-end. A test double (mock, stub, fake, spy) stands in for a real dependency so a unit test can isolate the logic under test. → [Definition and Purpose](../../handbook/testing/test-strategy-and-test-doubles.md#definition-and-purpose).

## 2. Why it exists

Without a strategy, teams under-test or build an ice-cream-cone suite. Test doubles exist because testing retry logic against a real, on-demand-failing dependency is normally impossible; a mock makes it a one-line setup. → [Definition and Purpose](../../handbook/testing/test-strategy-and-test-doubles.md#definition-and-purpose).

## 3. A real mock, verifying behavior not just a return value

Measured: `verify(gateway, times(3))` proves retry logic called its dependency the exact expected number of times with the exact arguments — something no return-value assertion alone could confirm. → [Internal Implementation](../../handbook/testing/test-strategy-and-test-doubles.md#internal-implementation) has the full trace.

## 4. The pyramid, and the ice-cream-cone anti-pattern

Many fast unit tests, fewer integration tests, very few end-to-end tests — a deliberate cost/coverage trade-off. Mock what's slow/external/non-deterministic; never mock the exact thing an integration test exists to verify. → [Core Concepts](../../handbook/testing/test-strategy-and-test-doubles.md#core-concepts).

## 5. Trade-offs

Unit tests are fast but blind to real dependency behavior; integration tests catch real boundary bugs at real cost; end-to-end tests catch whole-system issues but are slow and brittle. → [Trade-offs](../../handbook/testing/test-strategy-and-test-doubles.md#trade-offs).

## 6. Interview questions

1. Where do you draw the unit/integration line?
2. What does coverage percentage actually measure?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/testing/test-strategy-and-test-doubles.md#interview-questions).

## 7. Common mistakes

Treating coverage as a quality target; mocking the exact dependency an integration test exists to verify; building an ice-cream-cone suite. → [Common Mistakes](../../handbook/testing/test-strategy-and-test-doubles.md#common-mistakes).

## 8. Staff-level discussion

What to mock is itself an architectural decision — a codebase with business logic cleanly separated from IO makes unit testing natural; tangled code forces either brittle mocking or the ice-cream cone. → [Staff-Level Discussion](../../handbook/testing/test-strategy-and-test-doubles.md#interview-answer-framework).

## 9. Summary

A mock verifies both outcome and interaction, real and executed in 460ms. The pyramid's shape reflects a real cost/coverage trade-off; inverting it produces a slow, flaky suite for the sake of feeling thorough. → [Summary](../../handbook/testing/test-strategy-and-test-doubles.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/testing/test-strategy-and-test-doubles.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/testing/test-strategy-and-test-doubles.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/testing/test-strategy-and-test-doubles.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/testing/test-strategy-and-test-doubles.md#practice-exercises) and [Solutions](../../handbook/testing/test-strategy-and-test-doubles.md#solutions). Reproducible demo: `practice/java/week-11/testing/src/PaymentServiceUnitTest.java`.

## 14. Additional Reading

- [Martin Fowler — TestPyramid](https://martinfowler.com/bliki/TestPyramid.html)

## 15. Official References

- [Mockito documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
