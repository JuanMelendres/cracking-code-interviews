---
title: "Over-Sized Aggregate Causing System-Wide Lock Contention"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/architecture/ddd-tactical-design-aggregates.md
source: handbook/architecture/ddd-tactical-design-aggregates.md#production-scenarios
---

# Over-Sized Aggregate Causing System-Wide Lock Contention

## Context

`Customer` and all of a customer's `Order`s are modeled as a single aggregate, sharing one root, on the reasoning that "a customer has orders, so they belong together."

## Symptoms

During a flash sale, every order placement against a popular product times out, even though the underlying database has ample capacity and the product's inventory count itself is updated correctly. Unrelated customer-profile updates during the same window also slow down significantly.

## Impact

A routine, anticipated traffic spike causes a broad outage touching features — customer profile updates — that have nothing to do with the sale itself.

## Initial Hypotheses

- Database connection pool exhaustion — checked and ruled out; pool utilization is well below its limit.
- A slow query plan on the order-placement path — checked and ruled out; the query itself executes quickly.
- An over-sized aggregate causing broad lock contention — correct.

## Evidence

Profiling shows every order-placement transaction acquiring a lock on the entire `Customer` row, because `Customer` and all of a customer's `Order`s were modeled as one aggregate, sharing one root. The flash sale drives many concurrent orders from the same set of frequent customers, serializing all of their concurrent order placements — and blocking unrelated customer-profile updates from the same customers — on that one shared lock.

## Investigation Timeline

1. **Order-placement timeouts observed during a flash sale**, alongside unexplained slowdowns in unrelated customer-profile updates.
2. **Database capacity and query-plan hypotheses ruled out**, confirming neither the connection pool nor the query itself was the bottleneck.
3. **Transaction profiling run directly**, revealing every order-placement transaction acquiring a lock on the full `Customer` row.
4. **Aggregate boundary reviewed**, finding `Customer` and `Order` modeled as one aggregate sharing a single root and therefore a single lock.

## Root Cause

`Customer` and `Order` were modeled as a single aggregate rather than as two separate aggregates connected by a `customerId` reference. This is over-sizing that creates crippling lock contention, discovered only under real concurrent load.

## Immediate Mitigation

Temporarily route flash-sale order placement through a code path that avoids loading the full `Customer` aggregate, accepting some duplicated logic to relieve the immediate contention.

## Permanent Fix

Re-model `Order` as its own aggregate, referencing `Customer` by ID only, removing the shared lock entirely, since no invariant actually requires a customer's profile data and any one of their orders to change atomically together.

## Alternatives Considered

Optimistic locking on the combined aggregate instead of pessimistic. Rejected as treating the symptom — lock contention — without fixing the underlying modelling error: the two things were never one consistency unit to begin with.

## Trade-offs

Splitting the aggregate means a customer's total order history is no longer trivially available in one atomic read. Accepted, since that read was never actually required to be atomic in the first place — it's a query-time join, not a consistency requirement.

## Prevention

Apply the sizing test explicitly during design review for any proposed aggregate spanning what looks like a natural "has-many" relationship: does a real invariant require these two things to be consistent together, or would a change to one require locking the other anyway for no invariant-driven reason?

## Monitoring and Alerts

- Lock wait time and contention rate per table/row, tracked as a standing metric and correlated against known high-concurrency events (flash sales, batch jobs) before they occur, not discovered reactively during the event itself.
- A design-review checklist item (the Prevention item above) applied to every new aggregate proposal, catching over-sizing before it ships rather than under real concurrent production load, which is the only environment where the cost becomes visible.

## Interview Story

This maps to "your aggregate is too big and now everything's slow" — a direct application of DDD aggregate-sizing discipline under load. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a flash sale caused order-placement timeouts and unrelated customer-profile slowdowns simultaneously, despite ample database capacity.
- **Task:** find the connection between two seemingly unrelated features failing together.
- **Action:** rule out connection-pool and query-plan explanations using direct metrics; profile the actual lock being acquired; trace it to a `Customer`/`Order` aggregate boundary drawn by object composition rather than genuine invariant.
- **Result:** re-modeled `Order` as its own aggregate referencing `Customer` by ID, eliminating the shared lock and the cross-feature contention it caused.

## Staff-Level Discussion

This incident is the concrete cost of a modeling shortcut that reads as harmless in isolation: "a customer has orders" is true as a description of the domain, but it is not the same claim as "a customer and their orders must always be consistent together," and only the second claim justifies making them one aggregate. The gap between those two claims is invisible until real concurrent load exposes it as lock contention — normal testing and even normal production traffic may never trigger it, which is exactly why a flash sale (concentrated, high-frequency access to the same customers) is what surfaced it. A Staff engineer reviewing aggregate design should insist on the invariant-based sizing test explicitly, rather than accepting object-composition ("has-many" relationships) as sufficient justification for a shared consistency boundary — the two are easy to conflate and only one of them justifies the lock.

## Related Handbook Chapters

- [DDD Tactical Design: Aggregates](../handbook/architecture/ddd-tactical-design-aggregates.md) — canonical aggregate-sizing and invariant-boundary mechanics used here.
