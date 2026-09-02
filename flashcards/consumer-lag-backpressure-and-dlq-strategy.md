---
title: "Flashcards: Consumer Lag, Backpressure, and DLQ Strategy"
slug: consumer-lag-backpressure-and-dlq-strategy
document_type: flashcard-deck
domain: kafka
topic_id: T-707
canonical: ../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md
last_updated: 2026-09-02
---

# Flashcards: Consumer Lag, Backpressure, and DLQ Strategy

**Canonical chapter:** [`handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md`](../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md)

## Card: Why does one bad message block the whole partition?

**Prompt:**
Why can a single unprocessable message halt processing of every message behind it on the same Kafka partition?

**Answer:**
Because a partition is a strictly ordered log and a consumer cannot skip past an uncommitted offset without explicit action — this is the direct, unavoidable cost of the ordering guarantee that makes Kafka predictable, not a bug.

**Why it matters:**
It's the register's own named follow-up question, and measured directly in this chapter: 4 of 10 messages processed, lag stuck at 6, across 5 real retry rounds.

**Common trap:**
Treating this as a surprising defect rather than the expected consequence of ordering.

**Related:**
[handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md](../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md)

## Card: Does adding consumers always help?

**Prompt:**
Does adding more consumer instances to a group always increase processing parallelism?

**Answer:**
No. Kafka assigns each partition to at most one consumer per group — real parallelism is capped at partition count. This chapter measured it directly: 3 partitions, 5 consumers, exactly 2 sitting completely idle.

**Why it matters:**
It's the register's own named misconception, and a real, costly on-call mistake (scaling consumers instead of checking partition count first) documented in this chapter's production scenario.

**Common trap:**
Assuming more replicas always means more throughput, without reference to partition count.

**Related:**
[handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md](../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md)

## Card: Why is an explicit seek() needed after dead-lettering?

**Prompt:**
After committing an offset past a dead-lettered message, why might the consumer still stall?

**Answer:**
Because `poll()` sets the consumer's real fetch position at fetch time — advancing past the whole batch immediately — not per-record as the caller iterates it. Committing an offset only updates committed-offset metadata; it does not move the consumer's actual position. An explicit `seek()` is required.

**Why it matters:**
This is a real bug this chapter's own practice code hit and fixed while being built — not a hypothetical gotcha.

**Common trap:**
Assuming `commitSync()` alone is sufficient to make the consumer continue from the right place.

**Related:**
[handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md](../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md)
