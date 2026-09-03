---
title: "Cheat Sheet: Logging, Metrics, Tracing, and OpenTelemetry"
slug: logging-metrics-tracing-and-opentelemetry
document_type: cheat-sheet
domain: performance
topic_id: T-1205
canonical: ../handbook/performance/logging-metrics-tracing-and-opentelemetry.md
last_updated: 2026-08-04
---

# Logging, Metrics, Tracing, and OpenTelemetry

**Canonical chapter:** [`syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md`](../syllabus/13-observability/logging-metrics-tracing-and-opentelemetry.md)

## Core Mental Model

Metrics tell you something is wrong; a trace tells you where; logs tell you why. No one signal substitutes for the others — a dashboard alert without a trace can't localize a problem to a specific span, a trace without logs can't give full detail, and neither metrics nor logs alone can cheaply answer "what path did this one specific slow request actually take."

## Essential Definitions

- **Span** — one unit of work (an HTTP request, a database call) with a start time, duration, and status.
- **Trace** — a tree of spans sharing one `traceId`, reconstructing the full path a single request took across services.
- **OpenTelemetry (OTel)** — the vendor-neutral standard API/SDK for producing this data, so instrumentation doesn't lock a codebase to one observability vendor.
- **Logs** — discrete events with arbitrary detail: the richest detail, but expensive at full volume and hard to correlate across services without a shared trace ID embedded in each log line.
- **Metrics** — aggregated numbers over time (request rate, error rate, p99 latency): cheap to store and query at any volume, but only tell you THAT something is wrong, never WHICH request or WHY.

## Decision Table

| Signal | Cost to capture at full volume | What it answers |
|---|---|---|
| Metrics | Cheap — pre-aggregated | "Is something wrong, right now, in aggregate?" |
| Logs | Moderate — grows with event volume/detail | "What exactly happened, in detail, for this one thing?" |
| Traces | Expensive at 100% sampling — usually sampled in production | "Where, in a multi-service call chain, did the time/failure happen?" |

| Question | Signal to reach for |
|---|---|
| Is anything wrong right now? | Metrics/dashboard |
| Where in the call chain did this specific request fail/slow down? | A trace for that request |
| Why, in full detail, did that specific span fail? | Logs for that trace ID |

## Key Numbers (real, executed — OpenTelemetry Java SDK, `TracingDemo.java`)

```
4 spans, all sharing traceId 889ba9722928321ef6ddda8b315baf4e:
  'order-service.validate'  INTERNAL
  'payment-db.insert'       INTERNAL (ERROR: connection pool exhausted)
  'payment-service.charge'  INTERNAL (ERROR, propagated from db.insert)
  'POST /orders'            INTERNAL {http.route=/orders, http.method=POST}
```
Failure chain: `POST /orders` → `payment-service.charge` → `payment-db.insert`. Sampling config shows 100% capture for this trace given its error status.

## Common Pitfalls

- Relying on only one of logs/metrics/traces and being surprised it can't answer questions it was never designed to answer
- Instrumenting some services with trace propagation and not others, silently breaking trace reconstruction at exactly the boundary that matters most
- Treating tracing, GC-log-reading, and query-plan-reading as unrelated skills rather than the same "diagnose from an artifact" discipline applied to different artifact types

## Interview Answer Skeleton

**30-sec:** A trace is a tree of spans sharing one `traceId`; the shared ID is the entire mechanism that lets a tracing backend reconstruct a multi-service request's path. Metrics detect that something's wrong, traces localize where, logs explain why — three complementary signals, none substitutes for the others.

**2-min:** Add why it exists (no single signal answers all three questions) + the sampling trade-off (100% at full volume is expensive) + the real 4-span trace example with its error chain.

**Whiteboard:** Draw the tree: `POST /orders` as root, branching to `order-service.validate` (OK) and `payment-service.charge` (ERROR), which branches to `payment-db.insert` (ERROR). Annotate every node with the same `traceId` and a distinct `spanId`.

**Staff-level framing:** partial instrumentation is the failure mode that matters most — a single un-instrumented service silently breaks trace reconstruction at exactly the boundary an incident needs localized. Treat trace-context propagation as something that must survive a library/framework migration, verified explicitly, not assumed.

## Production Warning Signs

- **Real incident pattern:** a trace for a slow-checkout complaint ends abruptly at the `payment-service` boundary — no child spans — even though logs from a downstream fraud-check service show activity at the right timestamp. Root cause: the fraud-check service migrated to a new HTTP library, and the migration dropped the interceptor that propagated the incoming `traceparent` header to outbound calls.
- Fix: restore trace-context propagation; add an automated synthetic-trace check to the deployment pipeline that fails if any service in the call path breaks context propagation. Warning sign to watch for: a trace ending abruptly at a service boundary with no child spans — suspect broken propagation at that service before assuming sampling or backend issues.

## Related

- [Percentiles, Tail Latency, and Coordinated Omission](percentiles-tail-latency-and-coordinated-omission.md)
- [Performance Methodology and SLO Error Budgets](performance-methodology-and-slo-error-budgets.md)
- [GC Fundamentals and Log Analysis](gc-fundamentals-and-log-analysis.md)
