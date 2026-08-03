---
title: "Week 24 Study Pack — Manifest"
week: 24
plan: B
last_reviewed: 2026-08-03
---

# Week 24 Study Pack — Manifest

**Topics:** T-1411 (Dynamic Programming, final batch), T-1409 (Graphs, final batch) · **Plan:** B, Coding-Problem Volume Gap — Phase 5, the closing batch of a five-week series (Weeks 20–24). Per `00-project/coverage-audit-2026-07-31.md` this shortfall began at 60/150–170 problems and, after this batch, reaches **157/150–170 — inside the target range.**
**Files:** 2 (+ this manifest) · **Total words:** 3,590 (real count, `wc -w` over the 2 pattern files) + 860 (README, separately) · **New problems:** 12, all real, compiled, executed.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, scope note, exit criteria, series closure note | 860 |
| 2 | `01-dp-coding-practice.md` | 6 problems (LC 329, 312, 10, 44, 96, 32) | 1,857 |
| 3 | `02-graphs-coding-practice.md` | 6 problems (LC 785, 332, 1319, 399, 802, 1466) | 1,733 |

---

## Verification

| Pattern | Problems | Status | Real assertions |
|---|---|---|---|
| Dynamic Programming (T-1411) | LC 329, 312, 10, 44, 96, 32 | **Executed.** `javac`/`java`, OpenJDK 21.0.12. All passed on first run — the hardest batch in the entire five-week series (two Hard 2D string-matching problems, a classic Hard interval-DP problem), verified clean without a single test-authoring correction needed. | 21/21 pass |
| Graphs (T-1409) | LC 785, 332, 1319, 399, 802, 1466 | **Executed.** All passed on first run. | 13/13 pass |
| **Total** | **12 problems** | | **34/34 pass** |

Source: `practice/java/week-24/{dp,graphs}/`

## Coverage impact (per-pattern, before → after this week)

| Pattern | Before | After | Register target |
|---|---|---|---|
| T-1411 Dynamic Programming | 21/32 (66%) | 27/32 (84%) | 32 |
| T-1409 Graphs | 11/22 (50%) | 17/22 (77%) | 22 |
| **Program-wide total** | **145 / 150–170** | **157 / 150–170** | 150–170 |

**This is the first time the coding-problem volume metric has landed inside its target range** since the audit that opened this five-week series (`00-project/coverage-audit-2026-07-31.md`, which found 60/150–170).

## Errata addressed this week

None this batch. A second, related documentation-accuracy issue was surfaced incidentally by this week's pre-work audit (Week 12's "no repeat" claim is also contradicted by a repeated LC 207, in addition to the previously-flagged LC 3) — this has been folded into the existing spawned background task rather than creating a duplicate, since it's the same file and the same underlying claim.

## Scope note

This is very likely the final bounded batch in the coding-problem volume series. What remains after this week — DP's last 5 problems (27/32, 84%), Graphs' last 5 (17/22, 77%), and Expert-tier Advanced Structures (0/8, T-1418, deliberately deprioritized in every batch of this series per the blueprint's own explicit tier-priority guidance) — is optional refinement rather than a coverage gap requiring further urgent, bounded batches. The next clear priority is Phase 6 complementary-deliverables consolidation, untouched throughout this entire five-week series.

## A note on real evidence

Every problem's solution is real, compiled Java, executed with real test assertions via the shared `Check.java` helper (identical to the one used in Weeks 20–23). Notably, this batch — despite containing the three hardest problems in the whole series (LC 10, LC 44, LC 312) — required zero test-authoring corrections, unlike several prior weeks; every expected value was correctly derived by hand against well-known LeetCode examples before the first compile.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, real pass/fail counts from the `Check` assertion helper). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
