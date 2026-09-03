---
title: "Cheat Sheet: Capacity Planning & Headroom"
slug: capacity-planning-and-headroom
document_type: cheat-sheet
domain: performance
topic_id: T-1208
canonical: ../handbook/performance/capacity-planning-and-headroom.md
last_updated: 2026-09-01
---

# Capacity Planning & Headroom

**Canonical chapter:** [`syllabus/16-performance-jvm/capacity-planning-and-headroom.md`](../syllabus/16-performance-jvm/capacity-planning-and-headroom.md)

## Core Mental Model

Every system with finite capacity and incoming work behaves like a queue, whether or not anyone modeled it as one. As offered load approaches the system's maximum service rate, waiting time does not grow *linearly* — it grows sharply, because each request now waits behind an ever-larger backlog. Throughput can look perfectly healthy right up until the moment it doesn't, because throughput measures what got *done*, not what's *waiting*. Capacity planning finds where that cliff actually is before load finds it for you, then deliberately operates a safe distance back from the edge.

## Essential Definitions

- **Little's Law:** `L = λW` — average number in system = throughput × average time-in-system. Holds for any stable queueing system regardless of arrival distribution or server count.
- **Utilization:** `ρ = λ/μ` — offered rate over max service rate. Wait time grows roughly as `ρ/(1-ρ)` as `ρ → 1` — not linearly.
- **Headroom** — the deliberately unused capacity kept in reserve: the gap between provisioned capacity and typical operating load, sized for spikes, redundancy loss, and organic growth.
- **Saturation signal** — throughput and latency decouple exactly at the ceiling: throughput flattens (physically cannot go faster) while latency grows sharply.

## Decision Table

1. Has this system's actual saturation point ever been *measured* (not estimated from a spec sheet)? No → measure it before setting any capacity target.
2. What's the cost asymmetry between under- and over-provisioning for this service? A public-facing checkout path skews toward more headroom; an internal batch job with a flexible deadline tolerates less.
3. Does autoscaling react fast enough relative to how quickly load can spike? If new capacity takes minutes and load spikes in seconds, autoscaling alone isn't a substitute for static headroom.
4. Is there a known future event (launch, campaign, seasonal peak) historical trends won't predict? Yes → forecast it specifically and provision ahead of it.

**Trade-offs:**

| Choice | Helps | Hurts |
|---|---|---|
| High target utilization (90%) | Lower infra cost | Little headroom for spikes; tail latency risk grows sharply near saturation |
| Low target utilization (40%) | Large safety margin | Higher steady-state cost for rarely-used capacity |
| Reactive autoscaling only | Simple, no forecasting | Scale-out lags the spike (instance startup, warm-up) |

## Key Numbers (real, executed bounded `ExecutorService` under controlled real load)

Little's Law verified two independent ways, agreeing within 0.8%:

```
L (direct sampling): 3.264
λ × W (derived):      3.290
```

Saturation divergence (8-worker, 50ms-per-request pool):

```
Throughput plateaus around 148 req/s as offered load rises from 140 to 200 req/s
p50 latency: 54ms -> 701ms
p99 latency: 55ms -> 1373ms (a 25x increase)
```

## Common Pitfalls

- Assuming throughput and latency always move together — this chapter measures the exact point where they decouple.
- Treating utilization-to-latency as linear, leading to provisioning decisions that "look fine on paper" (e.g., "only at 85% CPU") but sit dangerously close to the queueing cliff.
- Never actually measuring a saturation point, provisioning against a vendor spec sheet or "what we've always used."
- Relying entirely on reactive autoscaling without accounting for its own reaction latency.

## Interview Answer Skeleton

**30-sec:** Capacity planning means measuring a system's real saturation point and deliberately provisioning below it — typically 60-70% utilization — because latency grows sharply, not linearly, as utilization approaches 100%. Throughput and latency decouple exactly at that ceiling.

**2-min:** Add Little's Law with its real cross-check (3.264 vs. 3.290, agreeing within 0.8%), the `ρ/(1-ρ)` non-linearity, and the measured divergence (148 req/s plateau while p99 grows 25x).

**Whiteboard:** Graph with offered load on x-axis, two lines: throughput (rises, then flattens at a ceiling) and latency (flat, then rises sharply past that same ceiling). Mark the ceiling "saturation point (measured)" and a line to its left "provisioned operating point (60-70% of ceiling)" — the gap between them is headroom. Narrate: "the entire risk lives in that gap, which is why headroom is a number, not a feeling."

**Staff-level framing:** Frame capacity planning at organizational scale as a portfolio decision — which services get dedicated headroom budgets, which share elastic capacity, and how a shared-infrastructure incident (one team's spike consuming another's assumed-available capacity) gets prevented through quota and isolation.

## Production Warning Signs

- A checkout service's p99 jumping from 80ms to 4 seconds during a flash sale while its throughput dashboard showed only a "modest increase" — the dashboard was showing throughput *flattening at its ceiling*, not tracking the much larger offered load; the service had never been load-tested past typical daily peak.
- CPU/connection-pool utilization pinned near 100% during an incident with no deployment in the preceding 48 hours — a real capacity ceiling reached, not a code regression.
- "We'll just autoscale" as the entire capacity strategy — ignores reaction latency and gives no answer for the gap between load arriving and new capacity becoming useful.

## Related

- `syllabus/13-observability/percentiles-tail-latency-and-coordinated-omission.md`
- `syllabus/13-observability/performance-methodology-and-slo-error-budgets.md`
- `syllabus/08-testing/performance-and-load-testing-methodology.md`
