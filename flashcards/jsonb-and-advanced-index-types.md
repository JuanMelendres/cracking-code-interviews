---
title: "Flashcards: JSONB and Advanced Index Types"
slug: jsonb-and-advanced-index-types
document_type: flashcard-deck
domain: 06-databases
topic_id: T-2402
canonical: ../syllabus/06-databases/jsonb-and-advanced-index-types.md
last_updated: 2026-09-11
---

# Flashcards: JSONB and Advanced Index Types

**Canonical chapter:** [`syllabus/06-databases/jsonb-and-advanced-index-types.md`](../syllabus/06-databases/jsonb-and-advanced-index-types.md)

## Card: GIN isn't always a win

**Prompt:**
Does adding a GIN index on a JSONB column always improve query performance?

**Answer:**
No — a real, measured low-selectivity case shows a GIN-indexed lookup regressing from 11.988ms to 13.896ms versus no index. Selectivity determines whether GIN actually helps.

**Why it matters:**
A concrete counterexample to "just add a GIN index" as a default reflex.

**Common trap:**
Adding a GIN index without measuring selectivity or actual query performance first.

**Related:**
[JSONB and Advanced Index Types](../syllabus/06-databases/jsonb-and-advanced-index-types.md)

## Card: GiST for overlap prevention

**Prompt:**
How do you prevent two overlapping bookings from ever being inserted concurrently, atomically, at the database level?

**Answer:**
A GiST `EXCLUDE` constraint — real, atomic evidence shows it correctly rejects an overlapping booking, unlike an application-level check-then-insert which loses atomicity under concurrent writes.

**Why it matters:**
A real, correct alternative to a race-prone application-level uniqueness check.

**Common trap:**
Relying on an application-level "check for overlap, then insert" sequence instead of a database-enforced constraint.

**Related:**
[JSONB and Advanced Index Types](../syllabus/06-databases/jsonb-and-advanced-index-types.md)

## Card: What BRIN actually indexes

**Prompt:**
Does a BRIN index store an entry per row, like a B-tree or GIN index does?

**Answer:**
No — BRIN stores a tiny summary (min/max) per physical storage block range, not per row. It only helps when the table is physically ordered by a column correlated with the indexed value.

**Why it matters:**
Explains both BRIN's real ~1,834×-smaller size advantage and why it fails silently (or gets ignored by the planner) on an unordered table.

**Common trap:**
Adding a BRIN index expecting B-tree-like per-row precision.

**Related:**
[JSONB and Advanced Index Types](../syllabus/06-databases/jsonb-and-advanced-index-types.md)
