-- Real PostgreSQL 16 lab backing syllabus/06-databases/window-functions-and-ctes.md (T-617).
-- Run against a disposable container:
--   docker run -d --name wf-demo -e POSTGRES_PASSWORD=demo -p 15920:5432 postgres:16
--   docker exec -i wf-demo psql -U postgres < window-functions-lab.sql

-- ===================== Schema and seed data =====================

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    department TEXT NOT NULL,
    salary NUMERIC NOT NULL,
    manager_id INTEGER REFERENCES employees(id)
);

INSERT INTO employees (name, department, salary, manager_id) VALUES
  ('Alice',   'Engineering', 190000, NULL),
  ('Bob',     'Engineering', 150000, 1),
  ('Carol',   'Engineering', 150000, 1),
  ('Dave',    'Engineering', 130000, 2),
  ('Erin',    'Engineering', 120000, 2),
  ('Frank',   'Sales',       160000, NULL),
  ('Grace',   'Sales',       140000, 6),
  ('Heidi',   'Sales',       140000, 6),
  ('Ivan',    'Sales',       110000, 7);

CREATE TABLE daily_sales (
    day DATE PRIMARY KEY,
    amount_usd NUMERIC NOT NULL
);
INSERT INTO daily_sales VALUES
  ('2026-01-01', 1000), ('2026-01-02', 1500), ('2026-01-03', 900),
  ('2026-01-04', 2000), ('2026-01-05', 1200), ('2026-01-06', 1800),
  ('2026-01-07', 2200);

-- ===================== 1. ROW_NUMBER vs RANK vs DENSE_RANK, with real ties =====================

SELECT name, department, salary,
       ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS row_num,
       RANK()       OVER (PARTITION BY department ORDER BY salary DESC) AS rank,
       DENSE_RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS dense_rank
FROM employees
ORDER BY department, salary DESC;

-- ===================== 2. Top-2-per-department: ROW_NUMBER vs RANK give DIFFERENT results at a tie =====================

SELECT name, department, salary FROM (
  SELECT name, department, salary,
         ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS rn
  FROM employees
) ranked
WHERE rn <= 2
ORDER BY department, salary DESC;

SELECT name, department, salary FROM (
  SELECT name, department, salary,
         RANK() OVER (PARTITION BY department ORDER BY salary DESC) AS rnk
  FROM employees
) ranked
WHERE rnk <= 2
ORDER BY department, salary DESC;

-- ===================== 3. Running total and 3-day moving average =====================

SELECT day, amount_usd,
       SUM(amount_usd) OVER (ORDER BY day ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_total,
       ROUND(AVG(amount_usd) OVER (ORDER BY day ROWS BETWEEN 2 PRECEDING AND CURRENT ROW), 2) AS moving_avg_3day
FROM daily_sales
ORDER BY day;

-- ===================== 4. Recursive CTE: org chart traversal with depth =====================

WITH RECURSIVE org_chart AS (
    SELECT id, name, manager_id, 0 AS depth, name::text AS path
    FROM employees
    WHERE name = 'Alice'
  UNION ALL
    SELECT e.id, e.name, e.manager_id, oc.depth + 1, oc.path || ' -> ' || e.name
    FROM employees e
    JOIN org_chart oc ON e.manager_id = oc.id
)
SELECT name, depth, path FROM org_chart ORDER BY depth, name;

-- ===================== 5. Real EXPLAIN ANALYZE: window function vs correlated subquery =====================
-- 200,000-row table, 20 departments, indexed on (department, salary DESC).

CREATE TABLE big_employees AS
SELECT
  gs AS id,
  'emp' || gs AS name,
  'dept' || (gs % 20) AS department,
  (random() * 100000 + 50000)::numeric(10,2) AS salary
FROM generate_series(1, 200000) gs;

CREATE INDEX idx_big_employees_dept_salary ON big_employees(department, salary DESC);
ANALYZE big_employees;

-- Window function approach:
EXPLAIN (ANALYZE, BUFFERS, TIMING)
SELECT name, department, salary FROM (
  SELECT name, department, salary,
         ROW_NUMBER() OVER (PARTITION BY department ORDER BY salary DESC) AS rn
  FROM big_employees
) t WHERE rn <= 3;

-- Correlated-subquery equivalent (same logical result, no window function):
EXPLAIN (ANALYZE, BUFFERS, TIMING)
SELECT name, department, salary FROM big_employees e1
WHERE (SELECT COUNT(*) FROM big_employees e2
       WHERE e2.department = e1.department AND e2.salary > e1.salary) < 3;
