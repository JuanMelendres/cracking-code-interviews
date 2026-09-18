-- Part 2: run AFTER a real `docker restart` of the Postgres container.
-- If Dave's row (committed in Part 1) is still here, durability is real,
-- not just "the same session never disconnected."

\echo '=== SECTION D (continued): after a REAL container restart, is the committed row still here? ==='
SELECT * FROM accounts WHERE id = 3;
SELECT count(*) AS total_rows_surviving_restart FROM accounts;
