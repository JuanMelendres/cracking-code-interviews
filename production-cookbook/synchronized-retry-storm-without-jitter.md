---
title: "Synchronized Retry Storm Without Jitter"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/11-system-design/resilience-patterns.md
source: handbook/system-design/resilience-patterns.md#production-scenarios
---

# Synchronized Retry Storm Without Jitter

## Context

A downstream payment-verification service is called by many upstream services. Every caller retries failed requests with exponential backoff — but without jitter, so every caller's backoff schedule is a deterministic function of the same triggering timeout.

## Symptoms

A downstream payment-verification service has a brief, roughly 2-second network blip. Every caller's in-flight request times out and retries with exponential backoff. The downstream, having just recovered from the original blip, immediately receives a synchronized spike of retries at the exact instant every client's backoff timer expires, and falls back over — worse than the original blip, and lasting longer.

## Impact

A 2-second transient network issue becomes a multi-minute outage, caused entirely by the retry behavior of the calling services rather than by the original triggering condition.

## Initial Hypotheses

- The original network issue was more severe than reported — checked and ruled out; network monitoring shows the blip resolved in under 2 seconds.
- A capacity regression in the payment-verification service — checked and ruled out; no code or config changes preceded the incident.
- A synchronized retry storm from the initial timeout wave — correct.

## Evidence

The payment-verification service's inbound request rate shows a series of sharp, narrow spikes at intervals matching the exponential backoff schedule (100ms, 200ms, 400ms, 800ms after the original timeout), rather than a smooth elevated rate — the "every client retries at the identical instant" signature of a synchronized, jitter-free backoff schedule.

## Investigation Timeline

1. **Outage observed**, starting shortly after a brief, already-resolved network blip.
2. **Severity and capacity hypotheses ruled out** using existing network and deployment monitoring, neither of which shows anything unusual beyond the original 2-second blip.
3. **Request-rate shape examined.** The spike pattern — narrow, sharp, and recurring at exponential-backoff intervals rather than smoothly elevated — points directly at synchronized client retries rather than a genuine capacity problem.
4. **Root cause confirmed**: every caller's backoff schedule, computed from the same triggering timeout with no randomization, produces identical retry timing across all callers.

## Root Cause

Every caller's retry logic uses exponential backoff without jitter. Because all callers experienced the same triggering timeout at roughly the same moment, their backoff delays compute to the same values, synchronizing every retry wave into a spike large enough to re-trigger an outage in a service that had actually already recovered.

## Immediate Mitigation

Manually stagger a service restart or traffic ramp-up to break the synchronization, and temporarily reduce caller concurrency to let the spikes dissipate.

## Permanent Fix

Add full jitter to every retry policy calling this service, converting synchronized spikes into a smoothed trickle spread across the full exponential backoff window instead of concentrated at its boundaries.

## Alternatives Considered

Removing retries entirely. Rejected — transient blips are real and retrying is the correct response to a genuine transient failure; the fix is decorrelating retry timing across callers, not eliminating retries.

## Trade-offs

Jitter adds worst-case latency variance to individual retrying requests — a request might wait close to the full exponential cap instead of a predictable fixed delay. Accepted, since the alternative is a self-inflicted outage amplification affecting every caller, not just the one retrying request.

## Prevention

A standing requirement that every outbound retry policy uses jitter as a non-negotiable default, verified in code review or — more robustly — enforced by a shared client library so individual implementations cannot omit it by oversight.

## Monitoring and Alerts

- Inbound request-rate shape on the downstream service, specifically watching for narrow periodic spikes rather than relying on average request rate alone, since a retry storm's average rate over a wider window can look unremarkable while the instantaneous peaks are what cause the fallover.
- Correlating any downstream fallover with a preceding upstream timeout event across multiple callers simultaneously, which distinguishes a retry-storm cascade from an independent capacity problem.

## Interview Story

This maps to a "why did retries make an outage worse instead of better" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a brief downstream blip turned into a multi-minute outage after the downstream had already recovered.
- **Task:** explain why the outage outlasted the triggering event by an order of magnitude.
- **Action:** examine the downstream's inbound request-rate shape rather than assuming a capacity regression; recognize the narrow-spike-at-backoff-interval pattern as synchronized retries, not organic load; trace it to jitter-free exponential backoff shared across all callers.
- **Result:** added jitter to the shared retry policy, converting a recurring outage-amplification risk into a smoothed retry trickle.

## Staff-Level Discussion

The mechanism here — deterministic backoff without jitter — looks like a minor implementation detail, and in isolation it usually is. It only becomes an outage multiplier at the scale of "every caller of a shared downstream experiences the same triggering failure simultaneously," which is precisely the condition a brief network blip creates. A Staff engineer's contribution is treating this as a platform-level policy question rather than a per-service bug: if jitter is left to individual teams to remember, it will be missing in exactly the caller that matters during the next incident. Enforcing it in a shared client library removes the dependency on every team's code review catching it, and converts a class of self-inflicted cascading failure into something structurally impossible rather than merely discouraged.

## Related Handbook Chapters

- [Resilience Patterns](../syllabus/11-system-design/resilience-patterns.md) — canonical jitter, backoff, circuit-breaker, and bulkhead mechanics.
- [Distributed Systems Failure Modes](../syllabus/10-distributed-systems/distributed-systems-failure-modes.md) — cascading-failure framing this incident is an instance of.
