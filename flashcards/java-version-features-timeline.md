---
title: "Flashcards: Java Version Features Timeline"
slug: java-version-features-timeline
document_type: flashcard-deck
domain: 02-java
topic_id: T-2211
canonical: ../syllabus/02-java/language-core/java-version-features-timeline.md
last_updated: 2026-09-08
---

# Flashcards: Java Version Features Timeline

**Canonical chapter:** [`syllabus/02-java/language-core/java-version-features-timeline.md`](../syllabus/02-java/language-core/java-version-features-timeline.md)

## Card: LTS releases

**Prompt:**
Which Java releases are LTS (Long-Term Support)?

**Answer:**
Java 8, 11, 17, 21, and 25.

**Why it matters:**
LTS releases are what most production codebases actually run, and get extended vendor support.

**Common trap:**
Assuming non-LTS releases are somehow unstable overall — only their preview features carry real risk.

**Related:**
[Java Version Features Timeline](../syllabus/02-java/language-core/java-version-features-timeline.md)

## Card: Preview vs. final features

**Prompt:**
What's the difference between a "preview" feature and a "final" feature in a Java release?

**Answer:**
A preview feature is real and working but not yet API-stable, and requires `--enable-preview` to compile and run. A final feature ships without that flag and is considered stable.

**Why it matters:**
Claiming a preview feature is "in Java X" without noting the flag is a real, common overclaim.

**Common trap:**
Assuming a feature is production-safe just because it technically exists in a given release.

**Related:**
[Java Version Features Timeline](../syllabus/02-java/language-core/java-version-features-timeline.md)

## Card: Records vs. Java 8

**Prompt:**
True or false: records were introduced in Java 8, alongside lambdas.

**Answer:**
False — records finalized in Java 16 (JEP 395). Java 8's headline features are lambdas, the Stream API, `Optional`, and default/static interface methods.

**Why it matters:**
One of the most common Java-version misattributions, since both are now heavily used.

**Common trap:**
Lumping every "modern Java" feature into "Java 8" from memory.

**Related:**
[Java Version Features Timeline](../syllabus/02-java/language-core/java-version-features-timeline.md)

## Card: What virtual threads actually solve

**Prompt:**
What problem do virtual threads (Java 21, finalized) solve, and what do they NOT solve?

**Answer:**
They solve the OS-thread-count ceiling for high-concurrency, I/O-bound, blocking-style code — you can run hundreds of thousands of them. They do NOT make CPU-bound work faster; that's still limited by real available cores.

**Why it matters:**
A common overclaim is treating virtual threads as a general performance feature rather than a concurrency-scaling one.

**Common trap:**
Claiming virtual threads speed up CPU-bound computation.

**Related:**
[Java Version Features Timeline](../syllabus/02-java/language-core/java-version-features-timeline.md)

## Card: Exhaustive switch over a sealed type

**Prompt:**
Why can a `switch` expression over a `sealed` interface's permitted types skip the `default` branch?

**Answer:**
Because the compiler can prove, from the `permits` clause, that every possible subtype is covered — a real compile-time exhaustiveness guarantee (Java 21, JEP 441), not just a convention.

**Why it matters:**
Demonstrates sealed types (Java 17) and pattern matching for switch (Java 21) working together — a genuinely new capability, not just syntax sugar.

**Common trap:**
Adding a `default` branch out of habit, which silently defeats the exhaustiveness check the compiler would otherwise perform.

**Related:**
[Java Version Features Timeline](../syllabus/02-java/language-core/java-version-features-timeline.md)
