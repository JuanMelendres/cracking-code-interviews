---
title: "Cheat Sheet: Metric Cardinality and Alert Fatigue"
slug: metric-cardinality-and-alert-fatigue
document_type: cheat-sheet
domain: 13-observability
topic_id: T-2409
canonical: ../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md
last_updated: 2026-09-11
---

# Metric Cardinality and Alert Fatigue

**Canonical chapter:** [`syllabus/13-observability/metric-cardinality-and-alert-fatigue.md`](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md)

## Core Mental Model

Both problems are the same underlying idea: signal quality degrades when a monitoring system tracks the wrong shape — unbounded label values for cardinality, single-point-in-time crossings for alerting. The fix in both cases is imposing real structure (bounded labels; sustained, multi-window burn-rate evaluation), not adding more capacity or sensitivity.

## Essential Definitions

- **Cardinality explosion** — an unbounded label value (a raw user ID, a raw URL) multiplying a metric's distinct time-series count.
- **Multi-window, multi-burn-rate alerting** — Google's published technique requiring an error-rate breach sustained across two independent time windows before paging.

## Decision Table

| Need | Concept/Technique |
|---|---|
| Understand why a metric's cost exploded | Check per-metric distinct time-series count before/after a suspected change |
| Fix a cardinality explosion | Bound the offending label (a template, class, category — not a raw ID) |
| Preserve per-value drill-down after removing a high-cardinality label | Use logs or traces instead of metrics |
| Reduce alert noise without losing detection speed | Multi-window, multi-burn-rate alerting |
| Google's real page-severity burn-rate parameters (99.9% SLO) | Burn rate ≥ 14.4, sustained across a 5-minute AND a 1-hour window |
| Verify an alert rule's real noise level | Backtest against real/synthetic historical data, counting discrete firings |

## Common Pitfalls

- Adding a raw, unbounded label (user ID, full URL) to a metric "for more detail," causing a real cardinality explosion.
- Fixing alert noise by loosening the threshold rather than requiring sustained, multi-window confirmation — trades false positives for slower real detection.
- Throwing more metrics-backend memory/hardware at a cardinality problem instead of bounding the offending label.

## Interview Answer Skeleton

**30-sec:** One unbounded label can multiply a metric's time-series count by thousands; single-window alert thresholds cause real alert fatigue. Both are fixed by imposing structure — bounded labels, multi-window burn-rate evaluation — not more capacity or sensitivity.

**2-min:** Add: real measured evidence shows one unbounded label producing 6,667x more distinct time series and ~43x more heap for identical request volume; a real 7-day synthetic simulation shows Google's multi-window, multi-burn-rate technique firing 14x less often than a naive single-threshold rule while still catching both real, injected incidents.

**Staff-level framing:** Alert fatigue is an organizational cost (on-call burnout, ignored pages) as much as a technical one — the fix must preserve detection speed for genuine incidents while cutting the noise that erodes trust in the alerting system itself.

## Related

- syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md
- syllabus/13-observability/performance-methodology-and-slo-error-budgets.md
