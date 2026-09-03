---
title: "Flashcards: G1 Internals: Remembered Sets and Write Barriers"
slug: g1-remembered-sets-and-write-barriers
document_type: flashcard-deck
domain: jvm
topic_id: T-304
canonical: ../handbook/jvm/g1-remembered-sets-and-write-barriers.md
last_updated: 2026-08-06
---

# Flashcards: G1 Internals: Remembered Sets and Write Barriers

**Canonical chapter:** [`syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md`](../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md)

## Card: What lets G1 collect one region without scanning the whole heap

**Prompt:**
What two mechanisms let G1 collect one region without scanning the whole heap?

**Answer:**
Remembered sets (per-region incoming-reference records) and write barriers (which keep them accurate).

**Why it matters:**
The core mechanism behind G1's ability to do partial, region-scoped collections at all.

**Common trap:**
Describing G1 collection without naming the specific remembered-set/write-barrier mechanism that makes it possible.

**Related:**
[handbook/jvm/g1-remembered-sets-and-write-barriers.md](../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md)

## Card: What RSet/write-barrier cost scales with

**Prompt:**
Does RSet/write-barrier cost scale with allocation volume or cross-region write volume?

**Answer:**
Cross-region write volume — measured ~1,841x dirty-card difference between volume-matched low/high cross-region-write workloads.

**Why it matters:**
A real, measured number that redirects diagnosis toward the actual cost driver instead of total allocation.

**Common trap:**
Attributing high RSet overhead to total allocation volume rather than the specific cross-region write pattern.

**Related:**
[handbook/jvm/g1-remembered-sets-and-write-barriers.md](../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md)

## Card: The JDK 17+ log name for the old "Update RS" phase

**Prompt:**
What's the JDK 17+ log phase name for what used to be called "Update RS"?

**Answer:**
"Merge Heap Roots" (with "Merged Cards" / "Scanned Cards" sub-metrics).

**Why it matters:**
Prevents searching a modern GC log for terminology that no longer exists in current JDKs.

**Common trap:**
Searching a current GC log for "Update RS" instead of the renamed "Merge Heap Roots" phase.

**Related:**
[handbook/jvm/g1-remembered-sets-and-write-barriers.md](../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md)
