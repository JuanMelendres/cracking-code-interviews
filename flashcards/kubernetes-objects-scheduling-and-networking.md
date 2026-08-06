---
title: "Flashcards: Kubernetes Objects, Scheduling, and Networking"
slug: kubernetes-objects-scheduling-and-networking
document_type: flashcard-deck
domain: cloud
topic_id: T-1002
canonical: ../handbook/cloud/kubernetes-objects-scheduling-and-networking.md
last_updated: 2026-08-06
---

# Flashcards: Kubernetes Objects, Scheduling, and Networking

**Canonical chapter:** [`handbook/cloud/kubernetes-objects-scheduling-and-networking.md`](../handbook/cloud/kubernetes-objects-scheduling-and-networking.md)

## Card: Why Deployments manage ReplicaSets, not Pods directly

**Prompt:**
Why does a Deployment manage ReplicaSets rather than Pods directly?

**Answer:**
So updating the Pod template can create a new ReplicaSet and gradually shift replicas from old to new — the mechanism that makes rolling updates possible.

**Why it matters:**
Explains the actual object model, not just "Kubernetes does rolling updates."

**Common trap:**
Describing Deployments as directly managing Pods, skipping the ReplicaSet layer.

**Related:**
[Core Concepts](../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#core-concepts)

## Card: requests vs limits

**Prompt:**
What's the functional difference between `resources.requests` and `resources.limits`?

**Answer:**
`requests` is what the scheduler uses to decide if a node has room; `limits` is enforced later, at runtime, by the kubelet/container runtime.

**Why it matters:**
A common source of capacity-planning confusion.

**Common trap:**
Assuming the scheduler enforces limits at placement time.

**Related:**
[Core Concepts](../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#core-concepts)

## Card: What maxUnavailable: 0 actually guarantees

**Prompt:**
Does `maxUnavailable: 0` guarantee performance is unaffected during a rollout?

**Answer:**
No — it guarantees Pod *count* never drops below the desired replica count, but says nothing about whether a newly-ready Pod is actually at steady-state performance yet.

**Why it matters:**
A real, measured gap between documented guarantee and commonly-assumed guarantee.

**Common trap:**
Assuming "zero unavailable" means "zero performance impact."

**Related:**
[Production Scenarios](../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#production-scenarios)
