---
title: "Binary Search, Including Search-on-Answer"
slug: binary-search-and-search-on-answer
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2103
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
  - hashing-patterns-and-frequency-maps.md
practice: ../../practice/java/week-22/binary-search/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-22/02-binary-search-coding-practice.md
---

# Binary Search, Including Search-on-Answer

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-22/02-binary-search-coding-practice.md` — real, compiled, executed code (`practice/java/week-22/binary-search/`), re-verified on OpenJDK 21.0.12 while writing this chapter (12/12 assertions passing).

This is Master Topic Register **T-1404** (IWI 5.4, very-high frequency). [Algorithmic Complexity](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#3-foundation-l1) already introduces binary search's O(log n) shape as the canonical example of that complexity class; this chapter goes deeper into the technique itself, including its most-tested and most-misunderstood variant: searching an *answer space* rather than an array.

## 1. Why This Matters

Binary search on a plain sorted array is a warm-up most candidates clear easily; the real interview differentiator is recognizing binary search's applicability where it isn't obvious — on the boundary of a match, on a rotated array, or on a space of candidate *answers* rather than array indices at all. This last variant, "binary search on the answer," is a genuinely transferable insight that turns "guess and check" optimization problems into O(log(range)) solutions, and it's tested disproportionately often relative to how rarely it's taught explicitly outside interview-specific material.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — the O(log n) complexity class and the "halves the remaining range each step" mental model this chapter builds on directly.

## 3. Foundation (L1)

**Binary search finds a target in a sorted collection by repeatedly checking the middle element and discarding the half that can't contain the answer** — exactly the phone-book analogy [Algorithmic Complexity's Foundation section](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#3-foundation-l1) uses. Each comparison eliminates half of what's left, which is why it takes only about 20 comparisons to search among a million items, rather than up to a million.

**The technique generalizes beyond "find a value in a sorted array" to "find the boundary point of a monotonic condition"** — anywhere a yes/no answer flips exactly once as you move across a range (true, true, true, false, false, false), binary search finds that flip point in O(log(range)), whether the range is array indices or something else entirely, like a candidate numeric answer.

## 4. Core Concepts (L2)

**Boundary-finding binary search** (Section 7, Problem 1) modifies the basic algorithm's equality branch: instead of returning immediately on a match, it keeps narrowing toward one side, treating the match as a candidate answer that might be improved by continuing to search past it. This turns "find the first and last position of a target" into two O(log n) searches, rather than one O(log n) search plus an O(n) linear scan outward in the worst case (an array that's mostly the target value).

**Binary search on a rotated sorted array** requires comparing against a boundary element (not the target) to determine *which half is still sorted*, then checking whether the target could lie in that sorted half. Two closely related but genuinely distinct versions exist: searching for a specific target value (comparing against the target) versus searching for the rotation point / minimum itself (Section 7, Problem 3, comparing `nums[mid]` against `nums[hi]` instead) — conflating the two comparison strategies is a common, real source of off-by-one bugs.

**Binary search on the answer** is the technique's most powerful generalization: instead of searching an array, search a *range of candidate answers* (e.g., "what capacity would let this ship finish in D days") using a feasibility check that is monotonic in the candidate — as the candidate increases, feasibility only ever improves or only ever worsens, never both. That monotonicity is the *entire* justification for why binary search applies at all here; without it, eliminating half the candidate range on each step would be unsound.

## 5. How It Works Internally (L3)

**The 2D-matrix-as-flattened-array technique** (Section 7, Problem 2) relies on a specific, stated guarantee — every row's first element exceeds the previous row's last element — which makes the entire matrix behave as a single sorted sequence when read in row-major order. Converting a flat index `mid` to 2D coordinates via `mid / cols` and `mid % cols` is the standard row-major mapping; this only works because that stronger sortedness guarantee holds. A weaker guarantee (only "each row and each column individually sorted," without the cross-row ordering) requires a genuinely different, O(rows + cols) algorithm (starting from a corner and eliminating a row or column per step) — recognizing which guarantee is actually given, and which algorithm it unlocks, is the real skill.

**Binary search on the answer's correctness argument, made precise**: for Capacity to Ship Packages Within D Days (Section 7, Problem 4), the feasibility function `daysNeeded(capacity)` is monotonic — decreasing (or flat) as `capacity` increases, since a larger ship can only ever need the same or fewer days, never more. This monotonicity is what licenses treating "is this capacity feasible" as the same kind of yes/no-flips-once condition an ordinary binary search exploits, even though no array is ever searched — the search happens directly over the integer range from "smallest possible feasible capacity" (the single heaviest package) to "trivially feasible" (the sum of everything).

**Median of Two Sorted Arrays** (Section 7, Problem 5) is the hardest example: instead of searching for a value, it binary-searches for a *partition point* in the smaller of the two arrays such that the combined left partition (across both arrays) has exactly half the total elements, and every element in that left partition is ≤ every element in the combined right partition. Forcing the search onto the *smaller* array bounds the search space to `O(log(min(m,n)))` (the actual required complexity, strictly better than the `O(log(m+n))` a naive equal-split approach might produce) and guarantees the second array's corresponding cut point is always a valid index. The sentinel values (`Integer.MIN_VALUE`/`MAX_VALUE` standing in for "off the edge of this array") let every boundary comparison work uniformly without special-casing either array's edges — a sentinel technique that generalizes well beyond this one problem, to essentially any partition- or merge-based algorithm needing uniform edge handling.

## 6. Practical Usage

- **Reach for boundary-finding binary search (two narrowing searches) instead of "find one match, then scan outward"** whenever a sorted array can contain many duplicates of the target.
- **Check which comparison target (the search value, or a boundary element) a rotated-array problem actually needs** before writing the loop — Section 4's distinction between "find a value" and "find the rotation point" is the concrete decision point.
- **Recognize a "minimize/maximize X such that condition Y holds" optimization problem as a binary-search-on-answer candidate** the moment the feasibility condition can be argued monotonic in the candidate value — this is the single most transferable pattern-recognition skill in this chapter.

## 7. Examples

**Problem 1 — LC 34, Find First and Last Position of Element in Sorted Array.**

```java
private static int findBound(int[] nums, int target, boolean findFirst) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) {
            result = mid;
            if (findFirst) hi = mid - 1; else lo = mid + 1; // keep searching past the match
        } else if (nums[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return result;
}
```

**Retrospective:** continuing the search *past* a found match (narrowing toward one side instead of returning immediately) keeps both boundary searches O(log n), avoiding an O(n) worst-case linear scan when the target occupies most of the array. **Complexity:** O(log n) per search.

**Problem 2 — LC 74, Search a 2D Matrix.**

```java
static boolean searchMatrix(int[][] matrix, int target) {
    int rows = matrix.length, cols = matrix[0].length;
    int lo = 0, hi = rows * cols - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int val = matrix[mid / cols][mid % cols];
        if (val == target) return true;
        if (val < target) lo = mid + 1; else hi = mid - 1;
    }
    return false;
}
```

**Retrospective:** see Section 5 — this problem's specific cross-row sortedness guarantee is what licenses treating the whole matrix as one flat sorted sequence. **Complexity:** O(log(rows·cols)).

**Problem 3 — LC 153, Find Minimum in Rotated Sorted Array.**

```java
static int findMin(int[] nums) {
    int lo = 0, hi = nums.length - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[hi]) lo = mid + 1; else hi = mid;
    }
    return nums[lo];
}
```

**Retrospective:** `nums[mid] > nums[hi]` means the minimum lies strictly right of `mid`; otherwise `mid` could itself be the minimum, so `hi` narrows to `mid`, not `mid - 1`. A genuinely different comparison target than "search for a specific value" in a rotated array (Section 4). **Complexity:** O(log n).

**Problem 4 — LC 1011, Capacity To Ship Packages Within D Days.**

```java
static int shipWithinDays(int[] weights, int days) {
    int lo = 0, hi = 0;
    for (int w : weights) { lo = Math.max(lo, w); hi += w; }
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (daysNeeded(weights, mid) <= days) hi = mid; else lo = mid + 1;
    }
    return lo;
}
```

**Retrospective:** see Section 5 — `daysNeeded`'s monotonicity in capacity is the entire justification for binary search applying here, despite no array ever being searched directly. **Complexity:** O(n log(sum − max)).

**Problem 5 — LC 4, Median of Two Sorted Arrays.**

```java
static double findMedianSortedArrays(int[] nums1, int[] nums2) {
    if (nums1.length > nums2.length) return findMedianSortedArrays(nums2, nums1);
    int m = nums1.length, n = nums2.length;
    int lo = 0, hi = m, half = (m + n + 1) / 2;
    while (lo <= hi) {
        int cut1 = lo + (hi - lo) / 2;
        int cut2 = half - cut1;
        int left1 = (cut1 == 0) ? Integer.MIN_VALUE : nums1[cut1 - 1];
        int left2 = (cut2 == 0) ? Integer.MIN_VALUE : nums2[cut2 - 1];
        int right1 = (cut1 == m) ? Integer.MAX_VALUE : nums1[cut1];
        int right2 = (cut2 == n) ? Integer.MAX_VALUE : nums2[cut2];
        if (left1 <= right2 && left2 <= right1) {
            return (m + n) % 2 == 0 ? (Math.max(left1, left2) + Math.min(right1, right2)) / 2.0 : Math.max(left1, left2);
        } else if (left1 > right2) hi = cut1 - 1;
        else lo = cut1 + 1;
    }
    throw new IllegalArgumentException("input arrays not sorted");
}
```

**Retrospective:** see Section 5 — a partition-point binary search on the smaller array, with sentinel edge values, achieving the required O(log(min(m,n))) bound rather than an O(m+n) merge. **Complexity:** O(log(min(m,n))).

## 8. Common Mistakes

- **Conflating "search for a value" and "search for a boundary/rotation point" comparison logic in a rotated array** — Section 4/7 names this directly as a common, real off-by-one source: the two problems look similar but compare against different things.
- **Using `(lo + hi) / 2` instead of `lo + (hi - lo) / 2`** — the former can integer-overflow for very large `lo`/`hi` values (a direct, real instance of [Number Representation's](../01-computer-science-foundations/number-representation.md) silent-overflow behavior), even though it's mathematically equivalent when no overflow occurs.
- **Assuming binary-search-on-answer applies without checking monotonicity first.** The feasibility function must genuinely be monotonic in the candidate for the technique to be sound — assuming it without verifying is the single most common conceptual error in this variant.

## 9. Edge Cases

- **An empty array, or a target not present at all** — Find First and Last Position's own verified test case (`searchRange([5,7,7,8,8,10], 6) = [-1,-1]`) covers exactly this.
- **A "rotated" array that isn't actually rotated** (Find Minimum's verified `[11,13,15,17]` case, correctly returning `11`) — the algorithm must handle the zero-rotation case without special-casing it separately.
- **Arrays of very different lengths, or one array empty**, for Median of Two Sorted Arrays — the sentinel-value technique (Section 5) is specifically what makes these edge cases fall out of the general logic rather than needing separate handling.

## 10. Performance Implications

Real, executed verification from `practice/java/week-22/binary-search/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC34 searchRange([5,7,7,8,8,10], 8) = [3,4]
  PASS  LC34 searchRange([5,7,7,8,8,10], 6) = [-1,-1]
  PASS  LC74 searchMatrix(target=3) -> true
  PASS  LC74 searchMatrix(target=13) -> false
  PASS  LC153 findMin([3,4,5,1,2]) = 1
  PASS  LC153 findMin([4,5,6,7,0,1,2]) = 0
  PASS  LC153 findMin([11,13,15,17]) not rotated = 11
  PASS  LC1011 shipWithinDays(1..10, 5 days) = 15
  PASS  LC1011 shipWithinDays([3,2,2,4,1,4], 3 days) = 6
  PASS  LC4 findMedianSortedArrays([1,3],[2]) = 2.0
  PASS  LC4 findMedianSortedArrays([1,2],[3,4]) = 2.5
  PASS  LC4 findMedianSortedArrays(uneven lengths) = 0.0 (merged [0,0,0,0,3,5,7], middle of 7)
