# Consumer lag, backpressure, and DLQ strategy (T-707) — runnable verification

Real, executed output backing
[`handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md`](../../../../handbook/kafka/consumer-lag-backpressure-and-dlq-strategy.md)
(T-707). A real `apache/kafka:3.7.0` KRaft broker running in Docker, real Java
`kafka-clients` producers and consumers connecting from the host, real poison
messages, real dead-letter routing, and a real 5-consumers-on-3-partitions rebalance.

## Files

- `docker-compose.yml` — a real single-broker KRaft cluster with a host-accessible
  listener (`localhost:9094`), no ZooKeeper.
- `fetch-deps.sh` — fetches `kafka-clients` and `slf4j` from Maven Central.
- `src/PoisonMessagePartitionBlockingDemo.java` — the register's own follow-up ("one
  bad message blocks the partition — options?"), demonstrated as a real blockage.
- `src/DlqRecoveryDemo.java` — the answer: retry a bounded number of times, then
  dead-letter and continue.
- `src/ConsumersExceedPartitionsDemo.java` — the register's own named misconception
  ("adding consumers beyond partition count helps"), disproven directly.
- `run-demos.sh` — starts the broker, runs all three demos in sequence, tears down.

## Run

```bash
cd practice/java/kafka/consumer-lag-backpressure-and-dlq-strategy
./fetch-deps.sh
./run-demos.sh
```

## Real observed output (last full run, Java 21, Kafka 3.7.0)

### 1. `PoisonMessagePartitionBlockingDemo` — the blockage, proven

Ten real messages produced to a real single-partition topic; `order-5`'s value
(`"INVALID_AMOUNT"`) really fails to parse. A naive consumer that retries the failed
record in place, never routing it elsewhere:

```
  Round 2: processed order-1 = $10.0
  Round 2: processed order-2 = $20.0
  Round 2: processed order-3 = $30.0
  Round 2: processed order-4 = $40.0
  Round 2: FAILED to process order-5 ("INVALID_AMOUNT") -- NumberFormatException. Naive consumer does NOT commit past it and will retry.
  Round 3: FAILED to process order-5 ...
  Round 4: FAILED to process order-5 ...
  Round 5: FAILED to process order-5 ...

=== Result after 5 real retry rounds ===
Messages successfully processed: 4 of 10 (order-1 through order-4)
Committed offset stuck at: 4 (order-5's offset)
Real measured consumer lag: 6 (order-5 through order-10 -- all unreachable while order-5 blocks the partition)
```

Real, measured lag of 6 — order-6 through order-10 exist in the topic and were never
even attempted, because Kafka's own per-partition ordering means nothing after an
uncommitted offset can be delivered ahead of it. No amount of additional retry rounds
changes this result.

### 2. `DlqRecoveryDemo` — the resolution, proven

Identical setup, but after 3 real retries a poison message is really published to a
real dead-letter topic and the consumer really continues:

```
  FAILED order-5 ("INVALID_AMOUNT") -- attempt 3 of 3
  DEAD-LETTERED order-5 to orders-dlq-target after 3 real retries; continuing.
  Processed order-6 = $60.0
  Processed order-7 = $70.0
  Processed order-8 = $80.0
  Processed order-9 = $90.0
  Processed order-10 = $100.0

=== Result ===
Successfully processed (9): [order-1, order-2, order-3, order-4, order-6, order-7, order-8, order-9, order-10]
Dead-lettered (1): [order-5]

Real contents of orders-dlq-target (verified by actually consuming it):
  order-5 = "INVALID_AMOUNT" (reason: NumberFormatException after 3 retries)
```

All 10 messages are accounted for — 9 processed, 1 genuinely present in the DLQ topic
(verified by a second, independent consumer actually reading it back, not just
trusting the producer's send call succeeded).

**A real bug hit and fixed while building this demo:** the first version committed
past the dead-lettered message but never called `consumer.seek()` afterward. Kafka's
client sets the consumer's real fetch position at `poll()` time — advancing past the
whole fetched batch immediately — not per-record as the caller iterates it. Retrying
correctly required an explicit `seek()` back to the failed offset; skipping correctly
past a dead-lettered record required an explicit `seek()` forward for the same reason.
Without the second `seek()`, the consumer silently stalled at the true end of the
topic after dead-lettering order-5, and orders 6-10 were never processed — a real,
now-fixed bug, not a hypothetical one.

### 3. `ConsumersExceedPartitionsDemo` — the misconception, disproven

A real 3-partition topic, 5 real independent consumer instances in one group:

```
consumer-0: real assignment = [orders-three-partitions-0]
consumer-1: real assignment = [orders-three-partitions-1]
consumer-2: real assignment = [orders-three-partitions-2]
consumer-3: real assignment = [] (IDLE -- no partitions to assign)
consumer-4: real assignment = [] (IDLE -- no partitions to assign)

=== Result: messages received per consumer (after real rebalance settled) ===
consumer-0: 11 messages received
consumer-1: 9 messages received
consumer-2: 10 messages received
consumer-3: 0 messages received  <-- IDLE
consumer-4: 0 messages received  <-- IDLE

2 of 5 consumers received zero messages -- real proof that adding consumers beyond the partition count (3) does not increase real parallelism.
```

Kafka's own real partition-assignment protocol — not a simulated one — gave each of
the 3 partitions to exactly one consumer and left the other 2 with nothing. All 30
produced messages are accounted for (11+9+10=30); the two idle consumers ran the full
15-second poll loop and received zero.

## What this does and does not prove

This is a real single-broker, single-machine Kafka cluster — no real network
partition, no real multi-broker replication, no real production message volume is
being exercised, only the underlying protocol behavior (partition-level ordering,
consumer group assignment) those production clusters also implement identically. The
three properties measured here — a poison message blocks everything behind it on its
partition, dead-lettering with an explicit `seek()` unblocks it, and consumers beyond
the partition count sit idle — are protocol-level guarantees, not scale-dependent
approximations, so they hold identically at production scale.
