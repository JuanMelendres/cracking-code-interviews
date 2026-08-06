---
title: "Flashcards — Index"
document_type: flashcard-index
status: draft
last_updated: 2026-08-06
---

# Flashcards

Atomic, spaced-repetition-ready Q/A decks, one deck per canonical `handbook/` chapter, per `CLAUDE.md`'s Flashcard Standard. Each card is one concept: a prompt, a concise answer, why it matters for an interview, and the common trap around it. These are **not** rapid-review pages — see `cheat-sheets/` for the one-page-per-chapter refresh; a flashcard is the smallest reviewable unit, meant for daily/weekly drilling on the topic register's `Rev` intervals (`00-project/learning-roadmap.md`), not a pre-interview cram pass.

## A note on scope

Every card in every deck below already existed, written into its canonical chapter's own `## Flashcards` section, as part of that chapter's original closure. Building this deliverable is an extraction and reorganization pass, not new content generation: each deck below is copied from its chapter's existing cards, promoted to standalone files (topic-ID-keyed, per the roadmap's own spec — `flashcards/` "by topic ID, reviewed on the register's `Rev` intervals"), with in-chapter anchor links rewritten to resolve correctly from `flashcards/`'s own directory.

Handbook chapters currently use one of two existing card formats: an explicit `### Card:` structure (Prompt/Answer/Why it matters/Common trap/Related — the format this deliverable's template follows directly), and a plainer `**Q:**`/`**A:**` format used in the newer `jvm/`, `security/`, and `testing/` chapters. Decks built from the second format require light, non-fabricated synthesis to add the "Why it matters" and "Common trap" fields the template requires — grounded in that chapter's own stated interview framing and common-mistakes material, never invented.

**Batches 1–3 cover the `databases/`, `java-core/`, and `collections/` domains: 16 decks, 52 cards, all from the `### Card:` format** — three bounded batches of 75 total chapters (238 real cards: 169 in `### Card:` format, 69 in `**Q:**` format) per `CLAUDE.md`'s instruction against generating an entire deliverable in one operation. Remaining domains are built in subsequent batches.

## Decks

| # | Deck | Topic ID | Cards | Canonical Chapter |
|---|---|---|---|---|
| 1 | [Database Index Structures](index-structures-btree-composite-covering.md) | T-609 | 5 | `handbook/databases/index-structures-btree-composite-covering.md` |
| 2 | [Isolation Levels and Concurrency Anomalies](isolation-levels-and-concurrency-anomalies.md) | T-611 | 4 | `handbook/databases/isolation-levels-and-concurrency-anomalies.md` |
| 3 | [Query Planning and EXPLAIN ANALYZE](query-planning-and-explain-analyze.md) | T-610 | 4 | `handbook/databases/query-planning-and-explain-analyze.md` |
| 4 | [Data Modelling and Explicit Join Tables](data-modelling-and-explicit-join-tables.md) | T-605/T-608 | 3 | `handbook/databases/data-modelling-and-explicit-join-tables.md` |
| 5 | [Table Partitioning and Sharding Strategies](table-partitioning-and-sharding-strategies.md) | T-614 | 3 | `handbook/databases/table-partitioning-and-sharding-strategies.md` |
| 6 | [Zero-Downtime Schema Migration](zero-downtime-schema-migration.md) | T-616 | 3 | `handbook/databases/zero-downtime-schema-migration.md` |
| 7 | [equals(), hashCode(), and Comparable Contracts](equals-hashcode-and-comparable-contracts.md) | T-101 | 3 | `handbook/java-core/equals-hashcode-and-comparable-contracts.md` |
| 8 | [Exception Design and Hierarchy Strategy](exception-design-and-hierarchy-strategy.md) | T-105 | 3 | `handbook/java-core/exception-design-and-hierarchy-strategy.md` |
| 9 | [Generics: Erasure, Variance, and PECS](generics-erasure-and-pecs.md) | T-104 | 3 | `handbook/java-core/generics-erasure-and-pecs.md` |
| 10 | [Immutability and Defensive Copying](immutability-and-defensive-copying.md) | T-103 | 3 | `handbook/java-core/immutability-and-defensive-copying.md` |
| 11 | [Streams and Collectors](streams-and-collectors.md) | T-107 | 3 | `handbook/java-core/streams-and-collectors.md` |
| 12 | [HashMap Internals](hashmap-internals.md) | T-201 | 3 | `handbook/collections/hashmap-internals.md` |
| 13 | [ArrayList and LinkedList Internals](arraylist-and-linkedlist-internals.md) | T-202 | 3 | `handbook/collections/arraylist-and-linkedlist-internals.md` |
| 14 | [ConcurrentHashMap Internals](concurrenthashmap-internals.md) | T-205 | 3 | `handbook/collections/concurrenthashmap-internals.md` |
| 15 | [BlockingQueue Family and Producer-Consumer](blockingqueue-family.md) | T-207 | 3 | `handbook/collections/blockingqueue-family.md` |
| 16 | [Collection Selection Decision Matrix](collection-selection-decision-matrix.md) | T-209 | 3 | `handbook/collections/collection-selection-decision-matrix.md` |

## How this relates to other deliverables

- `handbook/` — the canonical chapters these cards are extracted from; each card's `## Flashcards` section in its source chapter remains in place (unchanged) as the chapter's own embedded review aid.
- `cheat-sheets/` — a different grain and moment: a cheat sheet is a one-page whole-chapter refresh for the day before an interview; a flashcard is one atomic Q/A pair for ongoing spaced-repetition drilling. Neither restates the other's content verbatim.
- `00-project/learning-roadmap.md` — the source of the `Rev` (revision interval) scheduling concept this deliverable exists to make mechanically possible.
