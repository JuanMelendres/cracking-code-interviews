---
title: "Backend Java Specialization, Week 7 — Databases, Part 2: Indexing, Query Planning, and Operational Concerns"
document_type: study-pack
week: 7
track: backend-java-specialization
status: draft
estimated_hours: 9
---

# Week 7 — Databases, Part 2: Indexing, Query Planning, and Operational Concerns

## Weekly Outcome

By the end of this week you can explain B+Tree/composite/covering index structures, read an `EXPLAIN ANALYZE` plan, name the concurrency anomalies each isolation level prevents, explain PostgreSQL's MVCC and vacuum mechanics, diagnose a database-level deadlock, evaluate partitioning/sharding strategies, reason about replication lag, and run a zero-downtime schema migration.

## Why This Week Matters

This week closes the Databases domain that Week 6 opened — Week 6 covered how an application (via JPA/Hibernate) generates and manages queries; this week covers how the database itself executes, isolates, and scales them. [`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) groups these together as "indexing/query planning → concurrency/replication/migration" — the natural progression from "is this query fast" to "is this database correct and available under load."

## Prerequisites

Week 6 complete, in particular T-2202 (SQL Fundamentals) and T-601/T-602 (JPA Entity Lifecycle) — this week's index and query-planning chapters directly address the N+1 and slow-query patterns those chapters introduce.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | Database Index Structures — B+Tree, Composite, Covering (T-609) |
| Wed | Query Planning and EXPLAIN ANALYZE (T-610) |
| Thu | Isolation Levels and Concurrency Anomalies (T-611) |
| Fri | MVCC in PostgreSQL, Vacuum, and Bloat (T-612); Locks, Deadlocks, and Lock Escalation (T-613) |
| Sat | Table Partitioning and Sharding Strategies (T-614); Replication, Read Replicas, and Replica Lag (T-615) |
| Sun | Zero-Downtime Schema Migration (T-616); review checklist below |

## Required Reading

The second half of the Databases domain, per [`syllabus/06-databases/INDEX.md`](../../../syllabus/06-databases/INDEX.md) (the exhaustive, canonical source) — the remaining 8 of the domain's 15 topics.

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-609 — Database Index Structures — B+Tree, Composite, Covering | [`index-structures-btree-composite-covering.md`](../../../syllabus/06-databases/index-structures-btree-composite-covering.md) |
| 2 | T-610 — Query Planning and EXPLAIN ANALYZE | [`query-planning-and-explain-analyze.md`](../../../syllabus/06-databases/query-planning-and-explain-analyze.md) |
| 3 | T-611 — Isolation Levels and Concurrency Anomalies | [`isolation-levels-and-concurrency-anomalies.md`](../../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md) |
| 4 | T-612 — MVCC in PostgreSQL, Vacuum, and Bloat | [`mvcc-vacuum-and-bloat.md`](../../../syllabus/06-databases/mvcc-vacuum-and-bloat.md) |
| 5 | T-613 — Locks, Deadlocks, and Lock Escalation in RDBMS | [`locks-deadlocks-and-lock-escalation.md`](../../../syllabus/06-databases/locks-deadlocks-and-lock-escalation.md) |
| 6 | T-614 — Table Partitioning and Sharding Strategies | [`table-partitioning-and-sharding-strategies.md`](../../../syllabus/06-databases/table-partitioning-and-sharding-strategies.md) |
| 7 | T-615 — Replication, Read Replicas, and Replica Lag | [`replication-read-replicas-and-replica-lag.md`](../../../syllabus/06-databases/replication-read-replicas-and-replica-lag.md) |
| 8 | T-616 — Zero-Downtime Schema Migration | [`zero-downtime-schema-migration.md`](../../../syllabus/06-databases/zero-downtime-schema-migration.md) |

## Hands-On Exercises

Real, executed PostgreSQL 16 demos exist for all 8 chapters (mostly disposable Docker containers, per each chapter's own provenance note):

- [`practice/sql/week-01/index-lab.sql`](../../../practice/sql/week-01/index-lab.sql) (T-609 — a seeded 300,000-row `orders` table)
- [`practice/sql/week-02/query-plan-lab.sql`](../../../practice/sql/week-02/query-plan-lab.sql) (T-610 — same seeded schema as T-609)
- [`practice/sql/week-03/write-skew-setup.sql`](../../../practice/sql/week-03/write-skew-setup.sql) and [`write-skew-tx.sh`](../../../practice/sql/week-03/write-skew-tx.sh) (T-611 — a real write-skew reproduction from two genuinely concurrent `psql` sessions)
- [`practice/sql/mvcc-vacuum-and-bloat/`](../../../practice/sql/mvcc-vacuum-and-bloat/) (T-612)
- [`practice/sql/locks-deadlocks-and-lock-escalation/`](../../../practice/sql/locks-deadlocks-and-lock-escalation/) (T-613)
- [`practice/sql/week-10/sharding/`](../../../practice/sql/week-10/sharding/) (T-614 — a genuine hash-partitioned table with 40,000 seeded rows across 4 partitions)
- [`practice/sql/replication-and-replica-lag/`](../../../practice/sql/replication-and-replica-lag/) (T-615)
- [`practice/sql/week-10/zero-downtime-migration/`](../../../practice/sql/week-10/zero-downtime-migration/) (T-616 — real measured blocking-vs-`CONCURRENTLY` timings on a 2-million-row table)

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "what specific anomaly does `REPEATABLE READ` prevent that `READ COMMITTED` does not, and what can still go wrong at `REPEATABLE READ`?" and "why does `CREATE INDEX CONCURRENTLY` take longer but avoid blocking writes?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a real `EXPLAIN ANALYZE` output showing a sequential scan on a large table with a highly selective `WHERE` clause, identify why the planner chose it and what index would fix it, before checking T-610's own worked example.

## Review Checklist

- [ ] Completed all 8 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 5 of the 8 real demos listed above.
- [ ] Can explain, unprompted, why `VACUUM` is necessary in PostgreSQL specifically because of how MVCC implements row versioning.

## Completion Criteria

- [ ] Can read an `EXPLAIN ANALYZE` plan and identify whether an index is being used, and why or why not.
- [ ] Can name the write-skew anomaly and reproduce the two-session scenario that causes it.
- [ ] Can state the trade-off between range and hash partitioning for a given query pattern.
- [ ] Can describe a real zero-downtime migration strategy for adding a `NOT NULL` column to a large, live table.

## Retrospective

Note which of this week's 8 topics you would need a second pass on before a live system-design interview — index selection and isolation-level trade-offs are two of the highest-frequency Staff-level database discussion points, and shallow familiarity here shows up quickly under follow-up questioning.

## Next Week

[Week 8 — Messaging and Event-Driven Systems](../week-08/README.md).
