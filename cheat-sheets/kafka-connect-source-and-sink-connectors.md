---
title: "Cheat Sheet: Kafka Connect — Source and Sink Connectors"
slug: kafka-connect-source-and-sink-connectors
document_type: cheat-sheet
domain: 09-messaging-event-driven
topic_id: T-2408
canonical: ../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md
last_updated: 2026-09-11
---

# Kafka Connect: Source and Sink Connectors

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md`](../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md)

## Core Mental Model

Kafka Connect turns "build an integration between Kafka and an external system" into "configure an existing, reusable worker with a connector-specific config file" — offset tracking, retries, scaling, and fault tolerance live once in the framework, not re-implemented per integration.

## Essential Definitions

- **Source connector** — reads an external system into Kafka.
- **Sink connector** — writes Kafka into an external system.
- **Standalone mode** — single process, local/dev use only.
- **Distributed mode** — real production fault tolerance and scaling across a worker cluster.

## Decision Table

| Need | Concept/Config |
|---|---|
| Read an external system into Kafka | A source connector |
| Write Kafka into an external system | A sink connector |
| Control on-the-wire data format | `key.converter`/`value.converter` |
| Simple, single-process, local/dev use | Standalone mode |
| Real production fault tolerance and scaling | Distributed mode |
| Control crash-recovery redundancy window | `offset.flush.interval.ms` |
| Make a connector jar discoverable (standalone) | `plugin.path` |

## Common Pitfalls

- Running standalone mode in production — no fault tolerance or scaling, offsets stored in a local file.
- Assuming a connector needs custom offset-tracking code — that's exactly what the framework handles generically.
- Ignoring `offset.flush.interval.ms`'s trade-off (larger window = less overhead, more potential reprocessing on crash).

## Interview Answer Skeleton

**30-sec:** Kafka Connect is a framework that generalizes offset tracking, retries, and fault tolerance across any source/sink integration, configured rather than hand-coded per connector.

**2-min:** Add: real evidence shows a kill-and-restart of a Connect worker resuming from exactly the last durably-committed offset — zero data loss, zero duplication — with zero custom producer/consumer code, the concrete mechanism behind Connect's fault tolerance.

**Staff-level framing:** Standalone mode is a real, deliberate trade — good for local development, structurally wrong for production, since it has no distributed fault tolerance at all.

## Related

- syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md
- syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md
