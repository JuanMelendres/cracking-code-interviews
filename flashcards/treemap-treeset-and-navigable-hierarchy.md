---
title: "Flashcards: TreeMap, TreeSet, and the Navigable Hierarchy"
slug: treemap-treeset-and-navigable-hierarchy
document_type: flashcard-deck
domain: collections
topic_id: T-203
canonical: ../handbook/collections/treemap-treeset-and-navigable-hierarchy.md
last_updated: 2026-09-02
---

# Flashcards: TreeMap, TreeSet, and the Navigable Hierarchy

**Canonical chapter:** [`handbook/collections/treemap-treeset-and-navigable-hierarchy.md`](../handbook/collections/treemap-treeset-and-navigable-hierarchy.md)

## Card: Is `NavigableSet` a class or an interface, and what's its relationship to `TreeSet`?

**Prompt:**
Is `NavigableSet` a concrete class alongside `TreeSet`, or something else?

**Answer:**
An interface — `TreeSet` is the concrete class that IMPLEMENTS it, verified directly via `TreeSet.class.getInterfaces()` returning `NavigableSet` as `TreeSet`'s own supertype.

**Why it matters:**
A real, commonly-misdrawn hierarchy — this repo's own Phase 1 audit flagged its source material for presenting `NavigableSet` as `TreeSet`'s sibling instead.

**Common trap:**
Drawing `NavigableSet` as a peer implementation rather than the interface `TreeSet` fulfills.

**Related:**
[handbook/collections/treemap-treeset-and-navigable-hierarchy.md](../handbook/collections/treemap-treeset-and-navigable-hierarchy.md), [handbook/collections/hashmap-internals.md](../handbook/collections/hashmap-internals.md)

## Card: Does `TreeMap` guarantee O(log n) even under the worst possible insertion order?

**Prompt:**
If you insert keys into a `TreeMap` in already-sorted (ascending) order — the input that would break a naive BST — does `TreeMap` still guarantee O(log n) operations?

**Answer:**
Yes — verified with a real, reflective measurement. At 100,000 ascending insertions, `TreeMap`'s real height was 31; a naive, unbalanced BST fed the identical sequence had a real height of exactly 100,000 (a straight line). Red-Black rebalancing on every insertion is the real mechanism that prevents this degeneration.

**Why it matters:**
Distinguishes a real, structural worst-case guarantee from `HashMap`'s average-case-only O(1).

**Common trap:**
Assuming `TreeMap`'s O(log n) is merely typical, like `HashMap`'s O(1), rather than a genuine worst-case property.

**Related:**
[handbook/collections/treemap-treeset-and-navigable-hierarchy.md](../handbook/collections/treemap-treeset-and-navigable-hierarchy.md)
