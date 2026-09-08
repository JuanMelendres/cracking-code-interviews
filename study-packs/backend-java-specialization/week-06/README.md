---
title: "Backend Java Specialization, Week 6 — Databases, Part 1: JPA and Hibernate Mechanics"
document_type: study-pack
week: 6
track: backend-java-specialization
status: draft
estimated_hours: 9
---

# Week 6 — Databases, Part 1: JPA and Hibernate Mechanics

## Weekly Outcome

By the end of this week you can explain relational modelling with explicit join tables, the JPA entity lifecycle and why it causes the N+1 problem, Hibernate's first- and second-level caches, optimistic versus pessimistic locking, flush modes and batch writes, and how to size a HikariCP connection pool with a defensible reason.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) places Databases directly after Spring "because its ORM chapters (JPA entity lifecycle, N+1) directly build on Spring's own persistence-layer chapters" — Week 5's Spring Data/transaction material is the container this week's JPA and Hibernate mechanics run inside. Databases is split across two weeks (this one and Week 7) because its real chapter count (15, once T-2202 is included) is the largest of any domain in this path besides Java itself.

## Prerequisites

Week 5 complete, in particular T-503/T-504/T-505 (`@Transactional` proxy mechanics) — Hibernate's flush and cache behavior is scoped to the same transaction boundary that chapter describes.

## Schedule

| Day | Focus |
|---|---|
| Mon | SQL and Relational Database Fundamentals (T-2202) — a fast review if already comfortable with joins and constraints |
| Tue | Data Modelling and Explicit Join Tables (T-605/T-608) |
| Wed–Thu | JPA Entity Lifecycle, the Persistence Context, and the N+1 Problem (T-601/T-602) |
| Fri | Hibernate Second-Level and Query Cache (T-603) |
| Sat | Optimistic vs. Pessimistic Locking (T-604); Hibernate Flush Modes and Batch Writes (T-606) |
| Sun | Connection Pooling and Sizing (T-607); review checklist below |

## Required Reading

The first half of the Databases domain, per [`syllabus/06-databases/INDEX.md`](../../../syllabus/06-databases/INDEX.md) (the exhaustive, canonical source) — 7 of the domain's 15 topics. The remaining 8 (indexing, query planning, and operational concerns) are Week 7's reading.

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-2202 — SQL and Relational Database Fundamentals | [`sql-and-relational-database-fundamentals.md`](../../../syllabus/06-databases/sql-and-relational-database-fundamentals.md) |
| 2 | T-605/T-608 — Data Modelling and Explicit Join Tables | [`data-modelling-and-explicit-join-tables.md`](../../../syllabus/06-databases/data-modelling-and-explicit-join-tables.md) |
| 3 | T-601/T-602 — JPA Entity Lifecycle, the Persistence Context, and the N+1 Problem | [`jpa-entity-lifecycle-and-the-n1-problem.md`](../../../syllabus/06-databases/jpa-entity-lifecycle-and-the-n1-problem.md) |
| 4 | T-603 — Hibernate Second-Level and Query Cache | [`hibernate-second-level-and-query-cache.md`](../../../syllabus/06-databases/hibernate-second-level-and-query-cache.md) |
| 5 | T-604 — Optimistic vs. Pessimistic Locking | [`optimistic-vs-pessimistic-locking.md`](../../../syllabus/06-databases/optimistic-vs-pessimistic-locking.md) |
| 6 | T-606 — Hibernate Flush Modes and Batch Writes | [`hibernate-flush-modes-and-batch-writes.md`](../../../syllabus/06-databases/hibernate-flush-modes-and-batch-writes.md) |
| 7 | T-607 — Connection Pooling and Sizing (HikariCP) | [`connection-pooling-and-sizing.md`](../../../syllabus/06-databases/connection-pooling-and-sizing.md) |

## Hands-On Exercises

Real, compiled/executed demos exist for all 7 chapters:

- [`practice/sql/sql-fundamentals/`](../../../practice/sql/sql-fundamentals/) (T-2202 — a real PostgreSQL 16 lab in a disposable Docker container)
- [`practice/sql/week-02/many-to-many-lab.sql`](../../../practice/sql/week-02/many-to-many-lab.sql) (T-605/T-608 — real executed PostgreSQL 16 output, including a genuine data-integrity bug)
- [`practice/java/hibernate-jpa/entity-lifecycle-and-n1/`](../../../practice/java/hibernate-jpa/entity-lifecycle-and-n1/) (T-601/T-602)
- [`practice/java/hibernate-jpa/second-level-and-query-cache/`](../../../practice/java/hibernate-jpa/second-level-and-query-cache/) (T-603)
- [`practice/java/hibernate-jpa/optimistic-vs-pessimistic-locking/`](../../../practice/java/hibernate-jpa/optimistic-vs-pessimistic-locking/) (T-604)
- [`practice/java/hibernate-jpa/flush-modes-and-batch-writes/`](../../../practice/java/hibernate-jpa/flush-modes-and-batch-writes/) (T-606)
- [`practice/java/databases/connection-pooling-and-sizing/`](../../../practice/java/databases/connection-pooling-and-sizing/) (T-607)

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "why does lazy loading a collection inside a loop cause N+1 queries, and what are the three ways to fix it?" and "when would you choose optimistic locking over pessimistic locking, and what does each cost under contention?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given an entity graph with a lazy `@OneToMany` collection accessed inside a loop, identify the N+1 query pattern from a logged SQL trace and name the fix (`JOIN FETCH`, `@EntityGraph`, or batch fetching) before checking T-601/T-602's own worked example.

## Review Checklist

- [ ] Completed all 7 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 5 of the 7 real demos listed above.
- [ ] Can explain, unprompted, the difference between Hibernate's first-level (session-scoped) and second-level (`SessionFactory`-scoped) caches.

## Completion Criteria

- [ ] Can diagram an entity's lifecycle states (transient, managed, detached, removed) from memory.
- [ ] Can explain why an explicit join-table entity is sometimes preferable to a plain `@ManyToMany` mapping.
- [ ] Can size a HikariCP pool for a stated request rate and average query latency with a defensible formula.

## Retrospective

Note whether you have previously shipped an N+1 bug to production without noticing until a slow-query alert fired — this is one of the most common real-world Hibernate mistakes, and this week's chapters explain exactly why it is so easy to miss in code review.

## Next Week

[Week 7 — Databases, Part 2: Indexing, Query Planning, and Operational Concerns](../week-07/README.md).
