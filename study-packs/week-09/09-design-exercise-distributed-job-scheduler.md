---
title: "Design Exercise — Distributed Job Scheduler"
week: 9
last_reviewed: 2026-07-31
---

# Design Exercise — Distributed Job Scheduler

**45 minutes, timed, full six-phase method.** Per `00-project/learning-roadmap.md` §4 Week 9. Do this yourself before reading the worked notes below.

**Canonical location:** [Architecture Atlas: Distributed Job Scheduler](../../architecture-atlas/distributed-job-scheduler.md). This file is the Week 9 study-pack entry point; the full worked exercise is now canonical there.

## Table of Contents

1. [Phase 1 — Clarify](#phase-1--clarify)
2. [Phase 2 — Estimate](#phase-2--estimate)
3. [Phase 3 — API](#phase-3--api)
4. [Phase 4 — Data](#phase-4--data)
5. [Phase 5 — Architecture](#phase-5--architecture)
6. [Phase 6 — Bottlenecks](#phase-6--bottlenecks)
7. [Exit check](#exit-check)

---

## Phase 1 — Clarify

Schedule and execute jobs at-least-once, supporting cancellation and retries — the system itself must decide when work becomes due, unlike an event-reactive system. Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/distributed-job-scheduler.md#problem-statement).

## Phase 2 — Estimate

5M jobs/day → ~230 peak QPS; the real driver is the p50 (~200ms) vs. p99 (~30s) execution-time spread. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/distributed-job-scheduler.md#capacity-assumptions).

## Phase 3 — API

Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/distributed-job-scheduler.md#apis).

## Phase 4 — Data

Job definitions with `lockedBy`/`lockedUntil` lease claiming; append-only execution history as the idempotency boundary. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/distributed-job-scheduler.md#data-model).

## Phase 5 — Architecture

Two execution pools split by job duration; lease-based claiming instead of a distributed lock; conditional-update claiming instead of leader election. Full diagram and justification: canonical entry [§ Architecture Diagram](../../architecture-atlas/distributed-job-scheduler.md#architecture-diagram).

## Phase 6 — Bottlenecks

Due-job query contention, lease-duration trade-off, silent recurring-reenqueue failure — three named with mitigations. Full detail: canonical entry [§ Reliability Strategy](../../architecture-atlas/distributed-job-scheduler.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45 minutes
- [ ] The p50/p99 execution-time spread named explicitly in Phase 2 and traced through to the two-pool architecture decision in Phase 5
- [ ] Lease-based claiming chosen deliberately over a distributed lock, with the specific failure mode it avoids (crashed lock holder) stated
- [ ] Practiced the mid-round-change response from `08-week-9-checkpoint.md` Round 3: given "jobs now need exactly-once execution," revise Phase 4's idempotency boundary rather than bolting on a patch
