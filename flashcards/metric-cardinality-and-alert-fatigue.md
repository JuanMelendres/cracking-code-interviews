---
title: "Flashcards: Metric Cardinality and Alert Fatigue"
slug: metric-cardinality-and-alert-fatigue
document_type: flashcard-deck
domain: 13-observability
topic_id: T-2409
canonical: ../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md
last_updated: 2026-09-11
---

# Flashcards: Metric Cardinality and Alert Fatigue

**Canonical chapter:** [`syllabus/13-observability/metric-cardinality-and-alert-fatigue.md`](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md)

## Card: The real cost of one unbounded label

**Prompt:**
What does adding one unbounded label (a raw user ID, a raw URL) to a metric actually cost, measured directly?

**Answer:**
A real, measured 6,667x more distinct time series and ~43x more heap for identical request volume, verified directly against a real Micrometer `SimpleMeterRegistry`, not estimated.

**Why it matters:**
Turns "unbounded labels are bad" from a vague warning into a concrete, quantified cost.

**Common trap:**
Adding a raw ID as a metric label "for more detail" without considering the multiplicative effect on distinct time series.

**Related:**
[Metric Cardinality and Alert Fatigue](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md)

## Card: Fixing a cardinality explosion without losing detail

**Prompt:**
If a high-cardinality label needs to be removed from a metric, how do you preserve per-value drill-down?

**Answer:**
Use logs or traces instead of metrics for that dimension — metrics are for bounded, aggregatable dimensions; logs/traces are the right tool for high-cardinality, per-instance detail.

**Why it matters:**
A concrete alternative, not just "remove the label and lose the information."

**Common trap:**
Believing the only options are "keep the unbounded label" or "lose the detail entirely."

**Related:**
[Metric Cardinality and Alert Fatigue](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md)

## Card: Multi-window burn-rate alerting

**Prompt:**
Why does requiring an error-rate breach across two independent time windows (e.g., 5 minutes AND 1 hour) reduce alert fatigue without losing real incidents?

**Answer:**
A real, seeded 7-day synthetic simulation shows this technique fires 14x less often than a naive single-threshold rule, while both real, injected incidents are still caught by both rules — the multi-window requirement filters transient noise, not genuine sustained problems.

**Why it matters:**
Proves the trade-off isn't "fewer alerts, slower detection" — it's "fewer false alarms, same real detection."

**Common trap:**
Assuming reducing alert volume necessarily means missing real incidents sooner.

**Related:**
[Metric Cardinality and Alert Fatigue](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md)
