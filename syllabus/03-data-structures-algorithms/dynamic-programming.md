---
title: "Dynamic Programming: 1D, 2D, Knapsack, and Intervals"
slug: dynamic-programming
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2110
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
  - trees-bst-and-traversal-patterns.md
related:
  - trees-bst-and-traversal-patterns.md
  - hashing-patterns-and-frequency-maps.md
practice: ../../practice/java/week-21/dp/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-21/04-dynamic-programming-coding-practice.md
  - study-packs/week-23/01-dp-coding-practice.md
---

# Dynamic Programming: 1D, 2D, Knapsack, and Intervals

> **Provenance.** The twelve worked problems and retrospectives in Sections 7 and 15 are elevated from two study-packs — `study-packs/week-21/04-dynamic-programming-coding-practice.md` (7 problems) and `study-packs/week-23/01-dp-coding-practice.md` (5 problems) — real, compiled, executed code (`practice/java/week-21/dp/`, `practice/java/week-23/dp/`), re-verified on OpenJDK 21.0.12 while writing this chapter (15/15 and 15/15 assertions passing, 30 total).

This is Master Topic Register **T-1411** (IWI 5.85, ⭐, very-high frequency, and the register's single largest raw-count pattern by problem count). Dynamic programming is recursion with memory: identical to plain recursion in *what* it computes, but avoiding the exponential blowup of recomputing the same subproblem repeatedly by storing (and reusing) each subproblem's answer exactly once.

## 1. Why This Matters

DP problems have a reputation for being the hardest, most anxiety-inducing category in coding interviews — not because the final code is long (most DP solutions are short), but because *finding the recurrence* feels unguided compared to, say, recognizing "this is a two-pointer problem." The actual, learnable skill is a repeatable process: define what a DP state means in plain English, write the recurrence connecting a state to smaller states, identify the base cases, and only then translate that into code — in that order, every time, rather than trying to write DP code directly from the problem statement.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — DP's entire value proposition is turning an exponential naive-recursion complexity into a polynomial one by eliminating redundant recomputation, the same overlapping-subproblems idea amortized analysis addresses from a different angle. [Trees, BSTs, and Traversal Patterns](trees-bst-and-traversal-patterns.md) — tree DP (Section 4) is a direct, structural extension of the return-value/side-channel recursion discipline that chapter establishes.

## 3. Foundation (L1)

**Dynamic programming applies to problems with two properties: overlapping subproblems (the same smaller question gets asked repeatedly during a naive recursive solution) and optimal substructure (the best answer to the whole problem can be built from the best answers to its subproblems).** When both hold, storing each subproblem's answer the first time it's computed — instead of recomputing it every time it's asked again — turns an exponential naive recursion into a polynomial one.

**Every DP solution has the same three ingredients, findable in this order**: (1) a **state** — what does `dp[i]` (or `dp[i][j]`) actually represent, in one plain-English sentence; (2) a **recurrence** — how does a state's answer relate to smaller states' answers; (3) **base cases** — the smallest states, answered directly without recursion. Skipping straight to code without nailing down the state definition first is the single most common reason DP problems feel intractable under interview pressure.

## 4. Core Concepts (L2)

**Two-string grid DP** (Edit Distance, Section 7 Problem 1) is the general pattern behind an enormous family of two-sequence comparison problems — Longest Common Subsequence and Longest Palindromic Substring are both restricted special cases of the same `dp[i][j]`-over-two-indices shape, differing only in what operations the recurrence allows.

**Reduction to an already-solved subproblem** (House Robber II's circular variant, Section 7 Problem 2) is often more valuable than deriving a new recurrence — recognizing that a circular constraint decomposes into two runs of an already-known linear recurrence is a genuinely transferable insight, not specific to this one problem.

**Counting DP vs. optimization DP** are structurally similar but ask different questions: Coin Change (minimum coins) and Coin Change II (Section 7 Problem 3, count combinations) share the same 0/1-vs-unbounded knapsack shape, but one recurrence takes a `min`, the other a `+=` sum — a small code difference reflecting a genuinely different question.

**Knapsack loop-order determines what's actually being counted or allowed**: iterating the "item" dimension in the outer loop versus the inner loop (Section 5) is the specific, easy-to-get-backwards detail that determines whether combinations or permutations are counted, and whether each item can be reused (unbounded) or used at most once (0/1).

**Matrix-path DP** (Minimum Path Sum, Section 7 Problem 5) generalizes to any grid problem where each cell's answer depends only on cells above and to the left — recognizing a new problem as "the same grid shape, different combine operator" (sum-of-costs via `min`, versus count-of-paths via `+`) is a fast, real pattern-transfer skill.

**Interval DP** (Longest Palindromic Subsequence, Section 7 Problem 6) operates over `(i, j)` ranges rather than single indices, and requires a specific fill order — shorter ranges before longer ones — since a range's answer depends on strictly shorter sub-ranges.

**State-machine DP** (stock trading with cooldown, Section 7 Problem 7) tracks a small, named set of mutually exclusive states (holding a share, just sold, resting) as rolling variables rather than an array, turning a constraint that would otherwise need special-case `if` logic (the cooldown) into a structural property of which states can transition into which.

**Tree DP** (House Robber III, Section 7 Problem 3 of the second study-pack) generalizes linear and circular DP recurrences to a tree, returning a small fixed-size state tuple from each recursive call and combining it at the parent — a direct structural relative of [Trees'](trees-bst-and-traversal-patterns.md#4-core-concepts-l2) return-value-carries-what-the-parent-needs discipline.

**Precomputed-helper-table DP** (Palindrome Partitioning II, Section 7 Problem 5 of the second study-pack) separates a DP into two stages: first build an O(n²) helper table answering a repeatedly-needed sub-question (is this substring a palindrome) once, then run the main DP treating every lookup into that table as O(1) — avoiding redundant recomputation of the same helper fact across many different DP states.

## 5. How It Works Internally (L3)

**The coin-outer-vs-amount-outer loop order distinction, precisely** (Coin Change II, Section 7 Problem 3): iterating coins in the *outer* loop and amounts in the *inner* loop means every combination is built by considering coins in one fixed, canonical order (whatever order they appear in the coins array) — so `{1, 2}` summing to 3 is only ever counted once, as "one 1, then one 2," never also as "one 2, then one 1." Swapping the loop order (amount outer, coin inner) would instead count every *ordering* of coins reaching a given amount as distinct, turning a combination count into a permutation count — the exact, easy-to-miss detail behind one of DP's most common subtle bugs.

**The 0/1-vs-unbounded knapsack direction distinction, precisely** (Target Sum, Section 7 Problem 4, contrasted with Coin Change II): iterating the capacity dimension *downward* (from `subsetSum` to `num`) when updating `dp[s] += dp[s - num]` ensures each `dp[s - num]` read reflects the state *before* the current item was considered, guaranteeing the current item contributes at most once (0/1 knapsack). Iterating *upward* (as Coin Change II does) means a `dp[a - coin]` read can reflect a state that *already* includes the current coin, allowing it to be reused an unlimited number of times (unbounded knapsack). This single loop-direction flip is the entire difference between the two knapsack variants — worth deriving from first principles rather than memorizing which direction goes with which variant name.

**Interval DP's fill-order requirement, precisely** (Longest Palindromic Subsequence): computing `dp[i][j]` requires `dp[i+1][j-1]`, `dp[i+1][j]`, and `dp[i][j-1]` to already be known — all three cover strictly shorter ranges than `[i, j]`. Iterating `i` downward from `n-1` and `j` upward from `i+1` guarantees every dependency is filled before it's read, since any range `[i,j]` is only ever computed after every range strictly contained within `[i, j]`'s span in the relevant direction. Getting this fill order backwards produces a program that reads uninitialized (zero-valued) array cells instead of throwing an out-of-bounds exception — a silently wrong answer, not a crash.

**The state-machine's cooldown-encoding mechanism, precisely** (Section 7 Problem 7): `rest`'s transition formula, `Math.max(prevRest, prevSold)`, deliberately excludes `prevHold` as a source — meaning the algorithm can only enter the `rest` state from a previous `rest` or `sold` state, never directly from `hold`. This asymmetry is what structurally encodes "you can't buy the day immediately after selling" as a property of which state transitions are even representable, rather than as an `if` statement checking a day-counter against the last sale date.

**Tree DP's parent-child dependency, precisely**: at every node, whether that node itself is robbed forces a specific constraint on its children (`node.val + left[1] + right[1]` if robbed — both children *must* be in their not-robbed state) versus leaves each child free to independently choose its own better state if the current node isn't robbed (`Math.max(left[0], left[1]) + Math.max(right[0], right[1])`). This mirrors [Diameter of Binary Tree's](trees-bst-and-traversal-patterns.md#5-how-it-works-internally-l3) side-channel-accumulation technique structurally, but here the "side information" (whether robbed) is carried explicitly in the return tuple rather than through a separate mutable channel, since both possible values are genuinely needed by the parent, not just tracked as a running maximum.

## 6. Practical Usage

- **Write the state definition as a plain-English sentence before writing any code** — "`dp[i][j]` = the edit distance between the first `i` characters of `word1` and the first `j` characters of `word2`" — and only then derive the recurrence from that sentence.
- **Check whether a "new" problem is a reduction to (or a direct generalization of) an already-solved one** before deriving a fresh recurrence from scratch — House Robber II→I and Best Time to Buy/Sell IV→Cooldown (Section 7) are both real, worked examples of exactly this transfer.
- **When a knapsack-shaped problem's behavior seems backwards (over-counting, under-counting, or wrong reuse semantics), check loop order and loop direction first** (Section 5) — these two details, not the recurrence's mathematical logic, are the most common source of subtle DP bugs.

## 7. Examples

**Problem 1 — LC 72, Edit Distance.**

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

**Retrospective:** the three min-branches correspond exactly to substitution, deletion, and insertion. **Complexity:** O(m·n) time and space.

**Problem 2 — LC 213, House Robber II (circular).**

```java
static int robCircular(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    return Math.max(robLinear(nums, 0, n - 2), robLinear(nums, 1, n - 1));
}
```

**Retrospective:** houses 0 and n-1 can't both be robbed in a circle, so the answer is the better of two linear-range runs. **Complexity:** O(n) time, O(1) space.

**Problem 3 — LC 518, Coin Change II (unbounded knapsack, counting).**

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

**Retrospective:** see Section 5's coin-outer argument. **Complexity:** O(amount · coins.length).

**Problem 4 — LC 494, Target Sum (0/1 knapsack via transform).**

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

**Retrospective:** see Section 5's downward-iteration argument. **Complexity:** O(n · subsetSum).

**Problem 5 — LC 64, Minimum Path Sum.**

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

**Retrospective:** the direct dollar-cost cousin of Unique Paths — same grid shape, `min` instead of addition. **Complexity:** O(rows·cols).

**Problem 6 — LC 516, Longest Palindromic Subsequence.**

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

**Retrospective:** see Section 5's fill-order argument. **Complexity:** O(n²) time and space.

**Problem 7 — LC 309, Best Time to Buy and Sell Stock with Cooldown.**

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

**Retrospective:** see Section 5's cooldown-encoding argument. **Complexity:** O(n) time, O(1) space.

**Problem 8 — LC 91, Decode Ways.**

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

**Retrospective:** structurally identical to Climbing Stairs, except each "step size" is conditionally valid; a `'0'` can never stand alone. **Complexity:** O(n) time, O(1) space.

**Problem 9 — LC 63, Unique Paths II (with obstacles).**

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

**Retrospective:** a minimal, one-rule extension of Unique Paths, using a rolling 1D array. **Complexity:** O(rows·cols) time, O(cols) space.

**Problem 10 — LC 337, House Robber III (tree DP).**

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

**Retrospective:** see Section 5's parent-child-dependency argument. **Complexity:** O(n) time, O(h) space.

**Problem 11 — LC 188, Best Time to Buy and Sell Stock IV.**

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

**Retrospective:** generalizes the cooldown state machine to an explicit transaction-count dimension; the `k >= n/2` early-exit is a necessary optimization, not a shortcut. **Complexity:** O(n·k), or O(n) when the early-exit applies.

**Problem 12 — LC 132, Palindrome Partitioning II.**

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

**Retrospective:** precomputing the palindrome table once, rather than checking on the fly, brings the total from O(n³) to O(n²). **Complexity:** O(n²) time and space.

## 8. Common Mistakes

- **Writing DP code before defining the state in plain English** — the single most common cause of "I don't know where to start" on a DP problem.
- **Getting the knapsack loop direction backwards** (Section 5) — silently changes 0/1 semantics into unbounded semantics or vice versa, without any error.
- **Getting the interval-DP fill order backwards** (Section 5) — reads uninitialized array cells, producing a silently wrong answer rather than a crash.
- **Re-deriving a recurrence from scratch for a problem that's actually a reduction or generalization of an already-solved one** (House Robber II, Best Time to Buy/Sell IV) — wastes interview time and misses a strong pattern-fluency signal.
- **Recomputing a repeatedly-needed helper fact (like palindrome-ness) inline inside a DP's main loop** instead of precomputing it once — silently degrades the overall complexity class (Section 4/7, Problem 12).

## 9. Edge Cases

- **Empty or single-element input** — House Robber II's own verified single-house case (`[1]`, correctly returning `1` without attempting a circular split) and Cooldown's single-day case (`[1]`, correctly returning `0` with no possible trade).
- **A leading zero in Decode Ways** (verified `"06"` case, correctly returning `0`) — a `'0'` can never stand alone as a valid single-digit code.
- **`k = 0` transactions allowed** (Best Time to Buy/Sell IV's verified case, correctly returning `0`) — the algorithm must not attempt any trade at all in this case.
- **A string that's already a full palindrome** (Palindrome Partitioning II's verified `"racecar"` case, correctly returning `0` cuts needed).

## 10. Performance Implications

Real, executed verification from `practice/java/week-21/dp/` and `practice/java/week-23/dp/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
Week 21 — Dynamic Programming (LC 72, 213, 518, 494, 64, 516, 309): 15/15 assertions passed
Week 23 — Dynamic Programming (LC 91, 63, 337, 188, 132): 15/15 assertions passed
```

The performance lesson specific to DP is that the *state space size* directly determines complexity: a `dp[i]` (1D) problem is typically O(n); a `dp[i][j]` (2D grid or two-string) problem is typically O(n·m) or O(n²); interval DP over `(i,j)` ranges is O(n²) states with O(1)–O(n) work per state. Recognizing which shape a new problem's state space has — before writing any code — is a fast, reliable way to predict and sanity-check a solution's complexity ahead of implementing it.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Top-down (memoized recursion) | Mirrors the natural recursive problem statement; only computes states actually needed | Recursion overhead per call; risk of stack overflow on deep recursion |
| Bottom-up (tabulation) | No recursion overhead; often allows rolling-array space optimization | Requires figuring out a valid fill order upfront; computes every state even if not all are needed |
| Full 2D table | Simple, direct | O(n·m) or O(n²) space |
| Rolling 1D array (space-optimized) | O(n) or O(1) space | Only possible when each state depends on a bounded, nearby set of previous states; less readable |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is the state-definition-first discipline (Section 6) applied consistently under time pressure — being able to say, out loud, "let `dp[i]` represent X" before writing a single line of code, and deriving the recurrence as a direct logical consequence of that sentence. This is also what makes a candidate able to recover from a wrong first attempt: if the recurrence doesn't work, the bug is almost always traceable to an imprecise or incorrect state definition, not a coding mistake — and being able to diagnose it at that level, rather than randomly tweaking code, is a strong differentiator.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, DP's core insight — cache and reuse the answer to a subproblem instead of recomputing it — is the same principle behind memoization layers, materialized views, and computed-column caching in real production systems. The precomputed-helper-table technique (Section 4/7, Problem 12) directly mirrors a common real system-design pattern: precomputing and caching an expensive-but-frequently-needed derived fact (a palindrome check, or in production, something like a frequently-queried aggregate) once, rather than recomputing it inline on every access path that needs it — the same complexity-class argument (O(n³) collapsing to O(n²)) applies identically to a real system where an expensive derived value is queried from many different code paths.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a dynamic-programming-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry covering a real caching/memoization design (e.g., an expensive derived computation recomputed redundantly across multiple call sites before a shared cache was introduced) would be a natural, non-duplicative addition connecting this chapter's Section 13 transfer to a genuine production incident.

## 15. Interview Questions

### Question 1 — Walk me through your process for solving a dynamic programming problem you haven't seen before.

**Why interviewers ask it.** It's less about a specific algorithm and more about whether a candidate has a repeatable, structured process (Section 3/6) versus hoping for a flash of insight.

**Expected answer.** Define the state in plain English first — what does `dp[i]` (or `dp[i][j]`) actually represent. Derive the recurrence by asking "how does this state's answer relate to smaller states." Identify base cases. Only then translate into code, bottom-up or top-down. Check the recurrence against a small example by hand before trusting it.

**Minimum acceptable answer.** Describes some version of "figure out the smaller subproblem" without a fully structured process.

**Strong Senior answer.** Explicitly separates the state-definition step from the recurrence-derivation step, and can demonstrate it live on an unfamiliar variant, narrating the state definition out loud before writing code.

**Staff-level extension.** Connects this to a broader principle: the same "identify the smaller repeated subproblem, cache it" thinking transfers directly to real system design decisions about caching and memoization (Section 13).

**Common mistakes.** Jumping straight to code, then discovering partway through that the recurrence doesn't quite work — a symptom of skipping the explicit state-definition step.

**Follow-up questions.** "How would you verify your recurrence is correct before writing the full solution?" (Trace through a small example by hand, comparing the recurrence's predicted values against manually-computed ones.)

### Question 2 — What's the difference between 0/1 knapsack and unbounded knapsack, and how does the code actually differ?

**Why interviewers ask it.** It's a precise, checkable test of whether the loop-order/loop-direction mechanism (Section 5) is genuinely understood, rather than the two variants being treated as unrelated memorized templates.

**Expected answer.** 0/1 knapsack allows each item to be used at most once; unbounded knapsack allows unlimited reuse. In a 1D rolling-array implementation, unbounded knapsack iterates the capacity dimension *upward* (allowing a just-updated value, which may already include the current item, to be read again), while 0/1 knapsack iterates *downward* (ensuring each capacity's update only ever reads values from before the current item was considered).

**Minimum acceptable answer.** Correctly implements both, even without articulating the precise loop-direction mechanism unprompted.

**Strong Senior answer.** States the loop-direction mechanism precisely and unprompted, and can point to a concrete pair of problems (Coin Change II vs. Target Sum, Section 7) as worked examples.

**Staff-level extension.** Generalizes to a broader principle about DP correctness: any time an in-place, rolling-array DP update reads from the same array it's writing to, the read/write order (and direction) determines what "already updated" state is visible to that read — a subtle but real correctness consideration whenever the state array is smaller than the theoretical full table.

**Common mistakes.** Memorizing "unbounded goes forward, 0/1 goes backward" as an arbitrary rule rather than deriving it from what each loop direction actually causes to be visible during the update.

**Follow-up questions.** "How would this change if items had different weights, not just different reuse rules?" (The recurrence's index arithmetic changes to subtract each item's specific weight, but the loop-direction principle for 0/1 vs. unbounded remains identical.)

## 16. Coding/Practice Exercises

- Run the existing practice code ([`week-21/dp/`](../../practice/java/week-21/dp/), [`week-23/dp/`](../../practice/java/week-23/dp/)) yourself and confirm the same 15/15 and 15/15 assertions pass.
- This pattern has additional real, already-solved problems: LC 70 (Climbing Stairs), LC 198 (House Robber), LC 322 (Coin Change), LC 300 (Longest Increasing Subsequence), LC 62 (Unique Paths), LC 1143 (Longest Common Subsequence), LC 416 (Partition Equal Subset Sum), LC 5 (Longest Palindromic Substring), and LC 139 (Word Break) across earlier weeks' practice code — study Longest Common Subsequence directly alongside this chapter's Edit Distance (Section 7, Problem 1) as the two-string grid-DP template's simplest and most general instances, respectively.
- Attempt LC 123 (Best Time to Buy and Sell Stock III, at most 2 transactions) and LC 122 (unlimited transactions) as special cases of this chapter's Problem 11 (at most k transactions) — verify your understanding of the general recurrence by deriving both special cases from it.

## 17. Debugging Exercises

**Symptom:** an unbounded-knapsack-style "count ways to make change" implementation, when converted to a 0/1-style "each coin used at most once" variant by a developer unfamiliar with the distinction, produces counts that are too high rather than too low or a crash.

**Diagnose:** check the capacity loop's direction — Section 5 names this exact bug: leaving the capacity loop iterating upward (the unbounded-correct direction) while intending 0/1 semantics allows `dp[a - coin]` to read a value that may already include the current coin's contribution, silently permitting the same coin instance to be counted more than once. The fix is reversing the capacity loop to iterate downward, exactly as Target Sum (Section 7, Problem 4) does. Confirm by constructing a minimal test case (one coin, an amount that's a small multiple of it) where the unbounded and 0/1 answers provably differ, and checking which one the implementation actually produces.

## 18. Design Exercises

**Design constraint:** design a caching layer for a service whose most expensive operation is computing a derived value that many different, independent code paths each need — currently, each code path recomputes it from scratch on every call.

Design this using this chapter's precomputed-helper-table principle (Section 4/13) directly: introduce a single shared cache (analogous to Palindrome Partitioning II's `isPalindrome` table) computed once and reused by every calling code path, rather than recomputed per-path. State explicitly the complexity-class argument for why this matters — if the derived value costs O(f) to compute and is needed by O(k) independent call paths, redundant per-path computation costs O(k·f) total, while a shared, precomputed cache costs O(f) once plus O(1) per lookup — directly mirroring the O(n³)-to-O(n²) improvement Section 7's Problem 12 achieves by the identical technique.

## 19. Further Reading

- [Trees, BSTs, and Traversal Patterns](trees-bst-and-traversal-patterns.md) — the return-value/side-channel recursion discipline tree DP (Section 4/5) directly extends.
- [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) — memoization (top-down DP) is frequently implemented with a hash map keyed by the recursive call's parameters.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, what overlapping subproblems and optimal substructure mean, and why they license memoization | [Section 3](#3-foundation-l1) |
| L2 | Follow the state-definition-then-recurrence-then-base-cases process on a new, unfamiliar 1D or 2D DP problem | [Interview Question 1](#question-1--walk-me-through-your-process-for-solving-a-dynamic-programming-problem-you-havent-seen-before) |
| L3 | Derive the precise loop-direction mechanism distinguishing 0/1 from unbounded knapsack, and the fill-order requirement for interval DP | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real 0/1-vs-unbounded knapsack bug from its exact loop-direction error (Section 17), and design a real shared-caching system using the precomputed-helper-table principle deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
