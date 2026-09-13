DROP TABLE IF EXISTS events;

-- Declarative HASH partitioning by customer_id -- the same mechanism a
-- shard router would implement at the application layer, but done by
-- Postgres itself within one logical database.
CREATE TABLE events (
    id BIGSERIAL,
    customer_id BIGINT NOT NULL,
    event_type TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (id, customer_id)
) PARTITION BY HASH (customer_id);

CREATE TABLE events_p0 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE events_p1 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 1);
CREATE TABLE events_p2 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 2);
CREATE TABLE events_p3 PARTITION OF events FOR VALUES WITH (MODULUS 4, REMAINDER 3);

-- seed: 40,000 events across 1,000 distinct customers
INSERT INTO events (customer_id, event_type)
SELECT (i % 1000) + 1, 'click'
FROM generate_series(1, 40000) AS i;

ANALYZE events;
