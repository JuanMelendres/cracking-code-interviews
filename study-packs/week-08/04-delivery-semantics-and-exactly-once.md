---
title: "T-704 · Delivery Semantics & Exactly-Once Processing"
topic_id: T-704
domain: Kafka
tier: Advanced
iwi: 8.00
prerequisites: [T-701, T-702, T-703]
unlocks: [T-809]
week: 8
last_reviewed: 2026-07-30
canonical: ../../handbook/kafka/delivery-semantics-and-exactly-once.md
---

# T-704 · Delivery Semantics & Exactly-Once Processing

**IWI 8.00 · Advanced tier · highest-weighted topic in this week's cluster**

**Canonical chapter:** [Kafka Delivery Semantics and Exactly-Once Processing](../../handbook/kafka/delivery-semantics-and-exactly-once.md). This file is the Week 8 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the duplicate-processing and lost-processing traces behind this summary are real, executed output from `practice/java/week-08/kafka/src/DeliverySemanticsDemo.java` — actual offset commits against a live broker, not a simulated description.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [At-least-once and at-most-once, traced](#3-at-least-once-and-at-most-once-traced)
4. [Is exactly-once real?](#4-is-exactly-once-real)
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

Delivery semantics describe how many times a consumer can guarantee a record gets processed, given that "commit the offset" and "process the record" are two separate, independently-interruptible operations. → [Definition and Purpose](../../handbook/kafka/delivery-semantics-and-exactly-once.md#definition-and-purpose).

## 2. Why it exists

A consumer can't atomically process a record and record that it did so, as one step against two different systems, without extra coordination. Delivery semantics is the vocabulary for what happens to that gap when a crash lands inside it. → [Definition and Purpose](../../handbook/kafka/delivery-semantics-and-exactly-once.md#definition-and-purpose).

## 3. At-least-once and at-most-once, traced

Measured: commit-after-processing (at-least-once) produced 36 deliveries for 18 unique records after a simulated crash — duplicates, never loss. Commit-before-processing (at-most-once) produced 0 records actually processed out of 18 — silent loss, never duplicates. No ordering of just those two steps avoids both. → [Internal Implementation](../../handbook/kafka/delivery-semantics-and-exactly-once.md#internal-implementation) has both full traces.

## 4. Is exactly-once real?

Yes, but scoped narrowly: Kafka's exactly-once semantics covers the transactional read-process-write loop entirely *within* Kafka. It does NOT cover a write to an external system (a database, an HTTP call) — that gap needs a transactional outbox or an idempotent consumer. → [Core Concepts](../../handbook/kafka/delivery-semantics-and-exactly-once.md#core-concepts).

## 5. Trade-offs

Commit-after risks duplicates (usually the safer default); commit-before risks silent loss; Kafka's transactional EOS is real but Kafka-to-Kafka only; an idempotent consumer closes the external-system gap. → [Trade-offs](../../handbook/kafka/delivery-semantics-and-exactly-once.md#trade-offs).

## 6. Interview questions

1. Is exactly-once real? Explain precisely what Kafka provides and what it doesn't.
2. Consumer crashes after processing but before committing. What happens, and how do you make that safe?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/kafka/delivery-semantics-and-exactly-once.md#interview-questions).

## 7. Common mistakes

Believing Kafka provides end-to-end exactly-once by default; choosing at-most-once without a deliberate reason; treating redelivery under at-least-once as a bug rather than a condition to design for. → [Common Mistakes](../../handbook/kafka/delivery-semantics-and-exactly-once.md#common-mistakes).

## 8. Staff-level discussion

The commit-vs-process ordering problem is a specific instance of the general dual-write problem — every dual-write is choosing one of duplication, loss, or a coordinating mechanism, explicitly or not. → [Staff-Level Discussion](../../handbook/kafka/delivery-semantics-and-exactly-once.md#interview-answer-framework).

## 9. Summary

At-least-once and at-most-once are two sides of the same coin. Kafka's exactly-once is real but scoped to Kafka-to-Kafka; external systems need an outbox or idempotent consumer. → [Summary](../../handbook/kafka/delivery-semantics-and-exactly-once.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/kafka/delivery-semantics-and-exactly-once.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/kafka/delivery-semantics-and-exactly-once.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/kafka/delivery-semantics-and-exactly-once.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/kafka/delivery-semantics-and-exactly-once.md#practice-exercises) and [Solutions](../../handbook/kafka/delivery-semantics-and-exactly-once.md#solutions). Reproducible demo: `practice/java/week-08/kafka/src/DeliverySemanticsDemo.java`. Also the primary source for `05-kafka-guarantees-deliverable.md`'s "delivery semantics" row.

## 14. Additional Reading

- [Kafka documentation — Semantics of exactly-once](https://kafka.apache.org/documentation/#semantics)

## 15. Official References

- [KIP-98 — Exactly Once Delivery and Transactional Messaging](https://cwiki.apache.org/confluence/display/KAFKA/KIP-98+-+Exactly+Once+Delivery+and+Transactional+Messaging)
