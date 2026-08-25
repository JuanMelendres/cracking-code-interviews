CREATE TABLE accounts (
    id INTEGER PRIMARY KEY,
    owner TEXT NOT NULL,
    balance NUMERIC NOT NULL
);

INSERT INTO accounts (id, owner, balance) VALUES
    (1, 'alice', 1000.00),
    (2, 'bob', 1000.00);

CREATE TABLE many_rows (
    id SERIAL PRIMARY KEY,
    val INTEGER NOT NULL
);

INSERT INTO many_rows (val)
SELECT g FROM generate_series(1, 20000) AS g;
