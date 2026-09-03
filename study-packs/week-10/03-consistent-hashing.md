---
title: "T-806 · Data Partitioning & Consistent Hashing"
topic_id: T-806
domain: DistributedData
tier: Staff
iwi: 7.70
prerequisites: [T-614]
unlocks: []
week: 10
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/data-partitioning-and-consistent-hashing.md
---

# T-806 · Data Partitioning & Consistent Hashing

**IWI 7.70 · Staff tier**

**Canonical chapter:** [Data Partitioning and Consistent Hashing](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md). This file is the Week 10 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `10-design-exercise-distributed-cache.md` cites §6 Q2 directly, and `02-sharding-and-partitioning-strategies.md` references this file's measurement.

**Verification note:** the redistribution percentages behind this summary are real, executed output from `practice/java/week-10/consistent-hashing/src/ConsistentHashingDemo.java` — 10,000 real keys, 10 real nodes, one real removal, measured directly, not approximated.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Naive hash % N vs consistent hashing, measured](#3-naive-hash--n-vs-consistent-hashing-measured)
4. [Virtual nodes: why 150, not 1](#4-virtual-nodes-why-150-not-1)
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

Consistent hashing maps nodes and keys onto the same ring; adding/removing a node only affects keys immediately adjacent to it on the ring, unlike `hash(key) % N`. → [Definition and Purpose](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#definition-and-purpose).

## 2. Why it exists

Any hash-based distribution scheme needs to handle nodes being added or removed — the question is how much data has to move, and the naive answer is bad enough to matter directly. → [Definition and Purpose](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#definition-and-purpose).

## 3. Naive hash % N vs consistent hashing, measured

Measured: removing 1 of 10 nodes remapped 92.5% of keys under naive `hash % N`, versus 9.2% under consistent hashing with 150 virtual nodes per physical node (theoretical ideal ~10%). → [Internal Implementation](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#internal-implementation) has the full measured output.

## 4. Virtual nodes: why 150, not 1

One point per physical node gives uneven load distribution by chance; 150 virtual nodes per physical node (1,500 total points) converges each node's share close to an even 1/N via the law of large numbers. → [Core Concepts](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#core-concepts).

## 5. Trade-offs

`hash % N` is trivial but remaps nearly everything on any change; consistent hashing with virtual nodes gives even distribution close to ideal at the cost of ring memory and more hash computations. → [Trade-offs](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#trade-offs).

## 6. Interview questions

1. Add a node — how much data moves?
2. Your shard key is the timestamp. What breaks?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#interview-questions).

## 7. Common mistakes

Believing rebalancing is routine and cheap regardless of hashing scheme; using too few virtual nodes; conflating rebalancing-cost solved with hot-key distribution solved — they're different problems. → [Common Mistakes](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#common-mistakes).

## 8. Staff-level discussion

The 92.5%-vs-9.2% gap is the concrete justification for why every major distributed data system uses consistent hashing rather than naive modulo hashing — this is a number worth having memorized precisely, derivable from first principles. → [Staff-Level Discussion](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#interview-answer-framework).

## 9. Summary

Naive `hash % N` remaps nearly all keys on any node-count change — mathematically expected, not a rare edge case. Consistent hashing with virtual nodes bounds remapping to roughly 1/N. → [Summary](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#practice-exercises) and [Solutions](../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md#solutions). Reproducible demo: `practice/java/week-10/consistent-hashing/src/ConsistentHashingDemo.java`.

## 14. Additional Reading

- [Karger et al. — Consistent Hashing and Random Trees (1997), the original paper](https://www.akamai.com/site/en/documents/technical-publication/consistent-hashing-and-random-trees-distributed-caching-protocols-for-relieving-hot-spots-on-the-world-wide-web-technical-publication.pdf)

## 15. Official References

- [Amazon DynamoDB paper (2007) §4.2 — Partitioning](https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf) — a production system built directly on consistent hashing with virtual nodes
