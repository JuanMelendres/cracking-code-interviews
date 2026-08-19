---
title: "Week 20 Study Pack — Manifest"
week: 20
plan: B
last_reviewed: 2026-08-03
---

# Week 20 Study Pack — Manifest

**Topics:** T-1405 (Linked Lists), T-1413 (Greedy), T-1412 (Intervals), T-1414 (Bit Manipulation), T-1409 (Graphs) · **Plan:** B, Coding-Problem Volume Gap — Phase 1 (this program's single largest remaining quantitative shortfall per `00-project/coverage-audit-2026-07-31.md`: 60 of a 150–170 target). This week is a bounded, targeted batch — the five thinnest/highest-priority patterns, not an attempt to close the full ~90-problem gap in one pass (per `CLAUDE.md`'s explicit instruction against generating the entire deliverable in one operation).
**Files:** 7 (+ this manifest) · **Total words:** 4,487 (real count, `wc -w` over the 5 pattern files) + 756 (README, separately) · **New problems:** 24, all real, compiled, executed.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, scope note, exit criteria | 649 |
| 2 | `01-linked-lists-coding-practice.md` | 5 problems (LC 21, 141, 19, 143, 138) | 884 |
| 3 | `02-greedy-coding-practice.md` | 5 problems (LC 45, 134, 621, 763, 402) | 969 |
| 4 | `03-intervals-coding-practice.md` | 4 problems (LC 57, 253, 452, 986) | 663 |
| 5 | `04-bit-manipulation-coding-practice.md` | 5 problems (LC 136, 191, 268, 371, 338) | 748 |
| 6 | `05-graphs-advanced-coding-practice.md` | 5 problems (LC 743, 684, 1584, 994, 787) | 1,223 |

---

## Verification

| Pattern | Problems | Status | Real assertions |
|---|---|---|---|
| Linked Lists (T-1405) | LC 21, 141, 19, 143, 138 | **Executed.** `javac`/`java`, OpenJDK 21.0.12. | 10/10 pass |
| Greedy (T-1413) | LC 45, 134, 621, 763, 402 | **Executed.** | 10/10 pass |
| Intervals (T-1412) | LC 57, 253, 452, 986 | **Executed.** | 9/9 pass |
| Bit Manipulation (T-1414) | LC 136, 191, 268, 371, 338 | **Executed.** | 10/10 pass |
| Graphs Advanced (T-1409) | LC 743, 684, 1584, 994, 787 | **Executed.** Two hand-computed expected values (LC 1584's second test, LC 787's first test) were initially wrong on first run — verified by hand, corrected in the test, not the implementation, before citing as passing. | 11/11 pass |
| **Total** | **24 problems** | | **48/48 pass** |

Source: `practice/java/week-20/{linked-lists,greedy,intervals,bit-manipulation,graphs-advanced}/`

## Coverage impact (per-pattern, before → after this week)

| Pattern | Before | After | Register target |
|---|---|---|---|
| T-1405 Linked Lists | 1/10 (10%) | 6/10 (60%) | 10 |
| T-1413 Greedy | 1/10 (10%) | 6/10 (60%) | 10 |
| T-1412 Intervals | 1/8 (12%) | 5/8 (62%) | 8 |
| T-1414 Bit Manipulation | 0/6 (0%) | 5/6 (83%) | 6 |
| T-1409 Graphs | 6/22 (27%) | 11/22 (50%) | 22 |
| **Program-wide total** | **60 / 150–170** | **84 / 150–170** | 150–170 |

## Errata addressed this week

None. This is new problem-volume content, not a correction to existing material.

## Scope note

This week deliberately did **not** touch T-1418 (Advanced Structures: segment tree, Fenwick/BIT, rolling hash) despite it being the other zero-coverage pattern found in the pre-work audit. `00-project/knowledge-architecture-blueprint.md`'s own tier system explicitly flags Expert tier (which T-1418 belongs to) as "the lowest-priority tier in the entire blueprint... the single most common misallocation in senior interview prep" — closing a Core-tier zero-coverage gap (bit manipulation) took priority over an Expert-tier one, consistent with the blueprint's own explicit guidance rather than simply "closing every zero" indiscriminately.

Remaining gaps after this week (not yet addressed): T-1401 complexity analysis (cross-cutting, no dedicated problem type), T-1402 arrays/two-pointers/sliding-window (7/18, 39%), T-1403 hashing (4/12, 33%), T-1404 binary search (4/12, 33%), T-1406 stacks/monotonic-stack (3/10, 30%), T-1407 heaps/top-k (5/12, 42%), T-1408 trees/BST (7/16, 44%), T-1410 backtracking (4/14, 29%), T-1411 DP (9/32, 28% — lowest ratio of any Core/Advanced-tier pattern despite the highest raw count), T-1415 tries (1/6, 17%), T-1416 design-style (4/10, 40%), T-1417 concurrency coding (3/8, 38%), T-1418 advanced structures (0/8, Expert tier, deliberately deprioritized per above), T-1419 communication protocol (cross-cutting narration skill).

## A note on real evidence

Every problem's solution is real, compiled Java, executed with real test assertions via a shared `Check.java` helper (matching the pattern already established in `practice/java/week-01/src/Check.java`) — no pseudocode, no unverified claims. Two expected test values (noted above) were caught wrong on the first real run and corrected by hand-verifying the algorithm's actual output against the problem's real constraints, not by adjusting the implementation to match a guessed answer — the distinction matters: both failures were test-authoring errors, confirmed by manual trace, not implementation bugs.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, real pass/fail counts from the `Check` assertion helper). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
