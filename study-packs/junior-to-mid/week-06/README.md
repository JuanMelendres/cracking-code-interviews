---
title: "Junior → Mid, Week 6 — Databases and Testing"
document_type: study-pack
week: 6
track: junior-to-mid
status: draft
estimated_hours: 8
---

# Week 6 — Databases and Testing

## Weekly Outcome

By the end of this week you can explain why an index speeds up a query using a B+Tree's structure, model a many-to-many relationship with an explicit join table instead of a denormalized shortcut, write a correct JUnit test with setup/teardown, and explain the difference between a unit test, an integration test, and a mock.

## Why This Week Matters

Week 5 taught SQL usage; this week goes one layer deeper into *why* queries are fast or slow ([Database Index Structures](../../../syllabus/06-databases/index-structures-btree-composite-covering.md)) and how to model relationships correctly ([Data Modelling and Explicit Join Tables](../../../syllabus/06-databases/data-modelling-and-explicit-join-tables.md)) — the same usage-then-internals pattern already applied twice in this pack. Testing is introduced here, before Week 7's Spring/REST work, so that every controller and service built next week can be tested as it's written, not bolted on afterward.

## Prerequisites

Week 5 — comfortable writing a `SELECT` with a `JOIN` before this week's index and modelling material.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Database Index Structures — B+Tree, Composite, Covering](../../../syllabus/06-databases/index-structures-btree-composite-covering.md) (T-609) |
| Wed | [Data Modelling and Explicit Join Tables](../../../syllabus/06-databases/data-modelling-and-explicit-join-tables.md) (T-605/T-608) |
| Thu–Fri | [Unit Testing Fundamentals with JUnit](../../../syllabus/08-testing/unit-testing-fundamentals-with-junit.md) (T-2204) — read in full, reproduce the JUnit demo |
| Sat | [Test Strategy, the Pyramid, and Test Doubles](../../../syllabus/08-testing/test-strategy-and-test-doubles.md) (T-1101/T-1103) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Database Index Structures (T-609) | [`syllabus/06-databases/index-structures-btree-composite-covering.md`](../../../syllabus/06-databases/index-structures-btree-composite-covering.md) |
| 2 | Data Modelling and Explicit Join Tables (T-605/T-608) | [`syllabus/06-databases/data-modelling-and-explicit-join-tables.md`](../../../syllabus/06-databases/data-modelling-and-explicit-join-tables.md) |
| 3 | Unit Testing Fundamentals with JUnit (T-2204) | [`syllabus/08-testing/unit-testing-fundamentals-with-junit.md`](../../../syllabus/08-testing/unit-testing-fundamentals-with-junit.md) |
| 4 | Test Strategy and Test Doubles (T-1101/T-1103) | [`syllabus/08-testing/test-strategy-and-test-doubles.md`](../../../syllabus/08-testing/test-strategy-and-test-doubles.md) |

## Hands-On Exercises

- T-609 and T-605/T-608's own practice material — follow the links from each chapter.
- [`practice/java/testing-fundamentals/junit-basics/`](../../../practice/java/testing-fundamentals/junit-basics/) — `Calculator.java`/`CalculatorTest.java`, with both a real passing test run (`test-run-output.txt`) and a real, deliberately-produced failing assertion (`real-failure-output.txt`, `expected: <6> but was: <5>`) kept as genuine evidence of what a real JUnit failure looks like.
- T-1101/T-1103's own practice material on test doubles.

## Interview Answer Drills

Answer, out loud: "why does a composite index's column order matter?" and "what's the difference between a mock and a stub?" before checking each chapter's expected answer.

## Coding Problems

None dedicated this week — this week is conceptual (databases, testing), not DSA pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a slow query on a table with a two-column `WHERE` clause, explain what composite index you'd add and why, out loud, in under 2 minutes.

## Review Checklist

- [ ] Reproduced the JUnit demo, saw both the passing run and the real failing-assertion output.
- [ ] Can explain a B+Tree index lookup's rough cost (why it's not a full table scan) unprompted.
- [ ] Can state, from memory, the test pyramid's shape and why unit tests should outnumber integration tests.

## Completion Criteria

- [ ] Can design a join table for a stated many-to-many relationship (e.g., students and courses) with correct foreign keys.
- [ ] Wrote a new JUnit test from scratch (not copied) for a small class of your own, including one `assertThrows` case.
- [ ] Can explain when to reach for a mock versus a real test database, with a stated reason.

## Retrospective

Note whether the deliberately-broken JUnit test's failure message made the actual defect obvious or not — this is a preview of what real production test-failure triage feels like.

## Next Week

[Week 7 — Ship Something](../week-07/README.md).
