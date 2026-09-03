---
title: "T-1003 · Kubernetes Resource Limits, Probes, and JVM Sizing"
topic_id: T-1003
domain: Cloud
tier: Advanced
iwi: 6.80
prerequisites: []
unlocks: []
week: 15
last_reviewed: 2026-07-31
canonical: ../../handbook/cloud/kubernetes-resource-limits-probes-and-jvm-sizing.md
---

# T-1003 · Kubernetes Resource Limits, Probes, and JVM Sizing

**IWI 6.80 · Advanced tier · The highest-value entry in the Cloud & Infrastructure domain**

**Canonical chapter:** [Kubernetes Resource Limits, Probes, and JVM Sizing](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md). This file is the Week 15 study-pack entry point — a short summary of each section plus a link to the full canonical treatment.

**Verification note:** every trace behind this summary is real, executed output from Docker containers running `eclipse-temurin:21-jre`, source at `practice/java/week-15/container-ergonomics/src/`.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Container-aware heap sizing, measured](#3-container-aware-heap-sizing-measured)
4. [OutOfMemoryError vs OOMKilled, measured](#4-outofmemoryerror-vs-oomkilled-measured)
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

Since JDK 10, the JVM reads a container's cgroup memory limit for ergonomic heap sizing. Kubernetes probes (liveness, readiness, startup) let the platform detect and react to container health differently depending on probe type. → [Definition and Purpose](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#definition-and-purpose).

## 2. Why it exists

Without container awareness, a JVM sizes its heap against host memory, routinely far exceeding what a container is actually permitted to use. → [Definition and Purpose](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#definition-and-purpose).

## 3. Container-aware heap sizing, measured

Measured: heap sizes of ~121/123/247 MiB at container memory limits of 256m/512m/1g respectively — a surprising 47% ratio at 256m versus ~24% at 512m/1g, explained by `MinRAMPercentage`'s 50% floor for small containers, confirmed via `-XX:+PrintFlagsFinal`. → [Internal Implementation](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#internal-implementation) has the full trace.

## 4. OutOfMemoryError vs OOMKilled, measured

Measured: a generous container (512m) with a small explicit `-Xmx` (64m) produces a clean `java.lang.OutOfMemoryError` (exit 1). A small container (100m) with `-Xmx` set to exceed it (256m) produces an OOMKilled process (exit 137, confirmed via `docker inspect`'s `OOMKilled=true`) with zero application-level signal. → [Internal Implementation](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#internal-implementation) has the full trace.

## 5. Trade-offs

Relying on container-aware defaults is safer but doesn't account for non-heap memory; setting requests equal to limits gives predictable scheduling at the cost of no bursting. → [Trade-offs](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#trade-offs).

## 6. Interview questions

1. Your pods are restarting with no application logs at all. What's your first check?
2. Why would a startupProbe matter for a Spring Boot application specifically?

Full expected answers, minimum-acceptable bar, Senior/Staff scoring criteria, and follow-ups for each: → [Interview Questions](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#interview-questions).

## 7. Common mistakes

Assuming container-awareness alone is sufficient without accounting for non-heap memory; debugging a restart loop via application logs before checking the container-level termination reason. → [Common Mistakes](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#common-mistakes).

## 8. Staff-level discussion

A failure's visibility depends entirely on which layer of the stack actually detects it — the JVM cannot log a failure that happens to it from outside its own process boundary. → [Staff-Level Discussion](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#interview-answer-framework).

## 9. Summary

Container-aware heap sizing, measured directly, includes a surprising small-container floor. OutOfMemoryError and OOMKilled are measurably, structurally different failure modes for the same underlying problem. → [Summary](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#summary).

## 10. Key Takeaways

→ [Key Takeaways](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#key-takeaways).

## 11. Cheat Sheet

→ [Cheat Sheet](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#cheat-sheet).

## 12. Flashcards

→ [Flashcards](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#flashcards). Full week-level deck: `07-flashcards.md`.

## 13. Practice Exercises

→ [Practice Exercises](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#practice-exercises) and [Solutions](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md#solutions). Reproducible demos: `practice/java/week-15/container-ergonomics/src/`.

## 14. Additional Reading

- [Java containers and mystery of the disappearing memory](https://developers.redhat.com/articles/2022/04/19/java-17-whats-new-openjdks-container-awareness)

## 15. Official References

- [Kubernetes documentation — Resource Management for Pods and Containers](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/)
- [Kubernetes documentation — Configure Liveness, Readiness, and Startup Probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/)
