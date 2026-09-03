---
title: "T-617/T-811 · Storage Selection Trade-offs"
topic_id: T-617/T-811
domain: Database
tier: Advanced
iwi: 6.90
prerequisites: []
unlocks: []
week: 2
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/storage-selection-tradeoffs.md
---

# T-617 / T-811 · Storage Selection Trade-offs

**IWI 6.90 · Advanced tier**

**Canonical chapter:** [Storage Selection Trade-offs](../../syllabus/11-system-design/storage-selection-tradeoffs.md). This file is the Week 2 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `study-packs/week-03/08-design-exercise-ride-hailing.md` cites §3 directly.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The access-pattern method](#3-the-access-pattern-method)
4. [Trade-offs](#4-trade-offs)
5. [Interview questions](#5-interview-questions)
6. [Common mistakes](#6-common-mistakes)
7. [Staff-level discussion](#7-staff-level-discussion)
8. [Summary](#8-summary)
9. [Key Takeaways](#9-key-takeaways)
10. [Cheat Sheet](#10-cheat-sheet)
11. [Flashcards](#11-flashcards)
12. [Practice Exercises](#12-practice-exercises)
13. [Additional Reading](#13-additional-reading)
14. [Official References](#14-official-references)

---

## 1. The concept

Storage selection is answered by working backward from the queries the system needs to serve, not from a technology's reputation. The same logical data can correctly live in a relational table, document store, key-value store, or wide-column store depending on access pattern. → [Definition and Purpose](../../syllabus/11-system-design/storage-selection-tradeoffs.md#definition-and-purpose).

## 2. Why it exists

Choosing by reputation produces two recurring failures: over-normalized relational schemas for document-shaped data, or document stores forced into multi-record transactional roles they don't support well. → [Definition and Purpose](../../syllabus/11-system-design/storage-selection-tradeoffs.md#definition-and-purpose).

## 3. The access-pattern method

Answer in order before naming a technology: actual read/write patterns, per-operation consistency requirement, transactional scope, volume and growth shape. Only then does a technology choice become a conclusion rather than a guess. → [Core Concepts](../../syllabus/11-system-design/storage-selection-tradeoffs.md#core-concepts).

## 4. Trade-offs

Relational wins for multi-entity transactions and ad-hoc queries; document for flexible, self-contained data; key-value for high-throughput point lookups; wide-column for massive, predictably-patterned write volume. → [Trade-offs](../../syllabus/11-system-design/storage-selection-tradeoffs.md#trade-offs).

## 5. Interview questions

1. Choose between PostgreSQL and DynamoDB for a given workload. Defend it, then argue the opposite.
2. When would polyglot persistence be worth its operational cost?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/11-system-design/storage-selection-tradeoffs.md#interview-questions).

## 6. Common mistakes

Choosing storage by reputation; treating "NoSQL" as one category; adding a second storage technology without weighing its operational cost. → [Common Mistakes](../../syllabus/11-system-design/storage-selection-tradeoffs.md#common-mistakes).

## 7. Staff-level discussion

A storage decision is a multi-year commitment with a real migration cost — team operational maturity with a technology is a legitimate factor alongside pure technical fit. → [Staff-Level Discussion](../../syllabus/11-system-design/storage-selection-tradeoffs.md#interview-answer-framework).

## 8. Summary

Storage selection should follow the actual access pattern, not reputation. Each category wins under specific, nameable conditions — a defensible answer names the condition that would flip the decision. → [Summary](../../syllabus/11-system-design/storage-selection-tradeoffs.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/11-system-design/storage-selection-tradeoffs.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/11-system-design/storage-selection-tradeoffs.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/11-system-design/storage-selection-tradeoffs.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/11-system-design/storage-selection-tradeoffs.md#practice-exercises) and [Solutions](../../syllabus/11-system-design/storage-selection-tradeoffs.md#solutions).

## 13. Additional Reading

- Martin Kleppmann, *Designing Data-Intensive Applications*, Ch. 2 "Data Models and Query Languages" and Ch. 3 "Storage and Retrieval"

## 14. Official References

- [PostgreSQL documentation](https://www.postgresql.org/docs/current/) — relational baseline for comparison
