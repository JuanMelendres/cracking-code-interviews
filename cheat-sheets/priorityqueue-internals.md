---
title: "Cheat Sheet: PriorityQueue Internals"
slug: priorityqueue-internals
document_type: cheat-sheet
domain: 02-java/collections
topic_id: T-210
canonical: ../syllabus/02-java/collections/priorityqueue-internals.md
last_updated: 2026-09-11
---

# PriorityQueue Internals

**Canonical chapter:** [`syllabus/02-java/collections/priorityqueue-internals.md`](../syllabus/02-java/collections/priorityqueue-internals.md)

## Core Mental Model

`PriorityQueue` is a binary heap in a flat array: element at index `i` has parent `(i-1)/2`, children `2i+1`/`2i+2`, with every parent `<=` both children (min-heap). Insert appends then "sifts up"; remove-root swaps in the last element then "sifts down" — both touch only one root-to-leaf path, hence O(log n).

## Essential Definitions

- **Heap invariant** — every parent `<=` both children; says nothing about sibling order.
- **Sift up / sift down** — the O(log n) repair operations after insert/remove-root.
- **`PriorityBlockingQueue`** — the thread-safe, blocking equivalent for concurrent producer/consumer use.

## Decision Table

| Symptom | Likely cause | Fix |
|---|---|---|
| Printed order doesn't match processing order | Iterating the live queue instead of `poll()` | Copy to a `List` and sort for display; drain via `poll()` for real processing |
| Data corruption under concurrent access | `PriorityQueue` is not thread-safe | Use `PriorityBlockingQueue` or external synchronization |
| Need 2nd-smallest or range query | No efficient rank/range support in a heap | Use `TreeSet`/`TreeMap` instead |
| `ConcurrentModificationException` during iteration | Structural modification during `for`-each | Drain via `poll()` in a `while` loop instead |

## Common Pitfalls

- Assuming iteration order (or `toString()`) reflects sorted order — only `poll()`/`peek()` are ordering-guaranteed.
- Using `PriorityQueue` from multiple threads without external synchronization or `PriorityBlockingQueue`.
- Reaching for `PriorityQueue` when the real requirement is rank/range queries — a heap structurally can't do that efficiently.

## Interview Answer Skeleton

**30-sec:** `PriorityQueue` is a binary heap over a flat array; `offer()`/`poll()` are real O(log n) — verified by comparison counting, not assumed. Iteration order is not sorted order — only `poll()`/`peek()` guarantee ordering.

**2-min:** Add: it's not thread-safe or blocking (`PriorityBlockingQueue` is the concurrent alternative); it can't efficiently support rank/range queries (`TreeSet`/`TreeMap` are the right tool there).

**Staff-level framing:** Choosing `PriorityQueue` vs. `TreeSet`/`TreeMap` is a real decision point in Dijkstra/top-K-style interview problems — know which query patterns each structure actually supports before reaching for one.

## Related

- syllabus/02-java/language-core/java-time-api.md
