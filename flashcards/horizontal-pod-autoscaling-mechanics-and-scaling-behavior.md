---
title: "Flashcards: Horizontal Pod Autoscaling"
slug: horizontal-pod-autoscaling-mechanics-and-scaling-behavior
document_type: flashcard-deck
domain: 14-devops-containers
topic_id: T-2424
canonical: ../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md
last_updated: 2026-09-21
---

# Flashcards: Horizontal Pod Autoscaling

**Canonical chapter:** [`syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md`](../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md)

## Card: The real HPA formula

**Prompt:**
What is the actual formula Kubernetes' HorizontalPodAutoscaler uses to compute desired replicas?

**Answer:**
`desiredReplicas = ceil[currentReplicas * (currentMetricValue / desiredMetricValue)]`, recomputed fresh against whatever the replica count actually is right now — not a remembered baseline — every time a metric sample arrives.

**Why it matters:**
The entire mechanism is this one formula plus two governing rules (tolerance band, scale-up/down asymmetry) — no ML, no prediction.

**Common trap:**
Assuming HPA "remembers" how it reached the current replica count rather than recomputing fresh each time.

**Related:**
[Core Concepts](../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md#core-concepts)

## Card: Why scale-up and scale-down aren't symmetric

**Prompt:**
Does HPA apply a scale-down recommendation as quickly as a scale-up recommendation?

**Answer:**
No. Scale-up applies immediately (default scale-up stabilization window is 0s). Scale-down is deliberately delayed: the controller only applies the HIGHEST recommendation seen across the entire stabilization window (default 300s), not the latest sample — because under-provisioning during real load is a user-facing incident, while staying larger a few extra minutes during a lull is comparatively cheap.

**Why it matters:**
The most common source of "why is my HPA slow to scale down" confusion — it's usually working as designed, not a bug.

**Common trap:**
Assuming a "slow scale-down" report indicates a bug before checking whether it matches expected stabilization-window behavior.

**Related:**
[Internal Implementation](../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md#internal-implementation)

## Card: Real measured evidence — the stabilization delay

**Prompt:**
What did a real demo measure for the delay between a scale-down recommendation and it actually taking effect?

**Answer:**
A real Java simulation of the HPA algorithm showed load dropping to 20% (target 70%) with the per-sample formula recommending scale-down at `t=1227ms`, but the controller not actually shrinking the fleet until `t=3052ms` — a real, measured ~1.8-second stabilization delay (window compressed from Kubernetes' real 300s default for demo speed). Re-run twice with identical results.

**Why it matters:**
Turns the abstract stabilization-window concept into concrete, reproducible, measured evidence.

**Common trap:**
Treating the delay as evidence of a slow or broken system rather than the intended, measured behavior.

**Related:**
[Internal Implementation](../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md#internal-implementation)
