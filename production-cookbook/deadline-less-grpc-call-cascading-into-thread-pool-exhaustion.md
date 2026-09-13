---
title: "Deadline-Less gRPC Call Cascading Into Thread-Pool Exhaustion"
document_type: production-cookbook-entry
domain: api-design
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/07-api-design/grpc-api-design.md
source: syllabus/07-api-design/grpc-api-design.md#production-scenarios
---

# Deadline-Less gRPC Call Cascading Into Thread-Pool Exhaustion

## Context

An internal order service calls an inventory service over gRPC for every request. The inventory service's own database begins to slow down under unrelated load.

## Symptoms

Shortly after the inventory service's database slows down, the order service's request-handling thread pool exhausts and it starts rejecting unrelated requests that never touch inventory.

## Impact

A localized slowdown in one downstream service (inventory) becomes a full outage in an upstream service (orders) whose own logic was otherwise healthy.

## Initial Hypotheses

- A bug in the order service itself — checked, no recent deploy, no code path change.
- A general infrastructure issue — checked, other services are healthy.
- Calls to the slow inventory service holding order-service threads open with no deadline — correct.

## Evidence

Thread dumps on the order service show a large number of threads blocked inside the inventory-service gRPC call, with no deadline set on any of them.

## Investigation Timeline

1. Order service starts rejecting unrelated requests shortly after inventory's database slows down.
2. Own-code and general-infrastructure hypotheses ruled out via deploy history and other-service health checks.
3. Thread dumps taken on the order service, showing threads blocked in the inventory call with no configured deadline.

## Root Cause

The gRPC stub for the inventory call was never configured with `withDeadlineAfter(...)`, so a slow downstream call held an order-service thread for as long as inventory took to respond — with enough concurrent slow calls, every thread in the pool ends up blocked, and healthy, unrelated requests queue behind them.

## Immediate Mitigation

Roll out a deadline on the inventory-service stub via a fast configuration change, bounding how long any single call can hold a thread.

## Permanent Fix

Establish deadline propagation as a mandatory convention for every internal gRPC call in the codebase, paired with `DEADLINE_EXCEEDED`-aware retry/circuit-breaking logic on the caller side.

## Alternatives Considered

Scaling up the order service's thread pool — rejected as treating the symptom; a larger pool just raises the number of concurrent slow calls needed to exhaust it, it doesn't remove the unbounded-wait root cause.

## Trade-offs

An aggressive deadline risks cutting off calls that would have succeeded slightly slower — accepted, since an explicit, fast failure (`DEADLINE_EXCEEDED`, handled) is safer than an unbounded wait that can cascade.

## Prevention

Treat "does this gRPC call have a deadline" as a required review item for every new internal call.

## Monitoring and Alerts

- Thread-pool utilization/saturation alerting on every service making internal gRPC calls, catching cascading exhaustion before it fully saturates.
- A `DEADLINE_EXCEEDED` rate metric per downstream dependency, both to confirm deadlines are actually configured and to catch a genuinely degrading downstream early.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a slowdown in one internal service cascaded into a full outage of an unrelated upstream service.
- **Task:** find why a healthy service started rejecting requests that never touched the slow dependency.
- **Action:** took thread dumps on the affected service, found threads blocked indefinitely in a downstream gRPC call with no deadline configured, and rolled out a deadline as an immediate fix.
- **Result:** bounded the blast radius of any future downstream slowdown, and established deadline propagation as a mandatory convention.

## Staff-Level Discussion

gRPC's deadline mechanism exists precisely because internal service-to-service calls are exactly where an unbounded wait turns into a cascading failure. The organizational risk isn't this one call site — it's that any internal gRPC call anywhere in the codebase without a configured deadline is a latent version of this same incident, waiting only for its own downstream dependency to slow down; a mandatory, enforced convention (not a per-call reminder) is the only fix that scales across a growing internal service graph.

## Related Handbook Chapters

- [gRPC API Design](../syllabus/07-api-design/grpc-api-design.md) — the canonical deadline-propagation pattern behind this incident's fix.
