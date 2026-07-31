---
title: "Week 15 Mock — Cloud & Infrastructure Round (45 min)"
week: 15
document_type: study-pack-mock
status: draft
last_reviewed: 2026-07-31
---

# Week 15 Mock — Cloud & Infrastructure Round (45 min)

**Target role:** Senior/Staff Backend Engineer · **Duration:** 45 minutes · **Format:** self-recorded or with a partner, candidate/evaluator sections hard-separated below.

## Candidate Section

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

Same 1–5 scale and pass threshold (average ≥ 3.5, no score below 2) as Weeks 13/14's mocks — see `study-packs/week-13/08-week-13-mock-interview.md` for the full rubric description.
