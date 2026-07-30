---
title: "T-903 · DDD Tactical Design — Aggregates"
topic_id: T-903
domain: Architecture
tier: Advanced
iwi: 7.25
prerequisites: [T-901]
unlocks: []
week: 2
last_reviewed: 2026-07-30
canonical: ../../handbook/architecture/ddd-tactical-design-aggregates.md
---

# T-903 · DDD Tactical Design — Aggregates

**IWI 7.25 (paired with T-901) · Advanced tier · Prerequisite:** T-901 (Week 1) — an aggregate's persistence-agnostic modelling depends on the domain already being free of infrastructure dependencies.

**Canonical chapter:** [DDD Tactical Design — Aggregates](../../handbook/architecture/ddd-tactical-design-aggregates.md). This file is the Week 2 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [How it works internally](#3-how-it-works-internally)
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

An aggregate is a cluster of domain objects treated as a single consistency unit — one object (the aggregate root) is the only entry point outside code may reference; everything inside is only reachable through the root. → [Definition and Purpose](../../handbook/architecture/ddd-tactical-design-aggregates.md#definition-and-purpose).

## 2. Why it exists

Without an explicit boundary, invariants (an order's total must equal its lines) have no single enforcement point. The boundary is deliberately also the transaction boundary — cross-aggregate consistency is eventual, not shared-transactional. → [Definition and Purpose](../../handbook/architecture/ddd-tactical-design-aggregates.md#definition-and-purpose).

## 3. How it works internally

Sizing rule: as small as the true invariant requires, not as large as "everything related." `Order`+`OrderLine` are one aggregate; `Customer` is separate, referenced by ID. Repository-per-aggregate: only the root gets one. → [Core Concepts](../../handbook/architecture/ddd-tactical-design-aggregates.md#core-concepts).

## 4. Trade-offs

Enforcing invariants in one place costs a shared-transaction limitation across aggregates; correct sizing avoids both broken invariants (too small) and lock contention (too large). → [Trade-offs](../../handbook/architecture/ddd-tactical-design-aggregates.md#trade-offs).

## 5. Interview questions

1. What is an aggregate boundary, and why is it a *transaction* boundary?
2. What is the aggregate sizing rule, precisely?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/architecture/ddd-tactical-design-aggregates.md#interview-questions).

## 6. Common mistakes

Modelling aggregates around object composition rather than the actual invariant; giving a non-root entity its own repository; assuming aggregates map one-to-one onto tables. → [Common Mistakes](../../handbook/architecture/ddd-tactical-design-aggregates.md#common-mistakes).

## 7. Staff-level discussion

Aggregate boundaries drawn well today are frequently the service boundaries of tomorrow — both answer "what has to be consistent together, and what can be eventually consistent." → [Staff-Level Discussion](../../handbook/architecture/ddd-tactical-design-aggregates.md#interview-answer-framework).

## 8. Summary

An aggregate is the smallest cluster that must be consistent together, entered only through its root, saved atomically. Sizing it correctly is a modelling skill — getting it wrong shows up as broken invariants (too small) or lock contention (too large). → [Summary](../../handbook/architecture/ddd-tactical-design-aggregates.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../handbook/architecture/ddd-tactical-design-aggregates.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../handbook/architecture/ddd-tactical-design-aggregates.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../handbook/architecture/ddd-tactical-design-aggregates.md#flashcards). Full week-level deck: `08-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../handbook/architecture/ddd-tactical-design-aggregates.md#practice-exercises) and [Solutions](../../handbook/architecture/ddd-tactical-design-aggregates.md#solutions).

## 13. Additional Reading

- This chapter previews Week 5's T-907/T-908 (microservice decomposition) — the aggregate-boundary-as-service-boundary connection is worth re-reading once that week is reached.

## 14. Official References

- Vaughn Vernon, *Domain-Driven Design Distilled*, Ch. 5 "Tactical Design with Aggregates"
- Eric Evans, *Domain-Driven Design*, Ch. 6 "The Life Cycle of a Domain Object" (original aggregate definition)
