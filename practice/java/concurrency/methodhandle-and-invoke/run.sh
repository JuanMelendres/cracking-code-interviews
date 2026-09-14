#!/usr/bin/env bash
# Real, deterministic MethodHandle / java.lang.invoke demo. Pure JDK, no
# dependencies. Requires javac/java/javap 9+ (tested on OpenJDK 21).
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out

echo "### 1. findStatic / findConstructor / findVirtual / bindTo, invoke() vs invokeExact()"
javac -d out src/BasicsDemo.java
java -cp out BasicsDemo

echo
echo "### 2. Combinators — filterReturnValue and dropArguments"
javac -d out src/CombinatorsDemo.java
java -cp out CombinatorsDemo

echo
echo "### 3. A lambda compiles to invokedynamic + LambdaMetafactory, not a synthetic class"
javac -d out src/LambdaBytecode.java
java -cp out LambdaBytecode
echo "--- javap -v disassembly: the invokedynamic instruction and its bootstrap method ---"
javap -c -p -v out/LambdaBytecode.class | grep -A2 "0: invokedynamic"
echo "..."
javap -c -p -v out/LambdaBytecode.class | grep -A3 "^BootstrapMethods:"
echo "--- class files produced for the lambda version (expect: no synthetic \$1 class) ---"
ls out | grep LambdaBytecode

echo
echo "### 4. Contrast — the equivalent anonymous inner class DOES produce a synthetic class file"
javac -d out src/AnonClassBytecode.java
java -cp out AnonClassBytecode
echo "--- class files produced for the anonymous-inner-class version ---"
ls out | grep AnonClassBytecode

rm -rf out
