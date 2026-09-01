---
title: "LIKE Query Degradation as a Product Catalog Grows"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/system-design/search-and-indexing-systems.md
source: handbook/system-design/search-and-indexing-systems.md#production-scenarios
---

# LIKE Query Degradation as a Product Catalog Grows

## Context

A product-search feature had been built as an ordinary `WHERE description LIKE '%' || :term || '%'` query when the catalog was small enough that a full scan was still fast, with no explicit revisit trigger for when that assumption would stop holding.

## Symptoms

Product search latency grew roughly linearly with catalog size, eventually causing timeouts during peak traffic, while every other query against the same database stayed fast.

## Impact

Search timeouts during peak traffic degraded a customer-facing feature specifically at the moments of highest catalog browsing activity, while the rest of the database's workload was unaffected — isolating the cost to exactly one query pattern.

## Initial Hypotheses

- The database needed more CPU or a read replica — this was the first hypothesis pursued.

## Evidence

`EXPLAIN ANALYZE` on the actual search query showed a real sequential scan touching every row in the products table for every search, because the query used a leading wildcard (`'%term%'`) to support "contains" matching, which no standard B-tree index can serve.

## Investigation Timeline

1. **Search latency observed scaling with catalog size**, eventually causing peak-traffic timeouts while other queries against the same database stayed fast.
2. **Hardware/scaling hypothesis pursued first**, on the assumption the database needed more CPU or a read replica.
3. **`EXPLAIN ANALYZE` run on the actual query**, revealing a full sequential scan of the products table.
4. **Root cause isolated to the query shape**: a leading-wildcard `LIKE` pattern, which cannot be served by a standard B-tree index regardless of how much hardware is added.

## Root Cause

The search feature used a leading-wildcard `LIKE '%term%'` query to support "contains" matching. No standard B-tree index can serve a leading wildcard, so every search required a full sequential scan of the products table, and that scan's cost grew with the table's row count.

## Immediate Mitigation

Added a read replica to spread the scan cost, a stopgap that did not address the underlying O(n) scan behavior.

## Permanent Fix

Added a real `tsvector`/GIN full-text index directly on the existing PostgreSQL database, achieving a measured roughly 270x speedup for the same logical query — no new infrastructure needed, since the catalog's relevance-ranking needs were modest enough that a dedicated search engine wasn't yet justified.

## Alternatives Considered

Migrating to a dedicated search engine (e.g., Elasticsearch/OpenSearch). Not adopted at this stage because the catalog's relevance-ranking needs were modest and a `tsvector`/GIN index on the existing database delivered the needed speedup with zero new operational surface.

## Trade-offs

`tsvector`'s tokenization is less sophisticated than a dedicated search engine's — no fuzzy matching, weaker relevance tuning. This was accepted because it was a real, measured roughly 270x-class improvement with zero new operational surface.

## Prevention

Added a review checklist item flagging any new `LIKE '%...%'` query against a table expected to grow past a modest row count.

## Monitoring and Alerts

- Query latency tracked per query shape (not just aggregate database latency), so a single degrading query pattern is visible even while overall database health looks fine.
- A periodic `EXPLAIN ANALYZE` audit of hot-path queries against production-scale row counts, rather than only inspecting query plans at initial implementation time when the table was still small.

## Interview Story

This maps to a "why did our search feature slow down as we grew" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** product search latency grew with catalog size and eventually started timing out under peak traffic, while other queries stayed fast.
- **Task:** find why the slowdown was isolated to search specifically.
- **Action:** ruled out general database capacity; ran `EXPLAIN ANALYZE` and found a full sequential scan caused by a leading-wildcard `LIKE` pattern that no B-tree index could serve.
- **Result:** added a `tsvector`/GIN index on the existing database, achieving a measured roughly 270x speedup with no new infrastructure.

## Staff-Level Discussion

The fix here was not more hardware — it was recognizing that the query needed a genuinely different index structure, not a faster version of the same scan. This is a recurring architectural trap: a design choice (a simple `LIKE` query) that was correct for the system's initial scale has no built-in signal telling anyone when it stops being correct, since the query keeps returning right answers the whole time — only its cost profile changes, silently, as the table grows. The durable fix is a review checklist trigger tied to an observable property (table size, or the presence of a leading wildcard) rather than relying on someone noticing the latency trend before it becomes a customer-facing timeout; by the time the trend is visible in a latency dashboard, the query has usually already been slow for a while.

## Related Handbook Chapters

- [Search and Indexing Systems](../handbook/system-design/search-and-indexing-systems.md) — canonical `LIKE`-vs-GIN mechanism and full-text indexing trade-offs used here.
