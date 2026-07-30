---
title: "T-618 · Distributed Transactions: Saga, Outbox, 2PC"
topic_id: T-618
domain: DistributedData
tier: Staff
iwi: 7.65
prerequisites: [T-504, T-505, T-809, T-704]
unlocks: []
week: 10
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/distributed-transactions-saga-and-outbox.md
---

# T-618 · Distributed Transactions: Saga, Outbox, 2PC

**IWI 7.65 · Staff tier · the convergence point of three earlier threads (W3 transactions, W5 idempotency, W8 Kafka)**

**Canonical chapter:** [Distributed Transactions: Saga, Outbox, and 2PC](../../handbook/system-design/distributed-transactions-saga-and-outbox.md). This file is the Week 10 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `09-week-10-mock-architecture-round.md` cites §3/§4 directly, and `05-zero-downtime-migration.md` references this file's dual-write framing.

**Verification note:** every trace behind this summary is real, executed output from `practice/java/week-10/outbox-publisher/` against a live Postgres 16 (Docker) and single-broker KRaft Kafka cluster — a genuine dual-write failure, and a genuine working transactional outbox with crash recovery. The full working implementation is walked through in `08-outbox-implementation-deliverable.md`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The dual-write hazard, reproduced](#3-the-dual-write-hazard-reproduced)
4. [The transactional outbox, working](#4-the-transactional-outbox-working)
5. [Saga: the multi-service version of the same problem](#5-saga-the-multi-service-version-of-the-same-problem)
6. [Why 2PC is avoided](#6-why-2pc-is-avoided)
7. [Trade-offs](#7-trade-offs)
8. [Interview questions](#8-interview-questions)
9. [Common mistakes](#9-common-mistakes)
10. [Staff-level discussion](#10-staff-level-discussion)
11. [Summary](#11-summary)
12. [Key Takeaways](#12-key-takeaways)
13. [Cheat Sheet](#13-cheat-sheet)
14. [Flashcards](#14-flashcards)
15. [Practice Exercises](#15-practice-exercises)
16. [Additional Reading](#16-additional-reading)
17. [Official References](#17-official-references)

---

## 1. The concept

A dual write updates two independent systems as one logical unit with no shared transaction. The transactional outbox, Saga, and 2PC are three different answers to getting atomicity-like guarantees across such systems. → [Definition and Purpose](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#definition-and-purpose).

## 2. Why it exists

This topic is the convergence point of Week 3's `@Transactional` semantics, Week 5's idempotency, and Week 8's Kafka delivery semantics — closing T-704's own named gap that Kafka's exactly-once doesn't extend to an external database write. → [Definition and Purpose](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#definition-and-purpose).

## 3. The dual-write hazard, reproduced

Measured: an order committed to Postgres, then a simulated crash before the Kafka publish call — the order survives, but zero events were ever published, with nothing anywhere to retry. → [Internal Implementation](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#internal-implementation) has the full trace.

## 4. The transactional outbox, working

Measured: 3 orders written atomically with their outbox rows; a poller crash right after publishing row 1 but before marking it published; restart redelivers row 1 (a real duplicate) and publishes 2 and 3. Result: 3 orders, 4 messages, zero lost — at-least-once, not exactly-once. → [Internal Implementation](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#internal-implementation) has the full crash-recovery sequence.

## 5. Saga: the multi-service version of the same problem

Orchestration (central coordinator, easier debugging) vs. choreography (no central dependency, implicit flow). Compensating actions are forward-moving business operations (a refund), never a cross-service rollback. → [Core Concepts](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#core-concepts).

## 6. Why 2PC is avoided

Every participant holds its lock across the coordinator's round trip; a coordinator crash mid-protocol leaves a "prepared" participant stuck indefinitely — the in-doubt transaction problem, an availability cost most systems can't accept. → [Core Concepts](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#core-concepts).

## 7. Trade-offs

Dual writes are simplest but lose events measurably; the outbox eliminates loss at the cost of at-least-once delivery and new poller infrastructure; Sagas trade central visibility against implicit choreographed flow; 2PC gives true atomicity at a real availability cost. → [Trade-offs](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#trade-offs).

## 8. Interview questions

1. You wrote to the DB and published to Kafka. Prove no message is lost.
2. Compensate a charged payment.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#interview-questions).

## 9. Common mistakes

Believing a DB write plus a message publish can be made atomic without an outbox; treating Saga compensation as a rollback; forgetting the outbox's at-least-once guarantee requires an idempotent consumer. → [Common Mistakes](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#common-mistakes).

## 10. Staff-level discussion

Prefer at-least-once with idempotency over attempting exactly-once through coordination — the outbox's weaker guarantee with a strictly better availability profile is the pattern that recurs across distributed systems generally. → [Staff-Level Discussion](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#interview-answer-framework).

## 11. Summary

A plain dual write measurably loses events on a crash between the two writes. The transactional outbox eliminates that loss at the cost of at-least-once delivery, demonstrated with a real crash-recovery run producing one genuine duplicate and zero losses. → [Summary](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#summary).

## 12. Key Takeaways

→ [Key Takeaways](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#key-takeaways).

## 13. Cheat Sheet

→ [Cheat Sheet](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#cheat-sheet).

## 14. Flashcards

→ [Flashcards](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 15. Practice Exercises

→ [Practice Exercises](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#practice-exercises) and [Solutions](../../handbook/system-design/distributed-transactions-saga-and-outbox.md#solutions). Reproducible demo: `practice/java/week-10/outbox-publisher/`.

## 16. Additional Reading

- [microservices.io — Pattern: Transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html)

## 17. Official References

- [Debezium documentation — Outbox Event Router](https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html) — the CDC-based alternative to the polling publisher built in this chapter
