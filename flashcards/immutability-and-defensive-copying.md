---
title: "Flashcards: Immutability and Defensive Copying"
slug: immutability-and-defensive-copying
document_type: flashcard-deck
domain: java-core
topic_id: T-103
canonical: ../handbook/java-core/immutability-and-defensive-copying.md
last_updated: 2026-08-06
---

# Flashcards: Immutability and Defensive Copying

**Canonical chapter:** [`syllabus/02-java/language-core/immutability-and-defensive-copying.md`](../syllabus/02-java/language-core/immutability-and-defensive-copying.md)

## Card: What final fields do and don't guarantee

**Prompt:**
Do `final` fields alone make a class immutable?

**Answer:**
No — `final` prevents reassigning the field, but the object it references can still be mutated if a live reference leaks through the constructor or a getter.

**Why it matters:**
The single most common misconception about Java immutability.

**Common trap:**
Treating "all fields final, no setters" as sufficient proof of immutability.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/immutability-and-defensive-copying.md#internal-implementation)

## Card: The two leak points

**Prompt:**
What are the two places a supposedly-immutable class can leak mutability?

**Answer:**
The constructor (storing a caller's mutable reference directly) and a getter (returning a live reference to internal mutable state).

**Why it matters:**
Both must be defensively copied for genuine immutability.

**Common trap:**
Fixing only one of the two boundaries.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/immutability-and-defensive-copying.md#internal-implementation)

## Card: Why List.copyOf() is stronger than a plain copy

**Prompt:**
Why is `List.copyOf()` stronger than copying into a new `ArrayList`?

**Answer:**
It rejects any mutation attempt outright (`UnsupportedOperationException`), not just providing independence from the original list.

**Why it matters:**
A structural guarantee (loud failure on violation) is stronger than a convention (a mutable copy nobody's supposed to mutate).

**Common trap:**
Treating a plain defensive copy as equivalent to an immutable view.

**Related:**
[Core Concepts](../syllabus/02-java/language-core/immutability-and-defensive-copying.md#core-concepts)
