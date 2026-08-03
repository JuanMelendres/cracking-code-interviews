---
title: "Week 20 — Coding Sprint: Closing the Volume Gap"
document_type: study-pack
week: 20
status: draft
estimated_hours: 10
---

# Week 20 — Coding Sprint: Closing the Volume Gap

## Weekly Outcome

By the end of this week you have 24 new, real, compiled-and-executed LeetCode solutions across five algorithm patterns, closing this program's largest remaining single quantitative gap: 60 solved problems against its own stated 150–170 target, per `00-project/coverage-audit-2026-07-31.md`.

## Why This Week Matters

With Security, Testing, and JVM now fully closed to 100% register coverage (Weeks 17–19), the coding-problem volume shortfall became the single largest remaining numeric gap in the program. A follow-up per-pattern audit (not previously run) found the shortfall wasn't evenly spread: two patterns had literally zero problems (bit manipulation) or near-zero (linked lists, greedy, intervals all at just one problem each, 10–12% of their register target), while Graphs — the register's single highest-weight D14 topic (⭐, IWI 6.25, Very High frequency) — sat at only 27% coverage (6 of 22) despite already having more raw problems than any pattern except DP. This week targets exactly those five gaps rather than spreading effort evenly, following the same "biggest, most-real gap first" logic used for the domain closures in Weeks 17–19.

## Prerequisites

None formally required. `04-coding-interview-communication.md` (Week 1) for the six-phase narration method, applicable to every problem here.

## Schedule

No day-by-day checklist this week — this is a single, bounded sprint, not a themed learning week. Work through the five pattern files in any order; each is fully self-contained.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-linked-lists-coding-practice.md` | 5 problems (LC 21, 141, 19, 143, 138) — pattern coverage 1/10 → 6/10 |
| 3 | `02-greedy-coding-practice.md` | 5 problems (LC 45, 134, 621, 763, 402) — pattern coverage 1/10 → 6/10 |
| 4 | `03-intervals-coding-practice.md` | 4 problems (LC 57, 253, 452, 986) — pattern coverage 1/8 → 5/8 |
| 5 | `04-bit-manipulation-coding-practice.md` | 5 problems (LC 136, 191, 268, 371, 338) — pattern coverage 0/6 → 5/6 |
| 6 | `05-graphs-advanced-coding-practice.md` | 5 problems (LC 743, 684, 1584, 994, 787) — pattern coverage 6/22 → 11/22 |

## Hands-On Exercises

Every problem's code block in this week's five files is real, compiled, and executed — reproduce any of them directly via the verification command at the end of each file.

## Interview Answer Drills

For each problem, narrate all six phases from `study-packs/week-01/04-coding-interview-communication.md` aloud before checking the retrospective note — the retrospective states the key insight, but arriving at it yourself, narrated, is the actual practice.

## Coding Problems

This entire week *is* the coding-problems deliverable — 24 new problems, real Java, real execution, real complexity analysis per problem.

## System Design Exercise

None this week — this is a coding-volume sprint, not a system-design week.

## Behavioral Exercise

None this week.

## Mock Interview

None dedicated this week; use any of these 24 problems as live-coding material in a future mock, per `study-packs/week-18/02-writing-tests-live-in-an-interview.md`'s technique (though that chapter is about test-first discipline specifically — these problems are solution-first, matching how most coding rounds are actually run).

## Review Checklist

- [ ] All 24 problems reproduced with matching real output
- [ ] Each problem's complexity (time and space) stated correctly, unprompted
- [ ] For LC 787 specifically: can explain why plain Dijkstra is the wrong tool, not just that Bellman-Ford-style relaxation is the right one
- [ ] For LC 1584 specifically: can explain why Prim's (not Kruskal's) is the better choice for this dense, implicit graph

## Completion Criteria

- [ ] All 24 problems compiled and passing (48/48 real assertions across all five files)
- [ ] Can state, for each of the five patterns, the specific recognition signal that identifies it

## Retrospective

Note which of the five patterns needs a second pass, and whether the Graphs batch (the most conceptually dense — Dijkstra, Union-Find, MST, multi-source BFS, and a Dijkstra-trap problem all in one file) needs to be split into two review sessions.

## Next Week

Coding-problem coverage moves from 60 to 84 solved problems (60 + 24), closing roughly a quarter of the remaining gap to the 150–170 target. Per `00-project/coverage-audit-2026-07-31.md`'s own next-actions list, remaining priorities are: continuing the coding-problem volume closure (arrays/two-pointers, hashing, binary search, stacks, heaps, trees, backtracking, and DP are all still below 50% of their register targets — see this week's audit sub-agent findings for exact counts), or starting Phase 6 complementary deliverables (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook — still not started; behavioral stories and mock-interview content remain the cheapest wins there, since they already exist and just need consolidating).
