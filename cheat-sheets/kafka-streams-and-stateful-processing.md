---
title: "Cheat Sheet: Kafka Streams and Stateful Processing"
slug: kafka-streams-and-stateful-processing
document_type: cheat-sheet
domain: 09-messaging-event-driven
topic_id: T-709
canonical: ../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md
last_updated: 2026-09-11
---

# Kafka Streams and Stateful Stream Processing

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md`](../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md)

## Core Mental Model

Kafka Streams turns a sequence of topics into a graph of local, in-process transformations — `KStream` for independent events, `KTable` for continuously-updated per-key state — backing every stateful operation with a real, automatically-managed, compacted changelog topic instead of a hand-built persistence layer.

## Essential Definitions

- **`KStream<K, V>`** — an unbounded stream of independent events.
- **`KTable<K, V>`** — a continuously-updated, latest-value-per-key view, durable via a compacted changelog topic.
- **Repartitioning** — a real re-shuffle across partitions, triggered when a `.groupBy` changes the key.

## Decision Table

| Need | API/Concept |
|---|---|
| Unbounded stream of independent events | `KStream<K, V>` |
| Continuously-updated latest-value-per-key view | `KTable<K, V>` |
| Re-key before aggregating | `.groupBy((k, v) -> newKey)` (real repartition if key changes) |
| Materialize a `KTable`'s state explicitly | `Materialized.as(...)` |
| Inspect the real topology before deploying | `topology.describe()` |
| Stronger-than-default processing guarantee | `processing.guarantee=exactly_once_v2` |
| Verify a `KTable`'s changelog is really compacted | `kafka-topics.sh --describe` on `<app.id>-<store>-changelog` |

## Common Pitfalls

- Assuming a `KTable`'s state is only in-memory — it's backed by a real, compacted Kafka changelog topic, verifiable directly with `kafka-topics.sh --describe`.
- Not accounting for repartitioning cost when a `.groupBy` changes the key.
- Deploying without inspecting `topology.describe()` first — the real topology often differs from a mental model built purely from the DSL code.

## Interview Answer Skeleton

**30-sec:** Kafka Streams turns topics into a local processing graph — `KStream` for events, `KTable` for per-key state, backed by an automatically-managed compacted changelog for real fault tolerance.

**2-min:** Add: real evidence verifies via `kafka-topics.sh --describe` that a `KTable`'s auto-created changelog topic genuinely carries `cleanup.policy=compact` with zero manual configuration — a `KTable`'s durability is a direct, concrete application of log compaction.

**Staff-level framing:** Kafka Streams' fault tolerance is built directly on Kafka's own existing durability primitives (compacted topics), not a separate system bolted on top — know this when arguing for or against adopting it versus a separate stream-processing framework.

## Related

- syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md
- syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md
