---
title: "Week 21 — Coding Sprint: Volume Gap, Phase 2"
document_type: study-pack
week: 21
status: draft
estimated_hours: 11
---

# Week 21 — Coding Sprint: Volume Gap, Phase 2

## Weekly Outcome

By the end of this week you have 22 new, real, compiled-and-executed LeetCode solutions across four algorithm patterns, continuing this program's largest remaining quantitative gap: 84 solved problems (after Week 20) against the 150–170 target from `00-project/coverage-audit-2026-07-31.md`.

## Why This Week Matters

Week 20 closed the worst zero/near-zero patterns (bit manipulation, linked lists, greedy, intervals, graphs). A follow-up audit of the next-thinnest patterns found: Tries at 1/6 (17%, only LC 208), Dynamic Programming at 9/32 (28% — the single largest raw-count pattern in the entire register, yet its lowest ratio), Backtracking at 4/14 (29%), and Stacks/monotonic-stack at 3/10 (30%). This week targets exactly those four, using the same audit-first, no-duplication methodology as Week 20 — a dedicated research pass confirmed the exact existing LC numbers per pattern before writing anything, so no problem here duplicates one already solved elsewhere in the repo (see each pattern file's opening note for the specific cross-references, e.g. LC 402 and LC 42 are monotonic-stack-shaped but already solved and correctly categorized as Greedy and Two-pointers respectively).

## Prerequisites

None formally required. `study-packs/week-01/04-coding-interview-communication.md` for the six-phase narration method.

## Schedule

No day-by-day checklist — a single bounded sprint, same format as Week 20. Work through the four pattern files in any order.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-tries-coding-practice.md` | 5 problems (LC 211, 212, 421, 648, 677) — pattern coverage 1/6 → 6/6, full closure |
| 3 | `02-backtracking-coding-practice.md` | 5 problems (LC 17, 79, 47, 40, 51) — pattern coverage 4/14 → 9/14 |
| 4 | `03-stacks-coding-practice.md` | 5 problems (LC 496, 84, 150, 232, 503) — pattern coverage 3/10 → 8/10 |
| 5 | `04-dynamic-programming-coding-practice.md` | 7 problems (LC 72, 213, 518, 494, 64, 516, 309) — pattern coverage 9/32 → 16/32 |

## Hands-On Exercises

Every problem's code block is real, compiled, and executed — reproduce any of them via the verification command at the end of each file.

## Interview Answer Drills

For each problem, narrate all six phases from `study-packs/week-01/04-coding-interview-communication.md` aloud before checking the retrospective note.

## Coding Problems

This entire week *is* the coding-problems deliverable — 22 new problems, real Java, real execution, real complexity analysis per problem.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

None dedicated this week; use these 22 problems as live-coding material in a future mock.

## Review Checklist

- [ ] All 22 problems reproduced with matching real output
- [ ] Each problem's time and space complexity stated correctly, unprompted
- [ ] For LC 494 vs LC 518 specifically: can explain the loop-direction difference (0/1 vs unbounded knapsack) unprompted
- [ ] For LC 40 vs LC 47 specifically: can explain both the "same-depth sibling skip" rule and why LC 40 recurses on `i+1` while LC 47 uses a `used[]` array instead
- [ ] For LC 84 specifically: can explain why the sentinel height of 0 is appended at the end

## Completion Criteria

- [ ] All 22 problems compiled and passing (54/54 real assertions across all four files)
- [ ] Can state, for each of the four patterns, the specific recognition signal that identifies it

## Retrospective

Note which pattern needs a second pass. The DP file is the most conceptually dense (edit distance, circular-array reduction, two knapsack-counting variants, matrix path, interval DP, and state-machine DP all in one file) — consider splitting it into two review sessions.

## Next Week

Coding-problem coverage moves from 84 to 106 (84 + 22), a bit under two-thirds of the 150–170 target. Remaining patterns still below 50% of their register target after this week: T-1401 complexity analysis (cross-cutting, no dedicated problem type), T-1402 arrays/two-pointers/sliding-window (7/18, 39%), T-1403 hashing (4/12, 33%), T-1404 binary search (4/12, 33%), T-1407 heaps/top-k (5/12, 42%), T-1408 trees/BST (7/16, 44%), T-1409 graphs (11/22, 50% — right at the boundary), T-1416 design-style (4/10, 40%), T-1417 concurrency coding (3/8, 38%), T-1418 advanced structures (0/8, Expert tier, deliberately deprioritized per Week 20's rationale), T-1419 communication protocol (cross-cutting narration skill). DP itself remains the largest absolute remaining gap even after this week (16/32) and is a strong Phase 3 candidate. Alternatively, Phase 6 complementary-deliverables consolidation (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook) remains untouched and was flagged again as the cheapest available win.
