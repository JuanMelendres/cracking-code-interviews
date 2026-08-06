---
title: "Flashcards: ConcurrentHashMap Internals"
slug: concurrenthashmap-internals
document_type: flashcard-deck
domain: collections
topic_id: T-205
canonical: ../handbook/collections/concurrenthashmap-internals.md
last_updated: 2026-08-06
---

# Flashcards: ConcurrentHashMap Internals

**Canonical chapter:** [`handbook/collections/concurrenthashmap-internals.md`](../handbook/collections/concurrenthashmap-internals.md)

## Card: HashMap vs ConcurrentHashMap under concurrency

**Prompt:**
What happens to a plain HashMap under concurrent writes from multiple threads?

**Answer:**
It can corrupt silently — measured at 68,683 of an expected 160,000 entries surviving, with no exception thrown.

**Why it matters:**
The core reason ConcurrentHashMap exists.

**Common trap:**
Assuming a plain HashMap "probably won't cause a problem in practice" under light concurrent access.

**Related:**
[Internal Implementation](../handbook/collections/concurrenthashmap-internals.md#internal-implementation)

## Card: Why get-then-put is not atomic

**Prompt:**
Is `get()` followed by `put()` atomic on a ConcurrentHashMap?

**Answer:**
No — measured at a 26,212-of-160,000 lost-update rate. Each call is individually safe, but the sequence has an unprotected gap between them.

**Why it matters:**
The most common ConcurrentHashMap misuse in real codebases.

**Common trap:**
Assuming ConcurrentHashMap's thread-safety extends across multiple separate method calls.

**Related:**
[Internal Implementation](../handbook/collections/concurrenthashmap-internals.md#internal-implementation)

## Card: The correct atomic increment

**Prompt:**
What's the correct way to atomically increment a counter in a ConcurrentHashMap?

**Answer:**
`map.merge(key, 1, Integer::sum)` — performs the whole read-modify-write as one atomic operation.

**Why it matters:**
Eliminates the lost-update race entirely, measured directly.

**Common trap:**
Using a manual get()/put() pair instead of the built-in atomic compound operation.

**Related:**
[Internal Implementation](../handbook/collections/concurrenthashmap-internals.md#internal-implementation)
