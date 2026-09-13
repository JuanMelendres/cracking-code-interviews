CREATE ROLE replicator WITH REPLICATION LOGIN PASSWORD 'replicatorpass';

CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    owner TEXT NOT NULL,
    balance NUMERIC NOT NULL
);

INSERT INTO accounts (owner, balance) VALUES ('alice', 1000.00);
