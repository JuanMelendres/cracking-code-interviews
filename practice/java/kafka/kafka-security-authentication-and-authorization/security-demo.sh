#!/usr/bin/env bash
# Real SASL/PLAIN authentication + real KRaft ACL authorization against a
# real, disposable Kafka 3.8.0 broker. Requires Docker and the broker
# already started via `docker compose up -d` (see README.md).
set -euo pipefail
cd "$(dirname "$0")"

CONTAINER=kafka-security-demo
BIN=/opt/kafka/bin

docker cp admin.properties "$CONTAINER":/tmp/admin.properties
docker cp alice.properties "$CONTAINER":/tmp/alice.properties
docker cp alice-wrong-password.properties "$CONTAINER":/tmp/alice-wrong-password.properties

echo "### 1. Real topics created as admin (a real SASL/PLAIN-authenticated super user)"
docker exec "$CONTAINER" "$BIN/kafka-topics.sh" --bootstrap-server localhost:9092 --command-config /tmp/admin.properties \
    --create --topic alice-allowed-topic --partitions 1 --replication-factor 1
docker exec "$CONTAINER" "$BIN/kafka-topics.sh" --bootstrap-server localhost:9092 --command-config /tmp/admin.properties \
    --create --topic alice-forbidden-topic --partitions 1 --replication-factor 1

echo
echo "### 2. Real ACL grant: alice gets Read/Write/Describe on ONE topic only -- no group grant yet"
docker exec "$CONTAINER" "$BIN/kafka-acls.sh" --bootstrap-server localhost:9092 --command-config /tmp/admin.properties \
    --add --allow-principal User:alice --operation Read --operation Write --operation Describe --topic alice-allowed-topic

echo
echo "### 3. alice produces to her ALLOWED topic -- real success"
echo "hello-from-alice" | docker exec -i "$CONTAINER" "$BIN/kafka-console-producer.sh" --bootstrap-server localhost:9092 \
    --producer.config /tmp/alice.properties --topic alice-allowed-topic --sync --max-block-ms 5000

echo
echo "### 4. alice produces to the FORBIDDEN topic -- real TopicAuthorizationException"
echo "hello-from-alice-forbidden" | docker exec -i "$CONTAINER" "$BIN/kafka-console-producer.sh" --bootstrap-server localhost:9092 \
    --producer.config /tmp/alice.properties --topic alice-forbidden-topic --sync --max-block-ms 5000 2>&1 || true

echo
echo "### 5. alice with a WRONG password -- real SaslAuthenticationException (authentication, not authorization)"
echo "test" | docker exec -i "$CONTAINER" "$BIN/kafka-console-producer.sh" --bootstrap-server localhost:9092 \
    --producer.config /tmp/alice-wrong-password.properties --topic alice-allowed-topic --sync --max-block-ms 5000 2>&1 || true

echo
echo "### 6. alice tries to CONSUME her own allowed topic -- real GroupAuthorizationException"
echo "    (a real, discovered gotcha: topic ACLs alone do not grant consume access --"
echo "     consuming also requires a real ACL grant on the consumer GROUP resource)"
docker exec "$CONTAINER" "$BIN/kafka-console-consumer.sh" --bootstrap-server localhost:9092 \
    --consumer.config /tmp/alice.properties --topic alice-allowed-topic --from-beginning --max-messages 1 --timeout-ms 8000 2>&1 || true

echo
echo "### 7. Real fix: grant the missing group ACL, retry -- real success, real message"
docker exec "$CONTAINER" "$BIN/kafka-acls.sh" --bootstrap-server localhost:9092 --command-config /tmp/admin.properties \
    --add --allow-principal User:alice --operation Read --group "*"
docker exec "$CONTAINER" "$BIN/kafka-console-consumer.sh" --bootstrap-server localhost:9092 \
    --consumer.config /tmp/alice.properties --topic alice-allowed-topic --from-beginning --max-messages 1 --timeout-ms 8000

echo
echo "### 8. admin (super user) freely produces+consumes the topic alice is forbidden from -- real ACL bypass"
echo "hello-from-admin" | docker exec -i "$CONTAINER" "$BIN/kafka-console-producer.sh" --bootstrap-server localhost:9092 \
    --producer.config /tmp/admin.properties --topic alice-forbidden-topic --sync --max-block-ms 5000
docker exec "$CONTAINER" "$BIN/kafka-console-consumer.sh" --bootstrap-server localhost:9092 \
    --consumer.config /tmp/admin.properties --topic alice-forbidden-topic --from-beginning --max-messages 1 --timeout-ms 8000
