---
title: "Cheat Sheet: Dynamic Programming: 1D, 2D, Knapsack, and Intervals"
slug: dynamic-programming
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2110
canonical: ../syllabus/03-data-structures-algorithms/dynamic-programming.md
last_updated: 2026-09-06
---

# Dynamic Programming: 1D, 2D, Knapsack, and Intervals

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/dynamic-programming.md`](../syllabus/03-data-structures-algorithms/dynamic-programming.md)

> The chapter's front matter records `topic_id: T-2110`; its body prose also references an older Master Topic Register code, **T-1411** (IWI 5.85, the register's single largest raw-count pattern by problem count) — both refer to the same chapter.

## Core Mental Model

Dynamic programming is recursion with memory: identical to plain recursion in *what* it computes, but avoiding exponential blowup by storing each subproblem's answer exactly once instead of recomputing it. DP applies when a problem has **overlapping subproblems** and **optimal substructure**. Every DP solution has three ingredients, found in this order: (1) a **state** — what `dp[i]` (or `dp[i][j]`) means, in one plain-English sentence; (2) a **recurrence** — how a state relates to smaller states; (3) **base cases**. Skipping straight to code without nailing the state definition first is the most common cause of "I don't know where to start."

## Essential Definitions

- **Overlapping subproblems** — the same smaller question is asked repeatedly during naive recursion.
- **Optimal substructure** — the best answer to the whole problem is built from the best answers to its subproblems.
- **Top-down (memoized recursion)** vs **bottom-up (tabulation)** — mirrors the natural recursion vs. no recursion overhead / rolling-array space optimization.
- **0/1 knapsack** — each item used at most once. **Unbounded knapsack** — unlimited reuse.
- **Interval DP** — operates over `(i, j)` ranges rather than single indices; requires filling shorter ranges before longer ones.
- **State-machine DP** — tracks a small, named set of mutually exclusive states (e.g. holding, just sold, resting) as rolling variables.

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Complexity |
|---|---|---|
| Two strings compared index by index (edit ops, common subsequence, palindromic substring) | Two-string grid DP, `dp[i][j]` | O(m·n) |
| "Count the number of ways" vs. "minimum/maximum value" over the same combinatorial shape | Counting DP (`+=`) vs. optimization DP (`min`/`max`) | Same state space, different combine op |
| Items with weights/values, capacity constraint, reuse allowed or not | Knapsack — loop order/direction determines semantics (see below) | O(n · capacity) |
| Grid where each cell depends only on cells above/left | Matrix-path DP | O(rows·cols) |
| Answer over a range `[i, j]` of a string/array | Interval DP, fill shorter ranges before longer ones | O(n²) states |
| A "circular" or tree-shaped version of an already-known linear recurrence | Reduce to the known recurrence (e.g. run it twice over two linear ranges) | Same as the base recurrence |

## Common Mistakes

- Writing DP code before defining the state in plain English — the single most common cause of an intractable-feeling DP problem.
- Getting the knapsack loop direction backwards (see below) — silently changes 0/1 semantics into unbounded semantics or vice versa, without any error.
- Getting the interval-DP fill order backwards — reads uninitialized array cells, producing a silently wrong answer rather than a crash.
- Re-deriving a recurrence from scratch for a problem that's actually a reduction of an already-solved one (House Robber II, Best Time to Buy/Sell IV) — wastes time and misses a pattern-fluency signal.
- Recomputing a repeatedly-needed helper fact (like palindrome-ness) inline inside the main DP loop instead of precomputing it once — silently degrades the complexity class (O(n³) instead of O(n²)).

## The Knapsack Loop-Direction Rule (memorize this)

- **Unbounded** (Coin Change II — count combinations, reuse allowed): coin loop **outer**, amount loop **inner**, amount iterates **upward**. Each combination is built in one canonical coin order, so it's counted once, not once per permutation.
- **0/1** (Target Sum — each item used at most once): capacity loop iterates **downward**. This ensures each `dp[s - num]` read reflects the state *before* the current item was considered, so the item contributes at most once.
- Derive this from first principles, don't memorize "unbounded goes forward, 0/1 goes backward" as an arbitrary rule — the loop direction determines what "already updated" state a read can see.

## Complexity Reference

- Edit Distance: O(m·n) time and space.
- House Robber II (circular): O(n) time, O(1) space.
- Coin Change II: O(amount · coins.length).
- Target Sum: O(n · subsetSum).
- Minimum Path Sum: O(rows·cols).
- Longest Palindromic Subsequence: O(n²) time and space.
- Best Time to Buy/Sell with Cooldown: O(n) time, O(1) space.
- House Robber III (tree DP): O(n) time, O(h) space.
- Best Time to Buy/Sell IV (at most k transactions): O(n·k), or O(n) via the `k >= n/2` early-exit.
- Palindrome Partitioning II: O(n²) time and space (precomputed helper table collapses O(n³) to O(n²)).

## Interview Answer Skeleton

**30-sec:** DP is recursion with memory — it applies when a problem has overlapping subproblems and optimal substructure, and it works by defining a state, deriving a recurrence connecting it to smaller states, and identifying base cases, in that order, before writing any code.

**2-min:** State the process explicitly: define `dp[i]` (or `dp[i][j]`) in plain English first, derive the recurrence by asking how that state relates to smaller states, identify base cases, then translate to code. Check whether the "new" problem is a reduction of an already-solved one before deriving a fresh recurrence — House Robber II's circular variant decomposes into two runs of the already-known linear House Robber recurrence. When a knapsack-shaped problem behaves unexpectedly (over/under-counting, wrong reuse semantics), check loop order and loop direction first, not the recurrence's math.

**Whiteboard:** Draw a small `dp` table (2D grid for a two-string problem like Edit Distance) and fill in a handful of cells by hand, showing the recurrence arrows (which neighboring cells a given cell depends on). For interval DP, draw the `(i, j)` triangle and show the fill direction (`i` downward, `j` upward from `i+1`) so every dependency is filled before it's read.

**Staff-level framing:** DP's core insight — cache and reuse a subproblem's answer instead of recomputing it — is the same principle behind memoization layers, materialized views, and computed-column caching in production systems. The precomputed-helper-table technique (Palindrome Partitioning II) directly mirrors precomputing and caching an expensive-but-frequently-needed derived fact once, rather than recomputing it inline on every access path that needs it.

## Production Warning Signs

- **Symptom:** an unbounded-knapsack-style "count ways to make change" implementation, converted to a 0/1-style "each coin used at most once" variant, produces counts that are too high rather than too low or a crash.
- **Diagnose:** check the capacity loop's direction — leaving it iterating upward (the unbounded-correct direction) while intending 0/1 semantics allows `dp[a - coin]` to read a value that may already include the current coin's contribution, silently permitting the same coin instance to be counted more than once. Fix by reversing the capacity loop to iterate downward. Confirm with a minimal test case (one coin, an amount that's a small multiple of it) where the unbounded and 0/1 answers provably differ.

## Related

- syllabus/03-data-structures-algorithms/trees-bst-and-traversal-patterns.md
- syllabus/03-data-structures-algorithms/hashing-patterns-and-frequency-maps.md
