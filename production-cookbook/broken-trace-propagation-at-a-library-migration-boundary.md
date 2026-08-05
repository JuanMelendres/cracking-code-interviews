---
title: "Broken Trace Propagation at a Library Migration Boundary"
document_type: production-cookbook-entry
domain: performance
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/performance/logging-metrics-tracing-and-opentelemetry.md
source: handbook/performance/logging-metrics-tracing-and-opentelemetry.md#production-scenarios
---

# Broken Trace Propagation at a Library Migration Boundary

## Context

A `payment-service` calls a downstream `fraud-check` service as part of checkout. The fraud-check service was recently migrated to a new outbound HTTP client library.

## Symptoms

A team investigating a slow-checkout complaint pulls the trace for a representative slow request and finds it ends abruptly at the `payment-service` boundary — no child spans for whatever `payment-service` called next, even though logs from the downstream fraud-check service show activity at the right timestamp.

## Impact

The team cannot determine from tracing alone whether the fraud-check service, a database it calls, or something else entirely caused the slowdown — exactly the localization tracing is supposed to provide is unavailable at the one boundary that matters for this incident.

## Initial Hypotheses

- The tracing backend dropped spans due to sampling — checked and ruled out; sampling configuration shows 100% capture for this trace given its error status.
- A bug in the trace query itself — checked and ruled out; other traces through the same backend reconstruct correctly.
- The fraud-check service was deployed without trace-context propagation configured — correct.

## Evidence

Code review of the fraud-check service's outbound HTTP client shows it was recently migrated to a new HTTP library, and the migration dropped the interceptor that propagated the incoming `traceparent` header to outbound calls — every span the fraud-check service itself creates uses a fresh, unrelated trace context instead of continuing the incoming one.

## Investigation Timeline

1. **Trace gap noticed** while investigating a slow-checkout complaint, with the trace ending at `payment-service` and no child spans downstream.
2. **Sampling and query-bug hypotheses ruled out**, since sampling is at 100% for this trace and other traces reconstruct correctly through the same backend.
3. **Downstream logs checked directly**, confirming the fraud-check service was in fact active at the right timestamp — the work happened, it just wasn't linked.
4. **Code review of the recent HTTP client migration** in the fraud-check service finds the dropped `traceparent`-propagation interceptor.

## Root Cause

Inconsistent trace-context propagation breaks the trace tree exactly at the boundary where it's missing, turning what should be one reconstructable trace into disconnected fragments — here, at precisely the service the team most needed visibility into.

## Immediate Mitigation

Correlate the fraud-check service's own logs by timestamp manually, as a stopgap, to complete the incident's root-cause picture without a proper trace link.

## Permanent Fix

Restore trace-context propagation in the fraud-check service's HTTP client configuration, and add an automated check — a synthetic trace verified end-to-end — to the deployment pipeline that fails if any service in the call path breaks context propagation.

## Alternatives Considered

Relying on manual timestamp correlation as an ongoing practice. Rejected — it doesn't scale to services with any meaningful request volume, and is exactly the "no single service's own logs contain the full picture" problem tracing exists to solve.

## Trade-offs

Adding an automated end-to-end trace-propagation check adds a deployment-pipeline step and a small maintenance burden. Accepted, since the alternative is a silent, easy-to-reintroduce regression at any future library migration.

## Prevention

Treat trace-context propagation as a non-negotiable requirement verified by an automated check for every service in a distributed system, re-verified on any HTTP client or messaging library migration, not assumed to persist across such changes.

## Monitoring and Alerts

- A synthetic, end-to-end trace check in the deployment pipeline (the Permanent Fix above), run on every deploy rather than only discovered during a real incident's investigation.
- A standing metric for "trace tree depth reached" or "percentage of expected downstream spans present" per service, distinguishing a service that silently stops propagating context from one that's simply not called in a given request.

## Interview Story

This maps to a "your tracing has a gap at exactly the service you need visibility into" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a slow-checkout trace ended abruptly at one service boundary, with no visibility into what happened downstream.
- **Task:** determine whether the gap was a tracing infrastructure problem or something specific to one service.
- **Action:** rule out sampling and query-tooling issues by checking configuration and comparing against other working traces; confirm downstream activity existed via logs; trace the gap to a specific library migration that dropped a propagation interceptor.
- **Result:** restored propagation and added an automated end-to-end trace check to the deploy pipeline, preventing the same regression on any future library migration.

## Staff-Level Discussion

Observability infrastructure has a specific failure mode that's easy to underweight: it can degrade silently and non-uniformly, so the team only discovers the gap exactly when they need it most — during an incident, at the one boundary tracing exists to illuminate. A library migration is a mundane, routine change, which is precisely why it's dangerous here: nobody thinks of "swap the HTTP client" as an observability change, so it doesn't get observability-specific review. The Staff-level fix is making trace-context propagation a first-class, automatically verified contract for every service — not a property that's assumed to persist across unrelated dependency changes, which is the assumption that failed here.

## Related Handbook Chapters

- [Logging, Metrics, Tracing, and OpenTelemetry](../handbook/performance/logging-metrics-tracing-and-opentelemetry.md) — canonical trace-context propagation mechanics used here.
