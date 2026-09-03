---
title: "T-702 / T-705 · Producer Semantics & Partition Key Design"
topic_id: T-702
domain: Kafka
tier: Advanced
iwi: 7.40
prerequisites: [T-701]
unlocks: [T-704]
week: 8
last_reviewed: 2026-07-30
canonical: ../../handbook/kafka/producer-semantics-and-partition-keys.md
---

# T-702 / T-705 · Producer Semantics & Partition Key Design

**IWI 7.40 (T-702) / 7.55 (T-705) · Advanced tier**

**Canonical chapter:** [Kafka Producer Semantics: acks, Idempotence, and Partition Key Design](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md). This file is the Week 8 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the partition-routing behavior and idempotent-producer configuration behind this summary are real, executed output from `practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java` against a live single-broker KRaft cluster.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Partition key design, traced](#3-partition-key-design-traced)
4. [`acks`, idempotence, and what "durable" actually means](#4-acks-idempotence-and-what-durable-actually-means)
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

A producer decides two independent things per record: which partition (the partitioner, driven by the key) and how durably it's written before the call returns (`acks`) — frequently conflated in interview answers. → [Definition and Purpose](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#definition-and-purpose).

## 2. Why it exists

Without a deliberate partitioning strategy, related records could land anywhere, breaking per-entity ordering. Without a durability contract, a producer can't know whether a "successful" send survived a broker crash. → [Definition and Purpose](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#definition-and-purpose).

## 3. Partition key design, traced

Measured: with no key, the sticky partitioner (default since KIP-480) batches all records onto one partition per in-flight batch, not strict round-robin. Partition key choice is a permanent ordering commitment — too coarse a key causes a hot partition, too fine a key buys no real ordering guarantee. → [Historical Context](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#historical-context), [Core Concepts](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#core-concepts).

## 4. `acks`, idempotence, and what "durable" actually means

`acks=all` waits for the current ISR, not `replication.factor` — needs `min.insync.replicas` to mean anything. Idempotent producers (the modern default) dedupe producer-side retries via a PID and sequence number — they say nothing about consumer-side duplicate processing. → [Core Concepts](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#core-concepts), [Internal Implementation](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#internal-implementation) has the measured config dump.

## 5. Trade-offs

`acks=0` is fastest with no durability; `acks=all` + `min.insync.replicas≥2` survives single-broker failure at higher latency; idempotence costs essentially nothing. → [Trade-offs](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#trade-offs).

## 6. Interview questions

1. `acks=all` and you still lost a message. How?
2. What does the idempotent producer actually prevent, and what does it NOT prevent?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#interview-questions).

## 7. Common mistakes

Believing `acks=all` alone guarantees no data loss; assuming idempotent producers make the whole pipeline exactly-once; choosing a partition key for entities that don't need relative ordering. → [Common Mistakes](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#common-mistakes).

## 8. Staff-level discussion

`acks` and `min.insync.replicas` together express an explicit CAP-style trade — the same shape of decision as a quorum-write setting in Cassandra or DynamoDB. → [Staff-Level Discussion](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#interview-answer-framework).

## 9. Summary

Producer durability (`acks`, `min.insync.replicas`), producer-retry deduplication (idempotence), and partition-key choice are three separate mechanisms commonly conflated in interview answers. → [Summary](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#practice-exercises) and [Solutions](../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md#solutions). Reproducible demo: `practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java`. Also the producer-side source for `05-kafka-guarantees-deliverable.md`.

## 14. Additional Reading

- [Kafka documentation — Producer configs](https://kafka.apache.org/documentation/#producerconfigs)

## 15. Official References

- [KIP-98 — Exactly Once Delivery and Transactional Messaging](https://cwiki.apache.org/confluence/display/KAFKA/KIP-98+-+Exactly+Once+Delivery+and+Transactional+Messaging)
- [KIP-480 — Sticky Partitioner](https://cwiki.apache.org/confluence/display/KAFKA/KIP-480%3A+Sticky+Partitioner)
