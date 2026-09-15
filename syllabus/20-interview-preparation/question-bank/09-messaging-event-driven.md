---
title: "Interview Question Bank — 09-messaging-event-driven"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../09-messaging-event-driven/INDEX.md
  - 08-testing.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Messaging and Event-Driven Architecture

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 12 chapters yielded 24 deep questions + 33
quick-fire questions = **57 real questions**. No Junior Fundamentals chapter exists
in this domain — Kafka/event-driven architecture presupposes backend fundamentals
already covered elsewhere, so no separate leveled-Junior question set applies here.

**Incidental fix while mining this domain:** `schema-registry-and-compatibility-evolution.md`'s
Flashcards section used `## Card:` (heading level 2) instead of the file's own
surrounding `### Card:` convention (heading level 3) for all three of its cards — a
real, verified markdown-hierarchy inconsistency, corrected in the same pass.

---

## Kafka Architecture Fundamentals

### Q1 — Does Kafka guarantee ordering?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/kafka-architecture-fundamentals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers "yes" unconditionally — the single most consequential misconception in this domain, the common mistake this question targets.
- **Senior:** Correctly scopes it to within a single partition only, and names the partition key as the mechanism for achieving per-entity ordering.
- **Staff:** Names the failure mode — changing partition count later remaps every key, silently breaking that guarantee, making partition count a one-way door for keyed topics.

### Q2 — One partition is taking 60% of the traffic. What's happening and how do you fix it?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/kafka-architecture-fundamentals.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Reaches for "add more partitions" without addressing the skew — the common mistake this question targets; new partitions don't help a single hot key.
- **Senior:** Identifies key skew as the cause and proposes a compound key (e.g., `customerId + bucket`) to spread traffic, accepting a weaker ordering guarantee.
- **Staff:** Frames it as a genuine throughput-vs-ordering-granularity trade-off and discusses detecting skew via per-partition throughput metrics before it becomes an incident.

---

## Producer Semantics and Partition Key Design

### Q1 — `acks=all` and you still lost a message. How?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/producer-semantics-and-partition-keys.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Insists `acks=all` is unconditionally durable — the common mistake this question targets.
- **Senior:** Names the ISR-shrink mechanism — the ISR had shrunk to the leader alone at write time, with no `min.insync.replicas` enforcement.
- **Staff:** Explains the resulting availability trade explicitly — enforcing `min.insync.replicas=2` means writes fail during a two-broker outage rather than silently risking data loss.

### Q2 — What does the idempotent producer actually prevent, and what does it NOT prevent?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/producer-semantics-and-partition-keys.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates idempotent producers with "Kafka is exactly-once end to end" — the common mistake this question targets.
- **Senior:** Draws the producer-side vs. consumer-side boundary correctly — prevents duplicate writes from producer retries, doesn't prevent a consumer processing the same record twice.
- **Staff:** Connects it forward to the transactional read-process-write loop as the mechanism that closes the remaining gap within Kafka-to-Kafka pipelines.

---

## Consumer Groups and Rebalancing

### Q1 — Your consumer group rebalances every 30 seconds. Diagnose it.

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/consumer-groups-and-rebalancing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Jumps straight to "must be a network issue" — the common mistake this question targets.
- **Senior:** Names `max.poll.interval.ms` and `max.poll.records` as the levers, and considers reducing batch size or moving heavy work off the poll thread.
- **Staff:** Also considers cooperative-incremental rebalancing to reduce blast radius, and distinguishes this from a `session.timeout.ms`/heartbeat-thread issue.

### Q2 — Add consumers beyond the partition count. What happens?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/consumer-groups-and-rebalancing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes more consumers always means more throughput — the common mistake this question targets.
- **Senior:** States that partition count is the hard ceiling on consumer parallelism for a given topic; extra consumers sit idle.
- **Staff:** Connects this to topic-creation-time sizing decisions — consumer-side scalability for a keyed topic is decided when the topic is created, not at scaling time.

---

## Consumer Lag, Backpressure, and DLQ Strategy

### Q1 — One bad message blocks the partition. Options?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes to simply skip and forget the message with no DLQ — the common mistake this question targets, silently losing it.
- **Senior:** Names bounded retry and dead-letter topic specifically, and explains that the consumer's actual position must be explicitly moved, not just an offset commit.
- **Staff:** Discusses when this trade-off is unacceptable (strict ordering requirements) and proposes alerting plus manual intervention instead.

### Q2 — Does adding more consumers always increase throughput?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes more consumer instances always means more parallel processing — the common mistake this question targets.
- **Senior:** Names the partition-assignment protocol explicitly as the cap, with a concrete example (3 partitions, 5 consumers, 2 idle).
- **Staff:** Connects this to a real incident-response consequence — checking partition count before scaling consumers as a required first step.

