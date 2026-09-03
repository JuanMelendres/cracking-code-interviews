---
title: "Cheat Sheet: ArrayDeque Internals and the Legacy Stack/Vector Problem"
slug: arraydeque-internals-and-the-legacy-stack-problem
document_type: cheat-sheet
domain: collections
topic_id: T-204
canonical: ../handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md
last_updated: 2026-09-02
---

# ArrayDeque Internals and the Legacy Stack/Vector Problem

**Canonical chapter:** [`syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md`](../syllabus/02-java/collections/arraydeque-internals-and-the-legacy-stack-problem.md)

## Core Mental Model

`ArrayDeque` is a circular buffer: a fixed-size array with two moving pointers (`head`, `tail`) that wrap around once they reach the end — both ends are equally cheap to grow or shrink, without ever shifting existing elements.

## Essential Definitions

- **Circular buffer** — `head`/`tail` indices move (with wraparound) instead of shifting elements; every `addFirst`/`addLast`/`pollFirst`/`pollLast` is O(1) regardless of which end.
- **Real capacity (current JDK)** — `requestedCapacity + 1`, NOT power-of-two (that was older, bitmask-modulo JDK behavior) — the extra slot disambiguates full vs. empty.
- **Legacy `Stack`/`Vector`** — extends `Vector`, whose every method is `synchronized`, paying real lock cost even single-threaded.

## Decision Table

| Question | Answer |
|---|---|
| Stack, queue, or deque use case, no indexed access needed? | `ArrayDeque` — measured faster than both `LinkedList` and `Stack` |
| Collection ever needs to hold `null`? | `ArrayDeque` disallows it (real `NullPointerException`) — use `LinkedList` or a sentinel/`Optional` |
| Existing `java.util.Stack`/`Vector` code? | Flag for replacement with `ArrayDeque` — almost never a valid reason to keep it |
| Need genuine thread safety? | Neither `ArrayDeque` nor `Stack` provides it — use `ConcurrentLinkedDeque`/`BlockingQueue` |

## Key Numbers

- Real capacity: `requested + 1` exactly at every tested size (8 → 9, not 8 or 16) — disproves outdated power-of-two folklore for JDK 21.
- 20,000,000 push+pop pairs: `java.util.Stack` 106ms vs `ArrayDeque` 47ms (~2.26x speedup) vs `LinkedList` 97ms.

## Common Pitfalls

- Reciting "ArrayDeque uses power-of-two capacity" as fact without verifying against the actual JDK — real JDK 21 behavior is `requested + 1`.
- Defaulting to `java.util.Stack` out of habit — real, unnecessary synchronization cost.
- Attempting to store `null` in an `ArrayDeque` without knowing it throws `NullPointerException`.
- Assuming `ArrayDeque`'s O(1) both-end operations mean fast indexed access in the middle — it doesn't.

## Interview Answer Skeleton

**30-sec:** `ArrayDeque` is a circular-buffer `Deque` with real O(1) both-end operations via moving `head`/`tail`. Real JDK 21 capacity is `requested + 1` — not power-of-two, correcting older-JDK folklore. It's the JDK's recommended replacement for both `LinkedList`-as-queue and legacy, synchronized `Stack`/`Vector` — measured ~2.26x faster than `Stack`. It disallows `null` (its own empty-slot sentinel).

**2-min:** Add the real circular-wraparound proof (`head=3, tail=1` — head numerically greater than tail is normal, not corruption) and the real growth pattern (5 → 12 → 26, not doubling exactly).

**Whiteboard:** A ring of array slots with `head`/`tail` arrows pointing in, an explicit wraparound arrow from the last slot back to the first. Annotate "head can end up past tail numerically — that's normal."

**Staff-level framing:** Legacy APIs designed before Java's concurrency model matured often bake in unconditional synchronization, imposing real cost on the now-dominant single-threaded case — the same pattern shows up in `Hashtable` vs `HashMap`, `StringBuffer` vs `StringBuilder`. "It's always been done this way" is not evidence a legacy type remains the right choice.

## Production Warning Signs

- A profiled regression traces to `java.util.Stack`'s synchronized `push()`/`pop()` in a purely single-threaded undo/redo buffer — real, measured ~2.26x wasted lock-acquisition cost. Fix: swap to `ArrayDeque`.

## Related

- `syllabus/02-java/collections/arraylist-and-linkedlist-internals.md`
- `syllabus/02-java/collections/collection-selection-decision-matrix.md`
- `syllabus/02-java/collections/fail-fast-vs-weakly-consistent-iterators.md`
