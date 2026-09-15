-- Database Normalization -- real lab
-- Backs syllabus/06-databases/database-normalization-1nf-through-bcnf.md (T-2411)
-- Run: see README.md in this directory.

\timing on

-- ===========================================================
-- Part 1: 1NF -- a repeating group (comma-separated phone
-- numbers) makes "find every customer with this phone number"
-- genuinely hard, not just ugly.
-- ===========================================================

CREATE TABLE customers_unnormalized (
    customer_id  SERIAL PRIMARY KEY,
    name         TEXT NOT NULL,
    phones       TEXT NOT NULL  -- "555-0100, 555-0199" -- violates 1NF
);

INSERT INTO customers_unnormalized (name, phones) VALUES
    ('Alice',   '555-0100, 555-0199'),
    ('Bob',     '555-0200'),
    ('Charlie', '555-0100, 555-0300');  -- shares a phone with Alice -- a real fraud-detection signal, currently unqueryable directly

-- Genuinely hard/fragile: find every customer sharing a phone number
-- with Alice, using only the repeating-group column.
SELECT name, phones FROM customers_unnormalized
WHERE phones LIKE '%555-0100%';

-- The 1NF fix: one phone number per row.
CREATE TABLE customers_1nf (
    customer_id  SERIAL PRIMARY KEY,
    name         TEXT NOT NULL
);

CREATE TABLE customer_phones_1nf (
    customer_id  INT NOT NULL REFERENCES customers_1nf(customer_id),
    phone        TEXT NOT NULL,
    PRIMARY KEY (customer_id, phone)
);

INSERT INTO customers_1nf (customer_id, name) VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie');
INSERT INTO customer_phones_1nf (customer_id, phone) VALUES
    (1, '555-0100'), (1, '555-0199'), (2, '555-0200'), (3, '555-0100'), (3, '555-0300');

-- Now a real, simple, indexable equality query finds every customer
-- sharing a phone number with Alice -- including Alice herself, filtered out.
SELECT c.name
FROM customer_phones_1nf p
JOIN customers_1nf c ON c.customer_id = p.customer_id
WHERE p.phone IN (SELECT phone FROM customer_phones_1nf WHERE customer_id = 1)
  AND c.customer_id <> 1;

-- ===========================================================
-- Part 2: 2NF -- a partial dependency on a composite key. In
-- order_items(order_id, product_id), product_name depends only
-- on product_id, not on the whole (order_id, product_id) key.
-- ===========================================================

