---
title: "Flashcards: Percentiles, Tail Latency, and Coordinated Omission"
slug: percentiles-tail-latency-and-coordinated-omission
document_type: flashcard-deck
domain: performance
topic_id: T-1204
canonical: ../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md
last_updated: 2026-08-06
---

# Flashcards: Percentiles, Tail Latency, and Coordinated Omission

**Canonical chapter:** [`handbook/performance/percentiles-tail-latency-and-coordinated-omission.md`](../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md)

## Card: What coordinated omission is

**Prompt:**
What is coordinated omission?

**Answer:**
A load-testing measurement bug where a closed-loop generator (waits for each response before sending the next) sends fewer requests exactly when the service is slow, understating the true tail latency.

**Why it matters:**
A measurement bug that produces confidently-wrong, clean-looking numbers.

**Common trap:**
Trusting a "clean" percentile from a naive load generator.

**Related:**
[Internal Implementation](../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#internal-implementation)

## Card: Why average latency fails

**Prompt:**
Why can't average latency characterize user experience?

**Answer:**
It can't distinguish "uniformly mediocre" from "mostly fast, occasionally very slow" — very different experiences can produce the same average.

**Why it matters:**
The core reason SLOs target percentiles, never averages.

**Common trap:**
Reporting an average as evidence of good performance.

**Related:**
[Core Concepts](../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#core-concepts)

## Card: Why p99.9 beats max as an SLO target

**Prompt:**
Why is p99.9 usually a better SLO target than max/p100?

**Answer:**
Max is dominated by rare, often environmental outliers (a single GC pause); p99.9 targets a representative tail experience without chasing unrepresentative extremes.

**Why it matters:**
Prevents wasted engineering effort chasing unrepresentative outliers.

**Common trap:**
Proposing the raw max as the most rigorous possible SLO target.

**Related:**
[Trade-offs](../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#trade-offs)
