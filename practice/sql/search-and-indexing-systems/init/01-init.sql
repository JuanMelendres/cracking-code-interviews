CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    body TEXT NOT NULL
);

INSERT INTO articles (title, body) VALUES
('GC Tuning Guide', 'java garbage collection tuning guide for production heaps'),
('Keyword-Stuffed Long Page', 'java java java java collection collection performance database database database networking security testing deployment monitoring logging configuration scaling reliability observability'),
('Python GC Internals', 'python garbage collection internals reference counting cycle detector'),
('Database Indexing Guide', 'database indexing and query performance tuning for postgres'),
('Java Concurrency Basics', 'java concurrency and thread safety fundamentals'),
('Distributed Consensus', 'distributed systems consensus algorithms raft paxos leader election');

-- Real padding rows in the SAME table -- purely so a sequential scan's real
-- cost is actually visible in EXPLAIN ANALYZE. A tiny 6-row table would make
-- LIKE '%...%' fast regardless of indexing, hiding the real point being made.
INSERT INTO articles (title, body)
SELECT 'Padding Article ' || i, 'unrelated filler content number ' || i || ' about nothing in particular'
FROM generate_series(1, 200000) AS i;

-- A real, database-native inverted index: a generated tsvector column
-- (PostgreSQL's own tokenized/normalized document representation) backed by
-- a real GIN index -- the same "term -> postings" structure this pack's
-- Java demos build from scratch, provided natively by PostgreSQL.
ALTER TABLE articles ADD COLUMN search_vector tsvector
    GENERATED ALWAYS AS (to_tsvector('english', title || ' ' || body)) STORED;

CREATE INDEX articles_search_idx ON articles USING GIN (search_vector);
