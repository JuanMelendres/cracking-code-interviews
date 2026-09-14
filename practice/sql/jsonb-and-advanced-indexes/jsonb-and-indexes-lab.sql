-- Real PostgreSQL 16 lab backing syllabus/06-databases/jsonb-and-advanced-index-types.md (T-618).
-- Run against a disposable container:
--   docker run -d --name idx-demo -e POSTGRES_PASSWORD=demo -p 15930:5432 postgres:16
--   docker exec -i idx-demo psql -U postgres < jsonb-and-indexes-lab.sql

-- ===================== 1. JSONB basics: containment and path operators =====================

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    attributes JSONB NOT NULL
);

INSERT INTO products (name, attributes) VALUES
  ('Trail Runner 200', '{"category": "shoes", "color": "red", "sizes": [8,9,10], "waterproof": true}'),
  ('Trail Runner 200 (blue)', '{"category": "shoes", "color": "blue", "sizes": [7,8,9], "waterproof": true}'),
  ('Office Chair Pro', '{"category": "furniture", "material": "mesh", "adjustable": true}'),
  ('Desk Lamp Mini', '{"category": "lighting", "color": "black", "dimmable": true}');

-- Containment operator @>
SELECT name, attributes->>'color' AS color
FROM products
WHERE attributes @> '{"category": "shoes"}';

-- Array containment
SELECT name FROM products WHERE attributes -> 'sizes' @> '[9]';

-- ===================== 2. GIN index on JSONB: a real, honestly modest win =====================
-- 300,000 rows, independent random category/color, GIN index on the whole jsonb column.

CREATE TABLE big_products AS
SELECT
  gs AS id,
  'product' || gs AS name,
  jsonb_build_object(
    'category', (ARRAY['shoes','furniture','lighting','electronics','apparel'])[1 + floor(random()*5)::int],
    'color', (ARRAY['red','blue','black','white','green'])[1 + floor(random()*5)::int],
    'price', (random() * 500)::numeric(10,2)
  ) AS attributes
FROM generate_series(1, 300000) gs;

-- Before any GIN index
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM big_products WHERE attributes @> '{"category": "electronics", "color": "green"}';

CREATE INDEX idx_big_products_attrs_gin ON big_products USING GIN (attributes);

-- After the GIN index
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM big_products WHERE attributes @> '{"category": "electronics", "color": "green"}';

-- ===================== 3. GIN for full-text search: a real, dramatic win =====================
-- 500,000 rows of article text; a GIN index on a to_tsvector expression.

CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    body TEXT NOT NULL
);

INSERT INTO articles (body)
SELECT
  CASE WHEN gs % 5000 = 0
       THEN 'PostgreSQL query planning and vacuum internals explained for backend engineers, article number ' || gs
       ELSE 'A generic filler article about ' || (ARRAY['cooking','travel','gardening','finance','sports'])[1 + floor(random()*5)::int]
            || ' with random padding text number ' || gs
  END AS body
FROM generate_series(1, 500000) gs;

-- Before any full-text index
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM articles WHERE to_tsvector('english', body) @@ to_tsquery('english', 'vacuum & internals');

CREATE INDEX idx_articles_body_fts ON articles USING GIN (to_tsvector('english', body));

-- After the GIN full-text index
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM articles WHERE to_tsvector('english', body) @@ to_tsquery('english', 'vacuum & internals');

-- ===================== 4. GiST: a real EXCLUDE constraint preventing overlapping bookings =====================

CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE room_bookings (
    id SERIAL PRIMARY KEY,
    room_id INTEGER NOT NULL,
    during DATERANGE NOT NULL,
    EXCLUDE USING GIST (room_id WITH =, during WITH &&)
);

INSERT INTO room_bookings (room_id, during) VALUES (101, '[2026-06-01, 2026-06-05)');
INSERT INTO room_bookings (room_id, during) VALUES (102, '[2026-06-02, 2026-06-04)'); -- different room, overlapping dates: OK
INSERT INTO room_bookings (room_id, during) VALUES (101, '[2026-06-05, 2026-06-08)'); -- same room, non-overlapping dates: OK

SELECT * FROM room_bookings ORDER BY id;

-- Same room, genuinely overlapping dates: real constraint violation
INSERT INTO room_bookings (room_id, during) VALUES (101, '[2026-06-03, 2026-06-06)');

-- ===================== 5. BRIN: real, dramatic index-size win on naturally-ordered data =====================
-- 2,000,000 sensor readings, inserted in timestamp order (the natural case BRIN targets).

CREATE TABLE sensor_readings (
    id BIGSERIAL PRIMARY KEY,
    recorded_at TIMESTAMP NOT NULL,
    value NUMERIC NOT NULL
);

INSERT INTO sensor_readings (recorded_at, value)
SELECT
  TIMESTAMP '2026-01-01 00:00:00' + (gs || ' seconds')::interval,
  (random() * 100)::numeric(10,2)
FROM generate_series(1, 2000000) gs;

CREATE INDEX idx_sensor_readings_btree ON sensor_readings USING BTREE (recorded_at);
CREATE INDEX idx_sensor_readings_brin ON sensor_readings USING BRIN (recorded_at);

SELECT
  pg_size_pretty(pg_relation_size('idx_sensor_readings_btree')) AS btree_size,
  pg_size_pretty(pg_relation_size('idx_sensor_readings_brin')) AS brin_size;

-- Real range query with BOTH indexes present -- the planner's free choice
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM sensor_readings
WHERE recorded_at BETWEEN '2026-01-10 00:00:00' AND '2026-01-11 00:00:00';

-- Drop the B-tree; same query, BRIN-only, planner's free choice
DROP INDEX idx_sensor_readings_btree;
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM sensor_readings
WHERE recorded_at BETWEEN '2026-01-10 00:00:00' AND '2026-01-11 00:00:00';

-- Same query, BRIN forced (enable_seqscan off is a diagnostic override, never a production setting)
SET enable_seqscan = off;
EXPLAIN (ANALYZE, BUFFERS)
SELECT count(*) FROM sensor_readings
WHERE recorded_at BETWEEN '2026-01-10 00:00:00' AND '2026-01-11 00:00:00';
RESET enable_seqscan;
