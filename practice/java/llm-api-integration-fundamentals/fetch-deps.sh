#!/bin/bash
# Downloads the real Jackson jars this pack's demo needs, directly from
# Maven Central -- no Maven/Gradle install required. Jars are gitignored
# (*.jar); run this before compiling. The HTTP server and client themselves
# use only JDK built-ins (com.sun.net.httpserver.HttpServer,
# java.net.http.HttpClient) -- Jackson is only for JSON.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

JACKSON_VER=2.21.4

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/$JACKSON_VER/jackson-databind-$JACKSON_VER.jar" jackson-databind.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/$JACKSON_VER/jackson-core-$JACKSON_VER.jar" jackson-core.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.21/jackson-annotations-2.21.jar" jackson-annotations.jar

echo "All dependencies present in lib/."
