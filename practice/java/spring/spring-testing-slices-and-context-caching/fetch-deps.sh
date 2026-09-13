#!/bin/bash
# Downloads the real Spring Framework, Spring Boot, JUnit 5, and Mockito jars
# used by this pack's slice-testing and context-caching demos, directly from
# Maven Central -- no Maven/Gradle install required. Jars are gitignored
# (*.jar); run this before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

SPRING_VER=6.1.14
BOOT_VER=3.3.5

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
fetch "https://repo1.maven.org/maven2/org/springframework/spring-webmvc/$SPRING_VER/spring-webmvc-$SPRING_VER.jar" spring-webmvc.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-test/$SPRING_VER/spring-test-$SPRING_VER.jar" spring-test.jar

fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/$BOOT_VER/spring-boot-$BOOT_VER.jar" spring-boot.jar
fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-autoconfigure/$BOOT_VER/spring-boot-autoconfigure-$BOOT_VER.jar" spring-boot-autoconfigure.jar
fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-test/$BOOT_VER/spring-boot-test-$BOOT_VER.jar" spring-boot-test.jar
fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-test-autoconfigure/$BOOT_VER/spring-boot-test-autoconfigure-$BOOT_VER.jar" spring-boot-test-autoconfigure.jar

fetch "https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar" jakarta.servlet-api.jar
fetch "https://repo1.maven.org/maven2/aopalliance/aopalliance/1.0/aopalliance-1.0.jar" aopalliance.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar" slf4j-simple.jar

# Required transitively by WebMvcAutoConfiguration's request-observation support --
# a real, honest discovery made while building this pack (see README).
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-observation/1.13.6/micrometer-observation-1.13.6.jar" micrometer-observation.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-commons/1.13.6/micrometer-commons-1.13.6.jar" micrometer-commons.jar

# Mockito, for @MockBean
fetch "https://repo1.maven.org/maven2/org/mockito/mockito-core/5.12.0/mockito-core-5.12.0.jar" mockito-core.jar
fetch "https://repo1.maven.org/maven2/org/mockito/mockito-junit-jupiter/5.12.0/mockito-junit-jupiter-5.12.0.jar" mockito-junit-jupiter.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.17/byte-buddy-1.14.17.jar" byte-buddy.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.17/byte-buddy-agent-1.14.17.jar" byte-buddy-agent.jar
fetch "https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar" objenesis.jar

# JUnit 5, as a single shaded console-launcher jar -- runs tests with no
# Maven/Gradle build tool needed.
fetch "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.3/junit-platform-console-standalone-1.10.3.jar" junit-platform-console-standalone.jar

echo "All dependencies present in lib/."
