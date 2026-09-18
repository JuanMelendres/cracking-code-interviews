#!/bin/bash
# Measures real wall-clock time from process launch to the first successful
# HTTP response, across N runs, for one given command. This is the actual
# cold-start metric a serverless platform or a fast-scaling container cares
# about -- not just "JVM prints READY" but "can it actually serve."
set -e
LABEL="$1"; shift
PORT="$1"; shift
RUNS="${RUNS:-5}"

total=0
echo "=== $LABEL ($RUNS runs) ==="
for i in $(seq 1 "$RUNS"); do
  START=$(date +%s%N)
  "$@" "$PORT" >/tmp/jvm-startup-run.log 2>&1 &
  PID=$!
  until curl -sf "http://localhost:$PORT/health" >/dev/null 2>&1; do
    sleep 0.001
  done
  END=$(date +%s%N)
  ELAPSED_MS=$(( (END - START) / 1000000 ))
  echo "  run $i: ${ELAPSED_MS}ms"
  total=$((total + ELAPSED_MS))
  kill "$PID" 2>/dev/null || true
  wait "$PID" 2>/dev/null || true
done
AVG=$((total / RUNS))
echo "  average: ${AVG}ms"
echo ""
