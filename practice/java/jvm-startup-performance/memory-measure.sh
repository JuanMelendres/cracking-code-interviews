#!/bin/bash
# Real peak RSS (maximum resident set size) via /usr/bin/time -l (macOS).
# The server self-exits 50ms after handling one real request (see Main.java's
# "exit-after-first-request" flag), so /usr/bin/time can run synchronously
# and print its real footer stats once the process actually exits.
set -e
LABEL="$1"; shift
PORT="$1"; shift

echo "=== $LABEL ==="
/usr/bin/time -l "$@" "$PORT" exit-after-first-request > /tmp/jvm-startup-mem.log 2>&1 &
until curl -sf "http://localhost:$PORT/health" >/dev/null 2>&1; do sleep 0.01; done
wait
grep -E "real|maximum resident set size" /tmp/jvm-startup-mem.log
echo ""
