#!/bin/bash
# Downloads the real H2 database jar this demo needs, directly from Maven
# Central -- no Maven/Gradle install required. Jars are gitignored (*.jar);
# run this before compiling. H2 runs in-memory, so the demo needs no server
# and leaves nothing behind, but every transaction, lock, and UPDATE below is
# a real one against a real SQL engine.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/com/h2database/h2/2.3.232/h2-2.3.232.jar" h2.jar

echo "All dependencies present in lib/."
