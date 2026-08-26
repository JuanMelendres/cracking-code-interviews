# Messaging patterns: point-to-point vs. publish-subscribe (T-710) — runnable verification

Real, executed output backing
[`handbook/system-design/messaging-patterns-and-change-data-capture.md`](../../../../handbook/system-design/messaging-patterns-and-change-data-capture.md)
(T-710). A real Kafka broker, real consumer groups, and the identical 10 real
messages delivered two structurally different ways depending only on how consumers
are grouped — not on anything about the messages or topic themselves.

## Files

- `docker-compose.yml` — a real `apache/kafka:3.7.0` KRaft broker with a
  host-accessible listener, reused from this repository's established Kafka demo
  pattern.
- `PointToPointVsPubSubDemo.java` — both patterns, back to back, against two
  separate topics carrying identical message counts.

## Run

```bash
cd practice/java/kafka/messaging-patterns-point-to-point-vs-pubsub
./fetch-deps.sh
docker compose up -d
mkdir -p out
javac -cp "lib/*" -d out src/PointToPointVsPubSubDemo.java
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" PointToPointVsPubSubDemo
docker compose down -v
```

## Real observed output (last full run, Kafka 3.7.0)

```
=== Point-to-point (competing consumers): 3 real consumers, SAME group "order-processors" ===
  consumer-0 received: 10
  consumer-1 received: 0
  consumer-2 received: 0
Real total received across the whole group: 10 (expected 10 -- each message consumed exactly ONCE across the group)

=== Publish-subscribe: 3 real consumers, 3 DIFFERENT groups ===
  inventory-service received: 10 (expected 10 -- its OWN independent copy of every message)
  email-service received: 10 (expected 10 -- its OWN independent copy of every message)
  analytics-service received: 10 (expected 10 -- its OWN independent copy of every message)
```

10 real messages, produced once, to a topic with 3 real consumer instances attached
either way. Grouped into one consumer group ("point-to-point"), the group as a whole
really received exactly 10 messages total — each message consumed once. Grouped
into three independent consumer groups ("publish-subscribe"), each group really
received its own full copy of all 10 messages — 30 real deliveries total from one
real publish of 10.

## What this does and does not prove

This demo uses a single-partition topic, so within the point-to-point group only one
consumer instance was actually assigned any work — real, correct Kafka behavior
(see [Consumer Lag, Backpressure, and DLQ Strategy](../../../../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md)
for the real, separate proof that partition count bounds how many consumers in a
group can do real work simultaneously). This demo is not about load distribution
within a group — it is about the more fundamental delivery-pattern distinction:
exactly-once-per-group vs. once-per-independent-subscriber, which holds regardless
of partition count, and is real and measurable here with only 10 messages.
