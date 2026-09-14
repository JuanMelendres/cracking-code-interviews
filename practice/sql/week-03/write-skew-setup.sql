DROP TABLE IF EXISTS on_call;
CREATE TABLE on_call (doctor TEXT PRIMARY KEY, is_on_call BOOLEAN NOT NULL);
INSERT INTO on_call VALUES ('Alice', true), ('Bob', true);
