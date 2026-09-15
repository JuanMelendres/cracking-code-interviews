# Views and Materialized Views — Real Lab

Backs [`syllabus/06-databases/views-and-materialized-views.md`](../../../syllabus/06-databases/views-and-materialized-views.md) (T-2410).

Real PostgreSQL 16, run in a disposable Docker container. No app code —
pure SQL.

## Run it

```bash
docker run -d --name views-demo -e POSTGRES_PASSWORD=demo -p 15931:5432 postgres:16
docker exec -i views-demo psql -U postgres -a < views-lab.sql > views-lab-output.txt
docker stop views-demo && docker rm views-demo
```

Full captured output in [`views-lab-output.txt`](views-lab-output.txt).
Timings vary somewhat by run and machine; the qualitative findings below
are consistent.

## What it proves

1. **A plain `VIEW` is a saved query, not a cache** — a row inserted into
   the underlying tables *after* the view already existed shows up
   immediately through the view, with no refresh step.
2. **A view is a real, database-enforced access-control mechanism** — a
   role granted `SELECT` only on a narrower view (`customer_id`, `country`)
   can query that view but gets a real, captured
   `ERROR: permission denied for table customers` against the base table,
   which still has `email` the view never exposed. This isn't an
   application-layer convention; PostgreSQL itself refuses the read.
3. **A simple single-table view is automatically updatable; an
   aggregating/joining view genuinely is not** — a real, captured error:
   `ERROR: cannot update view "completed_order_totals" / DETAIL: Views
   containing GROUP BY are not automatically updatable.` A write against
   `us_customers` (a plain `WHERE`-filtered view over one table) succeeds
   and the underlying row changes.
4. **A `MATERIALIZED VIEW` is real, separate storage that goes stale** —
   a new completed order inserted after the materialized view was
   created does **not** appear in it until an explicit `REFRESH`.
5. **The real, measured win**: the same logical aggregation (2,000
   customers, 50,000 orders, 200,000 line items) costs **55.735ms** as a
   live `LEFT JOIN` + `GROUP BY`, versus **0.122ms** reading the
   materialized view — a real, measured **~457×** speedup, at the real
   cost of the data being frozen at last-refresh time.
6. **`REFRESH MATERIALIZED VIEW CONCURRENTLY` genuinely requires a
   unique index first** — a real, captured error:
   `ERROR: cannot refresh materialized view "public.customer_order_summary"
   concurrently / HINT: Create a unique index with no WHERE clause on one
   or more columns of the materialized view.` Refreshing succeeds only
   after that index is added.
