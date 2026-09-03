---
title: "T-809 · Idempotency"
topic_id: T-809
domain: System Design
tier: Advanced
iwi: 8.09
prerequisites: [T-909]
unlocks: []
week: 5
last_reviewed: 2026-07-30
canonical: ../../handbook/system-design/idempotency.md
---

# T-809 · Idempotency

**IWI 8.09 · Advanced tier · The structural fix to Week 4's retry-ambiguity problem**

**Canonical chapter:** [Idempotency at System Edges](../../syllabus/11-system-design/idempotency.md). This file is the Week 5 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `09-design-exercise-payment-processing.md` cites §3 directly.

**Verification note:** the full mechanism behind this summary is real, executed Java against real PostgreSQL 16 — genuine concurrent threads, a real unique-constraint race, and real TTL-based recovery. Source: `practice/java/week-05/idempotency/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The full mechanism, reproduced](#3-the-full-mechanism-reproduced)
4. [What the client does when it never receives the response](#4-what-the-client-does-when-it-never-receives-the-response)
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

An operation is idempotent if performing it multiple times produces the same result and the same side effect as performing it once. An idempotency key lets the server recognize a retried request and return the original result instead of re-executing it. → [Definition and Purpose](../../syllabus/11-system-design/idempotency.md#definition-and-purpose).

## 2. Why it exists

Directly answers Week 4's unresolved question: a network cannot distinguish "lost," "still processing," and "succeeded but the response was lost." Idempotency keys make the ambiguity safe to retry through by moving resolution to the server. → [Definition and Purpose](../../syllabus/11-system-design/idempotency.md#definition-and-purpose).

## 3. The full mechanism, reproduced

Measured: two concurrent duplicate requests produce exactly 1 charge, both returning the same result — coordinated by the database's own unique constraint, not application-level locking. A TTL on `IN_PROGRESS` rows lets a fresh attempt reclaim a key from a crashed prior attempt rather than blocking forever. → [Internal Implementation](../../syllabus/11-system-design/idempotency.md#internal-implementation) has the full measured traces and schema.

## 4. What the client does when it never receives the response

Retry, unconditionally, with the same idempotency key — safe specifically because the server-side mechanism resolves the ambiguity, not because the client resolved it. → [Core Concepts](../../syllabus/11-system-design/idempotency.md#core-concepts).

## 5. Trade-offs

No idempotency mechanism forces a choice between risking a duplicate or risking never completing; a short TTL bounds storage but risks premature reuse, a long TTL is safer but grows storage. → [Trade-offs](../../syllabus/11-system-design/idempotency.md#trade-offs).

## 6. Interview questions

1. Make a payment endpoint idempotent. Full mechanism — key, storage, TTL, concurrent-duplicate behaviour.
2. What does the client do when it never receives the response?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/11-system-design/idempotency.md#interview-questions).

## 7. Common mistakes

Implementing idempotency as a client-side check instead of server-side storage-backed; using application locking instead of a database unique constraint; no TTL at all. → [Common Mistakes](../../syllabus/11-system-design/idempotency.md#common-mistakes).

## 8. Staff-level discussion

Idempotency keys are one instance of a broader pattern: moving ambiguity resolution to the party with the most information — the server, which has ground truth, not the client. → [Staff-Level Discussion](../../syllabus/11-system-design/idempotency.md#interview-answer-framework).

## 9. Summary

An idempotency key backed by a unique-constraint storage mechanism converts "I don't know if my request succeeded" into a safe-to-retry-regardless guarantee. → [Summary](../../syllabus/11-system-design/idempotency.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/11-system-design/idempotency.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/11-system-design/idempotency.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/11-system-design/idempotency.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/11-system-design/idempotency.md#practice-exercises) and [Solutions](../../syllabus/11-system-design/idempotency.md#solutions). Reproducible demo: `practice/java/week-05/idempotency/`.

## 14. Additional Reading

- [Stripe API documentation — Idempotent requests](https://stripe.com/docs/api/idempotent_requests) — a widely-cited real-world implementation this chapter's mechanism follows the shape of

## 15. Official References

- No single RFC governs idempotency-key design; Stripe's documentation (above) functions as a de facto industry reference.
