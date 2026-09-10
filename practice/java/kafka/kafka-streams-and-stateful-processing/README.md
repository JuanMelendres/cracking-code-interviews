# Kafka Streams and Stateful Stream Processing (T-709) — runnable verification

Real, executed output backing
[`syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md`](../../../../syllabus/09-messaging-event-driven/kafka-streams-and-stateful-processing.md)
(T-709). A real, compiling Kafka Streams application (the library's own
canonical word-count topology) running against a real, disposable Kafka
3.8.0 broker (Docker) — no simulated topology, no mocked `KafkaStreams`
instance.

## Setup and run

Requires Docker.

```bash
cd practice/java/kafka/kafka-streams-and-stateful-processing
./fetch-deps.sh
docker compose up -d
sleep 5
./streams-demo.sh
```

Tear down: `docker compose down -v`.

## What it proves

`WordCountStreamsDemo.java` builds a real topology: `KStream<String,String>`
reads raw lines from `streams-input`, `flatMapValues` splits each into
words, `groupBy` re-keys by word (triggering a real internal repartition,
visible in the topology printout), and `.count()` produces a
`KTable<String, Long>` materialized into an in-memory state store, streamed
out to `streams-output`.

- **The real topology has two sub-topologies connected by an internal
  repartition topic** (`word-counts-store-repartition`) — printed directly
  by the running application (`topology.describe()`), not asserted from
  documentation. Re-keying by word after the source's natural partitioning
  is exactly why Kafka Streams needs this internal hop.
- **The aggregation is genuinely correct and stateful.** Feeding three
  lines (`"the quick brown fox"`, `"the lazy dog"`, `"the fox runs"`)
  produces real output `the:3`, `fox:2`, and `1` for every other word —
  matching each word's real occurrence count across all three lines, not
  per-line.
- **The KTable's state store is backed by a real, automatically-created
  Kafka topic, and that topic is automatically compacted.** `kafka-topics.sh
  --describe` on `word-count-demo-app-word-counts-store-changelog` shows
  `cleanup.policy=compact` — set by the Kafka Streams library itself, with
  no manual topic configuration from this demo. This is the direct,
  concrete link to [Retention, Log Compaction, and Tiered
  Storage](../retention-log-compaction-and-tiered-storage/README.md)
  (T-706): a KTable's state is durable and rebuildable specifically
  *because* its changelog is a real compacted topic, holding only the
  latest value per key — exactly the mechanism that chapter demonstrates
  directly, now shown here backing a real, running stream-processing
  application instead of an application-level topic.
