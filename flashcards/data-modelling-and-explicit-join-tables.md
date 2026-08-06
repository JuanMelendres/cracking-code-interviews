---
title: "Flashcards: Data Modelling and Explicit Join Tables"
slug: data-modelling-and-explicit-join-tables
document_type: flashcard-deck
domain: databases
topic_id: T-605/T-608
canonical: ../handbook/databases/data-modelling-and-explicit-join-tables.md
last_updated: 2026-08-06
---

# Flashcards: Data Modelling and Explicit Join Tables

**Canonical chapter:** [`handbook/databases/data-modelling-and-explicit-join-tables.md`](../handbook/databases/data-modelling-and-explicit-join-tables.md)

## Card: What a plain join table cannot store

**Prompt:**
What can't a plain `@ManyToMany` join table store?

**Answer:**
Any fact about the relationship itself — no columns beyond the two foreign keys.

**Why it matters:**
The structural limit that forces promotion to an explicit entity.

**Common trap:**
Trying to add a column directly to a framework-generated join table.

**Related:**
[Internal Implementation](../handbook/databases/data-modelling-and-explicit-join-tables.md#internal-implementation)

## Card: The real trigger for an explicit join entity

**Prompt:**
What's the real trigger for an explicit join entity, precisely?

**Answer:**
Any fact that must be true "as of" relationship-formation time, not "as of" read time — not just "has an extra attribute."

**Why it matters:**
Catches the price-history case, which has no obvious extra attribute.

**Common trap:**
Only checking for an obviously-missing column, missing facts that depend on mutable referenced data.

**Related:**
[Core Concepts](../handbook/databases/data-modelling-and-explicit-join-tables.md#core-concepts)

## Card: The canonical silent-bug example

**Prompt:**
Give the canonical silent-bug example for a naive join table.

**Answer:**
An order line's price re-fetched live from `products` reports the *current* price for old orders once the product's price changes — the naive join table has no way to lock in the historical price.

**Why it matters:**
A real, measured, silent (no error thrown) data-integrity defect.

**Common trap:**
Assuming a missing feature always produces a visible error rather than a wrong number.

**Related:**
[Production Scenarios](../handbook/databases/data-modelling-and-explicit-join-tables.md#production-scenarios)
