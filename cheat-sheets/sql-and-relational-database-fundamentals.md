---
title: "Cheat Sheet: SQL and Relational Database Fundamentals"
slug: sql-and-relational-database-fundamentals
document_type: cheat-sheet
domain: 06-databases
topic_id: T-2202
canonical: ../syllabus/06-databases/sql-and-relational-database-fundamentals.md
last_updated: 2026-09-18
---

# SQL and Relational Database Fundamentals

**Canonical chapter:** [`syllabus/06-databases/sql-and-relational-database-fundamentals.md`](../syllabus/06-databases/sql-and-relational-database-fundamentals.md)

## Core Mental Model

Data lives in tables; relationships between tables are expressed by one table referencing another's primary key via a foreign key — the database enforces that reference exists, not application code.

## Essential Definitions

- **Primary key** — uniquely identifies each row; the database rejects a duplicate or empty one, and automatically builds a unique index on it.
- **Foreign key** — a column referencing another table's primary key; the database rejects an insert/update pointing at a value that doesn't exist there.
- **Super key / candidate key / alternate (secondary) key** — any unique column set (super key); a *minimal* one (candidate key); a candidate key that exists but wasn't chosen as primary (alternate/secondary key) — a table can have several candidate keys but only one primary key.
- **Natural key vs. surrogate key** — a real-world meaningful unique value (email, ISBN) vs. a meaningless generated one (`SERIAL`, `UUID`); default to a surrogate key as the primary key.
- **`SELECT`/`FROM`/`WHERE`** — which columns come back / where the data comes from / which rows qualify. Logical order: `FROM` → `WHERE` → `GROUP BY` → `HAVING` → `SELECT` → `ORDER BY`.
- **`GROUP BY` + aggregate** (`COUNT`/`SUM`/`AVG`/`MIN`/`MAX`) — collapses many rows into one value per group.
- **`WHERE` vs. `HAVING`** — `WHERE` filters rows before grouping, can't reference an aggregate; `HAVING` filters groups after aggregation, can.
- **`UNIQUE`/`CHECK`/`DEFAULT`** — no-duplicates (permits one `NULL` per row, unlike PK); arbitrary per-row boolean rule; automatic fallback value when a column is omitted.
- **ACID** — Atomicity (all-or-nothing per transaction), Consistency (constraints always hold), Isolation (concurrent-transaction visibility, level-dependent), Durability (a commit survives a crash).
- **Trigger / stored function** — a function stored inside the database, wired to fire automatically on `INSERT`/`UPDATE`/`DELETE`; `RAISE EXCEPTION` inside one aborts the whole statement that fired it.

## JOIN Types

| JOIN | Returns | Unmatched left | Unmatched right |
|---|---|---|---|
| `INNER JOIN` | Only matched rows | Dropped | Dropped |
| `LEFT JOIN` | Every left row | Kept, `NULL` | Dropped |
| `RIGHT JOIN` | Every right row | Dropped | Kept, `NULL` |
| `FULL OUTER JOIN` | Every row, both sides | Kept, `NULL` | Kept, `NULL` |
| `CROSS JOIN` | Cartesian product, no condition | N/A | N/A |
| `SELF JOIN` | A table joined to itself (two aliases) | Depends on underlying join type | Depends |

## SQL Data Types (PostgreSQL)

| Category | Types | Example |
|---|---|---|
| Integer | `SMALLINT`/`INTEGER`/`BIGINT`/`SERIAL` | `emp_id SERIAL` |
| Exact decimal | `NUMERIC(p,s)`/`DECIMAL(p,s)` — use for money | `salary NUMERIC(10,2)` |
| Approx. decimal | `REAL`/`DOUBLE PRECISION` — never for money | — |
| Text | `TEXT` (default), `VARCHAR(n)`, `CHAR(n)` | `emp_name TEXT` |
| Boolean | `BOOLEAN` | `is_active BOOLEAN` |
| Date/time | `DATE`/`TIME`/`TIMESTAMPTZ` (default over `TIMESTAMP`)/`INTERVAL` | `hire_date DATE` |
| Identifier | `UUID` | `emp_uuid UUID DEFAULT gen_random_uuid()` |
| Semi-structured | `JSONB` (default over `JSON`) | `metadata JSONB`, queried with `->>` |
| Array | `<type>[]` | `tags TEXT[]`, queried with `= ANY(tags)` |

## Common Pitfalls

- Skipping a foreign key, relying on application code to keep two tables consistent — a bug can silently insert an orphaned row with no error.
- Using `INNER JOIN` when the report needs to include "zero of something" — silently drops exactly the rows that should show a zero count.
- Assuming `DELETE` on a referenced row succeeds automatically — fails with a foreign key violation by default unless the schema opts into cascade/set-null.
- Comparing `NULL` with `=` and expecting a match.
- Writing `WHERE COUNT(*) > 5` — a real syntax error, not a wrong result; aggregates aren't computed yet when `WHERE` runs. Use `HAVING`.
- Forgetting a `JOIN`'s `ON` condition — Postgres silently executes it as a `CROSS JOIN` instead of rejecting it.
- `AVG()` on a `NUMERIC(10,2)` column returns far more decimal places than the source column's scale — round explicitly for display.
- Running related statements outside an explicit transaction and assuming they're still atomic together — Atomicity only applies inside `BEGIN`/`COMMIT`.
- Forgetting a trigger exists when debugging an unexplained data change — a row changing with no obvious application-code cause is a real, common trigger symptom.

## Interview Answer Skeleton

**30-sec:** A primary key uniquely identifies a row and gets an automatic index; a foreign key references another table's primary key and is enforced by the database, not application code. `INNER JOIN` drops unmatched rows; `LEFT JOIN` keeps them with `NULL`s.

**2-min:** Add a concrete example — a `GROUP BY` + `COUNT` report using `LEFT JOIN` correctly shows `0` for an entity with no matches, while the same query with `INNER JOIN` would silently omit that entity from the report entirely (not show it as zero — omit it). Also add: `WHERE` filters rows before grouping, `HAVING` filters groups after aggregation and is the only one that can reference `COUNT`/`AVG`/etc.; and that a table can have multiple candidate keys, but only one becomes the primary key — the rest become `UNIQUE`-enforced alternate keys.

**Whiteboard:** Draw two tables with an arrow from the foreign-key column to the primary-key column it references. Draw the same join condition run two ways — one diagram where an unmatched row vanishes (`INNER`), one where it survives with a dashed/`NULL` cell (`LEFT`).

**Staff-level framing:** A foreign key enforced at the database is the one enforcement point every writer — present and future, across every service — is forced through; an application-level check must be independently re-implemented correctly everywhere data enters the table, the same "one canonical enforcement point" argument [Clean and Hexagonal Architecture](../syllabus/17-architecture/clean-hexagonal-architecture.md) makes for business rules.

## Related

- syllabus/06-databases/data-modelling-and-explicit-join-tables.md
- syllabus/06-databases/index-structures-btree-composite-covering.md
- syllabus/06-databases/query-planning-and-explain-analyze.md
