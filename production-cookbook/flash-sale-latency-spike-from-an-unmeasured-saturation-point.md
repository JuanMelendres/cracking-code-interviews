---
title: "Flash-Sale Latency Spike From an Unmeasured Saturation Point"
document_type: production-cookbook-entry
domain: performance
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/performance/capacity-planning-and-headroom.md
source: handbook/performance/capacity-planning-and-headroom.md#production-scenarios
---

# Flash-Sale Latency Spike From an Unmeasured Saturation Point

## Context

A checkout service had never been load-tested past its typical daily peak, and its instance count had been sized against average traffic rather than a measured saturation point.

## Symptoms

The checkout service's p99 latency jumped from 80ms to 4 seconds during a flash sale, while its throughput dashboard showed a modest, unremarkable increase from typical daily peak.

## Impact

Checkout — the most revenue-critical path in the system — became effectively unusable during a marketing-driven, high-visibility traffic event, producing lost sales and a public-facing outage during exactly the event it needed to support.

## Initial Hypotheses

- A downstream dependency (payment gateway) slowing down; database connection pool exhaustion; a deployed regression coinciding with the sale — these were the initial hypotheses pursued.

## Evidence

CPU and connection-pool utilization on the checkout service's own instances were pinned near 100% for the duration of the incident. The downstream payment gateway's own latency was unaffected, and no deployment had occurred in the preceding 48 hours.

## Investigation Timeline

1. **p99 latency spike observed** during the flash sale, alongside a throughput dashboard showing only a modest increase.
2. **Downstream and deployment hypotheses pursued first** — payment gateway slowness, connection pool exhaustion, a recent regression.
3. **Payment gateway and deployment history ruled out**: the gateway's own latency was unaffected, and no deployment had occurred in the preceding 48 hours.
4. **Instance-level utilization inspected**, showing CPU and connection-pool usage pinned near 100% — the signature of the service's own instances being saturated, not a downstream dependency.

## Root Cause

The service had never been load-tested past its typical daily peak and was sized against average traffic rather than a measured saturation point. The flash sale's real offered load exceeded the service's actual maximum throughput, and the throughput dashboard's "modest increase" was actually throughput flattening at its ceiling — its shape looked healthy specifically because it couldn't show the requests that were now queueing instead of completing.

## Immediate Mitigation

Emergency horizontal scale-out, plus a temporary request-shedding rule (reject a percentage of low-priority requests) to bring utilization back under saturation for the requests still being admitted.

## Permanent Fix

A real load test established the service's actual saturation point; capacity was re-provisioned to keep typical peak load at roughly 65% of that measured ceiling, with autoscaling configured to trigger well before that threshold rather than in response to already-elevated latency.

## Alternatives Considered

Relying solely on reactive autoscaling triggered by elevated latency, without first establishing a measured saturation point. Rejected because latency-triggered autoscaling reacts only after the service is already past a comfortable operating point — the permanent fix instead set the autoscaling trigger well below the measured ceiling, acting on utilization rather than on the already-degraded symptom.

## Trade-offs

The re-provisioned baseline costs more to run year-round for a spike that happens a few times a year. The team judged that cost acceptable against the cost of the incident — lost sales, on-call escalation, and a public-facing outage during a marketing-driven traffic event.

## Prevention

A standing calendar reminder to re-run the load test before each known high-traffic event, rather than relying on the previous test's numbers indefinitely as load and code both change.

## Monitoring and Alerts

- Throughput and latency tracked together, not independently, since the two signals decouple specifically at saturation — a throughput plateau alongside rising latency is a distinct, alertable saturation signature, not "throughput looks fine."
- Autoscaling triggers set on utilization approaching the measured saturation ceiling, rather than on already-elevated latency, so scaling happens before degradation rather than in response to it.

## Interview Story

This maps to a "how do you diagnose a latency spike under load" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** checkout p99 latency jumped from 80ms to 4 seconds during a flash sale, while throughput looked only modestly elevated.
- **Task:** find why throughput looked healthy while latency clearly wasn't.
- **Action:** ruled out the payment gateway and a recent deployment; found CPU and connection-pool utilization pinned near 100% on the service's own instances.
- **Result:** established the service's real saturation point via a proper load test, re-provisioned capacity to keep typical peak at roughly 65% of that ceiling, and moved autoscaling triggers ahead of the saturation threshold.

## Staff-Level Discussion

"Throughput looked fine, latency didn't" is the single most diagnostic sentence in a capacity-related incident story — it demonstrates understanding that the two signals decouple exactly at saturation, not that they always move together. A throughput dashboard cannot show the requests that are queueing instead of completing, so its "modest increase" during this incident was actively misleading rather than merely uninformative. The organizational lesson generalizes past this one service: capacity sized against average or typical-peak traffic, with no measured ceiling ever established, is a latent risk that stays invisible until a real spike exceeds it — and the permanent fix (headroom sized against a measured saturation point, re-verified before known high-traffic events) treats capacity as a maintained, periodically re-validated property of the system, not a one-time sizing decision made at initial launch.

## Related Handbook Chapters

- [Capacity Planning & Headroom](../handbook/performance/capacity-planning-and-headroom.md) — canonical saturation-point model and headroom-sizing method used here.
