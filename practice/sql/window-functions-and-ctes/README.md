# Window Functions and CTEs — Real Lab

Backs [`syllabus/06-databases/window-functions-and-ctes.md`](../../../syllabus/06-databases/window-functions-and-ctes.md) (T-2401).

Real PostgreSQL 16, run in a disposable Docker container. No app code —
pure SQL.

## Run it

```bash
docker run -d --name wf-demo -e POSTGRES_PASSWORD=demo -p 15920:5432 postgres:16
docker exec -i wf-demo psql -U postgres -a < window-functions-lab.sql > window-functions-lab-output.txt
docker stop wf-demo && docker rm wf-demo
```

Full captured output in [`window-functions-lab-output.txt`](window-functions-lab-output.txt).
The `EXPLAIN ANALYZE` timings in section 5 are real, executed against a real
200,000-row table, but are inherently timing- and plan-dependent — re-running
the lab will reproduce the qualitative result (a real gap of three to four
orders of magnitude) but not necessarily the exact millisecond figures or
exact query plan (the planner may choose a `Bitmap Heap Scan` or an
`Index Only Scan` for the correlated subquery's inner lookup depending on
table statistics from that specific run).

## What it proves

1. **`ROW_NUMBER()` vs. `RANK()` vs. `DENSE_RANK()`** — real, genuine salary
   ties produce real, different numbering: `1,2,3,4` / `1,2,2,4` / `1,2,2,3`.
2. **The tie-handling choice changes actual query results** — an identical
   "top 2 per department" cutoff returns 4 rows via `ROW_NUMBER()` and 6 rows
   via `RANK()`, from the same data.
3. **Running total and 3-day moving average** — two different frame clauses
   (`ROWS BETWEEN UNBOUNDED PRECEDING...` vs. `ROWS BETWEEN 2 PRECEDING...`)
   over the same `OVER (ORDER BY day)` base, producing two real, different
   computations.
4. **A recursive CTE correctly traverses a real 3-level org chart**, stopping
   at the correct depth and correctly excluding an unrelated department, with
   no hardcoded depth anywhere in the query.
5. **A real, measured `EXPLAIN ANALYZE` comparison** — a window-function
   "top N per group" query versus a logically equivalent correlated subquery,
   on a real 200,000-row indexed table — showing a real gap of three to four
   orders of magnitude (a captured run: 40.6ms vs. 201,295ms, ~4,957×; an
   earlier exploratory run: ~1,506×), because the correlated subquery
   re-executes its inner query once per outer row (200,000 times) while the
   window function scans the data once.
