---
title: "Coding Practice — Dynamic Programming, Full Closure (T-1411)"
week: 25
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Dynamic Programming, Full Closure (T-1411)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 27/32 to **32/32 — full register closure**, the second D14 coding-pattern closed to 100% (after Tries/T-1415 in Week 21), and — at 32 problems — by far the largest single pattern in the entire register to reach full closure (Security, Testing, and JVM reached 100% at the *domain* level in Weeks 17–19, a different granularity than these per-pattern D14 counts). Weeks 21, 23, and 24 progressively covered knapsack variants, tree DP, 2D string-matching, Catalan counting, and interval DP; this final batch adds sign-tracking product DP, a second unbounded-knapsack shape with a BFS alternative, reverse-direction 2D DP, a direct LIS follow-up, and fixed-state-machine counting DP.

---

## Problem 1 — LC 152 Maximum Product Subarray

**Pattern:** track *both* a running maximum and a running minimum ending at each position — a negative number can turn the current minimum into the new maximum, which a single running-max tracker (as used for LC 53, Maximum Subarray, a sum-based cousin) would miss entirely.

```java
static int maxProduct(int[] nums) {
    int maxEndingHere = nums[0], minEndingHere = nums[0], best = nums[0];
    for (int i = 1; i < nums.length; i++) {
        int n = nums[i];
        if (n < 0) { int tmp = maxEndingHere; maxEndingHere = minEndingHere; minEndingHere = tmp; }
        maxEndingHere = Math.max(n, maxEndingHere * n);
        minEndingHere = Math.min(n, minEndingHere * n);
        best = Math.max(best, maxEndingHere);
    }
    return best;
}
```

**Retrospective:** for sums, a negative number never turns a running minimum into a running maximum, which is why LC 53's Kadane's-algorithm-style single tracker works there — but for products, multiplying the current *most negative* running product by another negative number produces a large *positive* number, potentially the new best answer. Swapping `maxEndingHere` and `minEndingHere` whenever a negative number is encountered (before applying the multiplication) is what correctly accounts for this sign flip without needing a separate branch to recompute both from scratch. **Complexity:** O(n) time, O(1) space.

## Problem 2 — LC 279 Perfect Squares

**Pattern:** unbounded-knapsack-style DP — `dp[i]` is the minimum count of perfect squares summing to `i`, built from smaller subproblems by trying every perfect square not exceeding `i`.

```java
static int numSquares(int n) {
    int[] dp = new int[n + 1];
    Arrays.fill(dp, Integer.MAX_VALUE);
    dp[0] = 0;
    for (int i = 1; i <= n; i++) {
        for (int j = 1; j * j <= i; j++) {
            dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
        }
    }
    return dp[n];
}
```

**Retrospective:** this is the same unbounded-knapsack recurrence shape as LC 322 (Coin Change, already solved) — both ask "minimum count of items summing to a target, with unlimited reuse of each item" — but the "coin denominations" here are dynamically generated (every perfect square ≤ i) rather than a fixed input list, which is the only real difference in how the inner loop is constructed. This problem also has a well-known BFS-shortest-path alternative solution (treating each number 0..n as a graph node, with edges to `i - j²`) that LC 322 doesn't naturally admit — being able to mention that alternative, even while presenting the DP solution, is a good way to signal breadth without over-engineering the actual answer. **Complexity:** O(n·√n) time, O(n) space.

## Problem 3 — LC 174 Dungeon Game (reverse-direction 2D DP)

**Pattern:** fill the DP table from the bottom-right corner *backward* toward the top-left — the opposite fill direction from every other grid-DP problem in this register (LC 62/63/64/329), because the quantity being optimized (minimum starting health) only makes sense when computed from the destination back to the start.

```java
static int calculateMinimumHP(int[][] dungeon) {
    int[][] dp = new int[rows + 1][cols + 1];
    Arrays.fill each row with Integer.MAX_VALUE;
    dp[rows][cols - 1] = 1;
    dp[rows - 1][cols] = 1;
    for (int r = rows - 1; r >= 0; r--) {
        for (int c = cols - 1; c >= 0; c--) {
            int needed = Math.min(dp[r + 1][c], dp[r][c + 1]) - dungeon[r][c];
            dp[r][c] = Math.max(1, needed);
        }
    }
    return dp[0][0];
}
```

