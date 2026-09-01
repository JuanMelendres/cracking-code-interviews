#!/bin/sh
# Downloads real JMH (Java Microbenchmark Harness) jars and transitive
# dependencies from Maven Central. No Maven/Gradle needed to run this pack --
# only javac/java plus these jars on the classpath.
set -eu
cd "$(dirname "$0")"
mkdir -p lib
BASE=https://repo1.maven.org/maven2

fetch() {
  path="$1"
  name="$2"
  if [ ! -f "lib/$name" ]; then
    echo "Fetching $name..."
    curl -sL "$BASE/$path" -o "lib/$name"
  fi
}

fetch org/openjdk/jmh/jmh-core/1.37/jmh-core-1.37.jar jmh-core.jar
fetch org/openjdk/jmh/jmh-generator-annprocess/1.37/jmh-generator-annprocess-1.37.jar jmh-generator-annprocess.jar
fetch net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar jopt-simple.jar
fetch org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar commons-math3.jar

echo "Done. Jars in lib/:"
ls -la lib/
