---
title: "Flashcards: Java Memory Model and volatile"
slug: java-memory-model-and-volatile
document_type: flashcard-deck
domain: concurrency
topic_id: T-401
canonical: ../handbook/concurrency/java-memory-model-and-volatile.md
last_updated: 2026-08-06
---

# Flashcards: Java Memory Model and volatile

**Canonical chapter:** [`syllabus/02-java/concurrency/java-memory-model-and-volatile.md`](../syllabus/02-java/concurrency/java-memory-model-and-volatile.md)

## Card: What volatile actually guarantees

**Prompt:**
What does `volatile` actually guarantee?

**Answer:**
A happens-before edge — writes to the field are visible to subsequent reads of that same field, and specific compiler reorderings around it are forbidden. It is not a caching mechanism.

**Why it matters:**
Corrects the single most common, and previously actively-wrong, misconception in this project's own source material.

**Common trap:**
Describing `volatile` as "preventing caching" or "forcing a read from RAM."

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#internal-implementation)

## Card: volatile and compound operations

**Prompt:**
Does `volatile` make `count++` thread-safe?

**Answer:**
No — it's a read-modify-write, three operations; `volatile` only guarantees each individual read/write is visible, not that the sequence is atomic.

**Why it matters:**
The most common way `volatile` is over-trusted in practice.

**Common trap:**
Assuming a `volatile` counter is safe under concurrent increments.

**Related:**
[Core Concepts](../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#core-concepts)

## Card: Double-checked locking and volatile

**Prompt:**
Why does double-checked locking need `volatile` on the singleton field?

**Answer:**
Without it, a reader thread can observe a non-null reference before the constructor's writes are visible — a partially-constructed object.

**Why it matters:**
The canonical interview question this topic exists to answer.

**Common trap:**
Believing `synchronized` on the constructor block alone is sufficient without `volatile` on the field.

**Related:**
[Java Examples](../syllabus/02-java/concurrency/java-memory-model-and-volatile.md#java-examples)
