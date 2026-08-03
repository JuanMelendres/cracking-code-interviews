---
title: "Coding Practice — Arrays / Two-Pointers / Sliding Window (T-1402)"
week: 23
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Arrays / Two-Pointers / Sliding Window (T-1402)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 7/18 to 12/18. Previous coverage included LC 167 (Two Sum II), LC 42 (Trapping Rain Water), LC 15 (3Sum), and LC 76 (Minimum Window Substring), spread across Weeks 1, 11, and 12. This batch adds the classic two-pointer "container" problem, a monotonic-deque sliding-window problem, the prefix/suffix-product technique, in-place array rotation via reversal, and the lexicographic next-permutation algorithm.

---

## Problem 1 — LC 11 Container With Most Water

**Pattern:** two pointers starting at both ends, always advancing the pointer at the *shorter* line — the direct precursor problem to LC 42 (Trapping Rain Water, already solved), sharing the same two-pointer skeleton but a simpler area formula.

```java
static int maxArea(int[] height) {
    int lo = 0, hi = height.length - 1, best = 0;
    while (lo < hi) {
        int area = (hi - lo) * Math.min(height[lo], height[hi]);
        best = Math.max(best, area);
        if (height[lo] < height[hi]) lo++; else hi--;
    }
    return best;
}
```

**Retrospective:** advancing the shorter side is provably safe because the *current* area is already bounded by the shorter line's height — keeping that same shorter line and moving the other pointer inward can only ever produce a smaller or equal width with, at best, an equal limiting height, so it can never beat the current area; the only way to potentially find a *larger* area is to move past the shorter line and hope for a taller one. This is the same directional-pointer-movement justification technique as LC 42's more complex "track running max from both sides" logic, just applied to a simpler area formula (width × shorter height, rather than trapped-water-per-column). **Complexity:** O(n) time, O(1) space.

## Problem 2 — LC 239 Sliding Window Maximum

**Pattern:** a monotonic decreasing deque of *indices* — the maximum for each window is always the front of the deque, maintained in O(1) amortized per element.

```java
static int[] maxSlidingWindow(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) deque.pollFirst(); // expire out-of-window
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast(); // evict smaller values
        deque.offerLast(i);
        if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
    }
}
```

**Retrospective:** evicting every smaller value from the back of the deque before inserting the current index is safe because any smaller element to the left of a larger one can *never* become the window maximum again — once a bigger value arrives, the smaller one is strictly dominated for the remainder of its time in any shared window. This is the same "maintain a monotonic structure by evicting dominated candidates" idea as this repo's monotonic-stack problems (LC 496, 84, 503 from Week 21), just applied to a deque tracking a sliding window instead of a stack tracking a full left-to-right scan. **Complexity:** O(n) time — each index is pushed and popped from the deque at most once, despite the nested-looking while loops.

## Problem 3 — LC 238 Product of Array Except Self

**Pattern:** two passes — a running prefix product (stored directly in the result array), then a running suffix product multiplied in on a second pass — avoiding division entirely.

```java
static int[] productExceptSelf(int[] nums) {
    int[] result = new int[nums.length];
    result[0] = 1;
    for (int i = 1; i < nums.length; i++) result[i] = result[i - 1] * nums[i - 1];
    int suffix = 1;
    for (int i = nums.length - 1; i >= 0; i--) {
        result[i] *= suffix;
        suffix *= nums[i];
    }
    return result;
}
```

**Retrospective:** the naive approach — compute the total product, then divide by each element — breaks the moment any element is zero (division by zero) and also technically violates the problem's "no division operator" constraint; computing prefix and suffix products separately and multiplying them together for each index sidesteps both issues entirely, since a zero at position `i` simply propagates correctly through the suffix product for all `j < i` and the prefix product for all `j > i`. Reusing the `result` array itself to hold the prefix products (rather than allocating a separate array) is what gets this down to O(1) *extra* space beyond the required output array. **Complexity:** O(n) time, O(1) extra space (excluding the output array).

## Problem 4 — LC 189 Rotate Array

**Pattern:** in-place right rotation via three reversals — no auxiliary array needed.

```java
static void rotate(int[] nums, int k) {
    int n = nums.length;
    k %= n;
    reverse(nums, 0, n - 1);
    reverse(nums, 0, k - 1);
    reverse(nums, k, n - 1);
}
```

**Retrospective:** reversing the entire array first produces the correct *relative* order of both the "wrapped" segment and the "stayed" segment, but with each segment itself internally backward — reversing each segment individually afterward fixes their internal order while preserving their now-correct relative positions. The `k %= n` guard is essential and easy to forget: `k` can legally exceed `n` (e.g., rotating a 3-element array by 100), and without the modulo, `reverse(nums, 0, k-1)` would throw an `ArrayIndexOutOfBoundsException` rather than silently doing the wrong thing — a good example of a constraint that must be actively checked rather than assumed. **Complexity:** O(n) time, O(1) space — genuinely in-place, unlike an approach using an auxiliary array (also O(n) time, but O(n) space).

## Problem 5 — LC 31 Next Permutation

**Pattern:** find the rightmost ascent, swap it with the smallest larger value to its right, then reverse the suffix — the standard algorithm for generating the lexicographically next permutation in place.

```java
static void nextPermutation(int[] nums) {
    int i = n - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) i--; // find rightmost ascending pair
    if (i >= 0) {
        int j = n - 1;
        while (nums[j] <= nums[i]) j--; // find rightmost element greater than nums[i]
        swap(nums, i, j);
    }
    reverse(nums, i + 1, n - 1); // the suffix is always descending here; reverse makes it the smallest arrangement
}
```

**Retrospective:** the suffix starting right after the rightmost ascent is, by definition, entirely non-increasing (otherwise a later ascent would have been found instead) — meaning it's already at its lexicographically *largest* arrangement, so to get the *next* permutation overall, that suffix needs to become its *smallest* arrangement, which reversing it accomplishes directly. Swapping with the *smallest* value in the suffix that's still greater than `nums[i]` (found by scanning from the right, since the suffix is sorted descending) is what makes the resulting permutation the *immediate* next one, not just some larger one — using the wrong swap partner would overshoot. When no ascent exists at all (the array is fully descending, i.e., the last permutation), the `i = -1` case correctly falls through to reversing the entire array, producing the first permutation — the required "wrap around" behavior. **Complexity:** O(n) time, O(1) space.

## Verification

```
$ cd practice/java/week-23/arrays-two-pointers/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC11 maxArea(9 heights) = 49
  PASS  LC11 maxArea([1,1]) = 1
  PASS  LC239 maxSlidingWindow(k=3) = [3,3,5,5,6,7]
  PASS  LC239 maxSlidingWindow single element = [1]
  PASS  LC238 productExceptSelf([1,2,3,4]) = [24,12,8,6]
  PASS  LC238 productExceptSelf(with zero) = [0,0,9,0,0]
  PASS  LC189 rotate([1..7], k=3) = [5,6,7,1,2,3,4]
  PASS  LC189 rotate([-1,-100,3,99], k=2) = [3,99,-1,-100]
  PASS  LC31 nextPermutation([1,2,3]) = [1,3,2]
  PASS  LC31 nextPermutation([3,2,1]) wraps to [1,2,3]
  PASS  LC31 nextPermutation([1,1,5]) = [1,5,1]
Week 23 — Arrays/Two-Pointers (LC 11, 239, 238, 189, 31): 11/11 assertions passed
```
