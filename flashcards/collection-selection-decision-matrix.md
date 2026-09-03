---
title: "Flashcards: Collection Selection Decision Matrix"
slug: collection-selection-decision-matrix
document_type: flashcard-deck
domain: collections
topic_id: T-209
canonical: ../handbook/collections/collection-selection-decision-matrix.md
last_updated: 2026-08-06
---

# Flashcards: Collection Selection Decision Matrix

**Canonical chapter:** [`syllabus/02-java/collections/collection-selection-decision-matrix.md`](../syllabus/02-java/collections/collection-selection-decision-matrix.md)

## Card: The three questions

**Prompt:**
What three questions does every collection choice reduce to?

**Answer:**
How is it read, how is it written, and is it shared across more than one thread?

**Why it matters:**
The single decision process underlying all four of this week's individual topics.

**Common trap:**
Naming an interface (List, Map, Queue) as if that alone determines the implementation.

**Related:**
[Decision Framework](../syllabus/02-java/collections/collection-selection-decision-matrix.md#decision-framework)

## Card: When ArrayList's default hurts

**Prompt:**
When does defaulting to `ArrayList` actually hurt?

**Answer:**
When the dominant operation is frequent head/tail insertion — measured ~117x slower than `LinkedList` for front-insertion.

**Why it matters:**
A concrete, measured counter-example to "ArrayList is usually fine."

**Common trap:**
Defending ArrayList as a universal default without checking the actual access pattern.

**Related:**
[ArrayList and LinkedList Internals](../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md)

## Card: When HashMap's default hurts

**Prompt:**
When does defaulting to `HashMap` actually hurt?

**Answer:**
The moment it's accessed from more than one thread — it can corrupt silently, with no exception, measured directly.

**Why it matters:**
A correctness failure, not just a performance one, unlike the ArrayList case.

**Common trap:**
Assuming a HashMap "probably won't be accessed concurrently in practice."

**Related:**
[HashMap Internals](../syllabus/02-java/collections/hashmap-internals.md)
