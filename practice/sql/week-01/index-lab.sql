-- Week 1 index lab — schema + seed data
CREATE TABLE customers (
  id INT PRIMARY KEY,
  region TEXT NOT NULL
);

CREATE TABLE orders (
  id BIGINT PRIMARY KEY,
  customer_id INT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  amount NUMERIC(10,2) NOT NULL,
  status TEXT NOT NULL
);

INSERT INTO customers
SELECT g, (ARRAY['us-east','us-west','eu','apac'])[1 + (g % 4)]
FROM generate_series(1, 5000) g;

INSERT INTO orders
SELECT
  g,
  1 + (g % 5000),
  TIMESTAMP '2024-01-01' + (random() * 730) * INTERVAL '1 day',
  round((random() * 500 + 5)::numeric, 2),
  (ARRAY['completed','completed','completed','completed','pending','refunded'])[1 + floor(random()*6)::int]
FROM generate_series(1, 300000) g;

ANALYZE customers;
ANALYZE orders;

\echo '=== [A] Before any index: point lookup by customer_id ==='
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT) SELECT * FROM orders WHERE customer_id = 42;

\echo '=== [B] Create single-column index, re-run same query ==='
CREATE INDEX idx_orders_customer ON orders(customer_id);
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT) SELECT * FROM orders WHERE customer_id = 42;

\echo '=== [C] Composite index (customer_id, created_at) — leftmost prefix works ==='
CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at);
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM orders WHERE customer_id = 42 AND created_at > '2025-01-01';

\echo '=== [D] Same composite index, filtering only the SECOND column — leftmost prefix rule violated ==='
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM orders WHERE created_at > '2025-06-01' AND created_at < '2025-06-02';

\echo '=== [E] Covering index — index-only scan by including amount ==='
CREATE INDEX idx_orders_covering ON orders(customer_id, created_at) INCLUDE (amount);
VACUUM orders;
-- The planner's default cost model prefers a Bitmap Heap Scan over a plain
-- Index Only Scan at this table size, and a bitmap scan always rechecks the
-- heap, so it never reports as index-only even when reading a covering
-- index. Disabling bitmap scan for this one query forces the plan that
-- demonstrates the covering-index payoff (Heap Fetches: 0). This is itself
-- a teaching point: a covering index makes an index-only scan POSSIBLE,
-- not automatic — see study-packs/week-01/02-database-index-fundamentals.md §5.
SET enable_bitmapscan = off;
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT customer_id, created_at, amount FROM orders WHERE customer_id = 42 AND created_at > '2025-01-01';
SET enable_bitmapscan = on;

\echo '=== [F] Low selectivity — planner ignores the index even though one exists ==='
CREATE INDEX idx_orders_status ON orders(status);
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM orders WHERE status = 'completed';

\echo '=== [G] High selectivity on the same column — planner uses it ==='
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM orders WHERE status = 'refunded';
