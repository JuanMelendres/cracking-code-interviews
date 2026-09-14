#!/bin/bash
# Downloads real graphql-java 26.1 + its runtime deps directly from Maven
# Central -- no Maven/Gradle install required. Jars are gitignored (*.jar);
# run this before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

GRAPHQL_JAVA_VER=26.1
DATALOADER_VER=6.0.0
REACTIVE_STREAMS_VER=1.0.3
JSPECIFY_VER=1.0.0

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/com/graphql-java/graphql-java/$GRAPHQL_JAVA_VER/graphql-java-$GRAPHQL_JAVA_VER.jar" graphql-java.jar
fetch "https://repo1.maven.org/maven2/com/graphql-java/java-dataloader/$DATALOADER_VER/java-dataloader-$DATALOADER_VER.jar" java-dataloader.jar
fetch "https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/$REACTIVE_STREAMS_VER/reactive-streams-$REACTIVE_STREAMS_VER.jar" reactive-streams.jar
fetch "https://repo1.maven.org/maven2/org/jspecify/jspecify/$JSPECIFY_VER/jspecify-$JSPECIFY_VER.jar" jspecify.jar

echo "All dependencies present in lib/."
