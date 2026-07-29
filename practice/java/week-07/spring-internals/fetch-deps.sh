#!/bin/bash
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib
SPRING_VER=6.1.14

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
fetch "https://repo1.maven.org/maven2/org/springframework/spring-tx/$SPRING_VER/spring-tx-$SPRING_VER.jar" spring-tx.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-jdbc/$SPRING_VER/spring-jdbc-$SPRING_VER.jar" spring-jdbc.jar
fetch "https://repo1.maven.org/maven2/org/springframework/spring-expression/$SPRING_VER/spring-expression-$SPRING_VER.jar" spring-expression.jar
fetch "https://repo1.maven.org/maven2/aopalliance/aopalliance/1.0/aopalliance-1.0.jar" aopalliance.jar
fetch "https://repo1.maven.org/maven2/jakarta/annotation/jakarta.annotation-api/2.1.1/jakarta.annotation-api-2.1.1.jar" jakarta.annotation-api.jar
fetch "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar" h2.jar

echo "All dependencies present in lib/."
