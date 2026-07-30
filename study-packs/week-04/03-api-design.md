---
title: "T-803 · API Design"
topic_id: T-803
domain: System Design
tier: Advanced
iwi: 7.10
prerequisites: [T-804]
unlocks: []
week: 4
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/api-design.md
---

# T-803 · API Design

**IWI 7.10 · Advanced tier**

**Canonical chapter:** [API Design](../../handbook/system-design/api-design.md). This file is the Week 4 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `08-design-exercise-news-feed.md` and `09-week-4-checklist.md` cite §3 directly.

**Verification note:** the pagination comparison behind this summary is real, executed PostgreSQL 16 output against a 2-million-row table. Source: `practice/sql/week-04/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Pagination, measured — why not OFFSET](#3-pagination-measured--why-not-offset)
4. [Resource naming and standard methods](#4-resource-naming-and-standard-methods)
5. [Error design](#5-error-design)
6. [Trade-offs](#6-trade-offs)
7. [Interview questions](#7-interview-questions)
8. [Common mistakes](#8-common-mistakes)
9. [Staff-level discussion](#9-staff-level-discussion)
10. [Summary](#10-summary)
11. [Key Takeaways](#11-key-takeaways)
12. [Cheat Sheet](#12-cheat-sheet)
13. [Flashcards](#13-flashcards)
14. [Practice Exercises](#14-practice-exercises)
15. [Additional Reading](#15-additional-reading)
16. [Official References](#16-official-references)

---

## 1. The concept

API design is choosing a stable, predictable contract at a system's boundary — resource shapes, standard methods, error formats, pagination — that client code can be written against without knowing the implementation. → [Definition and Purpose](../../handbook/system-design/api-design.md#definition-and-purpose).

## 2. Why it exists

Without deliberate design, an interface leaks implementation details directly — endpoint shapes mirroring database tables, inconsistent error responses across independently-implemented endpoints. → [Definition and Purpose](../../handbook/system-design/api-design.md#definition-and-purpose).

## 3. Pagination, measured — why not OFFSET

Measured: `OFFSET` at depth 1,000,000 took 86.006ms (walking and discarding a million rows) vs. keyset pagination's 0.020ms at equivalent depth — a ~3,000x difference on identical data. Keyset can't jump to an arbitrary page number, only move from a known cursor. → [Internal Implementation](../../handbook/system-design/api-design.md#internal-implementation) has the full `EXPLAIN ANALYZE` output.

## 4. Resource naming and standard methods

Plural nouns for collections, no verbs in paths, nesting reflecting genuine ownership. `GET`/`PUT`/`DELETE` are idempotent by definition; `POST` only with a client-supplied idempotency key. → [Core Concepts](../../handbook/system-design/api-design.md#core-concepts) has the full standard-methods table.

## 5. Error design

A consistent error envelope — status code, machine-readable code, message, field (if applicable) — lets client code handle errors programmatically. A `409 Conflict` on a duplicate idempotent `POST` is definitively "already handled," versus an ambiguous `500`. → [Core Concepts](../../handbook/system-design/api-design.md#core-concepts).

## 6. Trade-offs

Offset pagination is simple and supports page-jump but degrades linearly with depth; keyset is flat-cost but loses page-jump; `PUT` is simple and idempotent but requires the full resource; `PATCH` is efficient but needs explicit idempotency semantics. → [Trade-offs](../../handbook/system-design/api-design.md#trade-offs).

## 7. Interview questions

1. Design pagination for a 500M-row endpoint. Why not `OFFSET`?
2. What makes an API idempotent, and why does it matter for retries?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/system-design/api-design.md#interview-questions).

## 8. Common mistakes

Choosing `OFFSET` by default without checking query depth; inconsistent error shapes across endpoints; verbs in resource paths. → [Common Mistakes](../../handbook/system-design/api-design.md#common-mistakes).

## 9. Staff-level discussion

API design decisions are among the most expensive to change once clients depend on them — worth getting the pagination decision right from the start rather than "optimizing later." → [Staff-Level Discussion](../../handbook/system-design/api-design.md#interview-answer-framework).

## 10. Summary

API design choices are contracts that become expensive to change once clients depend on them. `OFFSET` pagination has a real, measured, linear-with-depth cost; keyset avoids it at the cost of losing arbitrary page-jump. → [Summary](../../handbook/system-design/api-design.md#summary).

## 11. Key Takeaways

→ [Key Takeaways](../../handbook/system-design/api-design.md#key-takeaways).

## 12. Cheat Sheet

→ [Cheat Sheet](../../handbook/system-design/api-design.md#cheat-sheet).

## 13. Flashcards

→ [Flashcards](../../handbook/system-design/api-design.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 14. Practice Exercises

→ [Practice Exercises](../../handbook/system-design/api-design.md#practice-exercises) and [Solutions](../../handbook/system-design/api-design.md#solutions). Reproducible lab: `practice/sql/week-04/pagination-lab.sql`.

## 15. Additional Reading

- [Google API Design Guide](https://cloud.google.com/apis/design) — resource naming, standard methods, error design

## 16. Official References

- [RFC 9457 — Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457) — a standardized error-response format
