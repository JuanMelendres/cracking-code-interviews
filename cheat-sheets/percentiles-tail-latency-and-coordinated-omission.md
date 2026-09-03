---
title: "Cheat Sheet: Percentiles, Tail Latency, and Coordinated Omission"
slug: percentiles-tail-latency-and-coordinated-omission
document_type: cheat-sheet
domain: performance
topic_id: T-1204
canonical: ../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md
last_updated: 2026-08-04
---

# Percentiles, Tail Latency, and Coordinated Omission

**Canonical chapter:** [`syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md`](../syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md)

## Core Mental Model

A percentile answers "how bad is it for the worst-affected fraction of users," which is the question that actually matters — an average answers a question nobody experiences. No single user experiences "the average" of a distribution; every user experiences their own single data point, and the users who matter for complaints and churn are the ones in the tail. Coordinated omission is the specific, sneaky way a measurement tool can fail to capture that tail even while looking perfectly reasonable.

## Essential Definitions

- **Percentile** — the latency below which a given fraction of requests fall; p99 = 500ms means 99% of requests were faster than 500ms.
- **Coordinated omission** — a measurement bug in load-testing tools: a naive ("closed-loop") load generator that waits for each request to finish before sending the next one systematically fails to measure the true cost of a slowdown, because it sends fewer requests exactly when the service is struggling.
- **Closed-loop generator** — sends the next request only after the previous one completes.
- **Open-loop generator** — schedules requests on a fixed interval regardless of how long previous requests take.

## Decision Table

| Measurement approach | What it tells you |
|---|---|
| Average latency | Almost nothing about user experience — compatible with many very different real distributions |
| p50 (median) | Typical experience, insensitive to tail outliers |
| p99 / p99.9 | The experience of your most-affected users — directly what an SLO should target |
| Closed-loop load testing | Systematically understates tail latency under any real slowdown |
| Open-loop load testing | Correctly captures queueing delay from a slowdown — the honest, harder-to-implement number |

| Question | Answer |
|---|---|
| Average or percentile for an SLO? | Percentile — average hides too much |
| p99 or max? | p99 (or p99.9) — max is dominated by unrepresentative outliers |
| Closed-loop or open-loop load testing? | Open-loop — closed-loop understates the tail via coordinated omission |

## Key Numbers (real, executed — `CoordinatedOmissionDemo.java`, 100,000 simulated requests, same seed, both methodologies)

Underlying service: 98% of requests take 10ms, 2% stall for 500ms.

```
Closed-loop: p50=10ms  p90=10ms   p99=500ms  p99.9=500ms  max=500ms
Open-loop:   p50=10ms  p90=380ms  p99=830ms  p99.9=1370ms max=2110ms
```
Closed-loop average calculation: `0.98×10 + 0.02×500 = 19.8ms` — looks clean, is wrong. The open-loop p99 (830ms) is 66% higher than the closed-loop p99 (500ms) purely from correcting how latency was measured, not from any change to the service itself.

## Common Pitfalls

- Reporting average latency as if it characterizes user experience
- Load-testing with a closed-loop generator and treating the resulting percentiles as accurate
- Chasing p100/max as an SLO target rather than a high-but-representative percentile like p99 or p99.9

## Interview Answer Skeleton

**30-sec:** Percentiles measure the tail, which is what users actually experience — an average hides it. A closed-loop load generator systematically understates tail latency because it sends fewer requests exactly when the service is struggling; measured here as p99=500ms (closed-loop) vs. p99=830ms (open-loop) on identical underlying service behavior.

**2-min:** Add why it exists (no user experiences "the average") + the closed-loop-vs-open-loop mechanism + the measured 500ms→830ms p99 shift.

**Whiteboard:** Draw the load-generator flowchart; annotate the closed-loop branch: "this is why the number looks clean but is wrong."

**Staff-level framing:** the measurement methodology is itself part of the system under evaluation — getting it subtly wrong produces confidently-wrong conclusions, not obviously-wrong ones. Before trusting any performance claim, ask "how exactly was this number measured."

## Production Warning Signs

- **Real incident pattern:** a load test clears every threshold before launch (p99=200ms against a 500ms SLO), then production users report widespread slowness during peak hours — real-user-monitoring shows p99 ~900ms during peak. Root cause: the pre-launch load test used a closed-loop generator, which systematically understated tail latency under real slowdown conditions.
- Fix: rebuild the load-testing methodology around an open-loop generator; do not simply lower the SLO threshold to match the flawed measurement.

## Related

- [Logging, Metrics, Tracing, and OpenTelemetry](logging-metrics-tracing-and-opentelemetry.md)
- [Performance Methodology and SLO Error Budgets](performance-methodology-and-slo-error-budgets.md)
- `syllabus/08-testing/performance-and-load-testing-methodology.md`
