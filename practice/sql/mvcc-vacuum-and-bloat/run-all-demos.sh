#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

echo "############################################"
echo "# 1/3: Real MVCC tuple versioning           #"
echo "############################################"
./mvcc-tuple-versioning-demo.sh

echo
echo "############################################"
echo "# 2/3: Real bloat and VACUUM vs VACUUM FULL #"
echo "############################################"
./bloat-and-vacuum-full-demo.sh

echo
echo "############################################"
echo "# 3/3: A long transaction really blocks     #"
echo "#      VACUUM from reclaiming dead tuples   #"
echo "############################################"
./long-transaction-blocks-vacuum-demo.sh
