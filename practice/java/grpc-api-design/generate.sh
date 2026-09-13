#!/bin/bash
# Regenerates the Java protobuf/gRPC stubs in generated/ from
# src/main/proto/bookstore.proto using the real protoc + grpc-java plugin
# fetched by fetch-deps.sh.
set -e
cd "$(dirname "$0")"
rm -rf generated
mkdir -p generated
./lib/protoc \
  --plugin=protoc-gen-grpc-java=lib/protoc-gen-grpc-java \
  --java_out=generated \
  --grpc-java_out=generated \
  --proto_path=src/main/proto \
  src/main/proto/bookstore.proto
echo "Generated:"
find generated -type f
