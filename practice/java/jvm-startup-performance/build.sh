#!/bin/bash
# Builds all three real artifacts this pack compares:
#   1. main.jar          -- plain JVM, java -jar
#   2. app.jsa            -- a real dynamic AppCDS archive, for -XX:SharedArchiveFile
#   3. main-native         -- a real GraalVM native-image binary (requires
#                             GraalVM's native-image on PATH; see README)
set -e
cd "$(dirname "$0")"

mkdir -p out
rm -f out/*.class
javac -d out src/Main.java

jar --create --file main.jar --main-class Main -C out .
echo "OK   main.jar"

rm -f app.jsa
java -XX:ArchiveClassesAtExit=app.jsa -jar main.jar 18888 exit-after-first-request &
PID=$!
until curl -sf http://localhost:18888/health >/dev/null 2>&1; do sleep 0.01; done
wait "$PID" 2>/dev/null || true
echo "OK   app.jsa ($(wc -c < app.jsa) bytes)"

if command -v native-image >/dev/null 2>&1; then
  native-image -jar main.jar -o main-native --no-fallback
  echo "OK   main-native ($(wc -c < main-native) bytes)"
else
  echo "SKIP main-native -- native-image not on PATH (see README for how to get GraalVM)"
fi
