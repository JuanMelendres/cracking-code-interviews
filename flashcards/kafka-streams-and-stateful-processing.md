---
title: "Flashcards: Kafka Streams and Stateful Processing"
slug: kafka-streams-and-stateful-processing
document_type: flashcard-deck
domain: 09-messaging-event-driven
topic_id: T-709
canonical: ../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md
last_updated: 2026-09-11
---

# Flashcards: Kafka Streams and Stateful Processing

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md`](../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md)

## Card: KStream vs. KTable

**Prompt:**
What's the difference between a `KStream` and a `KTable`?

**Answer:**
`KStream` is an unbounded stream of independent events — every record matters on its own. `KTable` is a continuously-updated, latest-value-per-key view — a new record for an existing key replaces the previous value, like a row update.

**Why it matters:**
The foundational vocabulary distinction the whole Kafka Streams DSL is built on.

**Common trap:**
Treating every stream in Kafka Streams as a `KStream`, missing when per-key "current state" (a `KTable`) is the correct model.

**Related:**
[Kafka Streams and Stateful Processing](../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md)

## Card: Where a KTable's state actually lives

**Prompt:**
Is a `KTable`'s state only held in local memory?

**Answer:**
No — real, verified evidence (via `kafka-topics.sh --describe`) shows it's backed by an automatically-created, compacted Kafka changelog topic (`cleanup.policy=compact`), giving it real crash recovery without hand-built persistence code.

**Why it matters:**
Directly connects `KTable`'s durability to log compaction — not a separate mechanism.

**Common trap:**
Assuming `KTable` state is lost on application restart, or that its persistence requires custom code.

**Related:**
[Kafka Streams and Stateful Processing](../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md)

## Card: When repartitioning happens

**Prompt:**
Does every `.groupBy()` call in Kafka Streams trigger a real repartition?

**Answer:**
It triggers a real repartition specifically when the grouping changes the key — the data must be re-shuffled across partitions so all records for a given new key land on the same partition/task.

**Why it matters:**
A real, non-obvious cost worth knowing when reasoning about a topology's actual runtime behavior.

**Common trap:**
Assuming `.groupBy()` is always "free" the way an in-memory `groupBy` in application code might feel.

**Related:**
[Kafka Streams and Stateful Processing](../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md)
