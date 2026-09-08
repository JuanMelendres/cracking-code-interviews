---
title: "Mid → Senior, Week 6 — Messaging and Event-Driven Systems"
document_type: study-pack
week: 6
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 6 — Messaging and Event-Driven Systems

## Weekly Outcome

By the end of this week you can explain Kafka's partition-and-offset model well enough to reason about ordering guarantees, and correctly design for exactly-once-equivalent processing using idempotent consumers rather than assuming Kafka provides it for free.

## Why This Week Matters

This is the first genuinely distributed-systems-flavored domain in this pack — Kafka's failure modes (rebalances, redelivery, lag) are a direct preview of Week 7's broader distributed-systems material.

## Prerequisites

Week 1 (concurrency) and Week 4 (databases) — consumer-group coordination and the outbox pattern both assume solid grounding in both.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | Kafka Architecture Fundamentals |
| Thu–Sat | Kafka Delivery Semantics and Exactly-Once Processing |
| Sun | Review checklist below |

## Required Reading

[`syllabus/09-messaging-event-driven/INDEX.md`](../../../syllabus/09-messaging-event-driven/INDEX.md) — this week's two priority topics (Kafka Architecture Fundamentals; Kafka Delivery Semantics and Exactly-Once Processing).

## Hands-On Exercises

Real demos exist under [`practice/java/kafka/`](../../../practice/java/kafka/) — `messaging-patterns-point-to-point-vs-pubsub/`, `consumer-lag-backpressure-and-dlq-strategy/`, `schema-registry-and-compatibility-evolution/`. Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`kafka-consumer-group-rebalance-storm.md`](../../../production-cookbook/kafka-consumer-group-rebalance-storm.md)
- [`duplicate-payment-charge-from-kafka-redelivery.md`](../../../production-cookbook/duplicate-payment-charge-from-kafka-redelivery.md)

## Interview Answer Drills

Answer, out loud: "what guarantees ordering within a Kafka topic, and what breaks it?" and "how do you achieve exactly-once-equivalent processing when Kafka itself only guarantees at-least-once delivery?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described duplicate-processing incident (e.g., the redelivery cookbook entry), propose an idempotent-consumer design, out loud, in under 5 minutes.

## Review Checklist

- [ ] Completed both chapters' own L3 Mastery Checklists.
- [ ] Reproduced at least 2 of the real Kafka demos listed above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can explain partition-level ordering and consumer-group rebalancing unprompted.
- [ ] Can design an idempotent consumer for a stated duplicate-delivery scenario.
- [ ] Can state the difference between at-least-once, at-most-once, and exactly-once-equivalent processing.

## Retrospective

Note whether you previously assumed Kafka guarantees exactly-once delivery by default — this misconception is common enough that both this week's cookbook entries exist because of it.

## Next Week

[Week 7 — Distributed Systems](../week-07/README.md).
