---
title: "T-307 · Memory Leak Diagnosis and Heap Dump Analysis"
topic_id: T-307
domain: JVM
tier: Advanced
iwi: 6.75
prerequisites: [T-306]
unlocks: []
week: 16
last_reviewed: 2026-07-31
canonical: ../../handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md
---

# T-307 · Memory Leak Diagnosis and Heap Dump Analysis

**IWI 6.75 · Advanced tier · Moderate interview frequency — ⭐ top-25 by IWI**

**Canonical chapter:** [Memory Leak Diagnosis and Heap Dump Analysis](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md). This file is the Week 16 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-16/memory-leak-diagnosis/` on OpenJDK 21.0.12.

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

A Java memory leak is an object reachable from a GC root despite being logically dead — an accidental reference, not missing memory. Diagnose via `jmap -histo:live` (forces GC first, so counts reflect true reachability) sampled repeatedly, then a targeted heap dump for the specific reference chain. → [Definition and Purpose](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#definition-and-purpose).

## 2. Why it exists

More heap only delays an inevitable OOM proportionally — it never fixes a leak's growth rate. The diagnostic discipline exists because the fix is always "break the specific reference," never "add memory." → [Definition and Purpose](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#definition-and-purpose).

## 3. The measured evidence

Real listener-registration leak: `jmap -histo:live` samples showed live `Session` instances growing from 32,701 to 67,167 across two samples in a leaky run, while a fixed version (calling `unregister()` on session end) showed **zero** live instances at the same sample points. A real heap dump captured via `jcmd ... GC.heap_dump` produced a 201MB, magic-header-valid `.hprof` file. → [Internal Implementation](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#internal-implementation) has the full trace.

## 4. Trade-offs

Both histogramming (`:live` forces a full GC) and heap dumps are pause-inducing — cheap enough for occasional diagnostic use, not for continuous production monitoring. → [Trade-offs](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#trade-offs).

## 5. Interview questions

1. A service's memory grows steadily over days and eventually OOMs. Walk through your diagnostic process.
2. Why does `jmap -histo:live` matter specifically — what would plain `jmap -histo` get wrong?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#interview-questions).

## 6. Common mistakes

Treating "memory keeps growing" as automatic leak evidence without ruling out a warming cache; reaching for more heap first; taking a heap dump before narrowing the suspect class with a cheaper histogram. → [Common Mistakes](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#common-mistakes).

## 7. Staff-level discussion

Treats "does this registration have a guaranteed, symmetric cleanup path" as a standing code-review question, not something only investigated after an incident; `-XX:+HeapDumpOnOutOfMemoryError` captures evidence exactly when its cost is already unavoidable. → [Staff-Level Discussion](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#interview-answer-framework).

## 8. Summary

A Java leak is an accidental reference, diagnosed by histogram sampling (cheap, finds the class) then a targeted heap dump (finds the reference chain). Measured directly: 32,701 → 67,167 growth in a leaky run vs. zero in the fixed version. → [Summary](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#practice-exercises) and [Solutions](../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md#solutions). Reproducible demo: `practice/java/week-16/memory-leak-diagnosis/`.

## 13. Additional Reading

- [Eclipse Memory Analyzer (MAT) documentation](https://eclipse.dev/mat/)

## 14. Official References

- [`jmap` command reference (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jmap.html)
- [`jcmd` command reference (Java 21)](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jcmd.html)
