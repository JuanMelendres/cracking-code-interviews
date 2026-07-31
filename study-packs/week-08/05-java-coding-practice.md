---
title: "Java Coding Practice — Week 8"
week: 8
last_reviewed: 2026-07-31
---

# Java Coding Practice — Week 8

**T-1411 DP part 1 — LC 70, 198, 322, 300. All code compiled and executed, including a 200-trial randomized cross-check of the O(n log n) LIS solution against an O(n²) reference — see the verification block and `MANIFEST.md`.**

## Table of Contents

1. [LC 70 — Climbing Stairs](#lc-70--climbing-stairs)
2. [LC 198 — House Robber](#lc-198--house-robber)
3. [LC 322 — Coin Change](#lc-322--coin-change)
4. [LC 300 — Longest Increasing Subsequence](#lc-300--longest-increasing-subsequence)
5. [Verification](#verification--real-not-asserted)

---

## LC 70 — Climbing Stairs

```java
static int climbStairs(int n) {
    if (n <= 2) return n;
    int prev2 = 1, prev1 = 2;
    for (int i = 3; i <= n; i++) {
        int cur = prev1 + prev2;
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

**Invariant:** `dp[i] = dp[i-1] + dp[i-2]` — reaching step `i` means arriving from `i-1` (a 1-step) or `i-2` (a 2-step), the only two ways in. It's the Fibonacci recurrence wearing a different problem statement — recognizing that shape is the actual skill tested. **Complexity:** O(n) time, O(1) space with rolling variables.

## LC 198 — House Robber

```java
static int rob(int[] nums) {
    int prev2 = 0, prev1 = 0;
    for (int n : nums) {
        int cur = Math.max(prev1, prev2 + n);
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

**Invariant:** `dp[i] = max(dp[i-1], dp[i-2] + nums[i])` — at house `i`, either skip it (carry forward the best through `i-1`) or rob it (best through `i-2` plus this house's value); adjacency is the only constraint, so the recurrence only looks two steps back. **Complexity:** O(n) time, O(1) space.

## LC 322 — Coin Change

```java
static int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1); // sentinel: "unreachable so far"
    dp[0] = 0;
    for (int a = 1; a <= amount; a++) {
        for (int c : coins) {
            if (c <= a) dp[a] = Math.min(dp[a], dp[a - c] + 1);
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
```

**Invariant:** bottom-up unbounded knapsack — `dp[a]` is the fewest coins to make amount `a`, built from smaller amounts already solved; amounts in the OUTER loop, coins in the inner loop (not the reverse) is what allows unlimited reuse of each denomination. The `amount + 1` sentinel is larger than any valid answer (max coins needed is `amount` itself, all 1s), so it safely signals "unreached" without a separate boolean array. **Complexity:** O(amount · coins.length) time, O(amount) space.

## LC 300 — Longest Increasing Subsequence

```java
static int lengthOfLIS(int[] nums) {
    int[] tails = new int[nums.length];
    int len = 0;
    for (int n : nums) {
        int lo = 0, hi = len;
        while (lo < hi) {              // binary search: first tails[mid] >= n
            int mid = (lo + hi) / 2;
            if (tails[mid] < n) lo = mid + 1; else hi = mid;
        }
        tails[lo] = n;
        if (lo == len) len++;
    }
    return len;
}
```

**Invariant:** `tails[k]` holds the smallest possible tail value of any increasing subsequence of length `k+1` seen so far — NOT a real subsequence, just the best ending value per length, which is what makes binary search over it valid (the array stays sorted by construction). Each new number either extends the longest run (`lo == len`) or replaces an existing tail with a smaller one, improving future extensibility without changing `len`. Materially different from the textbook O(n²) `dp[i] = max(dp[j] + 1)` formulation — same problem, different state representation, one order of magnitude faster. **Complexity:** O(n log n) time, O(n) space.

## Verification — real, not asserted

```
== LC 70: Climbing Stairs ==
  PASS  climbStairs(1) = 1
  PASS  climbStairs(2) = 2
  PASS  climbStairs(3) = 3 (1+1+1, 1+2, 2+1)
  PASS  climbStairs(5) = 8 (Fibonacci shape)

== LC 198: House Robber ==
  PASS  rob([1,2,3,1]) = 4 (rob house 0 and 2)
  PASS  rob([2,7,9,3,1]) = 12 (rob houses 0,2,4)
  PASS  rob([]) = 0

== LC 322: Coin Change ==
  PASS  coinChange([1,2,5], 11) = 3 (5+5+1)
  PASS  coinChange([2], 3) = -1 (unreachable)
  PASS  coinChange([1], 0) = 0

== LC 300: Longest Increasing Subsequence ==
  PASS  lengthOfLIS([10,9,2,5,3,7,101,18]) = 4 (2,3,7,101 or 2,3,7,18)
  PASS  lengthOfLIS([7,7,7,7]) = 1 (strictly increasing)
  PASS  lengthOfLIS([0,1,0,3,2,3]) = 4

== LC 300 cross-check: O(n log n) vs O(n^2) reference on random inputs ==
  PASS  O(n log n) LIS matches O(n^2) reference on 200 random trials
Week 8 DP suite: 14/14 assertions passed
```

Full source: `practice/java/week-08/dp/src/`. Reproduce: `cd practice/java/week-08/dp && javac -d out src/*.java && java -cp out Main`.

## Exit check

- [ ] All 4 problems solved with a written retrospective
- [ ] Can derive the recurrence relation for each problem from first principles, not from memory
- [ ] Can explain what `tails[]` actually represents in the O(n log n) LIS solution (not a real subsequence) unprompted
