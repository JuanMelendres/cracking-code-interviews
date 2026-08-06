---
title: "Flashcards: Zero-Downtime Schema Migration"
slug: zero-downtime-schema-migration
document_type: flashcard-deck
domain: databases
topic_id: T-616
canonical: ../handbook/databases/zero-downtime-schema-migration.md
last_updated: 2026-08-06
---

# Flashcards: Zero-Downtime Schema Migration

**Canonical chapter:** [`handbook/databases/zero-downtime-schema-migration.md`](../handbook/databases/zero-downtime-schema-migration.md)

## Card: What a plain CREATE INDEX blocks

**Prompt:**
What lock does a plain `CREATE INDEX` hold, and what does it block?

**Answer:**
A `SHARE` lock on the table, held for the entire build duration — blocks writes (not reads) — measured at ~1943ms here.

**Why it matters:**
The specific mechanism that makes "just run CREATE INDEX" dangerous at scale.

**Common trap:**
Assuming index creation is always fast because it's fast on a small local table.

**Related:**
[Internal Implementation](../handbook/databases/zero-downtime-schema-migration.md#internal-implementation)

## Card: What CONCURRENTLY trades away

**Prompt:**
What does `CREATE INDEX CONCURRENTLY` trade for not blocking writes?

**Answer:**
A slower, multi-pass build, and the risk of a leftover `INVALID` index requiring manual cleanup on failure; also can't run inside an explicit transaction block.

**Why it matters:**
Not a free lunch — the write-blocking fix has its own operational cost.

**Common trap:**
Assuming `CONCURRENTLY` is strictly better with no downside.

**Related:**
[Trade-offs](../handbook/databases/zero-downtime-schema-migration.md#trade-offs)

## Card: Why direct column rename is unsafe

**Prompt:**
Why can't you just directly rename a column during a rolling deploy?

**Answer:**
Old and new application code versions run concurrently during the rollout; a direct rename breaks whichever version doesn't match — expand-contract keeps both working throughout.

**Why it matters:**
The core reason "fast at the catalog level" doesn't mean "safe."

**Common trap:**
Proposing a direct rename because it's fast and simple, ignoring the rolling-deploy mixed-version window.

**Related:**
[Core Concepts](../handbook/databases/zero-downtime-schema-migration.md#core-concepts)
