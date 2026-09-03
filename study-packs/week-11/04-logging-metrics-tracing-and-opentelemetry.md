---
title: "T-1205 · Logging, Metrics, Tracing & OpenTelemetry"
topic_id: T-1205
domain: Performance
tier: Staff
iwi: 6.90
prerequisites: []
unlocks: []
week: 11
last_reviewed: 2026-07-30
canonical: ../../handbook/performance/logging-metrics-tracing-and-opentelemetry.md
---

# T-1205 · Logging, Metrics, Tracing & OpenTelemetry

**IWI 6.90 · Staff tier**

**Canonical chapter:** [Logging, Metrics, Tracing, and OpenTelemetry](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md). This file is the Week 11 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the trace behind this summary is real, executed output from `practice/java/week-11/tracing/src/TracingDemo.java`, using the real OpenTelemetry Java SDK.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A real distributed trace, executed](#3-a-real-distributed-trace-executed)
4. [Logs, metrics, and traces are complementary, not redundant](#4-logs-metrics-and-traces-are-complementary-not-redundant)
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

A span represents one unit of work; a trace is a tree of spans sharing one `traceId`, reconstructing the full path a request took across services. OpenTelemetry is the vendor-neutral standard for producing this data. → [Definition and Purpose](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#definition-and-purpose).

## 2. Why it exists

A slow p99 is real but doesn't say WHERE in a multi-service request the time went. Tracing answers exactly that: which specific downstream call, in which service, caused it. → [Definition and Purpose](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#definition-and-purpose).

## 3. A real distributed trace, executed

Measured: a 4-span trace, all sharing one `traceId`, revealing a failure originated at a specific database insert inside a payment service — without tracing, only "the order failed" would be visible. → [Internal Implementation](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#internal-implementation) has the full trace.

## 4. Logs, metrics, and traces are complementary, not redundant

Metrics detect that something's wrong in aggregate; traces localize where in a call chain; logs explain why in full detail. No single signal substitutes for the others. → [Core Concepts](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#core-concepts).

## 5. Trade-offs

Metrics are cheap but can't localize; logs give full detail but are hard to correlate without a shared trace ID; traces localize precisely but are expensive at full sampling. → [Trade-offs](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#trade-offs).

## 6. Interview questions

1. Trace a request across seven services.
2. Pauses hit 4 seconds. Diagnose from this log.

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#interview-questions).

## 7. Common mistakes

Relying on only one signal type; instrumenting some services with trace propagation and not others; treating tracing, GC-log-reading, and query-plan-reading as unrelated skills. → [Common Mistakes](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#common-mistakes).

## 8. Staff-level discussion

Trace-context propagation must be a non-negotiable requirement for every service — retrofitting it into a system with untraced gaps is far more expensive than building it in from the start. → [Staff-Level Discussion](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#interview-answer-framework).

## 9. Summary

A real trace's shared `traceId` is the entire mechanism that lets a backend reconstruct which downstream call caused a failure. Metrics, logs, and traces are complementary — no one substitutes for the others. → [Summary](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#practice-exercises) and [Solutions](../../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md#solutions). Reproducible demo: `practice/java/week-11/tracing/src/TracingDemo.java`.

## 14. Additional Reading

- [OpenTelemetry documentation — Traces](https://opentelemetry.io/docs/concepts/signals/traces/)

## 15. Official References

- [OpenTelemetry Java SDK](https://github.com/open-telemetry/opentelemetry-java) — the library used directly in this chapter's real demo
