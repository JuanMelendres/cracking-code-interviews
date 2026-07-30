---
title: "T-703 · Consumer Groups, Rebalancing & Offset Management"
topic_id: T-703
domain: Kafka
tier: Advanced
iwi: 7.50
prerequisites: [T-701]
unlocks: [T-704]
week: 8
last_reviewed: 2026-07-30
canonical: ../../handbook/kafka/consumer-groups-and-rebalancing.md
---

# T-703 · Consumer Groups, Rebalancing & Offset Management

**IWI 7.50 · Advanced tier**

**Canonical chapter:** [Kafka Consumer Groups, Rebalancing, and Offset Management](../../handbook/kafka/consumer-groups-and-rebalancing.md). This file is the Week 8 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the assignment trace behind this summary is real, executed output from `practice/java/week-08/kafka/src/ConsumerGroupDemo.java` against a live single-broker KRaft cluster with a 4-partition topic and 18 records.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A rebalance, traced](#3-a-rebalance-traced)
4. [Rebalance cost: eager vs cooperative-incremental](#4-rebalance-cost-eager-vs-cooperative-incremental)
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

A consumer group is a set of consumers sharing a `group.id`, cooperatively consuming a topic so each partition is assigned to exactly one consumer within the group at a time. → [Definition and Purpose](../../handbook/kafka/consumer-groups-and-rebalancing.md#definition-and-purpose).

## 2. Why it exists

A single consumer caps read throughput at one process. Groups split partitions for parallel consumption, and rebalance automatically so scaling or failure needs no manual reassignment. → [Definition and Purpose](../../handbook/kafka/consumer-groups-and-rebalancing.md#definition-and-purpose).

## 3. A rebalance, traced

Measured: `consumer-1` joins alone and gets all 4 partitions; `consumer-2` joins and a rebalance splits the assignment; `consumer-2` correctly processes zero *new* records because the backlog was already committed by `consumer-1` — offset commits working as intended, not a bug. → [Internal Implementation](../../handbook/kafka/consumer-groups-and-rebalancing.md#internal-implementation) has the full trace.

## 4. Rebalance cost: eager vs cooperative-incremental

Eager rebalancing (the original protocol) revokes every partition from every consumer on any membership change; cooperative-incremental (`CooperativeStickyAssignor`, KIP-429) only revokes the partitions that actually move. The most common cause of a group rebalancing every 30 seconds: `max.poll.interval.ms` violations from slow processing, not networking. → [Historical Context](../../handbook/kafka/consumer-groups-and-rebalancing.md#historical-context), [Core Concepts](../../handbook/kafka/consumer-groups-and-rebalancing.md#core-concepts).

## 5. Trade-offs

More consumers means more parallelism, capped at partition count; cooperative-incremental narrows the blast radius but doesn't eliminate the underlying cause. → [Trade-offs](../../handbook/kafka/consumer-groups-and-rebalancing.md#trade-offs).

## 6. Interview questions

1. Your consumer group rebalances every 30 seconds. Diagnose it.
2. Add consumers beyond the partition count. What happens?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/kafka/consumer-groups-and-rebalancing.md#interview-questions).

## 7. Common mistakes

Assuming a rebalance means something is broken; adding consumers past partition count expecting more throughput; accidentally committing offsets before processing completes. → [Common Mistakes](../../handbook/kafka/consumer-groups-and-rebalancing.md#common-mistakes).

## 8. Staff-level discussion

Consumer-group rebalancing is a specific instance of cooperative work assignment under dynamic membership — the Staff-level distinction is separating scaling, crashes, and `max.poll.interval.ms` violations as three different causes with three different fixes. → [Staff-Level Discussion](../../handbook/kafka/consumer-groups-and-rebalancing.md#interview-answer-framework).

## 9. Summary

Consumer groups split partitions across members, one per consumer at a time, rebalancing automatically on membership change. Recurring rebalances almost always trace back to `max.poll.interval.ms` violations, not the rebalance mechanism itself. → [Summary](../../handbook/kafka/consumer-groups-and-rebalancing.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/kafka/consumer-groups-and-rebalancing.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/kafka/consumer-groups-and-rebalancing.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/kafka/consumer-groups-and-rebalancing.md#flashcards). Full week-level deck: `06-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/kafka/consumer-groups-and-rebalancing.md#practice-exercises) and [Solutions](../../handbook/kafka/consumer-groups-and-rebalancing.md#solutions). Reproducible demo: `practice/java/week-08/kafka/src/ConsumerGroupDemo.java`.

## 14. Additional Reading

- [Kafka documentation — Consumer configs](https://kafka.apache.org/documentation/#consumerconfigs)

## 15. Official References

- [KIP-429 — Kafka Consumer Incremental Rebalance Protocol](https://cwiki.apache.org/confluence/display/KAFKA/KIP-429%3A+Kafka+Consumer+Incremental+Rebalance+Protocol)
