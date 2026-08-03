---
title: "Week 23 — Coding Sprint: Volume Gap, Phase 4"
document_type: study-pack
week: 23
status: draft
estimated_hours: 11
---

# Week 23 — Coding Sprint: Volume Gap, Phase 4

## Weekly Outcome

By the end of this week you have 20 new, real, compiled-and-executed LeetCode solutions across four algorithm patterns, continuing this program's largest remaining quantitative gap: 125 solved problems (after Week 22) against the 150–170 target from `00-project/coverage-audit-2026-07-31.md`.

## Why This Week Matters

Weeks 20–22 closed the worst zero/near-zero patterns and the next-thinnest patterns by ratio. A follow-up audit found Dynamic Programming's *remainder* (16/32, 50%) is the largest absolute remaining gap in the entire register — 16 problems still needed even after Week 21's batch — while Arrays/Two-pointers (7/18, 39%), Heaps (5/12, 42%), and Trees (7/16, 44%) round out the next tier. This week targets all four, continuing the same audit-first, no-duplication methodology as prior weeks. The audit specifically verified several cross-pattern double-count risks (LC 347, LC 15, LC 42, LC 621 are all classic problems for *this* week's four patterns but are already solved and correctly tagged under Heaps/Two-pointers/Greedy respectively) — documented in each pattern file's opening note.

## Prerequisites

None formally required. `study-packs/week-01/04-coding-interview-communication.md` for the six-phase narration method.

## Schedule

No day-by-day checklist — a single bounded sprint, same format as Weeks 20–22. Work through the four pattern files in any order.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-dp-coding-practice.md` | 5 problems (LC 91, 63, 337, 188, 132) — pattern coverage 16/32 → 21/32 |
| 3 | `02-arrays-two-pointers-coding-practice.md` | 5 problems (LC 11, 239, 238, 189, 31) — pattern coverage 7/18 → 12/18 |
| 4 | `03-heaps-coding-practice.md` | 5 problems (LC 1046, 692, 373, 767, 1642) — pattern coverage 5/12 → 10/12 |
| 5 | `04-trees-coding-practice.md` | 5 problems (LC 543, 297, 230, 105, 112) — pattern coverage 7/16 → 12/16 |

## Hands-On Exercises

Every problem's code block is real, compiled, and executed — reproduce any of them via the verification command at the end of each file.

## Interview Answer Drills

For each problem, narrate all six phases from `study-packs/week-01/04-coding-interview-communication.md` aloud before checking the retrospective note.

## Coding Problems

This entire week *is* the coding-problems deliverable — 20 new problems, real Java, real execution, real complexity analysis per problem.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

None dedicated this week; use these 20 problems as live-coding material in a future mock. LC 337 (the first tree-DP problem in the register) and LC 105 (tree reconstruction from traversals) both make strong whiteboard-explanation material given their two-state/two-array-coordination structure.

## Review Checklist

- [ ] All 20 problems reproduced with matching real output
- [ ] Each problem's time and space complexity stated correctly, unprompted
- [ ] For LC 337 specifically: can explain the two-state (`robbed`/`notRobbed`) return-tuple pattern and how it generalizes LC 198/213's linear/circular recurrences to a tree
- [ ] For LC 188 specifically: can explain why the `k >= n/2` early-exit is a real optimization, not just a shortcut
- [ ] For LC 373 specifically: can explain why seeding the heap with `(i, 0)` for every `i` (not just `(0,0)`) is necessary

## Completion Criteria

- [ ] All 20 problems compiled and passing (54/54 real assertions across all four files)
- [ ] Can state, for each of the four patterns, the specific recognition signal that identifies it

## Retrospective

Note which pattern needs a second pass. The DP file's LC 337 (tree DP) and LC 132 (two-stage interval DP) are the most conceptually dense — worth reviewing the tuple-return and precompute-then-DP techniques separately from the more mechanical LC 91/63/188.

## Next Week

Coding-problem coverage moves from 125 to 145 (125 + 20), within range of the low end of the 150–170 target. Remaining patterns still below 50% of their register target after this week: T-1401 complexity analysis (cross-cutting, no dedicated problem type), T-1409 graphs (11/22, 50%), T-1411 DP remainder (21/32, 66% — now above 50% for the first time), T-1418 advanced structures (0/8, Expert tier, deliberately deprioritized). At 145/150–170, the program is close enough to the low end of its target range that the next session should weigh finishing the last ~5-25 problems (a short Phase 5) against finally starting Phase 6 complementary-deliverables consolidation (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook — still untouched, still flagged as the cheapest available win given existing behavioral stories and mock-interview content).
