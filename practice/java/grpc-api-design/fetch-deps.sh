#!/bin/bash
# Downloads real protoc, the grpc-java codegen plugin, and grpc-java 1.68.1's
# runtime jars directly from Maven Central -- no Maven/Gradle install
# required. Jars and the protoc/plugin binaries are gitignored; run this
# before generating stubs or compiling.
#
# Binaries below are pinned to macOS arm64 (osx-aarch_64). On another OS/arch,
# swap the classifier (see https://repo1.maven.org/maven2/com/google/protobuf/protoc/
# and https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/ for options).
set -e
cd "$(dirname "$0")"
mkdir -p lib
cd lib

PROTOC_VER=4.28.2
GRPC_VER=1.68.1

fetch() {
  url="$1"; out="$2"
  if [ -f "$out" ]; then echo "SKIP (exists) $out"; return; fi
  curl -sfL "$url" -o "$out" && echo "OK   $out ($(wc -c < "$out") bytes)" || { echo "FAIL $url"; exit 1; }
}

fetch "https://repo1.maven.org/maven2/com/google/protobuf/protoc/$PROTOC_VER/protoc-$PROTOC_VER-osx-aarch_64.exe" protoc
fetch "https://repo1.maven.org/maven2/io/grpc/protoc-gen-grpc-java/$GRPC_VER/protoc-gen-grpc-java-$GRPC_VER-osx-aarch_64.exe" protoc-gen-grpc-java
chmod +x protoc protoc-gen-grpc-java

fetch "https://repo1.maven.org/maven2/io/grpc/grpc-api/$GRPC_VER/grpc-api-$GRPC_VER.jar" grpc-api.jar
fetch "https://repo1.maven.org/maven2/io/grpc/grpc-context/$GRPC_VER/grpc-context-$GRPC_VER.jar" grpc-context.jar
fetch "https://repo1.maven.org/maven2/io/grpc/grpc-core/$GRPC_VER/grpc-core-$GRPC_VER.jar" grpc-core.jar
fetch "https://repo1.maven.org/maven2/io/grpc/grpc-protobuf/$GRPC_VER/grpc-protobuf-$GRPC_VER.jar" grpc-protobuf.jar
fetch "https://repo1.maven.org/maven2/io/grpc/grpc-protobuf-lite/$GRPC_VER/grpc-protobuf-lite-$GRPC_VER.jar" grpc-protobuf-lite.jar
fetch "https://repo1.maven.org/maven2/io/grpc/grpc-stub/$GRPC_VER/grpc-stub-$GRPC_VER.jar" grpc-stub.jar
fetch "https://repo1.maven.org/maven2/io/grpc/grpc-inprocess/$GRPC_VER/grpc-inprocess-$GRPC_VER.jar" grpc-inprocess.jar

fetch "https://repo1.maven.org/maven2/com/google/protobuf/protobuf-java/$PROTOC_VER/protobuf-java-$PROTOC_VER.jar" protobuf-java.jar
fetch "https://repo1.maven.org/maven2/com/google/protobuf/protobuf-javalite/$PROTOC_VER/protobuf-javalite-$PROTOC_VER.jar" protobuf-javalite.jar

fetch "https://repo1.maven.org/maven2/com/google/guava/guava/33.2.1-android/guava-33.2.1-android.jar" guava.jar
fetch "https://repo1.maven.org/maven2/com/google/guava/failureaccess/1.0.2/failureaccess-1.0.2.jar" failureaccess.jar
fetch "https://repo1.maven.org/maven2/com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar" jsr305.jar
fetch "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar" gson.jar
fetch "https://repo1.maven.org/maven2/io/perfmark/perfmark-api/0.27.0/perfmark-api-0.27.0.jar" perfmark-api.jar
fetch "https://repo1.maven.org/maven2/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar" javax.annotation-api.jar

echo "All dependencies present in lib/."