CREATE TABLE order_items_unnormalized (
    order_id      INT NOT NULL,
    product_id    INT NOT NULL,
    product_name  TEXT NOT NULL,   -- depends only on product_id -- partial dependency
    quantity      INT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

INSERT INTO order_items_unnormalized (order_id, product_id, product_name, quantity) VALUES
    (1001, 55, 'Widget', 3),
    (1002, 55, 'Widget', 1),
    (1003, 55, 'Widget', 7);

-- A real update anomaly: renaming product 55 requires updating every
-- order row that ever referenced it, or the data disagrees with itself.
UPDATE order_items_unnormalized SET product_name = 'Widget Pro' WHERE order_id = 1001 AND product_id = 55;

-- Proof of the anomaly: the identical product now has two different
-- names depending on which order row you look at.
SELECT DISTINCT product_id, product_name FROM order_items_unnormalized WHERE product_id = 55;

-- The 2NF fix: extract product_name into its own table, keyed only
-- on product_id (the part of the composite key it actually depends on).
CREATE TABLE products_2nf (
    product_id    INT PRIMARY KEY,
    product_name  TEXT NOT NULL
);

CREATE TABLE order_items_2nf (
    order_id    INT NOT NULL,
    product_id  INT NOT NULL REFERENCES products_2nf(product_id),
    quantity    INT NOT NULL,
    PRIMARY KEY (order_id, product_id)
);

INSERT INTO products_2nf (product_id, product_name) VALUES (55, 'Widget');
INSERT INTO order_items_2nf (order_id, product_id, quantity) VALUES (1001, 55, 3), (1002, 55, 1), (1003, 55, 7);

-- Renaming the product is now exactly one row, and every order
-- referencing it agrees automatically -- the anomaly is now structurally
-- impossible, not just avoided by discipline.
UPDATE products_2nf SET product_name = 'Widget Pro' WHERE product_id = 55;
SELECT oi.order_id, p.product_name, oi.quantity FROM order_items_2nf oi JOIN products_2nf p ON p.product_id = oi.product_id;

-- ===========================================================
-- Part 3: 3NF -- a transitive dependency. In
-- employees(employee_id, department_id, department_name),
-- department_name depends on department_id, which depends on
-- employee_id -- a dependency THROUGH a non-key column.
-- ===========================================================

CREATE TABLE employees_unnormalized (
    employee_id      SERIAL PRIMARY KEY,
    name             TEXT NOT NULL,
    department_id    INT NOT NULL,
    department_name  TEXT NOT NULL  -- transitively dependent via department_id
);

INSERT INTO employees_unnormalized (name, department_id, department_name) VALUES
    ('Dana',  10, 'Engineering'),
    ('Eve',   10, 'Engineering'),
    ('Frank', 20, 'Sales');

-- A real update anomaly: renaming a department requires updating
-- every employee row in it, or the data disagrees with itself.
UPDATE employees_unnormalized SET department_name = 'Platform Engineering' WHERE employee_id = 1;
SELECT employee_id, name, department_id, department_name FROM employees_unnormalized WHERE department_id = 10;

-- The 3NF fix: extract department_name into its own table, removing
-- the transitive dependency entirely.
CREATE TABLE departments_3nf (
    department_id    INT PRIMARY KEY,
    department_name  TEXT NOT NULL
);

CREATE TABLE employees_3nf (
    employee_id     SERIAL PRIMARY KEY,
    name            TEXT NOT NULL,
    department_id   INT NOT NULL REFERENCES departments_3nf(department_id)
);

INSERT INTO departments_3nf (department_id, department_name) VALUES (10, 'Engineering'), (20, 'Sales');
INSERT INTO employees_3nf (name, department_id) VALUES ('Dana', 10), ('Eve', 10), ('Frank', 20);

UPDATE departments_3nf SET department_name = 'Platform Engineering' WHERE department_id = 10;
SELECT e.name, d.department_name FROM employees_3nf e JOIN departments_3nf d ON d.department_id = e.department_id;

-- ===========================================================
-- Part 4: BCNF -- a table already in 3NF can still have an
-- anomaly when a non-candidate-key column determines part of a
-- candidate key. Classic case: (student_id, course_id) -> pk,
-- but instructor determines course (each instructor teaches
-- exactly one course), so instructor -> course_id is a real
-- functional dependency from a NON-key column.
-- ===========================================================

CREATE TABLE enrollments_3nf_not_bcnf (
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    instructor    TEXT NOT NULL,  -- each instructor teaches exactly one course
    PRIMARY KEY (student_id, course_id)
);

INSERT INTO enrollments_3nf_not_bcnf (student_id, course_id, instructor) VALUES
    (1, 100, 'Prof. Kim'),
    (2, 100, 'Prof. Kim'),
    (3, 200, 'Prof. Lee');

-- A real anomaly: nothing stops the SAME instructor from being
-- recorded against two DIFFERENT courses -- inconsistent with the
-- real-world rule (each instructor teaches exactly one course) that
-- this schema was supposed to encode, because that rule lives in a
-- non-candidate-key column's functional dependency, which a
-- (student_id, course_id) primary key cannot enforce.
INSERT INTO enrollments_3nf_not_bcnf (student_id, course_id, instructor) VALUES (4, 300, 'Prof. Kim');
SELECT DISTINCT instructor, course_id FROM enrollments_3nf_not_bcnf WHERE instructor = 'Prof. Kim';

-- The BCNF fix: split out instructor -> course_id as its own table,
-- so that real-world rule becomes a real, enforced constraint.
CREATE TABLE course_instructors_bcnf (
    instructor  TEXT PRIMARY KEY,
    course_id   INT NOT NULL
);

CREATE TABLE enrollments_bcnf (
    student_id  INT NOT NULL,
    instructor  TEXT NOT NULL REFERENCES course_instructors_bcnf(instructor),
    PRIMARY KEY (student_id, instructor)
);

INSERT INTO course_instructors_bcnf (instructor, course_id) VALUES ('Prof. Kim', 100), ('Prof. Lee', 200);
INSERT INTO enrollments_bcnf (student_id, instructor) VALUES (1, 'Prof. Kim'), (2, 'Prof. Kim'), (3, 'Prof. Lee');

-- Now the real-world rule IS enforced: Prof. Kim cannot be attached
-- to a second course_id, because course_instructors_bcnf.instructor
-- is a primary key -- a real, captured constraint violation.
INSERT INTO course_instructors_bcnf (instructor, course_id) VALUES ('Prof. Kim', 300);

-- ===========================================================
-- Part 5: the real, measured denormalization trade-off -- a
-- fully normalized 3-table join vs. a deliberately denormalized
-- flat table, same logical data, same machine.
-- ===========================================================

CREATE TABLE orders_norm (
    order_id     SERIAL PRIMARY KEY,
    customer_id  INT NOT NULL
);

CREATE TABLE customers_norm (
    customer_id  SERIAL PRIMARY KEY,
    name         TEXT NOT NULL,
    country      TEXT NOT NULL
);

CREATE TABLE order_lines_norm (
    order_line_id SERIAL PRIMARY KEY,
    order_id      INT NOT NULL REFERENCES orders_norm(order_id),
    product_id    INT NOT NULL,
    quantity      INT NOT NULL,
    unit_price    NUMERIC(10,2) NOT NULL
);

INSERT INTO customers_norm (name, country)
SELECT 'customer_' || i, (ARRAY['US','MX','CA'])[1 + (i % 3)]
FROM generate_series(1, 5000) AS i;

INSERT INTO orders_norm (customer_id)
SELECT 1 + (i % 5000) FROM generate_series(1, 100000) AS i;

INSERT INTO order_lines_norm (order_id, product_id, quantity, unit_price)
SELECT 1 + (i % 100000), 1 + (i % 200), 1 + (i % 5), (5 + (i % 100))::numeric
FROM generate_series(1, 400000) AS i;

ANALYZE customers_norm; ANALYZE orders_norm; ANALYZE order_lines_norm;

CREATE TABLE order_lines_denorm (
    order_line_id   INT PRIMARY KEY,
    order_id        INT NOT NULL,
    customer_name   TEXT NOT NULL,
    customer_country TEXT NOT NULL,
    product_id      INT NOT NULL,
    quantity        INT NOT NULL,
    unit_price      NUMERIC(10,2) NOT NULL
);

INSERT INTO order_lines_denorm
SELECT ol.order_line_id, ol.order_id, c.name, c.country, ol.product_id, ol.quantity, ol.unit_price
FROM order_lines_norm ol
JOIN orders_norm o ON o.order_id = ol.order_id
JOIN customers_norm c ON c.customer_id = o.customer_id;

ANALYZE order_lines_denorm;

-- Real cost: the normalized 3-table join, computing revenue by country.
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.country, SUM(ol.quantity * ol.unit_price) AS revenue
FROM order_lines_norm ol
JOIN orders_norm o ON o.order_id = ol.order_id
JOIN customers_norm c ON c.customer_id = o.customer_id
GROUP BY c.country;

-- Real cost: the identical logical query against the denormalized flat table.
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_country AS country, SUM(quantity * unit_price) AS revenue
FROM order_lines_denorm
GROUP BY customer_country;

-- The real, honest cost of denormalization: updating one customer's
-- name now means updating every one of their order lines, not one row.
UPDATE customers_norm SET name = 'customer_1_renamed' WHERE customer_id = 1;  -- 1 row, normalized
UPDATE order_lines_denorm SET customer_name = 'customer_1_renamed'
WHERE order_id IN (SELECT order_id FROM orders_norm WHERE customer_id = 1);   -- N rows, denormalized

SELECT count(*) AS denormalized_rows_touched FROM order_lines_denorm WHERE customer_name = 'customer_1_renamed';
