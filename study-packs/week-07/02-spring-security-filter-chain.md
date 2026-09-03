---
title: "T-511 · Spring Security Filter Chain"
topic_id: T-511
domain: Security
tier: Advanced
iwi: 7.20
prerequisites: [T-501]
unlocks: [T-512]
week: 7
last_reviewed: 2026-07-30
canonical: ../../handbook/spring/security-filter-chain.md
---

# T-511 · Spring Security Filter Chain

**IWI 7.20 · Advanced tier**

**Canonical chapter:** [Spring Security Filter Chain](../../syllabus/05-spring/security-filter-chain.md). This file is the Week 7 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `06-security-chain-trace-deliverable.md` references this file's scenarios and cheapest-first principle directly.

**Verification note:** the trace behind this summary is real, executed output from a plain-Java reproduction of Spring Security's `Filter`/`FilterChain` mechanism. Source: `practice/java/week-07/security/SecurityFilterChainDemo.java`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A request traced through the chain](#3-a-request-traced-through-the-chain)
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

Spring Security intercepts every request through a chain of servlet filters — each filter can inspect the request, do work, and either call the next filter or short-circuit the chain entirely. → [Definition and Purpose](../../syllabus/05-spring/security-filter-chain.md#definition-and-purpose).

## 2. Why it exists

Without an ordered, composable chain, every endpoint would need to hand-roll its own authentication and authorization checks, with no guarantee of consistency across the application. → [Definition and Purpose](../../syllabus/05-spring/security-filter-chain.md#definition-and-purpose).

## 3. A request traced through the chain

Measured: a valid-token request reaches the controller; a request with no `Authorization` header short-circuits at authentication (401); a valid token with the wrong role short-circuits at authorization (403) — two separate, sequential gates. → [Internal Implementation](../../syllabus/05-spring/security-filter-chain.md#internal-implementation) has all three real traces.

## 4. Trade-offs

Filter short-circuiting rejects cheaply but puts every filter on the critical path; stateless JWT auth needs no session lookup but can't revoke a token; method-level security is finer-grained but runs later. → [Trade-offs](../../syllabus/05-spring/security-filter-chain.md#trade-offs).

## 5. Interview questions

1. Trace an authenticated request through your security filter chain.
2. Why does CORS/CSRF filtering happen before authentication in the chain?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/05-spring/security-filter-chain.md#interview-questions).

## 6. Common mistakes

Conflating authentication with authorization as one step; assuming authenticated implies authorized; placing expensive checks earlier than cheap ones. → [Common Mistakes](../../syllabus/05-spring/security-filter-chain.md#common-mistakes).

## 7. Staff-level discussion

The filter chain is a concrete instance of a general pattern — ordering cross-cutting concerns from cheapest/most-decisive to most expensive/most-specific — that recurs throughout distributed systems design. → [Staff-Level Discussion](../../syllabus/05-spring/security-filter-chain.md#interview-answer-framework).

## 8. Summary

A security filter chain is an ordered chain-of-responsibility; authentication and authorization are two distinct, sequential gates, demonstrated with real short-circuit traces. → [Summary](../../syllabus/05-spring/security-filter-chain.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/05-spring/security-filter-chain.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/05-spring/security-filter-chain.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/05-spring/security-filter-chain.md#flashcards). Full week-level deck: `05-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/05-spring/security-filter-chain.md#practice-exercises). Reproducible demo: `practice/java/week-07/security/src/SecurityFilterChainDemo.java`.

## 13. Additional Reading

- [Spring Security documentation — Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html)

## 14. Official References

- [Jakarta Servlet specification — Filter](https://jakarta.ee/specifications/servlet/) — the underlying `Filter`/`FilterChain` interfaces this pattern is built on
