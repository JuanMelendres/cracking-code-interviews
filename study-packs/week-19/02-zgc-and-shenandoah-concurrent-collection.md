---
title: "T-305 · ZGC and Shenandoah: Concurrent Collection"
topic_id: T-305
domain: JVM
tier: Advanced
iwi: 5.40
prerequisites: [T-306]
unlocks: []
week: 19
last_reviewed: 2026-08-02
canonical: ../../handbook/jvm/zgc-and-shenandoah-concurrent-collection.md
---

# T-305 · ZGC and Shenandoah: Concurrent Collection

**IWI 5.40 · Advanced tier · Moderate interview frequency**

**Canonical chapter:** [ZGC and Shenandoah: Concurrent Collection](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md). This file is the Week 19 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the evidence behind this summary is real, executed output from `practice/java/week-19/zgc-vs-g1/` — G1 vs. ZGC pause times, real ZGC safepoint logs, and a confirmed real Shenandoah pause.

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

ZGC and Shenandoah move G1's stop-the-world evacuation work to run concurrently with application threads, using different reference-remapping mechanisms to keep references correct while objects move. → [Mental Model](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#mental-model).

## 2. Why it exists

G1's pause times, even well-tuned, scale somewhat with live-data volume — some latency-sensitive workloads need pause times that don't. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#definition-and-purpose).

## 3. The measured evidence

Real, identical allocation-churn workload: G1 max pause 0.748ms, no stalls, 28.9M allocations completed. ZGC's real "At safepoint" durations: 1-40 microseconds (one to two orders of magnitude shorter) — but 218 real allocation-stall events and only 22.5M allocations completed in the same window. Shenandoah confirmed a real 0.010ms pause on this environment. → [Internal Implementation](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#internal-implementation) has the full trace.

## 4. Trade-offs

Dramatically shorter, more predictable pauses at the real cost of allocation-stall risk under insufficient heap headroom and additional background overhead. → [Trade-offs](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#trade-offs).

## 5. Interview questions

1. A service migrates from G1 to ZGC and initially sees worse p99 under peak load. What would you check?
2. Would you recommend ZGC for a nightly batch job with no strict latency requirement?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#interview-questions).

## 6. Common mistakes

Describing ZGC/Shenandoah as "just faster G1"; migrating without adjusting heap headroom; treating "sub-millisecond pause" as "zero collection-related cost." → [Common Mistakes](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#common-mistakes).

## 7. Staff-level discussion

Reasons about collector choice as workload-shape-dependent, and treats heap-headroom re-provisioning as a required, first-class part of any migration to a concurrent collector. → [Staff-Level Discussion](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#interview-answer-framework).

## 8. Summary

Concurrent relocation buys dramatically shorter pauses at the real cost of allocation-stall risk. Measured directly: ZGC's real pauses in microseconds vs. G1's 0.748ms, but 218 real stalls and ~22% less throughput on this workload. → [Summary](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#practice-exercises) and [Solutions](../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md#solutions). Reproducible demo: `practice/java/week-19/zgc-vs-g1/`.

## 13. Additional Reading

- [JEP 439: Generational ZGC](https://openjdk.org/jeps/439)

## 14. Official References

- [JEP 439: Generational ZGC](https://openjdk.org/jeps/439)
- [JEP 379: Shenandoah](https://openjdk.org/jeps/379)
