---
title: "Mid → Senior, Week 8 — System Design"
document_type: study-pack
week: 8
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 8 — System Design

## Weekly Outcome

By the end of this week you can run a system-design interview's estimation phase (traffic, storage, bandwidth) without freezing, choose a caching and invalidation strategy with a stated trade-off, and apply at least two resilience patterns (circuit breaker, bulkhead) to a described service.

## Why This Week Matters

This week is where Weeks 1–7's internals knowledge (concurrency, JVM, Spring, databases, testing, Kafka, distributed systems) gets assembled into the interview format that actually tests it end to end — a system-design interview draws on nearly every domain covered so far.

## Prerequisites

Weeks 1–7 — this week assumes, rather than reteaches, concurrency, database, and distributed-systems fundamentals.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | System Design Method and Estimation |
| Wed–Thu | Caching Strategies and Invalidation |
| Fri–Sat | Resilience Patterns |
| Sun | Review checklist below |

## Required Reading

[`syllabus/11-system-design/INDEX.md`](../../../syllabus/11-system-design/INDEX.md) — this week's three priority topics (System Design Method and Estimation; Caching Strategies and Invalidation; Resilience Patterns).

## Hands-On Exercises

Real demos exist under [`practice/java/system-design/`](../../../practice/java/system-design/) — `load-balancing-and-health-checking/`, `rate-limiting-and-throttling/`, `api-gateway-bff-and-edge-concerns/`, `realtime-delivery-websocket-sse-long-poll/`, `search-and-indexing-systems/`, `twelve-factor-config/`. Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`naive-hash-mod-n-cache-scaling-causing-a-database-overload.md`](../../../production-cookbook/naive-hash-mod-n-cache-scaling-causing-a-database-overload.md)
- [`cache-cluster-failover-triggering-a-full-database-outage.md`](../../../production-cookbook/cache-cluster-failover-triggering-a-full-database-outage.md)

## Interview Answer Drills

Answer, out loud: "walk through your estimation for a system handling 10M daily active users, start to finish" and "when would you choose write-through versus write-behind caching, and what does each cost you on failure?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

Full mock: design a URL shortener or a rate limiter end to end (functional/non-functional requirements, estimation, data model, API, caching strategy, resilience patterns), timed at 45 minutes, narrated out loud as if to an interviewer.

## Behavioral Exercise

None this week.

## Mock Interview

Use the System Design Exercise above as this week's mock — score yourself against the System Design Method chapter's own rubric if it has one, otherwise self-assess against its stated interview-answer framework.

## Review Checklist

- [ ] Completed all three chapters' own L3 Mastery Checklists.
- [ ] Reproduced at least 2 of the real system-design demos listed above.
- [ ] Completed the 45-minute full mock design exercise.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Ran the estimation phase for a system unaided, arriving at a defensible order-of-magnitude answer.
- [ ] Chose and justified a caching strategy for the mock design, including its failure behavior.
- [ ] Applied at least two resilience patterns correctly in the mock design.

## Retrospective

Note where the 45-minute mock ran over time — estimation and caching trade-offs are the two phases candidates most commonly under-practice, so if either felt rushed, that's the one to redo before Week 9.

## Next Week

[Week 9 — Security and Observability](../week-09/README.md).
