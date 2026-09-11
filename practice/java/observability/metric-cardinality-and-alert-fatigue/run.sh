#!/usr/bin/env bash
# Real, deterministic metric-cardinality and alert-fatigue demos. Requires
# ./fetch-deps.sh once (Micrometer, pure JDK, no server needed for
# SimpleMeterRegistry). Tested on OpenJDK 21.0.12.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -cp "lib/*" -d out src/*.java

echo "### 1. Metric cardinality: identical request volume, wildly different time-series counts"
java -cp "out:lib/*" CardinalityExplosionDemo

echo
echo "### 2. Alert fatigue: naive static threshold vs. multi-window burn-rate alerting"
java -cp out BurnRateAlertingDemo

rm -rf out
