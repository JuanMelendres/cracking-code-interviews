---
title: "Coding Practice — Dynamic Programming (T-1411)"
week: 21
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Dynamic Programming (T-1411)

**7 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 9/32 to 16/32 — the register's single largest raw-count pattern, and (before this batch) its lowest coverage ratio despite that. Previous coverage (LC 70 Climbing Stairs, LC 198 House Robber, LC 322 Coin Change, LC 300 LIS, LC 62 Unique Paths, LC 1143 LCS, LC 416 Partition Equal Subset Sum, LC 5 Longest Palindromic Substring, LC 139 Word Break — spread across weeks 8, 9, and 12) left specific category gaps: edit-distance-style two-string DP, the circular-array variant of a classic linear DP, unbounded vs 0/1 knapsack *counting* variants, matrix-path DP, and stock-trading state-machine DP were all completely absent. This batch closes exactly those gaps.

---

## Problem 1 — LC 72 Edit Distance

**Pattern:** classic two-string grid DP — the general form that LC 1143 (LCS) and LC 5 (palindromic substring) are both restricted special cases of.

```java
static int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
    }
    return dp[m][n];
}
```

**Retrospective:** `dp[i][j]` represents the edit distance between the first `i` characters of `word1` and first `j` of `word2`; the three min-branches correspond exactly to the three allowed operations — `dp[i-1][j-1]` is a substitution, `dp[i-1][j]` is a deletion from `word1`, `dp[i][j-1]` is an insertion into `word1`. The base-case rows/columns (`dp[i][0]=i`, `dp[0][j]=j`) represent transforming a string into empty (or vice versa) via pure deletions/insertions. **Complexity:** O(m·n) time and space.

## Problem 2 — LC 213 House Robber II (circular street)

**Pattern:** reduce a circular-constraint DP to two runs of the already-known linear DP (LC 198), by observing the first and last house can't both be robbed.

