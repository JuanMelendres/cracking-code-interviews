---
title: "Cheat Sheets — Index"
document_type: cheat-sheet-index
status: draft
last_updated: 2026-08-03
---

# Cheat Sheets

One-page-equivalent rapid-review documents, one per canonical `handbook/` chapter, per `CLAUDE.md`'s Cheat Sheet Standard: core mental model, essential definitions, decision table, key numbers, common pitfalls, interview answer skeleton, production warning signs, related links. These are **not** full-length chapters — read the linked canonical chapter for the actual teaching material, internals, and full interview-question set; use these for the day-before-the-interview pass.

## A note on scope

Eighteen cheat sheets exist so far, covering the eighteen highest-IWI (interview-weight-index) canonical chapters in the entire 75-chapter handbook, per `00-project/knowledge-architecture-blueprint.md`'s Master Topic Register — built across three bounded batches, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. 57 chapters remain uncovered.

Every fact in every cheat sheet below (definitions, decision tables, measured numbers, production incidents) was extracted directly from its canonical chapter — nothing here was written from memory or general knowledge. Extraction was done via a dedicated read-and-report pass per chapter before any cheat sheet was drafted, consistent with this repository's no-fabrication discipline.

## Cheat Sheets

| # | Cheat Sheet | Topic ID | IWI | Domain | Canonical Chapter |
|---|---|---|---|---|---|
| 1 | [System Design Method and Estimation](system-design-method-and-estimation.md) | T-801 | 8.65 | system-design | `handbook/system-design/system-design-method-and-estimation.md` |
| 2 | [Distributed Systems Failure Modes](distributed-systems-failure-modes.md) | T-909 | 8.45 | system-design | `handbook/system-design/distributed-systems-failure-modes.md` |
| 3 | [Caching Strategies and Invalidation](caching-strategies-and-invalidation.md) | T-804 | 8.45 | system-design | `handbook/system-design/caching-strategies-and-invalidation.md` |
| 4 | [Microservice Decomposition and the Monolith Trade-off](microservice-decomposition-and-monolith-tradeoff.md) | T-907 | 8.40 | architecture | `handbook/architecture/microservice-decomposition-and-monolith-tradeoff.md` |
| 5 | [Database Index Structures](index-structures-btree-composite-covering.md) | T-609 | 8.30 | databases | `handbook/databases/index-structures-btree-composite-covering.md` |
| 6 | [Spring Transactional Proxy Mechanics and Propagation](transactional-proxy-mechanics-and-propagation.md) | T-504 | 8.15 | spring | `handbook/spring/transactional-proxy-mechanics-and-propagation.md` |
| 7 | [Kafka Delivery Semantics and Exactly-Once](delivery-semantics-and-exactly-once.md) | T-704 | 8.00 | kafka | `handbook/kafka/delivery-semantics-and-exactly-once.md` |
| 8 | [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md) | T-611 | 7.95 | databases | `handbook/databases/isolation-levels-and-concurrency-anomalies.md` |
| 9 | [CAP Theorem and Consistency Models](cap-theorem-and-consistency-models.md) | T-807 | 7.90 | system-design | `handbook/system-design/cap-theorem-and-consistency-models.md` |
| 10 | [API Design](api-design.md) | T-803 | 7.90 | system-design | `handbook/system-design/api-design.md` |
| 11 | [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md) | T-610 | 7.90 | databases | `handbook/databases/query-planning-and-explain-analyze.md` |
| 12 | [Idempotency at System Edges](idempotency.md) | T-809 | 7.85 | system-design | `handbook/system-design/idempotency.md` |
| 13 | [Java Memory Model and volatile](java-memory-model-and-volatile.md) | T-401 | 7.75 | concurrency | `handbook/concurrency/java-memory-model-and-volatile.md` |
| 14 | [Data Partitioning and Consistent Hashing](data-partitioning-and-consistent-hashing.md) | T-806 | 7.70 | system-design | `handbook/system-design/data-partitioning-and-consistent-hashing.md` |
| 15 | [Distributed Transactions: Saga and Outbox](distributed-transactions-saga-and-outbox.md) | T-618 | 7.65 | system-design | `handbook/system-design/distributed-transactions-saga-and-outbox.md` |
| 16 | [Table Partitioning and Sharding Strategies](table-partitioning-and-sharding-strategies.md) | T-614 | 7.60 | databases | `handbook/databases/table-partitioning-and-sharding-strategies.md` |
| 17 | [Resilience Patterns](resilience-patterns.md) | T-515 | 7.60 | system-design | `handbook/system-design/resilience-patterns.md` |
| 18 | [Consumer Groups and Rebalancing](consumer-groups-and-rebalancing.md) | T-703 | 7.50 | kafka | `handbook/kafka/consumer-groups-and-rebalancing.md` |

## How this relates to `flashcards/`

Cheat sheets and flashcards serve different grain sizes and different moments, and are meant to coexist without duplicating each other: a flashcard is one atomic Q/A pair for spaced-repetition drilling; a cheat sheet is a one-page whole-chapter refresh for the day before an interview. `flashcards/` is currently empty (not yet started as its own Phase 6 deliverable) — when it is built, individual cards should draw from the same canonical chapters these cheat sheets do, not restate a cheat sheet's content verbatim.

## Selection method

Chapters are ranked by the IWI each canonical chapter states in its own "Topic register" line — this is more reliable than `00-project/knowledge-architecture-blueprint.md`'s Master Topic Register table, which predates several newer system-design/T-8xx/T-9xx chapters and does not list them at all (a known staleness gap; see the flagged coverage-audit refresh task). Built across three batches (top 8, then the next 5, then the next 5), spanning 6 domains so far (system-design, architecture, databases, spring, kafka, concurrency) rather than front-loading one. Verified next candidates by IWI, for a future batch: `handbook/collections/hashmap-internals.md` (T-201, 7.4), `handbook/jvm/gc-fundamentals-and-log-analysis.md` (T-306, 7.35), `handbook/databases/zero-downtime-schema-migration.md` (T-616, 7.30), `handbook/spring/auto-configuration-and-bean-lifecycle.md` (T-501, 7.30), `handbook/architecture/ddd-tactical-design-aggregates.md` (T-903, 7.25) or `handbook/architecture/clean-hexagonal-architecture.md` (T-901, 7.25, tied).
