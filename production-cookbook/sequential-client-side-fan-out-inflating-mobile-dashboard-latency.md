---
title: "Sequential Client-Side Fan-Out Inflating Mobile Dashboard Latency"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md
source: syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md#production-scenarios
---

# Sequential Client-Side Fan-Out Inflating Mobile Dashboard Latency

## Context

A mobile app's home-screen dashboard had been built to call each of 6 backend services directly, one after another, rather than through an aggregation layer.

## Symptoms

The mobile app's home screen took a real, noticeably long time to become interactive, especially on cellular connections. The complaint correlated specifically with network conditions rather than app version.

## Impact

Users on slower networks experienced a measurably slow, unresponsive first screen, directly affecting perceived app quality at the moment of first interaction each session.

## Initial Hypotheses

- The mobile client's rendering code was inefficient — this was the first hypothesis pursued.

## Evidence

A network trace showed the screen issuing 6 separate, sequential HTTPS calls to 6 different backend services, each with its own real network round-trip latency, before the screen could render.

## Investigation Timeline

1. **User complaints correlated with network conditions**, not app version, pointing away from a pure client-rendering bug.
2. **Rendering-code hypothesis pursued first**, on the assumption client-side processing was the bottleneck.
3. **Network trace captured**, revealing 6 sequential HTTPS calls to 6 distinct backend services before the screen could render.
4. **Root mechanism confirmed**: no single call was slow — the screen was paying 6 real round trips' worth of latency, serially, because each call waited for the previous one to complete.

## Root Cause

The mobile client had been built to call each backend service directly, one after another, rather than concurrently or through an aggregation layer. Each call carried a real WAN round-trip cost to the client's actual network, and those costs summed serially across all 6 calls before the screen could render.

## Immediate Mitigation

Reordered the calls to start earlier in the screen's lifecycle, a partial improvement that did not address the underlying sequential-fan-out structure.

## Permanent Fix

Introduced a mobile-specific BFF endpoint that fanned out to all 6 backends concurrently server-side, in a data center with far lower inter-service latency than a mobile client's WAN round trip to each, and returned one combined, mobile-shaped response — collapsing 6 real client round trips into 1.

## Alternatives Considered

Having the mobile client issue the same 6 calls concurrently instead of sequentially, without introducing a BFF. Not adopted as the permanent fix because it still requires the client to pay 6 real WAN round trips (even if overlapped), rather than 1, and inter-service latency inside a data center is genuinely lower than client-to-service WAN latency for each of the 6 calls.

## Trade-offs

The BFF endpoint now needs updating whenever the mobile dashboard's data needs change, an ongoing maintenance cost. This was accepted against the real, measured latency improvement.

## Prevention

Added a review guideline that any new client screen needing data from more than two backend services should default to a BFF aggregation endpoint rather than direct multi-service calls from the client.

## Monitoring and Alerts

- Client-side screen time-to-interactive tracked by network condition (e.g., cellular vs. wifi), since this is the signal that first surfaced the issue and generalizes to future screens.
- A review guideline enforced at design time (more than two backend calls per screen triggers a BFF discussion) rather than relying on a latency complaint to surface the issue after the screen has already shipped.

## Interview Story

This maps to a "why is our mobile app slow to load" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a mobile dashboard was slow to become interactive, especially on slower networks.
- **Task:** find why network conditions specifically correlated with the complaint.
- **Action:** ruled out inefficient client rendering; a network trace showed 6 sequential backend calls each paying a real WAN round trip.
- **Result:** introduced a mobile-specific BFF that fans out to all 6 backends concurrently server-side, collapsing 6 client round trips into 1.

## Staff-Level Discussion

The BFF pattern's real value here is not architectural tidiness — it is a genuine reduction in the number of real network round trips a client has to pay for, achieved by moving the fan-out to a location where inter-service latency is far cheaper than client-to-service WAN latency. The trade-off is a durable, standing one: the BFF becomes a piece of infrastructure that must be kept in sync with the client's evolving data needs, effectively trading a one-time architectural cost for an ongoing coordination cost between backend and mobile teams. A Staff-level read of this is to set an explicit threshold (here, "more than two backend calls") that removes the decision from case-by-case judgment, since without a stated threshold every individual screen's fan-out looks locally reasonable and the aggregate cost only becomes visible in production, on real networks, after the fact.

## Related Handbook Chapters

- [API Gateway, BFF, and Edge Concerns](../syllabus/07-api-design/api-gateway-bff-and-edge-concerns.md) — canonical BFF fan-out model and round-trip-reduction mechanism used here.
