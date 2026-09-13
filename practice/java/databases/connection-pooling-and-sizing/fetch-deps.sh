#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

HIKARI_VER=5.1.0
PG_DRIVER_VER=42.7.3
SLF4J_VER=2.0.13

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/$HIKARI_VER/HikariCP-$HIKARI_VER.jar" hikaricp.jar
fetch "https://repo1.maven.org/maven2/org/postgresql/postgresql/$PG_DRIVER_VER/postgresql-$PG_DRIVER_VER.jar" postgresql.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/$SLF4J_VER/slf4j-api-$SLF4J_VER.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/$SLF4J_VER/slf4j-simple-$SLF4J_VER.jar" slf4j-simple.jar

echo "All dependencies present in lib/."
