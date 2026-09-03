---
title: "Flashcards: Generics: Erasure, Variance, and PECS"
slug: generics-erasure-and-pecs
document_type: flashcard-deck
domain: java-core
topic_id: T-104
canonical: ../handbook/java-core/generics-erasure-and-pecs.md
last_updated: 2026-08-06
---

# Flashcards: Generics: Erasure, Variance, and PECS

**Canonical chapter:** [`syllabus/02-java/language-core/generics-erasure-and-pecs.md`](../syllabus/02-java/language-core/generics-erasure-and-pecs.md)

## Card: What erasure removes

**Prompt:**
What does type erasure actually remove, and when?

**Answer:**
Generic type parameter information, removed after compile time — `List<String>` and `List<Integer>` are the identical class at runtime.

**Why it matters:**
Explains why you can't do `instanceof List<String>` or `new T[]`.

**Common trap:**
Assuming some generic type information survives to runtime.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/generics-erasure-and-pecs.md#internal-implementation)

## Card: When a defeated generic actually fails

**Prompt:**
When does a defeated generic (via unchecked cast) actually fail?

**Answer:**
At read time — when code relies on the declared element type (e.g., an implicit cast inside `get()`) — not at the point the incompatible value was inserted.

**Why it matters:**
Explains why such bugs are often hard to trace back to their real cause.

**Common trap:**
Assuming the cast operation itself is where the failure would occur.

**Related:**
[Production Scenarios](../syllabus/02-java/language-core/generics-erasure-and-pecs.md#production-scenarios)

## Card: PECS rule

**Prompt:**
State PECS.

**Answer:**
Producer Extends, Consumer Super — a parameter you only read from should be `? extends T`; one you only write to should be `? super T`.

**Why it matters:**
The rule that maximizes what callers can pass while keeping the compiler's safety guarantees.

**Common trap:**
Reversing extends/super, or using a wildcard where a plain type parameter would do.

**Related:**
[Core Concepts](../syllabus/02-java/language-core/generics-erasure-and-pecs.md#core-concepts)
