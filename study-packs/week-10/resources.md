---
title: "Week 10 Resources"
week: 10
last_reviewed: 2026-07-29
---

# Week 10 Resources

Classified by authority: **PRIMARY**, **BOOK**, **TOOL**, **SECONDARY**.

---

## T-618 — Saga, Outbox, 2PC

| Source | Type | Note |
|---|---|---|
| [microservices.io — Pattern: Transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html) | PRIMARY | |
| [microservices.io — Pattern: Saga](https://microservices.io/patterns/data/saga.html) | PRIMARY | |
| [Debezium documentation — Outbox Event Router](https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html) | PRIMARY | The CDC-based alternative to this week's polling publisher |
| Postgres 16 + `kafka-clients` 3.7.0 (Docker + Maven Central jars) | TOOL | Produced the real working outbox implementation, dual-write hazard, and crash-recovery demonstrations; see `practice/java/week-10/outbox-publisher/` |

## T-614 — Sharding & Partitioning

| Source | Type | Note |
|---|---|---|
| [PostgreSQL documentation — Table Partitioning](https://www.postgresql.org/docs/16/ddl-partitioning.html) | PRIMARY | |
| [PostgreSQL documentation — Partition Pruning](https://www.postgresql.org/docs/16/ddl-partitioning.html#DDL-PARTITION-PRUNING) | PRIMARY | |
| Postgres 16 (Docker) | TOOL | Produced the real partition-pruning `EXPLAIN` demonstration; see `practice/sql/week-10/sharding/` |

## T-806 — Consistent Hashing

| Source | Type | Note |
|---|---|---|
| [Karger et al. — Consistent Hashing and Random Trees (1997)](https://www.akamai.com/site/en/documents/technical-publication/consistent-hashing-and-random-trees-distributed-caching-protocols-for-relieving-hot-spots-on-the-world-wide-web-technical-publication.pdf) | PRIMARY | Original paper |
| [Amazon DynamoDB paper (2007)](https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf) | PRIMARY | Production system built on consistent hashing with virtual nodes |
| OpenJDK 21.0.12 | TOOL | Produced the real 92.5%-vs-9.2% redistribution measurement; see `practice/java/week-10/consistent-hashing/` |

## T-515 — Resilience Patterns

| Source | Type | Note |
|---|---|---|
| [Netflix Tech Blog — Fault Tolerance in a High Volume, Distributed System](https://netflixtechblog.com/fault-tolerance-in-a-high-volume-distributed-system-91ab4faae74a) | PRIMARY | |
| [Resilience4j documentation — Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker) | SECONDARY | Production library; this week's breaker was hand-built for full control over the state machine |
| OpenJDK 21.0.12 | TOOL | Produced the real circuit-breaker state-transition and retry-jitter measurements; see `practice/java/week-10/resilience/` |

## T-616 — Zero-Downtime Migration

| Source | Type | Note |
|---|---|---|
| [PostgreSQL documentation — Building Indexes Concurrently](https://www.postgresql.org/docs/16/sql-createindex.html#SQL-CREATEINDEX-CONCURRENTLY) | PRIMARY | |
| [PostgreSQL documentation — Explicit Locking](https://www.postgresql.org/docs/16/explicit-locking.html) | PRIMARY | |
| Postgres 16 (Docker) | TOOL | Produced the real 1943ms-vs-84ms blocking measurement; see `practice/sql/week-10/zero-downtime-migration/` |

## General

| Source | Type | Note |
|---|---|---|
| `00-project/knowledge-architecture-blueprint.md` §5.9-5.10 | PRIMARY | The condensed dossier entries this pack implements |
| `00-project/learning-roadmap.md` §4 (Week 10) | PRIMARY | Full Week 10 (Plan B) spec this pack implements |
