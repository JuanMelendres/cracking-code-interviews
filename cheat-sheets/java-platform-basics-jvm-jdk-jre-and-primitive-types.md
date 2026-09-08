---
title: "Cheat Sheet: Java Platform Basics: JVM, JDK, JRE, and Primitive Types"
slug: java-platform-basics-jvm-jdk-jre-and-primitive-types
document_type: cheat-sheet
domain: 02-java
topic_id: T-2209
canonical: ../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md
last_updated: 2026-09-08
---

# Java Platform Basics: JVM, JDK, JRE, and Primitive Types

**Canonical chapter:** [`syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md`](../syllabus/02-java/language-core/java-platform-basics-jvm-jdk-jre-and-primitive-types.md)

## Core Mental Model

The JVM executes bytecode; the JDK is what you install to develop Java (JVM + compiler + tools); a standalone JRE (JVM + standard library, no compiler) is what you used to install to only run Java — Oracle stopped shipping one separately after Java 8/9. Primitives hold their value directly, with no object allocation.

## Essential Definitions

- **JVM** — executes bytecode, one implementation per platform, makes "write once, run anywhere" real.
- **JDK** — JVM + `javac` + dev tools (`jar`, `javadoc`, `jshell`, `jlink`, ...).
- **JRE (historical)** — JVM + standard library, no compiler; effectively discontinued as a separate download since Java 11.
- **Autoboxing/unboxing** — the compiler's automatic primitive-to-wrapper conversion and back.

## Decision Table

| Type | Size | Range |
|---|---|---|
| `byte` | 1 byte | -128 to 127 |
| `short` | 2 bytes | -32,768 to 32,767 |
| `int` | 4 bytes | -2,147,483,648 to 2,147,483,647 |
| `long` | 8 bytes | ±9,223,372,036,854,775,807 |
| `float` | 4 bytes | ~7 significant digits |
| `double` | 8 bytes | ~15 significant digits |
| `char` | 2 bytes | 0 to 65,535 (unsigned, UTF-16) |
| `boolean` | JVM-dependent | `true`/`false` |

## Common Pitfalls

- Comparing boxed `Integer`s with `==` — works accidentally for -128..127 (cached), breaks silently outside that range. Use `.equals()`.
- Using `float`/`double` for money — `0.1 + 0.2 != 0.3`. Use `BigDecimal` or integer minor-units.
- Auto-unboxing a `null` wrapper throws a real `NullPointerException`, not a silent `0`.
- Reciting "JDK contains JRE contains JVM" as if a standalone JRE still exists today.

## Interview Answer Skeleton

**30-sec:** JVM executes bytecode; JDK = JVM + compiler + tools; JRE (historical) = JVM + stdlib only, no compiler — discontinued as a separate download since Java 11. Java has 8 primitive types, each with a fixed size and range, stored directly with no object overhead.

**2-min:** Add the Integer-cache `==` gotcha (-128..127 cached, same object; outside that range, different objects) and the real fix (`.equals()`).

**Whiteboard:** Draw three nested boxes — JVM inside JDK, with JRE (crossed out with "discontinued since Java 11") as a smaller box that used to sit between them.

**Staff-level framing:** JDK-vs-`jlink`-custom-runtime is a real operability-vs-image-size trade-off for container deployments, not a single correct answer.

## Related

- syllabus/02-java/language-core/java-syntax-fundamentals-variables-control-flow-and-methods.md
- syllabus/02-java/language-core/java-modifiers-and-method-signatures.md
- syllabus/14-devops-containers/docker-and-containers-fundamentals.md
