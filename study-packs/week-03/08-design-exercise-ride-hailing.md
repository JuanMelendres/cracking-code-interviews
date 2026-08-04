---
title: "Design Exercise — Ride-Hailing Dispatch System"
week: 3
last_reviewed: 2026-07-29
---

# Design Exercise — Ride-Hailing Dispatch System

**45 minutes, timed, full six-phase method from `03-system-design-method.md`.** Do this yourself, on paper or a whiteboard, before reading the worked notes — they're a calibration reference, not a script to follow.

**Canonical location:** [Architecture Atlas: Ride-Hailing Dispatch System](../../architecture-atlas/ride-hailing-dispatch-system.md). This file is the Week 3 study-pack entry point; the full worked exercise (all six phases, the mermaid diagram, bottleneck mitigations, and Staff-level discussion) is now canonical there.

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

A location-driven matching problem: rider requests a ride, system finds and dispatches a nearby driver, both track it to completion. Payment, surge pricing, and driver onboarding are explicitly out of scope. Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/ride-hailing-dispatch-system.md#problem-statement) and [§ Constraints](../../architecture-atlas/ride-hailing-dispatch-system.md#constraints).

## Phase 2 — Estimate

5M daily riders, 500K daily drivers → ~62,500 peak location-write QPS, ~93 peak ride-request QPS — a ~700x volume gap that drives the architecture split in Phase 5. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/ride-hailing-dispatch-system.md#capacity-assumptions).

## Phase 3 — API

Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/ride-hailing-dispatch-system.md#apis).

## Phase 4 — Data

Rides in PostgreSQL (strong consistency, state machine); driver location in a geospatial store (PostGIS/Redis GEO), not relational, matching the access-pattern method. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/ride-hailing-dispatch-system.md#data-model).

## Phase 5 — Architecture

Location-ingest and ride-request are separate services, justified by the ~700x volume gap from Phase 2. Full diagram and justification: canonical entry [§ Architecture Diagram](../../architecture-atlas/ride-hailing-dispatch-system.md#architecture-diagram).

## Phase 6 — Bottlenecks

Three named with mitigations: geospatial store contention (regional sharding), double-assignment race (atomic conditional update, same anomaly class as SQL write-skew), stale location data (TTL). Full detail: canonical entry [§ Scaling Strategy](../../architecture-atlas/ride-hailing-dispatch-system.md#scaling-strategy) and [§ Reliability Strategy](../../architecture-atlas/ride-hailing-dispatch-system.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45 minutes
- [ ] At least one architectural decision (Phase 5) explicitly traced back to a Phase 2 number
- [ ] At least 3 bottlenecks named with mitigations, not just named
- [ ] The write-skew connection in bottleneck #2 recognized as the same anomaly class from `02-isolation-levels-and-write-skew.md`, not treated as an unrelated new concept
