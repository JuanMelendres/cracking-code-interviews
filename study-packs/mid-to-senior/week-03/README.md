---
title: "Mid → Senior, Week 3 — Spring Internals"
document_type: study-pack
week: 3
track: mid-to-senior
status: draft
estimated_hours: 7
---

# Week 3 — Spring Internals

## Weekly Outcome

By the end of this week you can explain why `@Transactional` silently does nothing on a self-invoked call, state the actual rollback rule (unchecked vs. checked exceptions) without guessing, and describe how Spring Security's filter chain processes a request end to end.

## Why This Week Matters

[Junior → Mid](../../junior-to-mid/README.md) Week 7 taught Spring MVC usage — controller, service, repository, constructor injection. This week goes underneath that: proxy mechanics are the single most common source of "why doesn't my transaction/cache/security annotation work" bugs at Mid-to-Senior depth.

## Prerequisites

[Junior → Mid](../../junior-to-mid/README.md)'s Spring MVC Fundamentals week, and this pack's own Week 1 (concurrency) — transaction propagation across threads assumes solid concurrency reasoning.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation |
| Thu–Fri | Spring Security Filter Chain |
| Sat | Practice exercises from both chapters |
| Sun | Review checklist below |

## Required Reading

[`syllabus/05-spring/INDEX.md`](../../../syllabus/05-spring/INDEX.md) — this week's two priority topics (Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation; Spring Security Filter Chain).

## Hands-On Exercises

Real demos exist under [`practice/java/spring/`](../../../practice/java/spring/) — `spring-bean-scopes-and-proxy-modes/`, `spring-cache-abstraction-and-pitfalls/`, `spring-testing-slices-and-context-caching/`, `spring-webflux-and-reactive-programming/`, `spring-actuator-health-and-observability-hooks/`. Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`transactional-annotation-silently-skipped-on-self-invocation.md`](../../../production-cookbook/transactional-annotation-silently-skipped-on-self-invocation.md)
- [`expensive-authorization-check-ahead-of-cheap-filter-chain-validation.md`](../../../production-cookbook/expensive-authorization-check-ahead-of-cheap-filter-chain-validation.md)

## Interview Answer Drills

Answer, out loud: "why does calling `this.someTransactionalMethod()` from within the same class not start a new transaction?" and "what determines rollback — checked or unchecked exceptions, by default?" before checking the chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: draw the proxy indirection for a self-invocation bug on a whiteboard (or paper) from memory, then check against the @Transactional chapter's own Whiteboard Explanation section.

## Review Checklist

- [ ] Completed both chapters' own L3 Mastery Checklists.
- [ ] Reproduced at least 2 of the real Spring demos listed above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can explain the self-invocation proxy bug and its fix (extract to another bean, or use `AopContext`) unprompted.
- [ ] Can state the default rollback rule and how to override it with `rollbackFor`.
- [ ] Can describe the Spring Security filter chain's request flow, in order, from memory.

## Retrospective

Note whether you've hit the self-invocation bug before in real code without knowing why — this is one of the highest-frequency "gotcha" questions at Mid-to-Senior Spring interviews specifically because it looks like it should work.

## Next Week

[Week 4 — Database Internals](../week-04/README.md).
