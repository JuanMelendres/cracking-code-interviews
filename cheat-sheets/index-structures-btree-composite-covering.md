---
title: "Cheat Sheet: Database Index Structures (B+Tree, Composite, Covering)"
slug: index-structures-btree-composite-covering
document_type: cheat-sheet
domain: databases
topic_id: T-609
canonical: ../handbook/databases/index-structures-btree-composite-covering.md
last_updated: 2026-08-03
---

# Database Index Structures: B+Tree, Composite, Covering

**Canonical chapter:** [`handbook/databases/index-structures-btree-composite-covering.md`](../handbook/databases/index-structures-btree-composite-covering.md)

## Core Mental Model

An index is a second, sorted copy of a narrow slice of your table, purpose-built to answer one shape of question fast. Three consequences: **it's a copy** (write-time tax to keep in sync); **it's sorted** (fast for equality/range only when the predicate is a leftmost prefix of the sort order); **it's narrow** (if the query needs a column the index lacks, the database fetches the row from the heap anyway).

## Essential Definitions

- **B+Tree index** — sorted, balanced, disk-page-aware tree; locates a row/range in O(log n) page reads instead of an O(n) sequential scan; PostgreSQL's default.
- **Composite index** `(A, B)` — one B+Tree sorted first by A, then by B within each A; governed by the leftmost-prefix rule.
- **Covering index** (`... INCLUDE (col)`) — stores extra columns in the index's leaf pages so a query can be answered without touching the heap.
- **Index-only scan** — when the query is fully satisfied by index columns, PostgreSQL can skip the heap — conditional on the visibility map being current, not automatic.
- **Partial index** — indexes only rows matching a condition (`WHERE status = 'pending'`); smaller, cheaper to maintain.
- **Expression index** — indexes a function's output (`UPPER(status)`), since a plain index can't serve a predicate wrapping the column in a function/cast.
- **Selectivity** — fraction of rows a predicate matches; below some threshold (roughly single digits to ~15%, depends on physical clustering) random I/O via the index costs more than one sequential scan.

## Decision Table

| Situation | What to reach for |
|---|---|
| Point lookup on one column | Single-column B-Tree index |
| Filter on A, sometimes A+B together | Composite index `(A, B)` — never `(B, A)` |
| Filter on B alone, unrelated to A | Separate index on B, or reconsider query shape |
| Query needs columns already in the index | `INCLUDE` extra columns for index-only scan (verify `Heap Fetches: 0`) |
| Predicate wraps column in function/cast | Expression index, or rewrite the predicate |
| Filter matches large fraction of table | Don't force an index |
| Plan doesn't match expectation | `ANALYZE` the table first |
| Adding an index to a huge production table | `CREATE INDEX CONCURRENTLY`, verify it didn't get left `INVALID` |

**Engine comparison:**

| Concept | PostgreSQL | MySQL/InnoDB | SQL Server |
|---|---|---|---|
| Clustered index | None — every table is a heap | PK **is** the clustered index | Optional, often the PK |
| Secondary index row pointer | Heap tuple ID (TID) | Primary key value | Row locator |
| Covering mechanism | `INCLUDE` columns (PG 11+) | Naturally includes PK | `INCLUDE` in nonclustered indexes |

## Key Numbers (real EXPLAIN ANALYZE, 300,000-row table, PostgreSQL 16)

- Before index: `Seq Scan`, **5.754ms**
- After `CREATE INDEX`: `Bitmap Heap Scan`, **0.111ms** — **~52x**
- Composite index, leading+trailing predicate: **0.056ms** (full use)
- Composite index, trailing-column-only predicate: falls back to `Seq Scan`, **6.094ms** — proves the leftmost-prefix rule
- Covering index, index-only scan: **0.015-0.016ms**, `Heap Fetches: 0`
- B+Tree lookup: O(log n) page reads; a multi-million-row table is typically only 3-4 levels deep (real trees fan out in the hundreds per page)

## Common Pitfalls

- Assuming "I added an index" is sufficient without checking `EXPLAIN`
- Indexing every column "just in case" (real write-time/storage cost)
- Forgetting `ANALYZE` after a large bulk load (stale statistics)
- Believing a covering index guarantees an index-only scan regardless of cost model or vacuum state

## Interview Answer Skeleton

**30-sec:** Index = sorted secondary structure (B+Tree by default) enabling O(log n) lookups instead of full scans; costs write-time maintenance/storage; only helps the query shapes it was built for — composite `(A,B)` serves A alone or A+B, never B alone.

**2-min:** Add why it exists (avoids ruinous full scans at scale), the write-time tax trade-off (15 indexes = 15 updates per insert), and the 5.7ms→0.1ms production example via `CREATE INDEX CONCURRENTLY`.

**Whiteboard:** Draw the tree (root with routing keys → internal nodes → leaves), label leaves "→ heap TID," draw a separate heap box with arrows (the step candidates most often skip) — then draw the composite-index sorted-list example and cross out the standalone trailing-column lookup path.

## Production Warning Signs

- `Rows Removed by Filter` (large, nonzero) in a `Seq Scan` — the tell an index would help
- `Index Only Scan` expected but heap-fetch plan appears — check visibility map / `VACUUM` status
- Query got *slower* after adding an index — write amplification or planner-statistics skew
- **Real incident:** p99 tripled after a routine Hibernate minor-version upgrade, no schema change — root cause was generated SQL silently wrapping `status` in `CAST(status AS text)`, defeating the existing index. Diagnosed via `pg_stat_statements` (mean_exec_time up ~40x). Fixed with an expression index built `CONCURRENTLY`; prevented via `EXPLAIN`-plan CI assertions.

## Related

- `handbook/databases/query-planning-and-explain-analyze.md`
- [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md)
