---
title: "Rolling Update Latency Spike Despite maxUnavailable: 0"
document_type: production-cookbook-entry
domain: cloud
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md
source: handbook/cloud/kubernetes-objects-scheduling-and-networking.md#production-scenarios
---

# Rolling Update Latency Spike Despite maxUnavailable: 0

## Context

A service is configured with `maxSurge: 1, maxUnavailable: 0`, expecting zero capacity loss during a rollout per the documented semantics. Its readiness probe checks only that the application's health endpoint responds `200 OK`.

## Symptoms

A team deploys an update to the service. Despite the `maxUnavailable: 0` configuration, p99 latency briefly spikes during every deployment.

## Impact

A configuration intended to guarantee full capacity throughout a rollout doesn't fully prevent a real, measurable latency spike during deployment.

## Initial Hypotheses

- `maxUnavailable: 0` isn't actually being honored — checked and ruled out; `kubectl get pods` during a rollout confirms exactly the expected number of old-or-new Pods are always Ready, never fewer.
- The new version itself is slower — checked and ruled out; steady-state latency after the rollout completes matches the old version.
- A new Pod is being counted as available, and receiving traffic, before it's actually warmed up, even though its readiness probe passed — correct.

## Evidence

The new Pod's readiness probe checks only that the application's health endpoint responds `200 OK`, which happens shortly after Spring context initialization completes — but the JVM's JIT compiler hasn't yet warmed up the request-handling code paths, so the first several seconds of real traffic to a newly-ready Pod are measurably slower than steady-state, even though the Pod is legitimately "ready" by the probe's own definition.

## Investigation Timeline

1. **Latency spike noticed on every deployment**, despite a configuration expected to guarantee zero capacity loss.
2. **`maxUnavailable` enforcement and version-regression hypotheses ruled out**, confirming Pod count is always correct and steady-state latency matches between versions.
3. **New Pod behavior examined immediately post-readiness**, distinguishing its performance from an already-warmed-up Pod's.
4. **Mechanism traced to JIT warmup**: the readiness probe measures process health, not request-handling code-path performance.

## Root Cause

`maxUnavailable: 0` guarantees count — the right number of Pods are always marked Ready — but says nothing about whether a newly-Ready Pod is actually performing at steady-state capacity yet. The Service correctly routes traffic to the new Pod the instant its readiness probe passes, which is earlier than the point where its actual serving capacity matches an already-warmed-up Pod's.

## Immediate Mitigation

None available without a configuration change — this is a structural gap in what readiness actually measures, not a transient issue.

## Permanent Fix

Add a JVM warmup step to the readiness probe itself — for example, a readiness endpoint that only returns `200` after a synthetic warmup workload has run, or after a minimum uptime threshold — so "ready" more accurately reflects "actually at steady-state capacity," not just "the process started successfully."

## Alternatives Considered

Increasing `maxSurge` further to over-provision extra capacity during every rollout. A partial mitigation — more Pods means the JIT-warming penalty is diluted across more capacity — but doesn't address the root cause, and costs more resources on every single deployment indefinitely.

## Trade-offs

A warmup-aware readiness check makes rollouts take longer, since each new Pod takes more time to become Ready. Accepted, since the alternative is a real, measurable latency spike on every deployment.

## Prevention

Treat "readiness" as needing to reflect genuine serving capacity, not just process health, for any service where JIT warmup (or any other startup-vs-steady-state performance gap) is a real, measured effect — verified with a load test comparing a freshly-ready Pod's latency against a long-running one before relying on probe-based readiness alone.

## Monitoring and Alerts

- Per-Pod p99 latency tracked from the moment a Pod becomes Ready, distinguishing newly-ready Pods from steady-state ones — this makes the warmup gap directly visible as a metric rather than only inferable from an aggregate service-wide spike during rollouts.
- A load test comparing freshly-ready Pod latency against a long-running Pod's, run once per service to establish whether this gap is real and significant enough to warrant a warmup-aware readiness check, rather than assumed present or absent.

## Interview Story

This maps to a "why did a zero-downtime rollout still cause a latency spike" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a rollout configured for zero capacity loss (`maxUnavailable: 0`) still produced a measurable p99 spike on every deployment.
- **Task:** explain the gap between the documented guarantee and the observed behavior.
- **Action:** confirm Pod count was always correct throughout the rollout; rule out a version regression via steady-state comparison; isolate the specific window right after a Pod becomes Ready as the source, and trace it to JIT warmup lagging behind readiness-probe success.
- **Result:** added a warmup-aware readiness check so "ready" reflects actual serving capacity, not just process health, accepting slower rollouts in exchange for eliminating the spike.

## Staff-Level Discussion

`maxUnavailable: 0` is a precise, well-documented guarantee about Pod *count*, and the gap here is entirely in what teams often assume it also guarantees — full-capacity *performance* — which the Kubernetes documentation never actually promises. This is a recurring category of production surprise: a configuration behaves exactly as specified, and the incident is really a mismatch between the specification and the mental model operators built around it. The fix (a warmup-aware readiness probe) is JVM-specific here, but the general principle generalizes to any runtime with a startup-vs-steady-state performance gap: readiness should be defined as "performing at expected capacity," not merely "process started successfully," whenever that gap is measured to be real.

## Related Handbook Chapters

- [Kubernetes Objects, Scheduling, and Networking](../syllabus/14-devops-containers/kubernetes-objects-scheduling-and-networking.md) — canonical rolling-update and readiness-probe mechanics used here.
- [Kubernetes Resource Limits, Probes, and JVM Sizing](../syllabus/14-devops-containers/kubernetes-resource-limits-probes-and-jvm-sizing.md) — the JVM-in-container sizing context this warmup behavior is part of.
