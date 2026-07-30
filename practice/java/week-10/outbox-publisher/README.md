# Week 10 Java — Transactional Outbox — runnable verification

Real Postgres 16 (Docker) + real single-broker Kafka (Docker) + a real working outbox implementation. This is the week's named deliverable's implementation — see `study-packs/week-10/08-outbox-implementation-deliverable.md` for the full walkthrough and exact reproduce commands.

## Quick setup

```bash
cd practice/java/week-10/outbox-publisher
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
cat > out/simplelogger.properties << 'EOF'
org.slf4j.simpleLogger.defaultLogLevel=warn
EOF

docker run --rm -d --name week10-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week10 -p 5433:5432 postgres:16
docker cp ../../../sql/week-10/outbox/setup.sql week10-pg:/tmp/setup.sql
docker exec -e PGPASSWORD=postgres week10-pg psql -U postgres -d week10 -f /tmp/setup.sql

docker run -d --name week10-kafka -p 9093:9092 \
  -e KAFKA_NODE_ID=1 -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9093 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e CLUSTER_ID=NkU3OEVBNTcwNTJENDM2Ql \
  apache/kafka:3.7.0
sleep 8
```

## 1. Dual-write hazard (the control case, no outbox) — `DualWriteHazardDemo.java`

```bash
java -cp "out:lib/*" DualWriteHazardDemo
```

**Real observed output (last run):**

```
== dual write, no outbox: DB commit succeeds, then "crash" before the Kafka publish ==
Order 1 COMMITTED to Postgres, durable, visible to any other reader right now.
Simulating a crash HERE -- before any Kafka publish call is even attempted.
(In the no-outbox design, nothing else in the system knows this order needs an event published.
 There is no queue, no retry, no record of the intent -- the event is simply gone.)

== verifying the order exists but no event was ever published anywhere ==
orders rows for this customer: 1 (the business write DID survive)
Kafka topic 'order-events': 0 messages for this order (nothing ever published it -- there is no mechanism in this design that could have retried it)
```

## 2. The working outbox, with crash recovery

```bash
# reset first: docker exec -e PGPASSWORD=postgres week10-pg psql -U postgres -d week10 \
#   -c "DELETE FROM outbox; DELETE FROM orders; ALTER SEQUENCE orders_id_seq RESTART; ALTER SEQUENCE outbox_id_seq RESTART;"

java -cp "out:lib/*" OutboxWriter 3
java -cp "out:lib/*" OutboxPoller --crash-after-first-publish
java -cp "out:lib/*" OutboxPoller
java -cp "out:lib/*" VerifyConsumer
```

**Real observed output (last run):** see `study-packs/week-10/08-outbox-implementation-deliverable.md` §5 for the full captured sequence — summary: 3 orders written, 4 Kafka messages delivered (1 duplicate from the simulated crash), 0 lost.

## Teardown

```bash
docker rm -f week10-pg week10-kafka
```
