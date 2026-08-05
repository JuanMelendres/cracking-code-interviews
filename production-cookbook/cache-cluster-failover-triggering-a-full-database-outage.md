---
title: "Cache Cluster Failover Triggering a Full-Database Outage"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/system-design/caching-strategies-and-invalidation.md
source: handbook/system-design/caching-strategies-and-invalidation.md#production-scenarios
---

# Cache Cluster Failover Triggering a Full-Database Outage

## Context

A service's primary database is sized assuming its cache cluster absorbs the overwhelming majority of read traffic. A routine cache-cluster node replacement is scheduled as standard maintenance.

## Symptoms

During the node replacement, the application's primary database experiences a sudden, near-total connection-pool exhaustion and begins rejecting new connections. The on-call engineer initially suspects a database-side regression.

## Impact

Full-site read-path outage for several minutes, not just a slower cache.

## Initial Hypotheses

- A database configuration regression from an unrelated recent change — checked and ruled out; no relevant deploy in the window.
- A query regression — checked and ruled out; query shapes unchanged.
- The cache-cluster maintenance itself — correct, once cross-referenced against the maintenance window timing.

## Evidence

Database connection and query-rate metrics show an almost step-function increase precisely coinciding with the cache node replacement window. Application logs show a spike in cache-miss-triggered database reads across nearly the entire previously-cached key space, not one hot key.

## Investigation Timeline

1. **Connection-pool exhaustion observed**, initially read as a database-side incident.
2. **Deploy and query-regression hypotheses ruled out** against deployment and query-shape history, neither showing anything relevant.
3. **Timing cross-referenced against the maintenance calendar**, aligning the database spike almost exactly with the cache node replacement window.
4. **Read-pattern examined**: the spike spans nearly the entire previously-cached key space, distinguishing this from a single hot-key stampede.

## Root Cause

The cache-cluster maintenance briefly took the cache fully unavailable. Every request that would normally hit the cache fell through to the database simultaneously — a full-working-set stampede, not a single-key stampede — against a database that was sized assuming the cache would absorb the overwhelming majority of read traffic.

## Immediate Mitigation

Enable read-path circuit breaking or load shedding on the affected service to fail some requests fast rather than let every one queue against an overwhelmed database, and manually throttle incoming traffic during the remainder of the maintenance window.

## Permanent Fix

Require cache-cluster maintenance to use a rolling, partial-unavailability strategy that never takes the whole cache down at once, and add a database-side circuit breaker or graceful-degradation path — serving slightly stale data, or failing non-critical reads fast — specifically for the "cache is unavailable" case, rather than assuming the cache is always present.

## Alternatives Considered

Oversizing the database permanently to tolerate a full-cache-outage load. Rejected as prohibitively expensive for a rare event, versus a targeted graceful-degradation mechanism.

## Trade-offs

Graceful degradation means some requests are deliberately failed or served stale data during a cache outage. Accepted, since the alternative — the database falling over entirely — is strictly worse for every request, not just some.

## Prevention

Any cache-cluster maintenance runbook should explicitly model "what does the database receive if the cache is briefly fully unavailable" as a required pre-maintenance capacity check, not an afterthought discovered during the incident.

## Monitoring and Alerts

- Database connection and query-rate metrics cross-referenced against the maintenance calendar automatically, not manually correlated after the fact during an active incident — this is the single check that would have redirected the initial hypothesis correctly within minutes instead of longer.
- A pre-maintenance capacity check (the Prevention item above) run as a required gate before any cache-cluster maintenance is scheduled, modeling database load under a full-cache-outage assumption.

## Interview Story

This maps to the "your cache dies at peak, walk through what happens to the database" question directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** routine cache maintenance caused a full-site read-path outage via database connection-pool exhaustion.
- **Task:** determine whether this was a database regression or something else, quickly enough to mitigate.
- **Action:** rule out deploy and query-shape changes using existing history; correlate the database spike's timing against the maintenance calendar; distinguish a full-working-set stampede from a single hot-key stampede using the read-pattern shape.
- **Result:** added read-path circuit breaking as an immediate mitigation, and required rolling cache maintenance plus a database-side graceful-degradation path as the permanent fix.

## Staff-Level Discussion

The database in this incident was never actually unhealthy — it was correctly sized for its designed load, and the designed load implicitly assumed the cache would always be present. That assumption held until routine, planned maintenance broke it, which is a distinct and often underweighted risk category: not "the system fails under unexpected load" but "the system fails under expected, self-inflicted operational activity because a dependency's availability was silently load-bearing." A Staff engineer's contribution is making that dependency explicit — requiring every maintenance runbook to model the "what if this component briefly disappears" case as a capacity question, not just an operational checklist item, before the maintenance is scheduled rather than after it causes an outage.

## Related Handbook Chapters

- [Caching Strategies and Invalidation](../handbook/system-design/caching-strategies-and-invalidation.md) — canonical cache-stampede and graceful-degradation mechanics used here.
- [Resilience Patterns](../handbook/system-design/resilience-patterns.md) — the circuit-breaker and load-shedding mechanisms used as immediate mitigation.
