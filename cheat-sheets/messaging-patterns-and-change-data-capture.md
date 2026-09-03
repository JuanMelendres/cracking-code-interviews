---
title: "Cheat Sheet: Messaging Patterns and Change Data Capture"
slug: messaging-patterns-and-change-data-capture
document_type: cheat-sheet
domain: system-design
topic_id: T-710
canonical: ../handbook/system-design/messaging-patterns-and-change-data-capture.md
last_updated: 2026-09-02
---

# Messaging Patterns and Change Data Capture

**Canonical chapter:** [`syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md`](../syllabus/09-messaging-event-driven/messaging-patterns-and-change-data-capture.md)

## Core Mental Model

Every messaging pattern answers two separable questions: how does a change become a message in the first place (CDC tails a log after the fact; an application can also explicitly publish, as in the outbox pattern), and how many recipients does that message actually reach (point-to-point delivers to exactly one consumer within a competing group; publish-subscribe delivers an independent copy to every subscriber). CDC/outbox and point-to-point/pub-sub answer independent questions, so any combination is a valid design.

## Essential Definitions

- **Change Data Capture (CDC)** — capturing row-level changes directly from a database's own transaction log (WAL in PostgreSQL, binlog in MySQL) into a change-event stream, with zero application-code changes.
- **Point-to-point messaging** — delivers each message to exactly one consumer, even when multiple consumers compete for the same queue/topic.
- **Publish-subscribe messaging** — delivers an independent copy of each message to every subscriber, regardless of how many others exist.
- **CDC's retention cost** — an unconsumed CDC consumer (or its replication slot) prevents the source database from reclaiming its transaction log, structurally identical to the long-transaction-blocks-vacuum finding but applied to WAL retention.
- **Outbox's compounding cost** — every current and future write path must remember to write the outbox row; missing one is a real, silent bug.

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Many existing write paths would each need to remember to publish an event | CDC |
| Minimizing new infrastructure (no replication slots, no log-tailing tool) is the priority | Transactional outbox |
| "Which fields changed" needs to be explicit, controlled by application logic | Outbox (or a CDC transform layer) |
| Exactly one consumer should process each unit of work | Point-to-point (competing consumers) |
| Every interested party should get its own independent notification stream | Publish-subscribe |

**CDC vs. Outbox:**

| Dimension | CDC | Outbox |
|---|---|---|
| App-code changes required | None | Yes, every write path |
| New write paths automatically included | Yes | No — each must remember to participate |
| Real operational risk | Unconsumed slot → unbounded WAL retention | A missed write path → silent data-sync bug |

## Key Numbers (real, executed against PostgreSQL 16 + Kafka 3.7.0)

- CDC retention risk: 200,000 rows of unconsumed WAL activity grew the WAL directory from 16 MB to 48 MB, unreclaimed even across a real `CHECKPOINT`.
- Point-to-point: 3 real consumers in one group ("order-processors") received a combined total of exactly 10 deliveries for 10 published messages.
- Publish-subscribe: the same 10 messages, read by 3 consumers in 3 separate groups, produced 30 total real deliveries (10 to each independent subscriber).

## Common Pitfalls

- Name-dropping "CDC" or "Debezium" without being able to explain the log-tailing mechanism when asked a follow-up.
- Assuming CDC has no real operational cost, missing the replication-slot retention risk.
- Confusing "consumer group" (a Kafka-specific mechanism) with the general point-to-point pattern, which predates and extends beyond Kafka.
- Treating point-to-point's per-group exactly-once delivery as end-to-end exactly-once processing — redelivery on consumer failure is still possible; an idempotent consumer is still needed.
- Assuming publish-subscribe subscriber count is free — every additional subscriber is a full independent copy with real, linear broker-side cost.

## Interview Answer Skeleton

**30-sec:** CDC reads committed changes directly from a database's transaction log, requiring no application-code changes — the alternative to the outbox pattern's explicit, per-write-path event publishing. Point-to-point delivers each message to exactly one consumer in a competing group; publish-subscribe delivers an independent copy to every subscriber.

**2-min:** Add the real WAL-retention risk (16 MB → 48 MB from 200,000 unconsumed rows) as CDC's real operational cost, and the real Kafka proof: identical 10 messages produced 10 total deliveries (point-to-point) vs. 30 total deliveries (publish-subscribe), purely from consumer grouping.

**Whiteboard:** Draw a database with a "WAL" log icon beside it, and an arrow from the WAL (not the application) to a "CDC consumer" box — say explicitly "the application never knows this exists." Separately, draw one message splitting into one arrow (point-to-point, landing on one of several consumers) versus three arrows (publish-subscribe, landing on all three independently).

**Staff-level framing:** Discuss the organizational cost of the outbox pattern's per-write-path opt-in requirement at scale (three write paths silently missed over years in a representative scenario), reason about replication-slot monitoring as a new real operational responsibility CDC introduces, and connect the WAL-retention risk to the same underlying mechanism as MVCC/vacuum's long-transaction finding.

## Production Warning Signs

- Disk usage on the WAL volume climbing steadily with no corresponding change in write volume — check `pg_replication_slots`' `active` column for a slot expected to be actively consumed but sitting idle.
- A search index or downstream store silently drifts stale for records created via specific write paths — a symptom of the outbox pattern's per-path opt-in cost; CDC eliminates this class of bug by construction.
- Confusing "exactly once per group" with true end-to-end exactly-once — redelivery on consumer failure still requires an idempotent consumer.

## Related

- `syllabus/10-distributed-systems/distributed-transactions-saga-and-outbox.md`
- `syllabus/09-messaging-event-driven/consumer-lag-backpressure-and-dlq-strategy.md`
- `syllabus/06-databases/mvcc-vacuum-and-bloat.md`
- `syllabus/06-databases/replication-read-replicas-and-replica-lag.md`
