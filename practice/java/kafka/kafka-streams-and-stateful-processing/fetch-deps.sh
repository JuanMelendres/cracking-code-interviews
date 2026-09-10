#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

KAFKA_VER=3.8.0
SLF4J_VER=2.0.13
ZSTD_VER=1.5.6-3
LZ4_VER=1.8.0
SNAPPY_VER=1.1.10.5

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/apache/kafka/kafka-streams/$KAFKA_VER/kafka-streams-$KAFKA_VER.jar" kafka-streams.jar
fetch "https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/$KAFKA_VER/kafka-clients-$KAFKA_VER.jar" kafka-clients.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/$SLF4J_VER/slf4j-api-$SLF4J_VER.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/$SLF4J_VER/slf4j-simple-$SLF4J_VER.jar" slf4j-simple.jar
fetch "https://repo1.maven.org/maven2/com/github/luben/zstd-jni/$ZSTD_VER/zstd-jni-$ZSTD_VER.jar" zstd-jni.jar
fetch "https://repo1.maven.org/maven2/org/lz4/lz4-java/$LZ4_VER/lz4-java-$LZ4_VER.jar" lz4-java.jar
fetch "https://repo1.maven.org/maven2/org/xerial/snappy/snappy-java/$SNAPPY_VER/snappy-java-$SNAPPY_VER.jar" snappy-java.jar

echo "All dependencies present in lib/."
