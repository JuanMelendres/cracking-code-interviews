---
title: "T-609 · Database Index Structures"
topic_id: T-609
domain: Database
tier: Advanced
iwi: 8.30
prerequisites: []
unlocks: [T-610, T-611]
week: 1
last_reviewed: 2026-07-30
canonical: ../../handbook/databases/index-structures-btree-composite-covering.md
---

# T-609 · Database Index Structures

**IWI 8.30 · Advanced tier · Prerequisite for:** T-610 (query planning), T-611 (isolation levels), most of Chapter 08

**Canonical chapter:** [Database Index Structures — B+Tree, Composite, Covering](../../syllabus/06-databases/index-structures-btree-composite-covering.md). This file is the Week 1 study-pack entry point — a short summary of each section plus a link to the full canonical treatment (Mental Model, Historical Context, full Java/Spring examples, Production Scenario, Decision Framework, etc.). Section numbers below are kept stable because other Week 1/6 deliverables (the mock interview, the checklist, `03-technical-answer-framework.md`) cite them directly.

**Verification note:** every `EXPLAIN` block behind this summary is real output from PostgreSQL 16, run in a disposable Docker container against a seeded 300,000-row `orders` table (5,000 customers). See `MANIFEST.md` and `practice/sql/week-01/` for the exact reproducible command and full output.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [How it works internally — walking a real lookup](#3-how-it-works-internally--walking-a-real-lookup)
4. [Composite indexes and the leftmost-prefix rule](#4-composite-indexes-and-the-leftmost-prefix-rule)
5. [Covering indexes and index-only scans](#5-covering-indexes-and-index-only-scans)
6. [When the planner ignores your index — selectivity](#6-when-the-planner-ignores-your-index--selectivity)
7. [Engine-specific correction](#7-engine-specific-correction--the-feedback-item-this-week-targets)
8. [Trade-offs](#8-trade-offs)
9. [Interview questions](#9-interview-questions)
10. [Common mistakes](#10-common-mistakes)
11. [Staff-level discussion](#11-staff-level-discussion)
12. [Summary](#12-summary)
13. [Key Takeaways](#13-key-takeaways)
14. [Cheat Sheet](#14-cheat-sheet)
15. [Flashcards](#15-flashcards)
16. [Practice Exercises](#16-practice-exercises)
17. [Additional Reading](#17-additional-reading)
18. [Official References](#18-official-references)

---

## 1. The concept

A B+Tree index is a sorted, balanced tree structure letting the database find a row in `O(log n)` comparisons instead of scanning every row. → [Core Concepts](../../syllabus/06-databases/index-structures-btree-composite-covering.md#core-concepts), [Definition and Purpose](../../syllabus/06-databases/index-structures-btree-composite-covering.md#definition-and-purpose).

## 2. Why it exists

Without an index, a non-key lookup is a full sequential scan, `O(n)` — fine for small tables, ruinous at scale. An index trades write-time cost and storage for read-time speed. → [Definition and Purpose](../../syllabus/06-databases/index-structures-btree-composite-covering.md#definition-and-purpose).

## 3. How it works internally — walking a real lookup

Measured: a point lookup on `customer_id` over 300,000 rows goes from 5.754ms (sequential scan, `Rows Removed by Filter: 149970`) to 0.111ms (`Bitmap Index Scan`) after adding an index — a real ~52× improvement. → [Internal Implementation](../../syllabus/06-databases/index-structures-btree-composite-covering.md#internal-implementation) has the full `EXPLAIN` output and the root-to-heap walkthrough.

## 4. Composite indexes and the leftmost-prefix rule

An index on `(customer_id, created_at)` serves queries filtering `customer_id` alone or both columns together — **never** `created_at` alone, measured and confirmed via a forced sequential scan. → [Core Concepts, leftmost-prefix rule](../../syllabus/06-databases/index-structures-btree-composite-covering.md#core-concepts).

## 5. Covering indexes and index-only scans

An `INCLUDE` column lets a query skip the heap entirely (`Index Only Scan`, `Heap Fetches: 0`) — but only when the planner's cost model *and* the visibility map (recent `VACUUM`) both cooperate. → [Internal Implementation, covering index measured](../../syllabus/06-databases/index-structures-btree-composite-covering.md#internal-implementation).

## 6. When the planner ignores your index — selectivity

The planner correctly ignores a low-selectivity index — no fixed percentage, it's a random-I/O-vs-sequential-I/O cost comparison. → [Core Concepts, selectivity](../../syllabus/06-databases/index-structures-btree-composite-covering.md#core-concepts).

## 7. Engine-specific correction — the feedback item this week targets

PostgreSQL has **no clustered-index concept** — every table is a heap, every index (including the PK) is secondary. This differs from InnoDB, where the primary key *is* the clustered index. Naming the engine explicitly is the correction this week's interview feedback specifically targets. → [Comparisons](../../syllabus/06-databases/index-structures-btree-composite-covering.md#comparisons), [Historical Context](../../syllabus/06-databases/index-structures-btree-composite-covering.md#historical-context).

## 8. Trade-offs

`O(log n)` lookups cost write-time maintenance on every index per write, plus storage. Wrong composite column order makes an index invisible to the queries it was meant for. → [Trade-offs](../../syllabus/06-databases/index-structures-btree-composite-covering.md#trade-offs).

## 9. Interview questions

1. How does a B+Tree index actually find a row? Walk it root to heap.
2. Index on `(customer_id, created_at)` — which queries does it serve, which does it not, and why?
3. What is a covering index, and how do you know from `EXPLAIN` that you got one?
4. When is a sequential scan faster than an index scan?
5. You added an index and the query got slower. Give two distinct mechanisms.
6. Why did the planner ignore your index? Three reasons.
7. Clustered vs non-clustered — and what changes when the engine is PostgreSQL rather than InnoDB?
8. What is an index-only scan, and what has to be true for PostgreSQL to actually choose one?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/06-databases/index-structures-btree-composite-covering.md#interview-questions).

## 10. Common mistakes

Assuming "I added an index" is sufficient without checking `EXPLAIN`; indexing every column defensively; forgetting `ANALYZE` after bulk loads; conflating "has an index" with "uses that index for this query." → [Common Mistakes](../../syllabus/06-databases/index-structures-btree-composite-covering.md#common-mistakes).

## 11. Staff-level discussion

Indexing is write-path capacity planning, not just a read-side optimization — a table with 15 indexes has 15× the write amplification per insert. → [Staff-Level Discussion](../../syllabus/06-databases/index-structures-btree-composite-covering.md#interview-answer-framework).

## 12. Summary

Indexes trade write-time cost and storage for read-time speed via `O(log n)` lookups; composite indexes only serve their leftmost-prefix combinations; a covering index needs both planner cooperation and a current visibility map. → [Summary](../../syllabus/06-databases/index-structures-btree-composite-covering.md#summary).

## 13. Key Takeaways

→ [Key Takeaways](../../syllabus/06-databases/index-structures-btree-composite-covering.md#key-takeaways).

## 14. Cheat Sheet

→ [Cheat Sheet](../../syllabus/06-databases/index-structures-btree-composite-covering.md#cheat-sheet).

## 15. Flashcards

→ [Flashcards](../../syllabus/06-databases/index-structures-btree-composite-covering.md#flashcards). Full week-level deck, including T-901 cards: `08-flashcards.md`.

## 16. Practice Exercises

→ [Practice Exercises](../../syllabus/06-databases/index-structures-btree-composite-covering.md#practice-exercises) and [Solutions](../../syllabus/06-databases/index-structures-btree-composite-covering.md#solutions). Reproducible lab: `practice/sql/week-01/index-lab.sql`.

## 17. Additional Reading

- Markus Winand, *Use The Index, Luke* — Ch. 1–3, also free online at [use-the-index-luke.com](https://use-the-index-luke.com/)

## 18. Official References

- PostgreSQL documentation, [Ch. 11 "Indexes"](https://www.postgresql.org/docs/current/indexes.html)
- PostgreSQL documentation, [Ch. 14 "Performance Tips"](https://www.postgresql.org/docs/current/performance-tips.html)
