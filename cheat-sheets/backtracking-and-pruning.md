---
title: "Cheat Sheet: Backtracking and Pruning"
slug: backtracking-and-pruning
document_type: cheat-sheet
domain: 03-data-structures-algorithms
topic_id: T-2109
canonical: ../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md
last_updated: 2026-09-06
---

# Backtracking and Pruning

**Canonical chapter:** [`syllabus/03-data-structures-algorithms/backtracking-and-pruning.md`](../syllabus/03-data-structures-algorithms/backtracking-and-pruning.md)

## Core Mental Model

Backtracking explores a tree of choices: at each step, try one option, recurse into the consequences of that choice, then undo it and try the next option — the "undo" step is what makes it backtracking rather than plain recursion or brute force. It is DFS applied to a tree of *choices* rather than a tree of *nodes*. Most genuinely combinatorial problems (N-Queens, all permutations) are exponential by nature — there's no way around exploring an exponential number of possibilities.

## Essential Definitions

- **Choose/recurse/undo template** — pick a choice, add it to the partial solution, recurse one level deeper, then remove it before the loop tries the next choice at that same level.
- **Duplicate-input pruning** — sort the input first, then explicitly skip a duplicate value *at the same recursion depth* as an already-tried identical value.
- **Precomputed conflict state** — auxiliary boolean arrays (N-Queens' columns/diagonals) turn an O(n) re-scan of already-placed pieces into an O(1) conflict check per candidate move.

## Recognition Signals / When to Use This Pattern

| Signal in the problem | Technique | Complexity |
|---|---|---|
| Generate all combinations/permutations/arrangements from a fixed set of choices | Plain choose/recurse/undo template | O(k^n) / O(n!) worst case |
| Input array can contain duplicate values, output must not repeat | Sort first, skip duplicate siblings at the same recursion depth | Same shape, pruned |
| 2D grid path search reusing cells within one candidate path only | In-place marking (overwrite cell, restore on backtrack) | O(rows·cols·4^L) |
| Constraint-satisfaction with a per-candidate conflict check inside a hot recursive loop | Precomputed boolean/bitmask conflict state for O(1) checks | Same search-space size, smaller constant factor |

## Common Mistakes

- Forgetting the "undo" step, or undoing the wrong piece of state — leaves a stale, incorrect partial solution polluting sibling branches at the same recursion level.
- Skipping duplicates with the wrong condition — Permutations II's exact skip condition is `if (i > 0 && nums[i] == nums[i-1] && !used[i-1]) continue;`; checking `used[i-1]` instead of `!used[i-1]` either misses valid permutations or fails to deduplicate at all.
- Confusing "skip duplicate at this level" with "never reuse this value again" — Combination Sum II's `i > start` check permits the same value at *different* recursion depths (e.g. `[1,1,6]`) while forbidding it as an immediate sibling.
- Leaving an O(n) conflict check inside a backtracking loop's hot path when precomputed state could make it O(1) — not incorrect, but the difference between a solution that finishes instantly and one that visibly stalls.

## Complexity Reference

- Letter Combinations of a Phone Number: O(4^n · n).
- Word Search: O(rows·cols·4^L), L = word length.
- Permutations II: O(n · n!) worst case, less with duplicates.
- Combination Sum II: O(2^n) worst case, pruned significantly.
- N-Queens: O(n!) worst case (inherent to the problem), O(n) space; `n=8` yields exactly 92 solutions, a well-known, independently verifiable result.

## Interview Answer Skeleton

**30-sec:** Backtracking explores a tree of choices via choose/recurse/undo, used for exhaustive-search problems whose cost is often inherently exponential; the interview skill is writing the template correctly, pruning legitimate dead branches, and handling duplicate inputs without over- or under-generating results.

**2-min:** Write the bare choose/recurse/undo skeleton first, correctly, before adding any constraints. When the input can contain duplicate values, sort it first and skip a duplicate sibling at the *same* recursion depth — the precise condition differs by whether array *indices* can repeat (Combination Sum II: no, recurse on `i+1`) or the same *value* can appear later at a different position (Permutations II: yes, guarded by `!used[i-1]`). When a candidate-validity check inside the innermost loop costs O(n) (N-Queens' naive board re-scan), consider precomputed boolean state to make it O(1) instead.

**Whiteboard:** Draw the recursion as a tree: each node is a partial solution, each edge is one choice. Walk down one path (choose), hit a base case or dead end, then walk back up one level (undo) and take the next sibling edge. For N-Queens, annotate the diagonals with `row - col` and `row + col` to show why every cell on the same diagonal shares one of those two values — the basis for the O(1) conflict check.

**Staff-level framing:** Backtracking's exhaustive-search-with-pruning shape transfers directly to real constraint-satisfaction systems — a scheduler assigning resources to time slots subject to conflict constraints, or a configuration system searching for a valid combination of feature flags subject to compatibility constraints — and the same O(1)-conflict-check-via-precomputed-state optimization that makes N-Queens tractable is often the difference between a system that completes in reasonable time and one that stalls at production scale.

## Production Warning Signs

- **Symptom:** a "generate all unique combinations summing to a target" backtracking implementation, given an input with duplicate values, either produces duplicate combinations or is missing valid combinations that should include a repeated value.
- **Diagnose:** check the duplicate-skip condition — it should trigger only when `i > start` (a sibling at the *same* recursion level), not for every occurrence of a duplicate value regardless of depth. A too-broad skip (`i > 0`) incorrectly forbids valid combinations like `[1,1,6]`; a missing or too-narrow skip fails to deduplicate identical-looking combinations from the same level.

## Related

- syllabus/03-data-structures-algorithms/graphs-bfs-dfs-and-shortest-paths.md
