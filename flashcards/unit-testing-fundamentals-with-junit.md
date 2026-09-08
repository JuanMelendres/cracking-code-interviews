---
title: "Flashcards: Unit Testing Fundamentals with JUnit"
slug: unit-testing-fundamentals-with-junit
document_type: flashcard-deck
domain: 08-testing
topic_id: T-2204
canonical: ../syllabus/08-testing/unit-testing-fundamentals-with-junit.md
last_updated: 2026-09-07
---

# Flashcards: Unit Testing Fundamentals with JUnit

**Canonical chapter:** [`syllabus/08-testing/unit-testing-fundamentals-with-junit.md`](../syllabus/08-testing/unit-testing-fundamentals-with-junit.md)

## Card: @Test vs. @BeforeEach

**Prompt:**
What's the difference between `@Test` and `@BeforeEach`?

**Answer:**
`@Test` marks a method JUnit runs as an actual test with a pass/fail outcome. `@BeforeEach` marks a setup method that runs before every `@Test` in the class — it is not itself a test.

**Why it matters:**
The most basic JUnit distinction, and the mechanism behind test isolation.

**Common trap:**
Treating `@BeforeEach` as just another test method.

**Related:**
[Unit Testing Fundamentals with JUnit](../syllabus/08-testing/unit-testing-fundamentals-with-junit.md)

## Card: The correct way to test an exception

**Prompt:**
What's the correct way to test that a method throws an exception?

**Answer:**
`assertThrows(ExceptionType.class, () -> methodCall())`. It runs the code, and the test passes only if that exact exception type is thrown — if nothing is thrown, `assertThrows` itself fails the test.

**Why it matters:**
A manual `try`/`catch` + `fail()` is easy to write incorrectly (e.g., forgetting the `fail()` call in the `try` block).

**Common trap:**
Writing a manual `try`/`catch` that silently passes if no exception occurs, because `fail()` was never called or was placed wrong.

**Related:**
[Unit Testing Fundamentals with JUnit](../syllabus/08-testing/unit-testing-fundamentals-with-junit.md)

## Card: Why @BeforeEach prevents order-dependent failures

**Prompt:**
Why does using `@BeforeEach` to reset state (instead of a shared static field) matter for a growing test suite?

**Answer:**
A `static` field or a field set once outside `@BeforeEach` can make a test's outcome depend on which other test ran before it — producing a suite that passes locally in one order and fails intermittently when tests run in a different order or in parallel. `@BeforeEach` gives every test the same fresh starting state, making outcomes independent of order.

**Why it matters:**
A Senior-level differentiator: recognizing "intermittent CI failure" as a test-isolation question first, before assuming the code under test has a race condition.

**Common trap:**
Assuming an intermittently-failing test suite means the production code has a concurrency bug.

**Related:**
[Unit Testing Fundamentals with JUnit](../syllabus/08-testing/unit-testing-fundamentals-with-junit.md)

## Card: When to use @ParameterizedTest

**Prompt:**
You have four nearly-identical `@Test` methods differing only in one input value. What should you do?

**Answer:**
Collapse them into one `@ParameterizedTest` with `@ValueSource` (single values) or `@CsvSource` (multiple values per run) — one test method producing several independently-reported executions.

**Why it matters:**
A visible, easy-to-apply signal for reducing duplicated test code without losing per-case reporting.

**Common trap:**
Not recognizing the duplication pattern and continuing to write near-identical `@Test` methods by hand.

**Related:**
[Unit Testing Fundamentals with JUnit](../syllabus/08-testing/unit-testing-fundamentals-with-junit.md)

## Card: Reading a real assertion failure

**Prompt:**
A test fails with `expected: <6> but was: <5>`. Which side is wrong — the test or the code under test?

**Answer:**
The failure message alone doesn't say — it only states the two sides disagreed. You have to independently know which value is actually correct (here, `2 + 3` is mathematically `5`, so the test's own expected value was wrong).

**Why it matters:**
A genuinely common misreading: assuming a failing assertion always means the implementation is broken.

**Common trap:**
Assuming the code under test is always the thing at fault when an assertion fails.

**Related:**
[Unit Testing Fundamentals with JUnit](../syllabus/08-testing/unit-testing-fundamentals-with-junit.md)
