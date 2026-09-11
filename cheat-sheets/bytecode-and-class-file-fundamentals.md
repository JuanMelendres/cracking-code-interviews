---
title: "Cheat Sheet: Bytecode and Class File Fundamentals"
slug: bytecode-and-class-file-fundamentals
document_type: cheat-sheet
domain: 02-java/jvm-internals
topic_id: T-2406
canonical: ../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md
last_updated: 2026-09-11
---

# Bytecode and Class File Fundamentals

**Canonical chapter:** [`syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md`](../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md)

## Core Mental Model

A `.class` file is a portable, precisely specified binary format — magic number, version, a constant pool of symbolic references, stack-based bytecode — letting any conforming JVM parse, verify, and execute code compiled once, anywhere. "Write once, run anywhere" is a checkable claim about this file format, not a slogan.

## Essential Definitions

- **Magic number** — the fixed first 4 bytes, `0xCAFEBABE`, confirming a file is a real class file.
- **Constant pool** — every symbolic reference a class uses, resolved into real, checked references at link time.
- **Bytecode verifier** — a real, `StackMapTable`-based type-safety pass over every method before it can execute.

## Decision Table

| Need | Fact/Tool |
|---|---|
| Confirm a file is a real class file | First 4 bytes: `CA FE BA BE` |
| Identify the Java version a class was compiled for | Major version bytes (Java 21 = 65, Java 17 = 61, Java 11 = 55) |
| See a class's real constant pool and bytecode | `javap -c -p -v <ClassFile>.class` |
| Real method-call instructions | `invokevirtual`, `invokestatic`, `invokespecial`, `invokeinterface`, `invokedynamic` |
| Diagnose a version mismatch at deployment | Read `UnsupportedClassVersionError`'s own two version numbers directly |
| Diagnose an unexpected `VerifyError` | `javap -v` on the exact class/method the error names |

## Common Pitfalls

- Assuming bytecode is register-based — it's stack-based.
- Assuming a version-mismatch error and a corrupted-bytecode error are the same failure — they're two independent JVM safety gates (`UnsupportedClassVersionError` vs. `VerifyError`).
- Skipping `javap -v` when debugging a class-loading failure — it directly shows the version bytes and instruction stream causing it.

## Interview Answer Skeleton

**30-sec:** A `.class` file is a fixed, versioned binary format: magic number, version, constant pool, then fields/methods/attributes. Bytecode is stack-based; a real verifier checks type-safety before any method executes.

**2-min:** Add: two real, independent JVM safety gates — the major-version check (`UnsupportedClassVersionError`) and bytecode verification (`VerifyError`) — demonstrated by patching exactly one byte in each case.

**Staff-level framing:** Version-check and verification are independent, not redundant — a class can pass one and fail the other, which matters when diagnosing a real deployment failure.

## Related

- syllabus/02-java/language-core/java-platform-module-system.md
- syllabus/02-java/concurrency/methodhandle-and-invoke.md
