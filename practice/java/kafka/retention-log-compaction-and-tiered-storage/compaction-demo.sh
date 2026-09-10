#!/usr/bin/env bash
# Real log-compaction demo against a real, disposable Kafka broker.
# Requires Docker. Run `docker compose up -d` first.
set -euo pipefail

BROKER="localhost:9092"
CONTAINER="kafka-compaction-demo"
TOPIC="user-profile-compacted"
BIN="/opt/kafka/bin"

kexec() { docker exec "$CONTAINER" "$@"; }
kexeci() { docker exec -i "$CONTAINER" "$@"; }

echo "### Waiting for the broker to accept requests..."
for i in $(seq 1 30); do
  if kexec "$BIN/kafka-broker-api-versions.sh" --bootstrap-server "$BROKER" >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

echo "### Creating a compacted topic with tiny segments (to force fast, observable compaction)"
kexec "$BIN/kafka-topics.sh" --delete --bootstrap-server "$BROKER" --topic "$TOPIC" >/dev/null 2>&1 || true
sleep 1
kexec "$BIN/kafka-topics.sh" --create --bootstrap-server "$BROKER" --topic "$TOPIC" \
  --partitions 1 --replication-factor 1 \
  --config cleanup.policy=compact \
  --config segment.bytes=1000 \
  --config segment.ms=1000 \
  --config min.cleanable.dirty.ratio=0.01 \
  --config delete.retention.ms=5000 \
  --config min.compaction.lag.ms=0 \
  --config file.delete.delay.ms=1000

produce_one() {
  key="$1"; val="$2"
  if [ "$val" = "NULL" ]; then
    printf '%s:<NULL>\n' "$key" | kexeci "$BIN/kafka-console-producer.sh" \
      --bootstrap-server "$BROKER" --topic "$TOPIC" \
      --property "parse.key=true" --property "key.separator=:" --property "null.marker=<NULL>" >/dev/null 2>&1
  else
    printf '%s:%s\n' "$key" "$val" | kexeci "$BIN/kafka-console-producer.sh" \
      --bootstrap-server "$BROKER" --topic "$TOPIC" \
      --property "parse.key=true" --property "key.separator=:" >/dev/null 2>&1
  fi
}

echo "### Producing keyed records, spaced >1s apart so each lands in its own rolled segment:"
echo "###   user-1 -> v1, user-2 -> v1, user-1 -> v2, user-3 -> v1, user-1 -> v3,"
echo "###   user-2 -> v2, user-4 -> v1, user-4 -> <tombstone>"
produce_one user-1 v1;  sleep 1.5
produce_one user-2 v1;  sleep 1.5
produce_one user-1 v2;  sleep 1.5
produce_one user-3 v1;  sleep 1.5
produce_one user-1 v3;  sleep 1.5
produce_one user-2 v2;  sleep 1.5
produce_one user-4 v1;  sleep 1.5
produce_one user-4 NULL; sleep 1.5

echo
echo "### Waiting for the log cleaner to compact the now-closed segments..."
sleep 20

echo
echo "### Consuming the full topic from-beginning (post-compaction state):"
kexec "$BIN/kafka-console-consumer.sh" --bootstrap-server "$BROKER" --topic "$TOPIC" \
  --from-beginning --property print.key=true --property key.separator=":" \
  --timeout-ms 5000 2>&1 | grep -v "TimeoutException\|Processed a total\|Error processing message, terminating consumer process" || true

echo
echo "### Real on-disk segment listing (note the .deleted segments compaction has already merged away):"
kexec sh -c "ls -la /tmp/kafka-logs/${TOPIC}-0/ | grep -v '^total'"
