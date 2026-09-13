#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib
if [ -f postgresql.jar ]; then echo "SKIP (exists) postgresql.jar"; exit 0; fi
curl -sfL "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar" -o postgresql.jar \
  && echo "OK postgresql.jar ($(wc -c < postgresql.jar) bytes)"
