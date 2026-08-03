---
title: "Week 23 Study Pack — Manifest"
week: 23
plan: B
last_reviewed: 2026-08-03
---

# Week 23 Study Pack — Manifest

**Topics:** T-1411 (Dynamic Programming, continued), T-1402 (Arrays/Two-Pointers/Sliding Window), T-1407 (Heaps/Top-K), T-1408 (Trees/BST) · **Plan:** B, Coding-Problem Volume Gap — Phase 4 (continues Weeks 20–22; per `00-project/coverage-audit-2026-07-31.md` this remains the program's single largest remaining quantitative shortfall: 125 of a 150–170 target going into this week). Bounded batch — DP's remainder (largest absolute gap) plus the next three thinnest-by-ratio patterns, not an attempt to close the full remaining gap in one pass.
**Files:** 5 (+ this manifest) · **Total words:** 5,467 (real count, `wc -w` over the 4 pattern files) + 757 (README, separately) · **New problems:** 20, all real, compiled, executed.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, scope note, exit criteria | 757 |
| 2 | `01-dp-coding-practice.md` | 5 problems (LC 91, 63, 337, 188, 132) | 1,485 |
| 3 | `02-arrays-two-pointers-coding-practice.md` | 5 problems (LC 11, 239, 238, 189, 31) | 1,292 |
| 4 | `03-heaps-coding-practice.md` | 5 problems (LC 1046, 692, 373, 767, 1642) | 1,306 |
| 5 | `04-trees-coding-practice.md` | 5 problems (LC 543, 297, 230, 105, 112) | 1,384 |

---

## Verification

| Pattern | Problems | Status | Real assertions |
|---|---|---|---|
| Dynamic Programming (T-1411) | LC 91, 63, 337, 188, 132 | **Executed.** `javac`/`java`, OpenJDK 21.0.12. All passed on first run. | 15/15 pass |
| Arrays/Two-Pointers (T-1402) | LC 11, 239, 238, 189, 31 | **Executed.** All passed on first run. | 11/11 pass |
| Heaps (T-1407) | LC 1046, 692, 373, 767, 1642 | **Executed.** All passed on first run. | 10/10 pass |
| Trees/BST (T-1408) | LC 543, 297, 230, 105, 112 | **Executed.** One hand-computed expected value (LC 112's second assertion) was initially wrong — the test tree has a root-to-leaf path (5-8-13) summing to exactly 26, which the test incorrectly expected to be absent; corrected the test (both the expected boolean and the target sum used) after manually re-tracing all four root-to-leaf sums, not the implementation, before citing as passing. | 18/18 pass |
| **Total** | **20 problems** | | **54/54 pass** |

Source: `practice/java/week-23/{dp,arrays-two-pointers,heaps,trees}/`

## Coverage impact (per-pattern, before → after this week)

| Pattern | Before | After | Register target |
|---|---|---|---|
| T-1411 Dynamic Programming | 16/32 (50%) | 21/32 (66%) | 32 |
| T-1402 Arrays/Two-Pointers | 7/18 (39%) | 12/18 (67%) | 18 |
| T-1407 Heaps/Top-K | 5/12 (42%) | 10/12 (83%) | 12 |
| T-1408 Trees/BST | 7/16 (44%) | 12/16 (75%) | 16 |
| **Program-wide total** | **125 / 150–170** | **145 / 150–170** | 150–170 |

## Errata addressed this week

None new to this batch's problems. In passing, the pre-work audit surfaced an unrelated pre-existing documentation-accuracy issue in `study-packs/week-12/07-java-coding-practice.md` (a claim of "no problem repeated from Weeks 1–11" that is contradicted by LC 3 being re-solved there after already appearing in Week 1) — flagged as a separate background task rather than fixed inline here, since it's out of scope for this week's four target patterns and doesn't affect any register coverage count either way.

## Scope note

DP crossed 50% coverage for the first time this week (21/32, 66%) but 11 problems still remain in that pattern alone — still a plausible Phase 5 candidate alongside Graphs (11/22, 50%, unchanged this week) if further coding-volume work continues. At 145/150–170 after this week, the program is close enough to the low end of its target range that a future session should explicitly weigh a short closing batch against finally starting Phase 6 complementary-deliverables consolidation.

Remaining gaps after this week (not yet addressed): T-1401 complexity analysis (cross-cutting, no dedicated problem type), T-1409 graphs (11/22, 50%), T-1411 DP remainder (21/32, 66%), T-1418 advanced structures (0/8, Expert tier, deliberately deprioritized — see Week 20's rationale), T-1419 communication protocol (cross-cutting narration skill).

## A note on real evidence

Every problem's solution is real, compiled Java, executed with real test assertions via the shared `Check.java` helper (identical to the one used in Weeks 20–22). One expected test value (noted above, in Trees) was caught wrong on the first real run and corrected by hand-verifying all four root-to-leaf path sums in the test tree against the problem's real constraints — the failure was a test-authoring error (an incomplete manual sum enumeration), confirmed by manual re-trace, not an implementation bug.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, real pass/fail counts from the `Check` assertion helper). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
