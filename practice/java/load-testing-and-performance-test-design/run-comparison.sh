#!/bin/bash
# Runs the identical real server twice, once against a real closed-loop
# generator (fixed concurrency) and once against a real open-loop generator
# (k6, constant-arrival-rate) -- same code path (TargetServer's real,
# periodic stop-the-world-style pause), two real, independently measured
# percentile sets.
set -e
cd "$(dirname "$0")"

PORT1=19101
PORT2=19102

echo "=== Closed-loop generator (Java, concurrency=5, 10s) ==="
java -cp out TargetServer "$PORT1" > /tmp/target-server-closed.log 2>&1 &
SERVER_PID=$!
sleep 0.5
java -cp out ClosedLoopLoadGenerator "http://localhost:$PORT1/work" 5 10
kill "$SERVER_PID" 2>/dev/null || true
wait "$SERVER_PID" 2>/dev/null || true
echo ""

echo "=== Open-loop generator (k6, constant-arrival-rate, 40 req/s, 10s) ==="
java -cp out TargetServer "$PORT2" > /tmp/target-server-open.log 2>&1 &
SERVER_PID=$!
sleep 0.5
TARGET_PORT="$PORT2" k6 run --summary-trend-stats="avg,min,med,max,p(50),p(95),p(99)" k6/open-loop-test.js
kill "$SERVER_PID" 2>/dev/null || true
wait "$SERVER_PID" 2>/dev/null || true
