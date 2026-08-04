---
title: "Design Exercise — Metrics/Monitoring System"
week: 11
last_reviewed: 2026-07-31
---

# Design Exercise — Metrics/Monitoring System

**45 minutes, timed, full six-phase method.** Per `00-project/learning-roadmap.md` §4 Week 11. Do this yourself before reading the worked notes below.

**Canonical location:** [Architecture Atlas: Metrics/Monitoring System](../../architecture-atlas/metrics-monitoring-system.md). This file is the Week 11 study-pack entry point; the full worked exercise is now canonical there.

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

Ingest, aggregate, and alert on time-series metrics — tracing and logs explicitly out of scope. The core tension is write-path volume vs. percentile-query correctness cost. Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/metrics-monitoring-system.md#problem-statement).

## Phase 2 — Estimate

50,000 instances × ~200 series / 10s → 1,000,000 points/s ingested; percentiles can't be correctly averaged from pre-aggregated instance values. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/metrics-monitoring-system.md#capacity-assumptions).

## Phase 3 — API

Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/metrics-monitoring-system.md#apis).

## Phase 4 — Data

Time-range-partitioned hot/cold store; histogram sketches (HdrHistogram/t-digest), not raw samples; alert state in its own low-latency store. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/metrics-monitoring-system.md#data-model).

## Phase 5 — Architecture

Histogram sketches on the write path, a separate alert-evaluation read path, time-based range partitioning (not hash). Full diagram and justification: canonical entry [§ Architecture Diagram](../../architecture-atlas/metrics-monitoring-system.md#architecture-diagram).

## Phase 6 — Bottlenecks

Ingestion-tier batching, cardinality explosion (a distinct failure mode from volume), and monitoring the alert evaluator itself — three named with mitigations. Full detail: canonical entry [§ Reliability Strategy](../../architecture-atlas/metrics-monitoring-system.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45 minutes
- [ ] Histogram-sketch storage justified explicitly against `03`'s percentile-computation lesson, not just asserted as a design choice
- [ ] Time-based range partitioning chosen and explicitly contrasted with Week 10's hash-partitioned example — the SAME topic (partitioning), a DIFFERENT correct choice for this system's actual access pattern
- [ ] Cardinality explosion named as a distinct failure mode from raw ingestion volume, with a different fix (bounded tags, not more scaling)
