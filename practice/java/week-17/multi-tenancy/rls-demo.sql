-- Real Row-Level Security demo, run against PostgreSQL 16 (postgres:16-alpine).
-- Reproduce: docker run -d --name security-pg -e POSTGRES_PASSWORD=demo \
--   -e POSTGRES_DB=appdb -p 15432:5432 postgres:16-alpine
-- Then: docker exec -i security-pg psql -U postgres -d appdb < rls-demo.sql

CREATE TABLE orders (
  id serial primary key,
  tenant_id text not null,
  customer_name text not null,
  amount_usd numeric not null
);

INSERT INTO orders (tenant_id, customer_name, amount_usd) VALUES
  ('tenant_a', 'Acme Corp', 500.00),
  ('tenant_a', 'Acme Corp', 750.00),
  ('tenant_b', 'Globex Inc', 1200.00);

ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

CREATE POLICY tenant_isolation ON orders
  USING (tenant_id = current_setting('app.tenant_id', true));

CREATE ROLE app_user LOGIN PASSWORD 'demo' NOSUPERUSER;
GRANT SELECT, INSERT ON orders TO app_user;
GRANT USAGE, SELECT ON SEQUENCE orders_id_seq TO app_user;

-- Verification queries (run as app_user, non-superuser, subject to the policy):
--   psql -U app_user -d appdb -c "SET app.tenant_id = 'tenant_a'; SELECT * FROM orders;"
--   psql -U app_user -d appdb -c "SET app.tenant_id = 'tenant_b'; SELECT * FROM orders;"
--   psql -U app_user -d appdb -c "SELECT * FROM orders;"  -- no tenant context set: 0 rows (fail closed)
--
-- Contrast (run as superuser postgres, BYPASSRLS by default, no SET needed):
--   psql -U postgres -d appdb -c "SELECT * FROM orders;"  -- returns ALL tenants' rows
