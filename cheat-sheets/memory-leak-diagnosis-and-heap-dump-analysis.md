---
title: "Cheat Sheet: Memory Leak Diagnosis and Heap Dump Analysis"
slug: memory-leak-diagnosis-and-heap-dump-analysis
document_type: cheat-sheet
domain: jvm
topic_id: T-307
canonical: ../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md
last_updated: 2026-09-12
---

# Memory Leak Diagnosis and Heap Dump Analysis

**Canonical chapter:** [`syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md`](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)

## Mental Model

A Java memory leak is not memory that disappears — every byte is still perfectly accounted for by the garbage collector. It's memory that's accidentally still invited to the party: some object, usually meant to be scoped to a single request or session, ends up with a standing invitation (a reference held by something that outlives it) it was never supposed to have. The GC faithfully keeps every invited guest alive forever, because from its perspective that's correct behavior. Diagnosing a leak is finding out which long-lived object is holding the guest list that should have been cleared.

## Decision Table

| Step | Command | What it tells you |
|---|---|---|
| 1. Find the growing class | `jmap -histo:live <pid>` | Live instance counts, forced-GC-accurate |
| 2. Rule out a warming cache | Repeat step 1, 3+ times, spaced out | Real leaks never plateau |
| 3. Find the reference chain | `jcmd <pid> GC.heap_dump <file>` + MAT/VisualVM | GC-roots path to the leaked instances |
| Production safety net | `-XX:+HeapDumpOnOutOfMemoryError` | Captures the dump at the moment of OOM, no live repro needed |

## Decision Framework

Start with `jmap -histo:live` sampling — cheap enough to run a few times spaced minutes-to-hours apart, and it immediately tells you which class is growing. Only escalate to a full heap dump once the histogram has identified a specific suspect class, since the dump answers "which reference chain," a question the histogram alone can't. Reach for a heap dump proactively only when you can afford the pause it causes — a low-traffic window, or `-XX:+HeapDumpOnOutOfMemoryError` triggering it automatically, avoids taking one blind during peak load.

## Common Pitfalls

- Treating "memory keeps growing" as sufficient evidence of a leak without distinguishing it from a warming cache or genuinely increased working set.
- Reaching for more heap as a first response — doesn't fix a leak's growth rate, only delays the OOM.
- Taking a heap dump before running a live-object histogram, wasting the dump's size and pause cost on a search a cheaper histogram sample would have narrowed first.
- Not using `:live` on `jmap -histo`, producing a count polluted by not-yet-collected garbage that looks identical to a real leak on a single sample.

## Interview Answer Skeleton

30 seconds: a Java leak is an accidental long-lived reference, not vanished memory — diagnose by finding the growing class, then its reference chain. 2 minutes: add the histogram-first, dump-second workflow and the plateau test for ruling out a warming cache. Deep dive: walk an incident from `jmap -histo:live` samples showing no plateau, to a targeted heap dump, to MAT identifying the GC-roots chain holding the leaked objects.

## Production Warning Signs

- Steadily climbing heap-usage graph across multiple GC cycles that never returns to baseline — sample with `jmap -histo:live` before dumping.
- A single-sample histogram used as leak evidence — always resample 3+ times spaced out before concluding it's a leak vs. a warming cache.
- No `-XX:+HeapDumpOnOutOfMemoryError` configured on a production service — no automatic safety net if an OOM does occur.

## Related

- [JVM Memory Layout and Runtime Regions](../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md)
- [GC Fundamentals and Log Analysis](../syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md)
- [Profiling, JFR, and Flame Graphs](../syllabus/16-performance-jvm/profiling-jfr-and-flame-graphs.md)
