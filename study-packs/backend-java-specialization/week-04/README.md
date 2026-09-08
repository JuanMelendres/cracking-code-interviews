---
title: "Backend Java Specialization, Week 4 — JVM Internals"
document_type: study-pack
week: 4
track: backend-java-specialization
status: draft
estimated_hours: 12
---

# Week 4 — JVM Internals

## Weekly Outcome

By the end of this week you can describe object layout and header overhead, explain GC roots and reachability, read a GC log, contrast G1/ZGC/Shenandoah's trade-offs, explain escape analysis and JIT tiered compilation with deoptimization, diagnose a heap-based memory leak from a heap dump, and reason correctly about JVM flags under container CPU/memory limits.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/backend-java-specialization.md`](../../../syllabus/00-overview/learning-paths/backend-java-specialization.md) places `jvm-internals` last within Java specifically because it is "the layer underneath everything above it" — Week 1's object model, Week 2's collections, and Week 3's concurrency primitives are all implemented in terms of the heap layout, garbage collection, and JIT behavior this week covers directly.

## Prerequisites

Weeks 1–3 complete. T-401/T-402 (Java Memory Model) from Week 3 is a direct prerequisite for this week's GC and safepoint chapters — both describe consequences of the same underlying memory-visibility model from different angles.

## Schedule

| Day | Focus |
|---|---|
| Mon | Object Layout, Headers, and Compressed Oops (T-302); GC Roots, Reachability, and Reference Strength (T-303) |
| Tue | GC Fundamentals and Log Analysis |
| Wed | ZGC and Shenandoah (T-305); G1 Internals: Remembered Sets and Write Barriers |
| Thu | Escape Analysis and Scalar Replacement (T-309); JIT: Tiered Compilation, Inlining, and Deoptimization |
| Fri | Safepoints and Stop-the-World Mechanics (T-310); Native Memory, Direct Buffers, and Off-Heap (T-311) |
| Sat | JVM Memory Layout and Runtime Regions; JVM Flags and Container Ergonomics |
| Sun | Memory Leak Diagnosis and Heap Dump Analysis; review checklist below |

## Required Reading

The full `jvm-internals` subdomain, per [`syllabus/02-java/INDEX.md`](../../../syllabus/02-java/INDEX.md) (the exhaustive, canonical source).

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | T-302 — Object Layout, Headers, and Compressed Oops | [`object-layout-headers-and-compressed-oops.md`](../../../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md) |
| 2 | T-303 — GC Roots, Reachability, and Reference Strength | [`gc-roots-reachability-and-reference-strength.md`](../../../syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md) |
| 3 | GC Fundamentals and Log Analysis | [`gc-fundamentals-and-log-analysis.md`](../../../syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md) |
| 4 | T-305 — ZGC and Shenandoah: Concurrent Collection | [`zgc-and-shenandoah-concurrent-collection.md`](../../../syllabus/02-java/jvm-internals/zgc-and-shenandoah-concurrent-collection.md) |
| 5 | T-309 — Escape Analysis and Scalar Replacement | [`escape-analysis-and-scalar-replacement.md`](../../../syllabus/02-java/jvm-internals/escape-analysis-and-scalar-replacement.md) |
| 6 | T-310 — Safepoints and Stop-the-World Mechanics | [`safepoints-and-stop-the-world-mechanics.md`](../../../syllabus/02-java/jvm-internals/safepoints-and-stop-the-world-mechanics.md) |
| 7 | T-311 — Native Memory, Direct Buffers, and Off-Heap | [`native-memory-direct-buffers-and-off-heap.md`](../../../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md) |
| 8 | G1 Internals: Remembered Sets and Write Barriers | [`g1-remembered-sets-and-write-barriers.md`](../../../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md) |
| 9 | JIT: Tiered Compilation, Inlining, and Deoptimization | [`jit-tiered-compilation-and-deoptimization.md`](../../../syllabus/02-java/jvm-internals/jit-tiered-compilation-and-deoptimization.md) |
| 10 | JVM Flags and Container Ergonomics | [`jvm-flags-and-container-ergonomics.md`](../../../syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md) |
| 11 | JVM Memory Layout and Runtime Regions | [`jvm-memory-layout-and-runtime-regions.md`](../../../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md) |
| 12 | Memory Leak Diagnosis and Heap Dump Analysis | [`memory-leak-diagnosis-and-heap-dump-analysis.md`](../../../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md) |

