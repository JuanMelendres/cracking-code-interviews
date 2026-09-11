---
title: "Flashcards: PriorityQueue Internals"
slug: priorityqueue-internals
document_type: flashcard-deck
domain: 02-java/collections
topic_id: T-210
canonical: ../syllabus/02-java/collections/priorityqueue-internals.md
last_updated: 2026-09-11
---

# Flashcards: PriorityQueue Internals

**Canonical chapter:** [`syllabus/02-java/collections/priorityqueue-internals.md`](../syllabus/02-java/collections/priorityqueue-internals.md)

## Card: Iteration order vs. poll order

**Prompt:**
If you iterate a `PriorityQueue` directly with a `for`-each loop, do you get sorted order?

**Answer:**
No — the ordering guarantee applies only to `poll()`/`peek()`. Iteration (and `toString()`) reflects the internal heap array's storage order, which is not sorted.

**Why it matters:**
A real, common source of confusion — printing a `PriorityQueue` directly for debugging shows misleading "unsorted" output.

**Common trap:**
Assuming a `PriorityQueue`'s `toString()` or iterator reflects priority order.

**Related:**
[PriorityQueue Internals](../syllabus/02-java/collections/priorityqueue-internals.md)

## Card: Why offer/poll are O(log n)

**Prompt:**
Why are `PriorityQueue.offer()` and `poll()` O(log n), not O(1) or O(n)?

**Answer:**
Both operations touch only one root-to-leaf path in the underlying binary heap (sift-up on insert, sift-down on remove-root) — and that path's length is the tree's height, log n for n elements.

**Why it matters:**
Directly explains the complexity claim rather than just asserting it.

**Common trap:**
Assuming insertion is O(1) because "it just appends to the array" — it appends, then sifts up, which is the O(log n) part.

**Related:**
[PriorityQueue Internals](../syllabus/02-java/collections/priorityqueue-internals.md)

## Card: When PriorityQueue is the wrong tool

**Prompt:**
You need the 2nd-smallest element, or all elements less than X. Is `PriorityQueue` the right structure?

**Answer:**
No — a heap only efficiently exposes the single minimum (or maximum). Rank and range queries need `TreeSet`/`TreeMap`, which maintain full sorted order.

**Why it matters:**
A real decision-point interviewers probe directly in system/algorithm design questions.

**Common trap:**
Reaching for `PriorityQueue` out of habit for any "give me the smallest N" problem, without checking whether range/rank queries are also needed.

**Related:**
[PriorityQueue Internals](../syllabus/02-java/collections/priorityqueue-internals.md)
