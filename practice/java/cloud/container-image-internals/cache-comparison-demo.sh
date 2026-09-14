#!/bin/sh
# Real, reproducible comparison of Docker layer-cache behavior between a
# non-split multi-stage build and a dependency/source-split ("layered")
# multi-stage build, when only application source changes between builds.
#
# Run from this directory: sh cache-comparison-demo.sh
set -eu

APP_JAVA=app/src/main/java/interviewprep/container/HelloContainer.java
MARK() { sed -i.bak "s/demoBuildMarker\", \"[^\"]*\"/demoBuildMarker\", \"$1\"/" "$APP_JAVA"; rm -f "$APP_JAVA.bak"; }

echo "### 1. Cold build, Dockerfile.multistage (marker=v1) ###"
MARK v1
docker builder prune -f --filter type=exec.cachemount >/dev/null 2>&1 || true
docker builder prune -af >/dev/null
time docker build -f Dockerfile.multistage -t cii-multistage:cachecmp .

echo
echo "### 2. Source-only change, rebuild Dockerfile.multistage (marker=v2) ###"
MARK v2
time docker build -f Dockerfile.multistage -t cii-multistage:cachecmp . 2>&1 | grep -E "CACHED|package$|package\b" || true

echo
echo "### 3. Cold build, Dockerfile.layered (marker=v1) ###"
MARK v1
docker builder prune -af >/dev/null
time docker build -f Dockerfile.layered -t cii-layered:cachecmp .

echo
echo "### 4. Source-only change, rebuild Dockerfile.layered (marker=v2) ###"
MARK v2
time docker build -f Dockerfile.layered -t cii-layered:cachecmp . 2>&1 | grep -E "CACHED|go-offline|package$|package\b" || true
