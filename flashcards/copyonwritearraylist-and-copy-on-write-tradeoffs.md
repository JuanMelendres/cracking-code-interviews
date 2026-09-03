---
title: "Flashcards: CopyOnWriteArrayList and Copy-on-Write Trade-offs"
slug: copyonwritearraylist-and-copy-on-write-tradeoffs
document_type: flashcard-deck
domain: collections
topic_id: T-206
canonical: ../handbook/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md
last_updated: 2026-09-02
---

# Flashcards: CopyOnWriteArrayList and Copy-on-Write Trade-offs

**Canonical chapter:** [`syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md`](../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md)

## Card: The real write cost

**Prompt:**
Does `set(index, value)` on a `CopyOnWriteArrayList` copy the whole array, or just the changed slot?

**Answer:**
The whole array — every mutating operation, including `set()`, triggers a full O(n) copy, measured directly (0.37µs at 1K elements, 82.33µs at 500K).

**Why it matters:**
A common underestimate of the real write cost.

**Common trap:**
Assuming the copy cost scales with the size of the change rather than the size of the whole list.

**Related:**
[Internal Implementation](../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#internal-implementation)

## Card: The real read benefit

**Prompt:**
How much faster were `CopyOnWriteArrayList`'s reads than `Collections.synchronizedList()`'s reads under real concurrent access with zero writers?

**Answer:**
~44x faster, measured directly across 8 threads x 2,000,000 reads each.

**Why it matters:**
Quantifies "lock-free reads" as a real, measured number rather than an abstract claim.

**Common trap:**
Assuming `synchronizedList()`'s lock cost only matters when writers are actually contending.

**Related:**
[Internal Implementation](../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#internal-implementation)

## Card: Right workload shape

**Prompt:**
What workload shape makes `CopyOnWriteArrayList`'s trade-off favorable?

**Answer:**
Read-heavy, write-rare — e.g., listener/observer lists, rarely-changing configuration snapshots.

**Why it matters:**
The entire design is built around this one specific assumption.

**Common trap:**
Using it as a general-purpose thread-safe list default regardless of write frequency.

**Related:**
[Decision Framework](../syllabus/02-java/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md#decision-framework)
