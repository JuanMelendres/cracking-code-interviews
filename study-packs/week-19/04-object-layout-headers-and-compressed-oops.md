---
title: "T-302 · Object Layout, Headers, and Compressed Oops"
topic_id: T-302
domain: JVM
tier: Advanced
iwi: 4.90
prerequisites: [T-301]
unlocks: []
week: 19
last_reviewed: 2026-08-02
canonical: ../../handbook/jvm/object-layout-headers-and-compressed-oops.md
---

# T-302 · Object Layout, Headers, and Compressed Oops

**IWI 4.90 · Advanced tier · Moderate interview frequency**

**Canonical chapter:** [Object Layout, Headers, and Compressed Oops](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md). This file is the Week 19 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-19/object-layout/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The measured evidence](#3-the-measured-evidence)
4. [Trade-offs](#4-trade-offs)
5. [Interview questions](#5-interview-questions)
6. [Common mistakes](#6-common-mistakes)
7. [Staff-level discussion](#7-staff-level-discussion)
8. [Summary](#8-summary)
9. [Key Takeaways](#9-key-takeaways)
10. [Cheat Sheet](#10-cheat-sheet)
11. [Flashcards](#11-flashcards)
12. [Practice Exercises](#12-practice-exercises)
13. [Additional Reading](#13-additional-reading)
14. [Official References](#14-official-references)

---

## 1. The concept

Every Java object is a fixed-size header (12-16 bytes) plus its instance fields, padded to an 8-byte boundary — no object is "just its fields." → [Mental Model](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#mental-model).

## 2. Why it exists

Compressed oops exploits object-alignment guarantees to represent references in 32 bits instead of 64, roughly halving reference-field cost for a negligible decode overhead. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#definition-and-purpose).

## 3. The measured evidence

Real 5-million-node reference-heavy object graph: 134MB with compressed oops on (28 bytes/node) vs. 191MB with it off (40 bytes/node) — a real ~42% footprint difference from the flag alone. → [Internal Implementation](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#internal-implementation) has the full trace.

## 4. Trade-offs

Essentially free under the ~32GB heap ceiling; past it, the JVM silently falls back to uncompressed references, doubling every reference field's cost. → [Trade-offs](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#trade-offs).

## 5. Interview questions

1. A team's memory estimate (declared-field-size sum) significantly undershoots real production usage. What's the likely gap?
2. Why might increasing heap past ~32GB make a reference-heavy workload's memory footprint worse per logical unit of data?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#interview-questions).

## 6. Common mistakes

Estimating memory by summing only declared field sizes; assuming a reference field always costs a fixed size; treating heap growth past 32GB as purely additive. → [Common Mistakes](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#common-mistakes).

## 7. Staff-level discussion

Factors the compressed-oops addressability ceiling into heap-scaling capacity decisions for reference-heavy workloads specifically. → [Staff-Level Discussion](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#interview-answer-framework).

## 8. Summary

Header and reference-field overhead are real, measurable, and easy to omit from naive estimates. Measured directly: ~42% footprint difference from the compressed-oops flag alone, no data change. → [Summary](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#practice-exercises) and [Solutions](../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md#solutions). Reproducible demo: `practice/java/week-19/object-layout/`.

## 13. Additional Reading

- [OpenJDK Wiki — CompressedOops](https://wiki.openjdk.org/display/HotSpot/CompressedOops)

## 14. Official References

- [OpenJDK Wiki — CompressedOops](https://wiki.openjdk.org/display/HotSpot/CompressedOops)
