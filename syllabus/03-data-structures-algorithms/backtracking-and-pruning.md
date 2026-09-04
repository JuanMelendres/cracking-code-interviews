---
title: "Backtracking and Pruning"
slug: backtracking-and-pruning
document_type: syllabus-topic
domain: 03-data-structures-algorithms
topic_id: T-2109
status: draft
version: 1.0
last_updated: 2026-09-03
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - ../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md
related:
  - graphs-bfs-dfs-and-shortest-paths.md
practice: ../../practice/java/week-21/backtracking/
production_scenarios: []
interview_paths: [interview-emergency-sprint, senior-to-staff]
official_references: []
source_history:
  - study-packs/week-21/02-backtracking-coding-practice.md
---

# Backtracking and Pruning

> **Provenance.** The five worked problems and retrospectives in Sections 7 and 15 are elevated from `study-packs/week-21/02-backtracking-coding-practice.md` — real, compiled, executed code (`practice/java/week-21/backtracking/`), re-verified on OpenJDK 21.0.12 while writing this chapter (12/12 assertions passing).

This is Master Topic Register **T-1410** (IWI 5.1, high frequency). Backtracking is DFS ([Graphs](graphs-bfs-dfs-and-shortest-paths.md#3-foundation-l1)) applied to a tree of *choices* rather than a tree of *nodes* — exploring one decision path at a time, undoing a choice the moment it's exhausted, and trying the next option.

## 1. Why This Matters

Backtracking problems are exhaustive-search problems in disguise — generate every permutation, every subset, every valid arrangement — and without pruning, most of them are exponential by nature (there's no way around exploring an exponential number of possibilities for genuinely combinatorial problems like N-Queens or generating all permutations). The actual interview skill is threefold: writing the choose/recurse/undo template correctly and consistently, recognizing where legitimate pruning is possible (cutting off entire branches that can't lead to a valid answer), and correctly handling duplicate input values without either missing valid results or producing duplicate ones.

## 2. Prerequisites

[Algorithmic Complexity and Big-O](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md) — backtracking complexity is usually exponential (`O(2^n)`, `O(n!)`, `O(k^n)`) by the nature of the problems it solves, and recognizing that this exponential cost is often *inherent to the problem*, not a sign the algorithm is wrong, is itself part of this pattern's mental model.

## 3. Foundation (L1)

**Backtracking explores a tree of choices: at each step, try one option, recurse into the consequences of that choice, then undo it and try the next option** — the "undo" step is what makes it backtracking rather than plain recursion or brute force. The canonical template: pick a choice, add it to the current partial solution, recurse, then remove it from the partial solution before the loop tries the next choice at that same level.

**Every backtracking problem in this chapter follows the identical three-step shape** (Section 7, Problem 1 is the clearest example of it in isolation): make a choice, recurse one level deeper, undo the choice — repeated for every available option at every level of the recursion, until a complete solution is assembled or a branch is abandoned as invalid.

## 4. Core Concepts (L2)

**Grid/graph backtracking** (Word Search, Section 7, Problem 2) applies the same choose/recurse/undo template to a 2D grid, using in-place marking (temporarily overwriting a visited cell, then restoring it) as the "undo" step — the same visited-tracking idea [Graphs' BFS/DFS](graphs-bfs-dfs-and-shortest-paths.md#3-foundation-l1) use, applied here to prevent reusing the same cell twice within one candidate path, not to avoid infinite loops across the whole search.

**Duplicate-input pruning** (Permutations II and Combination Sum II, Section 7, Problems 3 and 4) is required whenever the input array can contain repeated values — the naive choose/recurse/undo template, applied directly to an array with duplicates, generates the same output multiple times. The fix, in both problems, is the same underlying idea: sort the input first, then explicitly skip over a duplicate value *at the same recursion depth* as an already-tried identical value — but the exact skip condition differs depending on whether each array *index* can be reused (Combination Sum II: no) or whether the same *value* can appear at a different position later in the same output (Permutations II: yes, just not as an immediate sibling choice).

**Constraint-satisfaction backtracking with precomputed conflict state** (N-Queens, Section 7, Problem 5) uses auxiliary boolean arrays (tracking used columns and diagonals) to turn what would otherwise be an O(n) re-scan of already-placed pieces into an O(1) conflict check per candidate move — a direct application of trading memory for a complexity-class improvement, the same trade-off [Hashing Patterns](hashing-patterns-and-frequency-maps.md#11-trade-offs) makes for lookup speed, here applied inside a backtracking loop's hot path specifically.

## 5. How It Works Internally (L3)

**Permutations II's duplicate-skip condition, precisely**: after sorting, `if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) continue;` skips a duplicate value *only* when the identical earlier value hasn't been used yet at this recursion level — this specific condition (not simply "skip if equal to the previous value") is what allows the duplicate value to still be used *later*, in a different position within the same permutation, while preventing the redundant branch where two indistinguishable choices (two `1`s, say) produce two output-identical permutations by being tried in a different order among *siblings* at the same level. Getting this condition slightly wrong (checking `used[i-1]` instead of `!used[i-1]`, a very easy transposition) either misses valid permutations or fails to deduplicate at all — a genuinely delicate correctness detail worth deriving carefully rather than pattern-matching from memory.

**Combination Sum II's two distinguishing changes from its no-duplicates sibling (Combination Sum)**: recursing on `i + 1` instead of `i` means each array *index* is used at most once, even though the same numeric *value* might appear at multiple indices (contrast with Combination Sum's unlimited reuse of the same index); and skipping `candidates[i] == candidates[i-1]` specifically when `i > start` (a sibling within the *same* recursion level, not across levels) is what permits `[1,1,6]` (using both `1`s, drawn from different recursion depths) while still forbidding a duplicate `[1,7]` combination from being generated twice from two different, indistinguishable `1`-then-`7` choices at the same level.

**N-Queens' O(1) conflict-check derivation**: every cell on the same "/" diagonal shares an identical `row + col` value; every cell on the same "\" diagonal shares an identical `row - col` value (offset by `n-1` to keep array indices non-negative). Precomputing these two facts as boolean lookup arrays converts "is this move safe" from an O(n) scan of every previously-placed queen into an O(1) array lookup — the concrete, measurable difference between a solution that finishes instantly for `n=8` and one that visibly stalls, since this conflict check runs inside the backtracking algorithm's innermost, most frequently executed loop.

## 6. Practical Usage

- **Write the choose/recurse/undo skeleton first, correctly, before adding any pruning** — Letter Combinations of a Phone Number (Section 7, Problem 1) is the template every other problem in this chapter specializes; getting the bare skeleton right first makes adding constraints and pruning additive rather than a rewrite.
- **Sort the input first whenever duplicate values need deduplication** — both duplicate-pruning problems in this chapter depend on sorting to make "is this value identical to my sibling" a simple adjacent-element check.
- **Look for any O(n) check inside the innermost backtracking loop and ask whether precomputed state (N-Queens' boolean arrays) could make it O(1)** — a concrete, transferable optimization technique, not specific to N-Queens alone.

## 7. Examples

**Problem 1 — LC 17, Letter Combinations of a Phone Number.**

```java
private static void backtrack(String digits, int idx, StringBuilder cur, List<String> result) {
    if (idx == digits.length()) { result.add(cur.toString()); return; }
    for (char c : DIGIT_LETTERS[digits.charAt(idx) - '0'].toCharArray()) {
        cur.append(c);
        backtrack(digits, idx + 1, cur, result);
        cur.deleteCharAt(cur.length() - 1);
    }
}
```

**Retrospective:** the template every subsequent problem specializes — pick, recurse, undo. **Complexity:** O(4^n · n) worst case.

**Problem 2 — LC 79, Word Search.**

```java
private static boolean dfs(char[][] board, int r, int c, String word, int idx) {
    if (idx == word.length()) return true;
    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != word.charAt(idx)) return false;
    char saved = board[r][c];
    board[r][c] = '#';
    boolean found = dfs(board, r+1, c, word, idx+1) || dfs(board, r-1, c, word, idx+1)
        || dfs(board, r, c+1, word, idx+1) || dfs(board, r, c-1, word, idx+1);
    board[r][c] = saved;
    return found;
}
```

**Retrospective:** marking then restoring is the visited-set pattern applied to backtracking — without the restore, a failed branch permanently blocks a different, valid path. **Complexity:** O(rows·cols·4^L), L = word length.

**Problem 3 — LC 47, Permutations II.**

```java
private static void backtrack(int[] nums, boolean[] used, List<Integer> cur, List<List<Integer>> result) {
    if (cur.size() == nums.length) { result.add(new ArrayList<>(cur)); return; }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue; // skip same-value sibling at this depth
        used[i] = true;
        cur.add(nums[i]);
        backtrack(nums, used, cur, result);
        cur.remove(cur.size() - 1);
        used[i] = false;
    }
}
```

**Retrospective:** see Section 5's precise skip-condition derivation. **Complexity:** O(n · n!) worst case, less with duplicates.

**Problem 4 — LC 40, Combination Sum II.**

```java
private static void backtrack(int[] candidates, int remaining, int start, List<Integer> cur, List<List<Integer>> result) {
    if (remaining == 0) { result.add(new ArrayList<>(cur)); return; }
    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > remaining) break; // sorted array: no later candidate can work either
        if (i > start && candidates[i] == candidates[i - 1]) continue; // skip duplicate sibling
        cur.add(candidates[i]);
        backtrack(candidates, remaining - candidates[i], i + 1, cur, result); // i+1, not i: each index used once
        cur.remove(cur.size() - 1);
    }
}
```

**Retrospective:** see Section 5's two-changes-from-Combination-Sum argument. **Complexity:** O(2^n) worst case, pruned significantly.

**Problem 5 — LC 51, N-Queens.**

```java
private static void backtrack(int row, int n, int[] queenCol, boolean[] usedCol,
                               boolean[] usedDiag, boolean[] usedAntiDiag, List<List<String>> result) {
    if (row == n) { result.add(buildBoard(queenCol, n)); return; }
    for (int col = 0; col < n; col++) {
        int diag = row - col + (n - 1), antiDiag = row + col;
        if (usedCol[col] || usedDiag[diag] || usedAntiDiag[antiDiag]) continue;
        queenCol[row] = col;
        usedCol[col] = usedDiag[diag] = usedAntiDiag[antiDiag] = true;
        backtrack(row + 1, n, queenCol, usedCol, usedDiag, usedAntiDiag, result);
        usedCol[col] = usedDiag[diag] = usedAntiDiag[antiDiag] = false;
    }
}
```

**Retrospective:** see Section 5's O(1)-conflict-check derivation. A strong Staff-level talking point: recognizing an O(n) check hiding inside a hot backtracking loop. **Complexity:** O(n!) worst case (inherent to the problem), O(n) space.

## 8. Common Mistakes

- **Forgetting the "undo" step**, or undoing the wrong piece of state — leaves a stale, incorrect partial solution polluting sibling branches at the same recursion level.
- **Skipping duplicates with the wrong condition** — Section 5's precise derivation for Permutations II (`!used[i-1]`, not `used[i-1]`) is a real, easy-to-transpose mistake that either silently under- or over-generates results.
- **Confusing "skip duplicate at this level" with "never reuse this value again"** — Combination Sum II's `i > start` check specifically permits the same value at *different* recursion depths (Section 5) while forbidding it as an immediate sibling; a coarser check would incorrectly forbid valid combinations like `[1,1,6]`.
- **Leaving an O(n) conflict check inside a backtracking loop's hot path** when precomputed state (N-Queens' boolean arrays) could make it O(1) — not incorrect, but the difference between a solution that finishes instantly and one that visibly stalls at moderate input sizes.

## 9. Edge Cases

- **Empty input** (Letter Combinations' own verified empty-string case, correctly returning an empty list rather than a list containing one empty string) — a real, easy-to-miss base-case distinction.
- **A word that would require reusing the same grid cell twice** (Word Search's verified `"ABCB"` reuse-cell case, correctly returning `false`) — confirms the visited-marking discipline actually prevents illegal reuse.
- **An input with all identical values** — Permutations II's duplicate-pruning logic must reduce the output to genuinely distinct permutations, not silently under- or over-count them.
- **A completely infeasible constraint-satisfaction problem** (an N-Queens board too small for any solution, e.g., `n=2` or `n=3`) — the algorithm must correctly return an empty result set, not crash or hang.

## 10. Performance Implications

Real, executed verification from `practice/java/week-21/backtracking/` (OpenJDK 21.0.12), re-run while writing this chapter:

```
  PASS  LC17 letterCombinations(23) count = 9
  PASS  LC17 letterCombinations(23) exact set
  PASS  LC17 letterCombinations(empty) = []
  PASS  LC79 exist(ABCCED) -> true
  PASS  LC79 exist(SEE) -> true
  PASS  LC79 exist(ABCB) reuse-cell -> false
  PASS  LC47 permuteUnique([1,1,2]) unique count = 3
  PASS  LC47 permuteUnique([1,1,2]) exact set
  PASS  LC40 combinationSum2 target 8 -> 4 combinations
  PASS  LC40 combinationSum2 exact set, no duplicate [1,7] twice
  PASS  LC51 solveNQueens(4) solution count = 2
  PASS  LC51 solveNQueens(8) solution count = 92 (well-known result)
Week 21 — Backtracking (LC 17, 79, 47, 40, 51): 12/12 assertions passed
```

N-Queens for `n=8` producing exactly 92 solutions (a well-known, independently verifiable result) is real, direct confirmation the O(1)-conflict-check optimization (Section 5) doesn't change *correctness*, only speed — the same 92 solutions would result from the unoptimized O(n)-rescan version, just measurably slower. This is the concrete performance lesson: pruning and precomputed-state optimizations in backtracking change the constant factor and prune dead branches earlier, but the *set of valid solutions found* must remain identical — a useful, checkable invariant when validating an optimization didn't accidentally change correctness.

## 11. Trade-offs

| Choice | Gains | Costs |
|---|---|---|
| Plain choose/recurse/undo | Simple, direct, correct starting point | No pruning — explores the full exponential search space even when most branches are obviously invalid |
| Early pruning (e.g., `if (candidates[i] > remaining) break`) | Cuts off entire invalid branches before recursing into them | Requires the input to be sorted, or some other structural property enabling early detection |
| Precomputed conflict state (N-Queens) | O(1) conflict checks instead of O(n) re-scans | O(n) extra memory for the tracking arrays |
| In-place marking (Word Search) | O(1) extra space for visited-tracking | Temporarily mutates shared input state — requires careful, guaranteed restoration on every exit path |

## 12. Senior-Level Considerations (L3)

The Senior-level skill is distinguishing pruning that changes *performance* from pruning that would change *correctness* — an early `break` on a sorted array (Combination Sum II) is safe specifically because sortedness guarantees no later candidate could work either; applying the same kind of early exit to an unsorted collection, or based on a property that isn't actually monotonic, would silently skip valid solutions rather than merely skip invalid ones faster. Being able to state, precisely, *why* a specific prune is safe — not just that a candidate solution happens to include one — is what an interviewer probing past a correct-looking answer is checking for.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, backtracking's exhaustive-search-with-pruning shape transfers directly to real constraint-satisfaction systems: a scheduling system searching for a valid assignment of resources to time slots subject to conflict constraints, or a configuration system searching for a valid combination of feature flags subject to compatibility constraints, is solving structurally the same problem N-Queens solves — and the same O(1)-conflict-check-via-precomputed-state optimization (Section 5) that makes N-Queens tractable at real board sizes is often the difference between a real constraint-satisfaction system that completes in a reasonable time and one that visibly stalls at production scale, once the search space grows past what a naive re-scan-per-candidate approach can handle.

## 14. Production Scenarios

No existing `production-cookbook/` entry has a backtracking-specific algorithmic root cause.

> Planned reference: a future `production-cookbook/` entry covering a real constraint-satisfaction system (a scheduler, a configuration validator) that stalled at scale due to a missing precomputed-conflict-state optimization — the exact class of fix N-Queens' own algorithm demonstrates — would be a natural, non-duplicative addition connecting this chapter's Section 13 transfer to a genuine incident.

## 15. Interview Questions

### Question 1 — Generate all permutations of an array that may contain duplicate values, without producing duplicate permutations in the output.

**Why interviewers ask it.** It's the canonical duplicate-pruning test, checking whether a candidate's backtracking template correctly handles the genuinely delicate skip-condition derivation (Section 5) rather than either missing valid permutations or failing to deduplicate at all.

**Expected answer.** Sort the input first. In the backtracking loop, skip a candidate value if it equals the immediately preceding value in the sorted array *and* that preceding value hasn't been used yet at the current recursion level (`!used[i-1]`) — this specific condition allows the duplicate value to still be chosen at a different point in the permutation while preventing the redundant sibling branch that would otherwise produce an output-identical permutation.

**Minimum acceptable answer.** Produces a correct solution, even via a less elegant approach (e.g., generating all permutations naively, then deduplicating the output with a `Set`).

**Strong Senior answer.** Produces the sort-plus-skip-condition approach directly, and can precisely justify why the condition checks `!used[i-1]` rather than simply `nums[i] == nums[i-1]` alone.

**Staff-level extension.** Explains the complexity trade-off between the naive-then-deduplicate approach (generates the full, possibly much larger, un-deduplicated set before filtering) and the pruning approach (never generates the duplicate branches at all) — a real, measurable efficiency difference that grows with the number of duplicate values in the input.

**Common mistakes.** Getting the skip condition backwards (`used[i-1]` instead of `!used[i-1]`) — a subtle, easy-to-make error that silently produces wrong output rather than a compile error or crash.

**Follow-up questions.** "How would this change for generating unique *combinations* instead of permutations?" (Combination Sum II, Section 7, Problem 4 — a related but structurally distinct duplicate-pruning condition, since combinations don't care about order the way permutations do.)

### Question 2 — Why does N-Queens use three separate boolean arrays for columns, diagonals, and anti-diagonals, instead of checking the board directly?

**Why interviewers ask it.** It tests whether a candidate can recognize and eliminate a hidden O(n) cost inside a hot recursive loop — a genuinely transferable optimization skill beyond this one specific problem.

**Expected answer.** Checking the board directly for a conflict (scanning all previously placed queens to see if any share a column or diagonal with the candidate position) costs O(n) per candidate move, since up to `n` queens might already be placed. Precomputing boolean arrays — one per column, and one each for the two diagonal directions, using the fact that every cell on the same diagonal shares either `row - col` or `row + col` — converts each conflict check into an O(1) array lookup, since placing or removing a queen just flips the corresponding boolean flags rather than requiring a fresh scan.

**Minimum acceptable answer.** Produces a correct N-Queens solution, even the O(n)-per-check version, and can identify the conflict-checking step as a possible optimization target when prompted.

**Strong Senior answer.** Derives the `row - col` / `row + col` diagonal-identity facts directly and implements the O(1) version without needing it explained first.

**Staff-level extension.** Generalizes the principle explicitly: any time a backtracking (or general recursive search) algorithm's per-candidate validity check scans already-committed state, ask whether that state can instead be maintained incrementally (updated on each choose/undo) rather than recomputed from scratch — the same principle behind [Stacks' amortized-cost arguments](stacks-and-monotonic-stack.md#5-how-it-works-internally-l3) and [Algorithmic Complexity's own amortized analysis](../01-computer-science-foundations/algorithmic-complexity-and-big-o-from-first-principles.md#4-core-concepts-l2), applied here to backtracking's specific choose/recurse/undo structure.

**Common mistakes.** Treating N-Queens' exponential time complexity (inherent to the problem) as something the boolean-array optimization is meant to fix — it isn't; the optimization reduces the *constant factor per candidate check*, not the fundamental O(n!) search-space size.

**Follow-up questions.** "Does this optimization change how many solutions are found?" (No — Section 10's own verification point: the same 92 solutions for `n=8` result either way; the optimization only affects speed, never correctness.)

## 16. Coding/Practice Exercises

- Run the [existing practice code](../../practice/java/week-21/backtracking/) yourself and confirm the same 12/12 assertions pass.
- This pattern has additional real, already-solved problems: LC 46 (Permutations, the no-duplicates case), LC 78 (Subsets), LC 39 (Combination Sum, unlimited reuse), and LC 22 (Generate Parentheses) in `study-packs/week-07/04-java-coding-practice.md`'s underlying practice code — study Combination Sum (LC 39) directly alongside this chapter's Combination Sum II (LC 40) to see the exact two changes Section 5 derives, side by side in real code.
- Implement the O(n)-per-check (unoptimized) version of N-Queens from scratch, then compare its measured wall-clock time against the existing O(1)-per-check version at `n=8` and `n=10` — confirm Section 10's claim that both produce identical solution counts while differing in speed.

## 17. Debugging Exercises

**Symptom:** a "generate all unique combinations summing to a target" backtracking implementation, given an input array with duplicate values, either produces duplicate combinations in its output or is missing some valid combinations that should include a repeated value.

**Diagnose:** check the duplicate-skip condition against Section 5's precise derivation for Combination Sum II — the skip should trigger only when `i > start` (a sibling at the *same* recursion level as an already-tried identical value), not for every occurrence of a duplicate value regardless of recursion depth. A too-broad skip condition (checking `i > 0` instead of `i > start`) would incorrectly forbid a valid combination like `[1,1,6]` that legitimately uses the same value from two different recursion depths; a missing or too-narrow skip condition would fail to deduplicate identical-looking combinations arising from different index choices at the same level. Confirm by constructing a small, controlled input with a known duplicate value and manually tracing which recursion level(s) the skip condition actually fires at.

## 18. Design Exercises

**Design constraint:** design a configuration-validation system that must find any valid assignment of a set of feature flags to `on`/`off`, subject to a list of pairwise incompatibility constraints (flag A and flag B cannot both be `on` simultaneously), and must do so efficiently even as the number of flags grows into the hundreds.

Design this using the backtracking-with-precomputed-conflict-state technique from Section 4/5/15 directly, modeled closely on N-Queens: maintain a boolean array (or bitset) tracking currently-active flags, and for each candidate flag to turn on, check its incompatibility list against the currently-active set in O(1) per check (via a precomputed incompatibility bitmask per flag, rather than re-scanning the full incompatibility list every time) — the same O(n)-to-O(1) conflict-check optimization N-Queens applies to diagonal conflicts. State explicitly why, at hundreds of flags, the naive O(n)-per-check version could become a real, measurable bottleneck in a system expected to validate configurations interactively, connecting this directly to Section 13's real-world transfer.

## 19. Further Reading

- [Graphs: BFS, DFS, Topological Sort, Dijkstra, and Union-Find](graphs-bfs-dfs-and-shortest-paths.md) — backtracking is DFS applied to a tree of choices; the visited-tracking discipline there is the direct ancestor of this chapter's in-place-marking technique (Word Search).
- [Hashing Patterns and Frequency Maps](hashing-patterns-and-frequency-maps.md) — the general memory-for-speed trade-off (Section 11 there) this chapter's N-Queens conflict-state optimization applies in a backtracking-specific form.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain, in plain language, the choose/recurse/undo shape of backtracking and why the "undo" step is essential | [Section 3](#3-foundation-l1) |
| L2 | Apply duplicate-input pruning correctly to a new permutation or combination problem, choosing the right skip condition | [Interview Question 1](#question-1--generate-all-permutations-of-an-array-that-may-contain-duplicate-values-without-producing-duplicate-permutations-in-the-output) |
| L3 | Derive the precise duplicate-skip conditions for both permutations and combinations, and the O(1)-conflict-check derivation for constraint-satisfaction backtracking | [Section 10's real verification](#10-performance-implications), [Section 5](#5-how-it-works-internally-l3) |
| L4 | Diagnose a real duplicate-combination bug from its exact skip-condition error (Section 17), and design a real constraint-satisfaction system using precomputed conflict state deliberately (Section 18) | [Debugging Exercise](#17-debugging-exercises), [Section 13](#13-staffsystem-level-considerations-l4) |
