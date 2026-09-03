---
title: "Flashcards: Database Index Structures — B+Tree, Composite, Covering"
slug: index-structures-btree-composite-covering
document_type: flashcard-deck
domain: databases
topic_id: T-609
canonical: ../handbook/databases/index-structures-btree-composite-covering.md
last_updated: 2026-08-06
---

# Flashcards: Database Index Structures — B+Tree, Composite, Covering

**Canonical chapter:** [`syllabus/06-databases/index-structures-btree-composite-covering.md`](../syllabus/06-databases/index-structures-btree-composite-covering.md)

## Card: B+Tree lookup path

**Prompt:**
Describe the B+Tree lookup path for an indexed query, root to result, in one sentence.

**Answer:**
Root → internal routing nodes (keys only) → leaf node → heap tuple ID → heap page fetch for the full row, in `O(log n)` comparisons — unless the index is covering, in which case the heap fetch is skipped.

**Why it matters:**
Distinguishes "recited the word B-Tree" from actually understanding the physical I/O reasoning interviewers probe for.

**Common trap:**
Describing a plain binary search tree, or omitting the final heap-fetch step.

**Related:**
[Internal Implementation](../syllabus/06-databases/index-structures-btree-composite-covering.md#internal-implementation)

## Card: Leftmost-prefix rule

**Prompt:**
What queries does a composite index on `(customer_id, created_at)` actually serve?

**Answer:**
`customer_id` alone, and `customer_id` + `created_at` together — never `created_at` alone.

**Why it matters:**
The single most common real-world index-design defect.

**Common trap:**
Assuming an index on `(A, B)` helps any query touching either column.

**Related:**
[Core Concepts](../syllabus/06-databases/index-structures-btree-composite-covering.md#core-concepts)

## Card: Proof of an index-only scan

**Prompt:**
What does `EXPLAIN` show when an index-only scan actually happens, versus merely being possible?

**Answer:**
Node type `Index Only Scan` with `Heap Fetches: 0` — a covering index alone doesn't guarantee this; it also requires the visibility map to be current (recent `VACUUM`) and the planner's cost model to favor it.

**Why it matters:**
Prevents overclaiming a "covering index" benefit that the planner never actually took.

**Common trap:**
Assuming a covering index automatically produces an index-only scan.

**Related:**
[Execution Flow](../syllabus/06-databases/index-structures-btree-composite-covering.md#execution-flow)

## Card: Two mechanisms for "index made it slower"

**Prompt:**
Name two distinct reasons adding an index can make a query slower.

**Answer:**
(1) Write amplification on every insert/update/delete. (2) The new index shifted planner statistics/candidate plans toward a worse choice, pending fresh `ANALYZE`.

**Why it matters:**
Most candidates name only one; naming both unprompted is a Senior/Staff signal.

**Common trap:**
Stopping at write amplification alone.

**Related:**
[Failure Modes and Debugging](../syllabus/06-databases/index-structures-btree-composite-covering.md#failure-modes-and-debugging)

## Card: PostgreSQL vs InnoDB clustering

**Prompt:**
Does PostgreSQL have a clustered index the way InnoDB does?

**Answer:**
No. Every PostgreSQL table is a heap; every index, including the primary key's, is secondary and points at a heap TID. InnoDB's primary key *is* the clustered index, storing rows in PK order.

**Why it matters:**
Using "clustered index" loosely for PostgreSQL is a specific, previously-flagged terminology error.

**Common trap:**
Assuming `CLUSTER` keeps the table physically ordered after the command runs — it doesn't; the next write breaks it.

**Related:**
[Comparisons](../syllabus/06-databases/index-structures-btree-composite-covering.md#comparisons)
