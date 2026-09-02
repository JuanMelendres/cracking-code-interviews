---
title: "Flashcards: JPA Entity Lifecycle and the N+1 Problem"
slug: jpa-entity-lifecycle-and-the-n1-problem
document_type: flashcard-deck
domain: databases
topic_id: T-601 / T-602
canonical: ../handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md
last_updated: 2026-09-02
---

# Flashcards: JPA Entity Lifecycle and the N+1 Problem

**Canonical chapter:** [`handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md`](../handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md)

## Card: What the persistence context guarantees

**Prompt:**
What does the persistence context guarantee about two `find()` calls for the same id, in the same session?

**Answer:**
They return the exact same Java object (reference equality), not just two equal objects — the second call never re-queries.

**Why it matters:**
The identity map is the single mechanism behind dirty checking, lazy loading, and cache-like `find()` behavior all at once.

**Common trap:**
Assuming JPA/Hibernate does value-equality comparison instead of returning the tracked instance.

**Related:**
[Internal Implementation](../handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md#internal-implementation)

## Card: Why LazyInitializationException happens

**Prompt:**
Why does accessing a lazy field on a detached entity throw `LazyInitializationException`?

**Answer:**
The entity's session (persistence context) has already closed, so the uninitialized lazy proxy has no open session left to fetch through.

**Why it matters:**
The single most common real Hibernate production bug — measured directly in this chapter.

**Common trap:**
"Fixing" it by widening the transaction to cover the whole request, rather than aligning the boundary with where lazy access actually happens.

**Related:**
[Internal Implementation](../handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md#internal-implementation)

## Card: Why EAGER doesn't fix N+1

**Prompt:**
Why is switching a lazy association to `EAGER` usually the wrong fix for N+1?

**Answer:**
It applies the extra load unconditionally to every code path touching that entity, including ones that never needed the association — relocating the cost rather than eliminating it.

**Why it matters:**
The most common wrong answer to this domain's single most-asked interview question.

**Common trap:**
Proposing `EAGER` as a complete fix with no acknowledgment of its blanket cost.

**Related:**
[Internal Implementation](../handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md#internal-implementation)
