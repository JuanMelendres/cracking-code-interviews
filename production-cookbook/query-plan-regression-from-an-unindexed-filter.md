---
title: "Query-Plan Regression From an Unindexed Filter"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/06-databases/query-planning-and-explain-analyze.md
source: handbook/databases/query-planning-and-explain-analyze.md#production-scenarios
---

# Query-Plan Regression From an Unindexed Filter

## Context

A reporting dashboard adds a new optional `region` filter to an existing, previously fast orders-summary endpoint. No index changes ship with it — the filter looked like a small, additive change to an already-verified query.

## Symptoms

The endpoint's p95 latency triples specifically when the new filter is applied. Unfiltered requests are unaffected.

## Impact

The dashboard is used by ops during incident response — a slow dashboard during an incident compounds the incident it's meant to help diagnose.

## Initial Hypotheses

- A missing index on the new filter column — plausible.
- The join order changed because of the new predicate — plausible.
- Connection pool contention — checked and ruled out; pool metrics are flat.

## Evidence

`EXPLAIN ANALYZE` on the filtered query shows a hash join with `customers` — filtered by `region` — as the build side, and the planner's row estimate for the filtered `customers` set is far higher than the actual count.

## Investigation Timeline

1. **Regression window confirmed** via `pg_stat_statements`: the filtered query variant appeared only after the release that added the filter, not before.
2. **Reproduced locally** with `EXPLAIN ANALYZE` against the same query shape.
3. **Missing index found**: no index on `customers.region` at all — the same missing-index shape covered elsewhere in this chapter, but on a column that is far more selective here (2% of customers, not 25%), which is exactly why the payoff of fixing it is dramatic rather than modest.
4. **Root cause isolated**: a highly selective filter with no supporting index and no query-shape-specific statistics yet.

## Root Cause

Missing index on a newly added, highly selective filter column, compounded by no statistics yet reflecting the new query shape's frequency.

## Immediate Mitigation

```sql
CREATE INDEX CONCURRENTLY idx_customers_region ON customers(region);
ANALYZE customers;
```

## Permanent Fix

Add the index as a tracked migration, and add an `EXPLAIN`-plan regression test for the dashboard's top query variants, so a future added filter without a matching index fails CI instead of production.

## Alternatives Considered

Materializing the dashboard's aggregate into a summary table refreshed on a schedule. Rejected for this endpoint specifically, because near-real-time freshness is a stated requirement the materialized approach would violate.

## Trade-offs

The new index adds write-path cost to `customers` inserts and updates. Accepted, given `customers` is a low-write-volume table relative to `orders`.

## Prevention

Any new filter added to an existing hot-path query should be treated as a new query shape requiring its own `EXPLAIN ANALYZE` check, not an incremental tweak to an already-verified plan. Not every "obvious" index pays off the same way — the only way to know is to measure the specific shape in question, which is why this same chapter also documents a case where a similar-looking index produced only a modest gain.

## Monitoring and Alerts

- `pg_stat_statements` mean-time regression per normalized query, alerted per-release rather than only reviewed reactively during an incident — this is what actually confirmed the regression window here, and it's cheap to check automatically on every deploy.
- An `EXPLAIN`-plan regression test in CI for a hot-path endpoint's top query variants (the Permanent Fix above), which converts this class of incident into a failed build rather than a production latency spike.

## Interview Story

This maps to a "diagnose a sudden latency regression on an endpoint that didn't change its code" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a dashboard endpoint's p95 latency tripled after a seemingly small, additive filter was shipped.
- **Task:** find why an "optional" filter caused a major regression instead of a minor one.
- **Action:** rule out connection pool contention with existing metrics; confirm the regression's start date against the release using `pg_stat_statements`; use `EXPLAIN ANALYZE` to find the specific unindexed, highly selective column driving the bad plan.
- **Result:** added the missing index and a CI-level `EXPLAIN`-plan regression test, closing both the immediate incident and the class of incident.

## Staff-Level Discussion

The specific fix is a one-line `CREATE INDEX`, but the incident is a symptom of treating "add an optional filter" as a low-risk, incremental change rather than what it actually is: a new query shape that has never been measured. A Staff engineer's real contribution is the Permanent Fix, not the mitigation — pushing `EXPLAIN`-plan regression testing into CI so this class of change is caught before merge rather than after a production incident. That is a process change with organization-wide leverage: it protects every future hot-path query from the same mistake, not just this one dashboard endpoint, and it does so without requiring every engineer to remember to think about selectivity before shipping a filter.

## Related Handbook Chapters

- [Query Planning and EXPLAIN ANALYZE](../syllabus/06-databases/query-planning-and-explain-analyze.md) — canonical `EXPLAIN ANALYZE` reading and plan-diagnosis methodology used here.
- [Index Structures: B-Tree, Composite, Covering](../syllabus/06-databases/index-structures-btree-composite-covering.md) — index-selection reasoning behind the fix.
