# SQL & Relational Database Fundamentals lab — runnable verification

Every result quoted in `syllabus/06-databases/sql-and-relational-database-fundamentals.md`'s Section 7 comes from this lab, run against PostgreSQL 16 in a disposable Docker container. `fundamentals-lab-output.txt` is the complete, unedited output of the last real run.

## Reproduce

```bash
docker run --rm -d --name sqlfund-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=sqlfund postgres:16
docker cp fundamentals-lab.sql sqlfund-pg:/tmp/lab.sql
docker exec -e PGPASSWORD=postgres sqlfund-pg psql -U postgres -d sqlfund -f /tmp/lab.sql
docker stop sqlfund-pg
```

## Sections, matching the chapter

| Section | Demonstrates |
|---|---|
| A | `CREATE TABLE` with a `PRIMARY KEY` and a `FOREIGN KEY` — `\d` output shows the real constraint names Postgres generates |
| B | `INSERT` and a plain `SELECT *` |
| C | `SELECT ... WHERE` — filtering rows |
| D | `UPDATE` — changing one row's value |
| E | `DELETE` — removing one row |
| F | The foreign key constraint actually rejecting an orphan insert (`author_id = 999`, no such author) — the real Postgres error text is the point, not a description of it |
| G | `INNER JOIN` vs. `LEFT JOIN`, same query, run twice — proves the actual difference: an author with zero books (`N. K. Jemisin`, seeded on purpose) disappears from the `INNER JOIN` result and appears with a `NULL` title in the `LEFT JOIN` result |
| H | `GROUP BY` + `COUNT` with a `LEFT JOIN`, so an author with 0 books shows a real `0`, not an absence |
| I | A second schema (`departments`/`employees`) deliberately linked by a *nullable* foreign key, so a genuinely unmatched row can exist on either side — needed to honestly demonstrate `RIGHT JOIN`/`FULL JOIN`, which the FK-required `authors`/`books` schema above cannot. Also introduces `UNIQUE`, `CHECK`, `DEFAULT`, `UUID`, `NUMERIC`, `DATE`, `BOOLEAN`, `TEXT[]`, and `JSONB` — real, declared column types, shown via `\d` |
| J | `RIGHT JOIN` — every department kept (including the zero-employee "Empty Dept"), the unassigned employee ("Dev," `dept_id IS NULL`) dropped |
| K | `FULL OUTER JOIN` — the real union of J and a `LEFT JOIN`: both "Empty Dept" (no employees) and "Dev" (no department) appear in the same result |
| L | `CROSS JOIN` — a real Cartesian product, 4 departments × 4 quarters = 16 rows |
| M | `SELF JOIN` — employees joined to employees, each row paired with its own manager's name (via `manager_id` referencing `emp_id` on the same table) |
| N | `WHERE` vs. `HAVING` — the identical `GROUP BY`/`AVG` query, once filtered by `WHERE` (before grouping) and once by `HAVING` (after aggregation) — real, different result sets |
| O | `UNIQUE`, `CHECK`, and `NOT NULL` constraints actually rejecting bad inserts — real Postgres error text for each, not a description; then `DEFAULT` actually applying when a column is omitted, confirmed by re-querying the inserted row |
| P | Querying a `JSONB` column with `->>` and a `TEXT[]` column with `= ANY(...)` — real, working operators beyond plain scalar types |
| Q | One report query built up six times, one clause at a time (`SELECT` → `WHERE` → `JOIN` → `GROUP BY`/aggregate → `HAVING` → `ORDER BY`/`LIMIT`), each version's real output shown |
