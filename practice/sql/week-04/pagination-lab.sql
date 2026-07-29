CREATE TABLE feed_items (
  id BIGINT PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  content TEXT NOT NULL
);

INSERT INTO feed_items
SELECT g, TIMESTAMP '2020-01-01' + g * INTERVAL '1 second', 'post number ' || g
FROM generate_series(1, 2000000) g;

ANALYZE feed_items;

\echo '=== [A] OFFSET pagination -- shallow page (offset 100) ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM feed_items ORDER BY id LIMIT 20 OFFSET 100;

\echo '=== [B] OFFSET pagination -- deep page (offset 1,000,000) ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM feed_items ORDER BY id LIMIT 20 OFFSET 1000000;

\echo '=== [C] Keyset (cursor) pagination -- equivalent "deep" page, via WHERE id > last_seen ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM feed_items WHERE id > 1000000 ORDER BY id LIMIT 20;
