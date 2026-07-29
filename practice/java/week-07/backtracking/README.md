# Week 7 Java — Backtracking — runnable verification

Compiled and run on OpenJDK 21.0.12. Same hand-rolled `Check` harness as prior weeks.

## Reproduce

```bash
cd practice/java/week-07/backtracking
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

## Files

| File | Corresponds to |
|---|---|
| `PermuteBuggy.java` | ⛔ The exact audited defect — `temp.contains()`, value-based, breaks on duplicate-value input |
| `BacktrackingProblems.java` | LC 46 (fixed), LC 78, LC 39, LC 22 |
| `Main.java` | Runs all 12 assertions, including the errata reproduction |

## Real output (last run) — the errata drill

```
== Errata drill: LC 46 with a DUPLICATE-VALUE input [1,1,2] ==
Buggy (value-based temp.contains()):  0 permutations found: []
Fixed (index-based used[] array):     6 permutations found
  PASS  fixed permute produces all 3! = 6 position-based permutations
  PASS  buggy permute UNDER-COUNTS on duplicate-value input (this IS the bug, reproduced on purpose)
```

**The buggy version doesn't just under-count — it finds zero permutations at all** for any input containing a duplicate value, because a value-based "used" check can never place a second occurrence of a value already in the temp list, regardless of which array index it came from. This is a stronger failure than the audit's own description implied, and worth stating precisely rather than just "it under-counts."

## Full output

```
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
