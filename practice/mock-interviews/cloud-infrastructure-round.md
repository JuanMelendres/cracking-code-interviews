---
title: "Mock Interview: Cloud & Infrastructure Round (45 min)"
slug: cloud-infrastructure-round
document_type: mock-interview
status: draft
version: 1.0
last_updated: 2026-08-11
target_levels:
  - senior
  - staff
duration_minutes: 45
competencies:
  - Kubernetes pod-restart / OOMKill diagnostics
  - Resource requests vs limits
  - Rollout guarantees vs steady-state performance
  - Reserved vs on-demand capacity economics
  - Deployment-strategy process gaps
  - Storage-service selection for mixed access patterns
related:
  - ../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md
  - ../../syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md
  - ../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md
  - ../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md
  - ../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md
source: ../../study-packs/week-15/08-week-15-mock-interview.md
official_references: []
---

# Mock Interview: Cloud & Infrastructure Round

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below. Elevated from `study-packs/week-15/08-week-15-mock-interview.md`.

## Table of Contents

1. [Competencies Assessed](#competencies-assessed)
2. [Interviewer Opening Script](#interviewer-opening-script)
3. [Candidate Section](#candidate-section)
4. [Evaluator Section](#evaluator-section)
5. [Scoring Rubric](#scoring-rubric)
6. [Debrief Guide](#debrief-guide)
7. [Remediation Recommendations](#remediation-recommendations)

---

## Competencies Assessed

| Competency | Question(s) | Canonical Chapter |
|---|---|---|
| Pod-restart / OOMKill diagnostics | Q1 | [Kubernetes Resource Limits, Probes, and JVM Sizing](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md) |
| Requests vs limits | Q2 | [Kubernetes Objects, Scheduling, and Networking](../../syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md) |
| Rollout guarantees vs steady-state performance | Q3 | [Kubernetes Objects, Scheduling, and Networking](../../syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md) |
| Reserved vs on-demand economics | Q4 | [Cloud Cost and Scaling Economics](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md) |
| Deployment-strategy process gaps | Q5 | [CI/CD Pipeline Design and Deployment Strategies](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md) |
| Storage-service selection | Q6 | [AWS Core Services for Backend Engineers](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md) |
| Cross-topic synthesis | Q7 | All five, above |

## Interviewer Opening Script

*"This is a 45-minute Cloud & Infrastructure round. I'll ask seven questions covering Kubernetes diagnostics, cost economics, deployment strategy, and AWS storage selection — most are diagnostic or scenario-based, one is a whiteboard architecture design, and the last is free-form synthesis. I want to hear your actual diagnostic process, not just the final answer — narrate the order you'd check things in. Let's begin."*

## Candidate Section

Answer each question aloud, unprompted, before checking the evaluator section. Record yourself — the goal is fluent, structured delivery, not just a correct answer typed out.

1. **(7 min)** Your pods are restarting with no application logs at all. Walk through your diagnostic process end to end.
2. **(6 min)** Explain the difference between `resources.requests` and `resources.limits`, and what each one actually controls.
3. **(6 min)** Your `maxUnavailable: 0` rollout still caused a brief latency spike. Why might that happen despite the documented guarantee?
4. **(6 min)** Walk through the actual math for whether a workload should be reserved or stay on-demand.
5. **(6 min)** Your canary passed with no alerts, but the release still caused a production issue. What's your process gap?
6. **(6 min, whiteboard)** Design the storage architecture (which AWS storage/database services, and why) for a service that ingests high-volume sensor readings for real-time lookups and monthly ad-hoc analytics reporting.
7. **(8 min)** Free-form: pick two of this week's five topics and explain how they interact in a single real system (e.g., a JVM's OOMKill risk interacting with a Kubernetes rolling update's resource requests).

## Evaluator Section

*(Do not read before completing the candidate section.)*

### Question 1 — Restart loop, no application logs

**Ideal answer outline:** checks the pod/container-level termination reason (`kubectl describe pod`, `OOMKilled`, exit 137) before assuming an application-level crash; explains why an OOMKill produces zero Java-level signal.
**Pass signal:** correctly identifies the container-level check as the first step, with the right mechanism.
**Fail signal:** investigates only application code/logs.

### Question 2 — requests vs limits

**Ideal answer outline:** requests drive scheduling (does a node have room); limits are enforced at runtime (CPU throttling, memory OOMKill).
**Pass signal:** correctly distinguishes scheduling-time from runtime enforcement.
**Fail signal:** conflates the two or claims the scheduler enforces limits.

### Question 3 — maxUnavailable: 0 latency spike

**Ideal answer outline:** the guarantee covers Pod count, not steady-state performance; a newly-ready Pod can be measurably slower during JIT warmup even while passing its readiness probe.
**Pass signal:** correctly separates the documented count guarantee from the assumed performance guarantee.
**Fail signal:** insists the configuration must be misconfigured, without considering the readiness-vs-performance gap.

### Question 4 — Reserve vs on-demand math

**Ideal answer outline:** establishes the workload's demand shape first (steady vs. variable), then computes real arithmetic for the confirmed steady baseline specifically, not peak.
**Pass signal:** produces real numbers and explicitly separates baseline from peak.
**Fail signal:** reasons qualitatively with no actual arithmetic, or reserves based on peak.

### Question 5 — Canary passed but still broke production

**Ideal answer outline:** identifies the missing human-approval gate — "no alert" is not the same evidence level as "a human confirmed this looks healthy."
**Pass signal:** correctly identifies the process-design gap, not just a threshold-tuning fix.
**Fail signal:** proposes only tightening the specific alert that missed it.

### Question 6 — Whiteboard: sensor data storage architecture

**Ideal answer outline:** DynamoDB (or similar) for the high-volume, key-based real-time lookup pattern; a separate store (S3 data lake, or a CDC-fed analytical store) for the ad-hoc monthly reporting pattern — applying the access-pattern method to both current and anticipated needs from the start, per this week's own production scenario.
**Pass signal:** proposes two purpose-built stores rather than forcing one to serve both patterns.
**Fail signal:** proposes a single store for both patterns without addressing the access-pattern mismatch.

### Question 7 — Free-form cross-topic synthesis

**Pass signal:** picks a genuine interaction (e.g., a rolling update's surge Pods needing enough scheduler-visible `requests` headroom cluster-wide, or a JVM's `-Xmx` needing to respect the same memory limit a Kubernetes resource limit sets) and reasons through it precisely.
**Fail signal:** describes two topics separately with no real connective insight.

## Scoring Rubric

Same 1–5 scale and pass threshold as the [Java Core Technical Round](java-core-technical-round.md):

| Score | Meaning |
|---|---|
| 1 | No coherent answer, or a factually wrong one |
| 2 | Names the right topic but no working mechanism |
| 3 | Correct mechanism, Senior-level bar met |
| 4 | Correct mechanism plus one Staff-level extension |
| 5 | Correct mechanism, Staff-level extension, and a real/plausible production connection |

**Pass threshold for this mock:** average score ≥ 3.5 across all seven questions, with no individual score below 2.

## Debrief Guide

Walk the candidate through their own scores question by question, starting with the lowest. Question 1 and Question 3 both probe the gap between what a Kubernetes guarantee documents and what candidates commonly assume it also implies (pod count ≠ zero performance impact; a restart loop ≠ necessarily an application bug) — a candidate weak on both may have a general pattern of taking Kubernetes documentation's literal scope at face value rather than checking it. Question 5 is a process-maturity signal distinct from the others — probe whether the candidate has actually operated a release process with a human gate, or is reciting the concept.

## Remediation Recommendations

- Any score ≤ 2 on Q1 → re-read [Kubernetes Resource Limits, Probes, and JVM Sizing](../../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md), specifically the OutOfMemoryError-vs-OOMKilled distinction.
- Any score ≤ 2 on Q2 or Q3 → re-read [Kubernetes Objects, Scheduling, and Networking](../../syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md) in full.
- Any score ≤ 2 on Q4 → re-read [Cloud Cost and Scaling Economics](../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md)'s worked reservation-sizing arithmetic and redo it independently.
- Any score ≤ 2 on Q5 → re-read [CI/CD Pipeline Design and Deployment Strategies](../../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md)'s canary-value material.
- Any score ≤ 2 on Q6 → re-read [AWS Core Services for Backend Engineers](../../syllabus/15-cloud/aws-core-services-for-backend-engineers.md)'s storage-access-model comparisons.
- Below the 3.5 pass threshold overall → retake this mock in full after remediation.
