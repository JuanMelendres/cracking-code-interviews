#!/usr/bin/env bash
# Real, deterministic vector-clock and quorum-replication demos. Pure JDK,
# no dependencies. Tested on OpenJDK 21.0.12.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -d out src/*.java

echo "### 1. Vector clocks: detecting a real concurrent write conflict vs. a real causal update"
java -cp out VectorClockDemo

echo
echo "### 2. Quorum replication: exhaustive proof of the W+R > N overlap rule"
java -cp out QuorumOverlapDemo

rm -rf out
