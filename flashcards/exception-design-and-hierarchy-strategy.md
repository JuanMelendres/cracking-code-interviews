---
title: "Flashcards: Exception Design and Hierarchy Strategy"
slug: exception-design-and-hierarchy-strategy
document_type: flashcard-deck
domain: java-core
topic_id: T-105
canonical: ../handbook/java-core/exception-design-and-hierarchy-strategy.md
last_updated: 2026-08-06
---

# Flashcards: Exception Design and Hierarchy Strategy

**Canonical chapter:** [`handbook/java-core/exception-design-and-hierarchy-strategy.md`](../handbook/java-core/exception-design-and-hierarchy-strategy.md)

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
[Internal Implementation](../handbook/java-core/exception-design-and-hierarchy-strategy.md#internal-implementation)

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
[Internal Implementation](../handbook/java-core/exception-design-and-hierarchy-strategy.md#internal-implementation)

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
[Internal Implementation](../handbook/java-core/exception-design-and-hierarchy-strategy.md#internal-implementation)
