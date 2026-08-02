---
title: "T-1106 · Performance and Load Testing Methodology"
topic_id: T-1106
domain: Testing
tier: Advanced
iwi: 5.90
prerequisites: [T-1101]
unlocks: []
week: 18
last_reviewed: 2026-08-02
canonical: ../../handbook/testing/performance-and-load-testing-methodology.md
---

# T-1106 · Performance and Load Testing Methodology

**IWI 5.90 · Advanced tier · Moderate interview frequency**

**Canonical chapter:** [Performance and Load Testing Methodology](../../handbook/testing/performance-and-load-testing-methodology.md). This file is the Week 18 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. This chapter deliberately owns the testing-*practice* half of performance testing; the percentile mathematics and coordinated-omission measurement pitfall are owned by `handbook/performance/percentiles-tail-latency-and-coordinated-omission.md` (T-1204) — see that chapter for the deeper measurement theory.

**Verification note:** the load-test evidence behind this summary is real, executed output from `practice/java/week-18/load-testing/`.

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

Load, stress, and soak testing answer three separate questions and belong at different points in a release process — load testing as a cheap, frequent gate; stress and soak testing as deliberate, more expensive exercises. → [Mental Model](../../handbook/testing/performance-and-load-testing-methodology.md#mental-model).

## 2. Why it exists

A load test's traffic *shape*, not just its volume, determines whether it catches real production-representative issues; performance testing needs an explicit owner and trigger or it silently lapses, unlike a functional test suite that fails loudly. → [Core Concepts](../../handbook/testing/performance-and-load-testing-methodology.md#core-concepts).

## 3. The measured evidence

Real load test: 2,000 requests, 20-way concurrency, a deliberately injected 1-in-20 slow path. Mean (12.45ms) and p50 (4.17ms) both look healthy; p95 (150.54ms) reveals the real 5%-of-traffic slow path, matching the injected delay almost exactly. → [Internal Implementation](../../handbook/testing/performance-and-load-testing-methodology.md#internal-implementation) has the full trace.

## 4. Trade-offs

Mandatory, automated load-test gating adds real per-release cost but catches regressions before production; manual, ownerless performance testing is cheaper per-instance but has a real, demonstrated tendency to lapse silently. → [Trade-offs](../../handbook/testing/performance-and-load-testing-methodology.md#trade-offs).

## 5. Interview questions

1. Your team's load-testing script hasn't run in six months. What's the underlying process problem?
2. A load test passes in staging but the same traffic volume causes real problems in production. What would you check?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/testing/performance-and-load-testing-methodology.md#interview-questions).

## 6. Common mistakes

Treating load, stress, and soak testing as interchangeable; designing traffic volume carefully while neglecting traffic shape; leaving performance testing without an explicit owner or trigger. → [Common Mistakes](../../handbook/testing/performance-and-load-testing-methodology.md#common-mistakes).

## 7. Staff-level discussion

Treats performance testing as requiring the same process discipline (ownership, trigger, pass/fail criteria) as any other release gate, given its demonstrated tendency to silently lapse without that structure. → [Staff-Level Discussion](../../handbook/testing/performance-and-load-testing-methodology.md#interview-answer-framework).

## 8. Summary

Load/stress/soak testing are distinct practices at distinct points in a release process. Measured directly: a load test's mean and p50 both looked healthy while p95 revealed a real, injected 5%-of-traffic slow path. → [Summary](../../handbook/testing/performance-and-load-testing-methodology.md#summary).

## 9. Key Takeaways

→ [Key Takeaways](../../handbook/testing/performance-and-load-testing-methodology.md#key-takeaways).

## 10. Cheat Sheet

→ [Cheat Sheet](../../handbook/testing/performance-and-load-testing-methodology.md#cheat-sheet).

## 11. Flashcards

→ [Flashcards](../../handbook/testing/performance-and-load-testing-methodology.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 12. Practice Exercises

→ [Practice Exercises](../../handbook/testing/performance-and-load-testing-methodology.md#practice-exercises) and [Solutions](../../handbook/testing/performance-and-load-testing-methodology.md#solutions). Reproducible demo: `practice/java/week-18/load-testing/`.

## 13. Additional Reading

- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110)

## 14. Official References

- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110)
