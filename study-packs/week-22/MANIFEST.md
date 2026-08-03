---
title: "Week 22 Study Pack — Manifest"
week: 22
plan: B
last_reviewed: 2026-08-03
---

# Week 22 Study Pack — Manifest

**Topics:** T-1403 (Hashing), T-1404 (Binary Search), T-1417 (Concurrency Coding), T-1416 (Design-Style) · **Plan:** B, Coding-Problem Volume Gap — Phase 3 (continues Weeks 20–21; per `00-project/coverage-audit-2026-07-31.md` this remains the program's single largest remaining quantitative shortfall: 106 of a 150–170 target going into this week). Bounded batch — the four next-thinnest patterns by ratio after Weeks 20–21's nine, not an attempt to close the full remaining gap in one pass.
**Files:** 5 (+ this manifest) · **Total words:** 5,222 (real count, `wc -w` over the 4 pattern files) + 830 (README, separately) · **New problems:** 19, all real, compiled, executed.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, scope note, exit criteria | 830 |
| 2 | `01-hashing-coding-practice.md` | 5 problems (LC 217, 560, 349, 202, 454) | 1,016 |
| 3 | `02-binary-search-coding-practice.md` | 5 problems (LC 34, 74, 153, 1011, 4) | 1,407 |
| 4 | `03-concurrency-coding-practice.md` | 4 problems (LC 1117, 1195, 1226, 1188) | 1,310 |
| 5 | `04-design-coding-practice.md` | 5 problems (LC 460, 981, 355, 1472, 359) | 1,489 |

---

## Verification

| Pattern | Problems | Status | Real assertions |
|---|---|---|---|
| Hashing (T-1403) | LC 217, 560, 349, 202, 454 | **Executed.** `javac`/`java`, OpenJDK 21.0.12. All passed on first run. | 11/11 pass |
| Binary Search (T-1404) | LC 34, 74, 153, 1011, 4 | **Executed.** One hand-computed expected value (LC 4's uneven-length test) was initially wrong — merged-array trace showed the true median was 0.0, not the guessed 3.0; corrected in the test, not the implementation, before citing as passing. | 12/12 pass |
| Concurrency Coding (T-1417) | LC 1117, 1195, 1226, 1188 | **Executed with real threads**, re-run 5 times consecutively to check for scheduling-dependent flakiness — identical 10/10 result every run. Dining Philosophers verified via a bounded `join(10_000)` timeout (a real deadlock would have caused a timeout; actual completion took ~2ms). | 10/10 pass (×5 runs) |
| Design-Style (T-1416) | LC 460, 981, 355, 1472, 359 | **Executed.** Two hand-computed expected values (LC 1472's second and third assertions) were initially wrong — miscounted the browser-history pointer position by hand; corrected in the test after re-tracing, not the implementation, before citing as passing. | 23/23 pass |
| **Total** | **19 problems** | | **56/56 pass** |

Source: `practice/java/week-22/{hashing,binary-search,concurrency,design}/`

## Coverage impact (per-pattern, before → after this week)

| Pattern | Before | After | Register target |
|---|---|---|---|
| T-1403 Hashing | 4/12 (33%) | 9/12 (75%) | 12 |
| T-1404 Binary Search | 4/12 (33%) | 9/12 (75%) | 12 |
| T-1417 Concurrency Coding | 3/8 (38%) | 7/8 (88%) | 8 |
| T-1416 Design-Style | 4/10 (40%) | 9/10 (90%) | 10 |
| **Program-wide total** | **106 / 150–170** | **125 / 150–170** | 150–170 |

## Errata addressed this week

None. This is new problem-volume content, not a correction to existing material.

## Scope note

T-1417 (Concurrency Coding) stops at 7/8, not 8/8 — the plausible 5th candidate, LC 1242 (Web Crawler Multithreaded), is a LeetCode Premium-only problem whose exact specification could not be verified against the free problem statement available to this project; reconstructing it from an unverified description would risk citing a solution to a problem that doesn't actually match LeetCode's real grader. Per `CLAUDE.md`'s prohibition on fabricated claims, this pattern is left one problem short of full closure rather than guessing at the spec.

Remaining gaps after this week (not yet addressed): T-1401 complexity analysis (cross-cutting, no dedicated problem type), T-1402 arrays/two-pointers/sliding-window (7/18, 39%), T-1407 heaps/top-k (5/12, 42%), T-1408 trees/BST (7/16, 44%), T-1409 graphs (11/22, 50%), T-1411 DP remainder (16/32, 50% — still the largest absolute remaining gap at 16 problems), T-1418 advanced structures (0/8, Expert tier, deliberately deprioritized — see Week 20's rationale), T-1419 communication protocol (cross-cutting narration skill).

## A note on real evidence

Every problem's solution is real, compiled Java, executed with real test assertions via the shared `Check.java` helper (identical to the one used in Weeks 20–21). The concurrency batch specifically used real `Thread` objects, real `Semaphore`/`ReentrantLock`/intrinsic-lock coordination, and real timing-bounded joins — no simulated or mocked concurrency. Three expected test values (noted above, in Binary Search and Design-Style) were caught wrong on the first real run and corrected by hand-verifying the algorithm's actual output against the problem's real constraints — all were test-authoring errors (a mis-guessed median, two mis-traced browser-history pointer positions), confirmed by manual re-trace, not implementation bugs.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12, real pass/fail counts from the `Check` assertion helper, the concurrency suite's 5-run repetition actually executed rather than asserted). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
