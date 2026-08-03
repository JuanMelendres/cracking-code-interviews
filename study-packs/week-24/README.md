---
title: "Week 24 — Coding Sprint: Volume Gap, Phase 5 (Closing Batch)"
document_type: study-pack
week: 24
status: draft
estimated_hours: 12
---

# Week 24 — Coding Sprint: Volume Gap, Phase 5 (Closing Batch)

## Weekly Outcome

By the end of this week you have 12 new, real, compiled-and-executed LeetCode solutions across two algorithm patterns, bringing this program's coding-problem count to **157 solved problems — inside the 150–170 target range** from `00-project/coverage-audit-2026-07-31.md` for the first time since this five-week sprint began.

## Why This Week Matters

Weeks 20–23 closed the worst zero/near-zero patterns, the next-thinnest patterns by ratio, and a broad sweep across eight more patterns — bringing the program to 145/150–170. Only two patterns remained meaningfully open: Dynamic Programming's remainder (21/32, 66%, 11 problems still needed — DP has the largest raw-count target in the entire register) and Graphs (11/22, 50%, 11 problems still needed). This week closes both partially — 6 problems each — landing the program at 157, inside its target range with room to spare. This is very likely the final bounded batch in this series; what remains afterward (DP's last 5 problems, Graphs' last 5, and the deliberately-deprioritized Expert-tier T-1418) is genuinely optional polish rather than a real coverage gap.

## Prerequisites

None formally required. `study-packs/week-01/04-coding-interview-communication.md` for the six-phase narration method. This week's DP file assumes comfort with the interval-DP and 2D-string-matching techniques from `study-packs/week-21/04-dynamic-programming-coding-practice.md` and `study-packs/week-23/01-dp-coding-practice.md` — it builds directly on those, it doesn't re-teach them.

## Schedule

No day-by-day checklist — a single bounded sprint, same format as Weeks 20–23. Work through the two pattern files in either order.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-dp-coding-practice.md` | 6 problems (LC 329, 312, 10, 44, 96, 32) — pattern coverage 21/32 → 27/32 |
| 3 | `02-graphs-coding-practice.md` | 6 problems (LC 785, 332, 1319, 399, 802, 1466) — pattern coverage 11/22 → 17/22 |

## Hands-On Exercises

Every problem's code block is real, compiled, and executed — reproduce any of them via the verification command at the end of each file. This week's DP file is the hardest single batch in the entire coding-volume series (two Hard-difficulty 2D string-matching problems, a classic Hard interval-DP problem) — budget extra review time accordingly.

## Interview Answer Drills

For each problem, narrate all six phases from `study-packs/week-01/04-coding-interview-communication.md` aloud before checking the retrospective note.

## Coding Problems

This entire week *is* the coding-problems deliverable — 12 new problems, real Java, real execution, real complexity analysis per problem.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

None dedicated this week; use these 12 problems as live-coding material in a future mock. LC 10 vs. LC 44 (regex vs. wildcard matching) make a strong paired live-coding exercise specifically because they look similar but require genuinely different recurrences — a good test of whether a candidate is pattern-matching superficially or reasoning from the actual semantics.

## Review Checklist

- [ ] All 12 problems reproduced with matching real output
- [ ] Each problem's time and space complexity stated correctly, unprompted
- [ ] For LC 10 vs. LC 44 specifically: can explain the exact recurrence difference (why LC 10 looks back two positions and LC 44 only one) unprompted
- [ ] For LC 312 specifically: can explain why "which balloon bursts last" is the right decision variable, not "which bursts first"
- [ ] For LC 332 specifically: can explain why a plain greedy DFS can get stuck, and what Hierholzer's algorithm does differently
- [ ] For LC 1319 vs. LC 684 (Week 20) specifically: can explain how the same Union-Find structure answers two different questions ("how many components" vs. "which edge is redundant")

## Completion Criteria

- [ ] All 12 problems compiled and passing (34/34 real assertions across both files)
- [ ] Can state, for each of the two patterns, the specific recognition signal that identifies each new sub-pattern introduced this week

## Retrospective

Note which problems need a second pass. LC 10, LC 44, and LC 312 are the three hardest problems in the entire five-week coding-volume series — worth flagging honestly if any of them didn't click on the first pass, since re-deriving the recurrence from scratch (not just re-reading the retrospective) is the actual test of whether the pattern is retained.

## Next Week

Coding-problem coverage moves from 145 to 157 (145 + 12), inside the 150–170 target range for the first time. What remains — DP's last 5 problems (27/32), Graphs' last 5 (17/22), and Expert-tier Advanced Structures (0/8, deliberately deprioritized throughout this series per the blueprint's own tier-priority guidance) — is optional refinement, not a coverage gap requiring urgent attention. The clear next priority is Phase 6: complementary deliverables (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook) remain completely untouched after five weeks of coding-volume focus, and behavioral stories (13 built) plus mock-interview content (21 artifacts) have been repeatedly flagged across this entire series as the cheapest available win, since they already exist and only need consolidating into their canonical homes per `CLAUDE.md`'s ownership model.
