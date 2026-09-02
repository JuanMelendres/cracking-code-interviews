---
title: "Flashcards: Enums, EnumMap, and EnumSet"
slug: enums-enummap-and-enumset
document_type: flashcard-deck
domain: java-core
topic_id: T-111
canonical: ../handbook/java-core/enums-enummap-and-enumset.md
last_updated: 2026-09-02
---

# Flashcards: Enums, EnumMap, and EnumSet

**Canonical chapter:** [`handbook/java-core/enums-enummap-and-enumset.md`](../handbook/java-core/enums-enummap-and-enumset.md)

## Card: Real singleton protection

**Prompt:**
Can reflection create a second instance of an enum constant?

**Answer:**
No — verified directly, a real, dedicated `IllegalArgumentException: Cannot reflectively create enum objects`, a JVM-level guard, not merely a private-constructor convention.

**Why it matters:**
The real reason enums are the recommended Singleton-pattern implementation.

**Common trap:**
Assuming enum singletons are only as protected as a hand-written one.

**Related:**
[Internal Implementation](../handbook/java-core/enums-enummap-and-enumset.md#internal-implementation)

## Card: The ordinal() danger

**Prompt:**
What happens if you persist `Enum.ordinal()` and later insert a new constant in the middle of the declaration?

**Answer:**
Every later constant's ordinal silently shifts — an old persisted value now resolves to the wrong constant, with zero exception or warning, reproduced directly in this chapter.

**Why it matters:**
A real, silent, genuinely dangerous production data-corruption pattern.

**Common trap:**
Assuming `ordinal()` is safe as long as constants are never removed.

**Related:**
[Internal Implementation](../handbook/java-core/enums-enummap-and-enumset.md#internal-implementation)

## Card: EnumMap's real advantage

**Prompt:**
Is `EnumMap` dramatically faster than `HashMap` for enum keys?

**Answer:**
Not dramatically — measured directly at a modest ~1.1x. Its real, unambiguous advantage is guaranteed natural iteration order, not raw throughput.

**Why it matters:**
An honest correction against overstated performance folklore.

**Common trap:**
Assuming array-backed-versus-hash-backed always implies a dramatic speed difference.

**Related:**
[Internal Implementation](../handbook/java-core/enums-enummap-and-enumset.md#internal-implementation)
