---
title: "Heap Overhead From Millions of AtomicLong Counters"
document_type: production-cookbook-entry
domain: concurrency
status: draft
last_updated: 2026-09-01
related_handbook:
  - ../handbook/concurrency/varhandles-and-unsafe.md
source: handbook/concurrency/varhandles-and-unsafe.md#production-scenarios
---

# Heap Overhead From Millions of AtomicLong Counters

## Context

A metrics library instantiated millions of small per-key counters, each backed by its own `AtomicLong` field, to track high-cardinality metric data.

## Symptoms

During a capacity review, the library was found to be spending a measurable fraction of its heap on the wrapper overhead of `AtomicLong` objects rather than the actual counter values themselves.

## Impact

Real heap capacity was consumed by object overhead rather than useful data, at a scale (millions of instances) where the wrapper cost was no longer negligible against the library's actual operating budget.

## Initial Hypotheses

- The counters needed to be sharded or aggregated more aggressively to reduce their count — this was the first mitigation direction considered.

## Evidence

Profiling showed each `AtomicLong` instance's own object header and padding cost meaningfully more than the 8 bytes of actual counter data it held. At millions of instances, that per-object overhead summed to a real, non-trivial amount of heap.

## Investigation Timeline

1. **Capacity review flagged heap usage** attributable to the metrics library's counter storage.
2. **Sharding/aggregation considered first**, as a way to reduce the total counter count rather than each counter's own footprint.
3. **Profiling performed on the actual counter objects**, revealing that per-instance object-header and padding overhead exceeded the 8 bytes of real data each counter held.
4. **Root cause identified as the wrapper object itself**, not the counter count or access pattern — the counters didn't need a separate heap object at all.

## Root Cause

Each `AtomicLong` counter required its own heap-allocated object, whose header and padding overhead exceeded the actual 8 bytes of counter data it held. At millions of instances, this per-object overhead — not the counting logic itself — was the source of the measured heap cost.

## Immediate Mitigation

None needed; this was identified proactively during a capacity review, not discovered as an incident.

## Permanent Fix

Replaced `AtomicLong` fields with plain `long` fields on the already-existing per-key object, accessed via a shared, static `VarHandle` per field — providing the identical atomic increment guarantee without a dedicated wrapper object's overhead.

## Alternatives Considered

Sharding or aggregating counters more aggressively to reduce their total count. Rejected once profiling identified the wrapper object itself, rather than the counter count, as the actual cost driver — reducing counter count would have reduced metric granularity without addressing the per-instance overhead directly.

## Trade-offs

Call sites became slightly more verbose (`HANDLE.getAndAdd(this, 1)` instead of `counter.incrementAndGet()`). This was accepted against a real, measured reduction in per-instance memory footprint at the library's actual scale.

## Prevention

Documented the `VarHandle`-backed plain-field pattern for future high-cardinality counter needs elsewhere in the codebase.

## Monitoring and Alerts

- Per-object heap overhead tracked as a distinct capacity-review dimension for any high-cardinality data structure, separate from raw data volume, since the two can diverge sharply at scale.
- A documented pattern reference so future high-cardinality counter designs default to the low-overhead approach rather than rediscovering the trade-off independently.

## Interview Story

This maps to a "how would you reduce memory overhead for a huge number of small counters" question. Present it as a representative scenario unless you have lived through an equivalent case:

- **Situation:** a metrics library's millions of per-key counters were found, during a capacity review, to spend a meaningful fraction of heap on wrapper-object overhead rather than actual data.
- **Task:** reduce that overhead without giving up atomic increment semantics.
- **Action:** considered sharding first, but profiling pointed to the `AtomicLong` wrapper object itself as the cost driver; replaced it with a plain `long` field accessed via a shared `VarHandle`.
- **Result:** eliminated the dedicated wrapper object per counter while keeping the identical atomicity guarantee, at the cost of slightly more verbose call sites.

## Staff-Level Discussion

`VarHandle`'s real advantage over `AtomicXxx` wrapper types is identical atomicity guarantees without paying for a dedicated wrapper object — a distinction that only matters at genuine scale but is a real, measurable win when it does. This is a useful example of a broader capacity-planning discipline: at small scale, "how many bytes does this object cost" is not worth optimizing, and doing so prematurely would be wasted engineering effort; at millions of instances, the same question becomes a real, budget-relevant capacity decision. The Staff-level judgment is recognizing which regime a given system is actually in — here, a capacity review surfaced the crossover point directly through profiling, rather than the team guessing at it in advance or optimizing reflexively before the scale justified it.

## Related Handbook Chapters

- [VarHandles, Unsafe, and Their Replacement](../handbook/concurrency/varhandles-and-unsafe.md) — canonical VarHandle-vs-AtomicLong overhead comparison used here.
