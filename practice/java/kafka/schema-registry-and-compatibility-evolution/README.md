# Schema Registry and compatibility evolution (T-708) — runnable verification

Real, executed output backing
[`handbook/kafka/schema-registry-and-compatibility-evolution.md`](../../../../handbook/kafka/schema-registry-and-compatibility-evolution.md)
(T-708). A real Confluent Schema Registry 7.6.1 (Docker), backed by a real Kafka
broker, driven with real HTTP calls (no mocked responses) — plus a real, compiling
Java demo showing exactly what the registry's compatibility check is protecting: real
Avro writer/reader schema resolution at the byte level.

## Setup and run

Requires Docker, `curl`, and `jq`.

```bash
cd practice/java/kafka/schema-registry-and-compatibility-evolution
docker compose up -d
sleep 15   # first run also pulls the (large) Confluent image
./registry-demo.sh
```

For the Java byte-level demo (no Docker needed for this part):

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
java -cp "out:lib/*" AvroSchemaResolutionDemo
```

Tear down: `docker compose down -v`.

## Real observed output (last full run)

### `registry-demo.sh` — the real 2×2 compatibility matrix

Four isolated subjects, each seeded identically with v1 (`orderId`, `customerId`,
`amount`) then v2 (adds `currency` with a default — accepts cleanly under the default
BACKWARD mode in all four). Two of the four are then switched to FORWARD mode. Each
subject then gets exactly one more real registration attempt.

**Test 1 — add `shippingAddress` with no default:**

```
-- against BACKWARD --
{"error_code":409,"message":"Schema being registered is incompatible with an earlier
schema for subject \"orders-backward-v3test\", details: [{errorType:'READER_FIELD_MISSING_DEFAULT_VALUE',
description:'The field 'shippingAddress' at path '/fields/4' in the new schema has no
default value and is missing in the old schema', ...}]"}
HTTP_STATUS:409

-- against FORWARD --
{"id":7}
HTTP_STATUS:200
```

**Test 2 — remove `amount`, which had no default:**

```
-- against BACKWARD --
{"id":8}
HTTP_STATUS:200

-- against FORWARD --
{"error_code":409,"message":"...The field 'amount' at path '/fields/2' in the old
schema has no default value and is missing in the new schema'..."}
HTTP_STATUS:409
```

**Real result matrix:**

```
                                 BACKWARD           FORWARD
add field, no default (v3)       REJECTED (409)     ACCEPTED (200)
remove field, no default (v4)    ACCEPTED (200)      REJECTED (409)
```

This is the real, direct, and frequently-surprising evidence for the two rules that
matter most in an interview: under BACKWARD compatibility (Confluent's default),
**removing** a field is safe and **adding** one without a default is not — the exact
opposite of what many candidates guess. FORWARD compatibility inverts both results,
for a real, structural reason explained in the handbook chapter, not an arbitrary
registry quirk.

### `AvroSchemaResolutionDemo.java` — what the compatibility check is actually protecting

```
Real bytes written with the v1 (writer) schema: 26 bytes
(no 'currency' field exists anywhere in these bytes — v1 never had one)

=== Decoding those exact v1 bytes with the v2 reader schema (adds 'currency', default "USD") ===
Real decoded record: {"orderId": "order-123", "customerId": "cust-42", "amount": 59.99, "currency": "USD"}
currency = USD  <- filled in from the schema's default, not from the bytes

=== Decoding those exact v1 bytes with the v3 reader schema (adds 'shippingAddress', NO default) ===
Real failure, as expected: AvroTypeException: Found com.crackingcodeinterviews.kafka.Order,
expecting com.crackingcodeinterviews.kafka.Order, missing required field shippingAddress
```

The same real bytes, written once with the v1 schema, are decoded twice with two
different reader schemas — the exact "old data, new reader" scenario BACKWARD
compatibility exists to keep safe. Decoding with v2 succeeds because the decoder has
a default to fall back on for the field that isn't in the bytes; decoding with v3
genuinely throws, because there is no default and no bytes for that field — resolution
has no correct value to produce. This is the real defect a 409 from the registry
prevents from ever reaching a running consumer.

## What this does and does not prove

Both demos run against real software making real decisions (a real running Schema
Registry's compatibility engine; real Avro binary encoding/decoding) — nothing here is
a described rule, everything is an observed result. What this doesn't cover: schema
evolution under NONE or FULL_TRANSITIVE compatibility modes, and the Confluent Kafka
Avro *serializer's* wire-format specifics (the 5-byte magic-byte-plus-schema-ID prefix
it adds on top of raw Avro bytes) — this demo uses the registry's REST API and plain
`org.apache.avro` directly rather than pulling in Confluent's Kafka serializer client
jars, to keep the dependency set to what's actually on Maven Central.
