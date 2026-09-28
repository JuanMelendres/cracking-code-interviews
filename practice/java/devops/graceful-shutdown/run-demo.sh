#!/usr/bin/env bash
# Drives both servers through an identical scenario: start, send one long
# request, deliver SIGTERM while that request is still in flight, and record
# exactly what the client saw. Real output captured in output-transcript.txt.
set -u

run_case() {
  local mode="$1" port="$2"
  echo "================================================================"
  echo "CASE: $mode"
  echo "================================================================"

  java -cp out ShutdownDemo "$mode" "$port" &
  local pid=$!
  sleep 1

  echo "[client] GET /readyz -> $(curl -s -o /dev/null -w '%{http_code}' "http://localhost:$port/readyz")"
  echo "[client] starting GET /work (server takes ~3s)"
  ( curl -s -S -m 15 -w '\n[client] /work HTTP status: %{http_code}, total %{time_total}s\n' \
      "http://localhost:$port/work" 2>&1 | sed 's/^/[client] /' ) &
  local curl_pid=$!

  sleep 1
  echo "[driver] sending SIGTERM to server pid $pid, 1s into a 3s request"
  kill -TERM "$pid" 2>/dev/null

  wait "$curl_pid" 2>/dev/null
  wait "$pid" 2>/dev/null
  echo "[driver] server process exited"
  echo
}

cd "$(dirname "$0")"
mkdir -p out
javac -d out src/ShutdownDemo.java || exit 1

run_case abrupt 8111
sleep 1
run_case graceful 8112
