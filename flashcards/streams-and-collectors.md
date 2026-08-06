---
title: "Flashcards: Streams and Collectors"
slug: streams-and-collectors
document_type: flashcard-deck
domain: java-core
topic_id: T-107
canonical: ../handbook/java-core/streams-and-collectors.md
last_updated: 2026-08-06
---

# Flashcards: Streams and Collectors

**Canonical chapter:** [`handbook/java-core/streams-and-collectors.md`](../handbook/java-core/streams-and-collectors.md)

## Card: When a stream pipeline actually executes

**Prompt:**
When does a stream pipeline actually execute?

**Answer:**
Only when a terminal operation is called — intermediate operations (filter, map, peek) build a lazy pipeline that does nothing on its own.

**Why it matters:**
Explains why `peek()`-based debugging can look confusing if you expect output immediately.

**Common trap:**
Assuming intermediate operations run as soon as they're called.

**Related:**
[Internal Implementation](../handbook/java-core/streams-and-collectors.md#internal-implementation)

## Card: Why toMap() throws on duplicates

**Prompt:**
Why does `Collectors.toMap()` throw on duplicate keys by default?

**Answer:**
The two-argument overload has no way to resolve a collision; the three-argument overload requires an explicit merge function.

**Why it matters:**
A common production `IllegalStateException` waiting to happen on real-world data.

**Common trap:**
Using the two-argument `toMap()` on data that could plausibly contain duplicate keys.

**Related:**
[Internal Implementation](../handbook/java-core/streams-and-collectors.md#internal-implementation)

## Card: What parallel() does and doesn't do

**Prompt:**
Does `parallel()` make a stream's writes to shared state thread-safe?

**Answer:**
No — measured directly: a plain `ArrayList` loses updates under `parallel().forEach()`. Use a proper collector instead.

**Why it matters:**
A silent, no-exception data-loss bug, not a crash — easy to miss without a size check.

**Common trap:**
Assuming `parallel()` handles thread-safety of the stream's own side effects.

**Related:**
[Production Scenarios](../handbook/java-core/streams-and-collectors.md#production-scenarios)
