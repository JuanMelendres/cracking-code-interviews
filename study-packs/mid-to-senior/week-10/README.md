---
title: "Mid → Senior, Week 10 — Delivery and Architecture"
document_type: study-pack
week: 10
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 10 — Delivery and Architecture

## Weekly Outcome

By the end of this week you can explain how Kubernetes schedules a pod and what a readiness/liveness probe actually protects against, size JVM memory correctly against a container's resource limits, and defend a Clean/Hexagonal Architecture boundary and a tactical DDD aggregate design for a stated domain.

## Why This Week Matters

This closing week pairs how an application ships and runs (Kubernetes) with how it's structured internally (architecture) — the natural conclusion to a pack that started with in-process concurrency and ends with how services are deployed and organized at scale.

## Prerequisites

[Junior → Mid](../../junior-to-mid/README.md)'s Docker Fundamentals week, and this pack's Week 2 (JVM) for correctly sizing heap against container limits.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | Kubernetes Objects, Scheduling, and Networking |
| Wed–Thu | Kubernetes Resource Limits, Probes, and JVM Sizing |
| Fri | Clean and Hexagonal Architecture |
| Sat | DDD Tactical Design — Aggregates |
| Sun | Review checklist below, then the full-pack retrospective |

## Required Reading

[`syllabus/14-devops-containers/INDEX.md`](../../../syllabus/14-devops-containers/INDEX.md) — Kubernetes Objects, Scheduling, and Networking; Kubernetes Resource Limits, Probes, and JVM Sizing. [`syllabus/17-architecture/INDEX.md`](../../../syllabus/17-architecture/INDEX.md) — Clean and Hexagonal Architecture; DDD Tactical Design — Aggregates.

## Hands-On Exercises

Kubernetes: `practice/k8s/week-15/`, building on [Junior → Mid](../../junior-to-mid/README.md)'s Docker Fundamentals demo. Architecture: real demos under [`practice/java/architecture/`](../../../practice/java/architecture/) — `ddd-bounded-contexts-and-context-mapping/`, `modular-monolith-boundary-enforcement/`, `event-sourcing-and-its-real-costs/`, `cqrs-read-write-separation/`, `strangler-fig-and-migration-patterns/`, `technical-debt-and-evolutionary-architecture/`, `event-driven-integration-styles/`. Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`kubernetes-oomkill-with-no-application-logs.md`](../../../production-cookbook/kubernetes-oomkill-with-no-application-logs.md)
- [`eroded-hexagonal-boundary-blowing-up-a-migration-estimate.md`](../../../production-cookbook/eroded-hexagonal-boundary-blowing-up-a-migration-estimate.md)

## Interview Answer Drills

Answer, out loud: "why would a JVM get OOMKilled by Kubernetes even though `-Xmx` is set below the container's memory limit?" and "what makes a domain boundary a genuine aggregate versus just a convenient grouping of tables?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week (see Week 8).

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: explain the OOMKill cookbook scenario's root cause (JVM overhead outside the heap — metaspace, thread stacks, direct buffers — not counted against `-Xmx`) on a whiteboard, then check against the chapter's own Whiteboard Explanation section.

## Review Checklist

- [ ] Completed all four chapters' own L3 Mastery Checklists.
- [ ] Reproduced at least 2 of the real architecture demos and the Kubernetes demo.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can explain why a JVM's actual memory footprint exceeds `-Xmx` and how to size a container limit correctly.
- [ ] Can defend a hexagonal architecture boundary for a stated domain and explain what erodes it over time.
- [ ] Can design a DDD aggregate's consistency boundary for a stated domain, correctly.

## Retrospective

This is the pack's final retrospective: review every prior week's retrospective note and confirm each is now resolved — concurrency, JVM, Spring proxies, database internals, testing discipline, Kafka semantics, distributed failure modes, system design, security/observability, and now delivery/architecture — before moving on to [Senior → Staff](../../../syllabus/00-overview/learning-paths/senior-to-staff.md).

## Next Week

This is the last week of the Mid → Senior pack. Continue with [Senior → Staff](../../../syllabus/00-overview/learning-paths/senior-to-staff.md).
