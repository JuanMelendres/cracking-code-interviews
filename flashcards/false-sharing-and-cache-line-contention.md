---
title: "Flashcards: False Sharing and Cache-Line Contention"
slug: false-sharing-and-cache-line-contention
document_type: flashcard-deck
domain: 16-performance-jvm
topic_id: T-2417
canonical: ../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md
last_updated: 2026-09-20
---

# Flashcards: False Sharing and Cache-Line Contention

**Canonical chapter:** [`syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md`](../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md)

## Card: What is false sharing, in one sentence?

**Prompt:**
What is false sharing, and why does it happen even when threads share no logical data?

**Answer:**
Independent variables written by different threads/cores contending for the same 64-byte CPU cache line — cache coherence operates at line granularity, so any write anywhere in the line invalidates every other core's cached copy of the whole line, regardless of whether the data is logically shared.

**Why it matters:**
This is invisible at the Java-language level entirely — the Java Memory Model has nothing to say about it.

**Common trap:**
Assuming "no shared state" rules out contention.

**Related:**
[Definition and Purpose](../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md#definition-and-purpose)

## Card: Real measured false-sharing cost

**Prompt:**
This chapter measured false sharing directly with 4 threads incrementing 4 independent counters. What was the real, measured slowdown, and what fixed it?

**Answer:**
A real, reproducible ~16x slowdown when the 4 counters shared a cache line, eliminated by padding each counter at least 64 bytes apart.

**Why it matters:**
A concrete, citable number for an interview answer, not just "it's slower."

**Common trap:**
Assuming false sharing's cost is minor/theoretical rather than a real, large, measured effect.

**Related:**
[Internal Implementation](../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md#internal-implementation)

## Card: Why doesn't padding an AtomicLong array work?

**Prompt:**
Why does padding an array of `AtomicLong` objects at the array-slot level fail to eliminate false sharing?

**Answer:**
The array holds object *references*, not inline objects — padding the array's slots doesn't control where the actual `AtomicLong` objects are allocated on the heap, and sequential allocation often places them adjacently anyway. Only a primitive array's elements are laid out inline and contiguous.

**Why it matters:**
A real, easy-to-miss pitfall this chapter's own lab discovered while building its demo.

**Common trap:**
Assuming any array-based padding technique works the same regardless of element type.

**Related:**
[Core Concepts](../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md#core-concepts)
