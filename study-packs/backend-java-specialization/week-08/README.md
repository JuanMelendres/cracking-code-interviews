---
title: "Backend Java Specialization, Week 8 — Messaging and Event-Driven Systems"
document_type: study-pack
week: 8
track: backend-java-specialization
status: draft
estimated_hours: 9
---

# Week 8 — Messaging and Event-Driven Systems

## Weekly Outcome

By the end of this week you can explain Kafka's partition/replication architecture, producer `acks`/idempotence/partition-key design, consumer groups and rebalancing, delivery semantics and exactly-once processing, consumer lag/backpressure/DLQ strategy, schema evolution, CDC versus outbox patterns, and the real costs of event sourcing and choreography-versus-orchestration integration styles.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) places Messaging after Databases because Kafka's own delivery-semantics and exactly-once chapters (T-704) build directly on Week 7's isolation-level and locking vocabulary, and the outbox pattern (T-710) is meaningless without Week 6's transactional-write reasoning already in place.

## Prerequisites

Weeks 6–7 complete, in particular T-611 (Isolation Levels) and the transactional-write concepts from T-601/T-602 (JPA Entity Lifecycle) — the outbox pattern this week's CDC chapter covers exists specifically to solve the dual-write problem those chapters describe.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | Kafka Architecture Fundamentals (T-701/T-702/T-703/T-704/T-705) |
| Wed | Kafka Producer Semantics: acks, Idempotence, and Partition Key Design (T-702/T-705) |
| Thu | Kafka Consumer Groups, Rebalancing, and Offset Management (T-703); Kafka Delivery Semantics and Exactly-Once Processing (T-704) |
| Fri | Consumer Lag, Backpressure, and DLQ Strategy (T-707); Schema Registry and Compatibility Evolution (T-708) |
| Sat | Messaging Patterns and Change Data Capture (T-710) |
| Sun | Event Sourcing and Its Real Costs (T-905); Event-Driven Architecture: Integration Styles (T-906); review checklist below |

## Required Reading

The full Messaging & Event-Driven Systems domain, per [`syllabus/09-messaging-event-driven/INDEX.md`](../../../syllabus/09-messaging-event-driven/INDEX.md) (the exhaustive, canonical source) — all 9 topics.

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-701/T-702/T-703/T-704/T-705 — Kafka Architecture Fundamentals — Topics, Partitions, Replication | [`kafka-architecture-fundamentals.md`](../../../syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md) |
| 2 | T-702/T-705 — Kafka Producer Semantics: acks, Idempotence, and Partition Key Design | [`producer-semantics-and-partition-keys.md`](../../../syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md) |
| 3 | T-703 — Kafka Consumer Groups, Rebalancing, and Offset Management | [`consumer-groups-and-rebalancing.md`](../../../syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md) |
| 4 | T-704 — Kafka Delivery Semantics and Exactly-Once Processing | [`delivery-semantics-and-exactly-once.md`](../../../syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md) |
| 5 | T-707 — Consumer Lag, Backpressure, and DLQ Strategy | [`consumer-lag-backpressure-and-dlq-strategy.md`](../../../syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md) |
| 6 | T-708 — Schema Registry and Compatibility Evolution | [`schema-registry-and-compatibility-evolution.md`](../../../syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md) |
| 7 | T-710 — Messaging Patterns and Change Data Capture (CDC) | [`messaging-patterns-and-change-data-capture.md`](../../../syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md) |
| 8 | T-905 — Event Sourcing and Its Real Costs | [`event-sourcing-and-its-real-costs.md`](../../../syllabus/09-messaging-event-driven/event-sourcing-and-its-real-costs.md) |
| 9 | T-906 — Event-Driven Architecture: Integration Styles, Choreography, and Orchestration | [`event-driven-architecture-integration-styles.md`](../../../syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md) |

## Hands-On Exercises

Real, executed demos exist for all 9 chapters:

- [`practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java`](../../../practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java) (T-701–T-705, Kafka Architecture Fundamentals and Producer Semantics — against a real single-broker KRaft cluster)
- [`practice/java/week-08/kafka/src/ConsumerGroupDemo.java`](../../../practice/java/week-08/kafka/src/ConsumerGroupDemo.java) (T-703)
- [`practice/java/week-08/kafka/src/DeliverySemanticsDemo.java`](../../../practice/java/week-08/kafka/src/DeliverySemanticsDemo.java) (T-704)
- [`practice/java/kafka/consumer-lag-backpressure-and-dlq-strategy/`](../../../practice/java/kafka/consumer-lag-backpressure-and-dlq-strategy/) (T-707)
- [`practice/java/kafka/schema-registry-and-compatibility-evolution/`](../../../practice/java/kafka/schema-registry-and-compatibility-evolution/) (T-708)
- [`practice/sql/cdc-via-logical-replication/`](../../../practice/sql/cdc-via-logical-replication/) and [`practice/java/kafka/messaging-patterns-point-to-point-vs-pubsub/`](../../../practice/java/kafka/messaging-patterns-point-to-point-vs-pubsub/) (T-710)
- [`practice/java/architecture/event-sourcing-and-its-real-costs/`](../../../practice/java/architecture/event-sourcing-and-its-real-costs/) (T-905)
- [`practice/java/architecture/event-driven-integration-styles/`](../../../practice/java/architecture/event-driven-integration-styles/) (T-906)

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "what does `acks=all` actually protect against, and what does it not protect against if the ISR shrinks to one replica?" and "why can Kafka only guarantee ordering within a single partition, not across a topic?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

[Architecture Atlas: Notification System](../../../architecture-atlas/notification-system.md) — a real design entry built around this week's own domain (its own prerequisites list T-704, Delivery Semantics and Exactly-Once, directly). Work through it after finishing T-704 and T-707, since it exercises exactly-once delivery and consumer-lag reasoning against a concrete multi-channel notification scenario.

## Behavioral Exercise

None this week.

## Mock Interview

[Mock Interview: Kafka Messaging Technical Round](../../../practice/mock-interviews/kafka-messaging-technical-round.md) — a real 45-minute Senior/Staff mock covering per-partition ordering, the `acks=all` data-loss mechanism, repeated-rebalance diagnosis, and honest exactly-once scoping, with candidate/evaluator sections hard-separated and a scoring rubric. This mock predates this study pack (elevated from the original `study-packs/week-08/` interview-emergency-sprint material); it is cited here, not re-verified.

## Review Checklist

- [ ] Completed all 9 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 6 of the 8 real demo locations listed above.
- [ ] Can explain, unprompted, why the outbox pattern solves the dual-write problem that a naive "write to DB, then publish to Kafka" sequence does not.

## Completion Criteria

- [ ] Can explain the trade-off between `acks=0`, `acks=1`, and `acks=all` with a concrete failure scenario for each.
- [ ] Can name at least two causes of consumer-group rebalancing and one mitigation for each.
- [ ] Can state event sourcing's real operational costs (replay time, schema evolution of events, snapshotting) rather than only its benefits.
- [ ] Completed the Notification System design exercise above and can defend its delivery-semantics choice.

## Retrospective

Note whether "exactly-once" claims in your own past work were actually exactly-once end to end, or only exactly-once at the Kafka broker level with duplicate risk still present downstream — this honest-scoping distinction is exactly what the Kafka mock interview above evaluates.

## Next Week

[Week 9 — Performance and JVM Tuning](../week-09/README.md).
