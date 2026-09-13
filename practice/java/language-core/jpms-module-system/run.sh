#!/usr/bin/env bash
# Real, deterministic JPMS demo. Pure JDK, no dependencies.
# Reproduces four real module-system behaviors and prints each one's real
# output in order. Requires Java 9+ (tested on OpenJDK 21).
set -euo pipefail
cd "$(dirname "$0")"

echo "### Scenario A.1 — reflective access into a package that is EXPORTED but NOT OPENED"
echo "### (temporarily strips the 'opens' line to reproduce the original, unopened module)"
cp src-a/com.example.modA/module-info.java /tmp/jpms-modA-module-info.bak
grep -v 'opens com.example.modA' src-a/com.example.modA/module-info.java > /tmp/jpms-modA-no-opens.java
cp /tmp/jpms-modA-no-opens.java src-a/com.example.modA/module-info.java
rm -rf out-a
javac -d out-a --module-source-path src-a $(find src-a -name '*.java')
echo "--- running ReflectiveProbe against the export-only module ---"
java --module-path out-a -m com.example.modB/com.example.modB.ReflectiveProbe && true || true
cp /tmp/jpms-modA-module-info.bak src-a/com.example.modA/module-info.java

echo
echo "### Scenario A.2 — same reflective call, now that modA declares 'opens com.example.modA to com.example.modB'"
rm -rf out-a
javac -d out-a --module-source-path src-a $(find src-a -name '*.java')
echo "--- running ReflectiveProbe again, module descriptor now includes the qualified opens ---"
java --module-path out-a -m com.example.modB/com.example.modB.ReflectiveProbe

echo
echo "### Scenario B.1 — ServiceLoader resolves a provider module the consumer never 'requires'"
echo "### (com.example.svc.app has no 'requires com.example.svc.provider' in its module-info.java)"
rm -rf out-b
javac -d out-b --module-source-path src-b $(find src-b -name '*.java')
echo "--- running: java --module-path out-b -m com.example.svc.app/com.example.svc.app.Main ---"
java --module-path out-b -m com.example.svc.app/com.example.svc.app.Main

echo
echo "### Scenario B.2 — a DIRECT import of that same provider's internal, non-exported package fails at compile time"
cp src-b/com.example.svc.app/com/example/svc/app/Main.java /tmp/jpms-Main.java.bak
cat > src-b/com.example.svc.app/com/example/svc/app/Main.java <<'EOF'
package com.example.svc.app;

import com.example.svc.api.Greeter;
import com.example.svc.provider.internal.EnglishGreeter;

public class Main {
    public static void main(String[] args) {
        Greeter direct = new EnglishGreeter();
        System.out.println(direct.greet("Direct"));
    }
}
EOF
rm -rf out-b-illegal
echo "--- compiling the illegal direct import (real javac error expected) ---"
javac -d out-b-illegal --module-source-path src-b $(find src-b -name '*.java') 2>&1 || true
cp /tmp/jpms-Main.java.bak src-b/com.example.svc.app/com/example/svc/app/Main.java
rm -rf out-a out-b out-b-illegal
