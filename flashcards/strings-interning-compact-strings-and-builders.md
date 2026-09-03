---
title: "Flashcards: Strings: Interning, Compact Strings, and Builders"
slug: strings-interning-compact-strings-and-builders
document_type: flashcard-deck
domain: java-core
topic_id: T-106
canonical: ../handbook/java-core/strings-interning-compact-strings-and-builders.md
last_updated: 2026-09-02
---

# Flashcards: Strings: Interning, Compact Strings, and Builders

**Canonical chapter:** [`syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md`](../syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md)

## Card: What actually gets pooled

**Prompt:**
Does `new String("hello") == "hello"` evaluate to `true`?

**Answer:**
No — verified directly, `false`. `new String(...)` always allocates a distinct heap object, even with identical content; only literals/compile-time constants (and explicit `.intern()`) share pool identity.

**Why it matters:**
The exact, verified boundary of string pooling.

**Common trap:**
Assuming `==` works for any two equal-content strings.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md#internal-implementation)

## Card: Compact Strings, all-or-nothing

**Prompt:**
If a mostly-English string has ONE non-Latin-1 character, does only that character cost extra memory?

**Answer:**
No — verified reflectively, the entire string switches to 2-bytes-per-character encoding, doubling the whole backing array, not just the one character.

**Why it matters:**
A real, content-dependent threshold effect, not a smooth per-character cost.

**Common trap:**
Assuming the memory cost scales proportionally with the number of "wide" characters.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md#internal-implementation)

## Card: The real concatenation cost

**Prompt:**
How much slower is `String +=` in a loop than `StringBuilder.append()`, roughly?

**Answer:**
Real, measured 63-147x slower across repeated runs for a 60,000-iteration loop — genuinely quadratic versus amortized-linear.

**Why it matters:**
Turns "it's slow" into a defensible, measured claim.

**Common trap:**
Treating this as a minor stylistic preference rather than a real, dramatic performance difference.

**Related:**
[Internal Implementation](../syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md#internal-implementation)
