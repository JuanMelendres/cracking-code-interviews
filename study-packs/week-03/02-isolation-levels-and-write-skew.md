---
title: "T-611 · Isolation Levels and Write Skew"
topic_id: T-611
domain: Database
tier: Advanced
iwi: 7.95
prerequisites: [T-609, T-610]
unlocks: []
week: 3
last_reviewed: 2026-07-30
canonical: ../../handbook/databases/isolation-levels-and-concurrency-anomalies.md
---

# T-611 · Isolation Levels and Write Skew

**IWI 7.95 · Advanced tier · The discriminating question this chapter builds toward: "explain write skew with a concrete example."**

**Canonical chapter:** [Isolation Levels and Concurrency Anomalies](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md). This file is the Week 3 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because other deliverables (the Week 3 checkpoint mock, the ride-hailing design exercise, Week 6's weak-list repair) cite them directly.

**Verification note:** the write-skew reproduction and prevention behind this summary are real, executed PostgreSQL 16 output from two genuinely concurrent `psql` sessions. Source: `practice/sql/week-03/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Write skew, reproduced and prevented](#3-write-skew-reproduced-and-prevented)
4. [Isolation levels, walked through](#4-isolation-levels-walked-through)
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

An isolation level defines how much one transaction can see of another transaction's uncommitted or concurrent work. Weaker isolation allows more concurrency at the cost of more anomalies; stronger isolation prevents more anomalies at the cost of more blocking and retries. → [Definition and Purpose](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#definition-and-purpose).

## 2. Why it exists

Without isolation, concurrent transactions can produce results no serial execution ever would — defeating the point of "transaction" as a unit of correctness. → [Definition and Purpose](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#definition-and-purpose).

## 3. Write skew, reproduced and prevented

The on-call-doctors scenario, measured: at `REPEATABLE READ`, both Alice's and Bob's transactions commit, leaving zero doctors on call — the invariant is violated even though neither transaction's own write conflicted with the other's. At `SERIALIZABLE`, identical code, one transaction aborts with a real SSI dependency-cycle error; the application must retry. → [Internal Implementation](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#internal-implementation) has the full session transcripts.

## 4. Isolation levels, walked through

READ COMMITTED prevents dirty reads only; REPEATABLE READ additionally prevents non-repeatable reads and same-row lost updates (but not write skew); SERIALIZABLE additionally prevents write skew via runtime dependency tracking (SSI). → [Core Concepts](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#core-concepts) has the full prevents/allows table and the balance-read-then-write walkthrough at all three levels.

## 5. Trade-offs

READ COMMITTED gives the highest concurrency but requires defensive application code; SERIALIZABLE gives the strongest guarantee but requires mandatory retry-on-serialization-failure in every code path touching the protected data. → [Trade-offs](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#trade-offs).

## 6. Interview questions

1. Two transactions read a balance and both write. Walk it at READ COMMITTED, REPEATABLE READ, SERIALIZABLE.
2. Explain write skew with a concrete example. *(the discriminating question)*
3. Estimate QPS and storage for a system with 10M DAU. Show every assumption. *(see `03-system-design-method.md` §3 for the estimation method — this question is cross-listed here as a Week 3 checkpoint drill, not a T-611 concept.)*

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for Q1–Q2: → [Interview Questions](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#interview-questions).

## 7. Common mistakes

Conflating write skew with a lost update (different anomaly classes — cross-row vs. same-row); assuming READ COMMITTED always risks lost updates (PostgreSQL's atomic `UPDATE` prevents this for the common case); choosing SERIALIZABLE everywhere without the required retry logic. → [Common Mistakes](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#common-mistakes).

## 8. Staff-level discussion

Isolation-level choice is a cross-cutting architectural decision, not a per-query tuning knob — SERIALIZABLE's guarantee is only real if every code path touching the invariant both uses it and retries on failure. → [Staff-Level Discussion](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#interview-answer-framework).

## 9. Summary

Write skew — two transactions each reading a shared multi-row state and writing to different rows in a way that jointly violates an invariant — is real, reproducible, and specifically not caught by REPEATABLE READ even though REPEATABLE READ does prevent same-row lost updates. → [Summary](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#practice-exercises) and [Solutions](../../syllabus/06-databases/isolation-levels-and-concurrency-anomalies.md#solutions). Reproducible lab: `practice/sql/week-03/write-skew-setup.sql` and `write-skew-tx.sh`.

## 14. Additional Reading

- Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 7 "Transactions," pp. 233–251

## 15. Official References

- [PostgreSQL documentation, Ch. 13 "Concurrency Control"](https://www.postgresql.org/docs/current/mvcc.html) — §13.2 isolation levels, §13.3 explicit locking
