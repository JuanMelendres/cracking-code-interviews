#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

# JUnit 5 console launcher -- a single self-contained jar, runs JUnit5
# tests with no Maven/Gradle needed.
fetch "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar" junit-platform-console-standalone.jar

# Mockito and its (small, stable) own dependency set.
fetch "https://repo1.maven.org/maven2/org/mockito/mockito-core/5.11.0/mockito-core-5.11.0.jar" mockito-core.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.12/byte-buddy-1.14.12.jar" byte-buddy.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.12/byte-buddy-agent-1.14.12.jar" byte-buddy-agent.jar
fetch "https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar" objenesis.jar

# JDBC driver for the "real dependency" integration test.
fetch "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar" postgresql.jar

echo "All dependencies present in lib/."
