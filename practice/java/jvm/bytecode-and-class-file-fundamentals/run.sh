#!/usr/bin/env bash
# Real, deterministic bytecode/class-file demo. Pure JDK (javac/java/javap)
# plus a tiny pure-Java byte-patcher for the two failure scenarios. Requires
# `xxd` (standard on macOS/most Linux) for the raw hex dump. Tested on
# OpenJDK 21.0.12.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out

echo "### 1. Compile and run normally"
javac -d out src/SumLoop.java src/RunSumLoop.java src/ClassBytePatcher.java
java -cp out RunSumLoop

echo
echo "### 2. Raw class file bytes: magic number + version (first 16 bytes)"
xxd -l 16 out/SumLoop.class

echo
echo "### 3. javap -v — constant pool, bytecode instructions, StackMapTable"
javap -c -p -v out/SumLoop.class

echo
echo "### 4. Patch the major version upward (simulate a too-new class file) and rerun"
java -cp out ClassBytePatcher version out/SumLoop.class 99
echo "--- rerunning; expect a real UnsupportedClassVersionError ---"
java -cp out RunSumLoop 2>&1 || true

echo
echo "### 5. Restore, then patch one opcode (iadd 0x60 -> iaload 0x2e) and rerun"
javac -d out src/SumLoop.java
java -cp out ClassBytePatcher opcode out/SumLoop.class 0x60 0x2e
echo "--- rerunning; expect a real VerifyError from the bytecode verifier ---"
java -cp out RunSumLoop 2>&1 || true

rm -rf out
