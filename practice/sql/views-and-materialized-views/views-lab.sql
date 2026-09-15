-- Views and Materialized Views -- real lab
-- Backs syllabus/06-databases/views-and-materialized-views.md (T-2410)
-- Run: see README.md in this directory.

\timing on

-- ===========================================================
-- Part 0: schema -- an orders/line-items setup, small enough
-- to read directly, big enough to make a materialized view's
-- refresh cost and query-time win real and measurable.
-- ===========================================================

CREATE TABLE customers (
    customer_id  SERIAL PRIMARY KEY,
    name         TEXT NOT NULL,
    email        TEXT NOT NULL,
    country      TEXT NOT NULL
);

CREATE TABLE orders (
    order_id     SERIAL PRIMARY KEY,
    customer_id  INT NOT NULL REFERENCES customers(customer_id),
    order_date   DATE NOT NULL,
    status       TEXT NOT NULL
);

CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id      INT NOT NULL REFERENCES orders(order_id),
    product_name  TEXT NOT NULL,
    quantity      INT NOT NULL,
    unit_price    NUMERIC(10,2) NOT NULL
);

INSERT INTO customers (name, email, country)
SELECT 'customer_' || i, 'customer_' || i || '@example.com',
       (ARRAY['US','MX','CA','BR','AR'])[1 + (i % 5)]
FROM generate_series(1, 2000) AS i;

INSERT INTO orders (customer_id, order_date, status)
SELECT 1 + (i % 2000),
       DATE '2026-01-01' + (i % 300),
       (ARRAY['completed','completed','completed','cancelled','pending'])[1 + (i % 5)]
FROM generate_series(1, 50000) AS i;

INSERT INTO order_items (order_id, product_name, quantity, unit_price)
SELECT 1 + (i % 50000),
       (ARRAY['widget','gadget','gizmo','doohickey'])[1 + (i % 4)],
       1 + (i % 5),
       (5 + (i % 200))::numeric
FROM generate_series(1, 200000) AS i;

ANALYZE customers;
ANALYZE orders;
ANALYZE order_items;

-- ===========================================================
-- Part 1: a plain VIEW -- a saved query, no storage of its own
-- ===========================================================

CREATE VIEW completed_order_totals AS
SELECT
    o.order_id,
    o.customer_id,
    o.order_date,
    SUM(oi.quantity * oi.unit_price) AS order_total
FROM orders o
JOIN order_items oi ON oi.order_id = o.order_id
WHERE o.status = 'completed'
GROUP BY o.order_id, o.customer_id, o.order_date;

SELECT * FROM completed_order_totals ORDER BY order_id LIMIT 5;

-- Proof it's genuinely a saved query, not a cache: insert a new
-- completed order+item after the view already exists, then query
-- the view again -- the new row appears immediately.

INSERT INTO orders (order_id, customer_id, order_date, status)
VALUES (999001, 1, DATE '2026-06-01', 'completed');
INSERT INTO order_items (order_id, product_name, quantity, unit_price)
VALUES (999001, 'widget', 3, 10.00);

SELECT * FROM completed_order_totals WHERE order_id = 999001;

-- ===========================================================
-- Part 2: a view can be a genuine access-control mechanism --
-- exposing fewer columns than the base table, real and enforced
-- by a GRANT, not by application-layer discipline.
-- ===========================================================

CREATE VIEW customers_public AS
SELECT customer_id, country FROM customers;

CREATE ROLE reporting_user LOGIN PASSWORD 'demo';
REVOKE ALL ON customers FROM reporting_user;
GRANT SELECT ON customers_public TO reporting_user;

-- As reporting_user: allowed against the view...
SET ROLE reporting_user;
SELECT * FROM customers_public LIMIT 3;

-- ...but the base table (with email, a column the view never
-- exposed) is genuinely inaccessible -- real, captured error.
SELECT * FROM customers LIMIT 3;

RESET ROLE;

-- ===========================================================
-- Part 3: updatable views -- a simple single-table view accepts
-- DML; a view with a GROUP BY/JOIN genuinely does not (real
-- Postgres error, not a style rule).
-- ===========================================================

CREATE VIEW us_customers AS
SELECT customer_id, name, email, country
FROM customers
WHERE country = 'US';

UPDATE us_customers SET name = 'customer_1_renamed' WHERE customer_id = 1;
SELECT name FROM customers WHERE customer_id = 1;

-- This one is NOT automatically updatable -- it aggregates and
-- joins, so Postgres cannot unambiguously map a write back to
-- one base-table row.
UPDATE completed_order_totals SET order_total = 0 WHERE order_id = 999001;

-- ===========================================================
-- Part 4: MATERIALIZED VIEW -- real storage, stale until
-- refreshed, measured cost/benefit.
-- ===========================================================

CREATE MATERIALIZED VIEW customer_order_summary AS
SELECT
    c.customer_id,
    c.name,
    c.country,
    COUNT(o.order_id)                              AS total_orders,
    COALESCE(SUM(oi.quantity * oi.unit_price), 0)  AS lifetime_value
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.customer_id AND o.status = 'completed'
LEFT JOIN order_items oi ON oi.order_id = o.order_id
GROUP BY c.customer_id, c.name, c.country;

SELECT * FROM customer_order_summary ORDER BY customer_id LIMIT 5;

-- Proof it's genuinely stale: change underlying data, requery the
-- materialized view before refreshing -- the new order is absent.

INSERT INTO orders (order_id, customer_id, order_date, status)
VALUES (999002, 1, DATE '2026-06-02', 'completed');
INSERT INTO order_items (order_id, product_name, quantity, unit_price)
VALUES (999002, 'gadget', 1, 999.00);

SELECT total_orders, lifetime_value FROM customer_order_summary WHERE customer_id = 1;

-- Real measured cost/benefit: the live equivalent query vs. the
-- materialized view, same logical result, same machine, same data.

EXPLAIN (ANALYZE, BUFFERS)
SELECT
    c.customer_id,
    c.name,
    c.country,
    COUNT(o.order_id)                              AS total_orders,
    COALESCE(SUM(oi.quantity * oi.unit_price), 0)  AS lifetime_value
FROM customers c
LEFT JOIN orders o ON o.customer_id = c.customer_id AND o.status = 'completed'
LEFT JOIN order_items oi ON oi.order_id = o.order_id
GROUP BY c.customer_id, c.name, c.country;

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM customer_order_summary;

-- REFRESH MATERIALIZED VIEW CONCURRENTLY requires a unique index
-- on the materialized view -- real error without one, then real
-- success after adding it.

REFRESH MATERIALIZED VIEW CONCURRENTLY customer_order_summary;

CREATE UNIQUE INDEX customer_order_summary_pk ON customer_order_summary (customer_id);

REFRESH MATERIALIZED VIEW CONCURRENTLY customer_order_summary;

SELECT total_orders, lifetime_value FROM customer_order_summary WHERE customer_id = 1;

-- ===========================================================
-- cleanup
-- ===========================================================

REVOKE SELECT ON customers_public FROM reporting_user;
DROP ROLE reporting_user;
