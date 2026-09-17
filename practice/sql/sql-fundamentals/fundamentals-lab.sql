-- SQL & Relational Database Fundamentals lab
-- Run against PostgreSQL 16. Every result in the chapter's Section 7 is
-- copied verbatim from this script's real output — see fundamentals-lab-output.txt.

\echo '=== SECTION A: CREATE TABLE with PRIMARY KEY and FOREIGN KEY ==='

CREATE TABLE authors (
    author_id   SERIAL PRIMARY KEY,
    name        TEXT NOT NULL
);

CREATE TABLE books (
    book_id     SERIAL PRIMARY KEY,
    title       TEXT NOT NULL,
    author_id   INTEGER NOT NULL REFERENCES authors(author_id),
    published_year INTEGER
);

\d authors
\d books

\echo '=== SECTION B: INSERT rows ==='

INSERT INTO authors (name) VALUES
    ('Ursula K. Le Guin'),
    ('Ted Chiang'),
    ('Octavia E. Butler');

-- An author with zero books yet, on purpose — used in Section G below.
INSERT INTO authors (name) VALUES ('N. K. Jemisin');

INSERT INTO books (title, author_id, published_year) VALUES
    ('The Left Hand of Darkness', 1, 1969),
    ('The Dispossessed', 1, 1974),
    ('Stories of Your Life and Others', 2, 2002),
    ('Kindred', 3, 1979),
    ('Parable of the Sower', 3, 1993);

SELECT * FROM authors ORDER BY author_id;
SELECT * FROM books ORDER BY book_id;

\echo '=== SECTION C: SELECT with WHERE (filtering) ==='

SELECT title, published_year FROM books WHERE published_year < 1980 ORDER BY published_year;

\echo '=== SECTION D: UPDATE ==='

UPDATE books SET published_year = 1975 WHERE title = 'The Dispossessed';
SELECT title, published_year FROM books WHERE title = 'The Dispossessed';

\echo '=== SECTION E: DELETE ==='

DELETE FROM books WHERE title = 'Parable of the Sower';
SELECT title FROM books ORDER BY book_id;

\echo '=== SECTION F: the foreign key constraint actually rejecting an orphan row ==='

-- author_id 999 does not exist in authors — this MUST fail, and the error
-- message itself is the real teaching content of this section.
INSERT INTO books (title, author_id) VALUES ('Orphan Book', 999);

\echo '=== SECTION G: INNER JOIN vs LEFT JOIN — the actual difference, shown not asserted ==='

\echo '--- INNER JOIN: only authors that have at least one matching book row ---'
SELECT a.name, b.title
FROM authors a
INNER JOIN books b ON a.author_id = b.author_id
ORDER BY a.name;

\echo '--- LEFT JOIN: every author row is kept, unmatched columns come back NULL ---'
SELECT a.name, b.title
FROM authors a
LEFT JOIN books b ON a.author_id = b.author_id
ORDER BY a.name;

\echo '=== SECTION H: GROUP BY and COUNT — books per author, including zero ==='

SELECT a.name, COUNT(b.book_id) AS book_count
FROM authors a
LEFT JOIN books b ON a.author_id = b.author_id
GROUP BY a.name
ORDER BY book_count DESC, a.name;

\echo '=== SECTION I: a second schema for the remaining JOIN types, key types, constraints, and data types ==='

-- departments/employees, deliberately unlinked by a NOT-NULL foreign key (unlike
-- authors/books above) so a genuinely unmatched row can exist on EITHER side --
-- needed to demonstrate RIGHT JOIN and FULL JOIN honestly, not just INNER/LEFT.
CREATE TABLE departments (
    dept_id     SERIAL PRIMARY KEY,
    dept_name   TEXT NOT NULL UNIQUE
);

CREATE TABLE employees (
    emp_id       SERIAL PRIMARY KEY,
    emp_uuid     UUID NOT NULL DEFAULT gen_random_uuid(),
    emp_name     TEXT NOT NULL,
    email        TEXT NOT NULL UNIQUE,
    dept_id      INTEGER REFERENCES departments(dept_id),   -- nullable: unassigned employees allowed
    manager_id   INTEGER REFERENCES employees(emp_id),      -- nullable, self-referencing FK
    salary       NUMERIC(10,2) NOT NULL CHECK (salary > 0),
    hire_date    DATE NOT NULL DEFAULT CURRENT_DATE,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE,
    tags         TEXT[] DEFAULT '{}',
    metadata     JSONB
);

\d departments
\d employees

INSERT INTO departments (dept_name) VALUES
    ('Engineering'), ('Sales'), ('Marketing'), ('Empty Dept');

INSERT INTO employees (emp_name, email, dept_id, manager_id, salary, tags, metadata) VALUES
    ('Alice', 'alice@example.com', 1, NULL, 150000.00, ARRAY['lead','backend'], '{"level": "senior"}'),
    ('Bob',   'bob@example.com',   1, 1,    110000.00, ARRAY['backend'],        '{"level": "mid"}'),
    ('Cara',  'cara@example.com',  2, NULL, 95000.00,  ARRAY['lead'],           '{"level": "senior"}'),
    ('Dev',   'dev@example.com',   NULL, NULL, 60000.00, '{}',                  NULL);

