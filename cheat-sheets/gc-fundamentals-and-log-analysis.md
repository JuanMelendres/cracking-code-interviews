---
title: "Cheat Sheet: GC Fundamentals and Log Analysis"
slug: gc-fundamentals-and-log-analysis
document_type: cheat-sheet
domain: jvm
topic_id: T-306
canonical: ../handbook/jvm/gc-fundamentals-and-log-analysis.md
last_updated: 2026-08-04
---

# GC Fundamentals and Log Analysis

**Canonical chapter:** [`handbook/jvm/gc-fundamentals-and-log-analysis.md`](../handbook/jvm/gc-fundamentals-and-log-analysis.md)

## Core Mental Model

A GC log is a time series of "how full is the heap, and how long did it take to clean up" — reading it is arithmetic on a handful of numbers, not algorithm trivia. Every pause line reports occupancy before, occupancy after, heap capacity, and pause duration. Trends across successive lines — not any single line — separate normal steady-state churn from a genuine problem.

## Essential Definitions

- **G1** — the JVM's default collector since JDK 9 (JEP 248); divides the heap into fixed-size regions rather than fixed contiguous generations, and collects the regions with the most garbage first.
- **Young collection** — reclaims only young-gen regions; normally sub-millisecond to a few milliseconds.
- **Mixed collection** — additionally reclaims some old-gen regions, triggered once old-gen occupancy crosses `InitiatingHeapOccupancyPercent`; takes longer because it scans more live data.
- **Full collection** — G1's fallback (ideally rare): stops the world and compacts the entire heap; signals G1 couldn't keep up incrementally.
- **Humongous allocations** — objects ≥50% of a region's size; bypass the normal young-gen path entirely, go straight into dedicated regions, collected less efficiently.
- **Regions** — typically 1-32MB each, sized automatically; any region can serve as young, old, or humongous at different points.

## Decision Table

| Symptom | Likely cause | Debugging step |
|---|---|---|
| Pause frequency rising, duration stable | Allocation rate climbing | Correlate with traffic/feature rollout — not necessarily a bug |
| Post-GC occupancy climbing across young collections | Objects surviving longer than expected | Heap dump comparison over time — growing cache vs. genuine leak |
| "Concurrent Start" + mixed sequence recurring frequently | Old-gen occupancy repeatedly crossing threshold | Check old-gen sizing and for a leak |
| Occasional very large single-pause spikes | Humongous allocations, or container CPU throttling | Check allocation sizes vs. region size; check cgroup CPU limits |

| Tuning lever | Benefit | Cost |
|---|---|---|
| Larger heap | Fewer collections needed | Each collection takes longer; more memory reserved whether used or not |
| Smaller young generation | More frequent, shorter pauses | More promotion pressure if objects don't die young enough |
| Larger young generation | Objects have longer to die before promotion | Each young collection takes longer to scan |
| Pause-time goal (`-XX:MaxGCPauseMillis`) | G1 auto-tunes sizing toward this target | Too aggressive a goal forces smaller, more frequent collections — sometimes worse total throughput |

## Key Numbers (real, captured log — `-Xmx64m`, `AllocationStormDemo.java`, ~4.9GB of short-lived 1KB objects into a 64MB heap)

```
GC(0): 24M->1M(64M)  0.329ms
GC(1): 38M->1M(64M)  0.302ms
GC(2): 38M->1M(64M)  0.186ms
GC(3): 38M->6M(64M)  0.605ms
```
Four real young-generation collections in under 10ms of wall-clock time, each sub-millisecond.

## Common Pitfalls

- Treating "tuning" as synonymous with "increase heap size"
- Not distinguishing young-only pauses from mixed/full collections when reading a log — different causes, different fixes
- Ignoring the occupancy-trend signal (post-GC size climbing across successive collections) that distinguishes normal churn from a genuine leak or undersized old generation

## Interview Answer Skeleton

**30-sec:** GC log analysis means reading pause type, before/after occupancy, and heap capacity from a real log line, then tracking the trend across collections — not reciting collector algorithm names. Rising post-GC occupancy signals objects surviving longer than expected; "increase the heap" is frequently the wrong fix.

**2-min:** Add why it exists (G1 regions, most-garbage-first) + young/mixed/humongous distinction + the four real sub-ms pauses example + the trade-off that a larger heap means fewer but longer collections.

**Whiteboard:** Draw object allocated → young region fills → young collection → survives enough collections? → promoted to old → old-gen threshold crossed? → mixed collection. Annotate "survives enough collections" as: "this is where a leak shows up as a trend, not a single pause."

**Staff-level framing:** GC log analysis is one of the clearest instances of "demonstrable skill beats recitable fact" — the same skill as reading a slow-query `EXPLAIN` plan or a flame graph. GC behavior is workload-specific, so generic advice ("use G1," "increase heap") is often wrong.

## Production Warning Signs

- **Real incident pattern:** p99 latency degrades gradually over days with no deploy or traffic change; young pause duration is unchanged but frequency rises, and mixed collections (rare historically) now recur every few minutes with rising duration each time. Two heap dumps 48 hours apart show the same cache-entry class growing in retained count with no eviction.
- Root cause is nearly always an unbounded/incorrectly-keyed in-memory cache, not GC tuning — "a genuinely unbounded cache eventually exhausts any heap size, just more slowly." Bound every long-lived cache at code review.
- Sizing a container's JVM heap against host memory instead of the cgroup limit causes OOM-kills that present as mysterious crashes, not GC problems.

## Related

- `handbook/concurrency/java-memory-model-and-volatile.md`
- `handbook/jvm/memory-leak-diagnosis-and-heap-dump-analysis.md`
- `handbook/jvm/jvm-memory-layout-and-runtime-regions.md`
