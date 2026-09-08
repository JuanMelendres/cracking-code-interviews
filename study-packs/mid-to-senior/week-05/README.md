---
title: "Mid → Senior, Week 5 — Testing at Senior Depth"
document_type: study-pack
week: 5
track: mid-to-senior
status: draft
estimated_hours: 7
---

# Week 5 — Testing at Senior Depth

## Weekly Outcome

By the end of this week you can write an integration test against a real dependency (via Testcontainers, not a mock) and explain when a mock would have hidden the exact bug the integration test catches, and design a contract test between two services without needing both running end to end.

## Why This Week Matters

[Junior → Mid](../../junior-to-mid/README.md) taught JUnit basics and the test pyramid shape. This week is where "when does a mock lie to you" becomes concrete — the cookbook cross-references below are real cases where a mocked test passed while production broke.

## Prerequisites

[Junior → Mid](../../junior-to-mid/README.md)'s Unit Testing and Test Strategy weeks.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | Integration Testing Against Real Dependencies |
| Thu–Fri | Contract Testing for Services |
| Sat | Practice exercises from both chapters |
| Sun | Review checklist below |

## Required Reading

[`syllabus/08-testing/INDEX.md`](../../../syllabus/08-testing/INDEX.md) — this week's two priority topics (Integration Testing Against Real Dependencies; Contract Testing for Services).

## Hands-On Exercises

Real demos exist under `practice/java/week-18/` — `contract-testing/`, `load-testing/` — and [`practice/java/spring/spring-testing-slices-and-context-caching/`](../../../practice/java/spring/spring-testing-slices-and-context-caching/). Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`mocked-repository-tests-masking-a-real-schema-migration-break.md`](../../../production-cookbook/mocked-repository-tests-masking-a-real-schema-migration-break.md)
- [`flaky-ci-integration-tests-from-shared-container-state.md`](../../../production-cookbook/flaky-ci-integration-tests-from-shared-container-state.md)

## Interview Answer Drills

Answer, out loud: "give a concrete example of a bug a mocked repository test would miss but a Testcontainers-backed integration test would catch" and "what does a consumer-driven contract test actually verify that an integration test doesn't?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described service boundary (e.g., an order service calling a payments service), explain what you'd contract-test versus integration-test versus unit-test, and why, in under 3 minutes.

## Review Checklist

- [ ] Completed both chapters' own L3 Mastery Checklists.
- [ ] Reproduced the contract-testing and load-testing demos.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Wrote an integration test using a real dependency (e.g., Testcontainers-backed database) for a class of your own.
- [ ] Can explain the mocked-repository cookbook scenario's root cause unprompted.
- [ ] Can describe consumer-driven contract testing's basic mechanism.

## Retrospective

Note any test in your own past work that mocked something it shouldn't have — this week's cookbook entries exist specifically because that mistake is common and expensive.

## Next Week

[Week 6 — Messaging and Event-Driven Systems](../week-06/README.md).
