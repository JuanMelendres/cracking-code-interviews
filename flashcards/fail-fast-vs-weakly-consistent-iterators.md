---
title: "Flashcards: Fail-Fast vs. Weakly-Consistent Iterators"
slug: fail-fast-vs-weakly-consistent-iterators
document_type: flashcard-deck
domain: collections
topic_id: T-208
canonical: ../handbook/collections/fail-fast-vs-weakly-consistent-iterators.md
last_updated: 2026-09-02
---

# Flashcards: Fail-Fast vs. Weakly-Consistent Iterators

**Canonical chapter:** [`handbook/collections/fail-fast-vs-weakly-consistent-iterators.md`](../handbook/collections/fail-fast-vs-weakly-consistent-iterators.md)

## Card: What actually triggers CME

**Prompt:**
What does a fail-fast iterator actually check to decide whether to throw `ConcurrentModificationException`?

**Answer:**
It compares the collection's `modCount` (bumped on structural modification) against the `expectedModCount` it captured at creation, on every `next()` call.

**Why it matters:**
The real mechanism, not the vague "you can't modify during iteration" rule.

**Common trap:**
Assuming Java actively prevents modification, rather than detecting it after the fact, and only at specific check points.

**Related:**
[Internal Implementation](../handbook/collections/fail-fast-vs-weakly-consistent-iterators.md#internal-implementation)

## Card: The best-effort quirk

**Prompt:**
Does removing an element from an `ArrayList` during a for-each loop always throw `ConcurrentModificationException`?

**Answer:**
No — removing the second-to-last element produces zero exception, a real, reproducible gap in the best-effort detector.

**Why it matters:**
Proof that "no exception" is not evidence of a safe loop.

**Common trap:**
Treating a clean test run as proof of correctness.

**Related:**
[Internal Implementation](../handbook/collections/fail-fast-vs-weakly-consistent-iterators.md#internal-implementation)

## Card: Two different weakly-consistent contracts

**Prompt:**
Do `CopyOnWriteArrayList` and `ConcurrentHashMap` give the same iteration guarantee under concurrent modification?

**Answer:**
No — `CopyOnWriteArrayList`'s iterator is a fixed snapshot; `ConcurrentHashMap`'s iterator may (non-deterministically) reflect concurrent writes. Both never throw, but they're not the same contract.

**Why it matters:**
"Weakly consistent" is a label covering genuinely different behaviors.

**Common trap:**
Assuming all non-fail-fast collections behave identically under concurrent iteration.

**Related:**
[Internal Implementation](../handbook/collections/fail-fast-vs-weakly-consistent-iterators.md#internal-implementation)
