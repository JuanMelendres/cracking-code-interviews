---
title: "T-209 · Collection Selection Decision Matrix"
topic_id: T-209
domain: Collections
tier: Core
iwi: 5.70
prerequisites: [T-201, T-202]
unlocks: []
week: 14
last_reviewed: 2026-07-30
canonical: ../../handbook/collections/collection-selection-decision-matrix.md
---

# T-209 · Collection Selection Decision Matrix

**IWI 5.70 · Core tier · High interview frequency**

**Canonical chapter:** [Collection Selection Decision Matrix](../../handbook/collections/collection-selection-decision-matrix.md). This file is the Week 14 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The four sub-decisions](#3-the-four-sub-decisions)
4. [The dominant-access-pattern discipline](#4-the-dominant-access-pattern-discipline)
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

Every collection choice reduces to three questions: how is it read, how is it written, and is it shared across threads? → [Definition and Purpose](../../handbook/collections/collection-selection-decision-matrix.md#definition-and-purpose).

## 2. Why it exists

The JDK deliberately offers multiple implementations per interface with different measured trade-offs; defaulting to one out of habit forfeits real, measurable performance. → [Definition and Purpose](../../handbook/collections/collection-selection-decision-matrix.md#definition-and-purpose).

## 3. The four sub-decisions

List: `ArrayList` for indexed reads, `LinkedList`/`ArrayDeque` for head/tail insertion. Map: `HashMap` single-threaded, `ConcurrentHashMap` + `merge()`/`compute()` for concurrent. Queue: bounded `BlockingQueue` for buffering with backpressure, `SynchronousQueue` for direct handoff. → [Core Concepts](../../handbook/collections/collection-selection-decision-matrix.md#core-concepts).

## 4. The dominant-access-pattern discipline

State the dominant operation explicitly, in one sentence, before naming any implementation — this single step prevents most of the mistakes this week's other four chapters document individually. → [Core Concepts](../../handbook/collections/collection-selection-decision-matrix.md#core-concepts).

## 5. Trade-offs

No collection implementation is free — every choice optimizes one operation at some cost to another. → [Trade-offs](../../handbook/collections/collection-selection-decision-matrix.md#trade-offs).

## 6. Interview questions

1. Walk me through choosing a collection for a service that ingests webhook events at a bursty rate and processes them at a steady rate.
2. Your team always uses ArrayList/HashMap by default. When does that default actually hurt?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/collections/collection-selection-decision-matrix.md#interview-questions).

## 7. Common mistakes

Naming an interface as if it determines the implementation; choosing by habit rather than access pattern; treating this week's four topics as unrelated facts. → [Common Mistakes](../../handbook/collections/collection-selection-decision-matrix.md#common-mistakes).

## 8. Staff-level discussion

The real Staff-level signal isn't knowing all four individual facts — it's applying them as one coherent decision process, unprompted, to a genuinely new scenario. → [Staff-Level Discussion](../../handbook/collections/collection-selection-decision-matrix.md#interview-answer-framework).

## 9. Summary

Every one of this week's measured findings is evidence for one branch of the same underlying decision framework, not four separate, unrelated facts. → [Summary](../../handbook/collections/collection-selection-decision-matrix.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/collections/collection-selection-decision-matrix.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/collections/collection-selection-decision-matrix.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/collections/collection-selection-decision-matrix.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/collections/collection-selection-decision-matrix.md#practice-exercises) and [Solutions](../../handbook/collections/collection-selection-decision-matrix.md#solutions).

## 14. Additional Reading

- [Storage Selection Trade-offs](../../handbook/system-design/storage-selection-tradeoffs.md) — the analogous method one layer up, at the storage-technology level

## 15. Official References

- [java.util.Collection (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Collection.html)
