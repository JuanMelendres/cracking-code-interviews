# Week 4 PostgreSQL — Pagination Lab

Real, executed comparison of `OFFSET` vs. keyset (cursor) pagination on a 2-million-row table, PostgreSQL 16 via Docker.

## Reproduce

```bash
docker run --rm -d --name week4-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week4 postgres:16
docker cp pagination-lab.sql week4-pg:/tmp/lab.sql
docker exec -e PGPASSWORD=postgres week4-pg psql -U postgres -d week4 -f /tmp/lab.sql
docker stop week4-pg
```

## Real results

| Approach | Page depth | Execution time | Mechanism visible in `EXPLAIN` |
|---|---|---|---|
| `OFFSET` | Shallow (offset 100) | 0.028ms | `Index Scan`, `rows=120` actually produced internally before the `LIMIT` trims it |
| `OFFSET` | Deep (offset 1,000,000) | **86.006ms** | Same `Index Scan`, but `rows=1000020` — the planner walks and discards **one million rows** before returning 20 |
| Keyset (`WHERE id > 1000000`) | Equivalent depth | **0.020ms** | `Index Cond: (id > 1000000)` — jumps directly to the right position, no rows discarded |

**~3,000x difference at depth, using the exact same index, on the exact same table** — full output in `pagination-lab-output.txt`. This is the real, measured answer to "why not `OFFSET` for a 500M-row endpoint": `OFFSET` cost is linear in the offset depth because the database must count through every skipped row; keyset pagination cost is flat regardless of depth because it seeks directly via the index condition.
