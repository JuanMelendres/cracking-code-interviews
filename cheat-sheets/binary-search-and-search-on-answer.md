---
title: "Cheat Sheet: Binary Search, Including Search-on-Answer"
slug: binary-search-and-search-on-answer
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2103
canonical: ../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md
last_updated: 2026-09-06
---

# Binary Search, Including Search-on-Answer

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md`](../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md)

## Core Mental Model

Binary search generalizes beyond "find a value in a sorted array" to "find the boundary point of a monotonic condition" — anywhere a yes/no answer flips exactly once as you move across a range (true, true, true, false, false, false), binary search finds that flip point in O(log(range)), whether the range is array indices or a candidate numeric answer.

## Essential Definitions

- **Boundary-finding binary search** — instead of returning immediately on a match, keeps narrowing toward one side, turning "find first/last position of a target" into two O(log n) searches rather than one search plus an O(n) scan.
- **Binary search on a rotated array** — compares against a boundary element (not the target) to determine which half is sorted; a genuinely different comparison strategy for finding a target value versus finding the rotation point/minimum itself.
- **Binary search on the answer** — searches a range of candidate answers using a feasibility check that is monotonic in the candidate; the monotonicity is the entire justification for the technique applying at all.
- **Partition-based binary search** (Median of Two Sorted Arrays) — binary-searches for a partition point in the smaller array so combined halves split evenly, using sentinel values (`MIN_VALUE`/`MAX_VALUE`) to handle edges uniformly.

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| Sorted array can contain many duplicates of the target | Boundary-finding binary search (two narrowing searches) |
| Array is rotated; need a specific value vs. the rotation point | Check which comparison target the problem needs (Section 4) before writing the loop |
| "Minimize/maximize X such that condition Y holds" | Binary search on the answer — first verify Y is monotonic in X |
| Cross-row sortedness guarantee on a 2D matrix | Flatten to 1D index (`mid / cols`, `mid % cols`) |

**Complexity:** Find First/Last Position O(log n) per search; Search a 2D Matrix O(log(rows·cols)); Find Minimum in Rotated Array O(log n); Capacity to Ship Packages O(n log(sum − max)); Median of Two Sorted Arrays O(log(min(m,n))).

## Common Pitfalls

- Conflating "search for a value" and "search for a boundary/rotation point" comparison logic in a rotated array — the two problems compare against different things.
- Using `(lo + hi) / 2` instead of `lo + (hi - lo) / 2` — the former silently integer-overflows for very large `lo`/`hi` values.
- Assuming binary-search-on-answer applies without checking monotonicity first — the single most common conceptual error in this variant.

## Interview Answer Skeleton

**30-sec:** Binary search eliminates half the remaining range each comparison; its most powerful generalization is binary search on the answer, which searches a candidate-value range using a monotonic feasibility check instead of searching an array at all.

**2-min:** For Capacity to Ship Packages Within D Days, `daysNeeded(capacity)` is monotonic — a larger ship only ever needs the same or fewer days — which is what licenses treating "is this capacity feasible" as the same yes/no-flips-once condition an ordinary binary search exploits, even though no array is searched. Contrast with the rotated-array case: finding a target needs two comparisons (which half is sorted, then whether the target could be in it); finding the minimum needs only the first.

**Whiteboard:** Draw the range `[lo, hi]`, mark `mid`, and show the elimination of one half based on the condition. For search-on-answer, relabel the axis as "candidate capacity/value" instead of an array index.

**Staff-level framing:** Binary-search-on-answer's real-world transfer is capacity planning: "what's the smallest instance size / pool size that keeps latency under budget" is structurally identical to "what's the smallest ship capacity that finishes within D days" — a monotonic feasibility function over a candidate range, searchable in O(log(range)) real experiments rather than a linear sweep.

## Production Warning Signs

- A binary-search implementation over a very large index range works correctly in unit tests using small arrays, but returns an incorrect result (or throws) once run against a genuinely large real dataset.
- Diagnose: check the midpoint calculation — `(lo + hi) / 2` silently overflows once `lo + hi` exceeds `Integer.MAX_VALUE`, producing a negative or nonsensical midpoint, while `lo + (hi - lo) / 2` never sums two values that could individually be large enough to overflow.

## Related

- syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
