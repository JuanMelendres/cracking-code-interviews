---
title: "Flashcards: HashMap Internals"
slug: hashmap-internals
document_type: flashcard-deck
domain: collections
topic_id: T-201
canonical: ../handbook/collections/hashmap-internals.md
last_updated: 2026-08-06
---

# Flashcards: HashMap Internals

**Canonical chapter:** [`handbook/collections/hashmap-internals.md`](../handbook/collections/hashmap-internals.md)

## Card: When HashMap resizes

**Prompt:**
When does a `HashMap` resize?

**Answer:**
When `size` exceeds `capacity × loadFactor` (the threshold) — the backing array doubles and every entry is rehashed.

**Why it matters:**
The mechanism behind HashMap's amortized O(1) put/get despite growing.

**Common trap:**
Not sizing initial capacity when the final entry count is known, causing avoidable resize events.

**Related:**
[Internal Implementation](../handbook/collections/hashmap-internals.md#internal-implementation)

## Card: Treeification threshold

**Prompt:**
When does a HashMap bucket treeify?

**Answer:**
When it holds at least 8 nodes AND the table's overall capacity is at least 64 — otherwise the table resizes instead.

**Why it matters:**
Distinguishes a genuine hash-collision problem from a simple sizing problem.

**Common trap:**
Stating only the bucket-size threshold without the capacity condition.

**Related:**
[Core Concepts](../handbook/collections/hashmap-internals.md#core-concepts)

## Card: What a poor hashCode() costs

**Prompt:**
What happens to HashMap performance with a poor hashCode()?

**Answer:**
Every key can land in the same bucket regardless of table size — measured at a ~3,076x lookup slowdown, even with treeification bounding the worst case at O(log n).

**Why it matters:**
Resizing the table doesn't fix a distribution problem.

**Common trap:**
Assuming a larger table always fixes slow HashMap lookups.

**Related:**
[Production Scenarios](../handbook/collections/hashmap-internals.md#production-scenarios)
