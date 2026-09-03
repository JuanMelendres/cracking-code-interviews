---
title: "Flashcards: ArrayList and LinkedList Internals"
slug: arraylist-and-linkedlist-internals
document_type: flashcard-deck
domain: collections
topic_id: T-202
canonical: ../handbook/collections/arraylist-and-linkedlist-internals.md
last_updated: 2026-08-06
---

# Flashcards: ArrayList and LinkedList Internals

**Canonical chapter:** [`syllabus/02-java/collections/arraylist-and-linkedlist-internals.md`](../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md)

## Card: ArrayList's growth factor

**Prompt:**
By what factor does `ArrayList` grow its backing array when full?

**Answer:**
Roughly 1.5x (`oldCapacity + oldCapacity/2`), not by doubling — confirmed via reflection.

**Why it matters:**
A common assumption is that it doubles, like many other growable structures.

**Common trap:**
Assuming ArrayList doubles its capacity like a typical dynamic array implementation.

**Related:**
[Internal Implementation](../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#internal-implementation)

## Card: Indexed access complexity

**Prompt:**
What is the time complexity of `get(index)` for `ArrayList` versus `LinkedList`?

**Answer:**
O(1) for `ArrayList` (direct array indexing); O(n) for `LinkedList` (node-by-node traversal) — measured at ~320x slower for a 50,000-element list.

**Why it matters:**
The single biggest reason to prefer `ArrayList` for read-heavy access patterns.

**Common trap:**
Using `LinkedList.get()` in a loop, creating an accidental O(n²) traversal.

**Related:**
[Internal Implementation](../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#internal-implementation)

## Card: The scope of LinkedList's O(1) insertion

**Prompt:**
Is `LinkedList`'s O(1) insertion guarantee true for any index?

**Answer:**
No — only at an already-known position (head, tail, or an existing iterator position). Inserting at an arbitrary index `k` is O(n) overall, since finding position `k` requires a traversal.

**Why it matters:**
A common overgeneralization that leads to choosing LinkedList for the wrong reason.

**Common trap:**
Assuming `add(k, x)` for an arbitrary `k` is O(1) on a `LinkedList`.

**Related:**
[Core Concepts](../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#core-concepts)
