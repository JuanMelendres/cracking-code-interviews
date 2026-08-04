---
title: "Design Exercise — Distributed Cache"
week: 10
last_reviewed: 2026-07-31
---

# Design Exercise — Distributed Cache

**45-60 minutes, timed, full six-phase method.** Per `00-project/learning-roadmap.md` §4 Week 10. Do this yourself before reading the worked notes below.

**Canonical location:** [Architecture Atlas: Distributed Cache](../../architecture-atlas/distributed-cache.md). This file is the Week 10 study-pack entry point; the full worked exercise is now canonical there.

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

A sharded key-value cache in front of a slower source-of-truth database. The central tension: fall through to the database on a node failure (safe, latency spike) or serve stale/nothing (fast, wrong data). Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/distributed-cache.md#problem-statement).

## Phase 2 — Estimate

500K reads/s peak, 95% hit rate → cache serves ~475K/s, DB serves ~25K/s — the 20x ratio that justifies the cache's existence. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/distributed-cache.md#capacity-assumptions).

## Phase 3 — API

Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/distributed-cache.md#apis).

## Phase 4 — Data

In-memory LRU with TTL per node, no persistence; routing-layer ring ownership must be consistently known by every client. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/distributed-cache.md#data-model).

## Phase 5 — Architecture

Consistent hashing with virtual nodes, per-node circuit breakers (not shared), always fall through on miss/downed node. Full diagram and justification: canonical entry [§ Architecture Diagram](../../architecture-atlas/distributed-cache.md#architecture-diagram).

## Phase 6 — Bottlenecks

Thundering herd on node failure, hot keys, cache stampede — three distinct failure shapes named with mitigations. Full detail: canonical entry [§ Reliability Strategy](../../architecture-atlas/distributed-cache.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45-60 minutes
- [ ] Consistent hashing's measured redistribution number (not just its name) used to justify the node-failure blast-radius claim in Phase 6.1
- [ ] Per-node (not shared) circuit breakers justified explicitly against the bulkhead principle from `04-resilience-patterns.md`
- [ ] All three Phase 6 bottlenecks correctly distinguished as different failure shapes (node failure vs hot key vs stampede), not conflated into one generic "cache can get overloaded" statement
