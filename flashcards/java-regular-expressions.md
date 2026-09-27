---
title: "Flashcards: Java Regular Expressions"
slug: java-regular-expressions
document_type: flashcard-deck
domain: 02-java/language-core
topic_id: T-2430
canonical: ../syllabus/02-java/language-core/java-regular-expressions.md
last_updated: 2026-09-26
---

# Flashcards: Java Regular Expressions

**Canonical chapter:** [`syllabus/02-java/language-core/java-regular-expressions.md`](../syllabus/02-java/language-core/java-regular-expressions.md)

## Card: Precompilation cost

**Prompt:**
Why is calling `String.matches(regex)` repeatedly with the same regex slower than reusing a precompiled `Pattern`?

**Answer:**
`String.matches()` compiles a fresh `Pattern` internally on every call. Measured directly: 200,000 calls took 112ms via `String.matches()` versus 21ms via a cached `Pattern` — a 5.3x difference.

**Why it matters:**
A real, easily-fixed performance cost in any hot path using regex.

**Common trap:**
Assuming `String.matches()` caches the compiled pattern across calls.

**Related:**
[Java Regular Expressions](../syllabus/02-java/language-core/java-regular-expressions.md)

## Card: Possessive quantifiers can change results

**Prompt:**
Can a possessive quantifier (`.++`) ever fail to match input that the equivalent greedy quantifier (`.+`) matches successfully?

**Answer:**
Yes — verified directly: `<.+>` matches `"<a><b><c>"` fully, but `<.++>` fails to match the same input at all, because the possessive quantifier consumes the trailing `>` and can never give it back.

**Why it matters:**
Possessive quantifiers are a genuine behavior change, not a safe, drop-in performance optimization.

**Common trap:**
Treating possessive quantifiers as "greedy but faster" with identical matching results.

**Related:**
[Java Regular Expressions](../syllabus/02-java/language-core/java-regular-expressions.md)

## Card: Catastrophic backtracking is real, but pattern-specific

**Prompt:**
Does `^(a+)+$` cause catastrophic backtracking in `java.util.regex`?

**Answer:**
Measured directly on this JDK: no — it consistently completed in 0ms even at 45 repetitions. A different shape, 15 adjacent `a*` quantifiers before a required literal, DID show real exponential blowup (34ms at 10 characters, 13.4 seconds at 19).

**Why it matters:**
ReDoS risk depends on the actual pattern shape and JDK, not on memorized folklore about "the" evil regex example.

**Common trap:**
Citing `(a+)+` as a universal ReDoS example without having verified it against the engine actually in use.

**Related:**
[Java Regular Expressions](../syllabus/02-java/language-core/java-regular-expressions.md)
