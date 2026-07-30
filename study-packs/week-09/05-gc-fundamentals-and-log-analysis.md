---
title: "T-303 / T-306 · GC Fundamentals & Log Analysis"
topic_id: T-306
domain: JVM
tier: Advanced
iwi: 7.35
prerequisites: []
unlocks: []
week: 9
last_reviewed: 2026-07-30
canonical: ../../handbook/jvm/gc-fundamentals-and-log-analysis.md
---

# T-303 / T-306 · GC Fundamentals & Log Analysis

**IWI 7.35 (T-306) / 6.90 (T-303) · Advanced / Core tier · centre of gravity of the JVM chapter, per the blueprint: "the valuable framing is not 'name the GC algorithms' but 'here is a latency graph and a GC log — what happened?'"**

**Canonical chapter:** [GC Fundamentals and Log Analysis](../../handbook/jvm/gc-fundamentals-and-log-analysis.md). This file is the Week 9 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the GC log excerpts behind this summary are real, captured output from `practice/java/week-09/gc/src/AllocationStormDemo.java`, run with `-Xlog:gc*` against a real, constrained heap — not synthesized or described from documentation.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A real GC log, read](#3-a-real-gc-log-read)
4. [Reading the log: what each field means](#4-reading-the-log-what-each-field-means)
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

G1 (the default collector since JDK 9) divides the heap into fixed-size regions and collects the regions with the most garbage first — mostly young-generation regions, occasionally old-generation regions in a mixed collection. → [Definition and Purpose](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#definition-and-purpose).

## 2. Why it exists

GC removes an entire class of manual-memory bugs (use-after-free, leaks) at the cost of pause time. The skill this topic rewards is diagnosis from a real artifact — a GC log or latency graph — not reciting collector names. → [Definition and Purpose](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#definition-and-purpose).

## 3. A real GC log, read

Measured: four real young-generation collections in under 10ms, each sub-millisecond, with post-GC occupancy rising from 1M to 6M as retained allocations accumulate — the trend, not any single line, is the diagnostic signal. → [Internal Implementation](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#internal-implementation) has the full trace.

## 4. Reading the log: what each field means

Each line reports pause type, before/after occupancy, heap capacity, and duration. Watch for: rising pause frequency (allocation rate climbing), rising post-GC occupancy (objects surviving longer), "Concurrent Start" + mixed sequences (old-gen threshold crossed), and humongous allocations (≥50% of a region, bypass normal handling). → [Core Concepts](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#core-concepts).

## 5. Trade-offs

A larger heap means fewer but longer collections; a smaller young generation means more frequent, shorter pauses but more promotion pressure; a pause-time goal lets G1 tune sizing automatically but can backfire if set too aggressively. → [Trade-offs](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#trade-offs).

## 6. Interview questions

1. Pauses hit 4 seconds. Diagnose from this log.
2. Tuning means increasing heap size — true or false?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#interview-questions).

## 7. Common mistakes

Treating "tuning" as synonymous with "increase heap size"; not distinguishing young-only from mixed/full pauses; ignoring the occupancy-trend signal. → [Common Mistakes](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#common-mistakes).

## 8. Staff-level discussion

GC log analysis is a clear instance of "demonstrable skill beats recitable fact" — the same artifact-reading skill as a slow-query `EXPLAIN` plan or a flame graph, and generic advice ("use G1," "increase heap") is frequently wrong for the actual allocation pattern in front of you. → [Staff-Level Discussion](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#interview-answer-framework).

## 9. Summary

A real captured GC log shows sub-millisecond young pauses with a rising post-collection occupancy trend — the artifact this topic trains diagnosis on. "Increase the heap" is frequently the wrong fix; the log itself usually shows why. → [Summary](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#practice-exercises) and [Solutions](../../handbook/jvm/gc-fundamentals-and-log-analysis.md#solutions). Reproducible demo: `practice/java/week-09/gc/src/AllocationStormDemo.java`.

## 14. Additional Reading

- [Oracle — Garbage-First (G1) Garbage Collector tuning guide](https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html)

## 15. Official References

- [JEP 248: Make G1 the Default Garbage Collector](https://openjdk.org/jeps/248)
