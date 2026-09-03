---
title: "Direct Buffer OOM Invisible to Heap Monitoring"
document_type: production-cookbook-entry
domain: jvm
status: draft
last_updated: 2026-08-05
related_handbook:
  - ../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md
source: handbook/jvm/native-memory-direct-buffers-and-off-heap.md#production-scenarios
---

# Direct Buffer OOM Invisible to Heap Monitoring

## Context

A high-throughput networking library used by the service employs direct buffers internally, common in NIO-based frameworks, for I/O performance.

## Symptoms

The service throws `OutOfMemoryError: Direct buffer memory` under load, while heap-focused monitoring — heap histograms, heap dumps, `-Xmx`-based alerting — shows no problem at all.

## Impact

A real, service-crashing memory exhaustion condition is entirely invisible to the team's existing monitoring stack, delaying diagnosis and leaving on-call without an obvious starting point.

## Initial Hypotheses

- A heap-based memory leak — the natural first hypothesis for any `OutOfMemoryError`, checked against heap histograms and dumps.
- The memory pressure lives outside the heap entirely, in direct-buffer allocations the standard tooling doesn't track — correct.

## Evidence

Heap-focused monitoring tooling is structurally blind to this failure mode, since direct buffers aren't heap objects. Direct-memory usage specifically, checked via Native Memory Tracking's `Other` category or JMX's `BufferPoolMXBean`, shows the actual growth pattern that heap tooling never surfaces.

## Investigation Timeline

1. **`OutOfMemoryError: Direct buffer memory` observed under load**, with heap monitoring showing nothing unusual throughout.
2. **Heap-based leak hypothesis checked and ruled out**, via heap histograms and dumps showing no corresponding growth.
3. **Investigation redirected to non-heap memory regions**, given that standard heap tooling was structurally incapable of explaining an error whose message explicitly names direct buffer memory.
4. **Direct-memory usage checked specifically**, via Native Memory Tracking's `Other` category or `BufferPoolMXBean`, confirming the growth pattern.

## Root Cause

Standard heap-monitoring tooling — heap histograms, heap dumps, `-Xmx`-based alerting — is structurally blind to direct-buffer memory usage, since direct buffers aren't heap objects at all. The high-throughput networking library's internal direct-buffer usage grew under load in a way no heap-focused signal could ever surface.

## Immediate Mitigation

Restart the service to clear the exhausted direct-memory pool, and reduce load or concurrency on the affected code path while the actual cause is diagnosed.

## Permanent Fix

Diagnose direct-memory usage specifically using Native Memory Tracking's `Other` category or `BufferPoolMXBean`, and address the underlying cause: either raise `-XX:MaxDirectMemorySize` with real justification if the usage is legitimate and simply under-budgeted, or investigate whether the library is leaking direct buffers — not releasing them promptly — rather than simply needing more budget.

## Alternatives Considered

Raising `-XX:MaxDirectMemorySize` immediately without first diagnosing whether the growth is legitimate usage or a leak. Rejected as premature — if the underlying cause is a leak, raising the limit only delays the eventual crash rather than fixing it, and wastes memory budget in the meantime.

## Trade-offs

Diagnosing via Native Memory Tracking or `BufferPoolMXBean` requires enabling tracking that isn't on by default and adds a small ongoing overhead. Accepted, since heap-only monitoring is provably insufficient for this failure mode, as this incident demonstrates directly.

## Prevention

Any service using a networking or I/O library known to use direct buffers internally should include direct-memory usage as a standard, first-class monitored metric from the start, not added reactively only after a direct-buffer-specific crash occurs.

## Monitoring and Alerts

- Direct-memory usage tracked as a first-class metric alongside heap metrics for any service using direct-buffer-heavy I/O libraries, via `BufferPoolMXBean` or Native Memory Tracking, closing the exact blind spot this incident demonstrates.
- `-XX:MaxDirectMemorySize` headroom alerted on directly, analogous to heap-headroom alerting, so a growing direct-memory footprint is visible well before it reaches the crash threshold.

## Interview Story

This maps to a "you got an OutOfMemoryError but heap monitoring shows nothing, why" question. Present it as a representative scenario unless you have lived through an equivalent incident:

- **Situation:** a service crashed with `OutOfMemoryError: Direct buffer memory` while heap-focused monitoring showed nothing abnormal throughout.
- **Task:** diagnose a memory exhaustion condition your existing tooling is structurally unable to see.
- **Action:** rule out a heap-based leak using heap histograms and dumps; recognize that the error message itself names a non-heap memory region; check direct-memory usage specifically via Native Memory Tracking or `BufferPoolMXBean`.
- **Result:** identified the direct-buffer growth pattern and addressed the underlying cause — a leak or an under-budgeted limit — rather than blindly raising `-XX:MaxDirectMemorySize`.

## Staff-Level Discussion

This incident is a precise illustration of a monitoring blind spot that's invisible until it isn't: a team can have genuinely thorough heap monitoring — histograms, dumps, `-Xmx` alerting — and still have zero visibility into an entire class of memory-related crash, because heap monitoring by definition only covers heap memory, and off-heap allocations (direct buffers, metaspace, native library memory) require entirely separate tooling to observe. The JVM's own error message (`OutOfMemoryError: Direct buffer memory`, distinct from a plain `OutOfMemoryError: Java heap space`) is actually diagnostic on its face, but only to someone whose mental model already distinguishes heap from non-heap memory regions. A Staff engineer designing a monitoring strategy for any service should explicitly enumerate every memory region the JVM manages — heap, metaspace, direct buffers, thread stacks, JIT code cache — and verify each has its own monitored signal, rather than assuming heap coverage implies coverage of "memory" as a whole.

## Related Handbook Chapters

- [Native Memory, Direct Buffers, and Off-Heap](../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md) — canonical direct-buffer and Native Memory Tracking mechanics used here.
