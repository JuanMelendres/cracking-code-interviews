---
title: "Flashcards: Logging, Metrics, Tracing, and OpenTelemetry"
slug: logging-metrics-tracing-and-opentelemetry
document_type: flashcard-deck
domain: performance
topic_id: T-1205
canonical: ../handbook/performance/logging-metrics-tracing-and-opentelemetry.md
last_updated: 2026-08-06
---

# Flashcards: Logging, Metrics, Tracing, and OpenTelemetry

**Canonical chapter:** [`syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md`](../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md)

## Card: What reconstructs a trace

**Prompt:**
What single piece of data lets a tracing backend reconstruct a whole multi-service request's path?

**Answer:**
A shared `traceId` across every span in that request, with parent-child `spanId` relationships.

**Why it matters:**
The actual mechanism, not just the vocabulary of "tracing."

**Common trap:**
Describing tracing without naming this specific mechanism.

**Related:**
[Internal Implementation](../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#internal-implementation)

## Card: What a metric alone can't tell you

**Prompt:**
What does a metric alone fail to tell you that a trace can?

**Answer:**
WHICH specific request, and WHERE in its call chain, the problem occurred — metrics only show aggregate trends.

**Why it matters:**
The core reason metrics, traces, and logs are complementary, not redundant.

**Common trap:**
Treating a metrics dashboard as sufficient observability on its own.

**Related:**
[Core Concepts](../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#core-concepts)

## Card: Why inconsistent propagation is serious

**Prompt:**
Why is inconsistent trace-context propagation across services a serious problem?

**Answer:**
It breaks the trace tree exactly at the boundary where propagation is missing, turning one reconstructable trace into disconnected fragments.

**Why it matters:**
The gap appears exactly where visibility matters most, often during an active incident.

**Common trap:**
Assuming propagation is guaranteed by default across every library and service.

**Related:**
[Production Scenarios](../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#production-scenarios)
