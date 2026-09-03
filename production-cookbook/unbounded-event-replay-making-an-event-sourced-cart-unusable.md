---
title: "Unbounded Event Replay Making an Event-Sourced Cart Unusable Over Time"
document_type: production-cookbook-entry
domain: architecture
status: draft
last_updated: 2026-09-02
related_handbook:
  - ../handbook/architecture/event-sourcing-and-its-real-costs.md
  - ../handbook/architecture/event-driven-architecture-integration-styles.md
source: handbook/architecture/event-sourcing-and-its-real-costs.md#production-scenarios
---

# Unbounded Event Replay Making an Event-Sourced Cart Unusable Over Time

## Context

*(Representative scenario, grounded directly in this chapter's own measured replay-cost mechanism.)* A shopping cart is implemented as an event-sourced aggregate — every add-to-cart, remove-from-cart, and quantity-change is an event — with no snapshotting mechanism at all.

## Symptoms

Cart-loading latency for long-lived customer accounts (users who kept items in their cart across many sessions, over months) grew noticeably slower over time, eventually exceeding a 2-second timeout for the most active carts.

## Impact

Checkout became unusable for the most engaged, longest-tenured customers specifically — the exact users a business would least want to lose to a performance failure.

## Initial Hypotheses

A database index was missing or degraded.

## Evidence

The slowest carts had accumulated tens of thousands of events across months of intermittent shopping sessions, and loading the cart meant replaying every one of them from event zero, every single time, exactly matching this chapter's own measured replay-cost growth.

## Investigation Timeline

1. Cart-loading latency observed growing noticeably slower over time for long-lived customer accounts, eventually exceeding a 2-second timeout.
2. Missing or degraded database index hypothesis raised initially.
3. Cart's persistence model reviewed, confirming it is implemented as an event-sourced aggregate with no snapshotting mechanism.
4. Event count per cart audited for the slowest-loading accounts, finding tens of thousands of accumulated events for the most active, longest-lived carts.
5. Confirmed the load path replays every event from event zero on every single cart load, with replay cost growing directly with accumulated event count — matching the chapter's own measured replay-cost growth curve.

## Root Cause

The team had adopted event sourcing for its audit-trail appeal without implementing the mitigation the pattern requires at any real scale — snapshotting.

## Immediate Mitigation

Added a temporary hard cap truncating cart history display to the most recent 500 events for affected accounts, unblocking checkout while a real fix was built.

## Permanent Fix

Implemented periodic snapshotting (every 100 events, matching a snapshot-plus-tail-replay pattern), reducing worst-case replay to a small, bounded tail regardless of total history length.

## Alternatives Considered

None recorded beyond adopting snapshotting — the scenario treats it as the direct, necessary mitigation the pattern requires rather than one option among several.

## Trade-offs

Snapshots are additional storage and a small amount of additional write-path complexity (deciding when to snapshot, keeping snapshot format in sync with the event schema) — accepted because the alternative (unbounded replay cost) was already causing real checkout failures.

## Prevention

Any new event-sourced aggregate now requires a stated snapshotting strategy at design time, not as an afterthought once replay latency becomes visible in production.

## Monitoring and Alerts

- Track per-aggregate event count and replay latency as a standing pair of metrics for every event-sourced aggregate, so a growing-event-count aggregate is visible as a latency risk well before it approaches a timeout threshold, rather than discovered only when checkout fails for the affected accounts.
- Alert when any aggregate's event count exceeds the snapshot interval by a wide margin (e.g., several multiples of the 100-event snapshotting interval) without a corresponding snapshot existing, indicating the snapshotting mechanism itself has failed or fallen behind for that aggregate.
- Add a synthetic load test that periodically loads the oldest, most event-heavy real (or representative) aggregates in a staging environment, catching a regression in replay or snapshot performance before it reaches the specific long-lived accounts most likely to be affected in production.

## Interview Story

This maps directly to "what does event sourcing cost you in practice" arriving as a real, gradually-worsening production failure. Present it as a representative scenario to adapt, not a claimed personal history:

- **Situation:** an event-sourced shopping cart's load latency grew steadily worse for long-lived customer accounts, eventually exceeding a 2-second timeout and blocking checkout.
- **Task:** find the cause after ruling out a database indexing problem.
- **Action:** audited event counts per cart and confirmed the slowest carts had accumulated tens of thousands of events, all replayed from event zero on every load, with no snapshotting mechanism in place.
- **Result:** added a temporary truncation cap to unblock checkout immediately, then implemented periodic snapshotting to bound worst-case replay cost regardless of total history length, going forward.

## Staff-Level Discussion

Event sourcing's audit-trail and full-history appeal is genuinely valuable, but this incident demonstrates why "and its real costs" is not a caveat tacked onto the pattern's description — it is the operational reality of running it past a small scale, and a team that adopts the pattern for its benefits alone, without budgeting for its required companion mechanism (snapshotting), is deferring a cost rather than avoiding it. The failure mode is also instructive in its shape: it degrades gradually and predictably as event count grows, which means it is nearly invisible during initial development and early production use (when no account has accumulated enough history to matter) and becomes a real production incident only once real usage patterns — long-lived, highly-active accounts — have had months to accumulate the event volume that exposes it. A Staff-level review of any new event-sourced aggregate should treat "what is the snapshotting strategy, and at what event count does it kick in" as a mandatory design-time question, precisely because the alternative is discovering the answer reactively, under the pressure of a customer-facing latency failure, exactly as this incident did.

## Related Handbook Chapters

- [Event Sourcing and Its Real Costs](../handbook/architecture/event-sourcing-and-its-real-costs.md) — canonical replay-cost mechanics and the snapshot-plus-tail-replay pattern this incident's fix applies.
- [Event-Driven Architecture: Integration Styles](../handbook/architecture/event-driven-architecture-integration-styles.md) — the broader event-driven context event-sourced aggregates typically operate within.
