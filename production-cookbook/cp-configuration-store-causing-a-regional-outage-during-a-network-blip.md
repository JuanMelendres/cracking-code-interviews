---
title: "CP Configuration Store Causing a Regional Outage During a Network Blip"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/system-design/cap-theorem-and-consistency-models.md
source: handbook/system-design/cap-theorem-and-consistency-models.md#production-scenarios
---

# CP Configuration Store Causing a Regional Outage During a Network Blip

## Context

Every service in a region depends on a shared, strongly consistent (CP) service-discovery and configuration store for lookups needed to operate.

## Symptoms

A brief, under-one-minute network partition between two data centers causes configuration lookups to start failing entirely in the minority-side data center, taking down every service that depends on it there, even though the underlying application services themselves were healthy.

## Impact

A full regional outage triggered by a transient network issue, disproportionate to the triggering event's actual duration.

## Initial Hypotheses

- An application-level bug — checked and ruled out; application services report healthy, only configuration lookups fail.
- A broader infrastructure failure — checked and ruled out; only the configuration store's cross-region replication was affected.
- The configuration store's CP design correctly refusing to serve potentially stale data — correct, and by design.

## Evidence

The configuration store's own logs show it correctly detected the partition and refused reads and writes on the minority side, exactly as its CP design specifies; the regional outage was a direct, intended consequence of that refusal, not a malfunction.

## Investigation Timeline

1. **Regional outage observed**, disproportionate in scope to a brief, sub-one-minute network partition.
2. **Application-level and broader-infrastructure hypotheses ruled out**, confirming only the configuration store's cross-region path was affected.
3. **Configuration store's own logs inspected**, confirming it correctly detected the partition and refused service on the minority side per its CP design.
4. **Conclusion reached**: the outage was the system working exactly as designed, not a defect.

## Root Cause

The configuration store is, correctly, a CP system — during the partition, it chose consistency over availability, refusing to risk serving stale configuration. The outage is not a bug in the configuration store; it's the system working exactly as designed, applied to a use case (regional service availability) where the cost of that design choice was higher than the team had explicitly accounted for.

## Immediate Mitigation

None needed at the configuration-store level — the partition healed within the minute, and the store correctly resumed serving once consistency could be guaranteed again.

## Permanent Fix

Add a bounded, explicitly stale local cache of the last-known-good configuration for services that can tolerate briefly stale configuration during a partition, so a transient network blip doesn't cascade into a full regional outage for services where staleness is an acceptable trade for availability, without changing the configuration store's own CP guarantee for the services that genuinely need it.

## Alternatives Considered

Switching the configuration store itself to an AP model. Rejected — some consumers, security policy configuration for instance, genuinely need the consistency guarantee and cannot tolerate stale reads.

## Trade-offs

The local fallback cache means those specific services could briefly operate on stale configuration during a partition. Accepted for services where that's tolerable, explicitly not applied to services where it isn't.

## Prevention

Any dependency on a CP system should be reviewed for whether every consumer genuinely needs the consistency guarantee, or whether some consumers would be better served by a bounded-staleness fallback — different consumers of the same shared store can warrant different consistency treatment.

## Monitoring and Alerts

- Configuration-store partition-refusal events tracked and alerted directly, distinguished from a generic "configuration lookup failing" alert, so on-call immediately knows the store is behaving correctly by design rather than investigating it as a malfunction.
- Per-consumer fallback-cache staleness age tracked once the bounded local cache is implemented, so any service operating on stale configuration during a partition is visible, not silently tolerated indefinitely.

## Interview Story

This maps directly to "what does your system give up during a partition" arriving as a real incident. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a brief network partition caused a full regional outage via a configuration store's own correct CP behavior, not a bug.
- **Task:** diagnose an outage where nothing was actually broken.
- **Action:** rule out application-level and broader infrastructure causes; check the configuration store's own logs directly; recognize the refusal to serve as intended CP behavior rather than a malfunction.
- **Result:** added a bounded, explicitly stale local fallback cache for consumers that can tolerate it, preserving the configuration store's strong consistency guarantee for the consumers that genuinely require it.

## Staff-Level Discussion

The most important realization in this incident is that no fix was needed at the level most engineers would first look — the configuration store behaved exactly as its CP design specifies, and "fixing" it (for example, by switching it to AP) would have broken the guarantee that other, security-sensitive consumers genuinely depend on. The actual gap was organizational, not technical: nobody had explicitly evaluated, per consumer, whether the store's CP cost (unavailability during a partition) was acceptable for every service depending on it. This is the general lesson CAP theorem teaches at the level of an entire organization's shared infrastructure, not just one system's internal design: a single shared dependency's consistency-vs-availability choice is not automatically the right choice for every consumer, and the fix belongs at the consumer layer (a bounded fallback cache) rather than by weakening the shared store's guarantee for everyone. A Staff engineer's contribution is recognizing this distinction and resisting the pressure to "fix" a correctly functioning system.

## Related Handbook Chapters

- [CAP Theorem and Consistency Models](../handbook/system-design/cap-theorem-and-consistency-models.md) — canonical CP/AP trade-off mechanics used here.
