#!/usr/bin/env bash
# Real, deterministic matrix/grid-traversal demos. Pure JDK, no dependencies.
# Tested on OpenJDK 21.0.12.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -d out src/*.java

echo "### 1. Rotate Image (LC 48): in-place transpose+reverse vs. brute-force ground truth"
java -cp out RotateMatrixDemo

echo
echo "### 2. Spiral Matrix (LC 54): correctness across square, non-square, and degenerate shapes"
java -cp out SpiralTraversalDemo

echo
echo "### 3a. Recursive DFS flood fill: real breaking point, one fresh JVM process per size"
echo "        (a same-process sweep produces a JIT-warmup artifact -- see GridFloodFillStackDemo's own comment)"
for cells in 1000 5000 10000 12000 14000 16000 18000 20000; do
    java -cp out GridFloodFillStackDemo $((cells / 10)) 10
done

echo
echo "### 3b. Iterative DFS/BFS (explicit stack/queue) at 2,000,000 cells -- far past the recursive breaking point"
echo "         and LC 200 (Number of Islands) correctness check"
java -cp out GridFloodFillStackDemo

rm -rf out
