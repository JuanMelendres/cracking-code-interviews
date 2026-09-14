#!/bin/bash
# Compiles and runs the real, deliberately inefficient HotspotWorkload under a real
# JDK Flight Recorder session -- built into every JDK 11+ distribution, no agent or
# external tool install required. Produces workload.jfr, a real binary recording of
# real CPU and allocation samples taken while the JVM actually ran.
set -euo pipefail
cd "$(dirname "$0")"

mkdir -p out
javac -d out HotspotWorkload.java

echo "=== Running HotspotWorkload for 10 real seconds under a real JFR recording ==="
rm -f workload.jfr
java -XX:StartFlightRecording=filename=workload.jfr,settings=profile -cp out HotspotWorkload 10

echo
echo "Real recording written to workload.jfr ($(du -h workload.jfr | cut -f1))"
