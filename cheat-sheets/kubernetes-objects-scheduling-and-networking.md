---
title: "Cheat Sheet: Kubernetes Objects, Scheduling, and Networking"
slug: kubernetes-objects-scheduling-and-networking
document_type: cheat-sheet
domain: cloud
topic_id: T-1002
canonical: ../handbook/cloud/kubernetes-objects-scheduling-and-networking.md
last_updated: 2026-08-04
---

# Kubernetes Objects, Scheduling, and Networking

**Canonical chapter:** [`handbook/cloud/kubernetes-objects-scheduling-and-networking.md`](../handbook/cloud/kubernetes-objects-scheduling-and-networking.md)

## Core Mental Model

Every Kubernetes object in this chapter exists to answer one question: given that Pods are disposable and can die at any time, how does the system keep the right number of the right version running, reachable, at all times, without a human intervening? A Deployment answers "how many, and which version." A ReplicaSet is the Deployment's mechanism for enforcing "how many" at any single point in time. A Service answers "how do other things find and reach them, given their IPs change constantly." Scheduling answers "which node does each Pod actually run on, and why."

## Essential Definitions

- **Pod** — the smallest deployable unit: one or more containers that share network and storage.
- **ReplicaSet** — ensures a specified number of Pod replicas matching a label selector are running at all times, replacing any that die.
- **Deployment** — manages ReplicaSets to support rolling updates: changing a Deployment's Pod template creates a new ReplicaSet and gradually shifts replicas from the old one to the new one.
- **Service** — a stable virtual IP and DNS name that load-balances traffic across whichever Pods currently match its label selector, solving the problem that individual Pod IPs are ephemeral.
- **Scheduler** — decides which node each Pod runs on, based on the Pod's declared resource *requests* (not limits) plus affinity/anti-affinity rules and taints/tolerations.
- **`maxSurge`** — how many extra Pods beyond the desired replica count can exist temporarily during a rollout. **`maxUnavailable`** — how many Pods can be below the desired count temporarily.

## Decision Table

| Choice | Benefit | Cost |
|---|---|---|
| `maxUnavailable: 0`, `maxSurge: 1` | Never fewer than the desired replica count during a rollout | Requires extra resource headroom (the surge Pod) during every deployment |
| `maxUnavailable: 1`, `maxSurge: 0` | No extra resource headroom needed during rollout | Capacity genuinely drops below desired count temporarily |
| `requests == limits` for a container | Predictable scheduling, no throttling/OOMKill surprises within budget | No bursting above the request when a node has spare capacity |
| A readiness endpoint checking only process health | Simple to implement | Doesn't capture a genuine startup-vs-steady-state performance gap |

| Question | Answer |
|---|---|
| What decides which node a Pod runs on? | The scheduler, based on `resources.requests` |
| What decides whether a Pod receives traffic right now? | Its Service's label selector, combined with readiness-probe status |
| What decides whether a Pod gets restarted? | Its liveness probe |
| What guarantees zero Pod-count reduction during a rollout? | `maxUnavailable: 0` |

## Key Numbers (real manifest, syntax-validated — `deployment-with-probes-and-limits.yaml`)

```
replicas: 3, maxSurge: 1, maxUnavailable: 0
requests: memory 512Mi, cpu 500m   |   limits: memory 512Mi, cpu 1000m
HPA: minReplicas 3, maxReplicas 12, target CPU averageUtilization 70

Rollout capacity math (3 desired, maxSurge:1, maxUnavailable:0):
  up to 4 Pods running, never fewer than 3
Alternative (maxUnavailable:1, maxSurge:0):
  as few as 2 Pods temporarily, never more than 3
```
Note: validated via YAML parsing only, not applied against a live cluster.

## Common Pitfalls

- Assuming `maxUnavailable: 0` guarantees full-capacity *performance* throughout a rollout, not just full Pod *count*
- Confusing what the scheduler reasons about (`requests`) with what's enforced at runtime (`limits`)
- Designing a readiness probe that only checks process health, missing a real startup-vs-steady-state performance gap

## Interview Answer Skeleton

**30-sec:** Deployment manages ReplicaSets, which enforce Pod count; Service routes by label selector and readiness status. `maxSurge`/`maxUnavailable` bound rollout Pod count, but that's a count guarantee, not a performance guarantee — a newly-Ready Pod can still be measurably slower than a warmed-up one.

**2-min:** Add why it exists (Pods are disposable, need automated reconciliation) + the requests-vs-limits distinction + the maxSurge/maxUnavailable count-vs-performance gap production example.

**Whiteboard:** Draw the flowchart: Deployment → ReplicaSet → Pods, Service routing by label selector to Ready Pods only. Annotate: "the Service doesn't know or care which ReplicaSet a Pod belongs to — only whether it matches the label and is Ready."

**Staff-level framing:** the gap between "the platform's documented guarantee" and "what the team actually needs" is the recurring theme — treat every platform guarantee ("zero downtime," "no capacity loss") as requiring an explicit check of exactly what's measured and guaranteed, rather than assuming the colloquial meaning matches the platform's precise technical definition.

## Production Warning Signs

- **Real incident pattern:** p99 latency briefly spikes during every deployment despite `maxSurge:1, maxUnavailable:0`. Ruled out: `maxUnavailable` not honored (false, `kubectl get pods` confirms exactly 3 Ready), new version genuinely slower (false, steady-state matches old version). Root cause: the readiness probe checks only a `200 OK` health endpoint, which passes right after Spring context init but before JIT warmup of the actual request-handling paths — the new Pod is counted "available" before it performs at steady-state.
- Fix: add a JVM warmup step to the readiness probe (synthetic warmup workload or minimum uptime threshold) — makes rollouts slower, accepted trade-off. Increasing `maxSurge` further is a partial mitigation only, doesn't fix the root cause.

## Related

- [Kubernetes Resource Limits, Probes, and JVM Sizing](kubernetes-resource-limits-probes-and-jvm-sizing.md)
- [Integration Testing Against Real Dependencies](integration-testing-against-real-dependencies.md)