SELECT emp_name, dept_id, manager_id, salary, hire_date, is_active, tags, metadata FROM employees ORDER BY emp_id;

\echo '=== SECTION J: RIGHT JOIN -- every department kept, unmatched employee columns NULL ==='

SELECT e.emp_name, d.dept_name
FROM employees e
RIGHT JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name;

\echo '=== SECTION K: FULL OUTER JOIN -- the union of LEFT and RIGHT, both unmatched sides kept ==='

SELECT e.emp_name, d.dept_name
FROM employees e
FULL OUTER JOIN departments d ON e.dept_id = d.dept_id
ORDER BY d.dept_name NULLS LAST, e.emp_name;

\echo '=== SECTION L: CROSS JOIN -- Cartesian product, every department x every quarter ==='

SELECT d.dept_name, q.quarter
FROM departments d
CROSS JOIN (SELECT unnest(ARRAY['Q1','Q2','Q3','Q4']) AS quarter) q
ORDER BY d.dept_name, q.quarter;

\echo '=== SECTION M: SELF JOIN -- each employee alongside their own manager ==='

SELECT e.emp_name AS employee, m.emp_name AS manager
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.emp_id
ORDER BY e.emp_name;

\echo '=== SECTION N: WHERE vs HAVING -- filtering rows before grouping vs filtering groups after ==='

\echo '--- WHERE: filters individual employee rows BEFORE grouping ---'
SELECT dept_id, AVG(salary) AS avg_salary
FROM employees
WHERE is_active = TRUE
GROUP BY dept_id
ORDER BY dept_id NULLS LAST;

\echo '--- HAVING: filters the GROUPED results AFTER aggregation -- WHERE cannot do this ---'
SELECT dept_id, AVG(salary) AS avg_salary
FROM employees
GROUP BY dept_id
HAVING AVG(salary) > 100000
ORDER BY dept_id NULLS LAST;

\echo '=== SECTION O: constraints actually being enforced -- real error text, not description ==='

\echo '--- UNIQUE: a duplicate department name is rejected ---'
INSERT INTO departments (dept_name) VALUES ('Engineering');

\echo '--- CHECK: a non-positive salary is rejected ---'
INSERT INTO employees (emp_name, email, salary) VALUES ('Bad Hire', 'bad@example.com', -5000);

\echo '--- NOT NULL: a missing required column is rejected ---'
INSERT INTO employees (emp_name, email, salary) VALUES (NULL, 'noname@example.com', 50000);

\echo '--- DEFAULT: omitted columns really do get their declared default, not NULL ---'
INSERT INTO employees (emp_name, email, salary) VALUES ('Erin', 'erin@example.com', 70000);
SELECT emp_name, hire_date, is_active, tags FROM employees WHERE emp_name = 'Erin';

\echo '=== SECTION P: querying beyond text/integer -- JSONB and array operators, for real ==='

SELECT emp_name, metadata ->> 'level' AS level
FROM employees
WHERE metadata IS NOT NULL
ORDER BY emp_name;

SELECT emp_name, tags
FROM employees
WHERE 'lead' = ANY(tags)
ORDER BY emp_name;

\echo '=== SECTION Q: a basic-to-advanced query, built up one clause at a time ==='

\echo '--- Level 1: plain SELECT ---'
SELECT emp_name FROM employees ORDER BY emp_name;

\echo '--- Level 2: + WHERE ---'
SELECT emp_name FROM employees WHERE is_active = TRUE ORDER BY emp_name;

\echo '--- Level 3: + JOIN ---'
SELECT e.emp_name, d.dept_name
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
WHERE e.is_active = TRUE
ORDER BY e.emp_name;

\echo '--- Level 4: + GROUP BY and an aggregate ---'
SELECT d.dept_name, COUNT(e.emp_id) AS headcount, AVG(e.salary) AS avg_salary
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
WHERE e.is_active = TRUE
GROUP BY d.dept_name
ORDER BY headcount DESC;

\echo '--- Level 5: + HAVING ---'
SELECT d.dept_name, COUNT(e.emp_id) AS headcount, AVG(e.salary) AS avg_salary
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
WHERE e.is_active = TRUE
GROUP BY d.dept_name
HAVING COUNT(e.emp_id) >= 1
ORDER BY headcount DESC;

\echo '--- Level 6: + ORDER BY (already present above) + LIMIT ---'
SELECT d.dept_name, COUNT(e.emp_id) AS headcount, AVG(e.salary) AS avg_salary
FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id
WHERE e.is_active = TRUE
GROUP BY d.dept_name
HAVING COUNT(e.emp_id) >= 1
ORDER BY headcount DESC
LIMIT 1;
