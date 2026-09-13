---
title: "finalize()-Based Native Memory Cleanup Leaking Under GC Pressure"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md
source: syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md#production-scenarios
---

# finalize()-Based Native Memory Cleanup Leaking Under GC Pressure

## Context

A custom off-heap buffer wrapper relies on `finalize()` to release native memory when an instance becomes garbage.

## Symptoms

A production incident traces a native-memory leak to finalization simply not running promptly enough under GC pressure.

## Impact

Native memory grows unbounded relative to heap-visible metrics, eventually risking an out-of-memory condition invisible to standard heap monitoring.

## Initial Hypotheses

- A native-library bug leaking memory directly — checked, the allocation/release code path is correct when finalization actually runs.
- A heap-sizing problem — checked, heap metrics look normal; the leak is native, not heap memory.
- `finalize()`'s lack of execution-timing guarantees under GC pressure is delaying or skipping cleanup — correct.

## Evidence

Native memory usage climbs independent of heap occupancy, and correlates with periods where finalizer-thread throughput is low relative to the rate of finalizable-object creation.

## Investigation Timeline

1. Native memory growth observed with heap metrics reporting normal.
2. Native-library-bug and heap-sizing hypotheses ruled out.
3. Finalizer-thread activity checked against object-creation rate, showing a real backlog under GC pressure.

## Root Cause

`finalize()` (deprecated since JDK 9, removed as a routine recommendation) has real, well-documented problems: no guaranteed timing, no guaranteed execution at all in some shutdown scenarios, and objects can be "resurrected" during finalization, complicating reachability further.

## Immediate Mitigation

Manually trigger additional native-resource release for known-idle instances, or restart the affected process to reclaim native memory while a proper fix ships.

## Permanent Fix

Replace `finalize()`-based cleanup with the phantom-reference-plus-`ReferenceQueue` pattern (or the higher-level `java.lang.ref.Cleaner` API built on it) — deterministic notification after the collector has determined the object unreachable, without `finalize()`'s timing and resurrection hazards.

## Alternatives Considered

Try-with-resources / explicit `close()` as the primary cleanup path — a real, preferable fix wherever the caller can be relied on to close deterministically; `Cleaner`/phantom references remain necessary as a safety net for callers who forget to close explicitly.

## Trade-offs

`Cleaner`/phantom-reference cleanup is still GC-timing-dependent for callers who never call `close()` explicitly — accepted as a safety net, not a substitute for deterministic closing where the caller can provide it.

## Prevention

Treat any `finalize()` override anywhere in a codebase as a standing migration item to phantom references/`Cleaner`, and require explicit `close()`/try-with-resources as the primary release mechanism for any native-resource wrapper going forward.

## Monitoring and Alerts

- Native (off-heap) memory usage tracked as its own metric, separate from heap usage, since heap monitoring alone is structurally blind to this failure mode.
- Finalizer-queue backlog length as a leading indicator, if the runtime exposes it, since a growing backlog precedes the native-memory symptom.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent leak.

- **Situation:** native memory grew unbounded while heap metrics stayed normal.
- **Task:** find the release mechanism responsible without any heap-level symptom to follow.
- **Action:** correlated native-memory growth against finalizer-thread backlog under GC pressure, confirming `finalize()`'s lack of timing guarantees as the cause.
- **Result:** migrated to a phantom-reference/`Cleaner`-based cleanup path with deterministic-`close()` as the primary release mechanism.

## Staff-Level Discussion

`finalize()`'s problems are real and well-documented, not folklore — no guaranteed timing, no guaranteed execution, and resurrection risk. The organizational takeaway is that any legacy `finalize()` override represents a standing, silent risk that surfaces exactly like this: invisible until GC pressure exposes the timing gap, at which point it looks like a mysterious native leak rather than a known, avoidable anti-pattern.

## Related Handbook Chapters

- [GC Roots, Reachability, and Reference Strength](../syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md) — the canonical phantom-reference/`Cleaner` pattern behind this incident's fix.
