---
title: "Flashcards: Binary Search, Including Search-on-Answer"
slug: binary-search-and-search-on-answer
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: "T-2103"
canonical: ../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md
last_updated: 2026-09-07
---

# Flashcards: Binary Search, Including Search-on-Answer

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md`](../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md)

## Card: The midpoint-overflow bug

**Prompt:**
Why does `lo + (hi - lo) / 2` avoid a bug that `(lo + hi) / 2` has?

**Answer:**
`(lo + hi) / 2` can silently integer-overflow once `lo + hi` exceeds `Integer.MAX_VALUE`, producing a negative or nonsensical midpoint. `lo + (hi - lo) / 2` never sums two values that could individually be large enough to overflow.

**Why it matters:**
The bug works correctly on small unit-test arrays and fails only once run against a genuinely large real dataset — a classic scale-dependent bug.

**Common trap:**
Using `(lo + hi) / 2` because it's mathematically equivalent when no overflow occurs.

**Related:**
[syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md](../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md)

## Card: The monotonicity precondition for binary search on the answer

**Prompt:**
What single property must a feasibility function have for "binary search on the answer" to be valid?

**Answer:**
Monotonicity — as the candidate answer increases, feasibility only ever improves or only ever worsens, never both (once true, stays true for all larger `x`, or the reverse). Without that guarantee, eliminating half the candidate range each step would be unsound.

**Why it matters:**
Assuming this without verifying it is the single most common conceptual error in this variant.

**Common trap:**
Applying binary-search-on-answer without checking monotonicity first.

**Related:**
[syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md](../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md)

## Card: Why boundary-finding search continues past a match

**Prompt:**
In Find First and Last Position of Element in Sorted Array (LC 34), why does the search keep narrowing past a found match instead of returning immediately?

**Answer:**
Continuing to search past a match — narrowing toward one side instead of returning immediately — keeps both boundary searches O(log n), avoiding an O(n) worst-case linear scan when the target occupies most of the array.

**Why it matters:**
Turns "find first/last position" into two O(log n) searches rather than one O(log n) search plus a potential O(n) scan outward.

**Common trap:**
Returning on the first match found instead of treating it as a candidate that might still be improved.

**Related:**
[syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md](../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md)

## Card: Rotated-array target search vs. minimum-finding

**Prompt:**
Why does finding the minimum in a rotated sorted array (LC 153) use a different comparison than searching for a specific target value in the same array?

**Answer:**
Target search needs two comparisons: which half is sorted (boundary vs. boundary), then whether the target could lie in that sorted half (boundary vs. target). Minimum-finding only needs the first comparison (`nums[mid] > nums[hi]` narrows right), since the minimum is the one point where the rotation happens, found by narrowing toward it directly without ever comparing against a target value.

**Why it matters:**
Conflating the two comparison strategies is a common, real source of off-by-one bugs.

**Common trap:**
Reusing target-search comparison logic unmodified for the minimum-finding variant.

**Related:**
[syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md](../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md)

## Card: Why Median of Two Sorted Arrays searches the smaller array

**Prompt:**
In Median of Two Sorted Arrays (LC 4), why does the algorithm binary search over the smaller array specifically, using sentinel values at the edges?

**Answer:**
Forcing the search onto the smaller array bounds the search space to O(log(min(m,n))) rather than a looser O(log(m+n)), and guarantees the second array's corresponding cut point is always a valid index. Sentinel values (`Integer.MIN_VALUE`/`MAX_VALUE`, standing in for "off the edge") let every boundary comparison work uniformly without special-casing either array's edges.

**Why it matters:**
Achieves the tight, actually-required complexity bound, and the sentinel technique generalizes to any partition- or merge-based algorithm needing uniform edge handling.

**Common trap:**
Binary searching the larger array, or omitting sentinel handling and needing separate edge-case branches instead.

**Related:**
[syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md](../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md)
