#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

AVRO_VER=1.11.3
JACKSON_VER=2.16.1
SLF4J_VER=2.0.13
COMMONS_COMPRESS_VER=1.26.0

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/apache/avro/avro/$AVRO_VER/avro-$AVRO_VER.jar" avro.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/$JACKSON_VER/jackson-core-$JACKSON_VER.jar" jackson-core.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/$JACKSON_VER/jackson-databind-$JACKSON_VER.jar" jackson-databind.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/$JACKSON_VER/jackson-annotations-$JACKSON_VER.jar" jackson-annotations.jar
fetch "https://repo1.maven.org/maven2/org/apache/commons/commons-compress/$COMMONS_COMPRESS_VER/commons-compress-$COMMONS_COMPRESS_VER.jar" commons-compress.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/$SLF4J_VER/slf4j-api-$SLF4J_VER.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/$SLF4J_VER/slf4j-simple-$SLF4J_VER.jar" slf4j-simple.jar

echo "All dependencies present in lib/."
