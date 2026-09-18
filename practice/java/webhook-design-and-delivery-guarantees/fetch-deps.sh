#!/bin/bash
# Downloads only the JUnit 5 console launcher -- this pack's webhook
# sender/receiver use nothing but the JDK itself (com.sun.net.httpserver,
# java.net.http.HttpClient, javax.crypto) -- directly from Maven Central,
# no Maven/Gradle install required. Jars are gitignored; run before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.3/junit-platform-console-standalone-1.10.3.jar" junit-platform-console-standalone.jar

echo "All dependencies present in lib/."
