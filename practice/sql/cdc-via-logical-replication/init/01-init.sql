CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    owner TEXT NOT NULL,
    balance INTEGER NOT NULL
);
