---
title: "T-610 · Query Planning and EXPLAIN"
topic_id: T-610
domain: Database
tier: Advanced
iwi: 7.90
prerequisites: [T-609]
unlocks: [T-611]
week: 2
last_reviewed: 2026-07-30
canonical: ../../handbook/databases/query-planning-and-explain-analyze.md
---

# T-610 · Query Planning and EXPLAIN

**IWI 7.90 · Advanced tier · Prerequisite:** T-609 (Week 1) — reading a plan requires already knowing what an index does

**Canonical chapter:** [Query Planning and EXPLAIN ANALYZE](../../handbook/databases/query-planning-and-explain-analyze.md). This file is the Week 2 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because other Week 2 deliverables (`06-answer-frameworks.md`, the mock interview, the checklist) cite them directly.

**Verification note:** all three scenarios behind this summary are real, executed PostgreSQL 16 output from a disposable Docker container, seeded with the same 300,000-row `orders` / 5,000-row `customers` schema as Week 1. Source and full output: `practice/sql/week-02/query-plan-lab.sql` and `query-plan-lab-output.txt`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Three real diagnosed scenarios](#3-three-real-diagnosed-scenarios)
4. [Join algorithms — when the planner picks each](#4-join-algorithms--when-the-planner-picks-each)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

`EXPLAIN` shows the plan the optimizer chose; `EXPLAIN ANALYZE` actually runs the query and adds real timings and row counts, which is what turns a plan from descriptive into diagnosable. → [Definition and Purpose](../../handbook/databases/query-planning-and-explain-analyze.md#definition-and-purpose).

## 2. Why it exists

The same SQL can execute a dozen structurally different ways at wildly different speeds — "the query is slow" is a symptom, not a diagnosis, and the plan is the only place the actual mechanism is visible. → [Definition and Purpose](../../handbook/databases/query-planning-and-explain-analyze.md#definition-and-purpose).

## 3. Three real diagnosed scenarios

Scenario 1: a missing index on a join column produced only an honest, modest 18.446ms → 18.074ms improvement, because the new index didn't touch the plan's actual bottleneck. Scenario 2: `UPPER(status) = 'REFUNDED'` defeated a plain index on `status`; an expression index fixed it, 21.614ms → 5.805ms (~3.7×). Scenario 3: a forced nested loop (47.811ms) vs. the planner's free hash-join choice (35.048ms), with `Memoize` softening the nested-loop cost. → [Internal Implementation](../../handbook/databases/query-planning-and-explain-analyze.md#internal-implementation) has the full `EXPLAIN` output for all three.

## 4. Join algorithms — when the planner picks each

Nested loop wins when one side is small or a selective index lookup exists per outer row; hash join wins when neither side is small enough, on equality; merge join wins when both sides are already sorted on the join key. → [Core Concepts, join algorithms](../../handbook/databases/query-planning-and-explain-analyze.md#core-concepts).

## 5. Trade-offs

Trusting the planner's free choice is right the overwhelming majority of the time; forcing a join strategy via `SET enable_*` is a diagnostic tool only, never production configuration. → [Trade-offs](../../handbook/databases/query-planning-and-explain-analyze.md#trade-offs).

## 6. Interview questions

1. Read `EXPLAIN ANALYZE` line by line. What is `rows=1000` vs `actual rows=48000` telling you?
2. Nested loop vs hash join vs merge join — when does the planner pick each?
3. You added an index and the query got slower. Give two distinct mechanisms.
4. Why did the planner ignore your index? Three reasons.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/databases/query-planning-and-explain-analyze.md#interview-questions).

## 7. Common mistakes

Reading only the top line of a plan and missing where cost actually concentrates; assuming an added index will help without checking which side of the join dominates cost; using `enable_hashjoin`-style overrides as a production fix. → [Common Mistakes](../../handbook/databases/query-planning-and-explain-analyze.md#common-mistakes).

## 8. Staff-level discussion

Query-plan literacy is what separates "the query is slow, add an index" from "the query is slow because of X specific mechanism, here's the fix and here's what it costs." → [Staff-Level Discussion](../../handbook/databases/query-planning-and-explain-analyze.md#interview-answer-framework).

## 9. Summary

`EXPLAIN ANALYZE` shows the actual plan, not just the estimated one — this chapter's three scenarios show a modest missing-index win, a ~3.7× expression-index fix, and a ~1.4× join-algorithm comparison — three different mechanisms, not one universal "just add an index" story. → [Summary](../../handbook/databases/query-planning-and-explain-analyze.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/databases/query-planning-and-explain-analyze.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/databases/query-planning-and-explain-analyze.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/databases/query-planning-and-explain-analyze.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/databases/query-planning-and-explain-analyze.md#practice-exercises) and [Solutions](../../handbook/databases/query-planning-and-explain-analyze.md#solutions). Reproducible lab: `practice/sql/week-02/query-plan-lab.sql`.

## 14. Additional Reading

- Markus Winand, *Use The Index, Luke*, Ch. 4 "The Join Operation"

## 15. Official References

- PostgreSQL documentation, [Ch. 14.1 "Using EXPLAIN"](https://www.postgresql.org/docs/current/using-explain.html)
- PostgreSQL documentation, [Ch. 14.2 "Statistics Used by the Planner"](https://www.postgresql.org/docs/current/planner-stats.html)
