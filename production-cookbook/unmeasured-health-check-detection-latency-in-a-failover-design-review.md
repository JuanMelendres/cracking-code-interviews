---
title: "Unverified Health-Check Detection Latency Assumption Ahead of a Design Review"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md
  - ../syllabus/10-distributed-systems/distributed-systems-failure-modes.md
source: handbook/system-design/load-balancing-service-discovery-and-health-checking.md#production-scenarios
---

# Unverified Health-Check Detection Latency Assumption Ahead of a Design Review

## Context

After a backend instance crashes, a design review asks "how long could requests keep failing against it before the load balancer notices?" — a question with no defensible answer without a real, measured number.

## Symptoms

The team has no evidence-backed answer to a concrete, load-bearing question about failover behavior; any answer offered would otherwise be an unverified estimate.

## Impact

Without a real measured bound, any SLA or capacity commitment depending on failover speed would be based on assumption rather than evidence — a risk that would only surface later, during an actual outage, if the assumption turned out to be wrong.

## Initial Hypotheses

None needed — the design review's question is itself the trigger; the response is to measure directly rather than estimate.

## Evidence

[`HealthCheckFailoverDemo`](../../practice/java/system-design/load-balancing-and-health-checking/README.md) really stopped a backend's HTTP server mid-run against a real active health checker polling every 300ms. Real, measured detection latency: **206ms**. Twelve real requests fired immediately after detection all landed correctly on the two remaining healthy backends — real, direct proof the routing decision respected the concurrently-updated health state, not just that detection eventually happened. After a real restart, re-detection took **70ms**.

## Investigation Timeline

1. Design review raises the question of how long requests could fail against a crashed backend before the load balancer's health check notices.
2. `HealthCheckFailoverDemo` set up with a real active health checker polling every 300ms against a live backend fleet.
3. Backend's HTTP server stopped mid-run to simulate a crash, with detection latency measured directly: 206ms.
4. Twelve requests fired immediately following detection, confirming all were routed correctly to the two remaining healthy backends — verifying the routing decision, not just the detection timing, updated correctly.
5. Backend restarted and re-detection latency measured separately: 70ms, establishing the recovery-side bound as well as the failure-side bound.

## Root Cause

Detection latency is structurally bounded by check interval plus per-probe timeout — not instantaneous, and not unbounded either, but a real, specific, tunable number that had simply never been measured before being asked about.

## Immediate Mitigation

None needed — this is expected, correct behavior; the real number (206ms in this test) is the actual answer to the design review's question.

## Permanent Fix

State the real detection-latency bound explicitly in any SLA or capacity discussion depending on failover speed, and shorten the check interval deliberately if a tighter bound is genuinely required — trading more real health-check traffic for faster detection.

## Alternatives Considered

None recorded as rejected for this specific measurement; the trade-off between check interval and detection speed is stated as the lever available if a tighter bound is later required.

## Trade-offs

A shorter check interval detects failures faster but generates real, continuous polling load on every backend, and a too-aggressive timeout risks false-positive ejections during a real, transient slowdown that wasn't actually a failure.

## Prevention

Combine active health checking (this chapter's mechanism) with passive checking (ejecting on real production request failures) to shrink the effective detection window below the active-only bound for failure modes real traffic surfaces faster than a synthetic health check does.

## Monitoring and Alerts

- Track actual production detection latency (time from backend failure to first ejection) as a live metric, not just the configured check-interval-plus-timeout theoretical bound, so any drift between configuration and real-world behavior (network jitter, probe queuing) is caught directly rather than assumed away.
- Alert on health-check probe latency itself trending upward, since a slow probe response inflates effective detection time even if the configured interval hasn't changed.
- Once passive checking is added (the Prevention step), track its contribution to detection speed separately from active checking's — comparing the two shows whether the combined approach is actually shrinking the effective window or whether passive checks are rarely triggering before the active check would have anyway.

## Interview Story

This maps directly to a design-review question about failover detection speed, answered with a real measurement instead of an estimate. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a design review needed a concrete answer to "how long could requests fail against a crashed backend before the load balancer notices?"
- **Task:** provide a defensible, evidence-backed number rather than an estimate.
- **Action:** ran a real health-check-and-failover test, stopping a live backend and measuring actual detection latency (206ms) and recovery re-detection latency (70ms), then verified routing correctness with real follow-up requests.
- **Result:** stated the real, measured bound explicitly in the design review and identified the check-interval/detection-speed trade-off as the lever available if a tighter bound is ever required.

## Staff-Level Discussion

The value of this scenario is procedural, not just numerical: it demonstrates the habit of answering an infrastructure-behavior question with a real, reproducible measurement rather than a plausible-sounding estimate, which matters because any SLA or capacity commitment built on an untested assumption carries silent risk until the assumption is tested — usually during an actual outage, which is the worst possible time to discover it was wrong. The check-interval trade-off itself (faster detection means more continuous polling load and a higher false-positive-ejection risk during transient slowdowns) is a concrete instance of a general Staff-level pattern: nearly every "make it faster" lever in a distributed system has a corresponding cost lever, and stating both explicitly — as this measurement did — is what turns a design review from an argument over intuition into a decision grounded in numbers everyone can inspect and, if needed, re-verify.

## Related Handbook Chapters

- [Load Balancing, Service Discovery, and Health Checking](../syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md) — canonical mechanics of active/passive health checking and the `HealthCheckFailoverDemo` measurement this scenario reproduces.
- [Distributed Systems Failure Modes](../syllabus/10-distributed-systems/distributed-systems-failure-modes.md) — the broader failure-detection context this bound feeds into.
