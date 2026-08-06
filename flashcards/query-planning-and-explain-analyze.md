---
title: "Flashcards: Query Planning and EXPLAIN ANALYZE"
slug: query-planning-and-explain-analyze
document_type: flashcard-deck
domain: databases
topic_id: T-610
canonical: ../handbook/databases/query-planning-and-explain-analyze.md
last_updated: 2026-08-06
---

# Flashcards: Query Planning and EXPLAIN ANALYZE

**Canonical chapter:** [`handbook/databases/query-planning-and-explain-analyze.md`](../handbook/databases/query-planning-and-explain-analyze.md)

## Card: Most useful EXPLAIN flag combination

**Prompt:**
What's the single most useful `EXPLAIN` flag combination for diagnosing a slow query?

**Answer:**
`(ANALYZE, BUFFERS)` — real timings and real row counts, plus real I/O buffer counts.

**Why it matters:**
`EXPLAIN` alone shows only estimates; without `ANALYZE` a stale-statistics problem is invisible.

**Common trap:**
Reading `EXPLAIN` output without `ANALYZE` and treating the estimate as fact.

**Related:**
[Core Concepts](../handbook/databases/query-planning-and-explain-analyze.md#core-concepts)

## Card: Estimate-vs-actual mismatch

**Prompt:**
What does a large gap between estimated and actual row counts in a plan usually mean?

**Answer:**
Stale statistics, or a correlation between columns the planner's per-column statistics can't capture.

**Why it matters:**
This single number is frequently the entire diagnosis for a misbehaving plan.

**Common trap:**
Not noticing the mismatch, or treating the estimate as measured.

**Related:**
[Internal Implementation](../handbook/databases/query-planning-and-explain-analyze.md#internal-implementation)

## Card: Function-wrapped predicate

**Prompt:**
Why can't a plain index serve `WHERE UPPER(col) = ?`?

**Answer:**
The index is built on raw column values; it has no entry matching the function's output. Needs an expression index, or a rewritten predicate.

**Why it matters:**
The most common plan-defeating mistake introduced by ORM-generated SQL.

**Common trap:**
Assuming any index on the column helps, regardless of how the predicate is written.

**Related:**
[Internal Implementation](../handbook/databases/query-planning-and-explain-analyze.md#internal-implementation)

## Card: Nested loop beating a hash join

**Prompt:**
When does a nested loop beat a hash join despite a large outer side?

**Answer:**
When the inner-side lookups are cheap (indexed) and repeated values let `Memoize` turn many of them into cache hits instead of fresh probes.

**Why it matters:**
Prevents the "hash joins are always faster" oversimplification.

**Common trap:**
Claiming one join algorithm is universally faster regardless of data shape.

**Related:**
[Internal Implementation](../handbook/databases/query-planning-and-explain-analyze.md#internal-implementation)
