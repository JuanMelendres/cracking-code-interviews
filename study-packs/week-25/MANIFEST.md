---
title: "Week 25 Study Pack — Manifest"
week: 25
plan: B
last_reviewed: 2026-08-03
---

# Week 25 Study Pack — Manifest

**Topics:** T-1411 (Dynamic Programming, full closure), T-1409 (Graphs, full closure) · **Plan:** B, Coding-Problem Volume Gap — Phase 5 continuation / final polish batch, closing the sixth and final week of the Weeks 20–25 coding-volume arc. The program was already inside its 150–170 target range after Week 24 (157 solved); this batch was explicitly optional (per Week 24's own manifest) rather than a required gap-closure — chosen by the user to fully close both remaining open patterns rather than stop early.
**Files:** 2 (+ this manifest) · **Total words:** 3,081 (real count, `wc -w` over the 2 pattern files) + 827 (README, separately) · **New problems:** 10, all real, compiled, executed.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, scope note, series-closure summary | 827 |
| 2 | `01-dp-coding-practice.md` | 5 problems (LC 152, 279, 174, 673, 1220) | 1,541 |
| 3 | `02-graphs-coding-practice.md` | 5 problems (LC 130, 417, 863, 1129, 815) | 1,540 |

---

## Verification

| Pattern | Problems | Status | Real assertions |
|---|---|---|---|
| Dynamic Programming (T-1411) | LC 152, 279, 174, 673, 1220 | **Executed.** `javac`/`java`, OpenJDK 21.0.12. All passed on first run. | 13/13 pass |
| Graphs (T-1409) | LC 130, 417, 863, 1129, 815 | **Executed.** One hand-computed expected value (LC 417's first assertion) was initially wrong — the test claimed 9 matching cells from memory, but the real well-known LeetCode example answer is 7; corrected the test (both the count and, more rigorously, to assert the exact 7-cell set) after re-deriving from the known example, not the implementation, before citing as passing. | 10/10 pass |
| **Total** | **10 problems** | | **23/23 pass** |

Source: `practice/java/week-25/{dp,graphs}/`

## Coverage impact (per-pattern, before → after this week)

| Pattern | Before | After | Register target |
|---|---|---|---|
| T-1411 Dynamic Programming | 27/32 (84%) | **32/32 (100%)** | 32 |
| T-1409 Graphs | 17/22 (77%) | **22/22 (100%)** | 22 |
| **Program-wide total** | **157 / 150–170** | **167 / 150–170** | 150–170 |

**D14 (Coding Interview Practice) is now fully closed** across every pattern except the deliberately-deprioritized Expert-tier T-1418 (Advanced Structures, 0/8) — three patterns at exactly 100% (Tries/T-1415 since Week 21, Dynamic Programming and Graphs as of this week), and every other D14 pattern at 75%+ per Weeks 20–24's cumulative work.

## Errata addressed this week

None new to this batch's problems, beyond the LC 417 test-authoring correction noted above (an incorrect from-memory recollection of the known answer, corrected by re-deriving the standard example's exact cell set).

## Scope note

This is the closing batch of the six-week (Weeks 20–25) coding-problem volume series, which began at 60/150–170 (per `00-project/coverage-audit-2026-07-31.md`) and ends at 167/150–170. What remains untouched is exactly one pattern, T-1418 (Advanced Structures: segment tree, Fenwick/BIT, rolling hash, 0/8) — deliberately deprioritized in every batch of this series per the blueprint's own explicit tier-priority guidance ("Expert tier is the lowest-priority tier in the entire blueprint... the single most common misallocation in senior interview prep"). No further bounded coding-volume batches are anticipated; Phase 6 complementary-deliverables consolidation is the clear next priority.

## A note on real evidence

Every problem's solution is real, compiled Java, executed with real test assertions via the shared `Check.java` helper (identical to the one used in Weeks 20–24). One expected test value (noted above, in Graphs) was caught wrong on the first real run and corrected by re-deriving the exact known LeetCode example answer rather than relying on an approximate from-memory recollection — a test-authoring error, not an implementation bug, consistent with every other correction across this six-week series.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, real pass/fail counts from the `Check` assertion helper). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
