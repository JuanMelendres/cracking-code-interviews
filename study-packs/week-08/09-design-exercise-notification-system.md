---
title: "Design Exercise — Notification System"
week: 8
last_reviewed: 2026-07-31
---

# Design Exercise — Notification System

**45 minutes, timed, full six-phase method.** Per `00-project/learning-roadmap.md` §4 Week 8. Do this yourself before reading the worked notes below.

**Canonical location:** [Architecture Atlas: Notification System](../../architecture-atlas/notification-system.md). This file is the Week 8 study-pack entry point; the full worked exercise is now canonical there.

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

Accept an event, fan out to enabled channels, respect preferences and rate limits — a fan-out and delivery-guarantee problem, not a storage problem. Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/notification-system.md#problem-statement).

## Phase 2 — Estimate

20M DAU, 3 events/user/day → ~2,100 peak events/s, up to 6,300 peak delivery attempts/s across 3 channels. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/notification-system.md#capacity-assumptions).

## Phase 3 — API

Fire-and-forget from the caller's perspective. Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/notification-system.md#apis).

## Phase 4 — Data

Preferences cached aggressively; append-only delivery log as both status source and idempotency boundary. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/notification-system.md#data-model).

## Phase 5 — Architecture

`userId` partition key throughout, per-channel topics downstream of one fan-out consumer, at-least-once delivery with the delivery log as the idempotency check. Full diagram and justification: canonical entry [§ Architecture Diagram](../../architecture-atlas/notification-system.md#architecture-diagram).

## Phase 6 — Bottlenecks

Hot event type (not hot partition), preference-DB read load, consumer lag as a per-channel SLO — three named with mitigations. Full detail: canonical entry [§ Reliability Strategy](../../architecture-atlas/notification-system.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45 minutes
- [ ] Partition key choice (`userId`) justified explicitly against a concrete ordering requirement, not asserted
- [ ] At-least-once delivery chosen deliberately, with the idempotency mechanism that makes it safe named explicitly
- [ ] Distinguished the "hot event type" scenario from the "hot partition" scenario from `01-kafka-architecture-fundamentals.md` — they are not the same failure mode
