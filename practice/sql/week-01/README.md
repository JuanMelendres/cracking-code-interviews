# Week 1 PostgreSQL Index Lab — runnable verification

Every `EXPLAIN` block quoted in `study-packs/week-01/02-database-index-fundamentals.md` comes from this lab, run against PostgreSQL 16 in a disposable Docker container. `index-lab-output.txt` is the complete, unedited output of the last real run.

## Reproduce

```bash
docker run --rm -d --name week1-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week1 postgres:16
docker cp index-lab.sql week1-pg:/tmp/lab.sql
docker exec -e PGPASSWORD=postgres week1-pg psql -U postgres -d week1 -f /tmp/lab.sql
docker stop week1-pg
```

## What it seeds

- `customers`: 5,000 rows
- `orders`: 300,000 rows, `customer_id` uniform over the 5,000 customers, `created_at` random over a 2-year window, `status` weighted so `completed` ≈ 66% and `refunded` ≈ 16% of rows — deliberately chosen to demonstrate both the low-selectivity (index ignored) and high-selectivity (index used) cases in §6 of the chapter with real, not constructed, numbers.

## Sections, matching the chapter

| Section | Demonstrates | Chapter reference |
|---|---|---|
| A | Sequential scan before any index | §3 |
| B | Single-column index, same query, ~52x faster | §3 |
| C | Composite index, both columns filtered — leftmost prefix works | §4 |
| D | Same index, only the second column filtered — leftmost prefix violated | §4 |
| E | Covering index, forced plain scan — real `Index Only Scan`, `Heap Fetches: 0` | §5 |
| F | Low selectivity (66% of rows) — planner correctly ignores the index | §6 |
| G | High selectivity (16.6% of rows) — planner uses the same index | §6 |

Numbers will vary slightly run-to-run (`random()` seeding, actual timings) but the *shape* of every result — which scan type the planner picks, whether `Heap Fetches` is zero — is deterministic given the schema and selectivity ratios above.
