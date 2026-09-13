#!/bin/bash
# Downloads the real Spring Framework, Spring Boot, Spring Boot Actuator,
# Micrometer, and JUnit 5 jars used by this pack's health/observability
# demos, directly from Maven Central -- no Maven/Gradle install required.
# Jars are gitignored (*.jar); run this before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

SPRING_VER=6.1.14
BOOT_VER=3.3.5
JACKSON_VER=2.17.2

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
fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-actuator/$BOOT_VER/spring-boot-actuator-$BOOT_VER.jar" spring-boot-actuator.jar
fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-actuator-autoconfigure/$BOOT_VER/spring-boot-actuator-autoconfigure-$BOOT_VER.jar" spring-boot-actuator-autoconfigure.jar

fetch "https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar" jakarta.servlet-api.jar
fetch "https://repo1.maven.org/maven2/aopalliance/aopalliance/1.0/aopalliance-1.0.jar" aopalliance.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar" slf4j-simple.jar

fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-observation/1.13.6/micrometer-observation-1.13.6.jar" micrometer-observation.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-commons/1.13.6/micrometer-commons-1.13.6.jar" micrometer-commons.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-core/1.13.6/micrometer-core-1.13.6.jar" micrometer-core.jar

fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/$JACKSON_VER/jackson-databind-$JACKSON_VER.jar" jackson-databind.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/$JACKSON_VER/jackson-core-$JACKSON_VER.jar" jackson-core.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/$JACKSON_VER/jackson-annotations-$JACKSON_VER.jar" jackson-annotations.jar

fetch "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.3/junit-platform-console-standalone-1.10.3.jar" junit-platform-console-standalone.jar

echo "All dependencies present in lib/."
