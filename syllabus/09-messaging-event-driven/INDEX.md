---
title: "Messaging & Event-Driven Systems — Domain Index"
document_type: syllabus-domain-index
domain: 09-messaging-event-driven
status: 9 of 9 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Messaging & Event-Driven Systems

Kafka mechanics plus the event-driven/event-sourcing/CDC chapters previously split across `architecture/` and `system-design/` — consolidated here since they are one coherent topic area, not three unrelated ones.

> **Phase 3 update (2026-09-03).** This domain's full existing content (9 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 9 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a post-office-bins analogy for partitions and keys, a certified-mail analogy for `acks`/idempotence, a restaurant-waitstaff analogy for consumer groups, a to-do-list-checkbox analogy for delivery semantics, a single-lane-conveyor-belt analogy for consumer lag and DLQs, a shared-paper-form analogy for schema compatibility, a security-camera-vs-clerk analogy for CDC vs. outbox plus a ticket-queue-vs-radio-broadcast analogy for point-to-point vs. pub-sub, a checkbook-register analogy for event sourcing, and a group-dinner-planning analogy for choreography vs. orchestration). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`09-messaging-event-driven` is now fully L1–L4 (9/9)** — the seventh fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-701/T-702/T-703/T-704/T-705 | Kafka Architecture Fundamentals — Topics, Partitions, Replication | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md` |
| T-702/T-705 | Kafka Producer Semantics: acks, Idempotence, and Partition Key Design | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md` |
| T-703 | Kafka Consumer Groups, Rebalancing, and Offset Management | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/consumer-groups-and-rebalancing.md` |
| T-704 | Kafka Delivery Semantics and Exactly-Once Processing | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/delivery-semantics-and-exactly-once.md` |
| T-707 | Consumer Lag, Backpressure, and DLQ Strategy | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md` |
| T-708 | Schema Registry and Compatibility Evolution | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/schema-registry-and-compatibility-evolution.md` |
| T-710 | Messaging Patterns and Change Data Capture (CDC) | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md` |
| T-905 | Event Sourcing and Its Real Costs | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/event-sourcing-and-its-real-costs.md` |
| T-906 | Event-Driven Architecture: Integration Styles, Choreography, and Orchestration | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/09-messaging-event-driven/event-driven-architecture-integration-styles.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