---

## Delivery Semantics and Exactly-Once Processing

### Q1 — Is exactly-once real? Explain precisely what Kafka provides and what it doesn't.

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/delivery-semantics-and-exactly-once.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Flatly says "yes, Kafka is exactly-once" or "no such thing, it's marketing" — the common mistake this question targets.
- **Senior:** States the Kafka-to-Kafka transactional scope correctly.
- **Staff:** Proposes the outbox pattern or idempotent-write mechanism unprompted, and explains why an uncoordinated dual-write can never be made safe without one.

### Q2 — Consumer crashes after processing but before committing. What happens, and how do you make that safe?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/delivery-semantics-and-exactly-once.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "the crash is the bug" rather than accepting redelivery as expected — the common mistake this question targets.
- **Senior:** States that at-least-once redelivery is expected on restart, and names idempotency keys/dedupe tables as the general mechanism.
- **Staff:** For genuinely non-idempotent side effects (emails, payments), proposes a durable dedupe check before performing the action.

---

## Kafka Connect — Source and Sink Connectors

### Q1 — A Kafka Connect source connector's worker crashed and restarted. How do you know whether data was lost or duplicated?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes a crash always means data loss — the common mistake this question targets.
- **Senior:** Correctly explains the at-least-once redundant-read behavior around the offset flush interval, and how to check the source system for genuine loss versus expected re-delivery.
- **Staff:** Connects `offset.flush.interval.ms` tuning to a deliberate, workload-specific trade between redundant-read window and flush overhead.

### Q2 — What's the actual difference between a `Converter` and a connector, and why does that distinction matter operationally?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Conflates "the connector" and "the format" as one concept — the common mistake this question targets.
- **Senior:** Correctly defines both and gives a concrete example of the confusion (blaming a connector for a format issue that's actually the converter's).
- **Staff:** Names the reusability implication explicitly — one converter choice applies uniformly across every connector on a worker.

---

## Kafka Streams and Stateful Stream Processing

### Q1 — How does a `KTable`'s state survive an application crash, concretely?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/kafka-streams-and-stateful-processing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes state is only ever in local memory/RocksDB with no durable backing — the common mistake this question targets.
- **Senior:** Correctly names the changelog topic backing the state store, and identifies `cleanup.policy=compact` as keeping replay fast and bounded.
- **Staff:** Connects this to the general pattern (a durable, compacted, replayable current-state topic) as a reusable building block beyond Kafka Streams.

### Q2 — Why did adding a `groupBy` to this topology suddenly add a new internal topic?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/kafka-streams-and-stateful-processing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats repartitioning as a bug or unexpected side effect — the common mistake this question targets.
- **Senior:** Explains the ordering-guarantee reason a repartition is structurally necessary when the grouping key changes.
- **Staff:** Connects this to a real optimization — recognizing when existing source partitioning already matches the intended grouping key, avoiding an unnecessary repartition.

---

## Retention, Log Compaction, and Tiered Storage

### Q1 — How does Kafka Streams rebuild a KTable's state after a crash, without replaying the entire history of every key?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the changelog topic retains full history and restoration is "slow but complete" — the common mistake this question targets.
- **Senior:** Correctly names compaction as the specific mechanism keeping the changelog small enough to replay efficiently.
- **Staff:** Connects this to the general architectural pattern (a durable, compacted, replayable current-state topic) as a reusable building block.

### Q2 — A tombstone was written for a key, but the key reappeared later with an old value. What happened?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as unexpected or broken compaction behavior — the common mistake this question targets.
- **Senior:** Explains this as a direct, expected consequence of compaction's single ordering rule — latest offset per key wins, tombstones included.
- **Staff:** Connects this to a real operational discipline — any producer capable of re-sending stale data for a key needs its own safeguard against undoing an intentional deletion.

---

## Schema Registry and Compatibility Evolution

### Q1 — Under BACKWARD compatibility (the default), is it safe to remove a required field? Is it safe to add one?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Guesses the two directions backward — the common mistake this question targets, one of the most commonly inverted rules in this domain.
- **Senior:** Gets both right — removing is safe; adding is only safe with a default — and explains why in terms of what the reader schema needs.
- **Staff:** Connects the answer to the deploy-order mental model unprompted, and names the exact real error a registry or deserializer would surface.

### Q2 — How would you decide between BACKWARD and FORWARD compatibility for a new topic?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "BACKWARD is the default" as the entire answer — the common mistake this question targets.
- **Senior:** States the deploy-order reasoning correctly — BACKWARD when consumers deploy after producers; FORWARD when producers must move ahead of lagging consumers.
- **Staff:** Names a concrete real scenario where FORWARD or FULL is the better choice, and states the real cost that choice imposes on schema design.

