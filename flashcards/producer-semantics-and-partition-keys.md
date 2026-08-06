---
title: "Flashcards: Kafka Producer Semantics: acks, Idempotence, and Partition Key Design"
slug: producer-semantics-and-partition-keys
document_type: flashcard-deck
domain: kafka
topic_id: T-702/T-705
canonical: ../handbook/kafka/producer-semantics-and-partition-keys.md
last_updated: 2026-08-06
---

# Flashcards: Kafka Producer Semantics: acks, Idempotence, and Partition Key Design

**Canonical chapter:** [`handbook/kafka/producer-semantics-and-partition-keys.md`](../handbook/kafka/producer-semantics-and-partition-keys.md)

## Card: Why acks=all alone isn't enough

**Prompt:**
Why isn't `acks=all` alone sufficient for durability?

**Answer:**
It only waits for the current ISR, which can shrink to a single replica; pair it with `min.insync.replicas` for a real guarantee.

**Why it matters:**
The named interview trap for this topic.

**Common trap:**
Believing `acks=all` is unconditionally durable.

**Related:**
[Internal Implementation](../handbook/kafka/producer-semantics-and-partition-keys.md#internal-implementation)

## Card: What idempotent producers dedupe

**Prompt:**
What does an idempotent producer deduplicate?

**Answer:**
Its own retried sends to Kafka (via PID + partition + sequence number) — not consumer-side duplicate processing.

**Why it matters:**
Prevents overclaiming "Kafka is exactly-once" from this mechanism alone.

**Common trap:**
Conflating idempotent producers with end-to-end exactly-once.

**Related:**
[Core Concepts](../handbook/kafka/producer-semantics-and-partition-keys.md#core-concepts)

## Card: Sticky partitioner behavior

**Prompt:**
What does the sticky partitioner do with a null key?

**Answer:**
Batches records onto one partition per in-flight batch (not strict round-robin) to maximize batch size and throughput.

**Why it matters:**
Corrects an outdated round-robin expectation from older Kafka documentation.

**Common trap:**
Expecting strict round-robin distribution for unkeyed records.

**Related:**
[Historical Context](../handbook/kafka/producer-semantics-and-partition-keys.md#historical-context)
