---
title: "Flashcards: SQL and Relational Database Fundamentals"
slug: sql-and-relational-database-fundamentals
document_type: flashcard-deck
domain: 06-databases
topic_id: T-2202
canonical: ../syllabus/06-databases/sql-and-relational-database-fundamentals.md
last_updated: 2026-09-07
---

# Flashcards: SQL and Relational Database Fundamentals

**Canonical chapter:** [`syllabus/06-databases/sql-and-relational-database-fundamentals.md`](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: Primary key vs. foreign key

**Prompt:**
What's the difference between a primary key and a foreign key?

**Answer:**
A primary key uniquely identifies each row in its own table (no duplicates, never empty), and gets an automatic unique index. A foreign key is a column referencing another table's primary key, and the database enforces that the referenced row actually exists.

**Why it matters:**
The single most basic relational-database distinction, checked constantly in follow-up questions.

**Common trap:**
Describing a foreign key as just "a reference" without mentioning the database enforces it.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: INNER JOIN vs. LEFT JOIN — the real difference

**Prompt:**
An author has zero books. What happens to that author's row under `INNER JOIN authors a ON b.author_id = a.author_id` vs. `LEFT JOIN`?

**Answer:**
Under `INNER JOIN`, the author row disappears entirely — there's no book row to join against. Under `LEFT JOIN`, the author row survives, with the book columns coming back `NULL`.

**Why it matters:**
The concrete, row-level consequence of a join type — not just syntax, but which rows actually come back.

**Common trap:**
Describing the difference only in terms of syntax, not which rows survive.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: Foreign key actually rejecting a bad insert

**Prompt:**
What happens if you `INSERT INTO books (author_id) VALUES (999)` and no author with id 999 exists?

**Answer:**
The database rejects it with a real constraint-violation error (`violates foreign key constraint ... Key (author_id)=(999) is not present in table "authors"`) — the write never happens.

**Why it matters:**
Proves the foreign key is an enforced guarantee, not a documentation convention.

**Common trap:**
Believing a foreign key is just a naming/documentation convention rather than an enforced database-level check.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: GROUP BY + COUNT with LEFT JOIN, correctly reporting zero

**Prompt:**
Why does a report of "books per author" need `LEFT JOIN`, not `INNER JOIN`, to correctly show an author with zero books?

**Answer:**
`INNER JOIN` drops the zero-books author's row before `GROUP BY`/`COUNT` ever see it — the author is silently absent from the report. `LEFT JOIN` keeps the row (with `NULL` book columns), so `COUNT` correctly reports `0` for that author.

**Why it matters:**
A very common, easy-to-miss bug: the report looks complete but silently omits exactly the rows a count-based report most needs to include.

**Common trap:**
Assuming `INNER JOIN` would just show `0` for a non-matching row instead of omitting it entirely.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: NULL = NULL is not true

**Prompt:**
Does `WHERE some_column = NULL` correctly find rows where `some_column` is `NULL`?

**Answer:**
No — in SQL, `NULL = NULL` evaluates to `NULL` (neither true nor false), not `true`. Testing for `NULL` requires `IS NULL`.

**Why it matters:**
A real, common bug source, especially after a `LEFT JOIN` produces `NULL` columns that then need filtering.

**Common trap:**
Writing `= NULL` and expecting standard equality semantics.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)
