-- Part 1: schema, Atomicity, Consistency, Isolation, and a committed row
-- that Part 2 (run after a real container restart) will verify survived.

\echo '=== SETUP: schema ==='
CREATE TABLE accounts (
    id      INT PRIMARY KEY,
    name    TEXT NOT NULL,
    balance NUMERIC(10,2) NOT NULL CHECK (balance >= 0)
);

\echo ''
\echo '=== SECTION A: Atomicity -- a transaction that fails partway rolls back EVERYTHING ==='
BEGIN;
INSERT INTO accounts VALUES (1, 'Alice', 100.00);
INSERT INTO accounts VALUES (2, 'Bob', 100.00);
INSERT INTO accounts VALUES (2, 'Carol', 100.00); -- duplicate id=2 -- real PK violation
\echo 'Third insert above should show a real duplicate-key error.'
ROLLBACK;
\echo 'Row count after ROLLBACK (expect 0 -- the two successful inserts were undone too):'
SELECT count(*) AS row_count FROM accounts;

\echo ''
\echo '=== SECTION A (continued): the identical transaction, no failure, COMMITs as one unit ==='
BEGIN;
INSERT INTO accounts VALUES (1, 'Alice', 100.00);
INSERT INTO accounts VALUES (2, 'Bob', 100.00);
COMMIT;
\echo 'Row count after COMMIT (expect 2):'
SELECT count(*) AS row_count FROM accounts;

\echo ''
\echo '=== SECTION B: Consistency -- a CHECK constraint refuses a transaction that would leave an invariant violated ==='
BEGIN;
UPDATE accounts SET balance = balance - 150.00 WHERE name = 'Alice'; -- would go to -50.00
\echo 'Update above should show a real check-constraint violation (balance >= 0).'
ROLLBACK;
\echo 'Alice''s real balance after ROLLBACK (expect untouched, 100.00):'
SELECT name, balance FROM accounts WHERE name = 'Alice';

\echo ''
\echo '=== SECTION C: Isolation -- the real DEFAULT isolation level, verified directly ==='
SHOW transaction_isolation;
\echo 'Deep-dive on the anomalies each isolation level does/does not prevent: see isolation-levels-and-concurrency-anomalies.md'

\echo ''
\echo '=== SECTION D: Durability -- commit a row now, verify it survives a real container restart in Part 2 ==='
BEGIN;
INSERT INTO accounts VALUES (3, 'Dave', 50.00);
COMMIT;
\echo 'Committed row, before restart:'
SELECT * FROM accounts WHERE id = 3;
