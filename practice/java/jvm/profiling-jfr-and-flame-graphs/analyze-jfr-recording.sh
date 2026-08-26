#!/bin/bash
# Real analysis of workload.jfr using only the JDK's own built-in `jfr` CLI -- no
# async-profiler binary, no third-party tool. Aggregates the real top-of-stack
# method for every jdk.ExecutionSample (CPU) and jdk.ObjectAllocationSample
# (allocation) event: exactly the data a flame graph visualizes as bar width, here
# printed as a real, ranked frequency table instead of rendered as an image.
set -euo pipefail
cd "$(dirname "$0")"

if [ ! -f workload.jfr ]; then
    echo "workload.jfr not found -- run ./run-profiled-workload.sh first." >&2
    exit 1
fi

echo "=== Real event counts captured in this recording ==="
jfr summary workload.jfr | grep -E "jdk.ExecutionSample|jdk.ObjectAllocationSample"

echo
echo "=== Real CPU profile: top-of-stack method by real sample count ==="
echo "(this ranking IS the data a flame graph's bar widths represent)"
jfr print --events jdk.ExecutionSample --stack-depth 1 workload.jfr \
    | grep -A1 "stackTrace = \[" | grep -v "stackTrace\|--" \
    | sed -E 's/^[[:space:]]+//' | sort | uniq -c | sort -rn | head -8

echo
echo "=== Real allocation profile: which class is actually being allocated ==="
jfr print --events jdk.ObjectAllocationSample workload.jfr \
    | grep "objectClass" | sort | uniq -c | sort -rn | head -8
