#!/bin/bash
# Downloads the real MapStruct (core + annotation processor), Spring
# Framework (just for the real @Component annotation MapStruct's
# generated code carries), and JUnit 5 jars used by this pack's
# DTO/Entity/Mapper demo, directly from Maven Central -- no Maven/Gradle
# install required. Jars are gitignored (*.jar); run this before
# compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

MAPSTRUCT_VER=1.6.3
SPRING_VER=6.1.14

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/mapstruct/mapstruct/$MAPSTRUCT_VER/mapstruct-$MAPSTRUCT_VER.jar" mapstruct.jar
fetch "https://repo1.maven.org/maven2/org/mapstruct/mapstruct-processor/$MAPSTRUCT_VER/mapstruct-processor-$MAPSTRUCT_VER.jar" mapstruct-processor.jar

fetch "https://repo1.maven.org/maven2/org/springframework/spring-core/$SPRING_VER/spring-core-$SPRING_VER.jar" spring-core.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-jcl/$SPRING_VER/spring-jcl-$SPRING_VER.jar" spring-jcl.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-beans/$SPRING_VER/spring-beans-$SPRING_VER.jar" spring-beans.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-context/$SPRING_VER/spring-context-$SPRING_VER.jar" spring-context.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-aop/$SPRING_VER/spring-aop-$SPRING_VER.jar" spring-aop.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-expression/$SPRING_VER/spring-expression-$SPRING_VER.jar" spring-expression.jar

fetch "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.3/junit-platform-console-standalone-1.10.3.jar" junit-platform-console-standalone.jar

echo "All dependencies present in lib/."
