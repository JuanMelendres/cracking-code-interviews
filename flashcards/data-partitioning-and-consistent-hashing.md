---
title: "Flashcards: Data Partitioning and Consistent Hashing"
slug: data-partitioning-and-consistent-hashing
document_type: flashcard-deck
domain: system-design
topic_id: T-806
canonical: ../handbook/system-design/data-partitioning-and-consistent-hashing.md
last_updated: 2026-08-06
---

# Flashcards: Data Partitioning and Consistent Hashing

**Canonical chapter:** [`syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md`](../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md)

## Card: Why naive hash % N remaps nearly all keys

**Prompt:**
Why does naive `hash(key) % N` remap nearly all keys when N changes?

**Answer:**
Because the divisor itself changed — nearly every key's modulus result is different, mathematically expected, not a rare edge case (measured 92.5% here).

**Why it matters:**
A quantitative, not just qualitative, understanding of the naive scheme's failure.

**Common trap:**
Assuming this cost is roughly constant regardless of hashing scheme.

**Related:**
[Internal Implementation](../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#internal-implementation)

## Card: Consistent hashing's remap fraction

**Prompt:**
What fraction of keys should move when removing 1 of N nodes under consistent hashing?

**Answer:**
Roughly `1/N` — measured at 9.2% for N=10, close to the 10% theoretical ideal.

**Why it matters:**
The concrete number that justifies consistent hashing's adoption industry-wide.

**Common trap:**
Assuming consistent hashing eliminates data movement entirely rather than bounding it.

**Related:**
[Internal Implementation](../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#internal-implementation)

## Card: Why virtual nodes

**Prompt:**
Why use many virtual nodes per physical node instead of one?

**Answer:**
One point per node gives uneven load distribution by chance; many virtual nodes converge each physical node's share closer to an even `1/N`.

**Why it matters:**
Without this, consistent hashing's theoretical benefit doesn't materialize evenly in practice.

**Common trap:**
Using consistent hashing with too few virtual nodes and being surprised by uneven load.

**Related:**
[Core Concepts](../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#core-concepts)
