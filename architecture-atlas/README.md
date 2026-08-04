---
title: "Architecture Atlas — Index"
document_type: architecture-atlas-index
status: draft
last_updated: 2026-08-04
---

# Architecture Atlas

The Architecture Atlas owns full system-design reference entries per `CLAUDE.md`'s Architecture Atlas Standard: problem statement, constraints, functional/non-functional requirements, capacity assumptions, architecture diagram, data model, APIs, request flow, consistency model, scaling strategy, reliability strategy, security/observability/cost, trade-offs, alternatives, migration path, Staff-level discussion, and interview presentation sequence — each entry referencing detailed canonical `handbook/` chapters rather than restating their explanations.

## A note on scope

This programme's study packs contain 13 real, worked "design exercise" files across Weeks 3–19, each a timed application of [System Design Method and Estimation](../handbook/system-design/system-design-method-and-estimation.md)'s six-phase method (or a domain-specific variant of it) to a concrete problem. The classic system-design-style exercises — full request/response systems with an architecture diagram, data model, and scaling story — are the natural elevation candidates for this Atlas: `ride-hailing dispatch` (Week 3, elevated), `news feed` (Week 4, elevated), `payment processing` (Week 5, elevated), `authentication service` (Week 7, elevated), `notification system` (Week 8), `distributed job scheduler` (Week 9), `distributed cache` (Week 10), `metrics/monitoring system` (Week 11). The later weeks' exercises (deployment infrastructure, JVM sizing/tuning playbooks, a security review, a test-strategy design) are differently shaped — domain-specific design/review exercises rather than classic system-design problems — and are lower priority for this specific deliverable.

## Entries

| Entry | Companion topic | What it's about |
|---|---|---|
| [Ride-Hailing Dispatch System](ride-hailing-dispatch-system.md) | T-801/T-802 (System Design Method and Estimation) | A location-driven matching system: the central design tension is that driver location (high write volume, staleness-tolerant) and ride state (low volume, correctness-critical) need different storage and consistency treatment, split along that line rather than as an afterthought. Elevated from `study-packs/week-03/08-design-exercise-ride-hailing.md`. |
| [News Feed System](news-feed-system.md) | Caching Strategies and Invalidation | A read-heavy fan-out system: the 200:1 read:write ratio justifies precomputing feeds at write time for most users, but a per-author split (fan-out-on-write vs. fan-out-on-read for the celebrity case) is needed to avoid a catastrophic write spike from a power-law-distributed follower count. Elevated from `study-packs/week-04/08-design-exercise-news-feed.md`. |
| [Payment Processing System](payment-processing-system.md) | Idempotency at System Edges / CAP Theorem | A deliberately low-QPS, high-consequence system where correctness — not throughput — is the binding constraint: idempotency is designed into the API contract from the start, and the honest limits of end-to-end "exactly-once" (dependent on the external provider) are stated explicitly rather than claimed as an unqualified guarantee. Elevated from `study-packs/week-05/09-design-exercise-payment-processing.md`. |
| [Authentication Service](authentication-service.md) | OAuth2, OIDC, and JWT | A ~700x asymmetry between token issuance and token validation drives the whole design: validation happens via local JWT signature verification with no call back to the Auth Service, trading instant revocation for elimination of a synchronous, availability-critical dependency at the platform's highest-traffic path. Elevated from `study-packs/week-07/08-design-exercise-authentication-service.md`. |

## How this relates to other deliverables

- `handbook/` — canonical explanations of individual concepts (storage selection, partitioning, isolation levels) that a full system design draws on; this Atlas references them rather than re-teaching them.
- `interview-playbook/system-design/` — the live-delivery discipline (time-boxing, handling mid-round changes) for running a design exercise like these under interview conditions.
- `cheat-sheets/` — one-page rapid review of a single canonical chapter's content, not a full worked system.
