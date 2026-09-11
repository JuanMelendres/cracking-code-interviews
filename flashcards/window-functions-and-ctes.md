---
title: "Flashcards: Window Functions and CTEs"
slug: window-functions-and-ctes
document_type: flashcard-deck
domain: 06-databases
topic_id: T-2401
canonical: ../syllabus/06-databases/window-functions-and-ctes.md
last_updated: 2026-09-11
---

# Flashcards: Window Functions and CTEs

**Canonical chapter:** [`syllabus/06-databases/window-functions-and-ctes.md`](../syllabus/06-databases/window-functions-and-ctes.md)

## Card: GROUP BY vs. window function

**Prompt:**
What's the fundamental difference between `GROUP BY` and a window function?

**Answer:**
`GROUP BY` collapses rows into one summarized row per group — the original rows are gone. A window function keeps every original row, adding a computed value derived from a defined neighborhood of related rows.

**Why it matters:**
The core distinction that explains why you'd reach for one over the other.

**Common trap:**
Trying to get per-row detail alongside an aggregate using only `GROUP BY`, requiring an awkward self-join instead of a window function.

**Related:**
[Window Functions and CTEs](../syllabus/06-databases/window-functions-and-ctes.md)

## Card: ROW_NUMBER() vs. RANK() with ties

**Prompt:**
For "top 2 per department" with a tie for 2nd place, do `ROW_NUMBER()` and `RANK()` return the same number of rows?

**Answer:**
No — real, verified evidence shows `ROW_NUMBER()` returns 4 rows (arbitrarily picks one of the tied rows) while `RANK()` returns 6 rows (both tied rows share rank 2, both included).

**Why it matters:**
A concrete way to demonstrate you understand the distinction isn't cosmetic — it changes actual query results.

**Common trap:**
Assuming `ROW_NUMBER()` and `RANK()` are interchangeable for "top N" queries.

**Related:**
[Window Functions and CTEs](../syllabus/06-databases/window-functions-and-ctes.md)

## Card: What a recursive CTE needs

**Prompt:**
What two things must a `WITH RECURSIVE` query define to correctly walk a hierarchy?

**Answer:**
A base case (the starting point/anchor) and a recursive step (how to find the next level from any given row) — the database repeatedly applies the recursive step to its own growing result set until no new rows are produced.

**Why it matters:**
Missing either piece produces either an empty result or an unbounded/infinite recursion.

**Common trap:**
Writing a recursive step with no genuine termination condition, risking runaway recursion.

**Related:**
[Window Functions and CTEs](../syllabus/06-databases/window-functions-and-ctes.md)
