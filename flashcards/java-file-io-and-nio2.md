---
title: "Flashcards: Java File I/O and NIO.2"
slug: java-file-io-and-nio2
document_type: flashcard-deck
domain: 02-java/language-core
topic_id: T-2429
canonical: ../syllabus/02-java/language-core/java-file-io-and-nio2.md
last_updated: 2026-09-26
---

# Flashcards: Java File I/O and NIO.2

**Canonical chapter:** [`syllabus/02-java/language-core/java-file-io-and-nio2.md`](../syllabus/02-java/language-core/java-file-io-and-nio2.md)

## Card: Why buffering matters

**Prompt:**
What does wrapping a `FileReader` in a `BufferedReader` actually save, mechanically?

**Answer:**
Real system-call overhead — a `BufferedReader` reads a large chunk into memory once and serves many subsequent calls from it. Measured directly: 4.6x faster for character-by-character reads of a ~2.3MB file.

**Why it matters:**
A real, common, easily-fixed performance issue in code doing unbuffered per-character or per-line I/O.

**Common trap:**
Describing it as vaguely "more efficient" without knowing the syscall mechanism.

**Related:**
[Java File I/O and NIO.2](../syllabus/02-java/language-core/java-file-io-and-nio2.md)

## Card: Charset mismatch corruption

**Prompt:**
If you write text as UTF-8 and read it back as `ISO-8859-1`, what happens?

**Answer:**
Silent corruption (mojibake) — no exception thrown. Verified directly: `"café résumé naïve"` read back as `"cafÃ© rÃ©sumÃ© naÃ¯ve"`.

**Why it matters:**
A real, silent bug class — always pass an explicit `Charset`, never rely on the platform default.

**Common trap:**
Assuming a charset mismatch would throw an exception rather than silently returning wrong data.

**Related:**
[Java File I/O and NIO.2](../syllabus/02-java/language-core/java-file-io-and-nio2.md)

## Card: Suppressed exceptions

**Prompt:**
In `try`-with-resources, if the body throws and the resource's `close()` also throws, what happens to the `close()` exception?

**Answer:**
It's attached to the primary (body) exception's `getSuppressed()` array, not lost and not replacing the original — unlike a manual `finally` block, where a `close()` failure silently replaces the body's exception.

**Why it matters:**
A real, structural reason to prefer `try`-with-resources over a manual `finally` block for cleanup.

**Common trap:**
Assuming `try`-with-resources and a manual `finally` block behave identically on a double failure.

**Related:**
[Java File I/O and NIO.2](../syllabus/02-java/language-core/java-file-io-and-nio2.md)