---

## Event-Driven Architecture Integration Styles

### Q1 — "Events decouple services." Is that fully true?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/event-driven-architecture-integration-styles.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "decoupled" as an unqualified true statement — the common mistake this question targets.
- **Senior:** Names both relocation destinations precisely (runtime availability if the event is thin and needs a callback; schema if the event carries the data itself), with an example of each.
- **Staff:** Discusses the organizational form this coupling takes — schema coupling becomes a cross-team schema-governance problem at scale.

### Q2 — How would you debug a choreographed workflow that silently stopped partway through?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/event-driven-architecture-integration-styles.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes "just add more logging" without addressing that logs live in different services with no shared identifier — the common mistake this question targets.
- **Senior:** Names correlation IDs or distributed tracing specifically as the required mechanism, explaining why the call stack alone can't help.
- **Staff:** Frames this as a design-review requirement — every new choreographed workflow must name its tracing strategy up front, not as a reactive fix.

---

## Event Sourcing and Its Real Costs

### Q1 — What's the real cost of event sourcing, beyond its benefits?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/event-sourcing-and-its-real-costs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Answers only with event sourcing's benefits when asked specifically about its costs — the common mistake this question targets.
- **Senior:** Names replay cost specifically and snapshotting as its standard mitigation.
- **Staff:** Adds the schema-evolution cost and connects it to concrete backward/forward compatibility mechanics.

### Q2 — Is event sourcing the same thing as CQRS?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/event-sourcing-and-its-real-costs.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats the two terms as interchangeable — the common mistake this question targets.
- **Senior:** Gives the precise distinction (CQRS separates read/write models; event sourcing is about persistence mechanism) and names an example of using one without the other.
- **Staff:** Explains why the two are frequently adopted together in practice while remaining conceptually independent decisions.

---

## Messaging Patterns and Change Data Capture

### Q1 — How does Change Data Capture actually work?

