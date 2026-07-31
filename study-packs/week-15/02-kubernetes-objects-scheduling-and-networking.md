---
title: "T-1002 · Kubernetes Objects, Scheduling, and Networking"
topic_id: T-1002
domain: Cloud
tier: Advanced
iwi: 6.50
prerequisites: [T-1003]
unlocks: []
week: 15
last_reviewed: 2026-07-31
canonical: ../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md
---

# T-1002 · Kubernetes Objects, Scheduling, and Networking

**IWI 6.50 · Advanced tier**

**Canonical chapter:** [Kubernetes Objects, Scheduling, and Networking](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md). This file is the Week 15 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** the manifest behind this summary is real, syntax-validated Kubernetes YAML at `practice/k8s/week-15/deployment-with-probes-and-limits.yaml`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [The Deployment/ReplicaSet/Pod layering, validated](#3-the-deploymentreplicasetpod-layering-validated)
4. [Requests vs limits: scheduling vs runtime enforcement](#4-requests-vs-limits-scheduling-vs-runtime-enforcement)
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

A Deployment manages ReplicaSets, which manage Pods; a Service routes to whichever Pods currently match its label selector. → [Definition and Purpose](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#definition-and-purpose).

## 2. Why it exists

Pods are disposable and their IPs are ephemeral — this layering keeps the right count of the right version running and reachable without manual intervention. → [Definition and Purpose](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#definition-and-purpose).

## 3. The Deployment/ReplicaSet/Pod layering, validated

Validated: a real Deployment/Service/HorizontalPodAutoscaler manifest set parses correctly (3 YAML documents). `maxSurge: 1, maxUnavailable: 0` means a rollout can run up to 4 Pods but never fewer than 3, for a 3-replica Deployment. → [Internal Implementation](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#internal-implementation) has the full trace.

## 4. Requests vs limits: scheduling vs runtime enforcement

The scheduler places Pods based on `requests`; `limits` is enforced separately, later, at runtime (CPU throttling, or an OOMKill for memory). → [Core Concepts](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#core-concepts).

## 5. Trade-offs

`maxUnavailable: 0` guarantees Pod count during a rollout but not necessarily steady-state performance — a newly-ready Pod can still be measurably slower during JIT warmup. → [Trade-offs](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#trade-offs).

## 6. Interview questions

1. What actually happens, step by step, when you update a Deployment's container image?
2. Why does the scheduler care about requests but not limits when placing a Pod?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#interview-questions).

## 7. Common mistakes

Assuming maxUnavailable: 0 guarantees performance, not just Pod count; confusing what the scheduler reasons about (requests) with what's enforced at runtime (limits). → [Common Mistakes](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#common-mistakes).

## 8. Staff-level discussion

Teams frequently assume a broader guarantee than what a platform actually documents, discovering the gap only once it manifests as a real, measured symptom. → [Staff-Level Discussion](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#interview-answer-framework).

## 9. Summary

The Deployment/ReplicaSet/Pod layering enables rolling updates; a Service decouples callers from ephemeral Pod IPs. maxUnavailable: 0 guarantees count, not performance — a real gap worth checking for latency-sensitive services. → [Summary](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#practice-exercises) and [Solutions](../../handbook/cloud/kubernetes-objects-scheduling-and-networking.md#solutions). Manifest: `practice/k8s/week-15/deployment-with-probes-and-limits.yaml`.

## 14. Additional Reading

- [Kubernetes documentation — Deployments](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)

## 15. Official References

- [Kubernetes documentation — Service](https://kubernetes.io/docs/concepts/services-networking/service/)
