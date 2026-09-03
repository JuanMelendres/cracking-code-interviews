---
title: "Flashcards: ArrayDeque Internals and the Legacy Stack/Vector Problem"
slug: arraydeque-internals-and-the-legacy-stack-problem
document_type: flashcard-deck
domain: collections
topic_id: T-204
canonical: ../handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md
last_updated: 2026-09-02
---

# Flashcards: ArrayDeque Internals and the Legacy Stack/Vector Problem

**Canonical chapter:** [`syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md`](../syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md)

## Card: Real capacity formula

**Prompt:**
On OpenJDK 21, is `ArrayDeque`'s capacity always a power of two?

**Answer:**
No — verified directly, the real capacity is `requestedCapacity + 1`, with no power-of-two rounding. That was true of older, bitmask-modulo implementations, not current behavior.

**Why it matters:**
A common, unverified claim worth checking against the actual JDK.

**Common trap:**
Repeating version-specific internals claims as timeless facts.

**Related:**
[Internal Implementation](../syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md#internal-implementation)

## Card: The legacy Stack cost

**Prompt:**
Why is `java.util.Stack` slower than `ArrayDeque` for single-threaded stack usage?

**Answer:**
`Stack` extends `Vector`, whose every method is `synchronized` — real, unconditional lock-acquisition cost, measured ~2.26x slower than `ArrayDeque`'s unsynchronized `push()`/`pop()`.

**Why it matters:**
Justifies the "avoid Stack" recommendation with an actual measured mechanism.

**Common trap:**
Citing "it's legacy" without a real reason.

**Related:**
[Internal Implementation](../syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md#internal-implementation)

## Card: Null restriction

**Prompt:**
Can you store `null` in an `ArrayDeque`?

**Answer:**
No — it throws `NullPointerException`, verified directly, because `null` is reserved internally as the empty-slot sentinel.

**Why it matters:**
A real, easy-to-miss behavioral gotcha versus `LinkedList`, which permits `null`.

**Common trap:**
Assuming all `Deque` implementations handle `null` identically.

**Related:**
[Internal Implementation](../syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md#internal-implementation)
