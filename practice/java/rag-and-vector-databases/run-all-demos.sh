#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

echo "=== Bringing up a real PostgreSQL 16 + pgvector ==="
docker compose up -d
until docker exec ragdemo-pg pg_isready -U postgres >/dev/null 2>&1; do sleep 1; done
echo "Ready."

mkdir -p out
javac -cp "lib/*" -d out src/demo/*.java

echo
echo "############################################"
echo "# RAG and Vector Databases (pgvector) demo  #"
echo "############################################"
java -cp "out:lib/*" demo.RagVectorDbDemo

echo
echo "=== Tearing down ==="
rm -rf out
docker compose down -v >/dev/null
echo "Done."
