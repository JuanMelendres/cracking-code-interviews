#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

echo "############################################"
echo "# 1/2: Real, reproduced PostgreSQL deadlock #"
echo "############################################"
./deadlock-demo.sh

echo
echo "############################################"
echo "# 2/2: No lock escalation, and its real     #"
echo "#      replacement failure mode              #"
echo "############################################"
./no-escalation-demo.sh
