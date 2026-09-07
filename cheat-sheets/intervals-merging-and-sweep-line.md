---
title: "Cheat Sheet: Intervals, Merging, and Sweep Line"
slug: intervals-merging-and-sweep-line
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2111
canonical: ../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md
last_updated: 2026-09-06
---

# Intervals, Merging, and Sweep Line

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md`](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)

## Core Mental Model

An interval problem's difficulty is almost always in choosing the correct sort key (by start, or by end) and handling the boundary comparison correctly. Two intervals `[a, b]` and `[c, d]` overlap exactly when `a <= d && c <= b` — both must start before the other ends. Sorting is almost always the first step; *which* field to sort by depends on the specific question being asked, not on habit.

## Essential Definitions

- **Overlap condition** — `[a, b]` and `[c, d]` overlap iff `a <= d && c <= b`.
- **Sort by start** — correct for merging overlapping ranges or inserting into an already-sorted interval list.
- **Sort by end** — correct for greedy "minimum covering points" selection (an interval's urgency is set by when it *ends*, not when it begins).
- **Min-heap of end times** — answers "how many resources are needed simultaneously," a different question from "can these all fit without overlap at all."

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Complexity |
|---|---|---|
| Merge overlapping ranges, or insert one new interval into an already-sorted list | Sort by start, single linear scan (three-phase insert if already sorted) | O(n) if pre-sorted, else O(n log n) |
| "Minimum number of rooms/resources needed simultaneously" | Sort by start; min-heap of end times, reuse when heap min ≤ new start | O(n log n) |
| "Minimum number of covering points/arrows" to hit every interval | Sort by **end**; greedily place a point at each remaining earliest end | O(n log n) |
| Intersect two independently sorted interval lists | Two-pointer merge, advance whichever interval ends first | O(n + m) |

## Common Pitfalls

- Sorting by start when the problem actually needs sorting by end (or vice versa) — Minimum Number of Arrows is specifically designed to catch this.
- Comparing interval endpoints with raw subtraction (`(a, b) -> a[1] - b[1]`) instead of `Comparator.comparingLong` or an explicit widening cast — a real, reproduced overflow bug existed in this chapter's own source material: `Integer.MIN_VALUE - Integer.MAX_VALUE` evaluates to `1`, not a large negative number.
- Using `<` instead of `<=` (or vice versa) in an overlap check — whether touching-but-not-overlapping intervals (`[1,2]` and `[2,3]`) count as overlapping is problem-specific and changes the correct operator.

## Complexity Reference

- Insert Interval (pre-sorted input): O(n) time, O(n) space.
- Meeting Rooms II (min-heap of end times): O(n log n).
- Minimum Number of Arrows (sort by end): O(n log n).
- Interval List Intersections (two-pointer): O(n + m).

## Interview Answer Skeleton

**30-sec:** An interval `[start, end]` problem reduces to detecting overlap (`a <= d && c <= b`) and choosing the right sort key — start for merging/tracking active ranges, end for greedy covering-point selection — then a single linear scan.

**2-min:** Define overlap first, then pick the sort key from the actual question being asked, not habit. For "how many resources needed simultaneously" (Meeting Rooms II), sort by start and maintain a min-heap of end times — the heap's minimum is always the only room worth checking for reuse, since if the earliest-ending room isn't free, no later-ending room could be either. For "fewest covering points" (Minimum Number of Arrows), sort by end instead, since urgency is set by when a balloon's window closes, not when it opens.

**Whiteboard:** Draw a number line with several `[start, end]` bars stacked at different heights. For Meeting Rooms II, walk left to right, and next to each bar's start, show whether the heap's current minimum end time is ≤ that start (reuse, draw an arrow reusing the room) or not (draw a new stacked room). For Minimum Number of Arrows, sort the same bars by their right edge and show a vertical line ("arrow") placed at the first remaining right edge, popping every bar it crosses.

**Staff-level framing:** A comparator using raw subtraction on values that could plausibly reach the extremes of their type's range is a latent, silent-failure-mode bug — common enough in real Java codebases to be a standing code-review heuristic, not a one-off fix. Meeting Rooms II's heap-based resource-tracking technique also transfers directly to real capacity-planning questions: "how many concurrent database connections/worker threads/API rate-limit slots are needed at peak" is structurally the same "track currently-active intervals, minimize simultaneous count" question.

## Production Warning Signs

- **Symptom:** a scheduling feature's sort-based conflict-resolution logic behaves correctly for almost all real data but occasionally produces a bizarre, clearly-wrong ordering for a small number of specific records.
- **Diagnose:** check whether the sort comparator uses raw subtraction on the sorted field, and whether that field's real-world values could plausibly approach the extremes of its integer type's range (a very large or very small timestamp, ID, or duration value). Confirm by testing the comparator against a deliberately constructed adversarial input pair whose difference would overflow, comparing its output against `Comparator.comparingLong` (or an equivalent widening approach).

## Related

- syllabus/03-data-structures-algorithms/heaps-top-k-and-k-way-merge.md
- syllabus/01-computer-science-foundations/number-representation.md
