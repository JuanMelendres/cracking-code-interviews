---
title: "Round-Robin Load Balancing Silently Overloading a Slower Backend Instance"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md
  - ../syllabus/11-system-design/resilience-patterns.md
source: handbook/system-design/load-balancing-service-discovery-and-health-checking.md#production-scenarios
---

# Round-Robin Load Balancing Silently Overloading a Slower Backend Instance

## Context

A fleet of three backend instances sits behind a load balancer configured to distribute traffic using round-robin.

## Symptoms

Under load, the fleet shows uneven latency — one instance's requests take dramatically longer to complete than the other two, despite receiving what should be an equal share of traffic.

## Impact

Overall batch/request latency across the fleet is dragged up by the slow instance's queue building up behind it, even though two of the three instances remain fast and lightly loaded.

## Initial Hypotheses

The uneven latency pattern itself pointed at the load-balancing algorithm's blindness to backend speed, rather than at the backends' code directly, once the measurement below was run.

## Evidence

[`AlgorithmComparisonDemo`](../../practice/java/system-design/load-balancing-and-health-checking/README.md) reproduced this directly: two fast backends (5ms real processing time) and one slow backend (200ms), 300 real requests through a real reverse proxy. Round-robin sent the slow backend its full, blind 100-of-300 share — real total batch time: **921ms**. The same 300-request batch through least-connections sent the slow backend only **10 of 300** requests, using its real, live in-flight-count as the only signal — real total batch time: **208ms**, a direct, measured **~4.4x** improvement from the algorithm alone, with zero change to any backend.

## Investigation Timeline

1. Uneven per-instance latency observed under load across an otherwise identical three-instance fleet.
2. Round-robin's request distribution confirmed to be even by request count (100 of 300 to each backend), ruling out a load-balancer misconfiguration in the "unequal shares" sense.
3. `AlgorithmComparisonDemo` run with two fast (5ms) and one slow (200ms) backend under round-robin, measuring a real 921ms total batch time with the slow backend receiving its full blind share.
4. The identical 300-request batch re-run through least-connections, measuring 208ms total batch time with the slow backend receiving only 10 of 300 requests based on its live in-flight count.
5. Diagnosis confirmed by comparison: the ~4.4x improvement came entirely from the algorithm's use of a runtime signal, with no change to any backend's code or capacity.

## Root Cause

Round-robin has no runtime signal at all; it cannot distinguish a slow backend from a fast one, so it keeps sending both an identical share regardless of the real, growing queue building up behind the slow one.

## Immediate Mitigation

Switch the algorithm to least-connections (or least-response-time) — no backend-side change required, confirmed directly by this chapter's own before/after measurement.

## Permanent Fix

Investigate why one backend is structurally slower (undersized instance, a hot-partition/hot-key problem, a code-level regression) — least-connections manages the symptom in real time, but doesn't fix a genuine capacity or code imbalance.

## Alternatives Considered

None recorded as rejected — least-connections is presented as the direct, measured fix for the algorithm-level symptom, with the underlying capacity investigation as a separate, necessary follow-up.

## Trade-offs

Least-connections requires the load balancer to track real, live per-backend state (in-flight count), a small but real bookkeeping cost round-robin doesn't pay.

## Prevention

Default to a load-balancing algorithm that uses a real runtime signal (least-connections or least-response-time) for any fleet where backend cost genuinely varies request-to-request, rather than assuming round-robin's "fairness" actually produces even load.

## Monitoring and Alerts

- Track per-backend latency (not just aggregate fleet latency) as a standing dashboard panel; the uneven-latency symptom in this incident is only visible when instances are compared against each other, and an aggregate-only view would have shown a milder, harder-to-diagnose overall latency increase instead.
- Alert on per-backend in-flight request count diverging significantly from the fleet average under an algorithm that should be balancing it (least-connections) — a divergence there indicates either a persistently slower backend or a load-balancer misconfiguration, and is the direct instrumentation this incident's own before/after measurement relied on.
- Periodically re-run a controlled comparison (fast vs. slow synthetic backend, as in `AlgorithmComparisonDemo`) whenever the load-balancing algorithm or backend fleet composition changes, to catch a regression back to a signal-blind algorithm before it reaches production traffic.

## Interview Story

This maps directly to a "compare load-balancing algorithms" question, backed by a real measured before/after. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** a three-instance backend fleet behind round-robin load balancing showed dramatically uneven per-instance latency under load.
- **Task:** determine whether the algorithm itself, rather than the backends, was responsible.
- **Action:** ran a controlled comparison with a known slow backend under both round-robin and least-connections, measuring total batch time and per-backend request share for each.
- **Result:** measured a ~4.4x total-batch-time improvement from switching to least-connections alone, with zero backend-side change, then separately investigated why one backend was structurally slower in the first place.

## Staff-Level Discussion

The organizational risk in this incident is that round-robin's "fairness" is a plausible-sounding default that is actually blind to the one thing that matters for real load distribution — how long each backend actually takes to serve a request. A team that never deliberately measures this trade-off can carry the assumption that equal request counts mean equal load indefinitely, right up until backend cost genuinely diverges (a partial capacity loss, a hot-key skew, a partial regression) and the blind algorithm silently amplifies it into a fleet-wide latency problem. The Staff-level framing worth carrying into a design review is that least-connections' small bookkeeping cost (tracking live in-flight count per backend) is cheap relative to the correctness it buys, and should be the default for any fleet where backend cost is not provably uniform — with round-robin reserved deliberately for the narrower case where uniformity is actually verified, not assumed.

## Related Handbook Chapters

- [Load Balancing, Service Discovery, and Health Checking](../syllabus/11-system-design/load-balancing-service-discovery-and-health-checking.md) — canonical comparison of load-balancing algorithms and the `AlgorithmComparisonDemo` measurement this incident reproduces.
- [Resilience Patterns](../syllabus/11-system-design/resilience-patterns.md) — broader patterns for handling backend-capacity variance beyond load-balancer algorithm choice.
