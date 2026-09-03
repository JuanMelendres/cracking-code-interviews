---
title: "T-312 · JVM Flags and Container Ergonomics"
topic_id: T-312
domain: JVM
tier: Core
iwi: 5.90
prerequisites: [T-301]
unlocks: []
week: 16
last_reviewed: 2026-07-31
canonical: ../../handbook/jvm/jvm-flags-and-container-ergonomics.md
---

# T-312 · JVM Flags and Container Ergonomics

**IWI 5.90 · Core tier · Moderate interview frequency**

**Canonical chapter:** [JVM Flags and Container Ergonomics](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md). This file is the Week 16 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Extends Week 15's container-memory ergonomics coverage (`study-packs/week-15/01-...md`) into CPU detection and flag-level heap-percentage tuning.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-16/container-ergonomics/` against real Docker containers (Docker 29.6.2, `eclipse-temurin:21-jre`).

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

Container ergonomics (default since JDK 10) read cgroup memory AND CPU limits — not host hardware — for two separate sizing decisions: a percentage of detected memory becomes the heap cap (`-XX:MaxRAMPercentage`, default 25%), and detected available CPUs drive GC thread counts and similar concurrency-scaled defaults. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#definition-and-purpose).

## 2. Why it exists

Without cgroup-aware sizing, a JVM in a memory-limited container would size its heap against the host's full RAM and get OOM-killed almost immediately. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#definition-and-purpose).

## 3. The measured evidence

Real CPU-quota detection: the same 10-core host reported "CPUs: 10 total, 2 available" or "6 available" depending on `--cpus`. Real `MaxRAMPercentage` scaling on a FIXED 1GB container: 25% (default) → 247MB heap, 75% → 742MB heap — proportional scaling from the flag alone, container limit unchanged. → [Internal Implementation](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#internal-implementation) has the full trace.

## 4. Trade-offs

The default 25% `MaxRAMPercentage` leaves deliberate headroom for metaspace/stacks/code-cache, safe but heap-conservative; raising it reclaims heap capacity at the cost of needing to reason explicitly about the other regions. → [Trade-offs](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#trade-offs).

## 5. Interview questions

1. Two identically-memory-limited containers show different GC pause behavior. What would you check first?
2. A container's memory limit was doubled, but heap-related metrics only grew modestly. Why?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#interview-questions).

## 6. Common mistakes

Assuming `Runtime.availableProcessors()` reflects host hardware; expecting the heap cap to grow by the container memory limit's full delta rather than the percentage of it; forgetting CPU limits affect GC thread counts, not just memory limits affecting heap. → [Common Mistakes](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#common-mistakes).

## 7. Staff-level discussion

CPU-limit and memory-limit provisioning is a joint decision affecting JVM ergonomics together — factor this into VM-to-container migration planning rather than discovering the interaction via a post-migration incident. → [Staff-Level Discussion](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#interview-answer-framework).

## 8. Summary

Container ergonomics govern CPU detection as well as heap sizing. Measured directly: CPU detection correctly reflects `--cpus` quota, not host core count; heap cap scales proportionally with `MaxRAMPercentage` on a fixed container memory limit. → [Summary](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#practice-exercises) and [Solutions](../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md#solutions). Reproducible demo: `practice/java/week-16/container-ergonomics/`.

## 13. Additional Reading

- [Java containers and the mystery of the disappearing memory](https://developers.redhat.com/articles/2022/04/19/java-17-whats-new-openjdks-container-awareness)

## 14. Official References

- [`Runtime.availableProcessors()` (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Runtime.html#availableProcessors())
