---
title: "Backend Java Specialization, Week 8 — Manifest"
week: 8
track: backend-java-specialization
last_reviewed: 2026-09-08
---

# Week 8 — Manifest

**Domain:** Messaging & Event-Driven Systems. **Topics:** 9, verified against [`syllabus/09-messaging-event-driven/INDEX.md`](../../../syllabus/09-messaging-event-driven/INDEX.md) as of this pack's construction (2026-09-08) — matches the learning path's own stated count exactly. **Track:** Backend Java Specialization, Week 8 of 9.
**Files:** 1 (+ this manifest) — no chapter content duplicated.

## Files

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | Weekly outcome, schedule, required reading, hands-on exercises, system design exercise, mock interview, review checklist |

## Verification

All 9 chapters predate this study pack's construction. Practice paths confirmed to exist on disk at manifest write time (2026-09-08), each verified against the specific path each chapter's own "Provenance" note cites; internal assertion/output claims were not re-executed here:

| Practice path | Confirmed on disk | Paired topic |
|---|---|---|
| `practice/java/week-08/kafka/src/ProducerPartitionKeyDemo.java` | Yes | T-701–T-705 |
| `practice/java/week-08/kafka/src/ConsumerGroupDemo.java` | Yes | T-703 |
| `practice/java/week-08/kafka/src/DeliverySemanticsDemo.java` | Yes | T-704 |
| `practice/java/kafka/consumer-lag-backpressure-and-dlq-strategy/` | Yes | T-707 |
| `practice/java/kafka/schema-registry-and-compatibility-evolution/` | Yes | T-708 |
| `practice/sql/cdc-via-logical-replication/`, `practice/java/kafka/messaging-patterns-point-to-point-vs-pubsub/` | Yes | T-710 |
| `practice/java/architecture/event-sourcing-and-its-real-costs/` | Yes | T-905 |
| `practice/java/architecture/event-driven-integration-styles/` | Yes | T-906 |

Two additional cross-references confirmed to exist on disk, cited but not re-verified in content:

| File | Confirmed on disk | Role |
|---|---|---|
| `architecture-atlas/notification-system.md` | Yes — its own front matter lists T-704 (Delivery Semantics and Exactly-Once) as a direct prerequisite | This week's System Design Exercise |
| `practice/mock-interviews/kafka-messaging-technical-round.md` | Yes — front matter shows it predates this pack (elevated from `study-packs/week-08/`, last updated 2026-08-11) | This week's Mock Interview |

## Scope note

This is the only week in the pack with a real System Design Exercise, per the task's own guidance that one only makes sense here — Messaging is the one domain in this path with a directly matching, pre-existing Architecture Atlas entry and mock interview, found by grepping the Atlas for references to `09-messaging-event-driven/` rather than assumed.

## Integrity note

No new technical content was authored beyond scheduling and review structure. No assertion counts are cited, since practice demos, the Atlas entry, and the mock interview were not re-executed or re-verified in content during this pack's construction.
