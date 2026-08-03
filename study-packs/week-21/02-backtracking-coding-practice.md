---
title: "Coding Practice — Backtracking (T-1410)"
week: 21
document_type: study-pack-coding-practice
status: draft
last_reviewed: 2026-08-03
---

# Coding Practice — Backtracking (T-1410)

**5 problems. All code on this page was compiled and executed — see `MANIFEST.md` for the exact commands and real pass counts.** Brings this pattern's coverage from 4/14 to 9/14. Previous coverage (LC 46 Permutations, LC 78 Subsets, LC 39 Combination Sum, LC 22 Generate Parentheses, in `study-packs/week-07/04-java-coding-practice.md`) covered the "no duplicates in input" case; this batch adds the foundational phone-letters problem, grid backtracking, and the two classic *duplicate-input* dedup variants plus N-Queens.

---

## Problem 1 — LC 17 Letter Combinations of a Phone Number

**Pattern:** foundational Cartesian-product backtracking — the base case every other backtracking problem builds on.

```java
static List<String> letterCombinations(String digits) {
    List<String> result = new ArrayList<>();
    if (digits.isEmpty()) return result;
    backtrack(digits, 0, new StringBuilder(), result);
    return result;
}

private static void backtrack(String digits, int idx, StringBuilder cur, List<String> result) {
    if (idx == digits.length()) { result.add(cur.toString()); return; }
    for (char c : DIGIT_LETTERS[digits.charAt(idx) - '0'].toCharArray()) {
        cur.append(c);
        backtrack(digits, idx + 1, cur, result);
        cur.deleteCharAt(cur.length() - 1);
    }
}
```

**Retrospective:** this is the template every subsequent problem in this file specializes: pick one choice, recurse into the next decision, then undo the choice (`deleteCharAt`) before trying the next option at that level — the undo step is what makes it *backtracking* rather than plain recursion. **Complexity:** O(4^n · n) worst case (digits 7 and 9 map to 4 letters), where n is `digits.length()`.

## Problem 2 — LC 79 Word Search

**Pattern:** grid DFS with in-place visited marking, undone on backtrack.

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

**Retrospective:** marking the current cell `'#'` before recursing and restoring it (`board[r][c] = saved`) after all four directions fail is the visited-set pattern applied to backtracking — without the restore, a failed path down one branch would permanently block a different, valid path that legitimately needs to revisit that cell. This is why LC 212 (Problem 2 of the Tries file) needed the same restore discipline. **Complexity:** O(rows·cols·4^L) worst case, where L is the word length.

## Problem 3 — LC 47 Permutations II

**Pattern:** permutation backtracking with duplicate-value pruning, contrasted with LC 46's no-duplicate case.

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

**Retrospective:** the standard LC 46 permutation loop, applied directly to an array with duplicates, produces the same permutation multiple times (e.g., picking either of two `1`s at a given position is indistinguishable in the output). Sorting first, then skipping `nums[i] == nums[i-1]` *only when the earlier duplicate is currently unused* (`!used[i-1]`), prunes exactly the redundant branches while still allowing the duplicate value to be used later in the same permutation. **Complexity:** O(n · n!) time in the worst case (no duplicates), less with duplicates due to pruning.

## Problem 4 — LC 40 Combination Sum II

**Pattern:** subset-sum backtracking with duplicate-input pruning and each-candidate-used-once, contrasted with LC 39's unlimited-reuse case.

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

**Retrospective:** two changes distinguish this from LC 39: recursing on `i + 1` instead of `i` (each array *index* can be used at most once, even though the same *value* may appear at multiple indices), and skipping `candidates[i] == candidates[i-1]` only `i > start` (siblings within the same recursion level), not across levels — this is what allows `[1,1,6]` (using both 1s from different depths) while forbidding a duplicate `[1,7]` combination from being generated twice. **Complexity:** O(2^n) worst case, pruned significantly by the sorted early-break and dedup checks.

## Problem 5 — LC 51 N-Queens

**Pattern:** constraint-satisfaction backtracking with O(1) conflict checks via three boolean arrays (column, diagonal, anti-diagonal) instead of re-scanning the board.

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

**Retrospective:** every cell on a given "/" diagonal shares the same `row + col` value, and every cell on a given "\" diagonal shares the same `row - col` value (offset by `n-1` here to keep the index non-negative) — precomputing these as boolean lookup arrays turns what would otherwise be an O(n) re-scan of placed queens into an O(1) conflict check per candidate column, which is the difference between a solution that finishes for n=8 instantly and one that visibly stalls. This is a strong Staff-level talking point: recognizing an O(n) check hiding inside a hot backtracking loop and eliminating it with precomputed state. **Complexity:** O(n!) time worst case (inherent to the problem), O(n) space for the tracking arrays plus recursion depth.

## Verification

```
$ cd practice/java/week-21/backtracking/src && javac -d ../out Check.java Problems.java Main.java && java -cp ../out Main
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
