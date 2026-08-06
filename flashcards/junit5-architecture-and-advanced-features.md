---
title: "Flashcards: JUnit 5 Architecture and Advanced Features"
slug: junit5-architecture-and-advanced-features
document_type: flashcard-deck
domain: testing
topic_id: T-1102
canonical: ../handbook/testing/junit5-architecture-and-advanced-features.md
last_updated: 2026-08-06
---

# Flashcards: JUnit 5 Architecture and Advanced Features

**Canonical chapter:** [`handbook/testing/junit5-architecture-and-advanced-features.md`](../handbook/testing/junit5-architecture-and-advanced-features.md)

## Card: Why JUnit 5 splits into three modules

**Prompt:**
Why does JUnit 5 split into three modules (Platform, Jupiter, Vintage) rather than one?

**Answer:**
So a framework-agnostic launcher, the modern programming model, and legacy JUnit 3/4 compatibility can evolve independently — most concretely enabling incremental, not big-bang, migration from JUnit 4.

**Why it matters:**
The architectural reason JUnit 5 can run alongside legacy JUnit 4 tests in the same build.

**Common trap:**
Describing JUnit 5 as a single monolithic library rather than a layered platform architecture.

**Related:**
[handbook/testing/junit5-architecture-and-advanced-features.md](../handbook/testing/junit5-architecture-and-advanced-features.md)

## Card: What replaced Runner and Rule

**Prompt:**
What replaced JUnit 4's separate Runner and Rule mechanisms in JUnit 5?

**Answer:**
A single, composable `Extension` interface family, registered via `@ExtendWith` — multiple extensions can apply to one test class, unlike JUnit 4's largely one-Runner-at-a-time model.

**Why it matters:**
The concrete mechanism behind JUnit 5's improved composability over JUnit 4.

**Common trap:**
Assuming JUnit 5's extension model is just a renamed version of JUnit 4's Runner, rather than a genuinely composable replacement.

**Related:**
[handbook/testing/junit5-architecture-and-advanced-features.md](../handbook/testing/junit5-architecture-and-advanced-features.md)

## Card: When to choose @TestFactory over @ParameterizedTest

**Prompt:**
When should `@TestFactory` be chosen over `@ParameterizedTest`?

**Answer:**
Only when the test-case set is genuinely computed at runtime (not known in advance) — for a fixed, known set, `@ParameterizedTest`'s built-in data-source annotations are simpler and more idiomatic.

**Why it matters:**
Prevents reaching for the more complex dynamic-test mechanism when a simpler, standard annotation already fits.

**Common trap:**
Defaulting to `@TestFactory` for a fixed, statically-known set of test cases that `@ParameterizedTest` would express more simply.

**Related:**
[handbook/testing/junit5-architecture-and-advanced-features.md](../handbook/testing/junit5-architecture-and-advanced-features.md)
