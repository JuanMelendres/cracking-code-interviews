---
title: "Flashcards: Java Platform Basics: JVM, JDK, JRE, and Primitive Types"
slug: java-platform-basics-jvm-jdk-jre-and-primitive-types
document_type: flashcard-deck
domain: 02-java
topic_id: T-2209
canonical: ../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md
last_updated: 2026-09-08
---

# Flashcards: Java Platform Basics: JVM, JDK, JRE, and Primitive Types

**Canonical chapter:** [`syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md`](../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md)

## Card: JVM vs. JDK vs. JRE

**Prompt:**
What's the difference between the JVM, the JDK, and the JRE?

**Answer:**
The JVM executes bytecode. The JDK is JVM + compiler (`javac`) + development tools — what you install to write Java. The JRE (historically) was JVM + standard library only, no compiler — enough to run already-built Java, but Oracle stopped shipping one separately after Java 8/9.

**Why it matters:**
One of the single most commonly asked Junior Java interview questions.

**Common trap:**
Reciting the three-layer hierarchy as if a standalone JRE download still exists today.

**Related:**
[Java Platform Basics](../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md)

## Card: The Integer cache gotcha

**Prompt:**
Why does `Integer a = 127; Integer b = 127; a == b` return `true`, but the same code with `128` returns `false`?

**Answer:**
`Integer.valueOf()` caches boxed values from -128 to 127 — two boxed `127`s are literally the same cached object. `128` is outside the cache range, so two boxed `128`s are separate objects, and `==` compares identity, not value, for wrapper types.

**Why it matters:**
A real, common production bug shape — code that "works" in tests with small values and breaks with real data.

**Common trap:**
Using `==` to compare wrapper types instead of `.equals()`.

**Related:**
[Java Platform Basics](../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md)

## Card: Java's 8 primitive types

**Prompt:**
Name all 8 of Java's primitive types.

**Answer:**
`byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`.

**Why it matters:**
The literal floor of the type system — assumed known by every other chapter in this domain.

**Common trap:**
Forgetting `char` and `boolean` are primitives too, or miscounting the four integer types.

**Related:**
[Java Platform Basics](../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md)

## Card: Why 0.1 + 0.2 != 0.3

**Prompt:**
Why does `0.1 + 0.2` not exactly equal `0.3` in Java?

**Answer:**
`double` stores values in binary floating-point, and `0.1` and `0.2` cannot be represented exactly in binary — the result is `0.30000000000000004`, extremely close but not exact.

**Why it matters:**
This is why financial/exact-decimal code must use `BigDecimal`, never `float`/`double`.

**Common trap:**
Assuming this is a Java bug rather than a real, universal binary-floating-point limitation.

**Related:**
[Java Platform Basics](../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md)

## Card: Auto-unboxing a null wrapper

**Prompt:**
What happens if you auto-unbox a `null` `Integer` into an `int`?

**Answer:**
A real `NullPointerException`, thrown at the exact line the unboxing happens — not a silent `0`.

**Why it matters:**
A genuine, common production bug shape, especially with `Integer` fields from a database or JSON payload that can be `null`.

**Common trap:**
Assuming an unboxed `null` silently becomes `0` or `false`.

**Related:**
[Java Platform Basics](../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md)
