---
title: "Unbounded Metric Label Tripling Metrics Backend Storage Cost"
document_type: production-cookbook-entry
domain: performance
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md
source: syllabus/13-observability/metric-cardinality-and-alert-fatigue.md#production-scenarios
---

# Unbounded Metric Label Tripling Metrics Backend Storage Cost

## Context

A deployment introduces a new label on an existing high-volume metric, added by a well-intentioned engineer to help "debug per-user issues" — e.g., a `user_id` or full-URL label.

## Symptoms

A metrics backend's storage and query-latency costs increase sharply after the deployment, despite request volume staying flat.

## Impact

Real, unplanned infrastructure cost growth, and slower dashboard/alert query performance across the whole metrics system, not just the affected metric.

## Initial Hypotheses

- A real traffic increase — checked, request volume logs show no change.
- A metrics backend misconfiguration — checked, no retention or scrape-interval change occurred.
- A new label on an existing high-volume metric with an effectively unbounded value space — correct, confirmed by inspecting the new metric's own real distinct-series count.

## Evidence

Querying the metrics backend's own cardinality (distinct series count per metric name) shows a sharp, unexplained jump in one specific metric, correlated exactly with the deployment.

## Investigation Timeline

1. Storage and query-latency costs rise sharply post-deploy, with request volume unchanged.
2. Traffic-increase and backend-misconfiguration hypotheses ruled out via logs and config review.
3. Per-metric cardinality queried before/after the deployment, isolating the jump to one specific metric.
4. The new label's value space (e.g., `user_id` or full URL) confirmed as effectively unbounded.

## Root Cause

A new label was added to a high-volume metric with an effectively unbounded value space, multiplying the number of distinct time series the metrics backend must store and index.

## Immediate Mitigation

Revert or reconfigure the offending label to a bounded alternative (a route template instead of a raw ID, a status class instead of a raw status).

## Permanent Fix

Add a real, automated cardinality check (many metrics backends and libraries support a configurable per-metric series-count limit) to catch an unbounded label before it reaches production.

## Alternatives Considered

Scaling the metrics backend's storage and compute to absorb the growth — rejected as treating the symptom, not the cause: cardinality growth from a genuinely unbounded label has no real ceiling, so this "fix" only delays, not resolves, the eventual cost and performance problem.

## Trade-offs

Removing a high-cardinality label loses the specific per-value drill-down it offered (e.g., per-user debugging) — a real, deliberate trade for cost and performance, with per-value drill-down better served by logs or traces (designed for high-cardinality data) rather than metrics.

## Prevention

Establish an explicit, reviewed convention for which fields are safe metric labels, and enforce it via the automated cardinality-limit check.

## Monitoring and Alerts

- A per-metric series-count limit enforced at the metrics-library or backend level, rejecting or warning on a label combination that would push a metric past its configured cardinality budget.
- Deployment-correlated cardinality dashboards, so a jump in any one metric's series count is immediately traceable to the specific deploy that introduced it.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a metrics backend's storage and query-latency costs tripled after a routine deployment, with no traffic increase.
- **Task:** find the specific cause of a cost regression not explained by load.
- **Action:** queried per-metric cardinality before and after the deployment, isolating a single metric whose new label had an effectively unbounded value space.
- **Result:** reverted the label to a bounded alternative and added an automated cardinality-limit check to catch this class of regression before production.

## Staff-Level Discussion

A cardinality explosion is a real, structural, mechanical consequence of one specific label choice — diagnosable directly by comparing per-metric series counts before and after a change. The organizational lesson is that "helpful" debugging labels (a raw user ID, a full URL) are a recurring, well-intentioned source of this exact incident, and the fix that scales is an enforced convention plus an automated limit, not relying on every engineer to reason correctly about cardinality at the point of adding a label.

## Related Handbook Chapters

- [Metric Cardinality and Alert Fatigue](../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md) — the canonical cardinality-measurement mechanics behind this incident.