## Hands-On Exercises

Real, compiled, executed demos exist for all 12 chapters:

- [`practice/java/week-19/object-layout/`](../../../practice/java/week-19/object-layout/) (T-302)
- [`practice/java/week-19/gc-roots-reachability/`](../../../practice/java/week-19/gc-roots-reachability/) (T-303)
- [`practice/java/week-09/gc/`](../../../practice/java/week-09/gc/) (GC Fundamentals — `AllocationStormDemo.java`, real captured `-Xlog:gc*` output)
- [`practice/java/week-19/zgc-vs-g1/`](../../../practice/java/week-19/zgc-vs-g1/) (T-305)
- [`practice/java/week-19/escape-analysis/`](../../../practice/java/week-19/escape-analysis/) (T-309)
- [`practice/java/week-19/safepoints/`](../../../practice/java/week-19/safepoints/) (T-310)
- [`practice/java/week-19/native-memory/`](../../../practice/java/week-19/native-memory/) (T-311)
- [`practice/java/week-16/g1-remembered-sets/`](../../../practice/java/week-16/g1-remembered-sets/) (G1 Internals — real measured pause totals, `-Xmx128m`)
- [`practice/java/week-16/jit-compilation/`](../../../practice/java/week-16/jit-compilation/) (JIT — `WarmupSpeedupDemo.java`, `DeoptDemo.java`)
- [`practice/java/week-16/container-ergonomics/`](../../../practice/java/week-16/container-ergonomics/) (JVM Flags — `ContainerErgonomicsDemo.java`, real `--cpus`/`--memory` traces)
- [`practice/java/week-16/memory-layout/`](../../../practice/java/week-16/memory-layout/) (JVM Memory Layout — `MetaspaceExhaustionDemo.java`, `StackDepthDemo.java`)
- [`practice/java/week-16/memory-leak-diagnosis/`](../../../practice/java/week-16/memory-leak-diagnosis/) (Memory Leak Diagnosis — `LeakyListenerDemo.java`)

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "why does G1 use remembered sets instead of scanning the whole old generation on every young collection?" and "what specifically does escape analysis let the JIT skip doing?"

## Coding Problems

None — this pack is domain-depth reading, not coding-pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a heap dump description (a growing `Subject` object retaining ever more `Session` listener registrations), state the diagnosis and the fix before checking Memory Leak Diagnosis's own worked example.

## Review Checklist

- [ ] Completed all 12 chapters' own L1–L4 Mastery Checklists.
- [ ] Reproduced at least 7 of the 12 real demos listed above.
- [ ] Can explain, unprompted, why metaspace exhaustion does not show up as heap growth in a profiler that only watches heap usage.

## Completion Criteria

- [ ] Can read a real GC log excerpt and identify pause type, cause, and rough duration.
- [ ] Can explain what changes about container CPU/memory ergonomics detection between JVM versions and why `-XX:+PrintFlagsFinal` matters for verifying it.
- [ ] Can state the difference between ZGC/Shenandoah's concurrent approach and G1's mostly-stop-the-world young collections.

## Retrospective

Note which GC algorithm's trade-offs you would confidently recommend for a stated latency-sensitive service versus a stated throughput-sensitive batch job — this decision-framing question recurs at Senior/Staff depth across nearly every JVM-tuning interview.

## Next Week

[Week 5 — Spring, End to End](../week-05/README.md).
