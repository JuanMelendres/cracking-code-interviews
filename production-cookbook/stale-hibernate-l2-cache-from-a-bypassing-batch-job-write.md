---
title: "Stale Hibernate L2 Cache From a Bypassing Batch-Job Write"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/databases/hibernate-second-level-and-query-cache.md
source: handbook/databases/hibernate-second-level-and-query-cache.md#production-scenarios
---

# Stale Hibernate L2 Cache From a Bypassing Batch-Job Write

## Context

A product-catalog service (using Hibernate's L2 cache over its `product` table) and a separate, legacy inventory-sync batch job both wrote to the same table. The batch job updated rows directly via its own JDBC connection pool, entirely outside the catalog service's Hibernate `SessionFactory`.

## Symptoms

Customers intermittently saw "in stock" for items that were actually out of stock, traced back to specific hours shortly after the nightly inventory-sync batch job ran.

## Impact

Customers were shown incorrect stock availability for hours after every nightly sync run, a recurring, predictable data-correctness gap rather than a one-off incident.

## Initial Hypotheses

- A race condition in the catalog service's own write path — this was the first hypothesis pursued.

## Evidence

The catalog service's own writes went through Hibernate and always correctly evicted the relevant L2 cache entries. The actual cause was the separate, older batch job, which updated the same `product` rows directly via its own JDBC connection pool, entirely outside the catalog service's Hibernate `SessionFactory`.

## Investigation Timeline

1. **Stale "in stock" reports observed**, correlated specifically with the hours following the nightly inventory-sync batch job.
2. **Race-condition hypothesis pursued first**, focused on the catalog service's own write path.
3. **Catalog service's write path audited**, confirming every Hibernate-mediated write correctly evicted the relevant L2 cache entries.
4. **Batch job identified as the actual writer**, updating the same rows through an independent JDBC connection pool that never touched Hibernate's `SessionFactory` at all.

## Root Cause

Hibernate's L2 cache had no way to know the batch job's rows changed, because the write genuinely never passed through any Hibernate API the cache could observe. A multi-writer table, where one writer bypasses the ORM layer owning the cache, leaves that cache with no mechanism to detect or invalidate on the bypassing writer's changes.

## Immediate Mitigation

Manually triggered an application restart, clearing the in-memory L2 cache, after each batch run — a crude stopgap.

## Permanent Fix

Had the batch job publish a real event after each sync completes, consumed by the catalog service to explicitly evict the affected L2 cache regions (`sessionFactory.getCache().evictEntityData(Product.class, id)`), making the cross-process write visible to Hibernate's cache through an explicit, deliberate integration point.

## Alternatives Considered

Migrating the batch job to write through the catalog service's own Hibernate layer instead of its own JDBC pool. Not adopted as the immediate fix because the batch job was a separate, older system with its own operational lifecycle; the event-based eviction integration point was judged a smaller, faster, less risky change than migrating the batch job's entire write path.

## Trade-offs

The catalog service now depends on receiving the batch job's completion event reliably — a real coupling accepted in exchange for correctness.

## Prevention

Added an architecture review checklist item requiring any new writer to a cached entity's underlying table to either go through the owning service's own Hibernate layer, or explicitly integrate with its cache eviction.

## Monitoring and Alerts

- Alerting on the batch job's eviction event failing to publish or failing to be consumed, since the catalog service's correctness now depends on that event arriving reliably.
- A periodic cross-check comparing cached stock values against real row values for a sample of products, specifically in the hours following any known external batch write, to catch a future multi-writer gap before it reaches customers.

## Interview Story

This maps to a "why is our cache showing stale data with no obvious cause in our own code" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** customers intermittently saw incorrect stock availability, specifically after a nightly batch job ran.
- **Task:** find why data was stale when the service's own cache-eviction logic looked correct.
- **Action:** ruled out a race condition in the catalog service's own writes; found a separate legacy batch job writing to the same table through an independent JDBC connection that Hibernate's cache could never see.
- **Result:** had the batch job publish a completion event that the catalog service consumes to explicitly evict the affected cache regions.

## Staff-Level Discussion

Hibernate can only defend the cache against writes it can see — a multi-writer table is a real, common architecture (legacy batch jobs, other services, direct operational scripts) that requires deliberate cache-invalidation design, not an edge case to hope never happens. The organizational risk is that ORM-level caching decisions are typically made by the team owning that one service, while the set of all writers to the underlying table is a fact about the wider system that team may not have full visibility into. A durable architecture review practice — requiring any new writer to a cached entity's table to either go through the owning ORM layer or explicitly integrate with its eviction — turns an invisible cross-team hazard into an explicit design question asked at the time a new writer is introduced, rather than discovered later as a customer-facing data-correctness incident.

## Related Handbook Chapters

- [Hibernate Second-Level and Query Cache](../handbook/databases/hibernate-second-level-and-query-cache.md) — canonical L2 cache visibility model and multi-writer staleness mechanism used here.
