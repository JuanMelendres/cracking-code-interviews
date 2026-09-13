#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

echo "=== Starting real Kafka broker (Docker) ==="
docker compose up -d
echo "Waiting for broker to be ready..."
sleep 10

mkdir -p out
javac -cp "lib/*" -d out src/*.java

echo
echo "############################################"
echo "# Demo 1: PoisonMessagePartitionBlockingDemo"
echo "############################################"
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" PoisonMessagePartitionBlockingDemo

echo
echo "############################################"
echo "# Demo 2: DlqRecoveryDemo"
echo "############################################"
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" DlqRecoveryDemo

echo
echo "############################################"
echo "# Demo 3: ConsumersExceedPartitionsDemo"
echo "############################################"
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" ConsumersExceedPartitionsDemo

echo
echo "=== Tearing down real Kafka broker ==="
docker compose down -v
rm -rf out
