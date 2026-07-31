---
title: "Java Coding Practice — Week 7"
week: 7
last_reviewed: 2026-07-31
---

# Java Coding Practice — Week 7

**4 backtracking problems + the errata #3 drill. All code compiled and executed — see the verification block and `MANIFEST.md`.**

## Table of Contents

1. [LC 46 — Permutations (errata #3 drill)](#lc-46--permutations-errata-3-drill)
2. [LC 78 — Subsets](#lc-78--subsets)
3. [LC 39 — Combination Sum](#lc-39--combination-sum)
4. [LC 22 — Generate Parentheses](#lc-22--generate-parentheses)
5. [Verification](#verification--real-not-asserted)

---

## LC 46 — Permutations (errata #3 drill)

The Phase 1 audit found the source material's `permute` used `temp.contains(num)` — a **value-based** check — to decide whether a number is already placed. **Real, measured consequence**, input `[1,1,2]` (a duplicate value):

```
Buggy (value-based temp.contains()):  0 permutations found: []
Fixed (index-based used[] array):     6 permutations found
```

**Total failure, not under-counting:** with a value-based check, once *any* `1` is placed, `temp.contains(1)` is true regardless of which index a later `1` would come from — a second `1` can never be placed, so no length-3 permutation ever completes.

```java
// FIXED — index-based used[] array distinguishes array SLOTS, not just values
static void backtrack(int[] nums, List<Integer> temp, boolean[] used, List<List<Integer>> result) {
    if (temp.size() == nums.length) { result.add(new ArrayList<>(temp)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue; // per-INDEX, correct even with duplicate values
        used[i] = true;
        temp.add(nums[i]);
        backtrack(nums, temp, used, result);
        temp.remove(temp.size() - 1);
        used[i] = false;
    }
}
```

**Complexity:** O(n · n!) time (n! permutations, O(n) to copy each into the result), O(n) space for the recursion stack and `used[]` array.

## LC 78 — Subsets

```java
static void subsetsBacktrack(int[] nums, int start, List<Integer> temp, List<List<Integer>> result) {
    result.add(new ArrayList<>(temp)); // every partial state IS a valid subset
    for (int i = start; i < nums.length; i++) {
        temp.add(nums[i]);
        subsetsBacktrack(nums, i + 1, temp, result);
        temp.remove(temp.size() - 1);
    }
}
```

**Invariant:** recording `temp` at the *start* of every call (not just at completion) generates all 2ⁿ subsets, including the empty one — unlike LC 46, there's no single terminal condition; every recursion-tree node is itself a valid answer. **Complexity:** O(n · 2ⁿ) time, O(n) space.

## LC 39 — Combination Sum

```java
static void combSumBacktrack(int[] candidates, int remaining, int start, List<Integer> temp, List<List<Integer>> result) {
    if (remaining == 0) { result.add(new ArrayList<>(temp)); return; }
    if (remaining < 0) return;
    for (int i = start; i < candidates.length; i++) {
        temp.add(candidates[i]);
        combSumBacktrack(candidates, remaining - candidates[i], i, temp, result); // i, not i+1
        temp.remove(temp.size() - 1);
    }
}
```

**Invariant:** passing `i` (not `i + 1`) allows the same candidate to be reused arbitrarily many times — the single-character difference from LC 78's `i + 1` is the entire distinction between "each element once" and "reusable." **Complexity:** exponential worst case, bounded by `target / min(candidates)` recursion depth.

## LC 22 — Generate Parentheses

```java
static void genParenBacktrack(int n, int open, int close, StringBuilder sb, List<String> result) {
    if (sb.length() == 2 * n) { result.add(sb.toString()); return; }
    if (open < n) { sb.append('('); genParenBacktrack(n, open + 1, close, sb, result); sb.deleteCharAt(sb.length() - 1); }
    if (close < open) { sb.append(')'); genParenBacktrack(n, open, close + 1, sb, result); sb.deleteCharAt(sb.length() - 1); }
}
```

**Invariant:** tracking `open`/`close` counts and only appending a valid next character prunes every invalid branch immediately — no generate-then-validate pass needed. **Complexity:** O(4ⁿ / √n) (Catalan-number bound), far better than generating and filtering all 2^(2n) raw strings.

## Verification — real, not asserted

```
== Errata drill: LC 46 with a DUPLICATE-VALUE input [1,1,2] ==
Buggy (value-based temp.contains()):  0 permutations found: []
Fixed (index-based used[] array):     6 permutations found
  PASS  fixed permute produces all 3! = 6 position-based permutations
  PASS  buggy permute UNDER-COUNTS on duplicate-value input (this IS the bug, reproduced on purpose)

== LC 46 on a normal, no-duplicates input ==
  PASS  permute([1,2,3]) produces 6 permutations

== LC 78: Subsets ==
  PASS  subsets([1,2,3]) produces 2^3 = 8 subsets, including the empty set
  PASS  subsets includes the empty subset
  PASS  subsets includes the full set

== LC 39: Combination Sum ==
  PASS  combinationSum([2,3,6,7], 7) finds exactly 2 combinations
  PASS  combinationSum finds [7]
  PASS  combinationSum finds [2,2,3] (element reused)

== LC 22: Generate Parentheses ==
  PASS  generateParenthesis(3) produces the Catalan number C(3) = 5 combinations
  PASS  generateParenthesis includes ((()))
  PASS  generateParenthesis includes ()()()
Week 7 backtracking suite: 12/12 assertions passed
```

Full output and reproduce instructions: `practice/java/week-07/backtracking/README.md`.

## Exit check

- [ ] All 4 problems solved with a written retrospective
- [ ] Can explain, from first principles, why the buggy permute finds ZERO results on duplicate input, not just "fewer"
- [ ] Can state the single-token difference (`i` vs `i + 1`) between LC 78 and LC 39 unprompted
