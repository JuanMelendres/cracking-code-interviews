---
title: "T-1204 · Latency: Percentiles, Tail Latency & Coordinated Omission"
topic_id: T-1204
domain: Performance
tier: Staff
iwi: 6.70
prerequisites: []
unlocks: []
week: 11
last_reviewed: 2026-07-30
canonical: ../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md
---

# T-1204 · Latency: Percentiles, Tail Latency & Coordinated Omission

**IWI 6.70 · Staff tier**

**Canonical chapter:** [Percentiles, Tail Latency, and Coordinated Omission](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md). This file is the Week 11 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the percentile figures behind this summary are real, computed output from `practice/java/week-11/percentiles/src/CoordinatedOmissionDemo.java` — 100,000 simulated requests through each of two measurement methodologies, same underlying service behavior, same random seed.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Coordinated omission, measured](#3-coordinated-omission-measured)
4. [Why average latency is close to useless](#4-why-average-latency-is-close-to-useless)
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

A percentile states the latency below which a given fraction of requests fall. Coordinated omission is a measurement bug: a naive closed-loop load generator sends fewer requests exactly when the service is struggling, systematically understating the true tail. → [Definition and Purpose](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#definition-and-purpose).

## 2. Why it exists

Averages hide exactly the information that matters for user experience and SLOs. Coordinated omission exists as a named concept because the natural way to write a load generator is precisely the wrong way. → [Definition and Purpose](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#definition-and-purpose).

## 3. Coordinated omission, measured

Measured: the identical service (98% fast, 2% stalling 500ms) reports p99=500ms under closed-loop load generation but p99=830ms (and p90 380ms vs. 10ms) under the correct open-loop methodology — purely from correcting how latency was measured. → [Internal Implementation](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#internal-implementation) has the full trace.

## 4. Why average latency is close to useless

A single average is compatible with wildly different real experiences — it can't distinguish "everyone waits near the average" from "98% fast, 2% very slow." Percentiles distinguish these; an average cannot. → [Core Concepts](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#core-concepts).

## 5. Trade-offs

Average latency reveals almost nothing; p50 shows typical experience; p99/p99.9 shows the tail an SLO should target; closed-loop testing understates the tail, open-loop captures it correctly at higher implementation cost. → [Trade-offs](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#trade-offs).

## 6. Interview questions

1. Your load test shows p99 = 200ms, but users report much worse in production. Why the gap?
2. Justify a percentile-based SLO instead of an average-based one.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#interview-questions).

## 7. Common mistakes

Reporting average latency as characterizing user experience; trusting closed-loop load-test percentiles; chasing p100/max as an SLO target. → [Common Mistakes](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#common-mistakes).

## 8. Staff-level discussion

The measurement methodology is itself part of the system under evaluation — getting it subtly wrong produces confidently-wrong conclusions, not obviously-wrong ones. → [Staff-Level Discussion](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#interview-answer-framework).

## 9. Summary

Percentiles reveal what averages hide. Coordinated omission is a real, measured methodology bug — the same service reporting a materially different, more honest picture once measured correctly. → [Summary](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#practice-exercises) and [Solutions](../../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#solutions). Reproducible demo: `practice/java/week-11/percentiles/src/CoordinatedOmissionDemo.java`.

## 14. Additional Reading

- [Gil Tene — "How NOT to Measure Latency" (talk)](https://www.infoq.com/presentations/latency-response-time/) — the original coordinated-omission talk

## 15. Official References

- [HdrHistogram documentation](http://hdrhistogram.org/) — the standard library for coordinated-omission-corrected percentile recording in production load-testing tools
