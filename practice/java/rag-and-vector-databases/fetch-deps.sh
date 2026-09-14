#!/bin/bash
# Downloads the real PostgreSQL JDBC driver this pack's demo needs, directly
# from Maven Central -- no Maven/Gradle install required. Jar is gitignored
# (*.jar); run this before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

PG_DRIVER_VER=42.7.4

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/postgresql/postgresql/$PG_DRIVER_VER/postgresql-$PG_DRIVER_VER.jar" postgresql.jar

echo "All dependencies present in lib/."
