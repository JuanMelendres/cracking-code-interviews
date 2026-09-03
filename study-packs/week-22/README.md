---
title: "Week 22 — Coding Sprint: Volume Gap, Phase 3"
document_type: study-pack
week: 22
status: draft
estimated_hours: 11
---

# Week 22 — Coding Sprint: Volume Gap, Phase 3

## Weekly Outcome

By the end of this week you have 19 new, real, compiled-and-executed LeetCode solutions across four algorithm patterns, continuing this program's largest remaining quantitative gap: 106 solved problems (after Week 21) against the 150–170 target from `00-project/coverage-audit-2026-07-31.md`.

## Why This Week Matters

Weeks 20 and 21 closed the worst zero/near-zero patterns and the next-thinnest four. A follow-up audit of the remaining patterns found: Hashing and Binary Search tied at 4/12 (33% each), Concurrency coding at 3/8 (38%), and Design-style at 4/10 (40%). This week targets exactly those four, continuing the same audit-first, no-duplication methodology as Weeks 20–21 — a dedicated research pass confirmed exact existing coverage and resolved a genuine ambiguity (rate limiter and bounded-queue problems could plausibly belong to either Concurrency or Design; each was assigned to exactly one pattern based on whether the canonical LeetCode problem actually requires thread coordination, documented in each pattern file's opening note).

## Prerequisites

None formally required. `study-packs/week-01/04-coding-interview-communication.md` for the six-phase narration method. The Concurrency-coding file assumes familiarity with `syllabus/02-java/concurrency/java-memory-model-and-volatile.md` and `deadlock-race-conditions-and-thread-diagnostics.md` — this week's problems apply those concepts, they don't re-teach them.

## Schedule

No day-by-day checklist — a single bounded sprint, same format as Weeks 20–21. Work through the four pattern files in any order.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-hashing-coding-practice.md` | 5 problems (LC 217, 560, 349, 202, 454) — pattern coverage 4/12 → 9/12 |
| 3 | `02-binary-search-coding-practice.md` | 5 problems (LC 34, 74, 153, 1011, 4) — pattern coverage 4/12 → 9/12 |
| 4 | `03-concurrency-coding-practice.md` | 4 problems (LC 1117, 1195, 1226, 1188) — pattern coverage 3/8 → 7/8 |
| 5 | `04-design-coding-practice.md` | 5 problems (LC 460, 981, 355, 1472, 359) — pattern coverage 4/10 → 9/10 |

## Hands-On Exercises

Every problem's code block is real, compiled, and executed — reproduce any of them via the verification command at the end of each file. The concurrency file's suite was additionally re-run 5 times in a row to check for scheduling-dependent flakiness (all 5 runs: 10/10 pass) — a discipline worth applying to any concurrency code you write in a live interview, not just this batch.

## Interview Answer Drills

For each problem, narrate all six phases from `study-packs/week-01/04-coding-interview-communication.md` aloud before checking the retrospective note.

## Coding Problems

This entire week *is* the coding-problems deliverable — 19 new problems, real Java, real execution, real complexity analysis per problem.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

None dedicated this week; use these 19 problems as live-coding material in a future mock. The Dining Philosophers and Bounded Blocking Queue problems specifically make good live-coding material for a "design + implement a small concurrent primitive" round, distinct from a pure-algorithm coding round.

## Review Checklist

- [ ] All 19 problems reproduced with matching real output (run the concurrency suite at least 3 times to personally observe its stability, not just once)
- [ ] Each problem's time and space complexity stated correctly, unprompted
- [ ] For LC 460 specifically: can explain why `minFreq` only ever needs incrementing, never decrementing (except on new-key insertion)
- [ ] For LC 1226 specifically: can explain which Coffman condition (circular wait) the asymmetric lock order breaks, and name at least one alternative fix
- [ ] For LC 359 vs LC 1188 specifically: can explain why one needed real thread coordination and the other didn't, unprompted

## Completion Criteria

- [ ] All 19 problems compiled and passing (56/56 real assertions across all four files)
- [ ] Can state, for each of the four patterns, the specific recognition signal that identifies it

## Retrospective

Note which pattern needs a second pass. The Concurrency file requires the most careful review — correctness in concurrent code can't be fully verified by a single passing run, so re-running the suite yourself (not just trusting the transcript) is part of the actual exercise, not optional.

## Next Week

Coding-problem coverage moves from 106 to 125 (106 + 19), roughly three-quarters of the way to the low end of the 150–170 target. Remaining patterns still below 50% of their register target after this week: T-1401 complexity analysis (cross-cutting, no dedicated problem type), T-1402 arrays/two-pointers/sliding-window (7/18, 39%), T-1407 heaps/top-k (5/12, 42%), T-1408 trees/BST (7/16, 44%), T-1409 graphs (11/22, 50%), T-1411 DP remainder (16/32, 50%), T-1418 advanced structures (0/8, Expert tier, deliberately deprioritized). DP remains the largest absolute remaining gap (16 problems still needed) and is a strong Phase 4 candidate, alongside arrays/two-pointers (the largest gap among patterns still under 40%). Alternatively, Phase 6 complementary-deliverables consolidation (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook) remains untouched and was flagged again as the cheapest available win.
