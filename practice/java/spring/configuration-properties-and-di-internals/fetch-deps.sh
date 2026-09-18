#!/bin/bash
# Downloads the real Spring Framework 6.2.19 + Spring Boot 3.5.16 jars these
# demos need (plain AnnotationConfigApplicationContext, no web/Tomcat) --
# from Maven Central, no Maven/Gradle install required. Jars are gitignored
# (*.jar); run this before compiling. Version numbers reused from
# practice/java/spring-mvc-fundamentals/fetch-deps.sh (a real, working set).
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

SPRING_VER=6.2.19
BOOT_VER=3.5.16

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/springframework/spring-core/$SPRING_VER/spring-core-$SPRING_VER.jar" spring-core.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-jcl/$SPRING_VER/spring-jcl-$SPRING_VER.jar" spring-jcl.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-context/$SPRING_VER/spring-context-$SPRING_VER.jar" spring-context.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-aop/$SPRING_VER/spring-aop-$SPRING_VER.jar" spring-aop.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-beans/$SPRING_VER/spring-beans-$SPRING_VER.jar" spring-beans.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-expression/$SPRING_VER/spring-expression-$SPRING_VER.jar" spring-expression.jar

fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/$BOOT_VER/spring-boot-$BOOT_VER.jar" spring-boot.jar

fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-observation/1.15.12/micrometer-observation-1.15.12.jar" micrometer-observation.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-commons/1.15.12/micrometer-commons-1.15.12.jar" micrometer-commons.jar

echo "All dependencies present in lib/."
