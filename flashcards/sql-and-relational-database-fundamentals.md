---
title: "Flashcards: SQL and Relational Database Fundamentals"
slug: sql-and-relational-database-fundamentals
document_type: flashcard-deck
domain: 06-databases
topic_id: T-2202
canonical: ../syllabus/06-databases/sql-and-relational-database-fundamentals.md
last_updated: 2026-09-18
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

## Card: FULL OUTER JOIN as a real union

**Prompt:**
What does a `FULL OUTER JOIN` return that neither `LEFT JOIN` nor `RIGHT JOIN` alone would?

**Answer:**
Both sides' unmatched rows at once — a row from the left table with no match on the right (which only `LEFT`/`FULL` keep), AND a row from the right table with no match on the left (which only `RIGHT`/`FULL` keep), in the same single result set. `FULL OUTER JOIN` is genuinely the union of what `LEFT` and `RIGHT` would each produce separately.

**Why it matters:**
A real data-reconciliation report ("which departments have no employees, AND which employees have no department") needs exactly this — either `LEFT` or `RIGHT` alone only answers half the question.

**Common trap:**
Assuming `LEFT JOIN` already covers "everything," when it only covers unmatched rows from one specific side.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: CROSS JOIN by accident

**Prompt:**
You write a `JOIN` between two tables but forget the `ON` condition. What actually happens in PostgreSQL?

**Answer:**
It silently executes as a `CROSS JOIN` — a Cartesian product, one row per (left row × right row) combination — not a rejected query. If the left table has 100 rows and the right has 50, the result has 5,000 rows, not an error.

**Why it matters:**
One of the most common real query-writing mistakes: a query that "runs fine" but returns a wildly, silently wrong, much larger result set than intended.

**Common trap:**
Assuming Postgres would reject a `JOIN` with no matching condition, rather than silently treating it as a Cartesian product.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: WHERE vs. HAVING — why you can't just use WHERE for both

**Prompt:**
Why does `WHERE COUNT(*) > 5` fail with a real error, while `HAVING COUNT(*) > 5` works fine?

**Answer:**
SQL's logical processing order runs `FROM` → `WHERE` → `GROUP BY` → `HAVING` → `SELECT`. `WHERE` executes *before* grouping/aggregation happens, so `COUNT(*)` doesn't exist yet at the point `WHERE` evaluates — it's a real syntax error, not a wrong-but-valid query. `HAVING` runs *after* aggregation specifically so it can filter on the aggregate's computed value.

**Why it matters:**
Understanding the logical order explains *why* the rule exists, rather than memorizing "WHERE can't use aggregates" as an arbitrary fact.

**Common trap:**
Treating `WHERE` and `HAVING` as interchangeable synonyms that happen to have different names.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: Candidate key vs. primary key vs. alternate key

**Prompt:**
A table has both `emp_id` (auto-incrementing integer) and `email` (declared `UNIQUE`), either of which could uniquely identify a row. What's the correct terminology for each?

**Answer:**
Both are **candidate keys** (minimal column sets that could uniquely identify a row). Whichever one is actually declared `PRIMARY KEY` (typically `emp_id`) is the **primary key**. The other candidate key that exists but wasn't chosen (`email`) is an **alternate key**, sometimes called a **secondary key** — still real and enforced (via `UNIQUE`), just not the table's official row identifier.

**Why it matters:**
A real, common interview gap: most candidates can define "primary key" but not "candidate key" or "alternate key," even though a real schema almost always has more than one candidate key.

**Common trap:**
Treating "primary key" and "candidate key" as synonyms.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: Natural key vs. surrogate key

**Prompt:**
Why default to an auto-generated `SERIAL` or `UUID` as a primary key instead of a real-world unique value like an email address?

**Answer:**
A natural key (email, ISBN, national ID) can turn out to be not-actually-unique later, or need to change — and a primary key referenced by foreign keys elsewhere is expensive to change once other tables depend on it. A surrogate key (a `SERIAL` integer or a `UUID`) has no real-world meaning, so it never needs to change for a real-world reason.

**Why it matters:**
A real, practical schema-design default, not just terminology — explains *why* most production schemas use generated IDs rather than "obviously unique" business data.

**Common trap:**
Assuming a value that looks permanently unique today (like an email) is safe to use as an unchangeable primary key.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: The four ACID guarantees, precisely

**Prompt:**
What does each letter in ACID actually guarantee, concretely?

**Answer:**
Atomicity: a transaction is all-or-nothing — one failed statement rolls back every statement already run in it. Consistency: no transaction can commit in a state that violates a declared constraint. Isolation: what one transaction can see of another's uncommitted changes, depending on the isolation level. Durability: once committed, data survives a real crash (via the write-ahead log).

**Why it matters:**
A near-universal Junior/Mid interview question that most candidates can expand the acronym for but can't tie to a concrete consequence.

**Common trap:**
Reciting the expanded acronym without stating what each guarantee actually prevents.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: What a trigger's RAISE EXCEPTION actually aborts

**Prompt:**
A `BEFORE UPDATE` trigger's function raises an exception partway through. What happens to the `UPDATE` statement that fired it?

**Answer:**
The entire `UPDATE` is aborted — not just the trigger's own side effect. Verified directly: a trigger rejecting an invalid new balance leaves the row's real value completely unchanged, and no audit-log row gets written for the rejected attempt either.

**Why it matters:**
The same all-or-nothing principle as Atomicity, applied to trigger logic specifically — a common point of confusion for candidates who assume a rejected trigger just "skips its own effect."

**Common trap:**
Assuming a failed trigger only prevents its own side effect while the original statement still succeeds.

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Card: NUMERIC vs. REAL/DOUBLE PRECISION for money

**Prompt:**
Why should a `salary` or `price` column use `NUMERIC(10,2)` instead of `REAL` or `DOUBLE PRECISION`?

**Answer:**
`NUMERIC`/`DECIMAL` is exact — no floating-point rounding error on repeated arithmetic. `REAL`/`DOUBLE PRECISION` are approximate binary floating-point types that can genuinely lose precision, which is unacceptable for money or any value where exact arithmetic matters.

**Why it matters:**
A real, common data-type choice mistake — floating-point types look interchangeable with exact-decimal types until repeated arithmetic reveals real rounding drift.

**Common trap:**
Picking `REAL`/`DOUBLE PRECISION` for money because it "looks like a decimal number type too."

**Related:**
[SQL and Relational Database Fundamentals](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)
