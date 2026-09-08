---
title: "Mid → Senior, Week 9 — Security and Observability"
document_type: study-pack
week: 9
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 9 — Security and Observability

## Weekly Outcome

By the end of this week you can name the OWASP Top 10 risks most relevant to a typical Spring backend with a concrete mitigation for each, correctly distinguish authentication from authorization and RBAC from ABAC, and design a metrics/logging/tracing setup around SLIs, SLOs, and error budgets rather than raw dashboards.

## Why This Week Matters

Two domains, one week — both are cross-cutting concerns that touch every service built in Weeks 1–8, and both are commonly under-prepared relative to their real interview and production frequency.

## Prerequisites

Week 3 (Spring, for the security filter chain) and Week 2 (JVM, for reading diagnostic output — observability builds directly on that skill).

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | OWASP Top 10 for Backend Services |
| Wed | AuthN vs AuthZ, RBAC vs ABAC |
| Thu–Fri | Performance Methodology (USE/RED) and SLI/SLO/Error Budgets |
| Sat | Logging, Metrics, Tracing, and OpenTelemetry |
| Sun | Review checklist below |

## Required Reading

[`syllabus/12-security/INDEX.md`](../../../syllabus/12-security/INDEX.md) — OWASP Top 10 for Backend Services; AuthN vs AuthZ, RBAC vs ABAC. [`syllabus/13-observability/INDEX.md`](../../../syllabus/13-observability/INDEX.md) — Performance Methodology (USE/RED) and SLI/SLO/Error Budgets; Logging, Metrics, Tracing, and OpenTelemetry.

## Hands-On Exercises

Security: [`practice/java/week-07/security/`](../../../practice/java/week-07/security/). Observability: [`practice/java/week-11/tracing/`](../../../practice/java/week-11/tracing/), `percentiles/`, and [`practice/java/spring/spring-actuator-health-and-observability-hooks/`](../../../practice/java/spring/spring-actuator-health-and-observability-hooks/). Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`actuator-env-exposed-to-the-public-internet-via-a-wildcard-override.md`](../../../production-cookbook/actuator-env-exposed-to-the-public-internet-via-a-wildcard-override.md)
- [`broken-trace-propagation-at-a-library-migration-boundary.md`](../../../production-cookbook/broken-trace-propagation-at-a-library-migration-boundary.md)

## Interview Answer Drills

Answer, out loud: "give a concrete example each of authentication failing versus authorization failing" and "what's the difference between an SLI and an SLO, and how does an error budget change a team's deploy decision?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week (see Week 8).

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described service missing distributed tracing, explain what breaks operationally (as in the trace-propagation cookbook entry) and how you'd instrument it with OpenTelemetry, out loud, in under 5 minutes.

## Review Checklist

- [ ] Completed all four chapters' own L3 Mastery Checklists.
- [ ] Reproduced the security and observability demos listed above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can name at least 5 OWASP Top 10 risks with a concrete mitigation each.
- [ ] Can correctly classify a described access-control failure as an authentication or authorization problem.
- [ ] Can define SLI, SLO, and error budget correctly and explain how they change a deploy decision.

## Retrospective

Note whether the actuator-exposure cookbook entry describes a mistake your own team's configuration could currently make — Spring Boot's sensible-looking defaults are exactly what make this class of misconfiguration common.

## Next Week

[Week 10 — Delivery and Architecture](../week-10/README.md).
