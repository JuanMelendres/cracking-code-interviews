---
title: "Architecture Atlas — Index"
document_type: architecture-atlas-index
status: draft
last_updated: 2026-08-04
---

# Architecture Atlas

The Architecture Atlas owns full system-design reference entries per `CLAUDE.md`'s Architecture Atlas Standard: problem statement, constraints, functional/non-functional requirements, capacity assumptions, architecture diagram, data model, APIs, request flow, consistency model, scaling strategy, reliability strategy, security/observability/cost, trade-offs, alternatives, migration path, Staff-level discussion, and interview presentation sequence — each entry referencing detailed canonical `handbook/` chapters rather than restating their explanations.

## A note on scope

This programme's study packs contain 13 real, worked "design exercise" files across Weeks 3–19, each a timed application of [System Design Method and Estimation](../handbook/system-design/system-design-method-and-estimation.md)'s six-phase method (or a domain-specific variant of it) to a concrete problem. The classic system-design-style exercises — full request/response systems with an architecture diagram, data model, and scaling story — are the natural elevation candidates for this Atlas: `ride-hailing dispatch` (Week 3, elevated), `news feed` (Week 4), `payment processing` (Week 5), `authentication service` (Week 7), `notification system` (Week 8), `distributed job scheduler` (Week 9), `distributed cache` (Week 10), `metrics/monitoring system` (Week 11). The later weeks' exercises (deployment infrastructure, JVM sizing/tuning playbooks, a security review, a test-strategy design) are differently shaped — domain-specific design/review exercises rather than classic system-design problems — and are lower priority for this specific deliverable.

## Entries

| Entry | Companion topic | What it's about |
|---|---|---|
| [Ride-Hailing Dispatch System](ride-hailing-dispatch-system.md) | T-801/T-802 (System Design Method and Estimation) | A location-driven matching system: the central design tension is that driver location (high write volume, staleness-tolerant) and ride state (low volume, correctness-critical) need different storage and consistency treatment, split along that line rather than as an afterthought. Elevated from `study-packs/week-03/08-design-exercise-ride-hailing.md`, the first fully-worked design exercise in the programme. |

## How this relates to other deliverables

- `handbook/` — canonical explanations of individual concepts (storage selection, partitioning, isolation levels) that a full system design draws on; this Atlas references them rather than re-teaching them.
- `interview-playbook/system-design/` — the live-delivery discipline (time-boxing, handling mid-round changes) for running a design exercise like these under interview conditions.
- `cheat-sheets/` — one-page rapid review of a single canonical chapter's content, not a full worked system.
