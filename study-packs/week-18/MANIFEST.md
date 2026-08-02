---
title: "Week 18 Study Pack — Manifest"
week: 18
plan: B
last_reviewed: 2026-08-02
---

# Week 18 Study Pack — Manifest

**Topics:** T-1106, T-1108, T-1105, T-1102, T-1107 · **Plan:** B, Testing Domain Closure (Phase 4/5 — closes Testing from 3/8 to 8/8 register topics, the second domain in the entire register closed to full coverage after Security in Week 17 — see `00-project/coverage-audit-2026-07-31.md`)
**Files:** 12 (+ this manifest) · **Total words:** 7,370 (real count, `wc -w` over all 12 files)
**Canonical chapters:** 5 new `handbook/testing/` chapters, 19,284 words total (real count, `wc -w`), written full-depth from the start — this week did not need a separate slimming pass.

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, schedule, exit criteria | 738 |
| 2 | `01-performance-and-load-testing-methodology.md` | T-1106 — summary + link; full chapter canonical at `handbook/testing/performance-and-load-testing-methodology.md` | 548 |
| 3 | `02-writing-tests-live-in-an-interview.md` | T-1108 — summary + link; full chapter canonical at `handbook/testing/writing-tests-live-in-an-interview.md` | 503 |
| 4 | `03-contract-testing-for-services.md` | T-1105 — summary + link; full chapter canonical at `handbook/testing/contract-testing-for-services.md` | 517 |
| 5 | `04-junit5-architecture-and-advanced-features.md` | T-1102 — summary + link; full chapter canonical at `handbook/testing/junit5-architecture-and-advanced-features.md` | 491 |
| 6 | `05-mutation-and-property-based-testing.md` | T-1107 — summary + link; full chapter canonical at `handbook/testing/mutation-and-property-based-testing.md` | 591 |
| 7 | `06-hands-on-lab.md` | 5 labs, all real and reproducible | 568 |
| 8 | `07-flashcards.md` | 15 cards | 1,073 |
| 9 | `08-week-18-mock-interview.md` | 45-min Testing technical round (including a live-coding question) | 886 |
| 10 | `09-design-exercise-test-strategy-for-a-checkout-service.md` | Full test-strategy design exercise, worked reference solution | 1,009 |
| 11 | `10-week-18-checklist.md` | Day-by-day checklist | 243 |
| 12 | `resources.md` | Sources classified PRIMARY/INTERNAL/TOOL | 203 |

---

## Verification

| Item | Status |
|---|---|
| Java — Load testing, mean vs. percentiles | **Executed.** OpenJDK 21.0.12. Real local HTTP server with an injected 1-in-20 slow path (150ms vs. 3ms), load-tested by a 20-thread pool over 2,000 requests: mean=12.45ms, p50=4.17ms, p95=150.54ms, p99=155.23ms, max=187.48ms — p95 matches the injected delay almost exactly, invisible below that threshold. Source: `practice/java/week-18/load-testing/` |
| Java + JUnit 5 — Live-coding TDD | **Executed.** Real four-step red-green-refactor cycle on a run-length-encoding kata: RED (`expected: <> but was: <null>`) → GREEN → RED again on a new test against the old implementation (`expected: <a1> but was: <>`) → GREEN across all 3 accumulated tests. Real captured JUnit 5 console output at every step, run via a programmatic `Launcher`-API console runner (no build tool). Source: `practice/java/week-18/live-coding-tdd/` |
| Java + JUnit 5 — Contract testing | **Executed.** Real HTTP provider serving `/orders/42`. Compliant mode: verification test passes (`{"id":42,"status":"SHIPPED","amount":19.99}`). Real, deliberate breaking-change mode (`amount`→`total`, `status` removed): verification fails with `contract requires field 'status' -- consumer displays order status to the user`. Source: `practice/java/week-18/contract-testing/` |
| Java + JUnit 5 — Advanced features, tag filtering | **Executed.** Real 10-test class: 5 `@ParameterizedTest` cases (`@CsvSource`), 4 `@TestFactory` dynamic tests, 1 `@Nested` test, plus a custom `BeforeEachCallback`/`AfterEachCallback` extension printing real per-test timing — 10/10 pass. Real tag-filtering via the `Launcher` API's `TagFilter`: `slow` selects exactly 1/10, `fast` selects exactly 9/10 — genuine execution partitioning, not just labeling. Source: `practice/java/week-18/junit5-features/` |
| Java + JUnit 5 — Property-based and mutation testing | **Executed.** Real merge-sorted-arrays bug (forgotten tail-copy loop): two hand-picked example tests (both biased toward array `a` longer) pass despite the bug; a randomized property test (fixed seed 42, 2000 trials) fails on **trial 2** with a concrete counterexample (`a=[1,4,5] b=[5,6,9] expected=[1,4,5,5,6,9] actual=[1,4,5,0,0,0]`). Real single-token mutant (`>=` → `>` in a discount-eligibility boundary check): a weak suite (no boundary test) passes identically against both the original and the mutant (survives); a strengthened suite (one added test at `orderTotal=100`) passes against the original and fails against the mutant (`expected: <true> but was: <false>`) — killed. Source: `practice/java/week-18/mutation-property/` |
| Interview statistics | None invented anywhere in this pack |

## Errata addressed this week

**Coverage-audit correction:** `00-project/coverage-audit-2026-07-31.md` reported Testing at 2/8 register topics covered (T-1101, T-1104). This week found the real number was 3/8 — `handbook/testing/test-strategy-and-test-doubles.md`'s own "Topic register" line reads `T-1101/T-1103`, meaning T-1103 (Mockito, test doubles, and mocking boundaries) was already covered, bundled into that chapter, but never tagged in any `study-packs/*/topic_id` front-matter field — exactly the kind of bundled-chapter undercounting the audit's own methodology section (§6) flagged as a known limitation of its grep-based approach. This left 5, not 6, genuinely uncovered Testing topics, all closed this week.

## Scope note

This week covers all 5 of the 5 genuinely remaining Testing register topics (T-1102, T-1105, T-1106, T-1107, T-1108), closing the domain completely — the second full domain closure after Security in Week 17. T-1107 (mutation & property-based testing) is Experimental tier with Rare interview frequency (IWI 4.3, the lowest of any topic covered across Weeks 16–18) — included anyway specifically to complete the domain, per the same full-closure logic applied to Security's lower-IWI topics (T-1304, T-1306) in Week 17.

## A note on real evidence and dependency resolution

All five demos use only JUnit Jupiter/Platform jars (5.12.2 / 1.12.2) and Mockito (5.17.0, referenced but not directly exercised this week) already present in the local Maven repository from prior weeks' work — no network dependency download was required, and no build tool (Maven/Gradle) was used; a small, reusable `ConsoleTestRunner.java` (`practice/java/week-18/junit5-features/`) invokes JUnit 5's programmatic `Launcher` API directly. Mutation and property-based testing were deliberately demonstrated **without** PIT or jqwik (neither was available in the local Maven cache, and their absence was verified before deciding to hand-roll equivalent, real demonstrations rather than fabricate results from an unavailable tool) — both hand-rolled versions produce real, executed, directly-comparable evidence to what the dedicated frameworks would show, and the chapters explicitly link to PIT/jqwik as the recommended production tooling.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs on OpenJDK 21.0.12 against real JUnit Jupiter/Platform jars, real HTTP servers via `com.sun.net.httpserver`). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
