#!/bin/bash
# Downloads the real Flyway 10.20.1 Java API jars (community edition) +
# the PostgreSQL JDBC driver, directly from Maven Central -- no Maven/Gradle
# install required. Jars are gitignored (*.jar); run this before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

FLYWAY_VER=10.20.1
PG_DRIVER_VER=42.7.4
JACKSON_VER=2.18.2

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/flywaydb/flyway-core/$FLYWAY_VER/flyway-core-$FLYWAY_VER.jar" flyway-core.jar
fetch "https://repo1.maven.org/maven2/org/flywaydb/flyway-database-postgresql/$FLYWAY_VER/flyway-database-postgresql-$FLYWAY_VER.jar" flyway-database-postgresql.jar
fetch "https://repo1.maven.org/maven2/org/postgresql/postgresql/$PG_DRIVER_VER/postgresql-$PG_DRIVER_VER.jar" postgresql.jar

fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/$JACKSON_VER/jackson-databind-$JACKSON_VER.jar" jackson-databind.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/$JACKSON_VER/jackson-core-$JACKSON_VER.jar" jackson-core.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/$JACKSON_VER/jackson-annotations-$JACKSON_VER.jar" jackson-annotations.jar

echo "All dependencies present in lib/."
