#!/bin/bash
# Downloads the real Spring Framework 6.2.19 + Spring Data JPA 3.5.13 +
# Hibernate ORM 6.6.55.Final + H2 jars this pack's repository-abstraction
# demos need, directly from Maven Central -- no Maven/Gradle install
# required. Jars are gitignored (*.jar); run this before compiling.
# Hibernate jar set matches practice/java/hibernate-jpa's own fetch-deps.sh
# (versions taken from hibernate-core's published POM). Spring Data JPA
# 3.5.13 / Spring Data Commons 3.5.13 verified on Maven Central as the
# latest 3.5.x patch compatible with Spring Framework 6.2.19 (same train
# Spring Boot 3.5.16 manages).
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

SPRING_VER=6.2.19
SDATA_VER=3.5.13
HIBERNATE_VER=6.6.55.Final

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

# Spring Framework
fetch "https://repo1.maven.org/maven2/org/springframework/spring-core/$SPRING_VER/spring-core-$SPRING_VER.jar" spring-core.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-jcl/$SPRING_VER/spring-jcl-$SPRING_VER.jar" spring-jcl.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-context/$SPRING_VER/spring-context-$SPRING_VER.jar" spring-context.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-aop/$SPRING_VER/spring-aop-$SPRING_VER.jar" spring-aop.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-beans/$SPRING_VER/spring-beans-$SPRING_VER.jar" spring-beans.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-expression/$SPRING_VER/spring-expression-$SPRING_VER.jar" spring-expression.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-tx/$SPRING_VER/spring-tx-$SPRING_VER.jar" spring-tx.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-orm/$SPRING_VER/spring-orm-$SPRING_VER.jar" spring-orm.jar
# spring-orm's DataSourceLookup class lives in spring-jdbc, not spring-orm
# itself -- a real NoClassDefFoundError at LocalContainerEntityManagerFactoryBean
# creation without it, caught empirically while building this pack's demo.
fetch "https://repo1.maven.org/maven2/org/springframework/spring-jdbc/$SPRING_VER/spring-jdbc-$SPRING_VER.jar" spring-jdbc.jar

# Spring Data JPA + its own Commons module
fetch "https://repo1.maven.org/maven2/org/springframework/data/spring-data-commons/$SDATA_VER/spring-data-commons-$SDATA_VER.jar" spring-data-commons.jar
fetch "https://repo1.maven.org/maven2/org/springframework/data/spring-data-jpa/$SDATA_VER/spring-data-jpa-$SDATA_VER.jar" spring-data-jpa.jar

# Hibernate ORM (JPA provider) + its own required runtime deps
fetch "https://repo1.maven.org/maven2/org/hibernate/orm/hibernate-core/$HIBERNATE_VER/hibernate-core-$HIBERNATE_VER.jar" hibernate-core.jar
fetch "https://repo1.maven.org/maven2/jakarta/persistence/jakarta.persistence-api/3.1.0/jakarta.persistence-api-3.1.0.jar" jakarta.persistence-api.jar
fetch "https://repo1.maven.org/maven2/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.jar" jakarta.transaction-api.jar
fetch "https://repo1.maven.org/maven2/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar" jakarta.annotation-api.jar
fetch "https://repo1.maven.org/maven2/org/jboss/logging/jboss-logging/3.5.0.Final/jboss-logging-3.5.0.Final.jar" jboss-logging.jar
fetch "https://repo1.maven.org/maven2/org/hibernate/common/hibernate-commons-annotations/7.0.3.Final/hibernate-commons-annotations-7.0.3.Final.jar" hibernate-commons-annotations.jar
fetch "https://repo1.maven.org/maven2/io/smallrye/jandex/3.2.0/jandex-3.2.0.jar" jandex.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/classmate/1.5.1/classmate-1.5.1.jar" classmate.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.17.8/byte-buddy-1.17.8.jar" byte-buddy.jar
fetch "https://repo1.maven.org/maven2/org/antlr/antlr4-runtime/4.13.2/antlr4-runtime-4.13.2.jar" antlr4-runtime.jar
fetch "https://repo1.maven.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.0/jakarta.xml.bind-api-4.0.0.jar" jakarta.xml.bind-api.jar
fetch "https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-runtime/4.0.2/jaxb-runtime-4.0.2.jar" jaxb-runtime.jar

# Database + logging
fetch "https://repo1.maven.org/maven2/com/h2database/h2/2.3.232/h2-2.3.232.jar" h2.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar" slf4j-simple.jar

echo "All dependencies present in lib/."
