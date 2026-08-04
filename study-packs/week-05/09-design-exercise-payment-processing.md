---
title: "Design Exercise — Payment Processing System"
week: 5
last_reviewed: 2026-07-29
---

# Design Exercise — Payment Processing System

**45 minutes, timed, full six-phase method.** Idempotency and exactly-once semantics are mandatory discussion points. Do this yourself before reading the worked notes below.

**Canonical location:** [Architecture Atlas: Payment Processing System](../../architecture-atlas/payment-processing-system.md). This file is the Week 5 study-pack entry point; the full worked exercise is now canonical there.

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

Accept, charge, record, notify — fraud detection, currency conversion, and refunds explicitly out of scope. Low volume, high financial consequence per request. Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/payment-processing-system.md#problem-statement).

## Phase 2 — Estimate

500,000 payments/day → ~17 peak QPS, a low-QPS/high-consequence system that inverts the usual scale-driven architecture pattern. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/payment-processing-system.md#capacity-assumptions).

## Phase 3 — API

Idempotency key in the API contract from the start, not retrofitted. Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/payment-processing-system.md#apis).

## Phase 4 — Data

Payments table and idempotency-keys table both CP, strongly consistent. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/payment-processing-system.md#data-model).

## Phase 5 — Architecture

The idempotency-key insert-or-read-stored-result sequence. Full diagram: canonical entry [§ Architecture Diagram](../../architecture-atlas/payment-processing-system.md#architecture-diagram).

## Phase 6 — Bottlenecks

Slow/degraded provider, the honest limits of end-to-end "exactly-once," and the idempotency store as a new CP single point of failure — three named with mitigations. Full detail: canonical entry [§ Reliability Strategy](../../architecture-atlas/payment-processing-system.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45 minutes
- [ ] Idempotency mechanism designed in from Phase 3 (API), not added as an afterthought in Phase 5
- [ ] The limitation of "exactly-once" (dependent on the external provider also supporting idempotent requests) stated explicitly, not claimed as an unqualified guarantee
- [ ] CAP/consistency choice for the payments and idempotency data explicitly justified as CP, connecting back to `03-cap-and-consistency.md`
