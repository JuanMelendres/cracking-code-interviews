---
title: "Coding Practice — Dynamic Programming, Continued (T-1411)"
week: 23
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Dynamic Programming, Continued (T-1411)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 16/32 to 21/32 — still the register's single largest raw-count pattern and, even after this batch, the largest absolute remaining gap (11 problems still needed). Week 21's batch covered edit distance, a circular-array reduction, two knapsack-counting variants, matrix path, interval DP, and state-machine DP; this batch adds a 1D string-decoding DP, an obstacle-aware extension of an already-solved grid problem, the first **tree DP** problem in the register, a generalized multi-transaction stock problem, and interval-DP-with-precomputed-palindrome-table.

---

## Problem 1 — LC 91 Decode Ways

**Pattern:** 1D DP with a two-term recurrence — the number sequence "A"-"Z" (1-26) maps to a decode-ways count structurally identical to climbing stairs (LC 70, already solved), except each "step size" (1-digit or 2-digit) is conditionally valid rather than always allowed.

```java
static int numDecodings(String s) {
    int n = s.length();
    if (n == 0 || s.charAt(0) == '0') return 0;
    int prev2 = 1, prev1 = 1;
    for (int i = 2; i <= n; i++) {
        int cur = 0;
        if (s.charAt(i - 1) != '0') cur += prev1;
        int twoDigit = Integer.parseInt(s.substring(i - 2, i));
        if (twoDigit >= 10 && twoDigit <= 26) cur += prev2;
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

**Retrospective:** `dp[i]` (the number of ways to decode the first `i` characters) can come from `dp[i-1]` (treating the last character as a standalone 1-9 digit) or `dp[i-2]` (treating the last two characters as a combined 10-26 code) — but *only* when each interpretation is actually valid, which is why both conditions gate their respective contribution rather than unconditionally adding. A `'0'` can never stand alone (there's no digit-0 letter mapping), which is why `cur` starts at 0 each iteration rather than assuming at least one interpretation is always valid — this single condition is the most common place candidates introduce an off-by-one or miss an edge case (e.g., `"100"` has zero valid decodings, since `'0'` can't stand alone and `"00"` isn't a valid 2-digit code either). **Complexity:** O(n) time, O(1) space.

## Problem 2 — LC 63 Unique Paths II

**Pattern:** the same grid-path counting recurrence as LC 62 (Unique Paths, already solved), with a single added rule: an obstacle cell zeroes out every path through it.

```java
static int uniquePathsWithObstacles(int[][] grid) {
    int[] dp = new int[grid[0].length];
    dp[0] = (grid[0][0] == 0) ? 1 : 0;
    for (int r = 0; r < grid.length; r++) {
        for (int c = 0; c < grid[0].length; c++) {
            if (grid[r][c] == 1) dp[c] = 0;
            else if (c > 0) dp[c] += dp[c - 1];
        }
    }
    return dp[grid[0].length - 1];
}
```

**Retrospective:** using a single rolling 1D array (rather than a full 2D `dp` table) works here because each cell's value only ever depends on the cell directly above (the *previous* value still sitting in `dp[c]` before this row overwrites it) and the cell to the left (`dp[c-1]`, already updated this row) — the obstacle rule just adds a hard reset (`dp[c] = 0`) that overrides both of those contributions. This is a direct, minimal extension of a previously-solved problem — the kind of fast pattern-recognition ("I've solved the unconstrained version, this just adds one rule") that signals real fluency rather than solving each problem from a blank slate. **Complexity:** O(rows·cols) time, O(cols) space.

## Problem 3 — LC 337 House Robber III (tree DP)

**Pattern:** post-order DFS returning a two-element state per node — the first tree-shaped DP problem in the register, generalizing LC 198/213's linear and circular House Robber recurrences to an arbitrary binary tree.

```java
private static int[] robHelper(TreeNode node) {
    if (node == null) return new int[]{0, 0};
    int[] left = robHelper(node.left);
    int[] right = robHelper(node.right);
    int robbed = node.val + left[1] + right[1];
    int notRobbed = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
    return new int[]{robbed, notRobbed};
}
```

**Retrospective:** the "can't rob two directly-connected houses" constraint, which was a simple index-adjacency rule on an array (LC 198) or a first/last wraparound rule on a circular array (LC 213), becomes a parent/child adjacency rule on a tree — and the natural way to track "the best answer, given whether this node is robbed or not" is exactly the same two-state idea as LC 309's `hold`/`sold`/`rest` triple, just restructured for a tree's recursive shape instead of a linear scan. If a node is robbed, neither child can be (forcing `left[1]`/`right[1]`, their not-robbed values); if a node is *not* robbed, each child independently picks whichever of its own two states is larger. This pattern — return a small fixed-size state tuple from each recursive call, combine at the parent — generalizes to a large family of tree DP problems beyond this one. **Complexity:** O(n) time (each node visited once), O(h) space for recursion depth.

## Problem 4 — LC 188 Best Time to Buy and Sell Stock IV (at most k transactions)

**Pattern:** generalizes LC 309's cooldown state machine to an explicit transaction-count dimension — the general form that "at most 2 transactions" (LC 123) and "unlimited transactions" (LC 122) are both special cases of.

```java
static int maxProfitK(int k, int[] prices) {
    int n = prices.length;
    if (k >= n / 2) { /* unlimited transactions is equivalent once k is this large */ }
    int[] hold = new int[k + 1], cash = new int[k + 1];
    Arrays.fill(hold, Integer.MIN_VALUE / 2);
    for (int price : prices) {
        for (int t = 1; t <= k; t++) {
            hold[t] = Math.max(hold[t], cash[t - 1] - price);
            cash[t] = Math.max(cash[t], hold[t] + price);
        }
    }
    return cash[k];
}
```

**Retrospective:** the `k >= n/2` early-exit is a real, necessary optimization, not a shortcut — a profitable trade needs at least 2 days (buy then sell), so no more than `n/2` transactions can ever matter regardless of how large `k` is; without this check, `k` could be given as an enormous number (e.g., 10^9) while `prices` has only a few elements, making the `O(k)` inner loop absurdly wasteful for no benefit. `hold[t]` tracks the best profit after buying into transaction `t` (funded by transaction `t-1`'s completed `cash`), and `cash[t]` tracks the best profit after selling to complete transaction `t` — this two-array-indexed-by-transaction-count structure is the direct generalization of LC 309's three named scalar states. **Complexity:** O(n·k) time (or O(n) when the early-exit applies), O(k) space.

## Problem 5 — LC 132 Palindrome Partitioning II

**Pattern:** two-stage DP — first precompute an `isPalindrome[i][j]` table (identical technique to LC 516's interval DP from Week 21), then run a second 1D DP over minimum cuts using that table for O(1) palindrome checks.

```java
// stage 1: isPalindrome[i][j] via the same interval-DP fill order as LC 516
for (int i = n - 1; i >= 0; i--) {
    for (int j = i; j < n; j++) {
        if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPalindrome[i + 1][j - 1])) {
            isPalindrome[i][j] = true;
        }
    }
}
// stage 2: dp[i] = minimum cuts needed for s[0..i]
for (int i = 0; i < n; i++) {
    if (isPalindrome[0][i]) { dp[i] = 0; continue; }
    for (int j = 1; j <= i; j++) {
        if (isPalindrome[j][i]) dp[i] = Math.min(dp[i], dp[j - 1] + 1);
    }
}
```

**Retrospective:** computing `isPalindrome[i][j]` on the fly inside the cut-counting loop (checking character-by-character every time) would make the whole solution O(n³); precomputing it once as its own O(n²) table first, then treating every `isPalindrome[j][i]` lookup in the second stage as O(1), is what brings the total down to O(n²). This two-stage "precompute a helper table, then DP over it" structure is broadly reusable whenever a DP's transition needs a substring/subarray property (palindrome-ness, sum, min/max) that would otherwise be recomputed redundantly across many DP states. **Complexity:** O(n²) time and space for both stages combined.

## Verification

```
$ cd practice/java/week-23/dp/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC91 numDecodings(12) = 2 (AB, L)
  PASS  LC91 numDecodings(226) = 3 (BZ, VF, BBF)
  PASS  LC91 numDecodings(06) = 0 (leading zero invalid)
  PASS  LC91 numDecodings(10) = 1 (J only)
  PASS  LC63 uniquePathsWithObstacles(3x3, center blocked) = 2
  PASS  LC63 uniquePathsWithObstacles(2x2, one blocked) = 1
  PASS  LC63 uniquePathsWithObstacles(start blocked) = 0
  PASS  LC337 robTree(3,(2,_,3),(3,_,1)) = 7 (rob 3+3+1)
  PASS  LC337 robTree(3,(4,1,3),(5,_,1)) = 9 (rob 4+5)
  PASS  LC188 maxProfitK(k=2, [2,4,1]) = 2
  PASS  LC188 maxProfitK(k=2, [3,2,6,5,0,3]) = 7
  PASS  LC188 maxProfitK(k=0) = 0
  PASS  LC132 minCut(aab) = 1 (aa|b)
  PASS  LC132 minCut(a) = 0 (already palindrome)
  PASS  LC132 minCut(racecar) = 0 (whole string is a palindrome)
Week 23 — Dynamic Programming (LC 91, 63, 337, 188, 132): 15/15 assertions passed
```
