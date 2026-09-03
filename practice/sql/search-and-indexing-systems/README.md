# Search and indexing systems: PostgreSQL full-text search (T-810) — runnable verification

Real, executed output backing
[`syllabus/11-system-design/search-and-indexing-systems.md`](../../../syllabus/11-system-design/search-and-indexing-systems.md)
(T-810). A real PostgreSQL 16 in Docker with 200,006 real rows, a real
`tsvector`/GIN index (PostgreSQL's own database-native inverted index), and
real `EXPLAIN ANALYZE` plans proving it's a genuinely different execution
strategy from a `LIKE '%...%'` scan. See also
[`practice/java/system-design/search-and-indexing-systems/`](../../java/system-design/search-and-indexing-systems/README.md)
for the from-scratch inverted-index/TF-IDF/BM25 side of this topic.

## Files

- `docker-compose.yml`, `init/01-init.sql` — a real Postgres 16 with 6 real
  on-topic articles plus 200,000 real, unrelated padding rows in the *same*
  table (so a sequential scan's real cost is actually visible), a generated
  `tsvector` column, and a real `GIN` index over it.
- `fts-vs-like-demo.sh` — the demo below.

## Run

```bash
cd practice/sql/search-and-indexing-systems
./fts-vs-like-demo.sh
```

## Real observed output (last full run)

```
=== Real EXPLAIN ANALYZE: LIKE '%java%' (no usable index for a leading/trailing wildcard) ===
   ->  Parallel Seq Scan on articles  (cost=0.00..7633.70 rows=667 width=36) (actual time=5.236..8.205 rows=1 loops=3)
         Rows Removed by Filter: 66668
 Execution Time: 9.882 ms

=== Real EXPLAIN ANALYZE: the SAME kind of query, but against the real GIN index ===
   ->  Bitmap Index Scan on articles_search_idx  (cost=0.00..22.09 rows=1000 width=0) (actual time=0.022..0.022 rows=3 loops=1)
         Index Cond: (search_vector @@ '''java'''::tsquery)
 Execution Time: 0.037 ms

=== Real relevance ranking via ts_rank -- OR of query terms, ranked by real relevance ===
1 | GC Tuning Guide | 0.0646
2 | Keyword-Stuffed Long Page | 0.0406
3 | Python GC Internals | 0.0304
5 | Java Concurrency Basics | 0.0190
4 | Database Indexing Guide | 0.0152
```

`LIKE '%java%'` forces a real sequential scan — Postgres has no way to use a
standard index for a pattern with a leading wildcard, so every one of the
200,006 real rows gets checked. The identical kind of query against the
`tsvector`/GIN index instead resolves as a real `Bitmap Index Scan`, a real
~270x measured speedup (9.882ms → 0.037ms) for the same logical question.
`ts_rank` then provides real, built-in relevance ranking — PostgreSQL's own
inverted index isn't just for exact-match lookups, it supports genuine
relevance scoring too.

## Real discoveries made while building this pack

**A real, honest gotcha: `docker exec ... psql` with no `-h` flag defaults to
the container's Unix socket — which is exactly what the official Postgres
image's short-lived, init-script-running server listens on (Unix socket
only, no TCP) before it restarts as the final server bound to
`0.0.0.0:5432`.** The first version of this script's `pg_isready`/readiness
check succeeded against that doomed, about-to-be-restarted temporary server,
and a subsequent `ANALYZE articles;` call got a real, verbatim
`FATAL: terminating connection due to administrator command` when that
temporary server shut down mid-query. The fix: force every `psql` call to
connect via `-h 127.0.0.1`, which always reaches the real, final server.

A second, corrected finding along the way: an earlier version of this demo
assumed the GIN index would only be trusted by the query planner *after*
running `ANALYZE` on the freshly bulk-loaded table (a real, common,
generally-true Postgres gotcha with statistics staleness after a bulk load).
Once the Unix-socket bug above was fixed, real `EXPLAIN ANALYZE` output
showed the planner using the `Bitmap Index Scan` correctly even *before*
`ANALYZE` was run — the earlier "before ANALYZE" seq-scan result had actually
been a symptom of the socket bug, not genuine statistics staleness. This
demo's final script only asserts the difference actually measured and
confirmed: real `LIKE` scan vs. real GIN index scan.
