#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib
OTEL_VER=1.38.0

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-api/$OTEL_VER/opentelemetry-api-$OTEL_VER.jar" opentelemetry-api.jar
fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-context/$OTEL_VER/opentelemetry-context-$OTEL_VER.jar" opentelemetry-context.jar
fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-sdk/$OTEL_VER/opentelemetry-sdk-$OTEL_VER.jar" opentelemetry-sdk.jar
fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-sdk-common/$OTEL_VER/opentelemetry-sdk-common-$OTEL_VER.jar" opentelemetry-sdk-common.jar
fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-sdk-trace/$OTEL_VER/opentelemetry-sdk-trace-$OTEL_VER.jar" opentelemetry-sdk-trace.jar
fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-sdk-metrics/$OTEL_VER/opentelemetry-sdk-metrics-$OTEL_VER.jar" opentelemetry-sdk-metrics.jar
fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-sdk-logs/$OTEL_VER/opentelemetry-sdk-logs-$OTEL_VER.jar" opentelemetry-sdk-logs.jar
fetch "https://repo1.maven.org/maven2/io/opentelemetry/opentelemetry-exporter-logging/$OTEL_VER/opentelemetry-exporter-logging-$OTEL_VER.jar" opentelemetry-exporter-logging.jar

echo "All dependencies present in lib/."
