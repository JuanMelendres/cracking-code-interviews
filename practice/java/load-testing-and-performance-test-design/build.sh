#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/TargetServer.java src/ClosedLoopLoadGenerator.java
echo "OK compiled TargetServer, ClosedLoopLoadGenerator"
command -v k6 >/dev/null 2>&1 && echo "OK k6 found: $(k6 version)" || echo "MISSING k6 -- install via 'brew install k6' or see https://k6.io/docs/get-started/installation/"
