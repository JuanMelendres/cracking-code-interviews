CREATE ROLE replicator WITH REPLICATION LOGIN PASSWORD 'replicatorpass';

CREATE TABLE ledger (
    seq SERIAL PRIMARY KEY,
    written_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    payload TEXT NOT NULL
);
