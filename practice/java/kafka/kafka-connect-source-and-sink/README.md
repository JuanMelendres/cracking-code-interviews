# Kafka Connect: Source and Sink Connectors (T-2408) — runnable verification

Real, executed output backing
[`syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md`](../../../../syllabus/09-messaging-event-driven/kafka-connect-source-and-sink-connectors.md)
(T-2408). A real, disposable Kafka 3.8.0 broker (Docker, KRaft mode) plus a
real Kafka Connect standalone worker running the core, built-in
`FileStreamSource`/`FileStreamSink` connectors — zero custom producer or
consumer code written for this demo; every byte of data movement happens
through configuration alone.

## Setup and run

Requires Docker.

```bash
cd practice/java/kafka/kafka-connect-source-and-sink
docker compose up -d
sleep 5
./connect-demo.sh
```

Tear down: `docker compose down -v`.

## What it proves

- **Data moves from a plain local file, through a real Kafka topic, to
  another plain local file, with no custom code.** `local-file-source`
  (a `FileStreamSource` connector) tails `input.txt` and produces each
  line as a record to `connect-demo-topic`; `local-file-sink` (a
  `FileStreamSink` connector) consumes that topic and appends each
  record to `output.txt` — a real, config-only end-to-end pipeline.
- **Records really carry the configured `JsonConverter`'s schema
  wrapper.** The real topic content is
  `{"schema":{"type":"string","optional":false},"payload":"line-1"}`,
  not a bare string — proof the converter, not the connector itself,
  controls the on-the-wire representation.
- **The pipeline streams incrementally, not in one batch.** Appending
  two more lines to `input.txt` *while the worker is already running*
  produces real, immediate updates to `output.txt` — the source
  connector is genuinely tailing the file, not reading it once at
  startup.
- **Killing and restarting the worker resumes from the real, saved
  offset — no reprocessing, no duplicates.** After a real `pkill` and
  restart against the same `connect.offsets` file, appending exactly one
  new line produces exactly one new line in `output.txt`. The real,
  raw offset file (inspectable directly) stores the exact byte position
  in `input.txt` the source connector had already read up to — the
  concrete mechanism behind Kafka Connect's fault tolerance.
