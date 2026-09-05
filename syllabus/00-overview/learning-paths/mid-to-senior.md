---
title: "Learning Path: Mid → Senior"
document_type: learning-path
status: draft
version: 1.0
last_updated: 2026-09-05
source: 00-project/syllabus-transformation-plan.md §6
---

# Learning Path: Mid → Senior

**Audience:** a working engineer (roughly 2–5 years) who can ship features correctly but hasn't yet built the internals depth, production-debugging instinct, and trade-off vocabulary a Senior loop actually tests.

**Goal:** reach L3 (Senior) — internals, performance reasoning, and production debugging — across the domains below.

**Time budget:** ~10 weeks, part-time (6–10 hours/week).

**Stops at:** L3 for every domain listed. A handful of topics are called out explicitly per domain as the highest-priority starting point; each domain's own `INDEX.md` is the exhaustive topic list — this path sequences *domains*, in order, rather than re-listing every topic inside them (a learning path never repeats topic content, per this project's own duplication rule).

## Sequence

| # | Domain | Priority topics to start with | Stop at |
|---|---|---|---|
| 1 | [Java — Concurrency](../../02-java/INDEX.md) | Java Memory Model and volatile; Executors and Thread Pool Sizing; Deadlock, Race Conditions, and Thread Diagnostics | L3 |
| 2 | [Java — JVM Internals](../../02-java/INDEX.md) | GC Fundamentals and Log Analysis; Escape Analysis and Scalar Replacement | L3 |
| 3 | [Spring](../../05-spring/INDEX.md) | Spring @Transactional: Proxy Mechanics, Rollback Rules, and Propagation; Spring Security Filter Chain | L3 |
| 4 | [Databases](../../06-databases/INDEX.md) | Query Planning and EXPLAIN ANALYZE; Isolation Levels and Concurrency Anomalies; MVCC in PostgreSQL, Vacuum, and Bloat | L3 |
| 5 | [Testing](../../08-testing/INDEX.md) | Integration Testing Against Real Dependencies; Contract Testing for Services | L3 |
| 6 | [Messaging & Event-Driven Systems](../../09-messaging-event-driven/INDEX.md) | Kafka Architecture Fundamentals; Kafka Delivery Semantics and Exactly-Once Processing | L3 |
| 7 | [Distributed Systems](../../10-distributed-systems/INDEX.md) | CAP Theorem and Consistency Models; Distributed Systems Failure Modes | L3 |
| 8 | [System Design](../../11-system-design/INDEX.md) | System Design Method and Estimation; Caching Strategies and Invalidation; Resilience Patterns | L3 |
| 9 | [Security](../../12-security/INDEX.md) | OWASP Top 10 for Backend Services; AuthN vs AuthZ, RBAC vs ABAC | L3 |
| 10 | [Observability](../../13-observability/INDEX.md) | Performance Methodology (USE/RED) and SLI/SLO/Error Budgets; Logging, Metrics, Tracing, and OpenTelemetry | L3 |
| 11 | [DevOps & Containers](../../14-devops-containers/INDEX.md) | Kubernetes Objects, Scheduling, and Networking; Kubernetes Resource Limits, Probes, and JVM Sizing | L3 |
| 12 | [Architecture](../../17-architecture/INDEX.md) | Clean and Hexagonal Architecture; DDD Tactical Design — Aggregates | L3 |

## Pair every domain with real production debugging

This path's own differentiator over reading alone: for each domain above, cross-reference the matching [`production-cookbook/`](../../../production-cookbook/README.md) entries once that domain's chapters are read — the cookbook's entries are written against real, already-diagnosed incidents at exactly this depth, and are the closest thing this program has to a debug-it verification exercise (see the [Mastery Model](../mastery-model.md)'s five verification forms).

## Completion criteria

- Can explain each priority topic's internals from memory and reason about a described performance symptom to a root cause, per each chapter's own L3 Mastery Checklist.
- Has read at least one matching `production-cookbook/` entry per domain and can restate its diagnosis without looking.
- Can compare named alternatives within each domain (e.g., ReentrantLock vs. synchronized, SSR vs. SSG-equivalent trade-offs, Kafka vs. a simpler queue) and defend the choice for a stated scenario.

## Next

[Senior → Staff](senior-to-staff.md) — the direct continuation into L4 systemic and organizational judgment.
