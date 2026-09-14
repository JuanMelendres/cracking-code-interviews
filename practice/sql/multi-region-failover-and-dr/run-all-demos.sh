#!/bin/bash
# Runs all three real demos in sequence. Each demo brings its own containers up and
# tears them down, so they can also be run individually and in any order.
set -euo pipefail
cd "$(dirname "$0")"

echo "############################################"
echo "# 1/3: RPO + RTO — hot standby (streaming) #"
echo "############################################"
./rpo-demo.sh

echo
echo "############################################"
echo "# 2/3: RPO — WAL archiving (log-shipping)  #"
echo "############################################"
./rpo-archive-demo.sh

echo
echo "############################################"
echo "# 3/3: Split-brain — naive vs. fenced      #"
echo "############################################"
./splitbrain-demo.sh
