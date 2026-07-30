# Week 10 PostgreSQL — Declarative Hash Partitioning & Pruning

A real hash-partitioned table, PostgreSQL 16 via Docker — partition pruning demonstrated via real `EXPLAIN (ANALYZE)` output.

## Reproduce

```bash
docker run --rm -d --name week10-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week10 -p 5433:5432 postgres:16
docker cp setup.sql week10-pg:/tmp/setup.sql
docker exec -e PGPASSWORD=postgres week10-pg psql -U postgres -d week10 -f /tmp/setup.sql

docker exec -e PGPASSWORD=postgres week10-pg psql -U postgres -d week10 \
  -c "EXPLAIN (ANALYZE, COSTS OFF, TIMING OFF) SELECT count(*) FROM events WHERE customer_id = 42;"

docker exec -e PGPASSWORD=postgres week10-pg psql -U postgres -d week10 \
  -c "EXPLAIN (ANALYZE, COSTS OFF, TIMING OFF) SELECT count(*) FROM events WHERE event_type = 'click';"
```

## Real result — filtering by the partition key: 1 of 4 partitions scanned

```
 Aggregate (actual rows=1 loops=1)
   ->  Seq Scan on events_p2 events (actual rows=40 loops=1)
         Filter: (customer_id = 42)
         Rows Removed by Filter: 11000
 Planning Time: 0.216 ms
 Execution Time: 0.727 ms
```

## Real result — filtering by a non-partition-key column: all 4 partitions scanned

```
 Aggregate (actual rows=1 loops=1)
   ->  Append (actual rows=40000 loops=1)
         ->  Seq Scan on events_p0 events_1 (actual rows=10360 loops=1)
               Filter: (event_type = 'click'::text)
         ->  Seq Scan on events_p1 events_2 (actual rows=9360 loops=1)
               Filter: (event_type = 'click'::text)
         ->  Seq Scan on events_p2 events_3 (actual rows=11040 loops=1)
               Filter: (event_type = 'click'::text)
         ->  Seq Scan on events_p3 events_4 (actual rows=9240 loops=1)
               Filter: (event_type = 'click'::text)
 Planning Time: 0.232 ms
 Execution Time: 2.667 ms
```

## Files

| File | Purpose |
|---|---|
| `setup.sql` | Creates the 4-way hash-partitioned `events` table and seeds 40,000 rows across 1,000 customers |
