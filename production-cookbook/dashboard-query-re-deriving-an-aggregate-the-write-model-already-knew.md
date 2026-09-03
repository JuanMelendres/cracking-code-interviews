---
title: "Reporting Query Re-Deriving an Aggregate the Write Side Already Knew"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/architecture/cqrs-read-write-separation.md
  - ../handbook/system-design/caching-strategies-and-invalidation.md
source: handbook/architecture/cqrs-read-write-separation.md#production-scenarios
---

# Reporting Query Re-Deriving an Aggregate the Write Side Already Knew

## Context

A "total spend per customer" report is computed by summing every order's line items on every request, directly against the normalized write schema.

## Symptoms

The report's latency degrades linearly with order volume and starts timing out at scale.

## Impact

The report becomes progressively unusable as the business grows, precisely because its cost is tied to a volume that only ever increases (total historical order count), not to anything that could naturally plateau.

## Initial Hypotheses

None stated as separately ruled out — the investigation proceeded directly to measuring the query's actual cost against an alternative shape.

## Evidence

[`QueryComplexityComparisonDemo`](../../practice/java/architecture/cqrs-read-write-separation/QueryComplexityComparisonDemo.java) populated 50,000 orders (4 items each, 300,000 total domain events), waited for the read model to fully converge, then timed the identical query both ways: walking every order and every item inside it on the normalized write model took a real **15.84ms**; summing the same total off precomputed, per-order values already sitting on the read model took a real **3.45ms** — a real **4.6x** measured speedup (a repeat run measured 17.63ms vs. 3.26ms, 5.4x — the exact multiplier moves with JIT warm-up, the shape doesn't). Both computations were checked for exact equality and matched — the read model is not a different or approximate answer, it is the identical answer, pre-shaped for the query it exists to serve.

## Investigation Timeline

1. "Total spend per customer" report observed degrading linearly with order volume, eventually timing out at scale.
2. Query's actual cost measured directly against the normalized write model: 15.84ms for the full walk-every-order-and-item computation at 50,000 orders / 300,000 events.
3. An alternative, precomputed read-model-based version of the identical query measured for comparison: 3.45ms.
4. Both computations checked for exact equality, confirming the read model produces the identical answer, not an approximation.
5. A repeat run (17.63ms vs. 3.26ms, 5.4x) confirmed the speedup's shape is consistent even as the exact multiplier shifts with JIT warm-up.
6. Diagnosis reached: the write model's normalization, which correctly enforces "an order's total is derived from its items" on write, is structurally the wrong shape for a query summing that derived total across tens of thousands of rows on every request.

## Root Cause

The write model's normalization — the very thing that makes "an order's total is derived from its items" easy to enforce correctly on write — is structurally the wrong shape for a query that needs that derived total summed across tens of thousands of rows on every request. The query is paying write-side normalization cost on the read path, every single time, for an answer that changes only when a write happens.

## Immediate Mitigation

A cache in front of the existing query buys time but re-introduces its own staleness-and-invalidation problem without solving the underlying re-derivation cost.

## Permanent Fix

Introduce a read model — `OrderSummaryView`-shaped or, for this specific report, a purpose-built `CustomerSpendView` — fed by a projector off the same domain events the write side already publishes for other reasons. The report now reads a precomputed number instead of re-deriving one.

## Alternatives Considered

A cache in front of the existing normalized-model query — considered and explicitly not treated as the actual fix, since it "buys time" but reintroduces staleness-and-invalidation concerns without addressing why the query is expensive in the first place.

## Trade-offs

The report becomes eventually consistent (real, measured lag) instead of strongly consistent with the write. For a spend *report*, a lag measured in milliseconds-to-seconds in production is very likely acceptable; for a balance check gating an immediate follow-on write, it likely is not.

## Prevention

Recognize the read pattern's divergence from the write pattern early — "this query needs an aggregate across many rows, computed on every request, against a table with a fast-growing row count" — and introduce a narrow, single-purpose read model before the query becomes a production incident, not after.

## Monitoring and Alerts

- Track query latency against total order (or event) volume for any reporting-style query still running against the normalized write schema; a curve that grows with volume rather than staying flat is the leading indicator this incident's own measured 15.84ms-at-50,000-orders result represents at just one point in time.
- Once a read model is introduced, track projector lag (time from a domain event being published to the read model reflecting it) as a standing metric, since the report's new eventual-consistency trade-off is only acceptable within a bounded, monitored lag window — an unmonitored, unbounded lag would silently erode the trade-off's own justification.
- Flag any new "aggregate across many rows, computed on every request" query at design-review time, before it ships, using the same recognition criterion the Prevention section states — turning a pattern-matching judgment call into an explicit, repeatable review question.

## Interview Story

This maps directly to a "when would you introduce CQRS" question, backed by a real measured before/after. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a customer-spend report, computed by summing every order's line items against the normalized write schema on every request, degraded linearly with order volume and began timing out.
- **Task:** decide whether a cache was sufficient or whether the underlying query shape itself needed to change.
- **Action:** measured the same query's cost on the normalized model versus a precomputed read model fed by the same domain events already published for other reasons, confirming both produced the identical answer.
- **Result:** measured a 4.6–5.4x speedup from the read model alone, adopted it as the permanent fix, and explicitly weighed the resulting eventual-consistency trade-off against the report's actual consistency requirements rather than assuming it was universally acceptable.

## Staff-Level Discussion

The judgment call this scenario turns on — is eventual consistency acceptable for this specific read — is not a property of CQRS in general, it is a property of the specific query, and a Staff engineer's real contribution is knowing which reads can tolerate a real, measured lag and which cannot, rather than defaulting either to "always add a read model" or "never accept eventual consistency." The rejected alternative (a cache) is instructive precisely because it looks like a cheaper fix that avoids introducing a new moving part, but it inherits a strictly harder problem (staleness and invalidation) without addressing the actual cost driver (re-deriving an aggregate from raw rows on every request) — a Staff-level review should recognize that a cache in front of an expensive query treats the symptom, while a read model fed by the same events the write side already emits treats the cause, at the cost of a new component (a projector) that must itself be operated and monitored for lag.

## Related Handbook Chapters

- [CQRS: Read/Write Separation](../handbook/architecture/cqrs-read-write-separation.md) — canonical trade-off analysis and the `QueryComplexityComparisonDemo` measurement this incident reproduces.
- [Caching Strategies and Invalidation](../handbook/system-design/caching-strategies-and-invalidation.md) — the rejected alternative's own staleness-and-invalidation cost, referenced directly in this incident's mitigation analysis.
