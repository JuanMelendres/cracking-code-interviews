---
title: "Coding Practice — Hashing (T-1403)"
week: 22
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Hashing (T-1403)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 4/12 to 9/12. Previous coverage (LC 1 Two Sum, LC 3 Longest Substring Without Repeating Characters, LC 49 Group Anagrams, LC 242 Valid Anagram, all in `study-packs/week-01/07-java-coding-practice.md`) covered the lookup/frequency-map basics; this batch adds prefix-sum-with-hashmap, set-intersection, cycle-detection-via-hash-set, and the four-array pairing pattern. A pre-work audit confirmed LC 128, LC 347, and LC 15 are also hashing-shaped problems but are already solved and correctly categorized elsewhere (Longest Consecutive Sequence under a general-practice week, Top K Frequent under Heaps/T-1407, 3Sum under Two-pointers/T-1402) — not re-added here to avoid double-counting.

---

## Problem 1 — LC 217 Contains Duplicate

**Pattern:** `HashSet.add()`'s return value doubles as the "have I seen this?" check.

```java
static boolean containsDuplicate(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    for (int n : nums) {
        if (!seen.add(n)) return true;
    }
    return false;
}
```

**Retrospective:** `Set.add()` already returns `false` when the element was already present, which removes the need for a separate `contains()` check followed by an `add()` call — a small but real interview signal that you know the collection's actual API contract, not just its existence. **Complexity:** O(n) time, O(n) space.

## Problem 2 — LC 560 Subarray Sum Equals K

**Pattern:** running prefix sum + hash map of prefix-sum frequencies — the standard technique for "count subarrays matching a sum condition" whenever negative numbers can appear (ruling out a sliding window).

```java
static int subarraySum(int[] nums, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1);
    int prefixSum = 0, count = 0;
    for (int num : nums) {
        prefixSum += num;
        count += prefixCount.getOrDefault(prefixSum - k, 0);
        prefixCount.merge(prefixSum, 1, Integer::sum);
    }
    return count;
}
```

**Retrospective:** a subarray `[i+1..j]` sums to `k` exactly when `prefixSum[j] - prefixSum[i] == k`, i.e. `prefixSum[i] == prefixSum[j] - k` — so at each position, counting how many earlier prefix sums equal `prefixSum - k` counts every valid subarray ending here, in one pass. Seeding `prefixCount` with `{0: 1}` before the loop starts is what correctly counts subarrays starting at index 0. This is the reason negative numbers break the sliding-window approach that works for LC 209 (Minimum Size Subarray Sum, non-negative only) — window growth/shrinkage isn't monotonic once negative values are allowed, but the prefix-sum-plus-hash-map technique doesn't depend on monotonicity at all. **Complexity:** O(n) time, O(n) space.

## Problem 3 — LC 349 Intersection of Two Arrays

**Pattern:** two hash sets — one to dedupe the first array's membership, one to dedupe the result.

```java
static int[] intersection(int[] nums1, int[] nums2) {
    Set<Integer> set1 = new HashSet<>();
    for (int n : nums1) set1.add(n);
    Set<Integer> result = new HashSet<>();
    for (int n : nums2) {
        if (set1.contains(n)) result.add(n);
    }
    // convert to array...
}
```

**Retrospective:** the problem's own constraint — "each element in the result must be unique" — is exactly why the result also needs to be a `Set`, not a `List`; a naive scan-and-append would produce duplicate entries whenever a value repeats in `nums2`. **Complexity:** O(n + m) time, O(n) space.

## Problem 4 — LC 202 Happy Number

**Pattern:** hash-set-based cycle detection — a number either reaches 1 or enters a repeating cycle; there is no third outcome.

```java
static boolean isHappy(int n) {
    Set<Integer> seen = new HashSet<>();
    while (n != 1 && seen.add(n)) {
        n = sumOfSquaredDigits(n);
    }
    return n == 1;
}
```

**Retrospective:** the loop condition `seen.add(n)` doubles as both the cycle check and the insertion — the loop stops the moment either `n` becomes 1 (happy) or `n` is seen a second time (this is provably always what happens for unhappy numbers — the sum-of-squared-digits sequence is bounded, so by pigeonhole it must eventually repeat a value). This is the same hash-set cycle-detection idea as Floyd's tortoise-and-hare (used in Week 20's LC 141/143 linked-list problems), just applied to a numeric sequence instead of a linked list — worth naming that connection explicitly in an interview. **Complexity:** O(log n) per digit-sum computation, and the cycle length is bounded by a small constant in practice, so effectively O(1) amortized despite the unbounded-looking `while` condition.

## Problem 5 — LC 454 4Sum II

**Pattern:** split four arrays into two pairs, hash the sum-frequency of the first pair, then look up the negated sum of the second pair — turns an O(n⁴) brute force into O(n²).

```java
static int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
    Map<Integer, Integer> sumCounts = new HashMap<>();
    for (int a : nums1) for (int b : nums2) sumCounts.merge(a + b, 1, Integer::sum);
    int count = 0;
    for (int c : nums3) for (int d : nums4) count += sumCounts.getOrDefault(-(c + d), 0);
    return count;
}
```

**Retrospective:** the key restructuring is recognizing `a+b+c+d == 0` is equivalent to `a+b == -(c+d)` — once seen that way, the problem decomposes into two independent O(n²) passes joined by a hash map, instead of one O(n⁴) quadruple-nested loop. This "split into two halves, hash one half, probe with the other" restructuring is a reusable technique whenever a problem's brute force is a product of independent choices (contrast with LC 15's 3Sum, which needs the two-pointer technique instead because its three indices are *not* independent — they're drawn from the same single array). **Complexity:** O(n²) time, O(n²) space.

## Verification

```
$ cd practice/java/week-22/hashing/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC217 containsDuplicate([1,2,3,1]) -> true
  PASS  LC217 containsDuplicate([1,2,3,4]) -> false
  PASS  LC560 subarraySum([1,1,1], 2) = 2
  PASS  LC560 subarraySum([1,2,3], 3) = 2
  PASS  LC560 subarraySum([1,-1,0], 0) = 3 (negatives handled)
  PASS  LC349 intersection([1,2,2,1],[2,2]) = [2]
  PASS  LC349 intersection([4,9,5],[9,4,9,8,4]) = [4,9]
  PASS  LC202 isHappy(19) -> true
  PASS  LC202 isHappy(2) -> false (cycles, never reaches 1)
  PASS  LC454 fourSumCount(4 arrays of 2) = 2
  PASS  LC454 fourSumCount(4 zero arrays) = 1
Week 22 — Hashing (LC 217, 560, 349, 202, 454): 11/11 assertions passed
```
