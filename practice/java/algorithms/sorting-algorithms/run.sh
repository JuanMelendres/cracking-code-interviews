#!/usr/bin/env bash
# Real, deterministic sorting-algorithm demos. Pure JDK, no dependencies.
# Tested on OpenJDK 21.0.12.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -d out src/*.java

echo "### 1. QuickSort: first-element pivot vs. random pivot, three input shapes"
java -cp out QuickSortPivotDemo

echo
echo "### 2. Stability: TimSort (Arrays.sort) vs. a naive in-place quicksort"
java -cp out StabilityDemo

echo
echo "### 3. InsertionSort vs. MergeSort: small/nearly-sorted vs. large/random"
java -cp out InsertionVsMergeDemo

rm -rf out
