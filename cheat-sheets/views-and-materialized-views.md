---
title: "Cheat Sheet: Views and Materialized Views"
slug: views-and-materialized-views
document_type: cheat-sheet
domain: 06-databases
topic_id: T-2410
canonical: ../syllabus/06-databases/views-and-materialized-views.md
last_updated: 2026-09-15
---

# Views and Materialized Views

**Canonical chapter:** [`syllabus/06-databases/views-and-materialized-views.md`](../syllabus/06-databases/views-and-materialized-views.md)

## Core Mental Model

A view is a saved query — no storage, always live, re-run on every read. A materialized view is a saved *result* — real storage, fast reads, stale until an explicit `REFRESH`.

## Essential Definitions

- **View** — a named query definition, substituted at query-planning time; zero storage of its own.
- **Materialized view** — a named, stored result set, populated once and left unchanged until `REFRESH MATERIALIZED VIEW` re-runs the defining query.
- **Automatically updatable view** — a plain, single-base-table view with no `GROUP BY`/`DISTINCT`/`UNION`/aggregate; accepts `UPDATE`/`INSERT`/`DELETE` directly.

## Decision Table

| Need | Reach for | Real evidence |
|---|---|---|
| Reuse a repeated `JOIN`, no perf concern | Plain view | Zero cost, always current |
| Restrict a role to fewer columns/rows | Plain view + `GRANT`/`REVOKE` | Real `permission denied` on the base table |
| Update through a single-table view | Works automatically | Real successful `UPDATE` |
| Update through a `JOIN`/`GROUP BY` view | Needs an `INSTEAD OF` trigger | Real `cannot update view` error without one |
| Expensive query, read often, staleness OK | Materialized view | Real ~457× read speedup (55.735ms → 0.122ms) |
| Materialized view read often in production | `REFRESH ... CONCURRENTLY` + unique index | Real error without the index; real success with it |

## PK vs. FK vs. Index vs. View, one line each

- **Primary key** — this row is uniquely, non-null identifiable.
- **Foreign key** — this reference genuinely points at a real row elsewhere.
- **Index** — finding matching rows is fast, without changing what's true.
- **View** — a reusable, possibly access-controlled or precomputed way to read the result of combining the other three.

## Common Pitfalls

- Believing a plain view is a performance optimization — it's exactly as slow as its defining query run inline.
- Materializing a query without first measuring it's actually slow — a materialized view trades staleness for speed and that trade needs real evidence.
- Using a plain, blocking `REFRESH MATERIALIZED VIEW` on a frequently-read materialized view in production — causes a real read outage for the refresh's duration.
- Expecting a `GROUP BY`/`JOIN` view to accept writes without an `INSTEAD OF` trigger.

## Interview Answer Skeleton

**30-sec:** A view is a saved query, always live, no storage. A materialized view is a saved result, fast to read, stale until refreshed. Choose by whether "always current" or "fast to read" matters more.

**2-min:** Add: automatic updatability requires exactly one base table and no aggregation — verified by a real `cannot update view` error otherwise. A materialized view's real cost lives in the refresh (full query cost), not the read (table-scan cost) — this chapter measured a real ~457× read speedup at a real ~50ms refresh cost.

**Staff-level framing:** A fleet of independently-scheduled materialized views across an org is real, ongoing operational surface — refresh-job failures produce no error, only silent staleness; needs an owner, monitoring, and a documented staleness contract, the same discipline applied to read-replica lag.

## Related

- syllabus/06-databases/query-planning-and-explain-analyze.md
- syllabus/06-databases/index-structures-btree-composite-covering.md
- syllabus/06-databases/sql-and-relational-database-fundamentals.md
