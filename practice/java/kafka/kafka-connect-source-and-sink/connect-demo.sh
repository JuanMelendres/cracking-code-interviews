#!/usr/bin/env bash
# Real Kafka Connect demo: FileStreamSource -> Kafka topic -> FileStreamSink,
# entirely via configuration, zero custom producer/consumer code, against a
# real, disposable Kafka 3.8.0 broker (Docker, KRaft). Proves incremental
# streaming and offset-based resume-after-restart with no reprocessing.
set -euo pipefail

BROKER="localhost:9092"
CONTAINER="kafka-connect-demo"
BIN="/opt/kafka/bin"
DEMO_DIR="/tmp/connect-demo"

kexec() { docker exec "$CONTAINER" "$@"; }

echo "### Waiting for the broker to accept requests..."
for i in $(seq 1 30); do
  if kexec "$BIN/kafka-broker-api-versions.sh" --bootstrap-server "$BROKER" >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

echo "### Setting up worker + connector configs and a fresh input file"
kexec bash -c "
mkdir -p $DEMO_DIR
cd $DEMO_DIR
cp /opt/kafka/config/connect-standalone.properties worker.properties
sed -i 's#offset.storage.file.filename=/tmp/connect.offsets#offset.storage.file.filename=$DEMO_DIR/connect.offsets#' worker.properties
echo 'plugin.path=/opt/kafka/libs' >> worker.properties
cat > source.properties <<'EOF'
name=local-file-source
connector.class=FileStreamSource
tasks.max=1
file=$DEMO_DIR/input.txt
topic=connect-demo-topic
EOF
cat > sink.properties <<'EOF'
name=local-file-sink
connector.class=FileStreamSink
tasks.max=1
file=$DEMO_DIR/output.txt
topics=connect-demo-topic
EOF
rm -f connect.offsets connect.log connect2.log
printf 'line-1\nline-2\n' > input.txt
touch output.txt
"

echo "### Starting the Kafka Connect standalone worker (source + sink connectors, no custom code)"
kexec bash -c "cd $DEMO_DIR && /opt/kafka/bin/connect-standalone.sh worker.properties source.properties sink.properties > connect.log 2>&1 &"
sleep 15

echo
echo "### Real topic content (FileStreamSource wrote these, JsonConverter-wrapped):"
kexec "$BIN/kafka-console-consumer.sh" --bootstrap-server "$BROKER" --topic connect-demo-topic --from-beginning --timeout-ms 5000 2>&1 | grep -v "Processed a total\|Error processing message, terminating consumer process\|TimeoutException"

echo
echo "### output.txt after the initial round trip (input file -> Kafka -> output file):"
kexec cat "$DEMO_DIR/output.txt"

echo
echo "### Appending 2 more lines to the input file WHILE the worker is running:"
kexec bash -c "printf 'line-3\nline-4\n' >> $DEMO_DIR/input.txt"
sleep 8
echo "output.txt now:"
kexec cat "$DEMO_DIR/output.txt"

echo
echo "### Killing the worker (simulating a crash), then restarting it from the SAME offset file:"
kexec bash -c "pkill -f ConnectStandalone" || true
sleep 3
kexec bash -c "cd $DEMO_DIR && /opt/kafka/bin/connect-standalone.sh worker.properties source.properties sink.properties > connect2.log 2>&1 &"
sleep 12

echo
echo "### Appending exactly ONE new line after the restart:"
kexec bash -c "printf 'line-5\n' >> $DEMO_DIR/input.txt"
sleep 8
echo "### Final output.txt (expect line-1..line-5, no duplicates, no reprocessing):"
kexec cat "$DEMO_DIR/output.txt"

kexec bash -c "pkill -f ConnectStandalone" || true
