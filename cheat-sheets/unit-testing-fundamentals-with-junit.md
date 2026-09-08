---
title: "Cheat Sheet: Unit Testing Fundamentals with JUnit"
slug: unit-testing-fundamentals-with-junit
document_type: cheat-sheet
domain: 08-testing
topic_id: T-2204
canonical: ../syllabus/08-testing/unit-testing-fundamentals-with-junit.md
last_updated: 2026-09-07
---

# Unit Testing Fundamentals with JUnit

**Canonical chapter:** [`syllabus/08-testing/unit-testing-fundamentals-with-junit.md`](../syllabus/08-testing/unit-testing-fundamentals-with-junit.md)

## Core Mental Model

A unit test is a small, automated piece of code that calls a method with a specific input and checks the output — replacing manual, repetitive eyeballing of a program's behavior. `@BeforeEach` gives every test a fresh, identical starting state, which is what makes tests independent of each other and of execution order.

## Essential Definitions

- **`@Test`** — marks a method JUnit runs automatically, with a pass/fail outcome.
- **`@BeforeEach`** — runs before every `@Test` in the class; the mechanism behind test isolation.
- **`assertThrows(Type.class, () -> code())`** — the only correct way to test an exception is thrown; fails the test itself if nothing (or the wrong type) is thrown.
- **`@ParameterizedTest`** — runs the same test logic once per supplied value (`@ValueSource`, `@CsvSource`), instead of near-identical `@Test` methods.

## Decision Table

| Situation | Use |
|---|---|
| Testing that an exception is thrown | `assertThrows`, never manual `try`/`catch` + `fail()` |
| 2+ `@Test` methods differing only in one literal value | `@ParameterizedTest` |
| Setup every test genuinely needs | `@BeforeEach`, never a field initialized once outside it |
| Comparing floating-point values | `assertEquals(expected, actual, delta)` — exact equality can fail on identical-in-theory values |

## Common Pitfalls

- Sharing mutable state across tests (a `static` field, or a field set once outside `@BeforeEach`) — makes a test's outcome depend on execution order, producing intermittent CI failures that look like flakiness in the code under test.
- Manual `try`/`catch` + `fail()` for exception testing instead of `assertThrows` — easy to write incorrectly (forgetting the `fail()` call).
- Writing several near-identical `@Test` methods instead of one `@ParameterizedTest`.
- Assuming a failing assertion tells you which side is wrong — it only tells you the two sides disagreed; `expected: <6> but was: <5>` still requires knowing which value is actually correct.

## Interview Answer Skeleton

**30-sec:** `@Test` marks a method JUnit runs with a pass/fail outcome; `@BeforeEach` runs before every test to guarantee isolation. `assertThrows` is the correct way to test an exception; `@ParameterizedTest` collapses near-identical tests into one.

**2-min:** Add the isolation mechanism explicitly — `@BeforeEach` constructs fresh state before each test, which is what prevents one test's leftover state from affecting another's outcome, and connect this to why an intermittently-failing CI suite should first be suspected of a test-isolation bug, not a production race condition.

**Whiteboard:** Draw a timeline of 3 tests, each preceded by its own `@BeforeEach` box resetting state to the same starting point — then draw a broken version where one shared box feeds all three, showing how test 2's outcome can now depend on what test 1 did.

**Staff-level framing:** A test suite that consistently follows isolation discipline can run in parallel safely at scale; one that doesn't must run serially or accept intermittent failures as a cost of doing business — real leverage is establishing that discipline via review/tooling as a suite grows from dozens to thousands of tests.

## Related

- syllabus/08-testing/test-strategy-and-test-doubles.md
- syllabus/08-testing/junit5-architecture-and-advanced-features.md
- syllabus/08-testing/writing-tests-live-in-an-interview.md
