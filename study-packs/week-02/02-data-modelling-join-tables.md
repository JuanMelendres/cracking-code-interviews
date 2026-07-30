---
title: "T-605/T-608 · Data Modelling and Explicit Join Tables"
topic_id: T-605/T-608
domain: Database
tier: Advanced
iwi: 5.20
prerequisites: []
unlocks: [T-903]
week: 2
last_reviewed: 2026-07-30
canonical: ../../handbook/databases/data-modelling-and-explicit-join-tables.md
---

# T-605 / T-608 · Data Modelling and Explicit Join Tables

**IWI 5.20 · Advanced tier**

**Canonical chapter:** [Data Modelling and Explicit Join Tables](../../handbook/databases/data-modelling-and-explicit-join-tables.md). This file is the Week 2 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `06-answer-frameworks.md` cites §3 directly.

**Verification note:** the many-to-many demonstration behind this summary, including the data-integrity bug, is real executed PostgreSQL 16 output. Source: `practice/sql/week-02/many-to-many-lab.sql`; full output: `many-to-many-lab-output.txt`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The trap, demonstrated](#3-the-trap-demonstrated)
4. [How it works internally](#4-how-it-works-internally)
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

A many-to-many relationship needs a join table; the naive version (just the two foreign keys) is what an unannotated JPA `@ManyToMany` generates. The moment the relationship has any attribute of its own, it needs to become an explicit join entity with its own key and columns. → [Definition and Purpose](../../handbook/databases/data-modelling-and-explicit-join-tables.md#definition-and-purpose).

## 2. Why it exists

ORMs make the naive version the path of least resistance, correct only as long as the relationship truly carries no data. The moment a fact like "3 units at this price" needs recording, the relationship *is* data. → [Definition and Purpose](../../handbook/databases/data-modelling-and-explicit-join-tables.md#definition-and-purpose).

## 3. The trap, demonstrated

Measured: a naive join table has no room for quantity, and — more importantly — silently reports the wrong historical total once a referenced product's price changes. An explicit `order_lines` entity that locks the price at insert time still reports correctly. The real trigger: any fact needing to be true "as of formation time," not "as of read time." → [Internal Implementation](../../handbook/databases/data-modelling-and-explicit-join-tables.md#internal-implementation) has the full trace.

## 4. How it works internally

`@ManyToMany` generates the naive join table with no entity to attach fields to. The fix is modelling the join table as its own `@Entity` (`OrderLine`) — a modelling decision, not a configuration flag. → [Internal Implementation](../../handbook/databases/data-modelling-and-explicit-join-tables.md#internal-implementation).

## 5. Trade-offs

A naive join table costs zero extra code but can't record any fact about the relationship; an explicit join entity is fully general at the cost of an extra class and join. → [Trade-offs](../../handbook/databases/data-modelling-and-explicit-join-tables.md#trade-offs).

## 6. Interview questions

1. Model many-to-many between `Order` and `Product`. Now the relationship needs `quantity` — what changes, and why was the original `@ManyToMany` a trap?
2. When is an explicit join entity mandatory rather than optional?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/databases/data-modelling-and-explicit-join-tables.md#interview-questions).

## 7. Common mistakes

Treating "does the relationship have an attribute" as the only test; adding timestamp columns to a naive join table without recognizing it's now an entity. → [Common Mistakes](../../handbook/databases/data-modelling-and-explicit-join-tables.md#common-mistakes).

## 8. Staff-level discussion

Any many-to-many relationship where one side's data can change independently of when the relationship was formed needs an explicit join entity — permission grants, pricing agreements, versioned configuration all share this pattern. → [Staff-Level Discussion](../../handbook/databases/data-modelling-and-explicit-join-tables.md#interview-answer-framework).

## 9. Summary

A plain join table can only record *that* two entities are related, not any fact about the relationship. The real, executed demonstration shows this isn't hypothetical: the naive table silently reports a wrong historical total once a referenced price changes. → [Summary](../../handbook/databases/data-modelling-and-explicit-join-tables.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/databases/data-modelling-and-explicit-join-tables.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/databases/data-modelling-and-explicit-join-tables.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/databases/data-modelling-and-explicit-join-tables.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/databases/data-modelling-and-explicit-join-tables.md#practice-exercises) and [Solutions](../../handbook/databases/data-modelling-and-explicit-join-tables.md#solutions). Reproducible demo: `practice/sql/week-02/many-to-many-lab.sql`.

## 14. Additional Reading

- Vaughn Vernon, *Implementing Domain-Driven Design* — read alongside `03-ddd-tactical-aggregates.md`; an `OrderLine` explicit join entity is frequently also the natural aggregate-internal entity in a DDD model of the same domain.

## 15. Official References

- [Jakarta Persistence specification](https://jakarta.ee/specifications/persistence/) — `@ManyToMany` and `@ElementCollection` semantics