Week 22 — Binary Search (LC 34, 74, 153, 1011, 4): 12/12 assertions passed
```

Every variant here is O(log n) or O(log(range)) — the practical performance implication is the same one [Algorithmic Complexity's own measurements](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#10-performance-implications) quantify directly for O(log n): indistinguishable from constant time across realistic input sizes, which is exactly why recognizing an applicable binary-search shape (including the answer-space variant) is worth the pattern-matching effort even for a problem that doesn't superficially look like a search at all.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Boundary-finding two-search approach | O(log n) worst case even with many duplicates | Slightly more complex loop logic than a single-match search |
| Binary search on the answer | Turns an optimization/"guess and check" problem into O(log(range)) | Requires proving monotonicity first — invalid if that assumption is wrong |
| Partition-based binary search (Median of Two Sorted Arrays) | Achieves the tight O(log(min(m,n))) bound | Meaningfully harder to implement correctly than a merge-based O(m+n) approach |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is recognizing binary-search-on-answer as applicable to a new, unfamiliar optimization problem by explicitly checking and stating the monotonicity argument — not pattern-matching to "this smells like Koko Eating Bananas" without being able to justify *why* the specific problem's feasibility function is monotonic. Being able to state the argument (as Section 5 does for shipping capacity) is what distinguishes genuine understanding from memorized template application, and is exactly what a good interviewer probes for with a variant they haven't seen a candidate rehearse.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, binary-search-on-answer's real-world transfer is capacity planning and resource sizing: "what's the smallest instance size / connection pool size / cache size that keeps latency under budget" is structurally identical to "what's the smallest ship capacity that finishes within D days" — a monotonic feasibility function (bigger resource, same-or-better performance) over a candidate range, searchable in O(log(range)) real experiments rather than a linear sweep through every candidate size. Recognizing this transfer turns an abstract interview technique into an actual, practical methodology for a real capacity-planning exercise, directly connected to [Capacity Planning and Headroom](../16-performance-jvm/capacity-planning-and-headroom.md)'s own sizing methodology.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a binary-search-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry applying binary-search-on-answer methodology to a real capacity-sizing incident (e.g., finding the minimum thread-pool or connection-pool size that avoids a specific latency regression) would be a natural, non-duplicative addition connecting this chapter to [Capacity Planning and Headroom](../16-performance-jvm/capacity-planning-and-headroom.md).

## 15. Interview Questions

### Question 1 — You need to find the smallest value `x` such that some expensive check `feasible(x)` returns true, where `feasible` is monotonic. How would you find it efficiently?

**Why interviewers ask it.** It's the direct, general-form test of binary-search-on-answer recognition — phrased abstractly enough that a candidate can't pattern-match to a memorized specific problem, forcing them to reason from the monotonicity property itself.

**Expected answer.** Binary search over the candidate range for `x`: at each step, check `feasible(mid)`; if true, the answer could be `mid` or smaller, so narrow the upper bound to `mid`; if false, narrow the lower bound to `mid + 1`. This is valid specifically because `feasible` is monotonic (once true, stays true for all larger `x`) — without that guarantee, eliminating half the range on each step would be unsound.

**Minimum acceptable answer.** Produces the binary-search loop correctly, even without explicitly naming monotonicity as the justification.

**Strong Senior answer.** States the monotonicity requirement explicitly and unprompted, and can give a concrete example (Section 7, Problem 4) of verifying it before applying the technique.

**Staff-level extension.** Connects this directly to a real capacity-planning application (Section 13) — finding a minimum viable resource size via a small number of real, monotonic feasibility experiments rather than either guessing or exhaustively testing every candidate size.

**Common mistakes.** Applying the technique without checking monotonicity at all, or defaulting to a linear scan through the candidate range out of uncertainty about whether binary search is safe here.

**Follow-up questions.** "What if `feasible` were expensive to compute — does that change anything?" (It makes the O(log(range)) factor even more valuable relative to a linear scan, and may motivate caching or memoizing `feasible` calls if they repeat.)

### Question 2 — Why does finding the minimum in a rotated sorted array need a different comparison than finding a specific target value in the same rotated array?

**Why interviewers ask it.** It tests whether "binary search on a rotated array" is understood as a family of related-but-distinct comparison strategies, or memorized as one single, over-generalized template that gets subtly misapplied to the wrong variant.

**Expected answer.** Searching for a specific target needs to determine, at each step, which half is sorted (comparing boundary values against each other) and then whether the target could lie within that sorted half (comparing against the target itself) — two separate comparisons. Searching for the minimum only needs the first comparison — determining which half is sorted — because the minimum, by definition, is the one point where the "rotation" happens, found by narrowing toward it directly without ever comparing against a target value at all.

**Minimum acceptable answer.** Recognizes the two are different problems needing different logic, even without precisely naming why.

**Strong Senior answer.** Derives the exact comparison (`nums[mid] > nums[hi]` for the minimum-finding case) and explains why comparing against `hi` rather than `lo` matters for correctness.

**Staff-level extension.** Names this as a specific instance of a general discipline: treating superficially similar problems as requiring independent correctness arguments rather than assuming a template transfers unchanged — the same discipline [Algorithmic Complexity's Staff-level section](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#13-staffsystem-level-considerations-l4) applies to scaling assumptions generally.

**Common mistakes.** Reusing the target-search comparison logic unmodified for the minimum-finding variant, producing subtle off-by-one errors that only surface on specific rotation points.

**Follow-up questions.** "What if the rotated array can contain duplicate values?" (A real, harder variant — duplicates can defeat the "compare against `hi`" strategy in specific cases, sometimes requiring a fallback to linear scan in the worst case, a genuinely worthwhile trade-off to be able to discuss.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-22/binary-search/) yourself and confirm the same 12/12 assertions pass.
- This pattern has additional real, already-solved problems from an earlier week: LC 704 (Binary Search), LC 35 (Search Insert Position), LC 33 (Search in Rotated Sorted Array), and LC 875 (Koko Eating Bananas, the *other* binary-search-on-answer instance in this repository) in `study-packs/week-02/07-java-coding-practice.md`'s underlying practice code — study Koko Eating Bananas alongside this chapter's Problem 4 to see the same technique applied to a second, independently-derived monotonic feasibility function.
- Attempt LC 240 (Search a 2D Matrix II) from scratch, and explain precisely why its weaker sortedness guarantee (rows and columns individually sorted, but no cross-row guarantee) rules out this chapter's Problem 2 approach and requires the different, O(rows + cols) corner-elimination algorithm instead.

## 17. Debugging Exercises

**Symptom:** a binary-search implementation over a very large index range works correctly in unit tests using small arrays, but returns an incorrect result (or throws an unexpected exception) once run against a genuinely large real dataset.

**Diagnose:** check the midpoint calculation — `(lo + hi) / 2` silently integer-overflows once `lo + hi` exceeds `Integer.MAX_VALUE` (a real, direct instance of [Number Representation's](../01-computer-science-foundations/number-representation.md#4-core-concepts-l2) silent-overflow behavior), producing a negative or otherwise nonsensical midpoint index — while `lo + (hi - lo) / 2` avoids this by never summing two values that could individually be large enough to overflow. Confirm by checking whether the failure threshold correlates with `lo + hi` approaching `Integer.MAX_VALUE`, and by testing the exact same logic with `long` indices, which would mask (not fix) the same underlying class of bug at a larger scale.

## 18. Design Exercises

**Design constraint:** a video-streaming service needs to determine the minimum bitrate that keeps rebuffering under a target threshold for a given network condition, re-evaluated periodically as conditions change, without exhaustively testing every possible bitrate.

Design this as a binary-search-on-answer problem directly: define the candidate range (minimum to maximum supported bitrate) and the feasibility check (does this bitrate keep rebuffering under threshold, measured via a real or simulated trial), and state explicitly why this feasibility function needs to be monotonic in bitrate for the technique to be sound (Section 5/12) — and what happens to the design's correctness if that assumption is ever violated (e.g., a codec-specific bitrate range where a slightly higher bitrate paradoxically performs worse due to encoding overhead) — naming this as the real risk of applying the technique without verifying its precondition, per Section 8's most common conceptual error.

## 19. Further Reading

- [Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — the O(log n) complexity class this entire chapter's techniques achieve.
- [Capacity Planning and Headroom](../16-performance-jvm/capacity-planning-and-headroom.md) — a real, production-grade application of the same monotonic-feasibility-search methodology, applied to sizing rather than array searching.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, how binary search eliminates half the remaining candidates each step | [Section 3](#3-foundation-l1) |
| L2 | Distinguish boundary-finding, rotated-array, and answer-space binary search variants, and choose the right comparison logic for each | [Interview Question 2](#question-2--why-does-finding-the-minimum-in-a-rotated-sorted-array-need-a-different-comparison-than-finding-a-specific-target-value-in-the-same-rotated-array) |
| L3 | State and verify the monotonicity argument required for binary-search-on-answer to apply, and explain the sentinel-value technique for partition-based binary search | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real large-scale binary-search bug (Section 17) as an integer-overflow midpoint calculation, and design a real capacity-sizing system using binary-search-on-answer methodology (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
