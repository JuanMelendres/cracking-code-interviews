---
title: "Cheat Sheet: ArrayList and LinkedList Internals"
slug: arraylist-and-linkedlist-internals
document_type: cheat-sheet
domain: collections
topic_id: T-202
canonical: ../handbook/collections/arraylist-and-linkedlist-internals.md
last_updated: 2026-08-05
---

# ArrayList and LinkedList Internals

**Canonical chapter:** [`syllabus/02-java/collections/arraylist-and-linkedlist-internals.md`](../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md)

## Core Mental Model

`ArrayList` is a resizable array; `LinkedList` is a doubly-linked list of nodes — every performance difference between them follows directly from that one structural fact. Random access is fast for an array (direct indexing) and slow for a linked list (must walk node by node). Insertion at an arbitrary position is slow for an array (must shift every subsequent element) and fast for a linked list (relink two pointers) — but only at a position you already have a reference to; finding that position is itself O(n).

## Essential Definitions

- **`ArrayList`** — backed by a resizable array (`elementData`), growing by reallocating a larger array and copying elements when full.
- **`LinkedList`** — a doubly-linked list of nodes, each holding references to its predecessor and successor.
- **Amortized O(1) append** — `ArrayList`'s occasional resize-and-copy is a real O(n) cost, but averaged (amortized) across many `add()` calls it yields O(1) per call.
- **The scope of LinkedList's O(1)** — insertion/removal is O(1) only at a position already held (head, tail, or an existing `ListIterator` position). Finding an arbitrary index is itself O(n).

## Decision Table

| Access pattern | Prefer |
|---|---|
| Frequent indexed reads or iteration | `ArrayList` |
| Frequent insertion/removal at the head or tail specifically | `LinkedList` or (usually better) `ArrayDeque` |
| Frequent insertion/removal at an arbitrary, not-already-known position | Neither is fast — `ArrayList` usually wins anyway on constant factors |
| Final size roughly known in advance | `ArrayList` with an explicit initial capacity |

**Trade-offs:** `ArrayList` gives O(1) indexed access and better cache locality at the cost of O(n) arbitrary-position insertion; `LinkedList` gives O(1) head/tail insertion at the cost of O(n) indexed access and worse cache locality (nodes scattered across the heap, plus two extra reference fields per node).

## Key Numbers (real, executed)

`ArrayList`'s growth factor, measured via reflection — roughly 1.5x, not doubling:

```
size=1  -> capacity=10
size=11 -> capacity=15  (ratio=1.50)
size=23 -> capacity=33  (ratio=1.50)
size=50 -> capacity=73  (ratio=1.49)
```

Random-access `get(index)`, 50,000-element lists, 20,000 random reads:

```
ArrayList:  1,252,583 ns
LinkedList: 401,220,459 ns
LinkedList is 320.3x SLOWER for random access
```

Front-insertion, 20,000 insertions:

```
ArrayList.add(0, x):     83,334,958 ns
LinkedList.addFirst(x):     710,333 ns
ArrayList is 117.3x SLOWER for front-insertion
```

## Common Pitfalls

- Choosing `LinkedList` "for flexibility" when the actual access pattern is dominated by indexed reads.
- Assuming `LinkedList`'s O(1) insertion applies at an arbitrary index rather than only at an already-known position.
- Using `LinkedList.get(index)` in a loop — O(n²) overall instead of the O(n) an iterator-based traversal gives.
- Not sizing an `ArrayList`'s initial capacity when the final size is roughly known, incurring avoidable resize-and-copy overhead.

## Interview Answer Skeleton

**30-sec:** `ArrayList` is a resizable array (O(1) indexed access, O(n) arbitrary insertion); `LinkedList` is a doubly-linked list (O(n) indexed access, O(1) insertion at an already-known position). Measured directly: `LinkedList` is ~320x slower for random-access reads; `ArrayList` is ~117x slower for front-insertion. Choose based on the actual dominant access pattern, not habit.

**2-min:** Add why each optimizes what it does (contiguous memory vs. relinking pointers) + the real evidence (growth-factor proof via reflection, the 320x/117x measured comparisons) + the trade-off (`LinkedList`'s O(1) insertion only applies at an already-known position — finding an arbitrary position is O(n) on either structure).

**Whiteboard:** `ArrayList` as a contiguous block with direct index arrows; `LinkedList` as nodes connected by bidirectional arrows, no direct index access. Point at the `LinkedList` diagram: "getting to index 40,000 means walking through 40,000 of these arrows."

**Staff-level framing:** the gap between "LinkedList has O(1) insertion" (true, but scoped to an already-known position) and the naive reading ("LinkedList is fast to insert into, anywhere") is a specific instance of a broader pattern — Big-O claims are always scoped to a specific operation and starting condition, and generalizing past that scope produces exactly this kind of well-intentioned-but-wrong refactor.

## Production Warning Signs

- A frequently-read list refactored from `ArrayList` to `LinkedList` "for flexibility" shows a measurable latency regression right after deploy — profile for time inside `LinkedList.get(int)`'s node-traversal loop; the refactor optimized for a property (insertion ease) the code barely exercised while regressing the one (indexed read speed) it actually depended on.
- An `ArrayList` grown one element at a time from an unsized default in a hot population path — multiple avoidable resize-and-copy passes when the final size was actually predictable.
- **Prevention:** default to `ArrayList` unless the access pattern specifically favors `LinkedList`'s O(1) head/tail operations; require an explicit access-pattern justification before choosing `LinkedList` over the default.

## Related

- `syllabus/02-java/collections/collection-selection-decision-matrix.md`
