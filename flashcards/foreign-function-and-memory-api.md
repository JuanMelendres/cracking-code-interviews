---
title: "Flashcards: Foreign Function & Memory API"
slug: foreign-function-and-memory-api
document_type: flashcard-deck
domain: concurrency
topic_id: T-416
canonical: ../handbook/concurrency/foreign-function-and-memory-api.md
last_updated: 2026-09-01
---

# Flashcards: Foreign Function & Memory API

**Canonical chapter:** [`handbook/concurrency/foreign-function-and-memory-api.md`](../handbook/concurrency/foreign-function-and-memory-api.md)

## Card: What does FFM replace?

**Prompt:**
What two older mechanisms does the Foreign Function & Memory API replace?

**Answer:**
JNI (for calling native code) and `sun.misc.Unsafe`/direct `ByteBuffer`s
(for off-heap memory) — both replaced with a pure-Java, safety-checked API.
Measured directly: a real native `strlen` call with zero JNI glue code, and
a real use-after-close exception instead of an unsafe memory read.

**Why it matters:**
It's the actual scope of what this Expert-tier, rare-frequency topic is
for.

**Common trap:**
Confusing FFM with `Unsafe` as interchangeable, rather than FFM being the
safer replacement for it.

**Related:**
[handbook/concurrency/foreign-function-and-memory-api.md](../handbook/concurrency/foreign-function-and-memory-api.md), [VarHandles, Unsafe, and Their Replacement](../handbook/concurrency/varhandles-and-unsafe.md)
