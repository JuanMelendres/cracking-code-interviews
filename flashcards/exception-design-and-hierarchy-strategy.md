---
title: "Flashcards: Exception Design and Hierarchy Strategy"
slug: exception-design-and-hierarchy-strategy
document_type: flashcard-deck
domain: java-core
topic_id: T-105
canonical: ../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md
last_updated: 2026-09-14
---

# Flashcards: Exception Design and Hierarchy Strategy

**Canonical chapter:** [`syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md`](../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md)

## Card: The exception hierarchy, top to bottom

**Prompt:**
What's the actual root of Java's exception hierarchy, and what's the structural rule for checked vs. unchecked?

**Answer:**
`Throwable` is the root — not `Exception`. It splits into `Error` and `Exception`; `Exception` splits into `RuntimeException` and everything else. A class is unchecked if and only if it extends `RuntimeException` or `Error`; every other `Throwable` subclass is checked.

**Why it matters:**
"What's the root of the exception hierarchy" is a near-universal opener, and answering "`Exception`" instead of "`Throwable`" is a common, checkable miss.

**Common trap:**
Forgetting `Error` exists, or thinking checked/unchecked is marked by an annotation rather than being purely which class is extended.

**Related:**
[Level 1 — Foundation](../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#level-1--foundation)

## Card: Common derived exceptions by category

**Prompt:**
Name at least three real Java exception classes in each of: `Error`, unchecked `Exception`, checked `Exception`.

**Answer:**
`Error`: `OutOfMemoryError`, `StackOverflowError`, `NoClassDefFoundError`. Unchecked (`RuntimeException`): `NullPointerException`, `IllegalArgumentException`, `IllegalStateException`, `ClassCastException`, `ArrayIndexOutOfBoundsException`. Checked: `IOException`, `SQLException`, `InterruptedException`, `TimeoutException`.

**Why it matters:**
Interviewers frequently follow up "what's checked vs. unchecked" with "give me real examples" — a candidate who can only recite the rule without naming real classes reads as having memorized, not understood, the distinction.

**Common trap:**
Misclassifying `ClassCastException` or `ArrayIndexOutOfBoundsException` as checked — both are unchecked `RuntimeException` subclasses despite sounding like they should require handling.

**Related:**
[Core Concepts](../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#core-concepts)

## Card: What chaining the cause preserves

**Prompt:**
What does chaining the cause when wrapping an exception actually preserve?

**Answer:**
The original exception and its full stack trace, retrievable via `getCause()` and shown in `printStackTrace()`'s `Caused by:` section.

**Why it matters:**
Without it, `getCause()` returns `null` and the real root cause is gone permanently.

**Common trap:**
Constructing a message-only wrapped exception inside a `catch` block.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#internal-implementation)

## Card: What try-with-resources does when both throw

**Prompt:**
What happens when both a try-with-resources body and `close()` throw?

**Answer:**
The body's exception propagates as primary; the `close()` exception is attached via `addSuppressed()` and retrievable via `getSuppressed()` — neither is lost.

**Why it matters:**
The specific guarantee that motivated try-with-resources over manual cleanup.

**Common trap:**
Assuming a manual `finally` block behaves the same way.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#internal-implementation)

## Card: Why manual finally cleanup is strictly worse

**Prompt:**
Why is a manual `finally`-block `close()` that also throws strictly worse than try-with-resources?

**Answer:**
It silently replaces the original exception entirely, with no suppressed-exception mechanism to recover it — measured directly.

**Why it matters:**
The concrete reason try-with-resources exists as a language feature.

**Common trap:**
Assuming both approaches are equivalent as long as `close()` is called.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/exception-design-and-hierarchy-strategy.md#internal-implementation)
