-- Real proof that a TRIGGER runs automatically, inside the SAME transaction
-- as the statement that fired it -- including a trigger that REJECTS the
-- write entirely by raising an exception, which rolls back the whole
-- statement, not just skips the trigger's own side effect.

\echo '=== SETUP: a table, a trigger function, and a trigger wired to it ==='
CREATE TABLE accounts_audit (
    id          INT PRIMARY KEY,
    name        TEXT NOT NULL,
    balance     NUMERIC(10,2) NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE balance_change_log (
    log_id      SERIAL PRIMARY KEY,
    account_id  INT NOT NULL,
    old_balance NUMERIC(10,2),
    new_balance NUMERIC(10,2),
    changed_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- A real stored function (PL/pgSQL) -- the actual logic a trigger runs.
CREATE FUNCTION log_balance_change() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.balance < 0 THEN
        RAISE EXCEPTION 'balance cannot go negative: attempted % on account %', NEW.balance, NEW.id;
    END IF;

    INSERT INTO balance_change_log (account_id, old_balance, new_balance)
    VALUES (NEW.id, OLD.balance, NEW.balance);

    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- The actual TRIGGER, wiring the function to fire before every UPDATE.
CREATE TRIGGER trg_log_balance_change
    BEFORE UPDATE ON accounts_audit
    FOR EACH ROW
    EXECUTE FUNCTION log_balance_change();

\echo ''
\echo '=== Seed one account, no trigger fires on INSERT (trigger is UPDATE-only) ==='
INSERT INTO accounts_audit (id, name, balance) VALUES (1, 'Alice', 100.00);
SELECT count(*) AS log_rows_after_insert FROM balance_change_log;

\echo ''
\echo '=== A real UPDATE: the trigger fires automatically, no application code called it ==='
UPDATE accounts_audit SET balance = 80.00 WHERE id = 1;
\echo 'The trigger inserted a real audit row, and stamped updated_at, with NO explicit INSERT statement written by the caller:'
SELECT * FROM balance_change_log;
SELECT id, name, balance, updated_at > (SELECT now() - interval '1 minute') AS updated_just_now FROM accounts_audit;

\echo ''
\echo '=== A real UPDATE the trigger REJECTS: the exception rolls back the whole statement ==='
UPDATE accounts_audit SET balance = -10.00 WHERE id = 1;
\echo 'Balance after the rejected update (expect UNCHANGED at 80.00 -- the trigger''s RAISE EXCEPTION aborted the whole UPDATE):'
SELECT id, name, balance FROM accounts_audit WHERE id = 1;
\echo 'Audit log row count (expect still 1 -- the rejected update never got far enough to log anything):'
SELECT count(*) AS log_rows_after_rejected_update FROM balance_change_log;
