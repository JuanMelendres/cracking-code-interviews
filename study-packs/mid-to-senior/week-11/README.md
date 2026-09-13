---
title: "Mid → Senior, Week 11 — Software Design and API Design (Advanced)"
document_type: study-pack
week: 11
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 11 — Software Design and API Design (Advanced)

## Weekly Outcome

By the end of this week you can defend a SOLID-principle violation's real production cost with a concrete example (not just the definition), walk through at least one non-trivial object-oriented design problem end to end, and explain — with real, measured evidence — why GraphQL's N+1 problem and gRPC's deadline-less-call risk are structural properties of each protocol, not incidental bugs.

## Why This Week Matters

Two domains, one week — both are cross-cutting design disciplines every service built in Weeks 1–10 already depended on implicitly. `04-software-design` and `07-api-design`'s advanced topics (beyond the REST fundamentals assumed complete from [Junior → Mid](../../junior-to-mid/README.md)) had never been scheduled in this path before a direct front-matter audit found the gap.

## Prerequisites

Week 3 (Spring, for dependency injection, which SOLID's Dependency Inversion Principle formalizes) and Junior → Mid's REST API Fundamentals topic (this week assumes REST basics are already solid and covers only what's beyond them).

## Schedule

| Day | Focus |
|---|---|
| Mon | SOLID Principles |
| Tue | Object-Oriented Design Interview Problems |
| Wed | GraphQL API Design |
| Thu | gRPC API Design |
| Fri | API Design (pagination, versioning) and API Gateway/BFF/Edge Concerns |
| Sat–Sun | Review checklist below |

## Required Reading

[`syllabus/04-software-design/INDEX.md`](../../../syllabus/04-software-design/INDEX.md) — SOLID Principles; Object-Oriented Design Interview Problems. [`syllabus/07-api-design/INDEX.md`](../../../syllabus/07-api-design/INDEX.md) — GraphQL API Design; gRPC API Design; API Design; API Gateway, BFF, and Edge Concerns.

## Hands-On Exercises

SOLID: [`practice/java/solid-principles/`](../../../practice/java/solid-principles/). OOD: [`practice/java/ood-interview-problems/`](../../../practice/java/ood-interview-problems/) (a real, fully-worked Parking Lot and a state-machine-based vending machine). GraphQL: [`practice/java/graphql-api-design/`](../../../practice/java/graphql-api-design/) (real graphql-java 26.1 output). gRPC: [`practice/java/grpc-api-design/`](../../../practice/java/grpc-api-design/) (real protoc-generated client/server on grpc-java 1.68.1). API Design: [`practice/sql/week-04/pagination-lab.sql`](../../../practice/sql/week-04/pagination-lab.sql). API Gateway/BFF: [`practice/java/system-design/api-gateway-bff-and-edge-concerns/`](../../../practice/java/system-design/api-gateway-bff-and-edge-concerns/).

## Production Cookbook Cross-Reference

- [`payment-processor-ocp-violation-breaking-a-neighboring-payment-method.md`](../../../production-cookbook/payment-processor-ocp-violation-breaking-a-neighboring-payment-method.md)
- [`report-generator-dip-violation-blocking-unit-tests-on-a-real-database.md`](../../../production-cookbook/report-generator-dip-violation-blocking-unit-tests-on-a-real-database.md)
- [`vending-machine-double-dispense-from-a-concurrent-duplicate-request.md`](../../../production-cookbook/vending-machine-double-dispense-from-a-concurrent-duplicate-request.md)
- [`graphql-n-plus-one-overloading-a-downstream-author-service.md`](../../../production-cookbook/graphql-n-plus-one-overloading-a-downstream-author-service.md)
- [`deadline-less-grpc-call-cascading-into-thread-pool-exhaustion.md`](../../../production-cookbook/deadline-less-grpc-call-cascading-into-thread-pool-exhaustion.md)
- [`offset-pagination-degrading-an-admin-tool-as-a-table-grows.md`](../../../production-cookbook/offset-pagination-degrading-an-admin-tool-as-a-table-grows.md)
- [`sequential-client-side-fan-out-inflating-mobile-dashboard-latency.md`](../../../production-cookbook/sequential-client-side-fan-out-inflating-mobile-dashboard-latency.md)

## Interview Answer Drills

Answer, out loud: "give a real example of an OCP violation you've seen or can construct, and the concrete fix" and "why is GraphQL's N+1 problem structural rather than a bug, and what's the standard fix" before checking each chapter's expected answer.

## Coding Problems

Work through the OOD chapter's Parking Lot design end to end, extending it for a stated new requirement (e.g., an EV-charging discount) without editing the original `ParkingLot` class — the OCP-in-practice check this chapter's own Production Scenario demonstrates.

## System Design Exercise

Design an API surface for a stated feature (e.g., a social feed) choosing REST, GraphQL, or gRPC explicitly, defending the choice against the other two using each protocol's real trade-offs from this week's chapters, not just familiarity.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described "one team blocked another team's release" or "an unrelated bug fix broke a different feature" scenario, identify which SOLID principle was violated and state the concrete fix, out loud, in under 5 minutes.

## Review Checklist

- [ ] Completed all six chapters' own L3 Mastery Checklists.
- [ ] Reproduced the SOLID, OOD, GraphQL, and gRPC demos listed above.
- [ ] Read all seven cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can name each SOLID principle with a concrete violation-and-fix example, not just a definition.
- [ ] Can walk through the Parking Lot or vending-machine design end to end, naming its extension points.
- [ ] Can explain GraphQL's N+1 problem and its `DataLoader` fix with real measured numbers.
- [ ] Can explain why an internal gRPC call needs an explicit deadline and what happens without one.

## Retrospective

Note whether any service you've worked on has ever accumulated a `PaymentProcessor`-style branching method the way the OCP cookbook entry describes — this is one of the most common real violations, not a contrived teaching example.

## Next Week

[Week 12 — AI/LLM Engineering](../week-12/README.md).
