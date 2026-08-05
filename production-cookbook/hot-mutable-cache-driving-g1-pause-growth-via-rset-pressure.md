---
title: "Hot Mutable Cache Driving G1 Pause Growth via RSet Pressure"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../handbook/jvm/g1-remembered-sets-and-write-barriers.md
source: handbook/jvm/g1-remembered-sets-and-write-barriers.md#production-scenarios
---

# Hot Mutable Cache Driving G1 Pause Growth via RSet Pressure

## Context

A hot, frequently mutated cache is backed by a single large array or map, holding references to constantly churning young objects.

## Symptoms

The service exhibits measurably longer G1 pauses than the live-data size alone would predict, while overall heap occupancy remains essentially unchanged.

## Impact

GC pause times degrade user-facing latency without any accompanying growth in heap usage, making the cause non-obvious to a team investigating via heap-size trends alone.

## Initial Hypotheses

- A general heap-sizing problem — the natural first hypothesis for any GC-pause-time increase.
- Write-barrier and remembered-set (RSet) pressure from a specific hot mutable structure, unrelated to overall heap occupancy — correct.

## Evidence

`gc+phases=debug` logging shows elevated `Merge Heap Roots` phase duration and `Scanned Cards` sums for the region backing the cache, disproportionate to overall heap growth, which stays flat.

## Investigation Timeline

1. **G1 pause times observed growing**, with heap occupancy metrics showing no corresponding increase.
2. **General heap-sizing explanation considered first**, the default assumption for any GC-pause-time trend.
3. **`gc+phases=debug` logging enabled and reviewed**, isolating pause-time contribution by phase rather than treating pause growth as a single undifferentiated number.
4. **`Merge Heap Roots` phase and `Scanned Cards` sums correlated against the specific cache-backing region**, confirming disproportionate RSet-related cost concentrated there.

## Root Cause

Every cache update is a cross-region write — a reference from the cache's region into a constantly churning young object elsewhere in the heap. Remembered-set merge and scan work for that one hot region grows with mutation rate, not with the cache's own size, so a small, frequently mutated cache can drive disproportionate GC pause cost independent of overall heap occupancy.

## Immediate Mitigation

None available without a code or configuration change — the RSet pressure is a direct, structural consequence of the cache's mutation pattern, not a runtime-tunable setting on its own.

## Permanent Fix

Reduce cross-region write frequency for the hot cache — for example, by co-locating the cache and its frequently referenced young objects where feasible, reducing the mutation rate of cross-region references, or restructuring the cache to hold values rather than references to independently churning objects, where the access pattern allows it.

## Alternatives Considered

Simply increasing heap size or GC threads to absorb the pause-time cost. Rejected as treating the symptom — the RSet/write-barrier cost scales with mutation rate at the specific hot region, not with overall heap size, so more heap doesn't address the actual mechanism.

## Trade-offs

Restructuring the cache to reduce cross-region reference churn may require accepting a different, less convenient data-access pattern for the cache's consumers. Accepted where the GC-pause cost is significant enough to justify the redesign.

## Prevention

A team seeing "GC pauses got worse but our heap occupancy didn't" should suspect write-barrier/RSet pressure from a specific hot mutable structure before suspecting a general sizing problem, and should default to `gc+phases=debug` logging to confirm it directly.

## Monitoring and Alerts

- `gc+phases=debug` logging enabled as a standing diagnostic capability, not something turned on only after a pause-growth investigation stalls — comparing `Merge Heap Roots` phase duration and `Scanned Cards` sums across pauses against overall heap growth is the direct, mechanical way to distinguish this cause from general sizing.
- Per-region RSet size and merge/scan cost tracked for any known hot, frequently mutated structure (a cache, a shared counter map), surfacing this specific failure mode proactively for structures known to have this shape, rather than only diagnosing it reactively after pause times degrade.

## Interview Story

This maps to "GC pauses got worse but heap occupancy didn't change, why." Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** G1 pause times grew measurably while overall heap occupancy stayed flat, defeating the natural "just size the heap differently" response.
- **Task:** find a GC-pause-growth cause that isn't explained by heap size at all.
- **Action:** move past the general-sizing hypothesis; enable phase-level GC logging to isolate which part of the pause grew; correlate the growth against a specific hot, mutable cache structure rather than the heap as a whole.
- **Result:** identified cross-region write frequency from the cache's mutation pattern as the actual driver of RSet merge/scan cost, and restructured the cache's reference pattern to reduce it.

## Staff-Level Discussion

The key diagnostic move in this incident is refusing to conflate "GC pause time" with "heap occupancy" as if they were the same signal — G1's remembered-set mechanism means pause cost is driven by cross-region reference *mutation rate*, a property distinct from how much live data exists, and a structure can be small in absolute size while still being expensive specifically because it's mutated frequently and referenced across regions. This is a specific, valuable piece of G1-internals knowledge that separates engineers who can only reason about GC via heap-size graphs from those who understand the mechanism well enough to look at phase-level logs and localize the actual cost driver. A Staff engineer designing any frequently mutated, long-lived structure holding references to short-lived objects (a cache, a registry, a connection pool's bookkeeping structures) should consider its RSet/write-barrier cost as a distinct design dimension from its memory footprint, since the two can diverge significantly.

## Related Handbook Chapters

- [G1 Remembered Sets and Write Barriers](../handbook/jvm/g1-remembered-sets-and-write-barriers.md) — canonical RSet, write-barrier, and cross-region-write cost mechanics used here.
