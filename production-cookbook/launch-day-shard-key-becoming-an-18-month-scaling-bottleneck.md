---
title: "Launch-Day Shard Key Becoming an 18-Month Scaling Bottleneck"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/databases/table-partitioning-and-sharding-strategies.md
source: handbook/databases/table-partitioning-and-sharding-strategies.md#production-scenarios
---

# Launch-Day Shard Key Becoming an 18-Month Scaling Bottleneck

## Context

A multi-tenant SaaS product sharded its primary `events` table by `customer_id` at launch, matching the dominant query pattern at the time — per-customer dashboards.

## Symptoms

Eighteen months later, a new cross-customer analytics feature needs to query `event_type` across all customers. Every such query fans out to every shard, with latency scaling linearly as shard count and per-shard data volume grow.

## Impact

The analytics feature's latency degrades every time a new shard is added — more shards to fan out to — the opposite of the scaling benefit sharding is supposed to provide for this specific access pattern.

## Initial Hypotheses

- Missing an index on `event_type` — checked and ruled out; an index exists on every shard, but each shard still must be queried.
- A query-planner regression — checked and ruled out; each individual shard's query plan is efficient.
- The cross-shard access pattern itself being fundamentally unsuited to a `customer_id`-sharded scheme — correct.

## Evidence

Query latency for the analytics feature scales linearly with shard count in load testing, while per-customer dashboard queries — the original design target — remain flat, confirming the shard key serves one access pattern well and the other poorly.

## Investigation Timeline

1. **New analytics feature's latency degrading as shard count grows**, noticed during load testing ahead of a planned scale-up.
2. **Indexing and query-planner hypotheses ruled out**, confirming each individual shard executes efficiently on its own.
3. **Access-pattern comparison run directly**: dashboard-query latency stays flat with shard count while analytics-query latency scales linearly.
4. **Root cause isolated**: the analytics query's fan-out is structural to any `customer_id`-sharded scheme, not fixable by indexing or query tuning.

## Root Cause

The shard key was chosen correctly for the launch-day dominant pattern, but a new, materially different query pattern — `event_type` across all customers — was never going to prune under a `customer_id` key. No amount of indexing or query tuning fixes a fan-out that's structural to the sharding scheme itself.

## Immediate Mitigation

Route the analytics feature to a read replica set explicitly provisioned for fan-out queries, accepting the cost, to stop the feature from degrading the primary shards' latency for the original access pattern.

## Permanent Fix

Build a separate, denormalized analytics store — a columnar warehouse or a materialized, `event_type`-indexed rollup — fed by change-data-capture from the sharded primary, rather than querying the sharded primary directly for a pattern it was never designed to serve.

## Alternatives Considered

Re-sharding by a composite or different key. Rejected as solving today's problem while likely creating the identical problem for whatever access pattern emerges next — a single shard key cannot serve every future query pattern well.

## Trade-offs

Maintaining a separate analytics store adds operational surface — a CDC pipeline, eventual consistency between the two stores. Accepted, since the alternative is degrading the primary sharded system's core access pattern for every customer.

## Prevention

Shard-key selection should be validated against the two or three most likely future access patterns the team can anticipate, not just the current dominant one, and any genuinely cross-cutting query pattern — analytics, reporting, search — should be routed to a purpose-built store from the start rather than assumed to work against the primary sharded system.

## Monitoring and Alerts

- Per-query-type latency-vs-shard-count correlation tracked as a standing metric for any sharded system, surfacing a structurally poor fit before it becomes a launch blocker for a new feature, not only during that feature's own load testing.
- A design-review question required for any new query pattern proposed against a sharded primary: does this pattern prune under the existing shard key, or does it fan out to every shard? — making the trade-off explicit before the feature is built against the wrong store.

## Interview Story

This maps to "a correct-at-the-time shard key becomes a bottleneck once the query pattern changes." Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** an analytics feature's latency degraded every time a new shard was added, the opposite of sharding's intended benefit.
- **Task:** explain why a shard key that served the product well for 18 months suddenly became a liability.
- **Action:** rule out indexing and query-planner explanations by checking each shard's own execution efficiency; compare latency scaling behavior between the original and new query patterns directly; identify the fan-out as structural to the shard key, not fixable by tuning.
- **Result:** built a separate, CDC-fed analytics store for the cross-cutting query pattern, rather than re-sharding the primary in a way that would likely reproduce the same problem for the next new pattern.

## Staff-Level Discussion

The genuinely difficult part of this incident is that nothing was done wrong at launch — the shard key was the correct choice for the access pattern that existed then, and no reasonable amount of foresight guarantees anticipating every future query shape a product will eventually need. The real lesson is architectural: a single shard key optimizes for the patterns it was chosen for and structurally penalizes anything orthogonal to it, so the sustainable answer isn't finding a "better" shard key (which just relocates the problem to a different future pattern) but recognizing when a new access pattern is fundamentally cross-cutting and routing it to a purpose-built store rather than forcing it through the primary sharded system. A Staff engineer evaluating a proposed new feature against an existing sharded system should ask explicitly whether the new query pattern prunes or fans out, before the feature is built, not after a load test reveals the cost.

## Related Handbook Chapters

- [Table Partitioning and Sharding Strategies](../handbook/databases/table-partitioning-and-sharding-strategies.md) — canonical shard-key selection and fan-out mechanics used here.
- [Storage Selection Trade-offs](../handbook/system-design/storage-selection-tradeoffs.md) — the broader access-pattern-driven storage-selection framework this incident is an instance of.
