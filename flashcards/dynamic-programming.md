---
title: "Flashcards: Dynamic Programming: 1D, 2D, Knapsack, and Intervals"
slug: dynamic-programming
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2110
canonical: ../syllabus/03-data-structures-algorithms/dynamic-programming.md
last_updated: 2026-09-07
---

# Flashcards: Dynamic Programming: 1D, 2D, Knapsack, and Intervals

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/dynamic-programming.md`](../syllabus/03-data-structures-algorithms/dynamic-programming.md)

## Card: The three DP ingredients, in order

**Prompt:**
What are the three things every DP solution needs, and in what order should you find them?

**Answer:**
(1) A state — what `dp[i]` (or `dp[i][j]`) represents, in one plain-English sentence; (2) a recurrence — how a state's answer relates to smaller states' answers; (3) base cases — the smallest states, answered directly. Skipping straight to code without nailing the state definition first is the most common cause of a DP problem feeling intractable.

**Why it matters:**
It turns "finding the recurrence" from an unguided flash of insight into a repeatable process.

**Common trap:**
Writing DP code before defining the state in plain English.

**Related:**
[syllabus/03-data-structures-algorithms/dynamic-programming.md](../syllabus/03-data-structures-algorithms/dynamic-programming.md)

## Card: When DP applies — the two required properties

**Prompt:**
What two properties must a problem have for dynamic programming (memoization) to apply?

**Answer:**
Overlapping subproblems (the same smaller question is asked repeatedly under naive recursion) and optimal substructure (the best whole-problem answer can be built from the best answers to its subproblems).

**Why it matters:**
When both hold, storing each subproblem's answer the first time it's computed turns an exponential naive recursion into a polynomial one.

**Common trap:**
Applying memoization to a problem that lacks optimal substructure, where a locally optimal subproblem answer doesn't actually compose into a globally optimal one.

**Related:**
[syllabus/03-data-structures-algorithms/dynamic-programming.md](../syllabus/03-data-structures-algorithms/dynamic-programming.md)

## Card: Coin Change II's loop order and what it counts

**Prompt:**
In Coin Change II, why does iterating coins in the outer loop and amounts in the inner loop count combinations rather than permutations?

**Answer:**
Every combination is built by considering coins in one fixed, canonical order — so `{1, 2}` summing to 3 is only ever counted as "one 1, then one 2," never also as "one 2, then one 1." Swapping the loop order (amount outer, coin inner) would instead count every ordering of coins reaching a given amount as distinct.

**Why it matters:**
This is one of DP's most common subtle bugs — the loop order, not the recurrence's math, silently determines whether you're counting combinations or permutations.

**Common trap:**
Swapping the loop order without realizing it changes what's actually being counted.

**Related:**
[syllabus/03-data-structures-algorithms/dynamic-programming.md](../syllabus/03-data-structures-algorithms/dynamic-programming.md)

## Card: 0/1 vs. unbounded knapsack loop direction

**Prompt:**
What single change to a 1D rolling-array knapsack DP flips it between 0/1 semantics (Target Sum) and unbounded semantics (Coin Change II)?

**Answer:**
Iterating the capacity dimension downward ensures each `dp[s - num]` read reflects the state before the current item was considered, so the item contributes at most once (0/1). Iterating upward means a read can reflect a state that already includes the current item, allowing unlimited reuse (unbounded).

**Why it matters:**
This single loop-direction flip is the entire difference between the two knapsack variants — worth deriving, not memorizing.

**Common trap:**
Memorizing "unbounded goes forward, 0/1 goes backward" as an arbitrary rule instead of deriving it from what each direction makes visible during the update.

**Related:**
[syllabus/03-data-structures-algorithms/dynamic-programming.md](../syllabus/03-data-structures-algorithms/dynamic-programming.md)

## Card: Interval DP fill-order requirement

**Prompt:**
Why must Longest Palindromic Subsequence's `dp[i][j]` table be filled with `i` decreasing and `j` increasing from `i+1`?

**Answer:**
Computing `dp[i][j]` requires `dp[i+1][j-1]`, `dp[i+1][j]`, and `dp[i][j-1]` — all strictly shorter ranges than `[i, j]`. This fill order guarantees every dependency is computed before it's read.

**Why it matters:**
Getting the fill order backwards produces a program that reads uninitialized (zero-valued) cells instead of crashing — a silently wrong answer, not an exception.

**Common trap:**
Filling the table in row-major order by habit without checking that a state's dependencies are actually already computed at that point.

**Related:**
[syllabus/03-data-structures-algorithms/dynamic-programming.md](../syllabus/03-data-structures-algorithms/dynamic-programming.md)

## Card: Precomputed-helper-table technique

**Prompt:**
How does Palindrome Partitioning II bring its total complexity from O(n³) to O(n²)?

**Answer:**
It separates the DP into two stages: first build an O(n²) helper table answering "is this substring a palindrome" once for every `(i, j)` pair, then run the main DP treating every lookup into that table as O(1), instead of recomputing palindrome-ness inline inside the main loop.

**Why it matters:**
This is a directly transferable technique — precompute a repeatedly-needed helper fact once rather than recomputing it inline across many DP states.

**Common trap:**
Recomputing a repeatedly-needed helper fact (like palindrome-ness) inline inside the main DP loop, silently degrading the overall complexity class.

**Related:**
[syllabus/03-data-structures-algorithms/dynamic-programming.md](../syllabus/03-data-structures-algorithms/dynamic-programming.md)
