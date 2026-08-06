---
title: "Flashcards: Performance Methodology (USE/RED) and SLI/SLO/Error Budgets"
slug: performance-methodology-and-slo-error-budgets
document_type: flashcard-deck
domain: performance
topic_id: T-1206
canonical: ../handbook/performance/performance-methodology-and-slo-error-budgets.md
last_updated: 2026-08-06
---

# Flashcards: Performance Methodology (USE/RED) and SLI/SLO/Error Budgets

**Canonical chapter:** [`handbook/performance/performance-methodology-and-slo-error-budgets.md`](../handbook/performance/performance-methodology-and-slo-error-budgets.md)

## Card: What USE stands for and diagnoses

**Prompt:**
What does USE stand for, and what does it diagnose?

**Answer:**
Utilization, Saturation, Errors — a methodology for diagnosing a RESOURCE (CPU, disk, heap, connection pool).

**Why it matters:**
Matches the right diagnostic lens to a resource rather than a service.

**Common trap:**
Applying USE to a service endpoint instead of a resource.

**Related:**
[Core Concepts](../handbook/performance/performance-methodology-and-slo-error-budgets.md#core-concepts)

## Card: What RED stands for and diagnoses

**Prompt:**
What does RED stand for, and what does it diagnose?

**Answer:**
Rate, Errors, Duration — a methodology for diagnosing a SERVICE (a request-handling endpoint or consumer group).

**Why it matters:**
Matches the right diagnostic lens to a service rather than a resource.

**Common trap:**
Applying RED to a resource like a CPU or disk.

**Related:**
[Core Concepts](../handbook/performance/performance-methodology-and-slo-error-budgets.md#core-concepts)

## Card: Why a monthly aggregate can mislead

**Prompt:**
Why can a monthly error-budget aggregate be misleading on its own?

**Answer:**
It can hide a severe single-day incident inside an otherwise-fine month — a 40-minute incident consuming ~14% of an ENTIRE month's budget was measured directly, invisible from the monthly total alone.

**Why it matters:**
The reason gating decisions on the aggregate alone is risky.

**Common trap:**
Treating a healthy aggregate percentage as sufficient justification for a risky decision.

**Related:**
[Production Scenarios](../handbook/performance/performance-methodology-and-slo-error-budgets.md#production-scenarios)
