---
title: "outbox-implementation.md Deliverable"
week: 10
last_reviewed: 2026-07-29
---

# `outbox-implementation.md` Deliverable

**A working transactional outbox with a polling publisher.** Per `00-project/learning-roadmap.md` §4 Week 10's own deliverable spec. This is the week's primary artifact — a real, running implementation, not a diagram or a description. Source: `practice/java/week-10/outbox-publisher/`, `practice/sql/week-10/outbox/setup.sql`.

## Table of Contents

1. [Architecture](#1-architecture)
2. [Schema](#2-schema)
3. [The three components](#3-the-three-components)
4. [Full reproduce sequence](#4-full-reproduce-sequence)
5. [What was actually proven](#5-what-was-actually-proven)
6. [Known limitations of this implementation](#6-known-limitations-of-this-implementation)
7. [Exit check](#7-exit-check)

---

## 1. Architecture

```mermaid
sequenceDiagram
    participant Writer as OutboxWriter
    participant DB as Postgres (orders + outbox)
    participant Poller as OutboxPoller
    participant Kafka
    participant Verify as VerifyConsumer

    Writer->>DB: BEGIN; INSERT order; INSERT outbox row; COMMIT
    Note over DB: one transaction -- both rows or neither
    Poller->>DB: SELECT * FROM outbox WHERE published=false ORDER BY id
    loop each unpublished row
        Poller->>Kafka: producer.send(topic, key=aggregateId, payload)
        Kafka-->>Poller: ack (acks=all)
        Poller->>DB: UPDATE outbox SET published=true WHERE id=?
    end
    Verify->>Kafka: consume order-events from earliest
    Kafka-->>Verify: every message ever published
```

## 2. Schema

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id TEXT NOT NULL,
    amount_cents BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE outbox (
    id BIGSERIAL PRIMARY KEY,
    aggregate_id BIGINT NOT NULL,
    event_type TEXT NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    published BOOLEAN NOT NULL DEFAULT false,
    published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished ON outbox (id) WHERE published = false;
```

The partial index on `published = false` keeps the poller's `SELECT ... WHERE published = false` query cheap regardless of how large the fully-published history grows — a small, deliberate detail worth naming: without it, the poller's own query would slow down over the table's entire lifetime, not just its unpublished backlog.

## 3. The three components

| File | Role |
|---|---|
| `OutboxWriter.java` | Writes a business row (`orders`) and its corresponding outbox row in ONE JDBC transaction (`conn.setAutoCommit(false)`, explicit `commit()`) — this is the entire atomicity mechanism |
| `OutboxPoller.java` | Polls `outbox` for `published = false` rows, publishes each to Kafka (`acks=all`), marks it published only after the send is confirmed. Accepts `--crash-after-first-publish` to simulate a process death in the one unsafe window (post-Kafka-ack, pre-DB-mark) |
| `VerifyConsumer.java` | Consumes `order-events` from the beginning, printing every message that was ever actually published — the ground truth for verifying no loss occurred |
| `DualWriteHazardDemo.java` | The control case: writes a business row, then simulates a crash before ANY publish attempt, with no outbox — demonstrates precisely what this implementation fixes |

## 4. Full reproduce sequence

```bash
cd practice/java/week-10/outbox-publisher
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java

# Postgres (schema)
docker run --rm -d --name week10-pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=week10 -p 5433:5432 postgres:16
docker cp ../../../sql/week-10/outbox/setup.sql week10-pg:/tmp/setup.sql
docker exec -e PGPASSWORD=postgres week10-pg psql -U postgres -d week10 -f /tmp/setup.sql

# Kafka (single-broker KRaft, port 9093 to avoid colliding with Week 8's setup)
docker run -d --name week10-kafka -p 9093:9092 \
  -e KAFKA_NODE_ID=1 -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9093 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e CLUSTER_ID=NkU3OEVBNTcwNTJENDM2Ql \
  apache/kafka:3.7.0

# The crash-recovery sequence
java -cp "out:lib/*" OutboxWriter 3
java -cp "out:lib/*" OutboxPoller --crash-after-first-publish   # dies after publishing row 1
java -cp "out:lib/*" OutboxPoller                                # restart: redelivers row 1, publishes 2 and 3
java -cp "out:lib/*" VerifyConsumer                               # ground truth: what Kafka actually received
```

## 5. What was actually proven

**Real output, the full sequence:**

```
== 1. write 3 orders atomically ==
Committed order 1 + its outbox row, atomically, in one transaction.
Committed order 2 + its outbox row, atomically, in one transaction.
Committed order 3 + its outbox row, atomically, in one transaction.

== 2. poller run 1: crashes right after publishing row 1 ==
Published outbox row 1 (OrderCreated, aggregate=1) to Kafka.
Simulating a crash HERE -- Kafka publish confirmed, but BEFORE marking outbox row 1 as published in Postgres.

== 3. poller run 2 (restart): redelivers row 1, publishes 2 and 3 ==
Published outbox row 1 (OrderCreated, aggregate=1) to Kafka.
Published outbox row 2 (OrderCreated, aggregate=2) to Kafka.
Published outbox row 3 (OrderCreated, aggregate=3) to Kafka.
Poller pass complete: 3 row(s) published this pass.

== 4. verify final Postgres state ==
 id | published 
----+-----------
  1 | t
  2 | t
  3 | t

== 5. verify Kafka: what actually landed ==
  [1] key=1 value={"orderId":1,"customerId":"outbox-customer-0"}
  [2] key=1 value={"orderId":1,"customerId":"outbox-customer-0"}
  [3] key=2 value={"orderId":2,"customerId":"outbox-customer-1"}
  [4] key=3 value={"orderId":3,"customerId":"outbox-customer-2"}
Total messages ever published to order-events: 4
```

**3 orders written. 4 messages delivered. 0 lost.** The duplicate (order 1's event, delivered twice) is the direct, measured cost of the one window this implementation does not make atomic — Kafka-ack to DB-mark — and is exactly why the downstream consumer of `order-events` must be idempotent (dedupe by `aggregate_id` + `event_type`, or by a dedicated event UUID in a production version) to be safe. The control case, `DualWriteHazardDemo.java`, shows the alternative: without the outbox, the equivalent crash produces a REAL, unrecoverable loss (the order exists, no event for it ever exists anywhere), not a recoverable duplicate.

## 6. Known limitations of this implementation

Stated explicitly, per this repository's own integrity convention (see `MANIFEST.md`) — not glossed over:

- **Polling, not CDC.** This implementation polls on-demand rather than using change-data-capture (e.g., Debezium reading Postgres's WAL) — CDC would eliminate polling latency and load entirely, at the cost of real operational complexity (a CDC connector to run and monitor) that was out of scope for this pack's time budget. The roadmap's own deliverable spec explicitly allows either.
- **Single poller, no lease/lock.** With multiple poller instances, this implementation would double-publish far more than the single-crash scenario above — a production version needs the same claim-with-a-lease mechanism as Week 9's distributed-job-scheduler design (`study-packs/week-09/09-design-exercise-distributed-job-scheduler.md` Phase 4), not demonstrated here.
- **No dead-letter handling.** A poison outbox row (one that permanently fails to publish, e.g., a malformed payload Kafka's serializer rejects) would block the poller indefinitely in this implementation's simple `while (rs.next())` loop — a production version needs a retry-count column and a dead-letter path, matching the same poison-message concern named in `T-707` from Week 8's Kafka material.

## 7. Exit check

- [ ] Reproduced the full sequence yourself and captured your own output (offsets/timestamps will differ, the duplicate-and-zero-loss OUTCOME should not)
- [ ] Can explain, from the real numbers, exactly which window the outbox pattern does NOT make atomic
- [ ] Can name at least one production gap in this specific implementation (§6) unprompted, not just recite the pattern's theoretical description
