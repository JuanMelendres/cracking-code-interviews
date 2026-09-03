---
title: "Cheat Sheet: Query Planning and EXPLAIN ANALYZE"
slug: query-planning-and-explain-analyze
document_type: cheat-sheet
domain: databases
topic_id: T-610
canonical: ../handbook/databases/query-planning-and-explain-analyze.md
last_updated: 2026-08-03
---

# Query Planning and EXPLAIN ANALYZE

**Canonical chapter:** [`syllabus/06-databases/query-planning-and-explain-analyze.md`](../syllabus/06-databases/query-planning-and-explain-analyze.md)

## Core Mental Model

A query plan is a bet, and `ANALYZE` is the only way to see if the bet paid off. The planner enumerates structurally different ways to execute the same SQL (scan types, join algorithms, join orders), assigns each an estimated cost from table statistics, and executes the cheapest one it can find. `EXPLAIN` shows the bet; `EXPLAIN ANALYZE` shows the bet *and* what actually happened. Never trust the estimate alone, and never predict a fix's payoff without checking which part of the plan actually dominates the measured time.

## Essential Definitions

- **`EXPLAIN`** — displays the planner's chosen execution plan: a tree of physical operations, each annotated with estimated cost, row count, width.
- **`EXPLAIN ANALYZE`** — actually **executes** the query, augmenting every node with real elapsed time and real row counts.
- **Cardinality estimation gap** — `rows=` (estimate) vs `actual rows=` (measured); an order-of-magnitude+ gap means stale statistics or unmodeled column correlation.
- **Seq scan** — reads every page of a table. **Index scan** — walks the index, fetches each match individually. **Bitmap heap scan** — builds a bitmap of matches from the index, visits heap pages in physical order (better than plain index scan for many scattered matches). **Index-only scan** — covering index, no heap visit at all.

## Decision Table (join algorithms)

| Algorithm | Wins when | Cost shape |
|---|---|---|
| Nested loop | One side is small, or a highly selective index lookup exists per outer row | O(outer rows × cost of inner lookup) |
| Hash join | Neither side small enough for cheap nested loop; equality condition | Build hash table over smaller side once, O(n+m) |
| Merge join | Both sides already sorted (or cheaply sortable) on the join key | O(n log n + m log m) for sorts, O(n+m) for merge |

**Situation → what to check:**

| Situation | What to check |
|---|---|
| Query reported slow | Run `EXPLAIN ANALYZE`, find the node where cost concentrates |
| Estimate/actual mismatch | Stale statistics — run `ANALYZE` on the table |
| Seq scan despite an index existing | Low selectivity, or a function/cast wraps the column |
| Added an index, no improvement | Check whether the indexed side was ever the bottleneck |
| Suspect the wrong join was chosen | Compare against `SET enable_hashjoin = off` (diagnostic only, never ship) |
| Fast in staging, slow in production | Data volume/distribution differ — never trust staging alone |

## Key Numbers (real, PostgreSQL 16 — 300K-row `orders`, 5K-row `customers`)

- **Scenario 1** (missing index, modest payoff): 18.446ms → 18.074ms (**~1.02x**) — the new index sped up the small hash-build side, which was never the bottleneck (hash join still reads all 300K rows either way)
- **Scenario 2** (function-wrapped predicate): 21.614ms → 5.805ms (**~3.7x**) — replaced a seq scan defeated by `UPPER(status)` with an expression index
- **Scenario 3** (forced nested loop vs. free hash-join choice): 47.811ms (forced) → 35.048ms (planner's free choice, **~1.4x**)
- This range (~1.02x, ~3.7x, ~1.4x) is deliberately unglamorous compared to the index-structures chapter's ~52x point-lookup win — most real production query tuning looks like *this*, not a textbook

## Common Pitfalls

- Reading only the top line of a plan and missing where actual cost concentrates
- Assuming an added index helps without checking which side actually dominates measured cost
- Treating `EXPLAIN` (without `ANALYZE`) as sufficient for diagnosis
- Using `SET enable_hashjoin = off`-style overrides as a production fix rather than diagnostic-only

## Interview Answer Skeleton

**30-sec:** `EXPLAIN` shows the chosen plan; `EXPLAIN ANALYZE` runs it and adds real timings/row counts. Two things matter most: find the node where cost concentrates, and check estimated vs. actual row count.

**2-min:** Add why the planner exists (SQL is declarative, many execution strategies) + the trust-the-planner's-free-choice trade-off + the modest 18.4ms→18.1ms "obvious index that didn't touch the bottleneck" example.

**Whiteboard:** Draw the pipeline (SQL → Parser → Planner → cheapest wins → Executor), then a small plan tree with estimated rows stacked above actual rows per node, leaving a visible gap on one node — "the gap is the diagnosis."

## Production Warning Signs

- Stale statistics corrupt every downstream planner decision without any error — "the plan simply looks confident and is wrong"
- `EXPLAIN ANALYZE` **executes** the query, including writes for DELETE/UPDATE/INSERT — wrap in a transaction with rollback, or restrict to read-only in production
- Hash join build side spilling to disk shows as `Batches: N` (N > 1) with a jump in execution time
- **Real incident:** dashboard p95 latency tripled after an unindexed `region` filter was added. Diagnosed via `pg_stat_statements` + `EXPLAIN ANALYZE`, fixed with `CREATE INDEX CONCURRENTLY`.

## Related

- [Database Index Structures](index-structures-btree-composite-covering.md)
- [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md)
