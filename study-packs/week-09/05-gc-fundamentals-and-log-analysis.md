---
title: "T-303 / T-306 · GC Fundamentals & Log Analysis"
topic_id: T-306
domain: JVM
tier: Advanced
iwi: 7.35
prerequisites: []
unlocks: []
week: 9
last_reviewed: 2026-07-29
---

# T-303 / T-306 · GC Fundamentals & Log Analysis

**IWI 7.35 (T-306) / 6.90 (T-303) · Advanced / Core tier · centre of gravity of the JVM chapter, per the blueprint: "the valuable framing is not 'name the GC algorithms' but 'here is a latency graph and a GC log — what happened?'"**

**Verification note:** the GC log excerpts in §3 are real, captured output from `practice/java/week-09/gc/src/AllocationStormDemo.java`, run with `-Xlog:gc*` against a real, constrained heap — not synthesized or described from documentation.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [A real GC log, read](#3-a-real-gc-log-read)
4. [Reading the log: what each field means](#4-reading-the-log-what-each-field-means)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

Garbage collection reclaims memory occupied by objects no longer reachable from any live root (thread stacks, static fields). G1 (the JVM's default collector since JDK 9) divides the heap into fixed-size **regions** rather than fixed contiguous generations, and collects the regions with the most garbage first ("Garbage First") — mostly young-generation regions holding short-lived objects, occasionally including old-generation regions in a mixed collection.

## 2. Why it exists

Manual memory management is a major source of production defects (use-after-free, double-free, leaks) — GC exists to remove that entire class of bugs at the cost of pause time and CPU spent collecting. The skill this topic actually rewards, per the blueprint, is **diagnosis from an artifact** (a real GC log or latency graph), not reciting collector names — this chapter is built around that, not around it.

## 3. A real GC log, read

**Real captured log**, `AllocationStormDemo.java` run with `-Xmx64m -Xlog:gc*:file=gc.log:time,level,tags` — allocating ~4.9GB of short-lived 1KB objects plus a smaller stream of retained 8KB objects into a deliberately small, fixed 64MB heap:

```
[2026-07-29T21:59:05.263-0600][info][gc,start    ] GC(0) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.264-0600][info][gc          ] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 24M->1M(64M) 0.329ms
[2026-07-29T21:59:05.267-0600][info][gc,start    ] GC(1) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.267-0600][info][gc          ] GC(1) Pause Young (Normal) (G1 Evacuation Pause) 38M->1M(64M) 0.302ms
[2026-07-29T21:59:05.270-0600][info][gc,start    ] GC(2) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.270-0600][info][gc          ] GC(2) Pause Young (Normal) (G1 Evacuation Pause) 38M->1M(64M) 0.186ms
[2026-07-29T21:59:05.272-0600][info][gc,start    ] GC(3) Pause Young (Normal) (G1 Evacuation Pause)
[2026-07-29T21:59:05.273-0600][info][gc          ] GC(3) Pause Young (Normal) (G1 Evacuation Pause) 38M->6M(64M) 0.605ms
```

Four real young-generation collections in under 10 milliseconds of wall-clock time, each sub-millisecond. Reading GC(3) specifically: heap occupancy went from 38MB to 6MB out of a 64MB max — a larger post-collection residency than GC(0)-(2)'s 1MB, meaning more objects survived this collection than the previous ones (consistent with the demo's periodic retained 8KB allocations accumulating toward the young-gen survivor space filling up and objects starting to age toward eventual promotion).

## 4. Reading the log: what each field means

```
GC(3) Pause Young (Normal) (G1 Evacuation Pause) 38M->6M(64M) 0.605ms
 │      │      │        │            │             │    │    │      │
 │      │      │        │            │             │    │    │      └─ pause duration
 │      │      │        │            │             │    │    └─ heap capacity at time of GC
 │      │      │        │            │             │    └─ heap occupancy AFTER
 │      │      │        │            │             └─ heap occupancy BEFORE
 │      │      │        │            └─ G1's internal name for this collection type
 │      │      │        └─ "Normal" vs "Concurrent Start" (the latter begins a mixed-collection cycle)
 │      │      └─ collector generation being collected (Young here; Mixed collections touch old-gen regions too)
 │      └─ this JVM's Nth collection since start (0-indexed, monotonically increasing)
 └─ GC event identifier
```

**What to look for when diagnosing a real production log:**

- **Pause frequency increasing over time** with roughly stable pause duration → allocation rate is climbing (more garbage created per unit time), not necessarily a leak yet.
- **Post-GC occupancy trending upward** across successive young collections (as GC(3) above shows relative to GC(0)-(2)) → objects are surviving longer than expected, heading toward promotion — worth checking whether that's intentional (a growing cache) or a leak.
- **A "Concurrent Start" pause followed by a mixed-collection sequence** → G1 has decided old-gen occupancy crossed its threshold (`InitiatingHeapOccupancyPercent`) and is running a full mixed cycle — frequent occurrences of this pattern usually mean the old generation is undersized or genuinely leaking.
- **Pause duration climbing into the hundreds of ms or seconds** → usually means either the heap is too small for the live-object working set (G1 working harder per collection) or a single region has an unusually large live-object density (see "humongous allocations" below).

**Humongous allocations** (objects ≥ 50% of a G1 region size) bypass the normal young-gen allocation path entirely and go straight into dedicated regions, collected less efficiently — a stream of humongous allocations (e.g., repeatedly allocating large byte arrays for buffering) is a common, specific, diagnosable cause of unexpectedly bad GC behavior that "increase the heap" doesn't fix, because the problem is allocation *pattern*, not total volume.

## 5. Trade-offs

| Tuning lever | Benefit | Cost |
|---|---|---|
| Larger heap | Fewer collections needed | Each collection (especially a full/mixed one) takes longer, since more live data to scan; also more memory reserved whether used or not |
| Smaller young generation | More frequent, but shorter, pauses | More promotion pressure if objects don't die young enough to be reclaimed before promotion |
| Larger young generation | Objects have longer to die before promotion, fewer objects promoted | Each young collection takes longer to scan; less memory available for old gen |
| Pause-time goal (`-XX:MaxGCPauseMillis`) | G1 tunes region/generation sizing automatically toward this target | A goal set too aggressively low forces more frequent, smaller collections — sometimes worse total throughput |

## 6. Interview questions

### Q1. Pauses hit 4 seconds. Diagnose from this log.

- **Expected answer:** look for whether it's a young-only pause (unusual for 4s — suggests a very large young generation or a memory-bandwidth-starved container) or a mixed/full collection (expected to take longer, scans old gen); check post-GC occupancy trend across preceding collections for a promotion/leak pattern; check for humongous allocations specifically.
- **Common mistakes:** jumping straight to "increase the heap" without reading what the log actually shows first.
- **Follow-up questions:** "The heap is already generously sized. What else could it be?"
- **Senior-level expectations:** distinguishes young vs. mixed/full pauses and reasons about promotion trend from occupancy numbers.
- **Staff-level expectations:** names humongous allocations and container-memory-bandwidth/CPU-throttling as specific, non-obvious causes that "just add more heap" doesn't fix, and can state the diagnostic signal for each.

### Q2. Tuning means increasing heap size — true or false?

- **Expected answer:** False, and it's the most common misconception per the blueprint. Heap size is one lever among several (generation sizing, pause-time goals, allocation-pattern fixes like avoiding humongous allocations); it can even make things worse (longer per-collection scan time for a bigger live set).
- **Common mistakes:** stopping at "give it more RAM" without a log-driven diagnosis.
- **Follow-up questions:** "When WOULD increasing heap size actually be the right fix?"
- **Senior-level expectations:** states heap size is only correct when the log shows the live-object working set genuinely doesn't fit, not just "GC is happening."
- **Staff-level expectations:** connects to T-312 (container ergonomics, named explicitly in the blueprint as "quietly important and widely missed") — a JVM sized against host memory rather than the container's cgroup limit is a distinct, common production failure that looks like a GC problem but isn't one.

## 7. Common mistakes

- Treating "tuning" as synonymous with "increase heap size."
- Not distinguishing young-only pauses from mixed/full collections when reading a log — they have very different causes and fixes.
- Ignoring the occupancy-trend signal (post-GC heap size climbing across successive collections) that distinguishes normal steady-state churn from a genuine leak or undersized old generation.

## 8. Staff-level discussion

GC log analysis is one of the clearest instances in this whole program of "demonstrable skill beats recitable fact" (the blueprint's own framing for this chapter) — an interviewer handing over a real log or latency graph is testing whether a candidate can extract a diagnosis from an artifact under time pressure, the same skill as reading a slow-query `EXPLAIN` plan or a flame graph. A Staff-level engineer treats every performance-tuning conversation as starting from an artifact (a log, a profile, a metric) rather than from intuition or a general "best practices" checklist, because GC behavior in particular is workload-specific enough that generic advice ("use G1," "increase heap") is frequently wrong for the actual allocation pattern in front of them.

## 9. Summary

A real, captured GC log (§3) shows four sub-millisecond young-generation pauses with a rising post-collection occupancy trend — exactly the kind of artifact this topic trains diagnosis on. Reading a GC log means extracting pause type, before/after occupancy, and trend across collections, not reciting collector algorithm names; "increase the heap" is frequently the wrong fix, and the log itself usually shows why.

## 10. Key Takeaways

- G1 collects region-by-region, prioritizing the most garbage first; young pauses and mixed (young+old) pauses have different causes.
- Rising post-GC occupancy across successive young collections signals objects surviving longer than expected — check for a leak or undersized old gen.
- Humongous allocations (≥50% of region size) bypass normal handling and are a specific, diagnosable cause of bad GC behavior that more heap doesn't fix.
- "Tuning means increasing heap size" is the most common misconception per the blueprint — heap size is one lever among several, and not always the right one.

## 11. Cheat Sheet

| Log signal | Likely meaning |
|---|---|
| Frequent young pauses, stable duration | Rising allocation rate |
| Post-GC occupancy climbing across young collections | Objects surviving longer — check for leak or growing cache |
| "Concurrent Start" + mixed-collection sequence | Old-gen occupancy crossed threshold — check old-gen sizing/leak |
| Occasional very large single-pause spikes | Check for humongous allocations or container CPU throttling |

## 12. Flashcards

1. **Q: What's the most common misconception about GC tuning, per this week's material?** A: That tuning means increasing heap size — it's one lever among several and isn't always correct.
2. **Q: What does a rising post-GC occupancy trend across successive young collections suggest?** A: Objects are surviving longer than expected, heading toward promotion — check for a leak or an intentionally growing cache.
3. **Q: What is a "humongous allocation" in G1, and why does more heap not fix problems it causes?** A: An object ≥50% of a region size, handled via dedicated regions outside normal young-gen allocation — the problem is allocation pattern, not total heap volume.

(Full week-level deck: `06-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-09/gc/src/AllocationStormDemo.java`, run with `java -Xmx64m "-Xlog:gc*:file=gc.log:time,level,tags" -cp out AllocationStormDemo`.
2. Lower `-Xmx` further (e.g., to 32M) and observe how pause frequency and the occupancy trend change.
3. Increase the demo's retained-object ratio and try to force a "Concurrent Start" / mixed-collection log line to appear — what allocation pattern was needed to trigger it?

## 14. Additional Reading

- [Oracle — Garbage-First (G1) Garbage Collector tuning guide](https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html)

## 15. Official References

- [JEP 248: Make G1 the Default Garbage Collector](https://openjdk.org/jeps/248)
