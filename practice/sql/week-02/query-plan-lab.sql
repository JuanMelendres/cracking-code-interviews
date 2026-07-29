-- Week 2 lab, part 1: query-plan-analysis — three real slow queries, diagnosed and fixed.
-- Reuses the orders/customers schema from Week 1's index lab, same seed sizes.

CREATE TABLE customers (
  id INT PRIMARY KEY,
  region TEXT NOT NULL,
  signup_date DATE NOT NULL
);

CREATE TABLE orders (
  id BIGINT PRIMARY KEY,
  customer_id INT NOT NULL REFERENCES customers(id),
  created_at TIMESTAMP NOT NULL,
  amount NUMERIC(10,2) NOT NULL,
  status TEXT NOT NULL
);

INSERT INTO customers
SELECT g, (ARRAY['us-east','us-west','eu','apac'])[1 + (g % 4)],
       DATE '2022-01-01' + (g % 1000) * INTERVAL '1 day'
FROM generate_series(1, 5000) g;

INSERT INTO orders
SELECT g, 1 + (g % 5000),
       TIMESTAMP '2024-01-01' + (random() * 730) * INTERVAL '1 day',
       round((random() * 500 + 5)::numeric, 2),
       (ARRAY['completed','completed','completed','completed','pending','refunded'])[1 + floor(random()*6)::int]
FROM generate_series(1, 300000) g;

ANALYZE customers;
ANALYZE orders;

\echo '--- Scenario 1: join with no index on the foreign key ---'
\echo '[1-before] Join customers to their orders, filtered by region'
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.id, COUNT(o.id) AS order_count
FROM customers c
JOIN orders o ON o.customer_id = c.id
WHERE c.region = 'eu'
GROUP BY c.id;

\echo '[1-fix] Add the index the join and filter both need'
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_customers_region ON customers(region);

\echo '[1-after] Same query, after both indexes exist'
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.id, COUNT(o.id) AS order_count
FROM customers c
JOIN orders o ON o.customer_id = c.id
WHERE c.region = 'eu'
GROUP BY c.id;

\echo '--- Scenario 2: function wrapped around an indexed column defeats the index ---'
CREATE INDEX idx_orders_status ON orders(status);

\echo '[2-before] Filtering with UPPER(status) — the plain index on status cannot be used'
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM orders WHERE UPPER(status) = 'REFUNDED';

\echo '[2-fix] Build a matching expression index'
CREATE INDEX idx_orders_status_upper ON orders(UPPER(status));

\echo '[2-after] Same query, expression index now usable'
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM orders WHERE UPPER(status) = 'REFUNDED';

\echo '--- Scenario 3: nested loop vs hash join, driven by join-column statistics ---'
\echo '[3-before] Force a nested loop to show its cost on this data volume'
SET enable_hashjoin = off;
SET enable_mergejoin = off;
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.region, AVG(o.amount)
FROM customers c JOIN orders o ON o.customer_id = c.id
GROUP BY c.region;
RESET enable_hashjoin;
RESET enable_mergejoin;

\echo '[3-after] Let the planner choose freely — it picks a hash join instead'
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.region, AVG(o.amount)
FROM customers c JOIN orders o ON o.customer_id = c.id
GROUP BY c.region;