```java
static int robCircular(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    return Math.max(robLinear(nums, 0, n - 2), robLinear(nums, 1, n - 1));
}

private static int robLinear(int[] nums, int start, int end) {
    int prev2 = 0, prev1 = 0;
    for (int i = start; i <= end; i++) {
        int cur = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

**Retrospective:** since houses 0 and n-1 are adjacent in a circle, any valid robbery plan either excludes house 0 or excludes house n-1 (never neither, and never can it include both) — so the answer is just the better of "rob linear range [0, n-2]" and "rob linear range [1, n-1]", reusing LC 198's exact O(1)-space rolling recurrence unchanged for each range. This is a strong example of recognizing a *reduction* to a previously-solved subproblem rather than re-deriving a new recurrence from scratch — a good Staff-level interview signal. **Complexity:** O(n) time, O(1) space.

## Problem 3 — LC 518 Coin Change II (unbounded knapsack, counting combinations)

**Pattern:** unbounded-knapsack counting DP — contrasts with LC 322's *minimum coins* framing by counting *combinations* instead, and with LC 494's 0/1 framing by allowing unlimited reuse per coin.

```java
static int change(int amount, int[] coins) {
    int[] dp = new int[amount + 1];
    dp[0] = 1;
    for (int coin : coins) {
        for (int a = coin; a <= amount; a++) {
            dp[a] += dp[a - coin];
        }
    }
    return dp[amount];
}
```

**Retrospective:** the outer loop over coins (not amounts) is what makes this count *combinations* instead of *permutations* — iterating amount-first would count `[1,2]` and `[2,1]` as distinct ways to make 3, but iterating coin-first ensures each combination is only ever built in one canonical coin order. This coin-outer vs amount-outer loop-order distinction is one of the most common subtle DP bugs and a frequent follow-up question. **Complexity:** O(amount · coins.length) time, O(amount) space.

## Problem 4 — LC 494 Target Sum (0/1 knapsack via sum-partition transform)

**Pattern:** transform a `+`/`-` sign-assignment problem into a 0/1 subset-sum counting problem via algebra, then apply the standard knapsack-counting recurrence.

```java
static int findTargetSumWays(int[] nums, int target) {
    int total = Arrays.stream(nums).sum();
    if (Math.abs(target) > total || (total + target) % 2 != 0) return 0;
    int subsetSum = (total + target) / 2;
    int[] dp = new int[subsetSum + 1];
    dp[0] = 1;
    for (int num : nums) {
        for (int s = subsetSum; s >= num; s--) {
            dp[s] += dp[s - num];
        }
    }
    return dp[subsetSum];
}
```

**Retrospective:** the key insight is algebraic, not algorithmic — splitting nums into a positive-sign subset P and negative-sign subset N, `P - N = target` and `P + N = total` together give `P = (total + target) / 2`, turning "count sign assignments" into "count subsets summing to P," a standard 0/1 knapsack counting problem. The inner loop runs `s` *downward* (`subsetSum` to `num`), not upward like LC 518's — that direction is what enforces "each element used at most once" (0/1 knapsack) instead of "each element reusable" (unbounded knapsack); this pairing with Problem 3 above is exactly the interview-relevant contrast to be able to state on demand. **Complexity:** O(n · subsetSum) time.

## Problem 5 — LC 64 Minimum Path Sum

**Pattern:** matrix-path DP — each cell's answer depends only on the cell above and to the left.

```java
static int minPathSum(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    int[][] dp = new int[rows][cols];
    dp[0][0] = grid[0][0];
    for (int c = 1; c < cols; c++) dp[0][c] = dp[0][c - 1] + grid[0][c];
    for (int r = 1; r < rows; r++) dp[r][0] = dp[r - 1][0] + grid[r][0];
    for (int r = 1; r < rows; r++)
        for (int c = 1; c < cols; c++)
            dp[r][c] = grid[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
    return dp[rows - 1][cols - 1];
}
```

**Retrospective:** this is the direct dollar-cost cousin of LC 62 (Unique Paths, already covered) — same grid-DP shape and same only-right-or-down movement constraint, but summing costs via `Math.min` instead of counting paths via addition. Recognizing "I've solved this shape before, just swap the combine operator" is a fast way to signal pattern fluency rather than re-deriving grid DP from scratch. **Complexity:** O(rows·cols) time; solvable in O(cols) space with a rolling 1D array, though the 2D form is shown here for clarity.

## Problem 6 — LC 516 Longest Palindromic Subsequence

**Pattern:** interval DP over `(i, j)` ranges, filled from shortest to longest range — contrasts with LC 5's *substring* (contiguous) framing by allowing non-contiguous characters.

```java
static int longestPalindromeSubseq(String s) {
    int n = s.length();
    int[][] dp = new int[n][n];
    for (int i = n - 1; i >= 0; i--) {
        dp[i][i] = 1;
        for (int j = i + 1; j < n; j++) {
            if (s.charAt(i) == s.charAt(j)) {
                dp[i][j] = 2 + (i + 1 <= j - 1 ? dp[i + 1][j - 1] : 0);
            } else {
                dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
            }
        }
    }
    return dp[0][n - 1];
}
```

**Retrospective:** the fill order matters critically — computing `dp[i][j]` requires `dp[i+1][j-1]`, `dp[i+1][j]`, and `dp[i][j-1]` to already be known, all of which cover *shorter* ranges than `[i,j]`, which is why `i` iterates downward from `n-1` and `j` iterates upward from `i+1` (guaranteeing every dependency is filled before it's read). This range-DP fill-order discipline is the same underlying pattern behind interval-scheduling and matrix-chain-multiplication style problems, worth naming explicitly as a family. **Complexity:** O(n²) time and space.

## Problem 7 — LC 309 Best Time to Buy and Sell Stock with Cooldown

**Pattern:** state-machine DP with three named states (holding, just-sold, resting) tracked via rolling variables, no array needed.

```java
static int maxProfitCooldown(int[] prices) {
    if (prices.length == 0) return 0;
    int hold = -prices[0], sold = 0, rest = 0;
    for (int i = 1; i < prices.length; i++) {
        int prevHold = hold, prevSold = sold, prevRest = rest;
        hold = Math.max(prevHold, prevRest - prices[i]);
        sold = prevHold + prices[i];
        rest = Math.max(prevRest, prevSold);
    }
    return Math.max(sold, rest);
}
```

**Retrospective:** the cooldown constraint (can't buy the day immediately after selling) is exactly why a plain greedy or two-pointer approach fails here, unlike simpler stock problems — the three-state machine (`hold`: currently holding a share; `sold`: sold today, must rest tomorrow; `rest`: not holding and free to buy) makes the cooldown structural rather than something to special-case with an `if`. `rest`'s transition specifically excludes `prevHold` — you can only enter `rest` from a previous `rest` or `sold` state, never straight from `hold`, which is what actually encodes the one-day cooldown rule. This closes the "stock problems" gap entirely, since LC 121 (Best Time to Buy and Sell Stock, elsewhere in this repo) is solved as a simple greedy single-pass, not DP — this is the first DP-framed stock problem in the register. **Complexity:** O(n) time, O(1) space.

## Verification

```
$ cd practice/java/week-21/dp/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC72 minDistance(horse, ros) = 3
  PASS  LC72 minDistance(intention, execution) = 5
  PASS  LC213 robCircular([2,3,2]) = 3
  PASS  LC213 robCircular([1,2,3,1]) = 4
  PASS  LC213 robCircular([1]) single house = 1
  PASS  LC518 change(5, [1,2,5]) = 4 combinations
  PASS  LC518 change(3, [2]) = 0 (unreachable)
  PASS  LC494 findTargetSumWays([1,1,1,1,1], 3) = 5
  PASS  LC494 findTargetSumWays([1], 1) = 1
  PASS  LC64 minPathSum(3x3 grid) = 7
  PASS  LC64 minPathSum(2x3 grid) = 12
  PASS  LC516 longestPalindromeSubseq(bbbab) = 4
  PASS  LC516 longestPalindromeSubseq(cbbd) = 2
  PASS  LC309 maxProfitCooldown([1,2,3,0,2]) = 3
  PASS  LC309 maxProfitCooldown([1]) single day = 0
Week 21 — Dynamic Programming (LC 72, 213, 518, 494, 64, 516, 309): 15/15 assertions passed
```
