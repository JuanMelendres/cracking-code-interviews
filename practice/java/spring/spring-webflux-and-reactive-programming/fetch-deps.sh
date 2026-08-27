#!/bin/bash
# Downloads the real Spring Framework, Spring WebFlux, Project Reactor, and
# JUnit 5 jars used by this pack's reactive-programming demos, directly from
# Maven Central -- no Maven/Gradle install required. Jars are gitignored
# (*.jar); run this before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

SPRING_VER=6.1.14
REACTOR_VER=3.6.10

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/springframework/spring-core/$SPRING_VER/spring-core-$SPRING_VER.jar" spring-core.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-jcl/$SPRING_VER/spring-jcl-$SPRING_VER.jar" spring-jcl.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-beans/$SPRING_VER/spring-beans-$SPRING_VER.jar" spring-beans.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-context/$SPRING_VER/spring-context-$SPRING_VER.jar" spring-context.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-aop/$SPRING_VER/spring-aop-$SPRING_VER.jar" spring-aop.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-expression/$SPRING_VER/spring-expression-$SPRING_VER.jar" spring-expression.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-web/$SPRING_VER/spring-web-$SPRING_VER.jar" spring-web.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-webflux/$SPRING_VER/spring-webflux-$SPRING_VER.jar" spring-webflux.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-test/$SPRING_VER/spring-test-$SPRING_VER.jar" spring-test.jar

fetch "https://repo1.maven.org/maven2/io/projectreactor/reactor-core/$REACTOR_VER/reactor-core-$REACTOR_VER.jar" reactor-core.jar
fetch "https://repo1.maven.org/maven2/io/projectreactor/reactor-test/$REACTOR_VER/reactor-test-$REACTOR_VER.jar" reactor-test.jar
fetch "https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/1.0.4/reactive-streams-1.0.4.jar" reactive-streams.jar

fetch "https://repo1.maven.org/maven2/aopalliance/aopalliance/1.0/aopalliance-1.0.jar" aopalliance.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar" slf4j-simple.jar

# Required transitively by WebTestClient's exchange-observation support --
# a real, honest discovery made while building this pack (see README).
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-observation/1.13.6/micrometer-observation-1.13.6.jar" micrometer-observation.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-commons/1.13.6/micrometer-commons-1.13.6.jar" micrometer-commons.jar

fetch "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.3/junit-platform-console-standalone-1.10.3.jar" junit-platform-console-standalone.jar

echo "All dependencies present in lib/."