**Retrospective:** the natural-seeming approach — track cumulative health forward from the top-left — fails because "the minimum health needed to survive" isn't a simple running sum; a path could dip arbitrarily low at some point even if its *total* net change is positive, so the minimum starting health has to account for the worst point along the path, which can only be determined by working backward from a known target (surviving with at least 1 HP at the very end). `dp[r][c]` represents "the minimum HP needed upon *entering* cell `(r,c)` to survive the rest of the journey," and the sentinel `dp[rows][cols-1] = dp[rows-1][cols] = 1` values represent "surviving with exactly 1 HP" at the two cells adjacent to the true destination, letting the recurrence treat the destination cell uniformly with every other cell. This reverse-fill-direction technique is worth contrasting explicitly with LC 62/63/64's forward fill in an interview — same DP shape, opposite direction, because the two problems are actually asking different questions (accumulate the best total vs. guarantee a survivable minimum at every step). **Complexity:** O(rows·cols) time and space.

## Problem 4 — LC 673 Number of Longest Increasing Subsequence

**Pattern:** extends LC 300 (Longest Increasing Subsequence, already solved) by tracking a *count* alongside each length — a direct, very commonly asked follow-up to LC 300 in real interviews.

```java
for (int i = 0; i < n; i++) {
    for (int j = 0; j < i; j++) {
        if (nums[j] < nums[i]) {
            if (length[j] + 1 > length[i]) { length[i] = length[j] + 1; count[i] = count[j]; }
            else if (length[j] + 1 == length[i]) { count[i] += count[j]; }
        }
    }
}
```

**Retrospective:** LC 300 only needs `length[i]` (the longest increasing subsequence length ending at `i`); this problem adds `count[i]` (how many *distinct* subsequences achieve that length), and the two update rules mirror each other precisely — when extending from `j` produces a *strictly longer* subsequence than previously known at `i`, the count resets to inherit `j`'s count (the old, shorter-length count is now irrelevant); when it *ties* the existing best length at `i`, the counts add together (both represent genuinely different ways to reach that same length). This "extend an existing solved DP with a parallel counting array, using the exact same comparison structure" technique generalizes to many "count the optimal solutions" follow-ups beyond just this one. **Complexity:** O(n²) time, O(n) space — identical complexity class to the underlying LC 300 solution.

## Problem 5 — LC 1220 Count Vowels Permutation (fixed 5-state transition DP)

**Pattern:** a fixed, small state machine (five vowels, precisely-specified allowed transitions) — a structurally different DP category from every other problem in this register, closer to a Markov-chain step-counting problem than a subsequence or knapsack problem.

```java
long[] dp = {1, 1, 1, 1, 1}; // a, e, i, o, u — length-1 strings
for (int step = 2; step <= n; step++) {
    long[] next = new long[5];
    next[0] = (dp[1] + dp[2] + dp[4]) % MOD; // a can follow e, i, u
    next[1] = (dp[0] + dp[2]) % MOD;         // e can follow a, i
    next[2] = (dp[1] + dp[3]) % MOD;         // i can follow e, o
    next[3] = dp[2];                          // o can follow only i
    next[4] = (dp[2] + dp[3]) % MOD;         // u can follow i, o
    dp = next;
}
```

**Retrospective:** the problem statement gives *forward* transition rules ("a can only be followed by e," etc.), but the DP needs to compute, for each vowel, the sum of *predecessor* states that are allowed to transition into it — so each `next[x]` line is actually the reverse-mapping of the stated rules, which is a common source of transcription errors if solved carelessly. With only 5 states and fixed transitions, this DP runs in O(n) time regardless of `n`'s size (each step is O(1) work across a constant 5 states), a useful contrast to point out against every other DP in this register, most of which are at minimum O(n) or O(n²) *per element* of a variable-sized input. The modular arithmetic (`% MOD` at every addition, not just at the end) is a standard requirement whenever a counting DP's answer could grow exponentially large. **Complexity:** O(n) time, O(1) space (five running values regardless of `n`).

## Verification

```
$ cd practice/java/week-25/dp/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC152 maxProduct([2,3,-2,4]) = 6
  PASS  LC152 maxProduct([-2,0,-1]) = 0
  PASS  LC152 maxProduct([-2,3,-4]) = 24 (two negatives cancel)
  PASS  LC279 numSquares(12) = 3 (4+4+4)
  PASS  LC279 numSquares(13) = 2 (4+9)
  PASS  LC279 numSquares(1) = 1
  PASS  LC174 calculateMinimumHP(3x3 example) = 7
  PASS  LC174 calculateMinimumHP(single positive cell) = 1
  PASS  LC673 findNumberOfLIS([1,3,5,4,7]) = 2
  PASS  LC673 findNumberOfLIS([2,2,2,2,2]) = 5 (each length-1 subsequence)
  PASS  LC1220 countVowelPermutation(1) = 5
  PASS  LC1220 countVowelPermutation(2) = 10
  PASS  LC1220 countVowelPermutation(5) = 68
Week 25 — Dynamic Programming, final closure (LC 152, 279, 174, 673, 1220): 13/13 assertions passed
```
