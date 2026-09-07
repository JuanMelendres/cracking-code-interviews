---
title: "Flashcards: Backtracking and Pruning"
slug: backtracking-and-pruning
document_type: flashcard-deck
domain: 03-data-structures-algorithms
topic_id: T-2109
canonical: ../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md
last_updated: 2026-09-07
---

# Flashcards: Backtracking and Pruning

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/backtracking-and-pruning.md`](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)

## Card: The choose/recurse/undo template

**Prompt:**
What are the three steps every backtracking solution repeats at each level of recursion?

**Answer:**
Make a choice (add it to the partial solution), recurse one level deeper, then undo the choice (remove it) before the loop tries the next option at that same level.

**Why it matters:**
The "undo" step is what distinguishes backtracking from plain recursion or brute force — skipping it leaves stale state polluting sibling branches.

**Common trap:**
Forgetting the undo step, or undoing the wrong piece of state.

**Related:**
[syllabus/03-data-structures-algorithms/backtracking-and-pruning.md](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)

## Card: Permutations II duplicate-skip condition

**Prompt:**
After sorting, what exact condition skips a duplicate value in Permutations II, and why is it `!used[i-1]` rather than `used[i-1]`?

**Answer:**
`if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) continue;` — skipping only when the identical earlier value hasn't been used yet at this recursion level allows that value to still be chosen later, in a different position, while forbidding the redundant sibling branch at the same level.

**Why it matters:**
Transposing this to `used[i-1]` either misses valid permutations or fails to deduplicate at all — a subtle, easy-to-make error.

**Common trap:**
Checking `used[i-1]` instead of `!used[i-1]`.

**Related:**
[syllabus/03-data-structures-algorithms/backtracking-and-pruning.md](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)

## Card: Combination Sum II's two changes from Combination Sum

**Prompt:**
What two changes distinguish Combination Sum II (each value used at most once) from Combination Sum (unlimited reuse)?

**Answer:**
Recursing on `i + 1` instead of `i` (each array index used at most once), and skipping `candidates[i] == candidates[i-1]` only when `i > start` (a sibling at the same recursion level, not across levels) — which permits `[1,1,6]` while forbidding a duplicate `[1,7]` generated twice.

**Why it matters:**
A too-broad skip condition (e.g. `i > 0`) would incorrectly forbid valid combinations that legitimately reuse a duplicate value from different recursion depths.

**Common trap:**
Confusing "skip duplicate at this level" with "never reuse this value again."

**Related:**
[syllabus/03-data-structures-algorithms/backtracking-and-pruning.md](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)

## Card: N-Queens' O(1) conflict check

**Prompt:**
How does N-Queens turn an O(n) conflict check into O(1) per candidate move?

**Answer:**
Every cell on the same "/" diagonal shares `row + col`; every cell on the same "\" diagonal shares `row - col` (offset to stay non-negative). Precomputing boolean arrays for used columns, diagonals, and anti-diagonals converts "is this move safe" into an O(1) array lookup instead of a rescan of every placed queen.

**Why it matters:**
It's the concrete, measurable difference between a solution that finishes instantly at n=8 and one that visibly stalls, since this check runs in backtracking's innermost, hottest loop.

**Common trap:**
Believing this optimization changes the fundamental O(n!) search-space size — it only reduces the constant factor per check, not the exponential nature of the problem.

**Related:**
[syllabus/03-data-structures-algorithms/backtracking-and-pruning.md](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)

## Card: Backtracking's inherent exponential complexity

**Prompt:**
Why is exponential complexity (O(2^n), O(n!), O(k^n)) often not a sign a backtracking algorithm is wrong?

**Answer:**
Many backtracking problems (generating all permutations, N-Queens) are genuinely combinatorial — there's no way around exploring an exponential number of possibilities for the problem as stated.

**Why it matters:**
Recognizing this distinguishes "the algorithm needs optimizing" from "the problem is inherently this expensive," which changes what kind of improvement (pruning the constant factor vs. a fundamentally different approach) is even possible.

**Common trap:**
Treating an optimization like N-Queens' O(1) conflict check as something that should reduce the exponential complexity class itself.

**Related:**
[syllabus/03-data-structures-algorithms/backtracking-and-pruning.md](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)

## Card: Word Search's in-place marking as the undo step

**Prompt:**
How does Word Search apply the choose/recurse/undo template to a 2D grid?

**Answer:**
It temporarily overwrites a visited cell (e.g. with `'#'`) before recursing, then restores the original character on the way back out — the in-place marking is the "choose," recursion into neighbors is the "recurse," and restoring the character is the "undo."

**Why it matters:**
Without the restore, a failed branch would permanently block a different, valid path through the same cell.

**Common trap:**
Marking a cell as visited but forgetting to restore it on every exit path (including early returns), which corrupts sibling branches.

**Related:**
[syllabus/03-data-structures-algorithms/backtracking-and-pruning.md](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)
