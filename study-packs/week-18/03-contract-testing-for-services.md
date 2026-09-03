---
title: "T-1105 · Contract Testing for Services"
topic_id: T-1105
domain: Testing
tier: Staff
iwi: 5.70
prerequisites: [T-1104]
unlocks: []
week: 18
last_reviewed: 2026-08-02
canonical: ../../handbook/testing/contract-testing-for-services.md
---

# T-1105 · Contract Testing for Services

**IWI 5.70 · Staff tier · Occasional interview frequency**

**Canonical chapter:** [Contract Testing for Services](../../syllabus/08-testing/contract-testing-for-services.md). This file is the Week 18 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the pass/fail evidence behind this summary is real, executed output from `practice/java/week-18/contract-testing/` against a real, live HTTP provider.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The measured evidence](#3-the-measured-evidence)
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

Contract testing sits between unit tests (isolated, no real integration confidence) and full end-to-end tests (real confidence, slow, cross-service-flaky) — verifying a provider's real implementation against what a specific consumer actually depends on. → [Mental Model](../../syllabus/08-testing/contract-testing-for-services.md#mental-model).

## 2. Why it exists

Consumer-driven contract ownership produces contracts reflecting real, specific usage rather than a speculative superset of a provider's full API — catching a breaking change at the exact point it's introduced. → [Definition and Purpose](../../syllabus/08-testing/contract-testing-for-services.md#definition-and-purpose).

## 3. The measured evidence

Real verification test against a compliant provider: passes cleanly. The same test against a provider with a real, deliberate breaking change (`amount` renamed to `total`, `status` removed): fails with a precise message naming the exact missing field and why the consumer needs it. → [Internal Implementation](../../syllabus/08-testing/contract-testing-for-services.md#internal-implementation) has the full trace.

## 4. Trade-offs

Contract testing gives real-implementation confidence at low cost and coupling, but requires active, ongoing contract maintenance by consumer teams — a stale contract produces false positives or negatives. → [Trade-offs](../../syllabus/08-testing/contract-testing-for-services.md#trade-offs).

## 5. Interview questions

1. Your org relies on manually notifying downstream teams before any shared-API change. What would you propose, and what would it actually require?
2. A contract-verification test fails. How do you tell a genuine breaking change from a stale contract?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/08-testing/contract-testing-for-services.md#interview-questions).

## 6. Common mistakes

Having providers author contracts unilaterally instead of consumers; treating contract testing as a full replacement for all integration testing; letting a contract go stale. → [Common Mistakes](../../syllabus/08-testing/contract-testing-for-services.md#common-mistakes).

## 7. Staff-level discussion

Positions contract testing precisely within a broader testing strategy — a targeted tool for cross-service compatibility, not a blanket replacement for end-to-end testing. → [Staff-Level Discussion](../../syllabus/08-testing/contract-testing-for-services.md#interview-answer-framework).

## 8. Summary

Consumer-owned contracts, verified against the provider's real implementation, give precise, actionable compatibility signals. Measured directly: a real pass against a compliant provider, a real precisely-worded fail against a real breaking change. → [Summary](../../syllabus/08-testing/contract-testing-for-services.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../syllabus/08-testing/contract-testing-for-services.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../syllabus/08-testing/contract-testing-for-services.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../syllabus/08-testing/contract-testing-for-services.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../syllabus/08-testing/contract-testing-for-services.md#practice-exercises) and [Solutions](../../syllabus/08-testing/contract-testing-for-services.md#solutions). Reproducible demo: `practice/java/week-18/contract-testing/`.

## 13. Additional Reading

- [Pact — Contract Testing documentation](https://docs.pact.io/)

## 14. Official References

- [Pact — Contract Testing documentation](https://docs.pact.io/)
