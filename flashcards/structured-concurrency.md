---
title: "Flashcards: Structured Concurrency"
slug: structured-concurrency
document_type: flashcard-deck
domain: concurrency
topic_id: T-411
canonical: ../handbook/concurrency/structured-concurrency.md
last_updated: 2026-09-02
---

# Flashcards: Structured Concurrency

**Canonical chapter:** [`handbook/concurrency/structured-concurrency.md`](../handbook/concurrency/structured-concurrency.md)

## Card: What "structured" actually guarantees

**Prompt:**
What does "structured" mean in structured concurrency?

**Answer:**
Subtask lifetime is bound to a lexical scope — the scope cannot exit while any forked subtask is still running, and a failure policy can automatically cancel siblings.

**Why it matters:**
The real, enforced guarantee `CompletableFuture` doesn't provide.

**Common trap:**
Treating it as a stylistic API difference rather than an enforced lifetime guarantee.

**Related:**
[Core Concepts](../handbook/concurrency/structured-concurrency.md#core-concepts)

## Card: The orphaned-task cost

**Prompt:**
Does `CompletableFuture` automatically cancel sibling tasks when one fails?

**Answer:**
No — measured directly: a sibling ran its full real ~2-second duration despite an unrelated branch failing at ~100ms.

**Why it matters:**
The real, quantified problem structured concurrency exists to solve.

**Common trap:**
Assuming `CompletableFuture.allOf()` provides cancellation-on-failure semantics.

**Related:**
[Internal Implementation](../handbook/concurrency/structured-concurrency.md#internal-implementation)

## Card: Version status

**Prompt:**
Is `StructuredTaskScope` a stable JDK 21 API?

**Answer:**
No — it's a preview API (JEP 453, second preview in JDK 21), requiring `--enable-preview`.

**Why it matters:**
Production adoption is a deliberate decision, not a default.

**Common trap:**
Assuming any documented JDK API is automatically production-stable.

**Related:**
[Version status callout](../handbook/concurrency/structured-concurrency.md#structured-concurrency)
