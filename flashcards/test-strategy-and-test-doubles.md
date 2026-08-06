---
title: "Flashcards: Test Strategy, the Pyramid, and Test Doubles"
slug: test-strategy-and-test-doubles
document_type: flashcard-deck
domain: testing
topic_id: T-1103
canonical: ../handbook/testing/test-strategy-and-test-doubles.md
last_updated: 2026-08-06
---

# Flashcards: Test Strategy, the Pyramid, and Test Doubles

**Canonical chapter:** [`handbook/testing/test-strategy-and-test-doubles.md`](../handbook/testing/test-strategy-and-test-doubles.md)

## Card: What a mock proves beyond a return value

**Prompt:**
What does `verify(gateway, times(3))` prove that `assertTrue(result)` alone cannot?

**Answer:**
That the retry logic called the dependency the exact expected number of times with the exact arguments — the interaction, not just the outcome.

**Why it matters:**
Catches wasted-retry and missing-retry bugs a return-value-only assertion would miss identically.

**Common trap:**
Treating a passing boolean assertion as proof the interaction logic itself is correct.

**Related:**
[Internal Implementation](../handbook/testing/test-strategy-and-test-doubles.md#internal-implementation)

## Card: Why mocking the database in a repository test is wrong

**Prompt:**
What's wrong with mocking the database in a repository test?

**Answer:**
It only verifies the test's own assumptions about what the database does — it never checks real SQL correctness.

**Why it matters:**
A common, false-confidence-producing mistake that a 100%-passing suite can hide.

**Common trap:**
Believing a fully-mocked, fully-passing repository suite is sufficient coverage of the boundary.

**Related:**
[Production Scenarios](../handbook/testing/test-strategy-and-test-doubles.md#production-scenarios)

## Card: What coverage percentage actually measures

**Prompt:**
What does coverage percentage actually measure?

**Answer:**
Execution (lines/branches run at least once) — nothing about assertion quality. A diagnostic tool, not a quality target.

**Why it matters:**
Prevents treating a coverage number as proof of test quality.

**Common trap:**
Setting a coverage percentage as a release gate without checking assertion quality.

**Related:**
[Core Concepts](../handbook/testing/test-strategy-and-test-doubles.md#core-concepts)
