---
title: "Cheat Sheet: SQL and Relational Database Fundamentals"
slug: sql-and-relational-database-fundamentals
document_type: cheat-sheet
domain: 06-databases
topic_id: T-2202
canonical: ../syllabus/06-databases/sql-and-relational-database-fundamentals.md
last_updated: 2026-09-07
---

# SQL and Relational Database Fundamentals

**Canonical chapter:** [`syllabus/06-databases/sql-and-relational-database-fundamentals.md`](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Core Mental Model

Data lives in tables; relationships between tables are expressed by one table referencing another's primary key via a foreign key — the database enforces that reference exists, not application code.

## Essential Definitions

- **Primary key** — uniquely identifies each row; the database rejects a duplicate or empty one, and automatically builds a unique index on it.
- **Foreign key** — a column referencing another table's primary key; the database rejects an insert/update pointing at a value that doesn't exist there.
- **`INNER JOIN`** — returns only rows with a match in both tables; an unmatched row disappears entirely.
- **`LEFT JOIN`** — returns every row from the left table, unmatched columns filled with `NULL`.
- **Normalization** — store each fact once, express relationships via foreign keys rather than repeating data.

## Decision Table

| Situation | Use |
|---|---|
| The question only makes sense for rows that have a match | `INNER JOIN` |
| The question needs to account for "zero of something" (e.g., a count report including zero-match rows) | `LEFT JOIN` |
| Testing for a `NULL` result | `IS NULL`, never `= NULL` (`NULL = NULL` evaluates to `NULL`, not `true`) |

## Common Pitfalls

- Skipping a foreign key, relying on application code to keep two tables consistent — a bug can silently insert an orphaned row with no error.
- Using `INNER JOIN` when the report needs to include "zero of something" — silently drops exactly the rows that should show a zero count.
- Assuming `DELETE` on a referenced row succeeds automatically — fails with a foreign key violation by default unless the schema opts into cascade/set-null.
- Comparing `NULL` with `=` and expecting a match.

## Interview Answer Skeleton

**30-sec:** A primary key uniquely identifies a row and gets an automatic index; a foreign key references another table's primary key and is enforced by the database, not application code. `INNER JOIN` drops unmatched rows; `LEFT JOIN` keeps them with `NULL`s.

**2-min:** Add a concrete example — a `GROUP BY` + `COUNT` report using `LEFT JOIN` correctly shows `0` for an entity with no matches, while the same query with `INNER JOIN` would silently omit that entity from the report entirely (not show it as zero — omit it).

**Whiteboard:** Draw two tables with an arrow from the foreign-key column to the primary-key column it references. Draw the same join condition run two ways — one diagram where an unmatched row vanishes (`INNER`), one where it survives with a dashed/`NULL` cell (`LEFT`).

**Staff-level framing:** A foreign key enforced at the database is the one enforcement point every writer — present and future, across every service — is forced through; an application-level check must be independently re-implemented correctly everywhere data enters the table, the same "one canonical enforcement point" argument [Clean and Hexagonal Architecture](../syllabus/17-architecture/clean-hexagonal-architecture.md) makes for business rules.

## Related

- syllabus/06-databases/data-modelling-and-explicit-join-tables.md
- syllabus/06-databases/index-structures-btree-composite-covering.md
- syllabus/06-databases/query-planning-and-explain-analyze.md
