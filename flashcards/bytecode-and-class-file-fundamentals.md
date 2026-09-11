---
title: "Flashcards: Bytecode and Class File Fundamentals"
slug: bytecode-and-class-file-fundamentals
document_type: flashcard-deck
domain: 02-java/jvm-internals
topic_id: T-2406
canonical: ../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md
last_updated: 2026-09-11
---

# Flashcards: Bytecode and Class File Fundamentals

**Canonical chapter:** [`syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md`](../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md)

## Card: The magic number

**Prompt:**
What are the first four bytes of every real `.class` file, and why does that matter?

**Answer:**
`CA FE BA BE` — the JVM checks this before anything else; a class file failing this check is rejected immediately, before any version or content parsing.

**Why it matters:**
A fast, concrete fact that grounds "class file format" in something checkable, not abstract.

**Common trap:**
Confusing the magic number with the version bytes that immediately follow it.

**Related:**
[Bytecode and Class File Fundamentals](../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md)

## Card: Two independent safety gates

**Prompt:**
A class fails to load. Is `UnsupportedClassVersionError` and `VerifyError` the same underlying check?

**Answer:**
No — two real, independent gates. `UnsupportedClassVersionError` is the major-version check (this JVM can't run a class compiled for a newer Java version). `VerifyError` is the bytecode verifier's type-safety check (this chapter's demo reproduces it by patching a single opcode, `iadd` → `iaload`).

**Why it matters:**
A class can pass one check and fail the other — conflating them misdiagnoses a real production class-loading failure.

**Common trap:**
Assuming any class-loading failure is a version mismatch.

**Related:**
[Bytecode and Class File Fundamentals](../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md)

## Card: Stack-based, not register-based

**Prompt:**
Is JVM bytecode stack-based or register-based?

**Answer:**
Stack-based — instructions like `iconst_<n>`/`iload_<n>` push values onto an operand stack, and arithmetic/method-call instructions consume from and push back onto that same stack.

**Why it matters:**
A foundational fact for reading any real `javap -v` disassembly.

**Common trap:**
Confusing JVM bytecode with a register-based ISA (like x86 or ARM machine code).

**Related:**
[Bytecode and Class File Fundamentals](../syllabus/02-java/jvm-internals/bytecode-and-class-file-fundamentals.md)
