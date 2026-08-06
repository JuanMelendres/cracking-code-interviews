---
title: "Flashcards: Kubernetes Resource Limits, Probes, and JVM Sizing"
slug: kubernetes-resource-limits-probes-and-jvm-sizing
document_type: flashcard-deck
domain: cloud
topic_id: T-1003
canonical: ../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md
last_updated: 2026-08-06
---

# Flashcards: Kubernetes Resource Limits, Probes, and JVM Sizing

**Canonical chapter:** [`handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md`](../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md)

## Card: What container-aware ergonomics read

**Prompt:**
What does JDK 10+ container-aware heap sizing actually read?

**Answer:**
The container's cgroup memory limit, not the host machine's total memory — confirmed directly via `Runtime.getRuntime().maxMemory()` at different `--memory` limits.

**Why it matters:**
Prevents a JVM from sizing its heap against memory it will never actually be allowed to use.

**Common trap:**
Assuming a flat 25% heap ratio regardless of container size.

**Related:**
[Internal Implementation](../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md#internal-implementation)

## Card: OutOfMemoryError vs OOMKilled

**Prompt:**
What's the structural difference between `OutOfMemoryError` and an OOMKill?

**Answer:**
`OutOfMemoryError` is a JVM-level, catchable exception when the heap itself is exhausted (exit 1). An OOMKill is the Linux kernel's SIGKILL when total process memory exceeds the container's cgroup limit, with zero Java-level signal (exit 137, `OOMKilled=true`).

**Why it matters:**
Determines where to look when debugging — application logs vs. container/pod-level termination reason.

**Common trap:**
Assuming an unexplained restart is an application bug without checking the container-level reason first.

**Related:**
[Internal Implementation](../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md#internal-implementation)

## Card: Readiness vs liveness

**Prompt:**
What's the difference between a readiness probe and a liveness probe?

**Answer:**
A failing readiness probe removes the pod from load-balancing without restarting it (temporary condition). A failing liveness probe restarts the container (permanently stuck condition).

**Why it matters:**
Using one probe type for both purposes loses the ability to distinguish "temporarily overloaded" from "permanently broken."

**Common trap:**
Using the same endpoint/probe for both readiness and liveness.

**Related:**
[Definition and Purpose](../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md#definition-and-purpose)
