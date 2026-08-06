---
title: "Flashcards: Kafka Consumer Groups, Rebalancing, and Offset Management"
slug: consumer-groups-and-rebalancing
document_type: flashcard-deck
domain: kafka
topic_id: T-703
canonical: ../handbook/kafka/consumer-groups-and-rebalancing.md
last_updated: 2026-08-06
---

# Flashcards: Kafka Consumer Groups, Rebalancing, and Offset Management

**Canonical chapter:** [`handbook/kafka/consumer-groups-and-rebalancing.md`](../handbook/kafka/consumer-groups-and-rebalancing.md)

## Card: Exclusive partition assignment

**Prompt:**
Can two consumers in the same group read the same partition simultaneously?

**Answer:**
No — exactly one consumer per partition per group at a time.

**Why it matters:**
The core guarantee that makes consumer-group parallelism coherent.

**Common trap:**
Assuming partitions can be shared for extra throughput within a group.

**Related:**
[Core Concepts](../handbook/kafka/consumer-groups-and-rebalancing.md#core-concepts)

## Card: Most common cause of repeated rebalancing

**Prompt:**
What's the most common cause of a group rebalancing repeatedly?

**Answer:**
`max.poll.interval.ms` violations from slow per-batch processing, evicting a live-but-slow consumer.

**Why it matters:**
The standard deep-dive question for this topic; most candidates guess "networking" first.

**Common trap:**
Assuming a networking issue before checking processing time per poll.

**Related:**
[Production Scenarios](../handbook/kafka/consumer-groups-and-rebalancing.md#production-scenarios)

## Card: Consumer parallelism ceiling

**Prompt:**
What caps consumer parallelism for one topic?

**Answer:**
The partition count — extra consumers beyond it sit idle.

**Why it matters:**
Ties consumer-side scaling back to a topic-creation-time decision.

**Common trap:**
Assuming adding consumers always increases throughput.

**Related:**
[Interview Questions](../handbook/kafka/consumer-groups-and-rebalancing.md#interview-questions), Question 2
