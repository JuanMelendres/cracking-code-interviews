#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

echo "=== Bringing up a real PostgreSQL 16 (capped at 2 real CPUs) ==="
docker compose up -d
until docker exec pool-pg pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done
echo "Ready."

mkdir -p out
javac -cp "lib/*" -d out src/*.java

echo
echo "############################################"
echo "# 1/3: Real connection-pool exhaustion      #"
echo "############################################"
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" PoolExhaustionDemo

echo
echo "############################################"
echo "# 2/3: Real leak detection                  #"
echo "############################################"
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" LeakDetectionDemo

echo
echo "############################################"
echo "# 3/3: Real pool-sizing throughput          #"
echo "############################################"
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "out:lib/*" PoolSizingThroughputDemo

echo
echo "=== Tearing down ==="
rm -rf out
docker compose down -v >/dev/null
echo "Done."
