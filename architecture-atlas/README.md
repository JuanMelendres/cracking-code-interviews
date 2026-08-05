---
title: "Architecture Atlas — Index"
document_type: architecture-atlas-index
status: draft
last_updated: 2026-08-04
---

# Architecture Atlas

The Architecture Atlas owns full system-design reference entries per `CLAUDE.md`'s Architecture Atlas Standard: problem statement, constraints, functional/non-functional requirements, capacity assumptions, architecture diagram, data model, APIs, request flow, consistency model, scaling strategy, reliability strategy, security/observability/cost, trade-offs, alternatives, migration path, Staff-level discussion, and interview presentation sequence — each entry referencing detailed canonical `handbook/` chapters rather than restating their explanations.

## A note on scope

This programme's study packs contain 13 real, worked "design exercise" files across Weeks 3–19, each a timed application of [System Design Method and Estimation](../handbook/system-design/system-design-method-and-estimation.md)'s six-phase method (or a domain-specific variant of it) to a concrete problem. All 8 classic system-design-style exercises — full request/response systems with an architecture diagram, data model, and scaling story — are now **fully elevated**: `ride-hailing dispatch` (Week 3), `news feed` (Week 4), `payment processing` (Week 5), `authentication service` (Week 7), `notification system` (Week 8), `distributed job scheduler` (Week 9), `distributed cache` (Week 10), `metrics/monitoring system` (Week 11). The remaining 5 weeks' exercises (deployment infrastructure, JVM sizing/tuning playbooks, a security review, a test-strategy design — Weeks 15–19) are differently shaped — domain-specific design/review exercises rather than classic system-design problems — and are lower priority for this deliverable.

## Entries

| Entry | Companion topic | What it's about |
|---|---|---|
| [Ride-Hailing Dispatch System](ride-hailing-dispatch-system.md) | T-801/T-802 (System Design Method and Estimation) | A location-driven matching system: the central design tension is that driver location (high write volume, staleness-tolerant) and ride state (low volume, correctness-critical) need different storage and consistency treatment, split along that line rather than as an afterthought. Elevated from `study-packs/week-03/08-design-exercise-ride-hailing.md`. |
| [News Feed System](news-feed-system.md) | Caching Strategies and Invalidation | A read-heavy fan-out system: the 200:1 read:write ratio justifies precomputing feeds at write time for most users, but a per-author split (fan-out-on-write vs. fan-out-on-read for the celebrity case) is needed to avoid a catastrophic write spike from a power-law-distributed follower count. Elevated from `study-packs/week-04/08-design-exercise-news-feed.md`. |
| [Payment Processing System](payment-processing-system.md) | Idempotency at System Edges / CAP Theorem | A deliberately low-QPS, high-consequence system where correctness — not throughput — is the binding constraint: idempotency is designed into the API contract from the start, and the honest limits of end-to-end "exactly-once" (dependent on the external provider) are stated explicitly rather than claimed as an unqualified guarantee. Elevated from `study-packs/week-05/09-design-exercise-payment-processing.md`. |
| [Authentication Service](authentication-service.md) | OAuth2, OIDC, and JWT | A ~700x asymmetry between token issuance and token validation drives the whole design: validation happens via local JWT signature verification with no call back to the Auth Service, trading instant revocation for elimination of a synchronous, availability-critical dependency at the platform's highest-traffic path. Elevated from `study-packs/week-07/08-design-exercise-authentication-service.md`. |
| [Notification System](notification-system.md) | Kafka Delivery Semantics and Exactly-Once | A fan-out and delivery-guarantee problem, not a storage problem: `userId` partitioning preserves per-user ordering, per-channel topics isolate a degraded provider from the others, and at-least-once delivery plus a delivery-log idempotency check trades an occasional duplicate for never silently dropping a notification. A "hot event type" is explicitly distinguished from a "hot partition" as a different failure mode. Elevated from `study-packs/week-08/09-design-exercise-notification-system.md`. |
| [Distributed Job Scheduler](distributed-job-scheduler.md) | Executors and Thread Pool Sizing | A p50-vs-p99 job-duration spread (200ms vs. 30s) drives a two-pool worker architecture; lease-based claiming (not a distributed lock) self-heals on crash without a failure-detection mechanism. Elevated from `study-packs/week-09/09-design-exercise-distributed-job-scheduler.md`. |
| [Distributed Cache](distributed-cache.md) | Data Partitioning and Consistent Hashing / Resilience Patterns | A sharded key-value cache where consistent hashing's measured ~9.2% (vs. ~92.5% naive) remap cost is what makes node scaling routine, and three distinct failure shapes (node failure, hot keys, stampede) each need their own specific mitigation rather than one generic fix. Elevated from `study-packs/week-10/10-design-exercise-distributed-cache.md`. |
| [Metrics/Monitoring System](metrics-monitoring-system.md) | Percentiles, Tail Latency, and Coordinated Omission | A monitoring system whose own write path (1M points/s) forces histogram sketches instead of raw samples, and whose alert-evaluation path must be architecturally isolated from dashboard queries — plus a cardinality explosion, distinguished explicitly as a different failure mode from raw volume with a different fix. Elevated from `study-packs/week-11/09-design-exercise-metrics-monitoring-system.md`. |

## How this relates to other deliverables

- `handbook/` — canonical explanations of individual concepts (storage selection, partitioning, isolation levels) that a full system design draws on; this Atlas references them rather than re-teaching them.
- `interview-playbook/system-design/` — the live-delivery discipline (time-boxing, handling mid-round changes) for running a design exercise like these under interview conditions.
- `cheat-sheets/` — one-page rapid review of a single canonical chapter's content, not a full worked system.
