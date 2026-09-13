#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

MICROMETER_VER=1.13.6
HDR_VER=2.2.2
LATENCY_UTILS_VER=2.0.3

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-core/$MICROMETER_VER/micrometer-core-$MICROMETER_VER.jar" micrometer-core.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-commons/$MICROMETER_VER/micrometer-commons-$MICROMETER_VER.jar" micrometer-commons.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-observation/$MICROMETER_VER/micrometer-observation-$MICROMETER_VER.jar" micrometer-observation.jar
fetch "https://repo1.maven.org/maven2/org/hdrhistogram/HdrHistogram/$HDR_VER/HdrHistogram-$HDR_VER.jar" hdrhistogram.jar
fetch "https://repo1.maven.org/maven2/org/latencyutils/LatencyUtils/$LATENCY_UTILS_VER/LatencyUtils-$LATENCY_UTILS_VER.jar" latencyutils.jar

echo "All dependencies present in lib/."
