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
