---
title: "Flashcards: Records, Sealed Types, and Pattern Matching"
slug: records-sealed-types-and-pattern-matching
document_type: flashcard-deck
domain: java-core
topic_id: T-110
canonical: ../handbook/java-core/records-sealed-types-and-pattern-matching.md
last_updated: 2026-09-02
---

# Flashcards: Records, Sealed Types, and Pattern Matching

**Canonical chapter:** [`handbook/java-core/records-sealed-types-and-pattern-matching.md`](../handbook/java-core/records-sealed-types-and-pattern-matching.md)

## Card: Record hashCode formula

**Prompt:**
Does a record's generated `hashCode()` equal `Objects.hash()` of its components?

**Answer:**
Not guaranteed. It satisfies the equals/hashCode contract (equal objects → equal hashes) but the exact formula is unspecified and measurably different from `Objects.hash()`.

**Why it matters:**
A common but incorrect interview claim; testable via a two-line demo.

**Common trap:**
Assuming record internals mirror IDE-generated `equals`/`hashCode`.

**Related:**
[handbook/java-core/records-sealed-types-and-pattern-matching.md](../handbook/java-core/records-sealed-types-and-pattern-matching.md)

## Card: Sealed exhaustiveness

**Prompt:**
Why doesn't a switch over a sealed interface need a `default` branch?

**Answer:**
The compiler enumerates the `permits` list and proves every possible value is covered by an existing case.

**Why it matters:**
Converts a class of runtime bugs (missed case) into a compile-time failure.

**Common trap:**
Adding `default` "just in case," which silently defeats the exhaustiveness guarantee for future variants.

**Related:**
[handbook/java-core/records-sealed-types-and-pattern-matching.md](../handbook/java-core/records-sealed-types-and-pattern-matching.md)
