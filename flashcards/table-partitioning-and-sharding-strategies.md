---
title: "Flashcards: Table Partitioning and Sharding Strategies"
slug: table-partitioning-and-sharding-strategies
document_type: flashcard-deck
domain: databases
topic_id: T-614
canonical: ../handbook/databases/table-partitioning-and-sharding-strategies.md
last_updated: 2026-08-06
---

# Flashcards: Table Partitioning and Sharding Strategies

**Canonical chapter:** [`handbook/databases/table-partitioning-and-sharding-strategies.md`](../handbook/databases/table-partitioning-and-sharding-strategies.md)

## Card: What partition pruning requires

**Prompt:**
What does partition pruning require to work?

**Answer:**
The query must filter by the partition/shard key — anything else fans out to every partition.

**Why it matters:**
The single condition that determines whether sharding actually helps a given query.

**Common trap:**
Assuming sharding speeds up every query, not just ones filtering by the key.

**Related:**
[Internal Implementation](../handbook/databases/table-partitioning-and-sharding-strategies.md#internal-implementation)

## Card: Why shard-key selection is a one-way door

**Prompt:**
Why is shard-key selection called a "one-way door"?

**Answer:**
Changing it after data exists requires physically migrating the data, not a configuration change.

**Why it matters:**
The reason shard-key selection deserves design-time rigor comparable to a public API contract.

**Common trap:**
Treating shard-key changes as a quick reconfiguration.

**Related:**
[Production Scenarios](../handbook/databases/table-partitioning-and-sharding-strategies.md#production-scenarios)

## Card: Postgres HASH partitioning's hidden cost

**Prompt:**
Does Postgres's own HASH partitioning avoid the naive `hash % N` remapping problem?

**Answer:**
No — changing partition count remaps nearly all data, the same underlying math as sharding's `hash % N` problem, just one layer down.

**Why it matters:**
A common assumption (database feature = safer) that doesn't hold here.

**Common trap:**
Assuming a built-in database feature is automatically immune to a well-known distributed-systems problem.

**Related:**
[Core Concepts](../handbook/databases/table-partitioning-and-sharding-strategies.md#core-concepts)
