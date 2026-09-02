---
title: "Flashcards: Hibernate Flush Modes and Batch Writes"
slug: hibernate-flush-modes-and-batch-writes
document_type: flashcard-deck
domain: databases
topic_id: T-606
canonical: ../handbook/databases/hibernate-flush-modes-and-batch-writes.md
last_updated: 2026-09-02
---

# Flashcards: Hibernate Flush Modes and Batch Writes

**Canonical chapter:** [`handbook/databases/hibernate-flush-modes-and-batch-writes.md`](../handbook/databases/hibernate-flush-modes-and-batch-writes.md)

## Card: Why IDENTITY defeats JDBC batching

**Prompt:**
Why does `hibernate.jdbc.batch_size` produce zero batching for an `IDENTITY`-generated entity?

**Answer:**
Hibernate must execute each `IDENTITY`-generated insert individually to retrieve the database-assigned key immediately — it cannot group several inserts into one batch before any of them has actually run, since it doesn't yet know their ids.

**Why it matters:**
`SEQUENCE`/`TABLE` generators can pre-allocate ids before the insert runs, so they batch fine — `IDENTITY` is structurally incompatible with batching, not a misconfiguration to be tuned away.

**Common trap:**
Assuming `batch_size` alone guarantees batched inserts regardless of the entity's id-generation strategy, and not checking `Statistics`'s JDBC-batch count to confirm batching is actually happening.

**Related:**
[handbook/databases/hibernate-flush-modes-and-batch-writes.md](../handbook/databases/hibernate-flush-modes-and-batch-writes.md)

## Card: Why FlushMode.COMMIT can miss the transaction's own write

**Prompt:**
Why can a query under `FlushMode.COMMIT` miss a change the same transaction just made?

**Answer:**
`FlushMode.COMMIT` only flushes at transaction commit, never before a query — the database still holds the pre-change value at query time, since the change genuinely hasn't been sent yet.

**Why it matters:**
The default `AUTO` flush mode flushes before a query that could be affected by pending changes; switching to `COMMIT` trades that safety for fewer round-trips, so the risk must be a deliberate choice, not a surprise.

**Common trap:**
Assuming Hibernate always synchronizes the persistence context with the database before every query, regardless of the configured flush mode.

**Related:**
[handbook/databases/hibernate-flush-modes-and-batch-writes.md](../handbook/databases/hibernate-flush-modes-and-batch-writes.md)

## Card: Bounding memory during bulk inserts

**Prompt:**
What's the standard pattern for bulk-inserting thousands of entities without unbounded memory growth?

**Answer:**
Periodically call `session.flush()` then `session.clear()` at a fixed interval (matching `batch_size`) — flush sends pending SQL, clear detaches those entities so the persistence context doesn't keep growing.

**Why it matters:**
Without periodic clearing, the persistence context accumulates every managed entity for the life of the session, turning a large bulk insert into an avoidable memory-growth problem.

**Common trap:**
Flushing without also clearing — flush alone sends the SQL but leaves every entity managed, so the persistence context still grows unbounded.

**Related:**
[handbook/databases/hibernate-flush-modes-and-batch-writes.md](../handbook/databases/hibernate-flush-modes-and-batch-writes.md)
