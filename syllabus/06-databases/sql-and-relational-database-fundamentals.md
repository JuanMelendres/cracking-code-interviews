---
title: "SQL and Relational Database Fundamentals"
slug: sql-and-relational-database-fundamentals
document_type: syllabus-topic
domain: 06-databases
topic_id: T-2202
status: canonical
version: 1.0
last_updated: 2026-09-07
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites: []
related:
  - data-modelling-and-explicit-join-tables.md
  - index-structures-btree-composite-covering.md
  - query-planning-and-explain-analyze.md
practice: ../../practice/sql/sql-fundamentals/
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://www.postgresql.org/docs/current/tutorial-table.html
  - https://www.postgresql.org/docs/current/tutorial-join.html
  - https://www.postgresql.org/docs/current/ddl-constraints.html
---

# SQL and Relational Database Fundamentals

## Table of Contents

1. [Why This Matters](#1-why-this-matters)
2. [Prerequisites](#2-prerequisites)
3. [Foundation (L1)](#3-foundation-l1)
4. [Core Concepts (L2)](#4-core-concepts-l2)
5. [How It Works Internally (L3)](#5-how-it-works-internally-l3)
6. [Practical Usage](#6-practical-usage)
7. [Examples](#7-examples)
8. [Common Mistakes](#8-common-mistakes)
9. [Edge Cases](#9-edge-cases)
10. [Performance Implications](#10-performance-implications)
11. [Trade-offs](#11-trade-offs)
12. [Senior-Level Considerations (L3)](#12-senior-level-considerations-l3)
13. [Staff/System-Level Considerations (L4)](#13-staffsystem-level-considerations-l4)
14. [Production Scenarios](#14-production-scenarios)
15. [Interview Questions](#15-interview-questions)
16. [Coding/Practice Exercises](#16-codingpractice-exercises)
17. [Debugging Exercises](#17-debugging-exercises)
18. [Design Exercises](#18-design-exercises)
19. [Further Reading](#19-further-reading)
20. [Mastery Checklist](#20-mastery-checklist)

## 1. Why This Matters

Every other chapter in `06-databases` assumes you can already write a `SELECT` with a `JOIN` and know what a foreign key does — reasonable for the Senior/Staff-only version of this repository, not reasonable once this project explicitly covers Junior through Staff. This chapter is that missing floor, the same role [Java OOP Fundamentals](../02-java/language-core/java-oop-fundamentals-classes-objects-and-interfaces.md) plays for `02-java`. A candidate who cannot explain what a primary key actually guarantees, or why a `LEFT JOIN` can produce more rows than an `INNER JOIN` on the identical query, will stall the moment an interviewer asks a single follow-up question past "write a query that returns X" — and every advanced chapter in this domain (index internals, isolation levels, replication) silently assumes this floor is already solid.

## 2. Prerequisites

None. This is a true entry point into `06-databases`.

## 3. Foundation (L1)

A relational database stores data in **tables** — think of a table as a spreadsheet with a fixed set of named, typed **columns**, where every **row** is one record. An `authors` table might have columns `author_id`, `name`; every author in the system is one row in that table. This is the entire relational model in one sentence: data lives in tables, and relationships between tables are expressed by one table referencing another's identifying column — not by nesting one record inside another the way a document or an object graph would.

A **primary key** is the column (or columns) that uniquely identifies each row in a table — no two rows can share one, and it can never be empty. `author_id` as a primary key guarantees the database itself will refuse a second row with the same `author_id`, rather than trusting every piece of application code to remember not to create a duplicate.

A **foreign key** is a column in one table that references a primary key in another, and it is how relationships are actually expressed. `books.author_id` referencing `authors.author_id` says, in a rule the database itself enforces: "every book row must point at an author row that genuinely exists." Section 7's Example F shows this rule being enforced directly — the database rejecting an attempt to insert a book with an author ID that isn't real, with no application code involved in catching the mistake.

The four operations you do to data are commonly abbreviated **CRUD**: `INSERT` (Create), `SELECT` (Read), `UPDATE`, and `DELETE`. Every one of Section 7's demos runs at least one of these against a real, disposable PostgreSQL database.

## 4. Core Concepts (L2)

A **`JOIN`** combines rows from two tables based on a matching condition — almost always "this table's foreign key equals that table's primary key." The two most common kinds answer genuinely different questions, not just stylistic variants of the same query:

- **`INNER JOIN`** returns only the rows that have a match in *both* tables. An author with zero books simply does not appear in the result at all — there is no book row to join against.
- **`LEFT JOIN`** returns *every* row from the left-hand table, whether or not it has a match — an author with zero books still appears, with the joined columns filled in as `NULL`.

Section 7's Example G runs the identical join condition both ways against the identical data specifically to make this concrete: one extra author (with zero books) appears in the `LEFT JOIN` result and is entirely absent from the `INNER JOIN` result — the same underlying fact, seen from opposite directions.

**Normalization**, at the level this chapter needs, is the practice of storing each fact exactly once and expressing relationships through foreign keys rather than by repeating data. Storing an author's name directly on every one of their book rows means updating that author's name requires updating every book row too, and risks the rows disagreeing with each other; storing `author_id` on `books` and looking the name up through a `JOIN` when needed means the name exists in exactly one place. [Data Modelling and Explicit Join Tables](data-modelling-and-explicit-join-tables.md) takes this idea further, into many-to-many relationships this chapter doesn't cover.

## 5. How It Works Internally (L3)

A `PRIMARY KEY` constraint is not just a naming convention — declaring one causes PostgreSQL to automatically build a unique index on that column (visible directly in Section 7 Example A's `\d` output: `"authors_pkey" PRIMARY KEY, btree (author_id)`), and every `INSERT` or `UPDATE` checks that index before committing, rejecting the operation if the value already exists. A `FOREIGN KEY` constraint works by the referencing table's write path checking the referenced table's primary-key index at the moment of the write — Section 7 Example F's rejected insert is that check firing in real time, not a delayed validation step.

A `JOIN` is executed by the database's own query planner, not by your query dictating a specific algorithm — the same `INNER JOIN` syntax might run as a nested-loop join, a hash join, or a merge join depending on table sizes and available indexes, a decision [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md) covers in full. What matters at this level is only that the *result* (which rows come back) is fully determined by `INNER` vs. `LEFT`, while the *execution strategy* used to produce that result is an internal decision the planner makes independently, invisible in the query's own syntax.

## 6. Practical Usage

Always give every table a primary key, even when nothing seems to require one yet — a table without one cannot reliably be referenced by a future foreign key, and cannot be safely updated or deleted by "this exact row" later. Default to expressing every real relationship between tables as an explicit foreign key rather than trusting application code to keep two tables consistent by convention — Section 8's most common mistake is exactly this shortcut. Reach for `LEFT JOIN` specifically when the answer needs to account for "zero of something" (Section 7 Example H's book count including authors with zero books); reach for `INNER JOIN` when a row with no match is genuinely irrelevant to the question being asked.

## 7. Examples

All results below are real, executed output from PostgreSQL 16 in a disposable Docker container — [`practice/sql/sql-fundamentals/`](../../practice/sql/sql-fundamentals/), full transcript in `fundamentals-lab-output.txt`.

**A — `CREATE TABLE` with a primary key and a foreign key**, real `\d` output:
```
Table "public.authors"
  Column   |  Type   | Nullable |                  Default
-----------+---------+----------+--------------------------------------------
 author_id | integer | not null | nextval('authors_author_id_seq'::regclass)
 name      | text    | not null |
Indexes:
    "authors_pkey" PRIMARY KEY, btree (author_id)
Referenced by:
    TABLE "books" CONSTRAINT "books_author_id_fkey" FOREIGN KEY (author_id) REFERENCES authors(author_id)
```

**B — `INSERT` and `SELECT`**: 4 authors and 5 books inserted; `SELECT * FROM authors` and `SELECT * FROM books` return every row back, exactly as written.

**C — `SELECT ... WHERE`**: filtering `books` to `published_year < 1980` returns exactly 3 of the 5 rows, correctly excluding the two published after 1980.

**D — `UPDATE`**: `UPDATE books SET published_year = 1975 WHERE title = 'The Dispossessed'` changes exactly that one row (`UPDATE 1`); re-querying it back confirms `1975`, not the original `1974`.

**E — `DELETE`**: `DELETE FROM books WHERE title = 'Parable of the Sower'` removes exactly that row (`DELETE 1`); the table now has 4 rows, not 5.

**F — the foreign key constraint actually rejecting an orphan row**, real Postgres error text:
```
ERROR:  insert or update on table "books" violates foreign key constraint "books_author_id_fkey"
DETAIL:  Key (author_id)=(999) is not present in table "authors".
```

**G — `INNER JOIN` vs. `LEFT JOIN`, same condition, run both ways**: `INNER JOIN` returns 4 rows (one per book); `LEFT JOIN` returns 5 — the extra row is `N. K. Jemisin` (seeded with zero books on purpose), with `title` coming back `NULL` rather than the row being dropped.

**H — `GROUP BY` + `COUNT` with a `LEFT JOIN`**: the same zero-books author correctly shows `book_count = 0` in the result, rather than being absent from a report that should account for every author.

## 8. Common Mistakes

- **Skipping a foreign key and relying on application code to keep two tables consistent** — the exact gap Section 7 Example F closes; without the constraint, a bug in application code can silently insert an orphaned row with no error at all.
- **Using `INNER JOIN` when the question needs to account for "zero of something"** — silently drops the exact rows (Section 7's zero-books author) the report was supposed to include, producing a report that looks complete but isn't.
- **Assuming `DELETE` on a referenced row succeeds automatically** — attempting to delete an author who still has books referencing them fails with a foreign key violation by default (the reverse direction of Section 7 Example F), unless the schema explicitly opts into a cascade or set-null behavior, which changes the failure into a real, possibly-unwanted side effect instead.
- **Comparing `NULL` with `=` and expecting it to match** — `NULL = NULL` evaluates to `NULL` (neither true nor false) in SQL, not `true`; testing for a `NULL` result (like Example G's `LEFT JOIN` unmatched rows) requires `IS NULL`, never `= NULL`.

## 9. Edge Cases

- A `LEFT JOIN`'s unmatched columns are exactly and only the columns from the *right-hand* table — the left-hand table's own columns are never `NULL` in the result unless the underlying data genuinely contains a `NULL` there. Getting the join direction backwards silently changes which side's missing-match rows get preserved.
- `GROUP BY` with a `LEFT JOIN` (Section 7 Example H) is the specific, easy-to-miss combination that correctly reports a *zero* count for something with no matches; the same query written with `INNER JOIN` instead would drop that row from the report entirely rather than reporting it as zero — two different, both-plausible-looking results from one word changed.
- A primary key does not have to be a single column — a **composite primary key** (two or more columns together forming the uniqueness guarantee) is common on join tables, covered in [Data Modelling and Explicit Join Tables](data-modelling-and-explicit-join-tables.md), not this chapter.

## 10. Performance Implications

A primary key's automatic index (Section 5) is why looking up one row by its primary key is fast even in a table with millions of rows — without it, finding one row would mean scanning every row in the table. A foreign key's own enforcement check similarly benefits from an index on the *referenced* column (which the primary key already provides) — the cost this chapter's fundamentals don't yet cover is indexing the *referencing* column itself (e.g., `books.author_id`) for fast lookups in the other direction, which [Database Index Structures](index-structures-btree-composite-covering.md) covers in full.

## 11. Trade-offs

| Concern | `INNER JOIN` | `LEFT JOIN` |
|---|---|---|
| Rows with no match on the right | Dropped entirely | Kept, with `NULL` in the unmatched columns |
| Correct when | The question only makes sense for rows that have a match | The question needs to account for "zero of something," or a complete list regardless of matches |
| Common failure if used wrongly | Silently under-reports (Section 8) | Requires explicit `IS NULL` handling downstream to distinguish "no match" from real data |

## 12. Senior-Level Considerations (L3)

A Senior engineer reviewing a schema checks not just "does this query return the right answer for today's data" but "does this constraint (or its absence) make an entire bug class impossible, or merely unlikely." A foreign key isn't a formality — it's the difference between "this orphaned-row bug can't happen" and "this orphaned-row bug hasn't happened yet." The same discipline applies to choosing `INNER` vs. `LEFT JOIN` deliberately rather than defaulting to whichever one a query happens to return correct-looking results with on today's data — Section 8's under-reporting mistake is specifically dangerous because it produces a plausible, wrong-looking-right report rather than an obvious error.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, the question shifts from one schema to a whole system's data-integrity posture: is enforcement happening at the database layer (foreign keys, `NOT NULL`, `UNIQUE`), where it's guaranteed regardless of which of a dozen services writes to the table, or only in application code, where every new service that touches the table has to independently re-implement the same rule correctly? A Staff engineer pushes integrity constraints down into the schema specifically because it is the one enforcement point every writer — present and future, known and not-yet-written — is forced through, the same design principle [Clean and Hexagonal Architecture](../17-architecture/clean-hexagonal-architecture.md) applies to keeping business rules in one place rather than re-implemented at every caller.

## 14. Production Scenarios

No existing `production-cookbook/` entry has an SQL-fundamentals-specific root cause — the closest adjacent entries are index- and lock-scale, not basic-constraint-scale.

> Planned reference: a future `production-cookbook/` entry covering a real incident caused by a missing foreign key constraint that let an application bug silently insert orphaned rows for months before a report surfaced the inconsistency would be a natural, non-duplicative addition connecting this chapter's Section 8 warning to a genuine production incident.

## 15. Interview Questions

**Q1 (Junior): "What's the difference between a primary key and a foreign key?"**
Expected answer: a primary key uniquely identifies each row in its own table; a foreign key references another table's primary key, expressing a relationship and letting the database enforce that the referenced row actually exists.

**Q2 (Junior/Mid): "What's the difference between `INNER JOIN` and `LEFT JOIN`?"**
Expected answer: Section 4's distinction, ideally illustrated with a concrete example like Section 7 Example G — an unmatched row disappears under `INNER JOIN`, survives with `NULL`s under `LEFT JOIN`. A weak answer describes syntax without describing which rows end up missing.

**Q3 (Mid): "You need a report showing every customer's order count, including customers with zero orders. What goes wrong if you use `INNER JOIN`?"**
Expected answer: customers with zero orders are silently dropped from the report entirely, rather than showing `0` — Section 9's `GROUP BY`-plus-join edge case exactly.

**Q4 (Mid/Senior): "Why enforce a foreign key at the database level instead of checking it in application code?"**
Expected answer: Section 13's framing — a database constraint is the one enforcement point every writer is forced through, regardless of how many services or code paths write to the table; application-level checks must be independently re-implemented correctly everywhere data enters the table.

**Q5 (Senior/Staff): "A table has grown a dozen writers over several years and just failed a data-integrity audit with thousands of orphaned rows. What's your first question?"**
Expected answer: whether the referencing column ever had an actual foreign key constraint — the Staff-level diagnosis is almost always "the relationship was only ever enforced by convention, not by the schema," which Section 13 frames as the real, structural failure, not a one-off bug in any single writer.

## 16. Coding/Practice Exercises

1. Add a `publishers` table (`publisher_id` primary key, `name`) and a `publisher_id` foreign key column on `books`. Insert at least one publisher with zero books and one book with a publisher, then write both an `INNER JOIN` and a `LEFT JOIN` between `books` and `publishers`, confirming the same disappearing-row behavior Example G demonstrates for `authors`.
2. Attempt to `DELETE` an author who still has books referencing them (e.g., `Ursula K. Le Guin`, who has 2). Capture the real error message and explain, in your own words, why Postgres refuses by default.
3. Rewrite Example H's query to also include a `HAVING book_count = 0` clause, isolating only the authors with zero books, and confirm it returns exactly the one seeded author.

## 17. Debugging Exercises

Given this query, run against the lab's real schema, predict the output before running it:

```sql
SELECT a.name, COUNT(b.book_id) AS book_count
FROM authors a
INNER JOIN books b ON a.author_id = b.author_id
GROUP BY a.name;
```

The zero-books author (`N. K. Jemisin`) does **not** appear in this result at all — not with a `0`, simply absent — because `INNER JOIN` drops the row before `GROUP BY`/`COUNT` ever see it. A candidate expecting to see `N. K. Jemisin | 0` is making exactly the Section 8 mistake: assuming `INNER JOIN` behaves like `LEFT JOIN` when the count would be zero.

## 18. Design Exercises

Design the tables (with explicit primary and foreign keys) for a simple blog: `users`, `posts` (each written by one user), and `comments` (each on one post, written by one user). State, for each foreign key, what real-world mistake it makes structurally impossible — e.g., what does `posts.author_id REFERENCES users(user_id)` prevent that a plain, unconstrained integer column would not?

## 19. Further Reading

- [Data Modelling and Explicit Join Tables](data-modelling-and-explicit-join-tables.md) — the next step once single-foreign-key relationships are solid: many-to-many relationships and composite keys.
- [Database Index Structures](index-structures-btree-composite-covering.md) — what actually makes a primary-key lookup, or a foreign-key join, fast at scale.
- [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md) — how the database actually decides to execute a `JOIN`, referenced in Section 5.

## 20. Mastery Checklist

- [ ] Can state what a primary key guarantees and what a foreign key enforces, in plain language.
- [ ] Can predict, for a given `INNER JOIN` vs. `LEFT JOIN`, which rows survive and which are dropped.
- [ ] Can explain why `NULL = NULL` is not `true` in SQL, and why `IS NULL` exists because of it.
- [ ] Can correctly answer the Section 17 debugging exercise before running it.
- [ ] Can explain, in Staff-level terms, why a foreign key enforced at the database is stronger than the same rule enforced only in application code.
