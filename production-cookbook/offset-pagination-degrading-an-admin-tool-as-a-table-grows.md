---
title: "OFFSET Pagination Degrading an Admin Tool as a Table Grows"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/07-api-design/api-design.md
source: syllabus/07-api-design/api-design.md#production-scenarios
---

# OFFSET Pagination Degrading an Admin Tool as a Table Grows

## Context

An internal admin tool lists customer records, built early with simple `OFFSET`-based pagination and a page-number control.

## Symptoms

The tool works fine for the first year. As the customer table grows past a few million rows, support staff report the tool becoming unusably slow specifically when jumping to later pages, while the first few pages remain fast.

## Impact

Internal tooling degradation affecting support-team productivity, discovered gradually rather than via a sudden outage.

## Initial Hypotheses

- General database load — checked and ruled out; other queries against the same table remain fast.
- A missing index — checked and ruled out; the underlying `ORDER BY id` column is indexed.
- The `OFFSET` pagination mechanism itself — correct.

## Evidence

`EXPLAIN ANALYZE` on the admin tool's actual query at a deep page shows a large `rows=` walked-and-discarded count, with execution time scaling with the requested offset.

## Investigation Timeline

1. **Gradual slowness complaints from support staff**, specific to deep page jumps rather than uniform across the tool.
2. **General load and indexing hypotheses ruled out**, confirming other queries stayed fast and the sort column was indexed.
3. **`EXPLAIN ANALYZE` run on the actual deep-page query**, revealing the offset-proportional walked-and-discarded row count.
4. **Root cause confirmed**: the pagination mechanism itself scales cost with depth, independent of indexing or general load.

## Root Cause

The `OFFSET`-based pagination was never expected to be queried at meaningful depth when originally built, and nobody revisited the decision as the table grew — by the time the problem became visible, the tool had existing users depending on its page-jump behavior.

## Immediate Mitigation

Cap the maximum page-jump depth exposed in the UI as a stopgap, preventing the worst-case query from being triggered while a proper fix is designed.

## Permanent Fix

Migrate the next/previous page interactions — the overwhelming majority of actual usage — to keyset pagination, and implement a separate, approximate jump-to-page control using a periodically refreshed row-count estimate rather than an exact `OFFSET` count.

## Alternatives Considered

Adding more database resources to absorb the cost. Rejected as treating the symptom, since the cost scales with table growth regardless of available resources, and would need to be revisited again at the next order of magnitude.

## Trade-offs

The hybrid approach means the jump-to-page control shows an approximate rather than exact page count. Accepted, since exact counts at this depth were never actually load-bearing for the admin tool's real usage pattern.

## Prevention

Any endpoint queried against a table expected to grow past a few hundred thousand rows should default to keyset pagination unless arbitrary page-jump is a genuine, load-bearing requirement — and even then, prefer the hybrid approach over exact `OFFSET` at scale.

## Monitoring and Alerts

- Query latency for pagination endpoints tracked and correlated against requested offset/page depth, surfacing the offset-proportional cost trend well before it becomes user-visible as "the tool is slow."
- A table-size threshold alert for any endpoint still using `OFFSET`-based pagination, flagging it for review before it crosses the point where deep-page cost becomes noticeable, rather than waiting for a complaint.

## Interview Story

This maps directly to "design pagination for a 500M-row endpoint, why not OFFSET." Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** an internal admin tool's pagination degraded specifically at deep page jumps as its underlying table grew past a few million rows.
- **Task:** diagnose a gradual degradation with no single triggering event.
- **Action:** rule out general database load and missing indexes; run `EXPLAIN ANALYZE` on the actual deep-page query to see the offset-proportional cost directly; recognize the pagination mechanism itself, not the query's other aspects, as the structural cause.
- **Result:** migrated the dominant next/previous usage to keyset pagination, and added a hybrid approximate jump-to-page control rather than an exact `OFFSET`-based one.

## Staff-Level Discussion

This incident is the concrete cost of a design decision that was entirely reasonable at the time it was made — the tool's early scale genuinely didn't warrant the complexity of keyset pagination — becoming expensive specifically because users came to depend on its behavior (arbitrary page-jump) before the underlying assumption (a small table) stopped holding. This is the general "optimize later" trap: the cost of revisiting the decision grows with the number of dependents relying on the current behavior, not just with the data volume, which is why proactively reviewing pagination-mechanism choices against projected table growth is cheaper than waiting for a complaint. A Staff engineer designing any paginated endpoint should default to keyset pagination unless arbitrary page-jump is a genuinely load-bearing product requirement, precisely because retrofitting the fix later means renegotiating an interface users already depend on, not just changing an implementation detail.

## Related Handbook Chapters

- [API Design](../syllabus/07-api-design/api-design.md) — canonical keyset-vs-OFFSET pagination mechanics and hybrid approach used here.
