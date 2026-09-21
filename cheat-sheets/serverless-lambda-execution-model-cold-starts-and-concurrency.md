---
title: "Cheat Sheet: Serverless Cold Starts"
slug: serverless-lambda-execution-model-cold-starts-and-concurrency
document_type: cheat-sheet
domain: 15-cloud
topic_id: T-2425
canonical: ../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md
last_updated: 2026-09-21
---

# Serverless Compute: Lambda Execution Model, Cold Starts, and Concurrency Scaling

**Canonical chapter:** [`syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md`](../syllabus/15-cloud/serverless-lambda-execution-model-cold-starts-and-concurrency.md)

## Core Mental Model

INIT (once per execution environment: load code, run initializers, open connections) → INVOKE (once per request, reuses INIT's work). A cold start is INIT's real cost, paid once per fresh environment, not once per request.

## Essential Definitions

- **Execution environment** — the sandboxed runtime instance (a Firecracker microVM on AWS) running your function's code.
- **Cold start** — the real latency added to the first invocation of a fresh execution environment, from completing INIT before INVOKE can run.
- **Provisioned concurrency** — a configured number of execution environments kept permanently warm and idle, eliminating cold starts for that slice of capacity, at continuous cost.

## Decision Table

| Situation | Behavior |
|---|---|
| Steady, sustained traffic | Environments stay warm naturally; cold starts rare |
| Bursty/spiky traffic, N concurrent requests, 0 warm environments | Up to N parallel cold starts, each paying full INIT cost |
| Traffic beyond provisioned concurrency's configured capacity | Overflow still cold-starts |

## Common Pitfalls

- Assuming cold start only affects the first request after a lull — it affects every concurrent request beyond the currently-warm environment count.
- Doing unnecessary/slow work in static/global initializer scope, inflating every cold start.
- Provisioning concurrency sized to average traffic instead of expected peak concurrency.

## Interview Answer Skeleton

**30-sec:** Cold start is real INIT-phase work (load code, run initializers, sometimes open connections) paid once per fresh execution environment; concurrency multiplies it since every concurrent request beyond the warm-environment count triggers its own. Provisioned concurrency mitigates it at a continuous cost.

**2-min:** Add: real demo proof — a cold invocation at ~176ms vs. a warm one at ~0.009ms (4-5 orders of magnitude); a 5-way concurrent cold burst at ~236ms vs. a repeat warm burst at ~0.08ms (~3000x). For a real JVM-based Lambda, JVM startup/class-loading is the dominant real INIT-phase contributor.

**Staff-level framing:** Sizing provisioned concurrency to a known future traffic spike (a launch, a batch window) is a proactive provisioning decision, not something to hope the platform silently absorbs.

## Related

- syllabus/15-cloud/aws-core-services-for-backend-engineers.md
- syllabus/15-cloud/cloud-cost-and-scaling-economics.md
- syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md
