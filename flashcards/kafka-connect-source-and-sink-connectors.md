---
title: "Flashcards: Kafka Connect — Source and Sink Connectors"
slug: kafka-connect-source-and-sink-connectors
document_type: flashcard-deck
domain: 09-messaging-event-driven
topic_id: T-2408
canonical: ../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md
last_updated: 2026-09-11
---

# Flashcards: Kafka Connect — Source and Sink Connectors

**Canonical chapter:** [`syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md`](../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md)

## Card: Source vs. sink

**Prompt:**
What's the difference between a Kafka Connect source connector and a sink connector?

**Answer:**
A source connector reads an external system's data into Kafka topics. A sink connector writes Kafka topic data out to an external system.

**Why it matters:**
The two-directional vocabulary Kafka Connect is built around.

**Common trap:**
Mixing up which direction "source" and "sink" refer to.

**Related:**
[Kafka Connect: Source and Sink Connectors](../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md)

## Card: The real fault-tolerance mechanism

**Prompt:**
What happens if a Kafka Connect worker crashes mid-stream and restarts?

**Answer:**
Real, demonstrated evidence: it resumes from exactly the last durably-committed offset, with zero data loss and zero duplication — with no custom producer/consumer code written by the integration author.

**Why it matters:**
The concrete mechanism behind "Kafka Connect handles fault tolerance for you," not just an assertion.

**Common trap:**
Assuming fault tolerance requires connector-specific recovery code.

**Related:**
[Kafka Connect: Source and Sink Connectors](../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md)

## Card: Standalone vs. distributed mode

**Prompt:**
Why is standalone mode wrong for production use?

**Answer:**
Standalone mode runs as a single process with offsets stored in a local file — no distributed fault tolerance or scaling. Distributed mode runs across a worker cluster with real fault tolerance, the correct choice for production.

**Why it matters:**
A real, common misconfiguration risk if standalone mode is used past initial local development.

**Common trap:**
Treating standalone mode as "the simple production option" rather than a dev-only convenience.

**Related:**
[Kafka Connect: Source and Sink Connectors](../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md)
