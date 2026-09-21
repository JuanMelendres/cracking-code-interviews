---
title: "Cheat Sheet: Horizontal Pod Autoscaling"
slug: horizontal-pod-autoscaling-mechanics-and-scaling-behavior
document_type: cheat-sheet
domain: 14-devops-containers
topic_id: T-2424
canonical: ../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md
last_updated: 2026-09-21
---

# Horizontal Pod Autoscaling: Mechanics, Metrics, and Scaling Behavior

**Canonical chapter:** [`syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md`](../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md)

## Core Mental Model

Two separate rules on top of one simple formula: a symmetric tolerance band (noise filter) and a scale-up-immediate/scale-down-stabilized asymmetry (directional bias toward staying larger a little longer).

## Essential Definitions

- **HPA formula** — `desiredReplicas = ceil[currentReplicas * (currentMetricValue / desiredMetricValue)]`, recomputed fresh against the CURRENT replica count every sample.
- **Tolerance band** — default ±10% around target; within it, no change is recommended, symmetrically in both directions.
- **Stabilization window** — default 300s; applies ONLY to scale-down; the actual applied value is the highest recommendation across the whole window, not the latest sample.

## Decision Table

| Situation | What happens |
|---|---|
| Metric within ±10% of target | No change (tolerance band) |
| Metric above target, outside band | Scale UP immediately, no delay |
| Metric below target, outside band | Scale-down recommendation HELD until it's the highest recommendation across the stabilization window |

## Common Pitfalls

- Assuming scale-up and scale-down behave symmetrically — they don't; only scale-down is stabilized.
- Tightening the tolerance band "for responsiveness" without measuring the workload's real metric noise first — reintroduces thrashing.
- Treating a "slow to scale down" report as a bug before checking whether it's the stabilization window working as designed.

## Interview Answer Skeleton

**30-sec:** HPA computes `ceil[currentReplicas * (currentMetric/targetMetric)]`, ignores noise within a 10% tolerance band, and scales up immediately while holding scale-down until the window's highest recent recommendation has itself dropped — a deliberate bias toward staying larger a little longer.

**2-min:** Add: real demo proof — near-target noise (68%/72%) produces zero change; a real spike scales up in the same sample; a real drop to 20% load is recommended for scale-down at t=1227ms but doesn't actually apply until t=3052ms, a measured ~1.8s delay.

**Staff-level framing:** Deviating from default tolerance/stabilization requires first measuring the workload's real metric noise floor and demand-lull duration — the same discipline `cloud-cost-and-scaling-economics.md` applies to reservation sizing.

## Related

- syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md
- syllabus/15-cloud/cloud-cost-and-scaling-economics.md
- syllabus/13-observability/metric-cardinality-and-alert-fatigue.md
