---
title: "Correlated-Subquery Leaderboard Timing Out at Production Scale"
document_type: production-cookbook-entry
domain: databases
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/06-databases/window-functions-and-ctes.md
source: syllabus/06-databases/window-functions-and-ctes.md#production-scenarios
---

# Correlated-Subquery Leaderboard Timing Out at Production Scale

## Context

A gaming platform ships a "top 10 players per region, by score" leaderboard API, implemented with a correlated subquery counting how many players in the same region outscore the current player. It works correctly and fast in QA (a few hundred test rows).

## Symptoms

In production, with several million real player-score rows, the endpoint's p99 latency climbs into multiple seconds and occasionally times out entirely, specifically for the most popular (highest-row-count) regions.

## Impact

The leaderboard page — a high-visibility, frequently-viewed feature — becomes unreliable exactly for the platform's largest, most active regions.

## Initial Hypotheses

- Database connection pool exhaustion — checked, pool metrics show available connections throughout.
- A missing index on the scores table — checked, an index on `(region, score DESC)` already exists and is being used.
- The query's fundamental approach doesn't scale with row count — correct.

## Evidence

`EXPLAIN ANALYZE` on the production query shows a `SubPlan` re-executed once per outer row, with `loops` equal to the region's total player count.

## Investigation Timeline

1. Leaderboard timeouts reported specifically for the largest regions.
2. Connection-pool exhaustion and missing-index hypotheses ruled out via metrics and `EXPLAIN`.
3. Reproduced the slow query in a staging environment seeded with production-representative row counts, since QA's small dataset had never surfaced the issue.
4. `EXPLAIN ANALYZE` confirms execution time scales roughly with the square of the region's row count, matching a correlated-subquery cost shape rather than a linear one.

## Root Cause

"Top N per group" implemented as a correlated subquery, which re-scans the indexed data once per candidate row rather than once total — a real, structural scaling problem invisible at QA-scale row counts and severe at production scale.

## Immediate Mitigation

Add a short-TTL cache in front of the leaderboard endpoint to absorb repeated identical requests while the real fix ships.

## Permanent Fix

Rewrite the query using `ROW_NUMBER() OVER (PARTITION BY region ORDER BY score DESC)` wrapped in an outer filter, bringing p99 latency down to a small, roughly-constant multiple of the existing index scan cost regardless of region size.

## Alternatives Considered

Pre-computing and caching leaderboards on a schedule — rejected as the primary fix, since the product requirement calls for near-real-time rank updates; adopted only as the short-term mitigation above, not the underlying query fix.

## Trade-offs

None meaningful for the rewrite itself; the window-function version is both faster and no less readable than the correlated subquery it replaces.

## Prevention

Treat any "top N per group" query as needing an explicit performance check against production-representative row counts before launch, since a correlated-subquery implementation can pass every QA-scale test while carrying a real, severe scaling defect invisible until production traffic and data volume actually arrive.

## Monitoring and Alerts

- Load/performance testing against production-representative data volumes as a required pre-launch gate for any "top N per group"-shaped feature, not just functional QA at small scale.
- p99 latency alerting segmented by the dimension the query partitions on (region, in this case), so a regression concentrated in the largest partitions is visible rather than averaged away.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a leaderboard feature that worked fine in QA started timing out in production for the largest regions.
- **Task:** find why row count, not request volume, was driving the regression.
- **Action:** reproduced the issue against production-representative row counts in staging, confirmed via `EXPLAIN ANALYZE` that the correlated subquery scaled quadratically, and rewrote it with `ROW_NUMBER() OVER (PARTITION BY ...)`.
- **Result:** brought p99 latency down to a small, roughly-constant multiple of the index scan cost, independent of region size.

## Staff-Level Discussion

This is Interview Question 1 in the canonical chapter's own Interview Questions section — "how would you find the top N rows per group, and why does it matter which approach you pick" — arriving as a real, production-scale outage rather than a definitional question. The organizational lesson is that QA environments sized far below production scale can hide structural scaling defects entirely — any "top N per group" implementation needs a specific, production-representative-scale performance check before launch, since correctness testing alone will not surface this class of problem.

## Related Handbook Chapters

- [Window Functions and CTEs](../syllabus/06-databases/window-functions-and-ctes.md) — the canonical `ROW_NUMBER()`-based top-N-per-group rewrite behind this incident's fix.
