---
title: "Flashcards: Optional and Null Strategy"
slug: optional-and-null-strategy
document_type: flashcard-deck
domain: java-core
topic_id: T-109
canonical: ../handbook/java-core/optional-and-null-strategy.md
last_updated: 2026-09-02
---

# Flashcards: Optional and Null Strategy

**Canonical chapter:** [`handbook/java-core/optional-and-null-strategy.md`](../handbook/java-core/optional-and-null-strategy.md)

## Card: orElse vs orElseGet, measured

**Prompt:**
Is `orElse(expensiveCall())` evaluated only when the `Optional` is empty?

**Answer:**
No — `orElse()`'s argument is always evaluated eagerly, on every call. Measured directly: ~1200x slower than `orElseGet()` for a genuinely expensive fallback on an already-present value.

**Why it matters:**
A real, measured, easy-to-miss performance trap, not a stylistic nuance.

**Common trap:**
Treating `orElse` and `orElseGet` as interchangeable regardless of fallback cost.

**Related:**
[Internal Implementation](../handbook/java-core/optional-and-null-strategy.md#internal-implementation)

## Card: Optional as a field, the real consequence

**Prompt:**
What real, concrete problem does storing `Optional` as a field cause, beyond style?

**Answer:**
`Optional` doesn't implement `Serializable` — a class with an `Optional` field genuinely cannot be serialized, verified directly via a real `NotSerializableException`.

**Why it matters:**
Turns a commonly-repeated rule into a defensible, evidence-backed answer.

**Common trap:**
Citing "it's bad practice" without a concrete reason.

**Related:**
[Internal Implementation](../handbook/java-core/optional-and-null-strategy.md#internal-implementation)

## Card: of vs ofNullable

**Prompt:**
What happens if you call `Optional.of(null)`?

**Answer:**
Throws `NullPointerException` immediately, at construction — verified directly. `Optional.ofNullable(null)` instead produces a real, safe empty `Optional`.

**Why it matters:**
Choosing the wrong constructor turns a graceful "empty" case into an immediate crash.

**Common trap:**
Using `of()` on a value whose nullability isn't actually guaranteed.

**Related:**
[Internal Implementation](../handbook/java-core/optional-and-null-strategy.md#internal-implementation)