**Canonical treatment:** [§ Interview Questions, Q1](../../09-messaging-event-driven/messaging-patterns-and-change-data-capture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes CDC as a polling mechanism, or confuses it with the outbox pattern itself — the common mistake this question targets.
- **Senior:** Names the database's own transaction log (WAL) specifically and contrasts it with the outbox pattern's explicit, application-level event write.
- **Staff:** Names the real operational cost (replication-slot lag monitoring) and the scenario where CDC is clearly preferable (many existing write paths).

### Q2 — What's the actual difference between point-to-point and publish-subscribe messaging?

**Canonical treatment:** [§ Interview Questions, Q2](../../09-messaging-event-driven/messaging-patterns-and-change-data-capture.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "consumer group" as synonymous with point-to-point specifically — the common mistake this question targets.
- **Senior:** Correctly and precisely distinguishes both, independent of any specific broker's implementation.
- **Staff:** Notes the two patterns compose freely with either CDC or explicit event-publishing as the source, with a concrete example of when each fits.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Can two consumers in the same group read the same partition simultaneously? | [Consumer Groups and Rebalancing](../../09-messaging-event-driven/consumer-groups-and-rebalancing.md#flashcards) |
| 2 | What's the most common cause of a group rebalancing repeatedly? | [Consumer Groups and Rebalancing](../../09-messaging-event-driven/consumer-groups-and-rebalancing.md#flashcards) |
| 3 | What caps consumer parallelism for one topic? | [Consumer Groups and Rebalancing](../../09-messaging-event-driven/consumer-groups-and-rebalancing.md#flashcards) |
| 4 | Why can a single unprocessable message halt processing of every message behind it on a partition? | [Consumer Lag, Backpressure, and DLQ Strategy](../../09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md#flashcards) |
| 5 | Does adding more consumer instances to a group always increase processing throughput? | [Consumer Lag, Backpressure, and DLQ Strategy](../../09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md#flashcards) |
| 6 | After committing an offset past a dead-lettered message, why might the consumer still reprocess it? | [Consumer Lag, Backpressure, and DLQ Strategy](../../09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md#flashcards) |
| 7 | What causes at-least-once duplicate processing? | [Kafka Delivery Semantics and Exactly-Once Processing](../../09-messaging-event-driven/delivery-semantics-and-exactly-once.md#flashcards) |
| 8 | What causes at-most-once silent loss? | [Kafka Delivery Semantics and Exactly-Once Processing](../../09-messaging-event-driven/delivery-semantics-and-exactly-once.md#flashcards) |
| 9 | Does Kafka's exactly-once cover a write to an external database? | [Kafka Delivery Semantics and Exactly-Once Processing](../../09-messaging-event-driven/delivery-semantics-and-exactly-once.md#flashcards) |
| 10 | "Events decouple services" — true or false, and why? | [Event-Driven Architecture: Integration Styles, Choreography, and Orchestration](../../09-messaging-event-driven/event-driven-architecture-integration-styles.md#flashcards) |
| 11 | Why can't you find the original cause of an event in a choreographed handler's call stack? | [Event-Driven Architecture: Integration Styles, Choreography, and Orchestration](../../09-messaging-event-driven/event-driven-architecture-integration-styles.md#flashcards) |
| 12 | How does this chapter's choreography-vs-orchestration question differ from the Saga chapter's version? | [Event-Driven Architecture: Integration Styles, Choreography, and Orchestration](../../09-messaging-event-driven/event-driven-architecture-integration-styles.md#flashcards) |
| 13 | What real, measurable cost does event sourcing introduce? | [Event Sourcing and Its Real Costs](../../09-messaging-event-driven/event-sourcing-and-its-real-costs.md#flashcards) |
| 14 | Does adding a snapshot change what an event-sourced aggregate's current state is? | [Event Sourcing and Its Real Costs](../../09-messaging-event-driven/event-sourcing-and-its-real-costs.md#flashcards) |
| 15 | Are event sourcing and CQRS the same architectural decision? | [Event Sourcing and Its Real Costs](../../09-messaging-event-driven/event-sourcing-and-its-real-costs.md#flashcards) |
| 16 | What does Kafka guarantee about record ordering? | [Kafka Architecture Fundamentals](../../09-messaging-event-driven/kafka-architecture-fundamentals.md#flashcards) |
| 17 | Why is changing partition count on a keyed topic dangerous? | [Kafka Architecture Fundamentals](../../09-messaging-event-driven/kafka-architecture-fundamentals.md#flashcards) |
| 18 | What does `acks=all` actually wait for — `replication.factor` replicas, or something else? | [Kafka Architecture Fundamentals](../../09-messaging-event-driven/kafka-architecture-fundamentals.md#flashcards) |
| 19 | What's the real mechanism behind Kafka Connect surviving a worker crash without losing or duplicating data? | [Kafka Connect](../../09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md#flashcards) |
| 20 | What's the real difference between a `Converter` and a connector in Kafka Connect? | [Kafka Connect](../../09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md#flashcards) |
| 21 | What makes a Kafka Streams `KTable`'s state durable and fast to recover after a crash? | [Kafka Streams](../../09-messaging-event-driven/kafka-streams-and-stateful-processing.md#flashcards) |
| 22 | Why does `groupBy` sometimes create a new internal Kafka topic not in the original application code? | [Kafka Streams](../../09-messaging-event-driven/kafka-streams-and-stateful-processing.md#flashcards) |
| 23 | What does Change Data Capture actually read to produce its change events? | [Messaging Patterns and CDC](../../09-messaging-event-driven/messaging-patterns-and-change-data-capture.md#flashcards) |
| 24 | What real, measurable risk does an unconsumed CDC consumer create? | [Messaging Patterns and CDC](../../09-messaging-event-driven/messaging-patterns-and-change-data-capture.md#flashcards) |
| 25 | What did this chapter's real Kafka demo prove about point-to-point vs. publish-subscribe? | [Messaging Patterns and CDC](../../09-messaging-event-driven/messaging-patterns-and-change-data-capture.md#flashcards) |
| 26 | Why isn't `acks=all` alone sufficient for durability? | [Producer Semantics and Partition Keys](../../09-messaging-event-driven/producer-semantics-and-partition-keys.md#flashcards) |
| 27 | What does an idempotent producer deduplicate? | [Producer Semantics and Partition Keys](../../09-messaging-event-driven/producer-semantics-and-partition-keys.md#flashcards) |
| 28 | What does the sticky partitioner do with a null key? | [Producer Semantics and Partition Keys](../../09-messaging-event-driven/producer-semantics-and-partition-keys.md#flashcards) |
| 29 | What does log compaction actually guarantee — full history, or something else? | [Retention, Log Compaction, Tiered Storage](../../09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md#flashcards) |
| 30 | Does a tombstone delete a key instantly and permanently? | [Retention, Log Compaction, Tiered Storage](../../09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md#flashcards) |
| 31 | Under BACKWARD compatibility, which is safe: removing a field, or adding one without a default? | [Schema Registry and Compatibility Evolution](../../09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#flashcards) |
| 32 | What question should decide BACKWARD vs. FORWARD for a topic? | [Schema Registry and Compatibility Evolution](../../09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#flashcards) |
| 33 | What real failure does a Schema Registry's compatibility check prevent, mechanically? | [Schema Registry and Compatibility Evolution](../../09-messaging-event-driven/schema-registry-and-compatibility-evolution.md#flashcards) |

---

## Related

- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
