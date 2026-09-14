#!/bin/bash
# Downloads the real Hibernate ORM + Jakarta Persistence + H2 jars used by
# this pack's entity-lifecycle/N+1 demos, directly from Maven Central -- no
# Maven/Gradle install required. Jars are gitignored (*.jar); run this before
# compiling. Versions taken directly from hibernate-core's own published POM
# (org/hibernate/orm/hibernate-core/6.6.55.Final/hibernate-core-6.6.55.Final.pom).
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

HIBERNATE_VER=6.6.55.Final

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/org/hibernate/orm/hibernate-core/$HIBERNATE_VER/hibernate-core-$HIBERNATE_VER.jar" hibernate-core.jar
fetch "https://repo1.maven.org/maven2/jakarta/persistence/jakarta.persistence-api/3.1.0/jakarta.persistence-api-3.1.0.jar" jakarta.persistence-api.jar
fetch "https://repo1.maven.org/maven2/jakarta/transaction/jakarta.transaction-api/2.0.1/jakarta.transaction-api-2.0.1.jar" jakarta.transaction-api.jar
fetch "https://repo1.maven.org/maven2/org/jboss/logging/jboss-logging/3.5.0.Final/jboss-logging-3.5.0.Final.jar" jboss-logging.jar
fetch "https://repo1.maven.org/maven2/org/hibernate/common/hibernate-commons-annotations/7.0.3.Final/hibernate-commons-annotations-7.0.3.Final.jar" hibernate-commons-annotations.jar
fetch "https://repo1.maven.org/maven2/io/smallrye/jandex/3.2.0/jandex-3.2.0.jar" jandex.jar
fetch "https://repo1.maven.org/maven2/com/fasterxml/classmate/1.5.1/classmate-1.5.1.jar" classmate.jar
fetch "https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.17.8/byte-buddy-1.17.8.jar" byte-buddy.jar
fetch "https://repo1.maven.org/maven2/org/antlr/antlr4-runtime/4.13.2/antlr4-runtime-4.13.2.jar" antlr4-runtime.jar
# Not runtime-optional despite the POM's own "runtime" scope label: Hibernate's
# native Configuration bootstrap eagerly initializes an XML mapping binder even
# when no XML mapping files are used -- omitting these two produces a real
# NoClassDefFoundError (jakarta/xml/bind/JAXBException) at SessionFactory
# construction, caught empirically while building this pack's demos.
fetch "https://repo1.maven.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.0/jakarta.xml.bind-api-4.0.0.jar" jakarta.xml.bind-api.jar
fetch "https://repo1.maven.org/maven2/org/glassfish/jaxb/jaxb-runtime/4.0.2/jaxb-runtime-4.0.2.jar" jaxb-runtime.jar
fetch "https://repo1.maven.org/maven2/com/h2database/h2/2.3.232/h2-2.3.232.jar" h2.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.16/slf4j-api-2.0.16.jar" slf4j-api.jar
fetch "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.16/slf4j-simple-2.0.16.jar" slf4j-simple.jar

echo "All dependencies present in lib/."
