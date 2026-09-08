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
