---
title: "Cheat Sheet: Performance Methodology and SLO Error Budgets"
slug: performance-methodology-and-slo-error-budgets
document_type: cheat-sheet
domain: performance
topic_id: T-1206
canonical: ../handbook/performance/performance-methodology-and-slo-error-budgets.md
last_updated: 2026-08-04
---

# Performance Methodology and SLO Error Budgets

**Canonical chapter:** [`handbook/performance/performance-methodology-and-slo-error-budgets.md`](../handbook/performance/performance-methodology-and-slo-error-budgets.md)

## Core Mental Model

USE diagnoses a resource, RED diagnoses a service, and an error budget turns "how risky can we be this month" into a number instead of a gut call. All three exist to replace vague operational language with something quantifiable and comparable across incidents.

## Essential Definitions

- **USE** (Utilization, Saturation, Errors) — diagnoses a RESOURCE (a CPU, a disk, a connection pool): how busy it is, how much work is queued waiting for it, and whether it's throwing errors.
- **RED** (Rate, Errors, Duration) — the equivalent methodology for a SERVICE (a request-handling endpoint): requests per second, error rate, latency distribution.
- **SLI** (service level indicator) — a measured metric (e.g., success rate). **SLO** (service level objective) — a target for that SLI (e.g., 99.9%). **Error budget** — what's left to spend before the SLO is breached.

## Decision Table

| Choice | Benefit | Cost |
|---|---|---|
| Tighter SLO (e.g., 99.99%) | Better guaranteed user experience | Much smaller error budget — one incident can breach it; expensive to sustain |
| Looser SLO (e.g., 99.5%) | Cheaper to sustain, more room for incidents | Worse guaranteed experience; may not meet real expectations |
| Error budget tracked monthly | Smooths day-to-day noise | Can hide a severe single-day incident inside a fine monthly aggregate — track both |
| USE for resources, RED for services | Matches methodology to diagnostic target | A resource can be healthy by USE while the service built on it fails by RED (a bug, not saturation) |

| Target | Methodology |
|---|---|
| A resource (CPU, disk, connection pool, heap) | USE: Utilization, Saturation, Errors |
| A service (an endpoint, a consumer group) | RED: Rate, Errors, Duration |
| "How much risk can we take this month?" | Error budget remaining + daily burn-rate trend, not just the monthly aggregate |

## Key Numbers (real, computed — `ErrorBudgetDemo.java`, 30-day SLO simulation)

```
SLO: 99.900% success over 30 days (60,000,000 requests) -> 60,000 allowed failures
Day 16: 446 failures       (budget remaining: 53,143)
Day 17 (incident): 8,791 failures  (budget remaining: 44,352)
Day 18: 402 failures       (budget remaining: 43,950)

Actual 30-day success rate: 99.96492% (SLO met)
Total failures: 21,050 of 60,000 allowed (35.1% of budget consumed)
```
The single 40-minute day-17 incident consumed ~8,350 more failures than the ~450/day background rate — close to 14% of the entire month's budget in under an hour, even though the monthly aggregate still showed "SLO met."

## Common Pitfalls

- Reporting only monthly/aggregate error-budget consumption without checking the daily burn-rate pattern underneath it
- Treating USE and RED as interchangeable rather than matched to their targets (resources vs. services)
- Treating this vocabulary as new content rather than recognizing it retrofits precision onto every earlier operational decision

## Interview Answer Skeleton

**30-sec:** USE diagnoses a resource; RED diagnoses a service. An SLO is a target for a measured SLI; the error budget is what's left before it's breached — measured directly: a single 40-minute incident consumed ~14% of an entire month's budget even though the monthly aggregate SLO was still met.

**2-min:** Add why it exists + the tighter-vs-looser SLO trade-off + the 40-minute-incident-vs-monthly-aggregate example.

**Whiteboard:** Draw USE → resource box, RED → service box, and the SLI → SLO → error budget chain with an arrow back into a "ship/hold decision" box.

**Staff-level framing:** the genuinely Staff-level move isn't computing error budgets — it's using them as an input to a concrete decision (ship/don't ship, run/defer a maintenance window). RED's Duration ties directly to percentile selection for timeout tuning, and a closed-loop measurement methodology understates the exact tail an SLO is meant to protect.

## Production Warning Signs

- **Real incident pattern:** with two weeks left in the month, a team sees 40% of the error budget consumed, judges the remaining 60% ample, and ships a moderately risky DB migration — three days later the service breaches its monthly SLO. The pre-migration 40% consumption had been trending upward daily for two weeks straight (a memory leak causing gradually increasing timeout-driven failures); the migration's added risk tipped an already-climbing trend over the edge.
- Prevention: any decision gated on error-budget headroom should require the daily burn-rate chart, not just the aggregate percentage.

## Related

- [Percentiles, Tail Latency, and Coordinated Omission](percentiles-tail-latency-and-coordinated-omission.md)
- [Logging, Metrics, Tracing, and OpenTelemetry](logging-metrics-tracing-and-opentelemetry.md)
- [Resilience Patterns](resilience-patterns.md)
