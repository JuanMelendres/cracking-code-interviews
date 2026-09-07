---
title: "Flashcards: Intervals, Merging, and Sweep Line"
slug: intervals-merging-and-sweep-line
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2111
canonical: ../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md
last_updated: 2026-09-07
---

# Flashcards: Intervals, Merging, and Sweep Line

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md`](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)

## Card: Interval overlap condition

**Prompt:**
When do two intervals `[a, b]` and `[c, d]` overlap?

**Answer:**
Exactly when `a <= d && c <= b` — both must start before the other ends.

**Why it matters:**
Almost every interval problem reduces to this one check; getting `<` vs `<=` wrong silently changes whether touching intervals count as overlapping.

**Common trap:**
Assuming the overlap check is symmetric in a way that lets you skip checking both directions.

**Related:**
[syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)

## Card: Sort-by-end for Minimum Number of Arrows

**Prompt:**
Why does Minimum Number of Arrows to Burst Balloons sort by end coordinate rather than start coordinate?

**Answer:**
Shooting at the earliest end among remaining balloons guarantees that shot pops every balloon whose range includes that point, and specifically pops the balloon about to "close its window" first — sorting by start loses this urgency guarantee.

**Why it matters:**
It's the exact opposite convention from the more commonly practiced "sort by start," so it directly tests whether sort-key choice is derived from the question, not habit.

**Common trap:**
Defaulting to sort-by-start because most interval problems use it, without re-deriving whether it's correct here.

**Related:**
[syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)

## Card: Meeting Rooms II heap-reuse correctness

**Prompt:**
In Meeting Rooms II, why is it always correct to check only the min-heap's minimum end time (never any other occupied room) when deciding whether to reuse a room?

**Answer:**
If the earliest-ending room isn't yet free, no other occupied room (all ending later) could be free either — so checking the minimum is both necessary and sufficient.

**Why it matters:**
This is the reasoning that justifies the O(n log n) heap-of-end-times solution over an O(n²) pairwise-overlap check.

**Common trap:**
Sorting meetings by end time instead of start time — Meeting Rooms II needs meetings processed in the order they begin.

**Related:**
[syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)

## Card: Comparator subtraction overflow bug

**Prompt:**
Why does `(a, b) -> a[1] - b[1]` silently break as an interval-endpoint comparator?

**Answer:**
Raw subtraction on `int` endpoints can overflow near `Integer.MIN_VALUE`/`MAX_VALUE` (e.g. `MIN_VALUE - MAX_VALUE` evaluates to `1`, not a large negative number), producing a genuinely wrong sort order. `Comparator.comparingLong(a -> (long) a[1])` widens before comparing and never overflows.

**Why it matters:**
This is a real, reproduced bug from this repository's own source material, verified live: the naive and safe comparators produce different orderings on the same adversarial input.

**Common trap:**
Assuming a comparator "looks fine" because it passes on typical, small test inputs, which never approach the type's boundary values.

**Related:**
[syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)

## Card: Interval List Intersections pointer advance rule

**Prompt:**
In the two-pointer merge for Interval List Intersections, which pointer do you advance after checking for overlap, and why is it always safe?

**Answer:**
Advance whichever interval ends earlier (`first[i][1] < second[j][1] ? i++ : j++`) — the interval ending earlier can never intersect anything further along in the other list either, so advancing it is always safe.

**Why it matters:**
It's the interval-specific instance of the general two-pointer-merge-of-two-sorted-sequences shape.

**Common trap:**
Advancing both pointers at once, or advancing based on start time instead of end time.

**Related:**
[syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)

## Card: Insert Interval vs. Merge Intervals complexity

**Prompt:**
Why is Insert Interval a single O(n) linear scan while the more general Merge Intervals problem must sort first?

**Answer:**
Insert Interval's input array of existing intervals is already given sorted, so a single linear scan suffices to place the new interval; Merge Intervals is given unsorted input and must pay an O(n log n) sort before it can scan.

**Why it matters:**
Recognizing "already sorted" as a given precondition — rather than re-sorting out of habit — avoids paying an unnecessary O(n log n) cost.

**Common trap:**
Re-sorting the entire interval collection for every single-interval insertion instead of exploiting the existing sortedness.

**Related:**
[syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md](../syllabus/03-data-structures-algorithms/intervals-merging-and-sweep-line.md)
