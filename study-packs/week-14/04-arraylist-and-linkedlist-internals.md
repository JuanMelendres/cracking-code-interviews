---
title: "T-202 · ArrayList and LinkedList Internals"
topic_id: T-202
domain: Collections
tier: Foundation
iwi: 5.60
prerequisites: []
unlocks: []
week: 14
last_reviewed: 2026-07-30
canonical: ../../handbook/collections/arraylist-and-linkedlist-internals.md
---

# T-202 · ArrayList and LinkedList Internals

**IWI 5.60 · Foundation tier · Very High interview frequency**

**Canonical chapter:** [ArrayList and LinkedList Internals](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md). This file is the Week 14 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-14/arraylist-linkedlist/src/` on OpenJDK 21.0.12.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [ArrayList's growth factor, measured](#3-arraylists-growth-factor-measured)
4. [Random access and front-insertion, measured](#4-random-access-and-front-insertion-measured)
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

`ArrayList` is a resizable array; `LinkedList` is a doubly-linked list of nodes. Every performance difference between them follows from that one structural fact. → [Definition and Purpose](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#definition-and-purpose).

## 2. Why it exists

`ArrayList` optimizes for random access and iteration; `LinkedList` optimizes for insertion/removal at the ends or an already-known position, at the cost of O(n) indexed access. → [Definition and Purpose](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#definition-and-purpose).

## 3. ArrayList's growth factor, measured

Measured via reflection: `ArrayList` grows by roughly 1.5x when full (10→15→22→33→...), not by doubling. → [Internal Implementation](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#internal-implementation) has the full trace.

## 4. Random access and front-insertion, measured

Measured: `LinkedList.get(index)` is ~320x slower than `ArrayList.get(index)` for random access on a 50,000-element list. `ArrayList.add(0, x)` is ~117x slower than `LinkedList.addFirst(x)` for front-insertion. → [Internal Implementation](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#internal-implementation) has the full trace.

## 5. Trade-offs

`ArrayList` gives O(1) indexed access and better cache locality at the cost of O(n) arbitrary insertion; `LinkedList` gives O(1) head/tail insertion at the cost of O(n) indexed access and worse cache locality. → [Trade-offs](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#trade-offs).

## 6. Interview questions

1. Your team switched a list to LinkedList for flexibility and a hot path got slower. Why?
2. Is LinkedList.addFirst() really O(1) regardless of list size? What about LinkedList.add(k, x) for an arbitrary k?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#interview-questions).

## 7. Common mistakes

Choosing LinkedList "for flexibility" without checking the access pattern; assuming LinkedList's O(1) insertion applies at an arbitrary index; not sizing ArrayList's initial capacity when the final size is known. → [Common Mistakes](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#common-mistakes).

## 8. Staff-level discussion

A Big-O claim about a data structure is always scoped to a specific operation and starting condition — generalizing past that scope produces exactly the kind of well-intentioned-but-wrong refactor this chapter measures. → [Staff-Level Discussion](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#interview-answer-framework).

## 9. Summary

ArrayList grows by ~1.5x, confirmed via reflection. LinkedList's indexed access is ~320x slower than ArrayList's; ArrayList's front-insertion is ~117x slower than LinkedList's — both measured directly. → [Summary](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#practice-exercises) and [Solutions](../../syllabus/02-java/collections/arraylist-and-linkedlist-internals.md#solutions). Reproducible demos: `practice/java/week-14/arraylist-linkedlist/src/`.

## 14. Additional Reading

- JDK `List` interface documentation

## 15. Official References

- [java.util.ArrayList (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ArrayList.html)
- [java.util.LinkedList (Java 21 API)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/LinkedList.html)
