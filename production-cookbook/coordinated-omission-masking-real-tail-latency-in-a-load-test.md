---
title: "Coordinated Omission Masking Real Tail Latency in a Load Test"
document_type: production-cookbook-entry
domain: performance
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md
source: handbook/performance/percentiles-tail-latency-and-coordinated-omission.md#production-scenarios
---

# Coordinated Omission Masking Real Tail Latency in a Load Test

## Context

Pre-launch load testing for a new checkout flow uses a request generator that waits for each response before issuing the next request — a closed-loop design.

## Symptoms

The load test reports p99 = 200ms, comfortably under the 500ms SLO target. After launch, during peak traffic hours, user complaints about slow checkouts rise sharply, and a real-user-monitoring dashboard shows p99 closer to 900ms during those windows.

## Impact

A feature that passed every pre-launch performance gate produces a real, user-visible slowness problem in production, undermining confidence in the load-testing process for every future launch.

## Initial Hypotheses

- Production traffic volume is simply higher than the load test simulated — checked and ruled out; peak request rate matches the load test's target rate closely.
- A code difference between the load-tested build and the production build — checked and ruled out; identical artifact deployed.
- The load-testing tool used a closed-loop methodology, understating the real tail — correct.

## Evidence

The load-testing tool's request generator explicitly waits for each response before issuing the next — a closed-loop design. Re-running the same load test with an open-loop-corrected tool against a staging environment reproduces a p99 much closer to the 900ms production figure, on the same underlying service.

## Investigation Timeline

1. **Discrepancy noticed** between the pre-launch load test's clean p99 and real-user-monitoring's much worse figure under peak load.
2. **Traffic-volume and build-difference hypotheses ruled out** by comparing request rates and confirming an identical deployed artifact.
3. **Load-testing tool's request-generation strategy inspected**, revealing a closed-loop (wait-for-response-before-next-request) design.
4. **Reproduced with a corrected tool**: an open-loop-corrected re-run against the same staging environment reproduces a p99 close to the real production figure.

## Root Cause

The pre-launch load test suffered from coordinated omission: because it only sent the next request after the previous one returned, it systematically sent fewer requests during any transient slowdown, undersampling exactly the moments that matter most for tail latency, producing a p99 = 200ms figure that looked clean but was measuring something other than real user experience.

## Immediate Mitigation

Treat the real-user-monitoring p99 as the authoritative signal going forward for this feature, and communicate to stakeholders that the pre-launch gate needs a methodology fix, not that the feature itself regressed.

## Permanent Fix

Replace the load-testing tool's request-generation strategy with an open-loop (fixed-schedule) design, or apply a coordinated-omission correction to existing closed-loop data, so future pre-launch gates reflect the tail latency real users will actually experience.

## Alternatives Considered

Simply lowering the pre-launch SLO threshold to compensate. Rejected — it treats the symptom, a passing number that didn't mean what it appeared to, rather than the actual measurement bug.

## Trade-offs

An open-loop load generator is harder to implement correctly — it must handle requests that haven't yet returned when the next is due. Accepted, since the alternative is a load-testing gate that systematically produces false confidence.

## Prevention

Any load-testing tool or harness adopted for pre-launch gating should be verified as open-loop, or coordinated-omission-corrected, before being trusted as an SLO gate.

## Monitoring and Alerts

- Real-user-monitoring p99, tracked as the ultimate source of truth and cross-checked against pre-launch load-test figures at launch time rather than trusted independently — the gap between the two here is the entire diagnostic signal.
- A one-time verification of any adopted load-testing tool's request-generation methodology (open-loop vs. closed-loop) before it's trusted as a launch gate, since this property doesn't change per test run and only needs establishing once per tool.

## Interview Story

This maps to "load test showed a good p99, users report a much worse real experience" directly. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a load test cleared its SLO target cleanly, but real users experienced significantly worse tail latency after launch.
- **Task:** explain the discrepancy without dismissing it as "the load test wasn't realistic" in vague terms.
- **Action:** rule out traffic volume and build differences using direct comparisons; inspect the load-testing tool's request-generation strategy; identify and name the specific measurement bug — coordinated omission from a closed-loop design.
- **Result:** replaced the load-testing methodology with an open-loop design, restoring the pre-launch gate's ability to reflect real user-experienced tail latency.

## Staff-Level Discussion

The specific value of naming "coordinated omission" rather than saying "the load test was unrealistic" is that it points at a precise, fixable measurement bug rather than a vague methodology complaint — a closed-loop generator doesn't merely produce noisy numbers, it produces numbers with a *specific, predictable bias* toward hiding exactly the tail behavior a launch gate exists to catch. This is a recurring trap across performance engineering: a measurement tool can look rigorous (it produced a clean percentile figure) while measuring something systematically different from what stakeholders assume it measures. A Staff engineer's contribution is recognizing that a passing gate is not evidence of correctness unless the measurement methodology itself has been verified — and that verification, once done for a given tool, protects every future launch that gate is used for, not just this one incident.

## Related Handbook Chapters

- [Percentiles, Tail Latency, and Coordinated Omission](../handbook/performance/percentiles-tail-latency-and-coordinated-omission.md) — canonical coordinated-omission mechanics and open-loop/closed-loop distinction used here.
