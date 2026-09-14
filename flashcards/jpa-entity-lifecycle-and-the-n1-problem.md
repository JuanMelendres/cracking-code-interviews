---
title: "Flashcards: JPA Entity Lifecycle and the N+1 Problem"
slug: jpa-entity-lifecycle-and-the-n1-problem
document_type: flashcard-deck
domain: databases
topic_id: T-601 / T-602
canonical: ../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md
last_updated: 2026-09-14
---

# Flashcards: JPA Entity Lifecycle and the N+1 Problem

**Canonical chapter:** [`syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md`](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md)

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
[Internal Implementation](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#internal-implementation)

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
[Internal Implementation](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#internal-implementation)

## Card: What merge() actually returns

**Prompt:**
After `Author managed = entityManager.merge(detachedAuthor);`, is `detachedAuthor` now managed?

**Answer:**
No — `merge()` copies the detached entity's state onto a different, managed instance and returns that. `detachedAuthor` itself remains detached forever; only the returned `managed` reference is tracked.

**Why it matters:**
A real, recurring bug: code that calls `merge()` and keeps using the original object as if it were now managed.

**Common trap:**
Ignoring `merge()`'s return value, assuming the method mutates its argument in place.

**Related:**
[Core Concepts](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#core-concepts)

## Card: orphanRemoval vs. CascadeType.REMOVE

**Prompt:**
What's the difference between `orphanRemoval = true` and `CascadeType.REMOVE`?

**Answer:**
`CascadeType.REMOVE` only deletes children when the parent itself is removed. `orphanRemoval = true` additionally deletes a child the instant it's taken out of the parent's collection, even while the parent stays alive.

**Why it matters:**
A frequently-tested distinction whose confusion leads to orphaned rows expected to be cleaned up automatically.

**Common trap:**
Using only `CascadeType.REMOVE` and expecting a child removed from the collection to also be deleted from the database.

**Related:**
[Core Concepts](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#core-concepts)

## Card: Why IDENTITY blocks batch inserts

**Prompt:**
Why does `GenerationType.IDENTITY` prevent Hibernate from batching `INSERT` statements, while `SEQUENCE` doesn't?

**Answer:**
`IDENTITY` requires the database to assign the id on `INSERT` and Hibernate to read it back immediately for identity-map placement, forcing one `INSERT` per entity. `SEQUENCE` pre-fetches a block of ids before any real inserts happen, so multiple inserts can be batched.

**Why it matters:**
A real, common surprise: enabling `hibernate.jdbc.batch_size` does nothing if the entity uses `IDENTITY`.

**Common trap:**
Assuming a larger batch-size setting alone fixes slow bulk inserts, regardless of id-generation strategy.

**Related:**
[Core Concepts](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#core-concepts)

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
[Internal Implementation](../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md#internal-implementation)
