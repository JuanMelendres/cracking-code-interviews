---
title: "T-807 · CAP Theorem and Consistency Models"
topic_id: T-807
domain: System Design
tier: Advanced
iwi: 7.10
prerequisites: []
unlocks: [T-908]
week: 5
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/cap-theorem-and-consistency-models.md
---

# T-807 · CAP Theorem and Consistency Models

**IWI 7.10 · Advanced tier**

**Canonical chapter:** [CAP Theorem and Consistency Models](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md). This file is the Week 5 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `09-design-exercise-payment-processing.md` cites §5 directly.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [What a system actually gives up during a partition](#3-what-a-system-actually-gives-up-during-a-partition)
4. [Eventual vs. strong consistency, for the user](#4-eventual-vs-strong-consistency-for-the-user)
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

CAP states that during an actual network partition, a distributed system must choose between Consistency and Availability — it cannot have both while the partition lasts. Outside a partition, both are achievable. → [Definition and Purpose](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#definition-and-purpose).

## 2. Why it exists

CAP corrects the naive intuition that a system can simply be "consistent and available" as a permanent goal, forcing the honest question of which guarantee is relaxed when the network genuinely fails to deliver a message. → [Definition and Purpose](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#definition-and-purpose).

## 3. What a system actually gives up during a partition

A CP system (`etcd`, `ZooKeeper`) refuses requests it can't guarantee are current on the minority side — giving up availability. An AP system (DNS, a shopping cart) keeps serving both sides, accepting disagreement until reconciliation — giving up consistency. The Staff-level answer always names the actual system and the specific guarantee relaxed. → [Core Concepts](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#core-concepts).

## 4. Eventual vs. strong consistency, for the user

Strong consistency: no stale reads ever, at a latency/availability cost. Eventual consistency: a temporary staleness window, invisible for a likes counter, consequential for "did my payment go through" — connecting directly to idempotency (`02-idempotency.md`). → [Core Concepts](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#core-concepts).

## 5. Trade-offs

CP costs some requests failing outright during a partition, right for ledgers and configuration stores; AP costs possibly-stale data, right for social feeds and shopping carts. → [Trade-offs](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#trade-offs).

## 6. Interview questions

1. CAP — what does a system actually give up during a partition? Be specific about your own system.
2. What is the difference between eventual and strong consistency for the user?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#interview-questions).

## 7. Common mistakes

Treating CAP as a permanent trade-off rather than partition-specific; answering "what does your system give up" in the abstract; assuming eventual consistency is uniformly acceptable system-wide. → [Common Mistakes](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#common-mistakes).

## 8. Staff-level discussion

The most sophisticated version of this topic recognizes that different data within the same system legitimately warrants different consistency models — mirroring the caching chapter's "partition by staleness tolerance" lesson. → [Staff-Level Discussion](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#interview-answer-framework).

## 9. Summary

CAP is a statement about partition behavior specifically. The Staff-level answer names the actual system and user-facing consequence, and assesses consistency per data type rather than uniformly. → [Summary](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#practice-exercises) and [Solutions](../../syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md#solutions).

## 14. Additional Reading

- Eric Brewer, ["CAP Twelve Years Later: How the 'Rules' Have Changed"](https://www.infoq.com/articles/cap-twelve-years-later-how-the-rules-have-changed/)

## 15. Official References

- Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 9 "Consistency and Consensus," pp. 321–345
