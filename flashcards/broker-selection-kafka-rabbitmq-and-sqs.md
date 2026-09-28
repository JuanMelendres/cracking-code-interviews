---
title: "Flashcards: Broker Selection — Kafka vs. RabbitMQ vs. SQS"
slug: broker-selection-kafka-rabbitmq-and-sqs
document_type: flashcard-deck
domain: 09-messaging-event-driven
topic_id: T-2433
canonical: ../syllabus/09-messaging-event-driven/broker-selection-kafka-rabbitmq-and-sqs.md
last_updated: 2026-09-28
---

# Flashcards: Broker Selection — Kafka vs. RabbitMQ vs. SQS

**Canonical chapter:** [`syllabus/09-messaging-event-driven/broker-selection-kafka-rabbitmq-and-sqs.md`](../syllabus/09-messaging-event-driven/broker-selection-kafka-rabbitmq-and-sqs.md)

## Card: The root difference

**Prompt:**
What single architectural difference explains nearly every behavioral difference between Kafka and RabbitMQ/SQS?

**Answer:**
Log versus queue. Kafka retains messages for a retention period and each consumer group tracks its own offset. A queue deletes a message once it is acknowledged. Replay, multi-consumer fan-out, backlog behavior, ordering granularity, and per-message retry all follow from this.

**Why it matters:**
It turns "it depends" into a specific, defensible comparison.

**Common trap:**
Framing the choice as throughput ("RabbitMQ for low volume, Kafka for high"), which is a caricature rather than a model difference.

**Related:**
[Kafka Architecture Fundamentals](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md)

## Card: The decisive selection question

**Prompt:**
What is the highest-signal question when choosing a broker?

**Answer:**
"Does any consumer need to re-read messages it already processed?" Replay exists only on a log. A consumer bug requiring three days of reprocessing is a new consumer group plus an offset reset on Kafka; on RabbitMQ or SQS the messages are gone unless you independently archived them.

**Why it matters:**
This question is usually answered *no* by assumption at design time and *yes* at incident time.

**Common trap:**
Forgetting that retention is the real guarantee — Kafka with 24-hour retention is in exactly the same position as a queue for a 3-day incident.

**Related:**
[Retention, Log Compaction, and Tiered Storage](../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md)

## Card: When Kafka is the wrong choice

**Prompt:**
Name a concrete workload that fits Kafka badly, with the mechanism.

**Answer:**
A job queue with independent tasks of wildly different durations — for example thumbnails where 4K video takes 40x longer than a photo. Consumption is partition-bound, so one slow message stalls its whole partition while consumers on other partitions idle. Queues let any free worker take the next task. Per-message retry, delay, and dead-lettering are also native in queues and hand-built on Kafka, and consumer parallelism on Kafka is capped by partition count.

**Why it matters:**
Being able to argue against the fashionable choice is the judgment signal the question is testing.

**Common trap:**
Answering only "Kafka is complex" with no workload-specific failure.

**Related:**
[Consumer Lag, Backpressure, and DLQ Strategy](../syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md)

## Card: Ordering guarantees, precisely

**Prompt:**
State the ordering guarantee of each broker.

**Answer:**
Kafka: ordered **within a partition**, with the partition chosen by message key — never globally across a topic. RabbitMQ: ordered from a single queue to a **single** consumer; adding a consumer for throughput breaks it immediately. SQS standard: **no** ordering guarantee; SQS FIFO: ordered within a **message group ID**, with a documented throughput ceiling.

**Why it matters:**
"Kafka guarantees ordering" without the partition qualifier is one of the most common wrong answers in messaging interviews.

**Common trap:**
Creating one partition per entity to preserve per-entity order — the key-based partitioner already does that with a sane partition count.

**Related:**
[Producer Semantics and Partition Keys](../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md)

## Card: Exactly-once, scoped honestly

**Prompt:**
What does each broker's strongest delivery guarantee actually cover?

**Answer:**
Kafka's transactional producer plus `read_committed` consumers gives exactly-once for read-process-write **within Kafka** — it does not extend to a database write unless that write goes through an outbox. SQS FIFO gives exactly-once processing within a five-minute deduplication window keyed by a deduplication ID. RabbitMQ offers publisher confirms and consumer acknowledgements, not distributed exactly-once. All three default to at-least-once.

**Why it matters:**
Every exactly-once feature is scoped more narrowly than its name, so consumers must be idempotent regardless of broker.

**Common trap:**
Presenting Kafka's exactly-once as end-to-end across your database.

**Related:**
[Delivery Semantics and Exactly-Once](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md)
