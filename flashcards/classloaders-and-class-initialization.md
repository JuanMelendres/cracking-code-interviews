---
title: "Flashcards: ClassLoaders and Class Initialization"
slug: classloaders-and-class-initialization
document_type: flashcard-deck
domain: java-core
topic_id: T-114
canonical: ../handbook/java-core/classloaders-and-class-initialization.md
last_updated: 2026-09-02
---

# Flashcards: ClassLoaders and Class Initialization

**Canonical chapter:** [`syllabus/02-java/language-core/classloaders-and-class-initialization.md`](../syllabus/02-java/language-core/classloaders-and-class-initialization.md)

## Card: Real class identity

**Prompt:**
What determines a class's real identity in the JVM — just its fully-qualified name?

**Answer:**
No — identity is the pair `(fully-qualified name, defining ClassLoader)`. Same name, different loader, produces two genuinely distinct `Class` objects, verified directly with a real `ClassCastException`.

**Why it matters:**
The mechanism behind a real, confusing, and diagnosable class of production bugs.

**Common trap:**
Assuming "same name" always means "same class."

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/classloaders-and-class-initialization.md#internal-implementation)

## Card: Active use, not reference

**Prompt:**
Does referencing `SomeClass.class` trigger its static initializer?

**Answer:**
No — verified directly. Only genuine active use (construction, static method call, non-constant static field access) triggers initialization; a compile-time constant field read never does, even for the same class.

**Why it matters:**
A common, real source of confusion about when static side effects actually happen.

**Common trap:**
Assuming loading and initialization happen together.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/classloaders-and-class-initialization.md#internal-implementation)

## Card: Diagnosing the identity gotcha

**Prompt:**
You see "class X cannot be cast to class X" — what's your first diagnostic step?

**Answer:**
Print `getClassLoader()` on both sides of the failed cast — different results confirm the classloader-identity mechanism this chapter reproduces directly.

**Why it matters:**
Turns a confusing, seemingly-impossible error into a fast, concrete diagnosis.

**Common trap:**
Assuming it's a JVM bug or a build/version mismatch instead.

**Related:**
[Production Scenarios](../syllabus/02-java/language-core/classloaders-and-class-initialization.md#production-scenarios)
