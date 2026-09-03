---
title: "Flashcards: Kafka Architecture Fundamentals — Topics, Partitions, Replication"
slug: kafka-architecture-fundamentals
document_type: flashcard-deck
domain: kafka
topic_id: T-701
canonical: ../handbook/kafka/kafka-architecture-fundamentals.md
last_updated: 2026-08-06
---

# Flashcards: Kafka Architecture Fundamentals — Topics, Partitions, Replication

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md`](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md)

## Card: What Kafka guarantees about ordering

**Prompt:**
What does Kafka guarantee about record ordering?

**Answer:**
Total order within a single partition only; no ordering guarantee across partitions or across the topic as a whole.

**Why it matters:**
The single most consequential and most commonly wrong assumption in this domain.

**Common trap:**
Assuming Kafka provides global, topic-wide ordering.

**Related:**
[Core Concepts](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md#core-concepts)

## Card: Why changing partition count is dangerous

**Prompt:**
Why is changing partition count on a keyed topic dangerous?

**Answer:**
It changes every key's `hash(key) % partitionCount` mapping, silently remapping and breaking existing per-key ordering for any data already in the topic.

**Why it matters:**
Makes partition count a one-way door, not a tunable capacity lever.

**Common trap:**
Treating partition count as freely adjustable for scaling.

**Related:**
[Production Scenarios](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md#production-scenarios)

## Card: ISR vs replication.factor

**Prompt:**
What does `acks=all` actually wait for — `replication.factor` replicas, or something else?

**Answer:**
The current In-Sync Replica set (ISR), which can be smaller than `replication.factor` if followers have fallen behind and been dropped.

**Why it matters:**
`acks=all` alone is not a durability guarantee without `min.insync.replicas` also enforced.

**Common trap:**
Assuming `replication.factor=3` means three replicas always ack a write.

**Related:**
[Core Concepts](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md#core-concepts)
