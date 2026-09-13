-- Week 2 lab, part 2: many-to-many modelling — the @ManyToMany trap and its fix.

\echo '--- The trap: a plain many-to-many join table has nowhere to put "quantity" ---'
CREATE TABLE products (
  id INT PRIMARY KEY,
  name TEXT NOT NULL,
  unit_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE customer_orders (
  id INT PRIMARY KEY,
  customer_id INT NOT NULL
);

-- The naive @ManyToMany-style join table: only the two foreign keys, no room
-- for anything about the RELATIONSHIP itself (how many units, at what price
-- when ordered, what line status). This is exactly what Hibernate generates
-- for an unannotated @ManyToMany and it is almost never what production
-- order-line data actually needs.
CREATE TABLE order_products_naive (
  order_id INT NOT NULL REFERENCES customer_orders(id),
  product_id INT NOT NULL REFERENCES products(id),
  PRIMARY KEY (order_id, product_id)
);

\echo 'Attempting to record "3 units of product 1 on order 1" — there is no column for quantity:'
\d order_products_naive

\echo ''
\echo '--- The fix: an explicit join ENTITY (OrderLine), not just a join TABLE ---'
CREATE TABLE order_lines (
  id INT PRIMARY KEY,
  order_id INT NOT NULL REFERENCES customer_orders(id),
  product_id INT NOT NULL REFERENCES products(id),
  quantity INT NOT NULL CHECK (quantity > 0),
  unit_price_at_order_time NUMERIC(10,2) NOT NULL,
  UNIQUE (order_id, product_id)
);

INSERT INTO products VALUES (1, 'Widget', 9.99), (2, 'Gadget', 24.50);
INSERT INTO customer_orders VALUES (1, 42);
INSERT INTO order_lines VALUES (1, 1, 1, 3, 9.99);  -- 3 widgets, price locked at order time
INSERT INTO order_products_naive VALUES (1, 1);      -- the naive table only records THAT they're linked

\echo 'Now every fact a real order line needs has somewhere to live:'
SELECT ol.id, p.name, ol.quantity, ol.unit_price_at_order_time,
       ol.quantity * ol.unit_price_at_order_time AS line_total
FROM order_lines ol JOIN products p ON p.id = ol.product_id
WHERE ol.order_id = 1;

\echo ''
\echo '--- Why the explicit entity matters even without quantity: price history ---'
\echo 'The naive join table has no unit_price_at_order_time. If the product price'
\echo 'changes later, every historical order silently "changes price" too when'
\echo 'joined against the current products table -- a real, silent data-integrity bug.'
UPDATE products SET unit_price = 12.99 WHERE id = 1;

\echo 'Naive approach (join table, no locked price) would now report the WRONG historical total:'
SELECT p.name, p.unit_price AS current_price_wrongly_used_for_history
FROM order_products_naive opn JOIN products p ON p.id = opn.product_id;

\echo 'Explicit order_lines entity still reports the CORRECT historical total:'
SELECT ol.id, p.name, ol.quantity, ol.unit_price_at_order_time,
       ol.quantity * ol.unit_price_at_order_time AS line_total
FROM order_lines ol JOIN products p ON p.id = ol.product_id
WHERE ol.order_id = 1;
