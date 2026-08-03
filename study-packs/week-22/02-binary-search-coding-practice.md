---
title: "Coding Practice — Binary Search (T-1404)"
week: 22
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Binary Search (T-1404)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 4/12 to 9/12. Previous coverage (LC 704 Binary Search, LC 35 Search Insert Position, LC 33 Search in Rotated Sorted Array, LC 875 Koko Eating Bananas, in `study-packs/week-02/07-java-coding-practice.md`) established plain binary search, insertion-point search, rotated-array search, and one binary-search-on-answer instance. This batch adds the boundary-finding variant, the 2D-matrix-as-flattened-1D trick, the minimum-in-rotated-array variant (contrasted with the earlier search-for-target version), a second binary-search-on-answer problem, and the classic hard partition-based transfer problem.

---

## Problem 1 — LC 34 Find First and Last Position of Element in Sorted Array

**Pattern:** two independent binary searches — one biased to keep searching left after a match, one biased right — rather than one search plus a linear scan outward.

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

**Retrospective:** the naive approach — find any occurrence, then linearly scan left and right to find the boundaries — is O(n) worst case if the target occupies most of the array (e.g., an array of all-target values); continuing the binary search *past* a found match (narrowing toward one side instead of stopping) keeps both boundary searches O(log n). This is the same core loop as LC 704/35 from Week 2, just with the equality branch modified to keep narrowing instead of returning immediately. **Complexity:** O(log n) time for each of the two searches.

## Problem 2 — LC 74 Search a 2D Matrix

**Pattern:** treat a row-sorted, column-sorted matrix (where every row's first element exceeds the previous row's last) as one flattened sorted array, using div/mod to map a 1D binary-search index back to 2D coordinates.

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

**Retrospective:** the specific guarantee this problem gives — each row's first value is greater than the previous row's last value — is what makes the whole matrix behave as a single sorted sequence; `mid / cols` and `mid % cols` are just the standard row-major index conversion. This is a different (and better, O(log(rows·cols))) approach than the "start top-right, eliminate a row or column each step" O(rows+cols) technique used for LC 240 (Search a 2D Matrix II), which lacks this problem's stronger sortedness guarantee — worth being able to state which guarantee unlocks which algorithm. **Complexity:** O(log(rows·cols)) time.

## Problem 3 — LC 153 Find Minimum in Rotated Sorted Array

**Pattern:** compare the middle element against the *right* boundary (not target) to decide which half is guaranteed sorted, narrowing toward the rotation point — the search-for-the-pivot counterpart to Week 2's LC 33 (search-for-a-target-value in a rotated array).

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

**Retrospective:** `nums[mid] > nums[hi]` means the minimum must lie strictly to the right of `mid` (the left half including `mid` is the "high" rotated segment), while `nums[mid] <= nums[hi]` means `mid` could itself be the minimum, so `hi` narrows to `mid` rather than `mid - 1`. This is a genuinely different comparison target than LC 33's approach (which compares against a specific search target to decide which half to discard) — the two problems look similar but solve different questions ("where is x" vs. "where is the smallest element"), and conflating their comparison logic is a common source of off-by-one bugs. **Complexity:** O(log n) time.

## Problem 4 — LC 1011 Capacity To Ship Packages Within D Days

**Pattern:** binary search on the answer space (candidate ship capacities) rather than on the input array — the second instance of this technique in the register, alongside LC 875 (Koko Eating Bananas) from Week 2.

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

**Retrospective:** the feasibility function `daysNeeded(capacity)` is monotonic — as capacity increases, days-needed only ever decreases or stays flat, never increases — and that monotonicity is precisely the property that makes binary search valid here, even though the array itself is never sorted or searched directly. `lo` starts at the largest single package weight (any smaller capacity is infeasible — some package wouldn't fit on any ship) and `hi` starts at the sum of all weights (trivially feasible — everything fits on one ship in one day). Recognizing "the answer space is monotonic in feasibility" is the transferable insight, reusable well beyond this specific problem. **Complexity:** O(n log(sum - max)) time, where the log factor is over the capacity search range.

## Problem 5 — LC 4 Median of Two Sorted Arrays

**Pattern:** partition-based binary search on the *smaller* array's cut point — the classic hard binary-search problem and a strong transfer signal.

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

**Retrospective:** rather than merging both arrays (O(m+n), fails the problem's required O(log(m+n)) bound), this binary-searches for a partition point in the *smaller* array such that the combined left half (across both arrays) has exactly `half = (m+n+1)/2` elements, all of them ≤ every element in the combined right half. Forcing the smaller array to be the one searched (the initial swap) bounds the search space to `O(log(min(m,n)))` and guarantees `cut2` is always a valid index. The sentinel values (`Integer.MIN_VALUE`/`MAX_VALUE` for out-of-bounds cuts) let the boundary comparisons work uniformly without special-casing the edges of either array — this sentinel technique generalizes to many other partition/merge problems. **Complexity:** O(log(min(m,n))) time — the actual required bound, not just "fast enough."

## Verification

```
$ cd practice/java/week-22/binary-search/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
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
