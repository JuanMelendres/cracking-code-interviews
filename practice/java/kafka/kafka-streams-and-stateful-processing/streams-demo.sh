#!/usr/bin/env bash
# Real Kafka Streams demo (word count via KStream -> groupBy -> KTable)
# against a real, disposable Kafka broker. Requires Docker + `docker compose
# up -d` first, and `./fetch-deps.sh` + a compile beforehand.
set -euo pipefail
cd "$(dirname "$0")"

BROKER="localhost:9092"
CONTAINER="kafka-streams-demo"
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

echo "### Creating the input/output topics"
kexec "$BIN/kafka-topics.sh" --create --bootstrap-server "$BROKER" --topic streams-input --partitions 1 --replication-factor 1 >/dev/null 2>&1 || true
kexec "$BIN/kafka-topics.sh" --create --bootstrap-server "$BROKER" --topic streams-output --partitions 1 --replication-factor 1 >/dev/null 2>&1 || true

rm -rf out
mkdir -p out
javac -cp "lib/*" -d out src/WordCountStreamsDemo.java

echo "### Starting the real Kafka Streams application in the background"
nohup java -cp "out:lib/*" WordCountStreamsDemo > streams-app.log 2>&1 &
APP_PID=$!
sleep 10

echo
echo "### The real topology, printed by the app itself at startup:"
grep -A 24 "^Topologies:" streams-app.log

echo
echo "### Producing three lines of input"
printf 'the quick brown fox\nthe lazy dog\nthe fox runs\n' | \
  kexeci "$BIN/kafka-console-producer.sh" --bootstrap-server "$BROKER" --topic streams-input
sleep 3

echo
echo "### Consuming the aggregated KTable output (word -> running count):"
kexec "$BIN/kafka-console-consumer.sh" --bootstrap-server "$BROKER" --topic streams-output \
  --from-beginning --property print.key=true --property key.separator=":" \
  --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer \
  --timeout-ms 5000 2>&1 | grep -v "TimeoutException\|Processed a total\|Error processing message, terminating consumer process" || true

echo
echo "### Describing the changelog topic Kafka Streams created automatically for the KTable's state store:"
kexec "$BIN/kafka-topics.sh" --describe --bootstrap-server "$BROKER" --topic word-count-demo-app-word-counts-store-changelog

echo
echo "### Stopping the application"
kill "$APP_PID" 2>/dev/null || true
wait "$APP_PID" 2>/dev/null || true
