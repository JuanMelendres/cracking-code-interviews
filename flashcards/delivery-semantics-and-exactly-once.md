---
title: "Flashcards: Kafka Delivery Semantics and Exactly-Once Processing"
slug: delivery-semantics-and-exactly-once
document_type: flashcard-deck
domain: kafka
topic_id: T-704
canonical: ../handbook/kafka/delivery-semantics-and-exactly-once.md
last_updated: 2026-08-06
---

# Flashcards: Kafka Delivery Semantics and Exactly-Once Processing

**Canonical chapter:** [`syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md`](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md)

## Card: What causes at-least-once duplicates

**Prompt:**
What causes at-least-once duplicate processing?

**Answer:**
Committing the offset AFTER processing; a crash between processing and commit causes redelivery.

**Why it matters:**
The standard, generally-correct default — but only safe if processing is idempotent.

**Common trap:**
Treating redelivery as a bug rather than expected behavior to design for.

**Related:**
[Internal Implementation](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md#internal-implementation)

## Card: What causes at-most-once loss

**Prompt:**
What causes at-most-once silent loss?

**Answer:**
Committing the offset BEFORE processing; a crash after commit but before processing means that record is never retried.

**Why it matters:**
Explains why at-most-once is rarely the right default.

**Common trap:**
Choosing at-most-once to "avoid duplicates" without recognizing the loss risk it trades in.

**Related:**
[Internal Implementation](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md#internal-implementation)

## Card: Scope of Kafka's exactly-once

**Prompt:**
Does Kafka's exactly-once cover a write to an external database?

**Answer:**
No — only the Kafka-to-Kafka transactional read-process-write loop; external writes need an outbox or idempotent consumer.

**Why it matters:**
The precise, scoped answer to this project's own discriminating interview question.

**Common trap:**
Assuming Kafka's transactional producer/consumer setup alone makes any external write exactly-once.

**Related:**
[Core Concepts](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md#core-concepts)
