---
title: "Design Exercise — News Feed"
week: 4
last_reviewed: 2026-07-29
---

# Design Exercise — News Feed

**45 minutes, timed, full six-phase method.** Caching and fan-out are mandatory discussion points. Do this yourself before reading the worked notes below.

**Canonical location:** [Architecture Atlas: News Feed System](../../architecture-atlas/news-feed-system.md). This file is the Week 4 study-pack entry point; the full worked exercise is now canonical there.

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

Follow relationships and a reverse-chronological feed, overwhelmingly read-heavy. Ranking, ads, and comments explicitly out of scope. Full statement: canonical entry [§ Problem Statement](../../architecture-atlas/news-feed-system.md#problem-statement).

## Phase 2 — Estimate

50M DAU, 20 feed views/day → ~34,700 peak read QPS vs. ~58 average write QPS — a ~200:1 read:write ratio, the number that justifies caching. Full worked math: canonical entry [§ Capacity Assumptions](../../architecture-atlas/news-feed-system.md#capacity-assumptions).

## Phase 3 — API

Full endpoint set: canonical entry [§ APIs](../../architecture-atlas/news-feed-system.md#apis).

## Phase 4 — Data

Posts relational/wide-column; follow graph access-pattern-driven; feed cache stores post IDs only. Full reasoning: canonical entry [§ Data Model](../../architecture-atlas/news-feed-system.md#data-model).

## Phase 5 — Architecture

Fan-out-on-write for most users, fan-out-on-read for the celebrity case, split by follower count. Full diagram and justification: canonical entry [§ Architecture Diagram](../../architecture-atlas/news-feed-system.md#architecture-diagram).

## Phase 6 — Bottlenecks

Celebrity fan-out cost, feed-cache stampede, deep pagination — three named with mitigations. Full detail: canonical entry [§ Reliability Strategy](../../architecture-atlas/news-feed-system.md#reliability-strategy).

## Exit check

- [ ] All six phases completed within 45 minutes
- [ ] Fan-out discussed explicitly, including the celebrity-case trade-off (fan-out-on-write vs. fan-out-on-read), not just one approach
- [ ] Caching discussed and traced back to the Phase 2 read:write ratio
- [ ] At least one bottleneck explicitly connects to a specific mechanism from this week's chapters (stampede, pagination) rather than a generic "add more caching"
