---
title: "Cheat Sheet: Arrays, Two Pointers, and Sliding Window"
slug: arrays-two-pointers-and-sliding-window
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2101
canonical: ../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md
last_updated: 2026-09-06
---

# Arrays, Two Pointers, and Sliding Window

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md`](../syllabus/03-data-structures-algorithms/arrays-two-pointers-and-sliding-window.md)

## Core Mental Model

Both techniques exist because most pairs or subarrays a brute-force approach would check are provably not worth checking, given what's already been learned from a related pair or window — eliminating an O(n²) rescan by proving specific comparisons are safe to skip, not by memorized incantation.

## Essential Definitions

- **Opposite-direction two pointers** — start and end pointers move toward each other based on a monotonic comparison; right when a monotonic property lets you decide which side to move (e.g., Container With Most Water).
- **Same-direction two pointers** — both pointers move forward, at different speeds or with a fixed gap; used for cycle detection and "Nth from the end" problems.
- **Sliding window (fixed-size)** — window length is given directly; each step adds one element and removes one.
- **Sliding window (variable-size)** — window grows/shrinks based on a condition; needs an explicit shrink-from-the-left rule.
- **Monotonic deque** — maintains candidate indices in decreasing order of value, evicting any index whose value can never again be the answer once a larger, more recent value dominates it (Sliding Window Maximum).

## Recognition Signals / When to Use This Pattern

| Signal | Technique |
|---|---|
| Array is sorted, or a comparison at both ends tells you which side to move | Opposite-direction two pointers |
| Problem asks about a contiguous subarray/substring satisfying a condition | Sliding window (fixed or variable size) |
| Need info from both directions simultaneously, can't compute in one pass | Two-pass prefix/suffix |
| "Max/min over every window of size k" | Monotonic deque |

**Complexity:** All five worked problems (Container With Most Water, Sliding Window Maximum, Product of Array Except Self, Rotate Array, Next Permutation) run O(n) time, O(1) extra space (Product of Array Except Self excludes the required output array from that count).

## Common Pitfalls

- Reaching for a nested loop out of habit before checking whether a monotonic property makes two pointers or a sliding window applicable.
- Forgetting to bound a rotation/shift parameter against the actual array length — `k %= n` is required correctness, not a defensive nicety, since a problem's own constraints can allow `k` to exceed `n`.
- Using a variable-size sliding window's shrink condition incorrectly — shrinking too eagerly (missing valid windows) or not eagerly enough (degenerating into an O(n²) scan in disguise).

## Interview Answer Skeleton

**30-sec:** Two pointers and sliding windows replace an O(n²) brute force with an O(n) pass by proving, at each step, that a whole class of remaining comparisons or re-scans can never beat what's already been found.

**2-min:** Use Container With Most Water: moving the pointer at the shorter line is always safe because the current area is already capped by that shorter height, so keeping it and moving the other pointer can only shrink the width without any chance of a taller limiting height — the same "evict dominated candidates" logic reappears in Sliding Window Maximum's monotonic deque, applied to a bounded window instead of two endpoints.

**Whiteboard:** Draw the array with `lo` at one end and `hi` at the other; show the area/height comparison, then draw the arrow moving the shorter side inward. For sliding window, draw a `[left, right]` bracket over the array and show it growing/shrinking one side at a time.

**Staff-level framing:** A service that repeatedly re-scans an already-processed prefix of a large collection on every incremental update is paying an avoidable O(n²) cost for the same reason a naive brute-force interview solution does — the fix is the same underlying idea: maintain enough incremental state (a pointer, a window, a monotonic structure) to avoid redoing work already provably settled.

## Production Warning Signs

- An in-place array rotation function works correctly in every manual test during development but throws `ArrayIndexOutOfBoundsException` intermittently once deployed and receiving real, varied input.
- Fix: check whether the rotation amount `k` is validated against the array's actual length before use (`k %= n`) — `k` can legally exceed `n` per most rotation-problem specifications, and skipping the modulo means any larger `k` reaches a reversal call with an out-of-bounds index.

## Related

- syllabus/01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
