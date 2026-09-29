#!/bin/bash
# Downloads the real JUnit 5 console launcher and Mockito 5 jars this demo
# needs, directly from Maven Central -- no Maven/Gradle install required. Jars
# are gitignored (*.jar); run this before compiling. Versions reused from
# practice/java/week-11/testing/fetch-deps.sh (a real, working set).
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar" junit-platform-console-standalone.jar

fetch "https://repo1.maven.org/maven2/org/mockito/mockito-core/5.11.0/mockito-core-5.11.0.jar" mockito-core.jar
fetch "https://repo1.maven.org/maven2/org/mockito/mockito-junit-jupiter/5.11.0/mockito-junit-jupiter-5.11.0.jar" mockito-junit-jupiter.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.12/byte-buddy-1.14.12.jar" byte-buddy.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.12/byte-buddy-agent-1.14.12.jar" byte-buddy-agent.jar
fetch "https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar" objenesis.jar

echo "All dependencies present in lib/."
