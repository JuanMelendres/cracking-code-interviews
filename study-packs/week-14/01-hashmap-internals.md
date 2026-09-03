---
title: "T-201 · HashMap Internals"
topic_id: T-201
domain: Collections
tier: Foundation
iwi: 7.40
prerequisites: []
unlocks: []
week: 14
last_reviewed: 2026-07-30
canonical: ../../handbook/collections/hashmap-internals.md
---

# T-201 · HashMap Internals

**IWI 7.40 · Foundation tier · Near-Certain interview frequency — the single most-asked Java data structure question**

**Canonical chapter:** [HashMap Internals](../../syllabus/02-java/collections/hashmap-internals.md). This file is the Week 14 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-14/hashmap-internals/src/` on OpenJDK 21.0.12, using `--add-opens java.base/java.util=ALL-UNNAMED` for reflective inspection of private fields.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Lazy init and resize, measured](#3-lazy-init-and-resize-measured)
4. [Hash collision degradation and treeification, measured](#4-hash-collision-degradation-and-treeification-measured)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

A `HashMap` is an array of buckets; a key's `hashCode()` (spread through an internal function) picks the bucket. Ideally each bucket holds zero or one entries, giving O(1) average lookup. → [Definition and Purpose](../../syllabus/02-java/collections/hashmap-internals.md#definition-and-purpose).

## 2. Why it exists

`HashMap` gives O(1) average-case lookup by key, trading memory overhead and ordering guarantees for speed. → [Definition and Purpose](../../syllabus/02-java/collections/hashmap-internals.md#definition-and-purpose).

## 3. Lazy init and resize, measured

Measured: the backing array is `null` until the first `put()`, then length 16 with threshold 12. Adding a 13th entry (size exceeding 12) resizes to length 32, threshold 24, rehashing every entry. → [Internal Implementation](../../syllabus/02-java/collections/hashmap-internals.md#internal-implementation) has the full trace.

## 4. Hash collision degradation and treeification, measured

Measured: a constant-hashCode key forces all 50,000 entries into one bucket, producing a ~3,076x lookup slowdown versus a well-distributed hash — even with JDK 8+ treeification (confirmed via reflection: the bucket's node class is genuinely `TreeNode`) bounding the worst case at O(log n). → [Internal Implementation](../../syllabus/02-java/collections/hashmap-internals.md#internal-implementation) has the full trace.

## 5. Trade-offs

A lower load factor reduces collisions at the cost of memory; a higher one saves memory at the cost of more collisions; treeification bounds but doesn't eliminate the cost of a poor hash distribution. → [Trade-offs](../../syllabus/02-java/collections/hashmap-internals.md#trade-offs).

## 6. Interview questions

1. Your HashMap-based cache's lookup latency is climbing even though entry count is stable. What do you check?
2. Why does treeification also require the table capacity to be at least 64, not just the bucket size to be at least 8?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/collections/hashmap-internals.md#interview-questions).

## 7. Common mistakes

Assuming O(1) lookup holds regardless of hash quality; not sizing initial capacity when the final size is known; believing treeification eliminates rather than bounds the cost of a poor distribution. → [Common Mistakes](../../syllabus/02-java/collections/hashmap-internals.md#common-mistakes).

## 8. Staff-level discussion

Treeification's dual condition (bucket size AND table capacity) reflects distinguishing a genuine distribution problem from a simple sizing problem — the same reasoning applies when diagnosing a real production HashMap performance issue. → [Staff-Level Discussion](../../syllabus/02-java/collections/hashmap-internals.md#interview-answer-framework).

## 9. Summary

A HashMap resizes once size exceeds capacity × load factor, measured directly. A poor hashCode() forces collisions regardless of table size, measured at ~3,076x, even with treeification bounding but not eliminating the cost. → [Summary](../../syllabus/02-java/collections/hashmap-internals.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/collections/hashmap-internals.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/collections/hashmap-internals.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/collections/hashmap-internals.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/collections/hashmap-internals.md#practice-exercises) and [Solutions](../../syllabus/02-java/collections/hashmap-internals.md#solutions). Reproducible demos: `practice/java/week-14/hashmap-internals/src/`.

## 14. Additional Reading

- Joshua Bloch, *Effective Java*, Item 11

## 15. Official References

- [java.util.HashMap (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html)
