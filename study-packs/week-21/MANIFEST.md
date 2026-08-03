---
title: "Week 21 Study Pack — Manifest"
week: 21
plan: B
last_reviewed: 2026-08-03
---

# Week 21 Study Pack — Manifest

**Topics:** T-1415 (Tries), T-1410 (Backtracking), T-1406 (Stacks), T-1411 (Dynamic Programming) · **Plan:** B, Coding-Problem Volume Gap — Phase 2 (continues Week 20's Phase 1; per `00-project/coverage-audit-2026-07-31.md` this remains the program's single largest remaining quantitative shortfall: 84 of a 150–170 target going into this week). Bounded batch — the four next-thinnest patterns after Week 20's five, not an attempt to close the full remaining gap in one pass.
**Files:** 5 (+ this manifest) · **Total words:** 5,353 (real count, `wc -w` over the 4 pattern files) + 763 (README, separately) · **New problems:** 22, all real, compiled, executed.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, scope note, exit criteria | 763 |
| 2 | `01-tries-coding-practice.md` | 5 problems (LC 211, 212, 421, 648, 677) | 1,348 |
| 3 | `02-backtracking-coding-practice.md` | 5 problems (LC 17, 79, 47, 40, 51) | 1,153 |
| 4 | `03-stacks-coding-practice.md` | 5 problems (LC 496, 84, 150, 232, 503) | 1,161 |
| 5 | `04-dynamic-programming-coding-practice.md` | 7 problems (LC 72, 213, 518, 494, 64, 516, 309) | 1,691 |

---

## Verification

| Pattern | Problems | Status | Real assertions |
|---|---|---|---|
| Tries (T-1415) | LC 211, 212, 421, 648, 677 | **Executed.** `javac`/`java`, OpenJDK 21.0.12. One test's expected value (LC 648) was initially wrong on first run — the input word itself was echoed instead of the replaced form; corrected in the test before citing as passing. | 14/14 pass |
| Backtracking (T-1410) | LC 17, 79, 47, 40, 51 | **Executed.** All passed on first run. | 12/12 pass |
| Stacks (T-1406) | LC 496, 84, 150, 232, 503 | **Executed.** One hand-computed expected value (LC 503's second test) was initially wrong — verified by hand-tracing the circular scan, corrected in the test, not the implementation, before citing as passing. | 13/13 pass |
| Dynamic Programming (T-1411) | LC 72, 213, 518, 494, 64, 516, 309 | **Executed.** All passed on first run. | 15/15 pass |
| **Total** | **22 problems** | | **54/54 pass** |

Source: `practice/java/week-21/{tries,backtracking,stacks,dp}/`

## Coverage impact (per-pattern, before → after this week)

| Pattern | Before | After | Register target |
|---|---|---|---|
| T-1415 Tries | 1/6 (17%) | 6/6 (100%) | 6 |
| T-1410 Backtracking | 4/14 (29%) | 9/14 (64%) | 14 |
| T-1406 Stacks | 3/10 (30%) | 8/10 (80%) | 10 |
| T-1411 Dynamic Programming | 9/32 (28%) | 16/32 (50%) | 32 |
| **Program-wide total** | **84 / 150–170** | **106 / 150–170** | 150–170 |

## Errata addressed this week

None. This is new problem-volume content, not a correction to existing material.

## Scope note

This week deliberately closed Tries fully (the smallest of the four target patterns) while leaving DP at 50% despite it being the largest remaining absolute gap (16/32 solved, meaning 16 problems still remain) — adding all 23 remaining DP problems in one file would violate `CLAUDE.md`'s explicit instruction against generating an entire deliverable in one operation, and DP's breadth (knapsack variants, interval DP, tree DP, digit DP, bitmask DP, DP-on-graphs) justifies treating it as a dedicated future batch rather than rushing the remainder here.

Remaining gaps after this week (not yet addressed): T-1401 complexity analysis (cross-cutting, no dedicated problem type), T-1402 arrays/two-pointers/sliding-window (7/18, 39%), T-1403 hashing (4/12, 33%), T-1404 binary search (4/12, 33%), T-1407 heaps/top-k (5/12, 42%), T-1408 trees/BST (7/16, 44%), T-1409 graphs (11/22, 50%), T-1411 DP remainder (16/32, 50%), T-1416 design-style (4/10, 40%), T-1417 concurrency coding (3/8, 38%), T-1418 advanced structures (0/8, Expert tier, deliberately deprioritized — see Week 20's rationale), T-1419 communication protocol (cross-cutting narration skill).

## A note on real evidence

Every problem's solution is real, compiled Java, executed with real test assertions via the shared `Check.java` helper (identical to the one used in Week 20, itself matching `practice/java/week-01/src/Check.java`) — no pseudocode, no unverified claims. Two expected test values (noted above, in Tries and Stacks) were caught wrong on the first real run and corrected by hand-verifying the algorithm's actual output against the problem's real constraints — both failures were test-authoring errors (an echoed-input string in LC 648, a mis-traced circular wraparound in LC 503), confirmed by manual trace, not implementation bugs.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, real pass/fail counts from the `Check` assertion helper). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
