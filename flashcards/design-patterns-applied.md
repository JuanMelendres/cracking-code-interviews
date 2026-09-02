---
title: "Flashcards: Design Patterns Applied"
slug: design-patterns-applied
document_type: flashcard-deck
domain: architecture
topic_id: T-914
canonical: ../handbook/architecture/design-patterns-applied.md
last_updated: 2026-09-02
---

# Flashcards: Design Patterns Applied

**Canonical chapter:** [`handbook/architecture/design-patterns-applied.md`](../handbook/architecture/design-patterns-applied.md)

## Card: What Strategy isolates

**Prompt:**
What specific kind of variation does the Strategy pattern isolate?

**Answer:**
Which algorithm runs, behind a common interface — the client never branches on which concrete strategy it holds.

**Why it matters:**
The core "problem shape" question every pattern in this chapter answers differently.

**Common trap:**
Confusing Strategy (algorithm choice) with Decorator (optional behavior layering) — they look similar but solve different problems.

**Related:**
[Core Concepts](../handbook/architecture/design-patterns-applied.md#core-concepts)

## Card: Why a naive lazy Singleton isn't thread-safe

**Prompt:**
Why is `if (instance == null) instance = new X();` not thread-safe?

**Answer:**
Check-then-act isn't atomic — multiple threads can observe `null` concurrently and each construct their own instance, measured directly (30 racing threads produced 30 separate objects).

**Why it matters:**
The single sharpest, most measurably-real result in this chapter.

**Common trap:**
Assuming Singleton is automatically safe because it's a well-known pattern.

**Related:**
[Internal Implementation](../handbook/architecture/design-patterns-applied.md#internal-implementation)

## Card: Composition vs. inheritance for optional behaviors

**Prompt:**
For `N` independent optional behaviors, how many classes does Decorator need, versus subclassing?

**Answer:**
Decorator needs `N` classes, composed however the caller wants; subclassing needs up to `2^N` classes to cover every combination.

**Why it matters:**
The precise, quantified reason Decorator exists, not just "it avoids too many subclasses."

**Common trap:**
Not being able to state the actual `N` vs. `2^N` scaling when asked why.

**Related:**
[Internal Implementation](../handbook/architecture/design-patterns-applied.md#internal-implementation)

## Card: The Singleton fix that needs no hand-written synchronization

**Prompt:**
Which Singleton implementation is thread-safe with zero hand-written synchronization code?

**Answer:**
An enum-based Singleton — the JLS guarantees enum constant initialization happens under the classloader's own initialization lock.

**Why it matters:**
Removes an entire class of bugs (getting double-checked locking subtly wrong) by construction.

**Common trap:**
Reaching for manual `synchronized`/double-checked locking when the enum form is simpler and provably correct.

**Related:**
[Internal Implementation](../handbook/architecture/design-patterns-applied.md#internal-implementation)
