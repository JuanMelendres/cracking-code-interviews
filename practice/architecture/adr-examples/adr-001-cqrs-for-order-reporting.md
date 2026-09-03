---
title: "ADR-001: Adopt CQRS for the Order Spend Reporting Read Path"
status: accepted
date: 2026-08-25
deciders: [platform-architecture-group]
---

# ADR-001: Adopt CQRS for the Order Spend Reporting Read Path

> **Representative scenario.** This ADR is a worked example, not a record of a real company decision. Every number it cites is real, measured evidence from this repository's own [CQRS: Read/Write Separation](../../../syllabus/17-architecture/cqrs-read-write-separation.md) chapter and its [practice code](../../java/architecture/cqrs-read-write-separation/README.md) — the decision itself is a representative scenario built to demonstrate what a fully-worked, evidence-grounded ADR looks like.

## Status

Accepted

## Context

The "total spend per customer" report, computed by re-summing every order's line
items against the normalized write schema on every request, is degrading as order
volume grows and has begun timing out during peak reporting windows. The write
schema's normalization — correct and necessary for enforcing order-total invariants —
is structurally the wrong shape for a query that needs an aggregate across tens of
thousands of rows on every request.

## Decision Drivers

- Real, measured query cost: walking the normalized write model for this report took
  15.84ms on 50,000 orders (300,000 events); the report needs to stay well under
  50ms at 10x that volume, which the write-model path will not do.
- The reporting team cannot tolerate more than a few seconds of staleness on this
  specific report — it is not used for any real-time decision.
- The platform team's on-call rotation already owns one async pipeline (the outbox
  publisher); adding a second is a real, bounded operational cost, not a novel one.

## Considered Options

### Option A: Add a database index to speed up the existing query

**Pros:**
- No new infrastructure, no new consistency model to reason about.

**Cons:**
- Does not change the fundamental shape problem: the query still re-derives an
  aggregate across every order and every item on every request. An index reduces
  constant-factor cost, not the structural one.

### Option B: A read replica serving the same query

**Pros:**
- Offloads read load from the primary with no application-level change.

**Cons:**
- Does not change the query's shape or cost — the same expensive aggregation now
  just runs on a different node.

### Option C: CQRS — a projected, denormalized `CustomerSpendView` read model

**Pros:**
- Real, measured 4.6–5.4x speedup (15.84ms → 3.45ms on the same 50,000-order
  dataset) by precomputing the aggregate at write time instead of read time.
- The read model can be rebuilt from the existing domain events at any time — no new
  source of truth is introduced.

**Cons:**
- Introduces a real, measured eventual-consistency window (this repository's own
  demo measured a real p50 of 1.5µs best-case, in-process; a production message
  broker would show a larger, but still real and boundable, number).
- A second model and a projector pipeline to build, deploy, and monitor.

## Decision

We will adopt Option C — a CQRS read model for this specific report — because the
report's cost problem is structural (query shape, not query volume), which is exactly
the condition this repository's own [CQRS chapter](../../../syllabus/17-architecture/cqrs-read-write-separation.md#decision-framework)
names as the correct trigger, and because the reporting team has explicitly confirmed
seconds-scale staleness is acceptable for this report.

This decision applies to this one report only — see that chapter's own warning
against introducing CQRS system-wide from a single justified case.

## Consequences

**Positive:**
- The report's query cost becomes flat with customer count rather than growing with
  total order and item count.
- The read model is disposable and rebuildable from the existing event stream,
  requiring no new backup strategy.

**Negative:**
- The report can now show a briefly stale answer immediately after a write — an
  operational fact that must be communicated to the reporting team's own stakeholders,
  not just accepted silently by engineering.
- Projector lag becomes a new metric the on-call rotation must monitor, per this
  repository's own [best practice](../../../syllabus/17-architecture/cqrs-read-write-separation.md#best-practices)
  on treating an un-monitored async boundary as a real risk.

**Follow-up:**
- Add a projector-lag alert before this ships to production, not after.
- Revisit in two quarters: if a second report needs the same shape of aggregate, this
  becomes the second data point for whether a shared projection framework is worth
  building, rather than one-off read models per report.

## Related

- [CQRS: Read/Write Separation](../../../syllabus/17-architecture/cqrs-read-write-separation.md) — the canonical chapter and all cited evidence.
- [CQRS practice code and real measurements](../../java/architecture/cqrs-read-write-separation/README.md)
