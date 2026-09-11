---
title: "Flashcards: Java Platform Module System (JPMS)"
slug: java-platform-module-system
document_type: flashcard-deck
domain: 02-java/language-core
topic_id: T-116
canonical: ../syllabus/02-java/language-core/java-platform-module-system.md
last_updated: 2026-09-11
---

# Flashcards: Java Platform Module System (JPMS)

**Canonical chapter:** [`syllabus/02-java/language-core/java-platform-module-system.md`](../syllabus/02-java/language-core/java-platform-module-system.md)

## Card: exports vs. opens

**Prompt:**
If a module `exports` a package but doesn't `opens` it, can a reflective caller call `setAccessible(true)` on one of its classes?

**Answer:**
No — a real `InaccessibleObjectException` is thrown. `exports` (compile-time/runtime visibility) and `opens` (reflective access) are independent grants; neither implies the other.

**Why it matters:**
A common, real source of confusion and production breakage after migrating to JPMS or upgrading a JDK version.

**Common trap:**
Assuming `exports` is "the strong version" of `opens`, or vice versa.

**Related:**
[Java Platform Module System (JPMS)](../syllabus/02-java/language-core/java-platform-module-system.md)

## Card: ServiceLoader's narrow exception

**Prompt:**
Does `ServiceLoader`'s `uses`/`provides` mechanism let a consumer bypass a provider module's encapsulation generally?

**Answer:**
No — it resolves a provider module the consumer never `requires`, but a direct import of that same provider's internal (non-exported) package still fails to compile with a real javac error. The SPI exception is narrowly scoped to service binding, not a general hole.

**Why it matters:**
Distinguishes a real, deliberate SPI mechanism from a general encapsulation bypass — a common conceptual error.

**Common trap:**
Assuming any `uses`/`provides` relationship means the consumer can freely access the provider's internals.

**Related:**
[Java Platform Module System (JPMS)](../syllabus/02-java/language-core/java-platform-module-system.md)

## Card: Migrating incrementally

**Prompt:**
How does JPMS let a codebase adopt modules without a big-bang rewrite of its entire dependency tree?

**Answer:**
The unnamed module (classpath) runs legacy, unmodularized code unchanged; automatic modules let a plain JAR participate on the module path without its own `module-info.java`. Both exist specifically to make modularization adoptable incrementally.

**Why it matters:**
JPMS's real-world adoption story — most codebases can't rewrite every dependency's module descriptor at once.

**Common trap:**
Believing JPMS requires every dependency to be a proper named module before any part of a codebase can use it.

**Related:**
[Java Platform Module System (JPMS)](../syllabus/02-java/language-core/java-platform-module-system.md)
