---
title: "Cheat Sheets — Index"
document_type: cheat-sheet-index
status: draft
last_updated: 2026-08-03
---

# Cheat Sheets

One-page-equivalent rapid-review documents, one per canonical `handbook/` chapter, per `CLAUDE.md`'s Cheat Sheet Standard: core mental model, essential definitions, decision table, key numbers, common pitfalls, interview answer skeleton, production warning signs, related links. These are **not** full-length chapters — read the linked canonical chapter for the actual teaching material, internals, and full interview-question set; use these for the day-before-the-interview pass.

## A note on scope

Eight cheat sheets exist so far, covering the eight highest-IWI (interview-weight-index) canonical chapters in the entire 75-chapter handbook, per `00-project/knowledge-architecture-blueprint.md`'s Master Topic Register — a deliberately bounded first batch, not the complete deliverable, per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. 67 chapters remain uncovered.

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

## How this relates to `flashcards/`

Cheat sheets and flashcards serve different grain sizes and different moments, and are meant to coexist without duplicating each other: a flashcard is one atomic Q/A pair for spaced-repetition drilling; a cheat sheet is a one-page whole-chapter refresh for the day before an interview. `flashcards/` is currently empty (not yet started as its own Phase 6 deliverable) — when it is built, individual cards should draw from the same canonical chapters these cheat sheets do, not restate a cheat sheet's content verbatim.

## Selection method for this batch

The top 8 chapters by IWI (interview-weight-index) score, per the Master Topic Register in `00-project/knowledge-architecture-blueprint.md` — the highest-value chapters get the first rapid-review pass. This naturally spanned 5 domains (system-design, architecture, databases, spring, kafka) rather than front-loading one. Remaining high-IWI chapters not yet covered, for a natural next batch: `handbook/system-design/cap-theorem-and-consistency-models.md` (7.90), `handbook/system-design/api-design.md` (7.90), `handbook/databases/query-planning-and-explain-analyze.md` (7.90), `handbook/system-design/idempotency.md` (7.85), `handbook/concurrency/java-memory-model-and-volatile.md` (7.75).
