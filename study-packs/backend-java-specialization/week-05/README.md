---
title: "Backend Java Specialization, Week 5 — Spring, End to End"
document_type: study-pack
week: 5
track: backend-java-specialization
status: draft
estimated_hours: 9
---

# Week 5 — Spring, End to End

## Weekly Outcome

By the end of this week you can explain Spring's bean lifecycle and auto-configuration mechanism, contrast Spring Framework and Spring Boot precisely, state why `@Transactional` silently does nothing on a self-invoked call, describe Spring Security's filter chain request flow, and explain WebFlux's reactive model, cache abstraction pitfalls, Actuator observability hooks, and Spring's test-slice/context-caching strategy.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) places Spring directly after Java "since its transaction and bean-lifecycle mechanics assume the concurrency and reflection material" from Weeks 1–4 — Spring's proxy-based AOP (used for `@Transactional`, `@Cacheable`, and Security) is built on the dynamic-proxy and reflection concepts from T-113 (Week 1), and its request-handling thread model assumes Week 3's concurrency reasoning.

## Prerequisites

Weeks 1–4 complete, in particular T-113 (Reflection and Dynamic Proxies) and T-406 (Executors and Thread Pool Sizing).

## Schedule

| Day | Focus |
|---|---|
| Mon | Spring MVC Fundamentals (T-2203) — a fast review if already comfortable with `@RestController`/`@Autowired` |
| Tue | Spring Auto-Configuration and Bean Lifecycle; Spring Framework vs. Spring Boot (both T-506/T-501) |
| Wed | Spring Bean Scopes and Proxy Modes (T-502) |
| Thu | Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation (T-503/T-504/T-505) |
| Fri | Spring Security Filter Chain (T-511) |
| Sat | Spring WebFlux and Reactive Programming (T-509); Spring Cache Abstraction and Pitfalls (T-514) |
| Sun | Spring Boot Actuator (T-516); Spring Testing: Slices and Context Caching (T-517); review checklist below |

## Required Reading

The full Spring domain, per [`syllabus/05-spring/INDEX.md`](../../../syllabus/05-spring/INDEX.md) (the exhaustive, canonical source) — 10 topics, including the T-2203 Junior Fundamentals chapter added 2026-09-07.

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-2203 — Spring MVC Fundamentals | [`spring-mvc-fundamentals.md`](../../../syllabus/05-spring/spring-mvc-fundamentals.md) |
| 2 | T-506/T-501 — Spring Auto-Configuration and Bean Lifecycle | [`auto-configuration-and-bean-lifecycle.md`](../../../syllabus/05-spring/auto-configuration-and-bean-lifecycle.md) |
| 3 | T-506/T-501 — Spring Framework vs. Spring Boot: Auto-Configuration and the Embedded Server | [`spring-framework-vs-spring-boot.md`](../../../syllabus/05-spring/spring-framework-vs-spring-boot.md) |
| 4 | T-502 — Spring Bean Scopes and Proxy Modes | [`spring-bean-scopes-and-proxy-modes.md`](../../../syllabus/05-spring/spring-bean-scopes-and-proxy-modes.md) |
| 5 | T-503/T-504/T-505 — Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation | [`transactional-proxy-mechanics-and-propagation.md`](../../../syllabus/05-spring/transactional-proxy-mechanics-and-propagation.md) |
| 6 | T-511 — Spring Security Filter Chain | [`security-filter-chain.md`](../../../syllabus/05-spring/security-filter-chain.md) |
| 7 | T-509 — Spring WebFlux and Reactive Programming | [`spring-webflux-and-reactive-programming.md`](../../../syllabus/05-spring/spring-webflux-and-reactive-programming.md) |
| 8 | T-514 — Spring Cache Abstraction and Pitfalls | [`spring-cache-abstraction-and-pitfalls.md`](../../../syllabus/05-spring/spring-cache-abstraction-and-pitfalls.md) |
| 9 | T-516 — Spring Boot Actuator, Health, and Observability Hooks | [`spring-actuator-health-and-observability-hooks.md`](../../../syllabus/05-spring/spring-actuator-health-and-observability-hooks.md) |
| 10 | T-517 — Spring Testing: Slices and Context Caching | [`spring-testing-slices-and-context-caching.md`](../../../syllabus/05-spring/spring-testing-slices-and-context-caching.md) |

## Hands-On Exercises

Real, compiled, executed demos exist for all 10 chapters:

- [`practice/java/spring-mvc-fundamentals/`](../../../practice/java/spring-mvc-fundamentals/) (T-2203 — a real Spring Boot 3.5.16 app with embedded Tomcat, exercised live with `curl`)
- [`practice/java/week-07/spring-internals/`](../../../practice/java/week-07/spring-internals/) (Auto-Configuration and Bean Lifecycle)
- [`practice/java/spring-vs-spring-boot/embedded-server-and-autoconfig/`](../../../practice/java/spring-vs-spring-boot/embedded-server-and-autoconfig/) (Framework vs. Boot)
- [`practice/java/spring/spring-bean-scopes-and-proxy-modes/`](../../../practice/java/spring/spring-bean-scopes-and-proxy-modes/) (T-502)
- [`practice/java/week-03/spring-demos/`](../../../practice/java/week-03/spring-demos/) (T-503/T-504/T-505 — six demos, plain Spring Framework 6.1.14 jars, no Boot auto-configuration masking the mechanism)
- [`practice/java/week-07/security/`](../../../practice/java/week-07/security/) (T-511 — `SecurityFilterChainDemo.java`)
- [`practice/java/spring/spring-webflux-and-reactive-programming/`](../../../practice/java/spring/spring-webflux-and-reactive-programming/) (T-509)
- [`practice/java/spring/spring-cache-abstraction-and-pitfalls/`](../../../practice/java/spring/spring-cache-abstraction-and-pitfalls/) (T-514)
- [`practice/java/spring/spring-actuator-health-and-observability-hooks/`](../../../practice/java/spring/spring-actuator-health-and-observability-hooks/) (T-516)
- [`practice/java/spring/spring-testing-slices-and-context-caching/`](../../../practice/java/spring/spring-testing-slices-and-context-caching/) (T-517)

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "why does calling `this.someTransactionalMethod()` from within the same class not start a new transaction?" and "what determines rollback — checked or unchecked exceptions, by default?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: draw the proxy indirection for a self-invocation `@Transactional` bug on paper from memory, then check against the chapter's own Whiteboard Explanation section.

## Review Checklist

- [ ] Completed all 10 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 6 of the 10 real demos listed above.
- [ ] Can describe the Spring Security filter chain's request flow, in order, from memory.

## Completion Criteria

- [ ] Can explain the self-invocation proxy bug and its fix (extract to another bean, or use `AopContext`) unprompted.
- [ ] Can state the default rollback rule and how to override it with `rollbackFor`.
- [ ] Can explain what auto-configuration actually does at startup (conditional bean registration) rather than describing it as "magic."

## Retrospective

Note whether you've hit the self-invocation proxy bug before in real code without knowing why — it is one of the highest-frequency "gotcha" questions at Spring interviews specifically because it looks like it should work.

## Next Week

[Week 6 — Databases, Part 1: JPA and Hibernate Mechanics](../week-06/README.md).
