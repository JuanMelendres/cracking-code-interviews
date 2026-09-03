---
title: "Naive hash % N Cache Scaling Causing a Database Overload"
document_type: production-cookbook-entry
domain: system-design
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md
source: handbook/system-design/data-partitioning-and-consistent-hashing.md#production-scenarios
---

# Naive hash % N Cache Scaling Causing a Database Overload

## Context

A caching layer selects nodes using naive `hash(key) % N` node selection, where `N` is the current node count.

## Symptoms

The cache cluster is scaled from 10 to 11 nodes to handle growing load. Immediately after the scaling event, the origin database experiences a severe, sustained overload despite the cache cluster now having more total capacity.

## Impact

A scaling operation intended to relieve load instead triggers exactly the outage it was meant to prevent.

## Initial Hypotheses

- The new node is misconfigured — checked and ruled out; it's healthy and serving traffic normally.
- The added capacity somehow increased database traffic directly — implausible, and not supported by any code path.
- The cache-key remapping itself caused a mass cache-miss event — correct.

## Evidence

Cache hit-rate metrics show a near-total drop immediately after the scaling event, recovering gradually over the following minutes as the cache warms back up. The caching layer's node-selection code is confirmed to use `hash(key) % nodeCount`.

## Investigation Timeline

1. **Severe database overload observed** immediately following a routine cache-cluster scale-out.
2. **New-node misconfiguration and direct-traffic-increase hypotheses ruled out**, confirming the new node was healthy and no code path directly increases database calls.
3. **Cache hit-rate metrics inspected**, revealing a near-total drop coinciding exactly with the scaling event.
4. **Node-selection logic reviewed**, confirming the plain `hash(key) % nodeCount` scheme.

## Root Cause

Adding an 11th node changed the divisor in `hash(key) % N` from 10 to 11, which remaps the overwhelming majority of keys to a different node than before. Every remapped key is now a guaranteed cache miss on the node it's newly assigned to, producing a mass, simultaneous cache-stampede-like effect across nearly the entire working set, hitting the origin database with close to its full unmitigated read load at once.

## Immediate Mitigation

Temporarily rate-limit or shed read traffic at the application layer to protect the database while the cache re-warms.

## Permanent Fix

Migrate the cache-node-selection logic to consistent hashing with virtual nodes, so future scaling events remap only the new node's proportional share — roughly 1/N — of keys rather than nearly all of them, eliminating this class of self-inflicted stampede on every future scaling operation.

## Alternatives Considered

Scheduling scaling events during low-traffic windows as a mitigation. Rejected as a workaround that doesn't fix the underlying mechanism and doesn't help for unplanned node failures, which trigger the identical remapping problem without any scheduling control at all.

## Trade-offs

Consistent hashing with virtual nodes adds a small amount of memory and lookup overhead — the ring structure — compared to a plain modulus. Accepted, since the alternative is a mass cache invalidation on every scaling or failure event.

## Prevention

Any hash-based node-selection scheme should be reviewed specifically for its behavior under a node-count change, not just its behavior at a fixed node count — the naive scheme's problem is invisible until the exact moment a node is added or removed.

## Monitoring and Alerts

- Cache hit-rate tracked as a first-class, real-time metric with alerting on any sharp drop, catching this class of remapping event immediately rather than only being visible retroactively through the database overload it causes.
- A pre-scaling review checklist item explicitly checking the node-selection scheme's remapping behavior before any planned scaling operation, since the naive scheme's failure mode is entirely predictable in advance once the mechanism is understood.

## Interview Story

This maps directly to "add a node, how much data moves" as a real, self-inflicted incident. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a cache-cluster scale-out from 10 to 11 nodes, intended to add capacity, instead caused a severe database overload.
- **Task:** explain how adding capacity produced an outage rather than relieving load.
- **Action:** rule out node misconfiguration and any direct code path increasing database traffic; inspect cache hit-rate metrics, finding a near-total drop timed exactly to the scaling event; trace the mechanism to the `% N` divisor change remapping nearly every key.
- **Result:** migrated to consistent hashing with virtual nodes, so future scaling and failure events remap only a proportional share of keys instead of nearly all of them.

## Staff-Level Discussion

The critical insight this incident teaches is that a naive `hash % N` scheme's correctness at any fixed node count is irrelevant to its safety under a node-count *change* — the scheme can serve perfectly well for months at N=10, giving no signal that it's fragile, and then fail catastrophically the instant N becomes 11, because the failure mode is specifically about the transition, not the steady state. This is why the chapter's own framing treats node addition (scaling) and node removal (failure) as the same underlying risk: both change N, and both trigger the identical mass-remapping mechanism regardless of whether the change was planned or not. A Staff engineer reviewing any hash-based partitioning or node-selection scheme should explicitly test its behavior under a node-count change before relying on it in production, since a scheme's behavior at a fixed size says nothing about its safety during the exact operational events — scaling and failure recovery — that a distributed system needs to handle routinely.

## Related Handbook Chapters

- [Data Partitioning and Consistent Hashing](../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md) — canonical consistent-hashing and virtual-node remapping mechanics used here.
