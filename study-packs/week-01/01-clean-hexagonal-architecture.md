---
title: "T-901 · Clean / Hexagonal Architecture"
topic_id: T-901
domain: Architecture
tier: Advanced
iwi: 7.25
prerequisites: []
unlocks: [T-903, T-912]
week: 1
last_reviewed: 2026-07-30
canonical: ../../handbook/architecture/clean-hexagonal-architecture.md
---

# T-901 · Clean / Hexagonal Architecture

**IWI 7.25 · Advanced tier · Prerequisite for:** T-903 (aggregates), T-912 (technology replacement boundaries), most of Chapter 07

**Canonical chapter:** [Clean and Hexagonal Architecture](../../handbook/architecture/clean-hexagonal-architecture.md). This file is the Week 1 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `03-technical-answer-framework.md`, `06-domain-purity-exercise.md`, `09-week-1-mock-interview.md`, `11-week-1-checklist.md`, and `README.md` all cite §1, §3, §4, and §7 directly.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [How it works internally](#3-how-it-works-internally)
4. [Trade-offs](#4-trade-offs)
5. [Performance, memory, and concurrency implications](#5-performance-memory-and-concurrency-implications)
6. [Production example](#6-production-example-template--fill-from-your-own-system)
7. [Interview questions](#7-interview-questions)
8. [Common mistakes and anti-patterns](#8-common-mistakes-and-anti-patterns)
9. [Staff-level discussion](#9-staff-level-discussion)
10. [Decision criteria](#10-decision-criteria-cheat-sheet)
11. [Summary](#11-summary)
12. [Key Takeaways](#12-key-takeaways)
13. [Cheat Sheet](#13-cheat-sheet)
14. [Flashcards](#14-flashcards)
15. [Practice Exercises](#15-practice-exercises)
16. [Additional Reading](#16-additional-reading)
17. [Official References](#17-official-references)

---

## 1. The concept

Hexagonal architecture organizes a system around one rule: the domain has no compile-time dependency on anything outside it. The domain defines ports (interfaces stating what it needs or offers); everything outside is an adapter implementing or calling a port. Clean Architecture and Onion Architecture are the same shape under different names. → [Definition and Purpose](../../handbook/architecture/clean-hexagonal-architecture.md#definition-and-purpose).

## 2. Why it exists

Layered architecture looks similar but typically inverts the real dependency — the service layer usually imports framework types directly. Cockburn's original motivation was testability: a domain with no infrastructure dependency can be unit-tested with plain object construction. → [Definition and Purpose](../../handbook/architecture/clean-hexagonal-architecture.md#definition-and-purpose) and [Historical Context](../../handbook/architecture/clean-hexagonal-architecture.md#historical-context).

## 3. How it works internally

Primary (driving) ports are called into the domain; secondary (driven) ports are called out from it. Three defensible answers for where JPA entities live, in increasing order of purity — separate mapped models, annotated domain objects, or (invalid) domain methods returning ORM types directly. Transactions live at the application-service level. → [Core Concepts](../../handbook/architecture/clean-hexagonal-architecture.md#core-concepts).

## 4. Trade-offs

Domain testability and infrastructure-swap containment cost extra interfaces and mapping code. When NOT to use it: a thin CRUD service with no real business rules to protect — applying the pattern there is indirection with no payoff. → [Trade-offs](../../handbook/architecture/clean-hexagonal-architecture.md#trade-offs).

## 5. Performance, memory, and concurrency implications

The pattern itself adds no measurable runtime cost; the real cost is at the mapping boundary, especially on a hot read path mapping a full aggregate graph for a summary view. The domain also loses any implicit thread-confinement a framework would otherwise provide. → [Performance, Memory, and Concurrency Implications](../../handbook/architecture/clean-hexagonal-architecture.md#performance-memory-and-concurrency-implications).

## 6. Production example (template — fill from your own system)

Fill this from real experience before your first mock — a fabricated number collapses on the first follow-up. The canonical chapter's own representative scenario (a two-week ORM migration estimate that took three months because the domain wasn't actually clean) is explicitly labeled fictionalized, not a substitute for your own. → [Production Scenarios](../../handbook/architecture/clean-hexagonal-architecture.md#production-scenarios).

## 7. Interview questions

1. What problem does hexagonal architecture solve that layered architecture does not?
2. What exactly is a port, and what is an adapter? Give one of each.
3. Where does the repository interface live, and why not next to its implementation?
4. Your domain model must not depend on JPA. What does that cost you, concretely?
5. Would you use this on every project? *(the Staff-differentiating question)*
6. You are replacing PostgreSQL with DynamoDB. Which files change, and which must not?
7. Isn't this a lot of mapping code?
8. How do you handle transactions across the port boundary?
9. What about queries that don't fit the repository abstraction — a complex report, a dashboard aggregate?
10. How would you introduce this into an existing, tangled codebase without a rewrite?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/architecture/clean-hexagonal-architecture.md#interview-questions).

## 8. Common mistakes and anti-patterns

Believing hexagonal architecture is a folder layout with no enforced dependency direction; anemic use cases that just forward to the repository; leaking a framework exception through a port; one port per method instead of per capability. → [Common Mistakes](../../handbook/architecture/clean-hexagonal-architecture.md#common-mistakes) and [Anti-Patterns](../../handbook/architecture/clean-hexagonal-architecture.md#anti-patterns).

## 9. Staff-level discussion

Hexagonal boundaries are team boundaries as much as code boundaries — a well-drawn port is a contract two teams can develop against in parallel. Retrofitting the pattern into a legacy system should be scoped per bounded context, not attempted all at once. → [Staff-Level Discussion](../../handbook/architecture/clean-hexagonal-architecture.md#interview-answer-framework).

## 10. Decision criteria (cheat sheet)

→ [Decision Framework](../../handbook/architecture/clean-hexagonal-architecture.md#decision-framework).

## 11. Summary

Hexagonal architecture inverts the dependency between domain and infrastructure via ports the domain defines and adapters that implement them. The payoff is testability and swappability; the cost is mapping code and indirection — a deliberate trade-off, not a universal default. → [Summary](../../handbook/architecture/clean-hexagonal-architecture.md#summary).

## 12. Key Takeaways

→ [Key Takeaways](../../handbook/architecture/clean-hexagonal-architecture.md#key-takeaways).

## 13. Cheat Sheet

→ [Cheat Sheet](../../handbook/architecture/clean-hexagonal-architecture.md#cheat-sheet).

## 14. Flashcards

→ [Flashcards](../../handbook/architecture/clean-hexagonal-architecture.md#flashcards). Full week-level deck, including T-609 cards: `08-flashcards.md`.

## 15. Practice Exercises

→ [Practice Exercises](../../handbook/architecture/clean-hexagonal-architecture.md#practice-exercises). Solutions are not provided by design — Exercise 1 in particular only has value worked against your own system. Use `06-domain-purity-exercise.md` as the guided version of exercises 1–3.

## 16. Additional Reading

- Jeffrey Palermo, "The Onion Architecture" (blog series, 2008) — same shape, different name; useful for recognizing the pattern under any label an interviewer uses.
- Vaughn Vernon, *Implementing Domain-Driven Design* — Ch. 4, "Architecture," for how hexagonal composes with DDD's bounded contexts (previewed for Week 2's T-903).

## 17. Official References

- Alistair Cockburn, ["Hexagonal Architecture"](https://alistair.cockburn.us/hexagonal-architecture/) (original article, 2005)
- Robert C. Martin, *Clean Architecture*, Ch. 22 "The Clean Architecture", Ch. 23 "Presenters and Humble Objects"
