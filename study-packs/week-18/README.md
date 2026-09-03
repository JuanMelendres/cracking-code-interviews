---
title: "Week 18 — Testing Domain Closure"
document_type: study-pack
week: 18
status: draft
estimated_hours: 14
---

# Week 18 — Testing Domain Closure

## Weekly Outcome

By the end of this week you can explain, defend, and reproduce with real code the five remaining Testing register topics: performance and load testing methodology, writing tests live in an interview, contract testing for services, JUnit 5 architecture and advanced features, and mutation and property-based testing — closing the Testing domain to 8/8 register topics, the second domain (after Security in Week 17) closed to full coverage.

## Why This Week Matters

The repository's own coverage audit (`00-project/coverage-audit-2026-07-31.md`) reported Testing at 2/8 register topics covered, but a closer check this week found the real number was 3/8 — T-1103 (Mockito, test doubles, and mocking boundaries) was already bundled into Week 11's test-strategy chapter but untagged in study-pack front matter, so the audit's grep-based method undercounted it. That correction left exactly 5 genuinely uncovered topics, small enough to close the domain fully in one week, the same way Week 17 closed Security. These topics recur directly in Senior/Staff interviews: live-coding rounds test TDD discipline under pressure, contract testing comes up in any microservices-architecture discussion, and mutation testing is a recognizable signal of deep testing maturity even at Rare interview frequency.

## Prerequisites

Week 11's `test-strategy-and-test-doubles.md` and `integration-testing-against-real-dependencies.md` — this week's five chapters all build on the pyramid/test-doubles vocabulary and the Testcontainers-based integration-testing pattern established there. `syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md` (Week 11) for the percentile-mathematics depth the load-testing chapter deliberately doesn't duplicate.

## Schedule

See `10-week-18-checklist.md` for the day-by-day breakdown.

## Required Reading

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | This file |
| 2 | `01-performance-and-load-testing-methodology.md` | T-1106 — summary + link; full chapter canonical at `syllabus/08-testing/performance-and-load-testing-methodology.md` |
| 3 | `02-writing-tests-live-in-an-interview.md` | T-1108 — summary + link; full chapter canonical at `syllabus/08-testing/writing-tests-live-in-an-interview.md` |
| 4 | `03-contract-testing-for-services.md` | T-1105 — summary + link; full chapter canonical at `syllabus/08-testing/contract-testing-for-services.md` |
| 5 | `04-junit5-architecture-and-advanced-features.md` | T-1102 — summary + link; full chapter canonical at `syllabus/08-testing/junit5-architecture-and-advanced-features.md` |
| 6 | `05-mutation-and-property-based-testing.md` | T-1107 — summary + link; full chapter canonical at `syllabus/08-testing/mutation-and-property-based-testing.md` |
| 7 | `06-hands-on-lab.md` | 5 labs reproducing this week's real demonstrations |
| 8 | `07-flashcards.md` | 15 cards |
| 9 | `08-week-18-mock-interview.md` | 45-min Testing technical round |
| 10 | `09-design-exercise-test-strategy-for-a-checkout-service.md` | Full test-strategy design exercise for a checkout service |
| 11 | `10-week-18-checklist.md` | Day-by-day checklist |
| 12 | `resources.md` | Sources classified PRIMARY/INTERNAL/TOOL |

## Hands-On Exercises

Complete all 5 labs in `06-hands-on-lab.md` — a real load test showing mean/percentile divergence, a real four-step red-green-refactor TDD cycle, a real contract-verification pass/fail pair, real JUnit 5 advanced-feature and tag-filtering evidence, and a real property-based bug discovery plus mutation-survival/kill pair.

## Interview Answer Drills

Deliver the 30-second and 2-minute answers for each topic aloud, unprompted, from each canonical chapter's Interview Answer Framework section.

## Coding Problems

None this week in the usual LeetCode sense — testing-methodology topics are practice-and-reasoning-shaped, not algorithm-shaped. See `06-hands-on-lab.md` for this week's hands-on equivalent, and `02-writing-tests-live-in-an-interview.md` for a dedicated live-coding-technique treatment.

## System Design Exercise

`09-design-exercise-test-strategy-for-a-checkout-service.md` — produce a full test strategy for a checkout service, applying all five of this week's topics.

## Behavioral Exercise

None formally scheduled this week; continue any in-progress STAR story work from earlier weeks.

## Mock Interview

`08-week-18-mock-interview.md` — 45-minute Testing technical round, candidate/evaluator sections hard-separated, including one live-coding question.

## Review Checklist

See `10-week-18-checklist.md`.

## Completion Criteria

- [ ] All five canonical chapters read in full
- [ ] All five labs in `06-hands-on-lab.md` reproduced with matching results
- [ ] Design exercise completed independently before checking the reference solution
- [ ] Mock interview average score ≥ 3.5

## Retrospective

Note which of the five topics needs a second pass, and whether the design exercise revealed a gap not caught by the individual chapter labs.

## Next Week

Testing coverage is now at 8/8 register topics (was 3/8 before this week, corrected from the audit's reported 2/8) — the second domain in the entire register closed to full coverage, after Security in Week 17. Per `00-project/coverage-audit-2026-07-31.md`'s next-actions list: continue JVM depth (6 topics remain), tackle the coding-problem volume gap (60 of a 150–170 target, the largest remaining numeric shortfall), or start Phase 6 complementary deliverables (interview-playbook, cheat-sheets, architecture atlas, production cookbook, behavioral handbook — none started yet).
