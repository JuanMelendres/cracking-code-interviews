---
title: "T-1201 / T-1206 · Performance Methodology (USE/RED) & SLI/SLO/Error Budgets"
topic_id: T-1206
domain: Performance
tier: Staff
iwi: 6.90
prerequisites: [T-1204]
unlocks: []
week: 11
last_reviewed: 2026-07-30
canonical: ../../handbook/performance/performance-methodology-and-slo-error-budgets.md
---

# T-1201 / T-1206 · Performance Methodology (USE/RED) & SLI/SLO/Error Budgets

**IWI 6.90 (T-1205's neighbor) / 6.80 (T-1206) · Staff tier**

**Canonical chapter:** [Performance Methodology (USE/RED) and SLI/SLO/Error Budgets](../../handbook/performance/performance-methodology-and-slo-error-budgets.md). This file is the Week 11 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the 30-day simulation behind this summary is real, computed output from `practice/java/week-11/error-budget/src/ErrorBudgetDemo.java`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [USE and RED, applied to artifacts already produced this program](#3-use-and-red-applied-to-artifacts-already-produced-this-program)
4. [A real error budget, burned by a real incident](#4-a-real-error-budget-burned-by-a-real-incident)
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

USE (Utilization, Saturation, Errors) diagnoses a resource; RED (Rate, Errors, Duration) diagnoses a service. An SLI is a measured metric, an SLO is its target, and the error budget is what's left before the SLO breaches. → [Definition and Purpose](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#definition-and-purpose).

## 2. Why it exists

This vocabulary makes an incident or scaling story credible and quantified — "we were at 60% of budget and one incident consumed 15% in 40 minutes" conveys precision "the system had problems" doesn't. → [Definition and Purpose](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#definition-and-purpose).

## 3. USE and RED, applied to artifacts already produced this program

USE applied to a real GC log: heap occupancy as utilization, a GC pause as saturation made visible, `OutOfMemoryError` as the error signal. RED applied to a real Kafka consumer group: messages/sec as rate, failed processing as errors, produce-to-process time as duration. → [Core Concepts](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#core-concepts).

## 4. A real error budget, burned by a real incident

Measured: a 30-day, 99.9%-SLO simulation where a single 40-minute incident consumed roughly 14% of the entire month's error budget, even though the SLO was still met overall — invisible from the monthly aggregate alone. → [Internal Implementation](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#internal-implementation) has the full trace.

## 5. Trade-offs

A tighter SLO gives a better guaranteed experience at much smaller budget; a monthly-tracked budget smooths noise but can hide a severe single-day incident — track both the aggregate and the daily trend. → [Trade-offs](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#trade-offs).

## 6. Interview questions

1. We're at 35% of our monthly error budget with two weeks left. Do we ship the risky migration this week?
2. Set the timeout — from what data?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#interview-questions).

## 7. Common mistakes

Reporting only the monthly aggregate without checking the daily burn rate; treating USE and RED as interchangeable; treating this vocabulary as new rather than a retrofit onto earlier material. → [Common Mistakes](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#common-mistakes).

## 8. Staff-level discussion

The genuinely Staff-level move isn't computing an error budget — it's using it as an actual ship/hold decision input, not a retrospective health score. → [Staff-Level Discussion](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#interview-answer-framework).

## 9. Summary

USE diagnoses resources, RED diagnoses services, applied directly to artifacts already in hand. A real error-budget simulation shows a single incident consuming a disproportionate share of a month's budget — precisely why the daily trend matters as much as the aggregate. → [Summary](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#practice-exercises) and [Solutions](../../handbook/performance/performance-methodology-and-slo-error-budgets.md#solutions). Reproducible demo: `practice/java/week-11/error-budget/src/ErrorBudgetDemo.java`.

## 14. Additional Reading

- [Google SRE Book — Service Level Objectives](https://sre.google/sre-book/service-level-objectives/)
- [Brendan Gregg — The USE Method](https://www.brendangregg.com/usemethod.html)

## 15. Official References

- [Google SRE Workbook — Implementing SLOs](https://sre.google/workbook/implementing-slos/)
