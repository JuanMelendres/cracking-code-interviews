# JSONB and Advanced PostgreSQL Index Types — Real Lab

Backs [`syllabus/06-databases/jsonb-and-advanced-index-types.md`](../../../syllabus/06-databases/jsonb-and-advanced-index-types.md) (T-2402).

Real PostgreSQL 16, run in a disposable Docker container. No app code —
pure SQL.

## Run it

```bash
docker run -d --name idx-demo -e POSTGRES_PASSWORD=demo -p 15930:5432 postgres:16
docker exec -i idx-demo psql -U postgres -a < jsonb-and-indexes-lab.sql > jsonb-and-indexes-lab-output.txt
docker stop idx-demo && docker rm idx-demo
```

Full captured output in [`jsonb-and-indexes-lab-output.txt`](jsonb-and-indexes-lab-output.txt).
Timings and exact plan choices (`EXPLAIN ANALYZE`, `pg_relation_size`) are
real but will vary somewhat by run and machine; the qualitative findings
below are consistent.

## What it proves

1. **JSONB containment queries** (`@>`, `->`, `->>`) work exactly as
   documented against a small, real table.
2. **A GIN index on JSONB is not automatically a win** — real, honest
   evidence: adding a GIN index to a 300,000-row table made an `@>`
   containment query measurably *slower* (11.988ms → 13.896ms) than a
   parallel sequential scan, because the predicate matched enough rows
   (~4%) that indexed heap fetches lost to a parallelized full scan.
3. **A GIN index on `to_tsvector(...)` for full-text search is a real,
   dramatic win** — the identical logical query against 500,000 article
   rows: 721.775ms unindexed versus 0.191ms indexed, a real ~3,780×
   speedup.
4. **A GiST `EXCLUDE` constraint really prevents overlapping bookings** —
   a real, captured PostgreSQL error rejecting a genuinely overlapping
   date range for the same room, while a non-overlapping booking for the
   same room and an overlapping booking for a *different* room both
   succeed.
5. **A BRIN index is real, dramatically smaller than an equivalent B-tree**
   (43 MB vs. 24 kB on 2,000,000 naturally time-ordered rows) — but the
   real, honest finding is that the query planner *declined* to use it by
   default even as the only available index (still chose a seq scan,
   26.571ms); only forcing it (`enable_seqscan = off`, a diagnostic
   override) revealed BRIN's real, better performance (8.637ms) — a real
   planner cost-estimation quirk, not a BRIN limitation.
