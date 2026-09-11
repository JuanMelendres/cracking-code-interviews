---
title: "Cheat Sheet: Sorting Algorithms"
slug: sorting-algorithms
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2119
canonical: ../syllabus/03-data-structures-algorithms/sorting-algorithms.md
last_updated: 2026-09-11
---

# Sorting Algorithms

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/sorting-algorithms.md`](../syllabus/03-data-structures-algorithms/sorting-algorithms.md)

## Core Mental Model

Almost every comparison-based sort is a variation on one of two ideas: repeatedly pick the next-smallest remaining element (selection-style), or split and combine already-sorted pieces (divide-and-conquer). No comparison-based sort beats O(n log n) worst case — a real, provable information-theoretic bound, not an engineering limitation.

## Essential Definitions

- **Stability** — do elements comparing equal keep their original relative order?
- **In-place** — sorts without additional memory proportional to input size.
- **Dual-Pivot QuickSort** — Java's real `Arrays.sort()` algorithm for primitive arrays.
- **TimSort** — Java's real, stable, adaptive `Arrays.sort(Object[])` algorithm (merge sort + insertion sort hybrid).

## Decision Table

| Algorithm | Worst case | Stable? | In-place? | Real note |
|---|---|---|---|---|
| QuickSort (naive pivot) | O(n²) | No | Yes | Real, measured 1,999,000 comparisons for n=2000 on sorted/reverse-sorted input |
| QuickSort (random pivot) | O(n log n) w.h.p. | No | Yes | Same inputs drop to ~23,000–24,000 comparisons |
| MergeSort | O(n log n) always | Yes | No (O(n) extra space) | Guaranteed, no exceptions |
| HeapSort | O(n log n) always | No | Yes | Heap's internal reordering has no notion of original position |
| InsertionSort | O(n²) average | Yes | Yes | Real ~O(n) on nearly-sorted input — why hybrid sorts use it for small runs |

## Common Pitfalls

- Assuming QuickSort is "just O(n log n)" without knowing its real O(n²) worst case on already-sorted/reverse-sorted input with a naive pivot.
- Assuming all sorts are stable, or that stability doesn't matter — a real bug class when sorting by one field while depending on a previous sort's tiebreak order.
- Treating InsertionSort as strictly worse than MergeSort — real, measured evidence shows it wins on small/nearly-sorted input by a wide margin.

## Interview Answer Skeleton

**30-sec:** QuickSort averages O(n log n) but has a real O(n²) worst case triggered by naive pivot choice on sorted input; MergeSort guarantees O(n log n) at the cost of O(n) space; a random pivot fixes QuickSort's worst case.

**2-min:** Add: real, measured evidence — naive first-element-pivot QuickSort hits exactly `n(n-1)/2` comparisons (1,999,000 for n=2000) on sorted/reverse-sorted input; a random pivot drops this to ~23,000–24,000. `Arrays.sort()` uses Dual-Pivot QuickSort for primitives and TimSort (real, documented stable) for objects.

**Staff-level framing:** Choosing MergeSort over QuickSort for a real-time system with a hard latency bound is a worst-case-guarantee decision, not an average-case-performance one — QuickSort's rare O(n²) case is intolerable there even though its average case is faster.

## Related

- syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md
