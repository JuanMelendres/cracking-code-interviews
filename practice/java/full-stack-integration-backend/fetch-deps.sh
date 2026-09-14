#!/bin/bash
# Downloads the real Spring Boot 3.5.16 + Spring Framework 6.2.19 + embedded
# Tomcat jars this pack's backend needs, directly from Maven Central -- no
# Maven/Gradle install required. Jars are gitignored (*.jar); run this before
# compiling. Same dependency set as
# practice/java/spring-vs-spring-boot/embedded-server-and-autoconfig's own
# fetch-deps.sh (F-214 reuses that chapter's real, working jar list).
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

SPRING_VER=6.2.19
BOOT_VER=3.5.16
TOMCAT_VER=10.1.55
JACKSON_VER=2.21.4

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
fetch "https://repo1.maven.org/maven2/org/springframework/spring-web/$SPRING_VER/spring-web-$SPRING_VER.jar" spring-web.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-webmvc/$SPRING_VER/spring-webmvc-$SPRING_VER.jar" spring-webmvc.jar

fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-observation/1.15.12/micrometer-observation-1.15.12.jar" micrometer-observation.jar
fetch "https://repo1.maven.org/maven2/io/micrometer/micrometer-commons/1.15.12/micrometer-commons-1.15.12.jar" micrometer-commons.jar

fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/$BOOT_VER/spring-boot-$BOOT_VER.jar" spring-boot.jar
fetch "https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-autoconfigure/$BOOT_VER/spring-boot-autoconfigure-$BOOT_VER.jar" spring-boot-autoconfigure.jar
fetch "https://repo1.maven.org/maven2/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar" jakarta.annotation-api.jar
fetch "https://repo1.maven.org/maven2/org/yaml/snakeyaml/2.4/snakeyaml-2.4.jar" snakeyaml.jar

fetch "https://repo1.maven.org/maven2/ch/qos/logback/logback-classic/1.5.34/logback-classic-1.5.34.jar" logback-classic.jar
fetch "https://repo1.maven.org/maven2/ch/qos/logback/logback-core/1.5.34/logback-core-1.5.34.jar" logback-core.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.17/slf4j-api-2.0.17.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-to-slf4j/2.24.3/log4j-to-slf4j-2.24.3.jar" log4j-to-slf4j.jar
fetch "https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.24.3/log4j-api-2.24.3.jar" log4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/jul-to-slf4j/2.0.17/jul-to-slf4j-2.0.17.jar" jul-to-slf4j.jar

fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/$JACKSON_VER/jackson-databind-$JACKSON_VER.jar" jackson-databind.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/$JACKSON_VER/jackson-core-$JACKSON_VER.jar" jackson-core.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.21/jackson-annotations-2.21.jar" jackson-annotations.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/$JACKSON_VER/jackson-datatype-jdk8-$JACKSON_VER.jar" jackson-datatype-jdk8.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/$JACKSON_VER/jackson-datatype-jsr310-$JACKSON_VER.jar" jackson-datatype-jsr310.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/module/jackson-module-parameter-names/$JACKSON_VER/jackson-module-parameter-names-$JACKSON_VER.jar" jackson-module-parameter-names.jar

fetch "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/$TOMCAT_VER/tomcat-embed-core-$TOMCAT_VER.jar" tomcat-embed-core.jar
fetch "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-annotations-api/$TOMCAT_VER/tomcat-annotations-api-$TOMCAT_VER.jar" tomcat-annotations-api.jar
fetch "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-el/$TOMCAT_VER/tomcat-embed-el-$TOMCAT_VER.jar" tomcat-embed-el.jar
fetch "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-websocket/$TOMCAT_VER/tomcat-embed-websocket-$TOMCAT_VER.jar" tomcat-embed-websocket.jar

echo "All dependencies present in lib/."
