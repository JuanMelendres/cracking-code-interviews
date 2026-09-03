---
title: "Flashcards: Scoped Values and ThreadLocal Migration"
slug: scoped-values-and-threadlocal-migration
document_type: flashcard-deck
domain: concurrency
topic_id: T-412
canonical: ../handbook/concurrency/scoped-values-and-threadlocal-migration.md
last_updated: 2026-09-02
---

# Flashcards: Scoped Values and ThreadLocal Migration

**Canonical chapter:** [`syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md`](../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md)

## Card: No set(), nothing to forget

**Prompt:**
Does `ScopedValue` have a `set()` method like `ThreadLocal`?

**Answer:**
No — the only way to bind a value is `ScopedValue.where(value, x).run(...)`/`.call(...)`, for that call's exact dynamic extent.

**Why it matters:**
This is exactly what makes it structurally immune to the thread-pool-reuse leak.

**Common trap:**
Looking for a `set()` method that doesn't exist.

**Related:**
[Core Concepts](../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md#core-concepts)

## Card: The real ThreadLocal leak

**Prompt:**
What real bug can happen if `ThreadLocal.remove()` is forgotten in thread-pool-based code?

**Answer:**
The next, unrelated task that reuses that same physical thread genuinely sees the stale value — measured directly in this chapter.

**Why it matters:**
A real, common production bug pattern, not a theoretical concern.

**Common trap:**
Assuming a task finishing automatically clears its `ThreadLocal` state.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md#internal-implementation)

## Card: Propagation differences

**Prompt:**
Does a `ThreadLocal` set on a parent thread automatically appear on a spawned child thread?

**Answer:**
No — verified directly, it's `null` on a manually-created child `Thread`. `ScopedValue`, by contrast, genuinely propagates into `StructuredTaskScope` subtasks.

**Why it matters:**
A common, incorrect assumption about `ThreadLocal` inheritance.

**Common trap:**
Assuming any child thread automatically inherits parent `ThreadLocal` state.

**Related:**
[Internal Implementation](../syllabus/02-java/concurrency/scoped-values-and-threadlocal-migration.md#internal-implementation)
