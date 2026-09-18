#!/bin/bash
# Downloads the real Spring Boot 3.5.16 + Spring Framework 6.2.19 + embedded
# Tomcat jars (same proven set as practice/java/rest-api-fundamentals's own
# fetch-deps.sh) plus springdoc-openapi 2.9.1 and its real transitive
# dependency chain (swagger-core-jakarta 2.2.55 and everything it needs),
# resolved by hand from each artifact's own POM on Maven Central -- no
# Maven/Gradle install required. Also downloads the real, self-contained
# openapi-generator-cli 7.25.0 shaded jar into tools/ for real client-code
# generation from the spec this pack produces. Jars are gitignored; run
# this before compiling.
set -e
cd "$(dirname "$0")"
mkdir -p lib tools
cd lib

SPRING_VER=6.2.19
BOOT_VER=3.5.16
TOMCAT_VER=10.1.55
JACKSON_VER=2.21.4
SPRINGDOC_VER=2.9.1
SWAGGER_VER=2.2.55

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

# --- Spring / Spring Boot / embedded Tomcat (same set as rest-api-fundamentals) ---

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
fetch "https://repo1.maven.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-yaml/$JACKSON_VER/jackson-dataformat-yaml-$JACKSON_VER.jar" jackson-dataformat-yaml.jar

fetch "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-core/$TOMCAT_VER/tomcat-embed-core-$TOMCAT_VER.jar" tomcat-embed-core.jar
fetch "https://repo1.maven.org/maven2/org/apache/tomcat/tomcat-annotations-api/$TOMCAT_VER/tomcat-annotations-api-$TOMCAT_VER.jar" tomcat-annotations-api.jar
fetch "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-el/$TOMCAT_VER/tomcat-embed-el-$TOMCAT_VER.jar" tomcat-embed-el.jar
fetch "https://repo1.maven.org/maven2/org/apache/tomcat/embed/tomcat-embed-websocket/$TOMCAT_VER/tomcat-embed-websocket-$TOMCAT_VER.jar" tomcat-embed-websocket.jar

# --- springdoc-openapi 2.9.1: generates the real OpenAPI 3 spec from this pack's real controller ---

fetch "https://repo1.maven.org/maven2/org/springdoc/springdoc-openapi-starter-webmvc-api/$SPRINGDOC_VER/springdoc-openapi-starter-webmvc-api-$SPRINGDOC_VER.jar" springdoc-openapi-starter-webmvc-api.jar
fetch "https://repo1.maven.org/maven2/org/springdoc/springdoc-openapi-starter-common/$SPRINGDOC_VER/springdoc-openapi-starter-common-$SPRINGDOC_VER.jar" springdoc-openapi-starter-common.jar

# --- swagger-core-jakarta 2.2.55 and its real transitive dependency chain (resolved by hand from its POM) ---

fetch "https://repo1.maven.org/maven2/io/swagger/core/v3/swagger-core-jakarta/$SWAGGER_VER/swagger-core-jakarta-$SWAGGER_VER.jar" swagger-core-jakarta.jar
fetch "https://repo1.maven.org/maven2/io/swagger/core/v3/swagger-annotations-jakarta/$SWAGGER_VER/swagger-annotations-jakarta-$SWAGGER_VER.jar" swagger-annotations-jakarta.jar
fetch "https://repo1.maven.org/maven2/io/swagger/core/v3/swagger-models-jakarta/$SWAGGER_VER/swagger-models-jakarta-$SWAGGER_VER.jar" swagger-models-jakarta.jar
fetch "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.20.0/commons-lang3-3.20.0.jar" commons-lang3.jar
fetch "https://repo1.maven.org/maven2/commons-io/commons-io/2.18.0/commons-io-2.18.0.jar" commons-io.jar
fetch "https://repo1.maven.org/maven2/org/yaml/snakeyaml/2.6/snakeyaml-2.6.jar" snakeyaml.jar
fetch "https://repo1.maven.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/3.0.1/jakarta.xml.bind-api-3.0.1.jar" jakarta.xml.bind-api.jar
fetch "https://repo1.maven.org/maven2/com/sun/activation/jakarta.activation/2.0.1/jakarta.activation-2.0.1.jar" jakarta.activation.jar
fetch "https://repo1.maven.org/maven2/jakarta/validation/jakarta.validation-api/3.0.2/jakarta.validation-api-3.0.2.jar" jakarta.validation-api.jar

# --- Hibernate Validator: springdoc's SchemaUtils has a real, direct compile-time
# reference to org.hibernate.validator.constraints.Range (not just an optional
# runtime check) -- omitting it throws a real NoClassDefFoundError from
# /v3/api-docs, discovered by actually running this demo without it first. ---

fetch "https://repo1.maven.org/maven2/org/hibernate/validator/hibernate-validator/8.0.5.Final/hibernate-validator-8.0.5.Final.jar" hibernate-validator.jar
fetch "https://repo1.maven.org/maven2/org/jboss/logging/jboss-logging/3.6.3.Final/jboss-logging-3.6.3.Final.jar" jboss-logging.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/classmate/1.7.3/classmate-1.7.3.jar" classmate.jar
fetch "https://repo1.maven.org/maven2/org/glassfish/expressly/expressly/5.0.0/expressly-5.0.0.jar" expressly.jar
fetch "https://repo1.maven.org/maven2/jakarta/el/jakarta.el-api/5.0.0/jakarta.el-api-5.0.0.jar" jakarta.el-api.jar

echo "All dependencies present in lib/."

# --- Real, self-contained openapi-generator-cli, for real client-code generation ---

cd ../tools
GEN_VER=7.25.0
if [ -f openapi-generator-cli.jar ]; then
  echo "SKIP (exists) openapi-generator-cli.jar"
else
  curl -sfL "https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/$GEN_VER/openapi-generator-cli-$GEN_VER.jar" -o openapi-generator-cli.jar \
    && echo "OK   openapi-generator-cli.jar ($(wc -c < openapi-generator-cli.jar) bytes)" \
    || { echo "FAIL openapi-generator-cli"; exit 1; }
fi
