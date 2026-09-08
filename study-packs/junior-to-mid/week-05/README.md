---
title: "Junior → Mid, Week 5 — Coding Patterns, Part 2, and First SQL"
document_type: study-pack
week: 5
track: junior-to-mid
status: draft
estimated_hours: 7
---

# Week 5 — Coding Patterns, Part 2, and First SQL

## Weekly Outcome

By the end of this week you can implement binary search correctly on the first try (including the search-on-answer variant), manipulate a linked list in place without losing a reference, and write a correct `SELECT` with a `JOIN` and a `WHERE` clause against a real schema.

## Why This Week Matters

Binary search and linked-list manipulation are the two remaining foundational coding patterns this pack covers, closing out the DSA sequence started in Week 4. SQL is introduced this week as a deliberate pivot — [SQL and Relational Database Fundamentals](../../../syllabus/06-databases/sql-and-relational-database-fundamentals.md) (T-2202) is the direct prerequisite for Week 6's index-structures and data-modelling chapters, exactly the same "usage before internals" sequencing this pack already applied to Java collections in Weeks 2–3.

## Prerequisites

Week 4 — comfortable narrating a coding approach before implementing it. No prior SQL exposure assumed.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Binary Search, Including Search-on-Answer](../../../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md) (T-2103) |
| Wed–Thu | [Linked Lists and In-Place Manipulation](../../../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md) (T-2104) |
| Fri–Sat | [SQL and Relational Database Fundamentals](../../../syllabus/06-databases/sql-and-relational-database-fundamentals.md) (T-2202) — read in full, reproduce the fundamentals lab |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Binary Search and Search-on-Answer (T-2103) | [`syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md`](../../../syllabus/03-data-structures-algorithms/binary-search-and-search-on-answer.md) |
| 2 | Linked Lists and In-Place Manipulation (T-2104) | [`syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md`](../../../syllabus/03-data-structures-algorithms/linked-lists-and-in-place-manipulation.md) |
| 3 | SQL and Relational Database Fundamentals (T-2202) | [`syllabus/06-databases/sql-and-relational-database-fundamentals.md`](../../../syllabus/06-databases/sql-and-relational-database-fundamentals.md) |

## Hands-On Exercises

- T-2103 and T-2104's own practice-problem lists — follow the links from each chapter.
- [`practice/sql/sql-fundamentals/`](../../../practice/sql/sql-fundamentals/) — a real PostgreSQL 16 lab run in Docker (`fundamentals-lab.sql` plus its real captured `fundamentals-lab-output.txt`). Run it yourself against a local Postgres container rather than only reading the transcript.

## Interview Answer Drills

For binary search specifically: state the loop-invariant you're maintaining (`lo`/`hi`/`mid`) out loud before writing any code — this is the single most common source of off-by-one bugs in this pattern.

## Coding Problems

T-2103 and T-2104's own practice problems, timed at 25 minutes each, same discipline as Week 4.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: implement binary search from a blank file, cold, in under 10 minutes, including the boundary condition (`lo <= hi` vs. `lo < hi`) stated correctly before you type it.

## Review Checklist

- [ ] Solved every practice problem in T-2103 and T-2104.
- [ ] Reproduced the SQL fundamentals lab and got the same real output as `fundamentals-lab-output.txt`.
- [ ] Can write a `JOIN` between two tables from memory, using correct PostgreSQL syntax, without copying from the chapter.

## Completion Criteria

- [ ] Implemented binary search and one search-on-answer variant correctly, unaided.
- [ ] Solved at least one in-place linked-list manipulation problem (e.g., reversal) without losing a node reference.
- [ ] Wrote and ran a `SELECT` with a `JOIN` against the fundamentals lab schema, got a correct result set.

## Retrospective

Note whether your binary search bug (if any) was an off-by-one in the loop bound or in the midpoint calculation — this distinction matters for how you debug the next one.

## Next Week

[Week 6 — Databases and Testing](../week-06/README.md).
