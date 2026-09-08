---
title: "Mid → Senior, Week 4 — Database Internals"
document_type: study-pack
week: 4
track: mid-to-senior
status: draft
estimated_hours: 9
---

# Week 4 — Database Internals

## Weekly Outcome

By the end of this week you can read an `EXPLAIN ANALYZE` plan and identify the actual bottleneck, name a concrete write-skew scenario an isolation level does and doesn't prevent, and explain how a long-running transaction blocks autovacuum and bloats a table.

## Why This Week Matters

[Junior → Mid](../../junior-to-mid/README.md) Week 5–6 taught SQL usage, indexing, and data modelling. This week goes into the engine internals those chapters deferred: query planning, MVCC, and the operational consequences of getting isolation and vacuum wrong in production.

## Prerequisites

[Junior → Mid](../../junior-to-mid/README.md)'s SQL and Index Structures weeks.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | Query Planning and EXPLAIN ANALYZE |
| Wed–Thu | Isolation Levels and Concurrency Anomalies |
| Fri–Sat | MVCC in PostgreSQL, Vacuum, and Bloat |
| Sun | Review checklist below |

## Required Reading

[`syllabus/06-databases/INDEX.md`](../../../syllabus/06-databases/INDEX.md) — this week's three priority topics (Query Planning and EXPLAIN ANALYZE; Isolation Levels and Concurrency Anomalies; MVCC in PostgreSQL, Vacuum, and Bloat).

## Hands-On Exercises

Real demos exist under [`practice/sql/`](../../../practice/sql/) — `mvcc-vacuum-and-bloat/`, `locks-deadlocks-and-lock-escalation/`, `replication-and-replica-lag/`, `search-and-indexing-systems/`, plus the earlier `week-01/`–`week-03/` query-planning material. Follow the link from each priority chapter to its own matching demo.

## Production Cookbook Cross-Reference

- [`long-held-bi-transaction-blocking-autovacuum-and-bloating-a-hot-table.md`](../../../production-cookbook/long-held-bi-transaction-blocking-autovacuum-and-bloating-a-hot-table.md)
- [`assumed-mysql-lock-escalation-risk-that-doesnt-apply-to-postgresql.md`](../../../production-cookbook/assumed-mysql-lock-escalation-risk-that-doesnt-apply-to-postgresql.md)

## Interview Answer Drills

Answer, out loud: "what's the difference between a sequential scan and an index scan in an EXPLAIN plan, and when is a sequential scan actually the right choice?" and "give a concrete write-skew scenario `REPEATABLE READ` doesn't prevent" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a real `EXPLAIN ANALYZE` output (from the Query Planning chapter's own practice material), identify the bottleneck and propose one concrete fix, cold, in under 10 minutes.

## Review Checklist

- [ ] Completed all three chapters' own L3 Mastery Checklists.
- [ ] Reproduced at least 2 of the real SQL demos listed above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can read an `EXPLAIN ANALYZE` plan and identify the bottleneck unprompted.
- [ ] Can name a concrete anomaly for at least 2 of the 4 standard isolation levels.
- [ ] Can explain how vacuum works and what blocks it, with a correct example.

## Retrospective

Note whether the MySQL-vs-PostgreSQL lock-escalation cookbook entry surprised you — assuming behavior from one database applies to another is one of the most common Senior-level database mistakes this pack tracks.

## Next Week

[Week 5 — Testing at Senior Depth](../week-05/README.md).
