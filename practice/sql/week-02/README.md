# Week 2 PostgreSQL Labs — runnable verification

Two labs, both run against PostgreSQL 16 in a disposable Docker container.

## Reproduce

```bash
docker run --rm -d --name week2-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week2 postgres:16
docker cp query-plan-lab.sql week2-pg:/tmp/lab1.sql
docker cp many-to-many-lab.sql week2-pg:/tmp/lab2.sql
docker exec -e PGPASSWORD=postgres week2-pg psql -U postgres -d week2 -f /tmp/lab1.sql
docker exec -e PGPASSWORD=postgres week2-pg psql -U postgres -d week2 -f /tmp/lab2.sql
docker stop week2-pg
```

## `query-plan-lab.sql` — three real before/after diagnoses

Seeds the same `customers`/`orders` shape as Week 1 (5,000 customers, 300,000 orders). Three scenarios, each with a real `EXPLAIN (ANALYZE, BUFFERS)` before, a fix, and the plan after:

1. **Join with a missing FK index** — modest, honestly-reported improvement (18.45ms → 18.07ms). The dominant cost was scanning all 300K orders regardless; the region-filter index sped up only the customer side. Included deliberately as a realistic, non-dramatic result — not every index pays off as much as the textbook case.
2. **Function-wrapped predicate defeats a plain index** (`UPPER(status) = 'REFUNDED'`) — 21.6ms → 5.8ms after an expression index.
3. **Nested loop vs. hash join** — forced nested loop (`enable_hashjoin/mergejoin = off`) at 47.8ms vs. the planner's own free choice (hash join) at 35.0ms.

## `many-to-many-lab.sql` — the explicit-join-entity demonstration

Builds a naive `@ManyToMany`-shape join table (`order_products_naive`, just two foreign keys) alongside an explicit join entity (`order_lines`, carrying `quantity` and a locked `unit_price_at_order_time`), then **demonstrates a real, reproducible data-integrity bug**: after a product's price changes, the naive join table's "historical total" (recomputed via a live join to `products`) silently changes to reflect the *new* price, while the explicit `order_lines` entity still reports the correct historical total. This is real executed SQL, not a hypothetical — see `many-to-many-lab-output.txt`.
