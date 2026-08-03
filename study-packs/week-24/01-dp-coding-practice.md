---
title: "Coding Practice — Dynamic Programming, Final Batch (T-1411)"
week: 24
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Dynamic Programming, Final Batch (T-1411)

**6 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 21/32 to 27/32 (84%). Weeks 21 and 23 covered edit distance, knapsack variants, matrix path, interval DP, state-machine DP, tree DP, and Catalan-adjacent counting. This batch — the hardest single batch in the entire coding-volume series — adds DP-on-a-grid-with-memoized-DFS, the classic hard interval DP (Burst Balloons), two closely-related 2D string-matching DP problems (regex vs. wildcard), true Catalan-number counting, and the DP formulation of a problem this repo has previously touched only via a stack.

---

## Problem 1 — LC 329 Longest Increasing Path in a Matrix

**Pattern:** DFS with memoization on a grid — structurally different from every prior grid-DP problem in this register (LC 62/63/64, which fill a DP table by fixed row/column order) because the "increasing" constraint means there's no single valid fill order; memoized DFS sidesteps that entirely.

```java
private static int dfs329(int[][] matrix, int r, int c, int[][] memo) {
    if (memo[r][c] != 0) return memo[r][c];
    int best = 1;
    for (int[] d : DIRS_4) {
        int nr = r + d[0], nc = c + d[1];
        if (inBounds(nr, nc) && matrix[nr][nc] > matrix[r][c]) {
            best = Math.max(best, 1 + dfs329(matrix, nr, nc, memo));
        }
    }
    memo[r][c] = best;
    return best;
}
```

**Retrospective:** unlike LC 62/63/64 (Unique Paths, Unique Paths II, Min Path Sum), where every cell's dependencies are always "up" and "left" — a fixed, exploitable fill order — this problem's dependencies point toward whichever neighbor has a *strictly greater* value, which could be in any of the four directions depending on the matrix's actual contents. Memoized DFS handles this correctly because the recursion naturally follows the "increasing" direction (a cell only recurses into neighbors with larger values, and since values strictly increase along any path, there's no possibility of infinite recursion or a cycle) — this is the same DFS-plus-memo shape as Week 20's graph DFS problems, but applied to enforce a numeric ordering constraint instead of a visited-set constraint. **Complexity:** O(rows·cols) time — each cell's `dfs329` computation runs exactly once thanks to memoization, despite the four-directional branching.

## Problem 2 — LC 312 Burst Balloons (interval DP)

**Pattern:** interval DP with an inverted way of thinking about "last event" — the key insight is treating the balloon burst **last** within a range as the DP's decision variable, not the one burst first.

```java
static int maxCoins(int[] nums) {
    int[] balloons = padded(nums); // sentinel 1s at both ends
    for (int len = 2; len <= n + 1; len++) {
        for (int left = 0; left + len <= n + 1; left++) {
            int right = left + len;
            for (int k = left + 1; k < right; k++) {
                int coins = balloons[left] * balloons[k] * balloons[right] + dp[left][k] + dp[k][right];
                dp[left][right] = Math.max(dp[left][right], coins);
            }
        }
    }
    return dp[0][n + 1];
}
```

**Retrospective:** thinking about which balloon to burst *first* is a trap — bursting any balloon first changes which balloons become adjacent to which, making the subproblems interdependent in a way that resists clean decomposition. Thinking instead about which balloon is burst **last** within a given range `(left, right)` fixes the boundary balloons (`left` and `right` are guaranteed to still be present, since everything strictly between them has already been burst) — so `balloons[left] * balloons[k] * balloons[right]` is exactly the coins earned for that final burst, and the two sub-ranges `(left, k)` and `(k, right)` become fully independent problems that don't interact. This "think about what happens last, not first" reframing is a distinctive interval-DP technique, different from LC 132's Week 23 palindrome-partitioning DP (which processes prefixes left-to-right) — worth explicitly contrasting the two in an interview. The sentinel `1` balloons padded at both ends let the boundary multiplication work uniformly without special-casing the array's actual edges. **Complexity:** O(n³) time, O(n²) space.

## Problem 3 — LC 10 Regular Expression Matching

**Pattern:** 2D string-matching DP where `'*'` means "zero or more of the *preceding* character" — paired deliberately with Problem 4 (LC 44) to contrast two similar-looking but semantically different `'*'` rules.

```java
if (pc == '*') {
    dp[i][j] = dp[i][j - 2]; // treat the preceding element as occurring zero times
    char preceding = p.charAt(j - 2);
    if (preceding == '.' || preceding == sc) {
        dp[i][j] = dp[i][j] || dp[i - 1][j]; // one or more occurrences
    }
}
```

**Retrospective:** because `*` here always applies to the *single character immediately before it* in the pattern (not to itself), matching it requires looking two positions back in the pattern (`j - 2`) rather than one — and the recurrence has to consider both "the starred character matches zero times" (`dp[i][j-2]`) and, separately, "it matches one more time and the string still has characters left to consume" (`dp[i-1][j]`), OR-ing the two possibilities together. This is meaningfully different from Problem 4's wildcard `*`, which matches *any sequence of characters* on its own, not a repetition of a specific preceding character — conflating the two rules is the single most common source of bugs when solving these problems back-to-back, which is exactly why both are included together in this batch. **Complexity:** O(m·n) time and space.

## Problem 4 — LC 44 Wildcard Matching

**Pattern:** 2D string-matching DP where `'*'` means "any sequence of characters (including empty)" — structurally simpler than LC 10's DP despite looking similar on the surface.

```java
if (pc == '*') {
    dp[i][j] = dp[i - 1][j] || dp[i][j - 1]; // '*' consumes current char, or matches empty
} else if (pc == '?' || pc == s.charAt(i - 1)) {
    dp[i][j] = dp[i - 1][j - 1];
}
```

