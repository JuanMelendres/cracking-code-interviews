---
title: "Databases — Domain Index"
document_type: syllabus-domain-index
domain: 06-databases
status: 14 of 14 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Databases

Relational modeling, indexing, query planning, isolation levels, MVCC, replication, and partitioning — PostgreSQL-focused. Existing `handbook/databases/` (14 chapters) relocates here unchanged in content.

> **Phase 3 update (2026-09-03).** This domain's full existing content (14 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 14 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each Level 1/Level 2 pair is grounded in that chapter's own real subject (a book-index analogy for B+Tree indexes, a "diary vs. shared notice board" framing for L1 vs. L2 Hibernate caching, a doorway-standoff analogy for deadlocks) rather than a generic template. Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`06-databases` is now fully L1–L4 (14/14)** — the fourth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-601/T-602 | JPA Entity Lifecycle, the Persistence Context, and the N+1 Problem | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md` |
| T-603 | Hibernate Second-Level and Query Cache | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/hibernate-second-level-and-query-cache.md` |
| T-604 | Optimistic vs. Pessimistic Locking | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/optimistic-vs-pessimistic-locking.md` |
| T-605/T-608 | Data Modelling and Explicit Join Tables | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/data-modelling-and-explicit-join-tables.md` |
| T-606 | Hibernate Flush Modes and Batch Writes | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/hibernate-flush-modes-and-batch-writes.md` |
| T-607 | Connection Pooling and Sizing (HikariCP) | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/connection-pooling-and-sizing.md` |
| T-609 | Database Index Structures — B+Tree, Composite, Covering | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/index-structures-btree-composite-covering.md` |
| T-610 | Query Planning and EXPLAIN ANALYZE | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/query-planning-and-explain-analyze.md` |
| T-611 | Isolation Levels and Concurrency Anomalies | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md` |
| T-612 | MVCC in PostgreSQL, Vacuum, and Bloat | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/mvcc-vacuum-and-bloat.md` |
| T-613 | Locks, Deadlocks, and Lock Escalation in RDBMS | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/locks-deadlocks-and-lock-escalation.md` |
| T-614 | Table Partitioning and Sharding Strategies | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/table-partitioning-and-sharding-strategies.md` |
| T-615 | Replication, Read Replicas, and Replica Lag | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/replication-read-replicas-and-replica-lag.md` |
| T-616 | Zero-Downtime Schema Migration | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/06-databases/zero-downtime-schema-migration.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
