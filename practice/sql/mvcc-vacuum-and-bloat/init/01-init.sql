CREATE EXTENSION IF NOT EXISTS pageinspect;

CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    balance INTEGER NOT NULL
);

INSERT INTO accounts (balance)
SELECT 1000
FROM generate_series(1, 50000);
