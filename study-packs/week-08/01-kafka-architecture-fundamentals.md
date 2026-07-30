---
title: "T-701 · Kafka Architecture Fundamentals"
topic_id: T-701
domain: Kafka
tier: Core
iwi: 6.40
prerequisites: []
unlocks: [T-702, T-703, T-704, T-705]
week: 8
last_reviewed: 2026-07-30
canonical: ../../handbook/kafka/kafka-architecture-fundamentals.md
---

# T-701 · Kafka Architecture Fundamentals

**IWI 6.40 · Core tier**

**Canonical chapter:** [Kafka Architecture Fundamentals — Topics, Partitions, Replication](../../handbook/kafka/kafka-architecture-fundamentals.md). This file is the Week 8 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every partition/broker fact behind this summary is exercised directly by the demos in `practice/java/week-08/kafka/` (real single-broker KRaft cluster, 4-partition topic) — see `MANIFEST.md` for exact reproduce commands. Replication/ISR mechanics are described from the blueprint (`00-project/knowledge-architecture-blueprint.md` §5.8) since the practice environment runs a single broker and cannot itself demonstrate a multi-broker ISR shrink/expand; that gap is stated explicitly rather than faked.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Topics, partitions, and what "ordering" actually means](#3-topics-partitions-and-what-ordering-actually-means)
4. [Replication and the ISR](#4-replication-and-the-isr)
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

A topic is split into partitions, each an independent, strictly-ordered log with its own leader and follower replicas. → [Definition and Purpose](../../handbook/kafka/kafka-architecture-fundamentals.md#definition-and-purpose).

## 2. Why it exists

A single log caps throughput at one machine; splitting into partitions parallelizes reads and writes, at the cost of only guaranteeing order within a partition — the most consequential, most commonly misunderstood fact in this domain. → [Definition and Purpose](../../handbook/kafka/kafka-architecture-fundamentals.md#definition-and-purpose).

## 3. Topics, partitions, and what "ordering" actually means

Measured: the same key always lands on the same partition in strict order; different keys spread across partitions with no relative ordering guarantee. Partition count is effectively immutable once a keyed topic is live — changing it remaps every key's assignment. → [Core Concepts](../../handbook/kafka/kafka-architecture-fundamentals.md#core-concepts), [Internal Implementation](../../handbook/kafka/kafka-architecture-fundamentals.md#internal-implementation) has the full trace.

## 4. Replication and the ISR

`acks=all` waits for the current ISR, not `replication.factor` — a shrunk ISR means fewer replicas need to ack a write. Unclean leader election trades data loss for availability. → [Core Concepts](../../handbook/kafka/kafka-architecture-fundamentals.md#core-concepts).

## 5. Trade-offs

More partitions means more parallelism but more rebalance cost and file-handle pressure; higher `replication.factor` survives more failures at more disk/network cost. → [Trade-offs](../../handbook/kafka/kafka-architecture-fundamentals.md#trade-offs).

## 6. Interview questions

1. Does Kafka guarantee ordering?
2. One partition is taking 60% of the traffic. What's happening and how do you fix it?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/kafka/kafka-architecture-fundamentals.md#interview-questions).

## 7. Common mistakes

Believing Kafka provides global ordering; treating more partitions as a free scalability lever; assuming `replication.factor=3` means three replicas are always available. → [Common Mistakes](../../handbook/kafka/kafka-architecture-fundamentals.md#common-mistakes).

## 8. Staff-level discussion

Partition count is one of the few genuinely irreversible decisions in a Kafka deployment for a keyed topic — the same category as a database shard key. → [Staff-Level Discussion](../../handbook/kafka/kafka-architecture-fundamentals.md#interview-answer-framework).

## 9. Summary

Kafka splits a topic into independently-ordered partitions to parallelize throughput; ordering is guaranteed only within a partition. Replication with an ISR provides durability, but `acks=all` is only as strong as the current ISR. → [Summary](../../handbook/kafka/kafka-architecture-fundamentals.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/kafka/kafka-architecture-fundamentals.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/kafka/kafka-architecture-fundamentals.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/kafka/kafka-architecture-fundamentals.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/kafka/kafka-architecture-fundamentals.md#practice-exercises). Reproducible demo: `practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java`.

## 14. Additional Reading

- [Kafka documentation — Design](https://kafka.apache.org/documentation/#design)

## 15. Official References

- [KIP-500 — Replace ZooKeeper with a Self-Managed Metadata Quorum (KRaft)](https://cwiki.apache.org/confluence/display/KAFKA/KIP-500) — the mode this week's practice cluster runs in
