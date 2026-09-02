---
title: "Cheat Sheet: Hibernate Flush Modes and Batch Writes"
slug: hibernate-flush-modes-and-batch-writes
document_type: cheat-sheet
domain: databases
topic_id: T-606
canonical: ../handbook/databases/hibernate-flush-modes-and-batch-writes.md
last_updated: 2026-09-02
---

# Hibernate Flush Modes and Batch Writes

**Canonical chapter:** [`handbook/databases/hibernate-flush-modes-and-batch-writes.md`](../handbook/databases/hibernate-flush-modes-and-batch-writes.md)

## Core Mental Model

A flush is Hibernate synchronizing the persistence context to the database via SQL — controlling *when* it happens (`FlushMode`) is a separate lever from *what* gets flushed (dirty checking). Batching is a pure JDBC-level optimization: it requires Hibernate to know a row's primary key before sending it, which `IDENTITY` generation structurally cannot provide.

## Essential Definitions

- **`FlushMode.AUTO`** (default) — flushes before a query if its target tables overlap with anything currently dirty in the persistence context.
- **`FlushMode.COMMIT`** — only flushes at transaction commit, never before a query; a query can genuinely miss the transaction's own uncommitted change.
- **`hibernate.jdbc.batch_size`** — groups multiple `INSERT`/`UPDATE` statements into one `executeBatch()` call; requires the identifier to be known before the insert executes.
- **`IDENTITY` vs `SEQUENCE`/`TABLE`** — `SEQUENCE`/`TABLE` pre-allocate ids in memory, enabling batching; `IDENTITY` delegates key assignment to the database at insert time, structurally incompatible with batching.

## Decision Table

| Question | Answer |
|---|---|
| Entity gets bulk-inserted in meaningful volume? | Use `SEQUENCE`/`TABLE` generation and set `batch_size`, not `IDENTITY` |
| Application code queries data it just modified in the same transaction? | Keep `FlushMode.AUTO` — switching to `COMMIT` risks a real correctness bug |
| Specific, measured flush-frequency problem in a query-heavy path? | Consider `COMMIT`/`MANUAL` scoped narrowly, with explicit flushes where correctness requires |
| Large batch-insert loop holding thousands of managed entities? | Periodically `flush()` then `clear()` at a fixed interval to bound memory growth |

## Key Numbers

- Inserting 40 rows, `batch_size=10` identical for both: `SEQUENCE` entity → 5 real `executeBatch()` calls, 0 `executeUpdate()` calls. `IDENTITY` entity → 0 `executeBatch()` calls, 40 individual `executeUpdate()` calls.
- Renamed entity queried by new name: `FlushMode.AUTO` → 1 row found (auto-flushed first). `FlushMode.COMMIT` → 0 rows found (stale, pre-rename data).

## Common Pitfalls

- Setting `hibernate.jdbc.batch_size` and assuming it applies universally, without checking the entity's identifier generation strategy.
- Switching to `FlushMode.COMMIT` for a perceived performance win without auditing every query in that session for a read-your-own-writes dependency.
- Bulk-inserting thousands of entities in one transaction without periodic `flush()`/`clear()` — risks unbounded persistence-context memory growth.
- Assuming a batching problem is a Hibernate bug rather than checking `Statistics` output for the actual JDBC call pattern first.

## Interview Answer Skeleton

**30-sec:** `FlushMode` controls *when* Hibernate synchronizes changes — `AUTO` flushes before a query that could see pending changes; `COMMIT` only flushes at commit, so a query can miss the transaction's own uncommitted change. Separately, `batch_size` only works for identifier strategies (`SEQUENCE`, `TABLE`) that let Hibernate know the id before insert — it silently does nothing for `IDENTITY`.

**2-min:** Add the real, counted evidence: renaming an entity in-session and querying by the new name returns 1 row under `AUTO`, 0 under `COMMIT` — measured, not inferred. And the real batching proof via a custom dynamic-proxy `ConnectionProvider`: 5 `executeBatch()` calls for 40 `SEQUENCE` rows vs 40 individual `executeUpdate()` calls for 40 `IDENTITY` rows under the identical `batch_size` setting.

**Whiteboard:** `session.persist()` branching on identifier strategy. `SEQUENCE`/`TABLE` branch: "ID known before INSERT runs → grouped into one `executeBatch()`." `IDENTITY` branch: "ID only known after THIS insert runs → executed individually." Annotate with the measured 5-vs-40 call counts.

**Staff-level framing:** Identifier-generation strategy is a schema-design decision with a long tail — retrofitting `IDENTITY` to `SEQUENCE` on a live, high-volume table is a real migration project (backfilling sequence start values, coordinating cutover), not a one-line config change once the table has significant traffic.

## Production Warning Signs

- A nightly batch job loading 500,000 rows takes over an hour despite `hibernate.jdbc.batch_size=50` being set — `Statistics` shows "0 JDBC batches" and one round trip per row. Diagnosis: the entity used `GenerationType.IDENTITY`, structurally incompatible with batching. Fix: migrate to `GenerationType.SEQUENCE`.

## Related

- `handbook/databases/jpa-entity-lifecycle-and-the-n1-problem.md`
- `handbook/databases/hibernate-second-level-and-query-cache.md`
- `handbook/databases/connection-pooling-and-sizing.md`
