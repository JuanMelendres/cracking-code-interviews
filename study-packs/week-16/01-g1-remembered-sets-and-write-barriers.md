---
title: "T-304 · G1 Internals: Remembered Sets and Write Barriers"
topic_id: T-304
domain: JVM
tier: Advanced
iwi: 6.80
prerequisites: [T-306]
unlocks: []
week: 16
last_reviewed: 2026-07-31
canonical: ../../handbook/jvm/g1-remembered-sets-and-write-barriers.md
---

# T-304 · G1 Internals: Remembered Sets and Write Barriers

**IWI 6.80 · Advanced tier · High interview frequency**

**Canonical chapter:** [G1 Internals: Remembered Sets and Write Barriers](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md). This file is the Week 16 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-16/g1-remembered-sets/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The measured cost](#3-the-measured-cost)
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

A remembered set (RSet) is a per-region record of incoming cross-region references; a write barrier keeps it accurate by dirtying a card on every reference store. Together they let G1 evacuate one region without scanning the whole heap for pointers into it. → [Definition and Purpose](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#definition-and-purpose).

## 2. Why it exists

G1's defining strategy — collect the regions with the most garbage first — is only safe if incoming references to a to-be-collected region are already known without a full-heap scan. → [Definition and Purpose](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#definition-and-purpose).

## 3. The measured cost

Real measured evidence: a low-cross-region-write workload produced 13 total Dirty Cards across 4 pauses; a volume-matched high-cross-region-write workload produced 23,938 — a ~1,841x difference, far exceeding the 8x pause-count difference between the two runs, proving the cost tracks write pattern, not allocation volume. → [Internal Implementation](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#internal-implementation) has the full trace.

## 4. Trade-offs

Write barriers impose a small, constant cost on every reference store, in exchange for avoiding a full-heap scan per pause — the correct trade for nearly every workload. → [Trade-offs](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#trade-offs).

## 5. Interview questions

1. Why can G1 collect a subset of regions without scanning the whole heap, and what two mechanisms make that safe?
2. Pause times grew but heap occupancy didn't — what do you suspect, and how do you confirm it?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#interview-questions).

## 6. Common mistakes

Naming remembered sets without write barriers or vice versa; assuming cost scales with allocation volume rather than cross-region write volume; citing pre-JDK-17 log terminology ("Update RS"/"Scan RS") as current. → [Common Mistakes](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#common-mistakes).

## 7. Staff-level discussion

Every regional/generational collector needs some equivalent of a write barrier plus a per-region incoming-reference record — G1's card table is one engineering choice, not the only possible design. → [Staff-Level Discussion](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#interview-answer-framework).

## 8. Summary

Remembered sets plus write barriers let G1 collect regions in isolation. Cost tracks cross-region write volume specifically — measured 1,841x dirty-card difference on volume-matched workloads. Pause time growing with flat heap occupancy is the production signature; fix by partitioning hot mutable structures. → [Summary](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#practice-exercises) and [Solutions](../../handbook/jvm/g1-remembered-sets-and-write-barriers.md#solutions). Reproducible demo: `practice/java/week-16/g1-remembered-sets/`.

## 13. Additional Reading

- [OpenJDK Wiki — G1 Garbage Collector](https://wiki.openjdk.org/display/HotSpot/G1+Garbage+Collector)

## 14. Official References

- [Oracle — G1 Garbage Collector tuning guide](https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html)
