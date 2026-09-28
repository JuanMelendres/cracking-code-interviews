---
title: "Cheat Sheet: Broker Selection — Kafka vs. RabbitMQ vs. SQS"
slug: broker-selection-kafka-rabbitmq-and-sqs
document_type: cheat-sheet
domain: 09-messaging-event-driven
topic_id: T-2433
canonical: ../syllabus/09-messaging-event-driven/broker-selection-kafka-rabbitmq-and-sqs.md
last_updated: 2026-09-28
---

# Broker Selection: Kafka vs. RabbitMQ vs. SQS

**Canonical chapter:** [`syllabus/09-messaging-event-driven/broker-selection-kafka-rabbitmq-and-sqs.md`](../syllabus/09-messaging-event-driven/broker-selection-kafka-rabbitmq-and-sqs.md)

## Core Mental Model

A log keeps the data and gives each reader a bookmark. A queue hands the data out and deletes it. Everything else follows from that one difference.

## Decision Order

1. **Does anything need to re-read processed messages?** Yes → Kafka. Highest-signal question.
2. **Do multiple independent systems need every message with independent progress?** Yes → Kafka, or broker-side fan-out (RabbitMQ exchange, SNS → SQS).
3. **Ordering, at what granularity?** Per key → Kafka partitions or SQS FIFO message groups. Global ordering is unrealistic anywhere.
4. **Is it really a work queue** (independent tasks, variable durations, one bad task must not block others)? Yes → queue.
5. **Broker-side routing rules needed?** Yes → RabbitMQ.
6. **Who operates it?** Small team, AWS, modest volume → SQS. This can override 3–5.
7. **What do you already run?** A second broker is a permanent cost.

## Comparison

| Dimension | Kafka | RabbitMQ | SQS |
|---|---|---|---|
| Model | Log | Queue + exchanges | Managed queue |
| Retained after read | Yes (retention policy) | No | No |
| Replay | Offset reset | No | No |
| Ordering | Per partition, by key | Per queue, single consumer | None / per message group (FIFO) |
| Routing in broker | No | Rich | No |
| Per-message retry/delay/DLQ | Hand-built | Native | Native (visibility timeout, redrive) |
| Consumer parallelism | Capped by partition count | Unbounded | Unbounded |
| Backlog | Just data within retention | Operational concern | Managed |
| Ops burden | High self-managed | Moderate | ~None |

## Common Pitfalls

- Describing Kafka instead of comparing it when asked "why Kafka?".
- Claiming Kafka guarantees global ordering (it is per partition).
- Treating Kafka's exactly-once as end-to-end — it covers read-process-write **within Kafka**, not your database write.
- Kafka as a job queue: one slow message blocks its whole partition while other consumers idle.
- Forgetting consumer parallelism is capped by partition count.
- Assuming retention covers the incident window — with 24-hour retention you are in the same position as a queue.

## Interview Answer Skeleton

**30-sec:** Kafka is a retained, partitioned log with per-consumer-group offsets, so replay and independent multi-consumer fan-out are native. RabbitMQ and SQS are queues: deleted on acknowledgement, with native per-message retry, delay, and dead-lettering, plus (RabbitMQ) broker-side routing. Log when history has value; queue when the unit of work is a task.

**Killer example:** a consumer bug needing three days of reprocessing is an offset reset on Kafka and unrecoverable on a queue unless you independently archived the messages.

## Production Warning Signs

- Rising consumer lag on one partition only → a hot key or a slow message class.
- Queue depth rising with age-of-oldest-message flat → throughput problem, not a poison message.
- Retries with no backoff in front of a degraded dependency → amplification.

## Related

- [Kafka Architecture Fundamentals](../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md)
- [Delivery Semantics and Exactly-Once](../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md)
- [Consumer Lag, Backpressure, and DLQ Strategy](../syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md)
