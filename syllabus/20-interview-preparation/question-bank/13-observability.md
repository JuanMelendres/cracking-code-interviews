---
title: "Interview Question Bank — 13-observability"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../13-observability/INDEX.md
  - 12-security.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Observability

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 5 chapters yielded 10 deep questions + 14 quick-fire
questions = **24 real questions**. No Junior Fundamentals chapter exists in this
domain — observability presupposes backend fundamentals already covered elsewhere.
This is a genuinely small domain (5 chapters).

---

## Incident Response and Blameless Postmortems

### Q1 — Mitigate or diagnose first? Defend it.

**Canonical treatment:** [§ Interview Questions, Q1](../../13-observability/incident-response-and-blameless-postmortems.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States a preference for mitigating first without a concrete justification.
- **Senior:** States the compounding-impact justification explicitly — user-facing impact compounds with every minute, and a mitigation is usually far faster than a full diagnosis — with a concrete example.
- **Staff:** Names the real exception: when the available mitigation is itself risky or hard to reverse (an irreversible data migration), the calculus shifts toward more diagnosis before acting.

### Q2 — What's wrong with a postmortem that identifies a single root cause?

**Canonical treatment:** [§ Interview Questions, Q2](../../13-observability/incident-response-and-blameless-postmortems.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that incidents can have multiple causes without being specific about the consequence of ignoring that.
- **Senior:** Gives a concrete example of two independent contributing factors to the same incident, each worth its own fix.
- **Staff:** Connects this to blameless culture — naming a single root cause is often, in practice, a proxy for naming a single person or team, since a technical root cause is frequently easier to isolate than the full, honest set of contributing factors.

---

## Logging, Metrics, Tracing, and OpenTelemetry

### Q1 — Trace a request across seven services.

**Canonical treatment:** [§ Interview Questions, Q1](../../13-observability/logging-metrics-tracing-and-opentelemetry.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes tracing generally, even without naming `traceId`/`spanId` precisely — the common mistake this question targets.
- **Senior:** Correctly describes the shared-`traceId`, parent-child-`spanId` mechanism — every span shares the request's `traceId`, each service's span is a child of whichever upstream call invoked it.
- **Staff:** Identifies that a service failing to propagate trace context breaks the chain at exactly that point, and explains why context propagation must be part of every service's instrumentation, not just the ones a team happens to own.

### Q2 — Pauses hit 4 seconds. Diagnose from this log.

**Canonical treatment:** [§ Interview Questions, Q2](../../13-observability/logging-metrics-tracing-and-opentelemetry.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Recognizes the question as an artifact-reading exercise, even without connecting it explicitly to GC logs.
- **Senior:** Proposes checking the trace first to localize WHERE the time went, then the GC log if the slow span points at JVM-level behavior.
- **Staff:** Explicitly sequences the diagnostic funnel — metrics alerted something's wrong, trace localizes which span, logs/GC-log/`EXPLAIN`-plan give the detailed why — as one shared discipline regardless of artifact type.

---

## Metric Cardinality and Alert Fatigue

### Q1 — A metrics backend's cost tripled after a routine deployment with no traffic increase. Walk me through how you'd diagnose it.

**Canonical treatment:** [§ Interview Questions, Q1](../../13-observability/metric-cardinality-and-alert-fatigue.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Identifies cardinality as a plausible cause, even without the specific before/after series-count diagnostic method.
- **Senior:** Correctly names the diagnostic method (comparing per-metric distinct time-series counts before and after) and the structural fix (bounding the offending label).
- **Staff:** Proposes an automated, enforced cardinality-limit check as prevention, framing this as a platform-governance decision, not a one-off fix.

### Q2 — Why does requiring two time windows to agree reduce alert noise without losing real-incident detection?

**Canonical treatment:** [§ Interview Questions, Q2](../../13-observability/metric-cardinality-and-alert-fatigue.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that using two windows reduces noise, even without the precise mechanism of why a blip fails the long-window check.
- **Senior:** Correctly explains why a brief spike satisfies only the short window while a genuine, sustained incident satisfies both.
- **Staff:** Names the real, published parameter choices (burn rate 14.4 for a 1-hour/5-minute pair at page severity, per Google's SRE Workbook) and connects severity tiers to different window/burn-rate combinations.

---

## Percentiles, Tail Latency, and Coordinated Omission

### Q1 — Your load test shows p99 = 200ms, but users report much worse in production. Why the gap?

**Canonical treatment:** [§ Interview Questions, Q1](../../13-observability/percentiles-tail-latency-and-coordinated-omission.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Suspects a methodology issue with the load test, even without naming coordinated omission specifically.
- **Senior:** Names coordinated omission by name and its root cause — a closed-loop load test doesn't account for requests queueing behind a slowdown.
- **Staff:** Proposes a concrete fix (an open-loop load generator, or a coordinated-omission-corrected percentile calculation) and can explain, numerically, roughly how large the understatement typically is.

### Q2 — Justify a percentile-based SLO instead of an average-based one.

**Canonical treatment:** [§ Interview Questions, Q2](../../13-observability/percentiles-tail-latency-and-coordinated-omission.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States that percentiles are more representative of user experience than an average.
- **Senior:** Correctly rejects average-based SLOs — an average can't distinguish "uniformly mediocre" from "mostly great, occasionally terrible."
- **Staff:** Explains why p100/max is also a poor SLO target — it's dominated by rare, often environmental outliers, and chasing it produces diminishing, expensive returns.

---

## Performance Methodology (USE/RED) and SLI/SLO/Error Budgets

### Q1 — We're at 35% of our monthly error budget with two weeks left. Do we ship the risky migration this week?

**Canonical treatment:** [§ Interview Questions, Q1](../../13-observability/performance-methodology-and-slo-error-budgets.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Asks for more information before answering, even without specifying the daily-trend breakdown precisely.
- **Senior:** Asks for the daily burn-rate breakdown before answering — a steadily climbing background rate versus one isolated incident changes the answer.
- **Staff:** Frames the error budget as a genuine resource-allocation decision — it exists specifically to make "can we ship something risky" answerable with data.

### Q2 — Set the timeout — from what data?

**Canonical treatment:** [§ Interview Questions, Q2](../../13-observability/performance-methodology-and-slo-error-budgets.md#interview-questions)

**What's expected:**
- **Junior/Mid:** States the timeout should come from real latency data, without connecting it to a named signal.
- **Senior:** Connects RED's Duration signal to percentile selection — from a high percentile (p99), not a round number.
- **Staff:** Notes that a closed-loop-measured Duration distribution would understate the real tail (per coordinated omission), meaning a timeout set from that data could be miscalibrated — tying resilience, percentiles, and RED into one coherent answer.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | During an active incident, should you mitigate or fully diagnose first? | [Incident Response and Blameless Postmortems](../../13-observability/incident-response-and-blameless-postmortems.md#flashcards) |
| 2 | Why is "Contributing Factors" the correct postmortem frame, not "Root Cause"? | [Incident Response and Blameless Postmortems](../../13-observability/incident-response-and-blameless-postmortems.md#flashcards) |
| 3 | Is a postmortem blameless simply because it doesn't name an individual? | [Incident Response and Blameless Postmortems](../../13-observability/incident-response-and-blameless-postmortems.md#flashcards) |
| 4 | What single piece of data lets a tracing backend reconstruct a whole multi-service request's path? | [Logging, Metrics, Tracing, and OpenTelemetry](../../13-observability/logging-metrics-tracing-and-opentelemetry.md#flashcards) |
| 5 | What does a metric alone fail to tell you that a trace can? | [Logging, Metrics, Tracing, and OpenTelemetry](../../13-observability/logging-metrics-tracing-and-opentelemetry.md#flashcards) |
| 6 | Why is inconsistent trace-context propagation across services a serious problem? | [Logging, Metrics, Tracing, and OpenTelemetry](../../13-observability/logging-metrics-tracing-and-opentelemetry.md#flashcards) |
| 7 | Why does adding one unbounded label to a metric multiply its cost, rather than just adding to it? | [Metric Cardinality and Alert Fatigue](../../13-observability/metric-cardinality-and-alert-fatigue.md#flashcards) |
| 8 | Why does requiring both a short AND a long window to show a high burn rate reduce alert noise better than a single threshold? | [Metric Cardinality and Alert Fatigue](../../13-observability/metric-cardinality-and-alert-fatigue.md#flashcards) |
| 9 | What is coordinated omission? | [Percentiles, Tail Latency, and Coordinated Omission](../../13-observability/percentiles-tail-latency-and-coordinated-omission.md#flashcards) |
| 10 | Why can't average latency characterize user experience? | [Percentiles, Tail Latency, and Coordinated Omission](../../13-observability/percentiles-tail-latency-and-coordinated-omission.md#flashcards) |
| 11 | Why is p99.9 usually a better SLO target than max/p100? | [Percentiles, Tail Latency, and Coordinated Omission](../../13-observability/percentiles-tail-latency-and-coordinated-omission.md#flashcards) |
| 12 | What does USE stand for, and what does it diagnose? | [Performance Methodology (USE/RED) and SLI/SLO/Error Budgets](../../13-observability/performance-methodology-and-slo-error-budgets.md#flashcards) |
| 13 | What does RED stand for, and what does it diagnose? | [Performance Methodology (USE/RED) and SLI/SLO/Error Budgets](../../13-observability/performance-methodology-and-slo-error-budgets.md#flashcards) |
| 14 | Why can a monthly error-budget aggregate be misleading on its own? | [Performance Methodology (USE/RED) and SLI/SLO/Error Budgets](../../13-observability/performance-methodology-and-slo-error-budgets.md#flashcards) |

---

## Related

- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