**Retrospective:** because this `*` stands on its own (unlike LC 10's, which modifies a preceding character), its recurrence only ever needs to look one row or one column back, not two — `dp[i-1][j]` says "let `*` absorb the current string character and keep matching the rest of the string against the same `*`" (so `*` can greedily consume as many characters as needed), while `dp[i][j-1]` says "`*` matches zero characters here, move on to the next pattern character." The `?` case is a simple 1-for-1 wildcard, identical in spirit to LC 10's `.`. Solving both LC 10 and LC 44 back-to-back and being able to state precisely *why* their recurrences differ (despite both looking like "2D DP with a wildcard `*`") is a strong Staff-level signal — it shows the candidate understands the semantics driving the recurrence, not just a memorized DP shape. **Complexity:** O(m·n) time and space.

## Problem 5 — LC 96 Unique Binary Search Trees (Catalan number counting)

**Pattern:** counting DP where the recurrence sums over "which value is the root" — a structurally new category in this register: counting distinct tree *shapes*, not computing an optimal value over a fixed tree.

```java
static int numTrees(int n) {
    int[] dp = new int[n + 1];
    dp[0] = 1;
    for (int nodes = 2; nodes <= n; nodes++) {
        for (int root = 1; root <= nodes; root++) {
            dp[nodes] += dp[root - 1] * dp[nodes - root];
        }
    }
    return dp[n];
}
```

**Retrospective:** for `nodes` values arranged in a BST, choosing which value is the root immediately fixes the left subtree to be built from the `root - 1` smaller values and the right subtree from the `nodes - root` larger values — and because BST structure only depends on the *count* of values in each subtree (not their actual values, since any contiguous range of `k` distinct values has exactly the same number of possible BST shapes as any other range of `k` values), `dp[nodes]` can be computed purely from smaller `dp` values, summed across every possible root choice. This is the direct computational form of the Catalan numbers, and recognizing "the number of subtrees possible only depends on subtree *size*, not the actual values involved" is the specific insight that unlocks the DP — a comparable insight to LC 337's tree-DP problem from Week 23, which similarly decomposes independently by subtree. **Complexity:** O(n²) time, O(n) space.

## Problem 6 — LC 32 Longest Valid Parentheses (DP formulation)

**Pattern:** `dp[i]` = length of the longest valid parenthesis substring *ending exactly at* index `i` — solved here via DP specifically to contrast with the stack-based technique this register already uses for related problems (Week 21's monotonic-stack file).

```java
if (s.charAt(i) == ')') {
    if (s.charAt(i - 1) == '(') {
        dp[i] = (i >= 2 ? dp[i - 2] : 0) + 2;
    } else {
        int matchStart = i - dp[i - 1] - 1;
        if (matchStart >= 0 && s.charAt(matchStart) == '(') {
            dp[i] = dp[i - 1] + 2 + (matchStart >= 1 ? dp[matchStart - 1] : 0);
        }
    }
}
```

**Retrospective:** the two cases correspond to the two ways a `')'` at index `i` can complete a valid pair: either it directly closes the immediately preceding `'('` (the simple `"()"` case, extending whatever valid run ended just before that pair), or it closes a `'('` that sits *just before* the valid run that already ends at `i-1` (the `"(())"`-nesting case) — `matchStart = i - dp[i-1] - 1` computes exactly where that opening `'('` would need to be, and if it's really there, the two valid chunks (this new pair, plus anything valid immediately before it) are stitched together by adding `dp[matchStart - 1]`. A stack-based solution (tracking indices and computing lengths from stack gaps) solves the same problem with arguably less index arithmetic — being able to produce *both* solutions and explain the trade-off (DP: more index bookkeeping but a single forward pass with O(1) lookups; stack: more intuitive to derive live, but requires care around the sentinel `-1` base index) is itself a useful thing to have ready, since this problem is a common one for interviewers to ask "now do it the other way" as a follow-up. **Complexity:** O(n) time, O(n) space.

## Verification

```
$ cd practice/java/week-24/dp/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
  PASS  LC329 longestIncreasingPath(3x3 example) = 4 (path 1-2-6-9)
  PASS  LC329 longestIncreasingPath(3x3 example 2) = 4 (path 3-4-5-6)
  PASS  LC329 longestIncreasingPath(single cell) = 1
  PASS  LC312 maxCoins([3,1,5,8]) = 167
  PASS  LC312 maxCoins([1,5]) = 10
  PASS  LC10 isMatch(aa, a) -> false
  PASS  LC10 isMatch(aa, a*) -> true (zero-or-more)
  PASS  LC10 isMatch(ab, .*) -> true
  PASS  LC10 isMatch(aab, c*a*b) -> true
  PASS  LC10 isMatch(mississippi, mis*is*p*.) -> false
  PASS  LC44 isMatchWildcard(aa, a) -> false
  PASS  LC44 isMatchWildcard(aa, *) -> true
  PASS  LC44 isMatchWildcard(cb, ?a) -> false
  PASS  LC44 isMatchWildcard(adceb, *a*b) -> true
  PASS  LC44 isMatchWildcard(acdcb, a*c?b) -> false
  PASS  LC96 numTrees(3) = 5
  PASS  LC96 numTrees(1) = 1
  PASS  LC96 numTrees(4) = 14
  PASS  LC32 longestValidParentheses("(()") = 2
  PASS  LC32 longestValidParentheses(")()())") = 4
  PASS  LC32 longestValidParentheses(empty) = 0
Week 24 — Dynamic Programming (LC 329, 312, 10, 44, 96, 32): 21/21 assertions passed
```
