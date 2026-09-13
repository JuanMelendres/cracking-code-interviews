---
title: "Cheat Sheet: JVM Memory Layout and Runtime Regions"
slug: jvm-memory-layout-and-runtime-regions
document_type: cheat-sheet
domain: jvm
topic_id: T-301
canonical: ../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md
last_updated: 2026-09-12
---

# JVM Memory Layout and Runtime Regions

**Canonical chapter:** [`syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md`](../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md)

## Mental Model

A JVM process is a building with several separately-metered utilities, not one shared meter. The heap is the shared warehouse (`-Xmx`) every thread can put objects into. Metaspace is the shared blueprint archive holding class metadata (`-XX:MaxMetaspaceSize`). Each thread's stack is that thread's own private hallway for call-frame bookkeeping (`-Xss`) — running out of hallway space (deep recursion) has nothing to do with how full the shared warehouse is. Running out of any one region produces a different, specific failure, not a generic "out of memory."

## Decision Table

| Region | Sizing flag | Shared or per-thread | Failure mode |
|---|---|---|---|
| Heap | `-Xmx` / `-Xms` | Shared | `OutOfMemoryError: Java heap space` |
| Metaspace | `-XX:MaxMetaspaceSize` | Shared | `OutOfMemoryError: Metaspace` |
| JVM stack | `-Xss` | Per-thread | `StackOverflowError` |
| Diagnostic tool | `-XX:NativeMemoryTracking=summary` + `jcmd VM.native_memory summary` | — | Exact per-region committed/reserved bytes |

## Decision Framework

Read the exact error message/region name first (`Metaspace`, `Java heap space`, `unable to create native thread`, `StackOverflowError`) — each points at a different region and needs a different fix. Only after confirming which region is exhausted, choose sizing or code-level remediation; raising the wrong flag (e.g., `-Xmx` for a metaspace or stack problem) has zero effect and wastes a deploy cycle confirming that.

## Common Pitfalls

- Treating `-Xmx` as controlling total JVM memory usage, rather than specifically the heap.
- Raising `-Xmx` in response to a `StackOverflowError` or `Metaspace` OOM — zero effect on either.
- Not accounting for `threads × -Xss` when sizing a high-thread-count workload.
- Assuming PermGen-era terminology still applies on modern JDKs — Java 8+ uses metaspace (native-memory-backed, effectively unbounded unless capped).

## Interview Answer Skeleton

30 seconds: heap, metaspace, and thread stacks are separately sized, separately failing regions — `-Xmx` only bounds the heap. 2 minutes: add the specific sizing flag and failure mode per region, and that thread count multiplies stack reservation. Deep dive: walk a real `OutOfMemoryError: Metaspace` incident where raising `-Xmx` did nothing, then fixed by raising `-XX:MaxMetaspaceSize` after confirming the region from the error text.

## Production Warning Signs

- `OutOfMemoryError` with a region name other than "Java heap space" — don't touch `-Xmx`.
- A large thread pool sized without accounting for `-Xss × thread count` in address-space reservation.
- Metaspace growth uncapped in a long-running service that dynamically generates/loads classes (proxies, bytecode generation).

## Related

- [G1 Internals: Remembered Sets and Write Barriers](../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md)
- [Memory Leak Diagnosis and Heap Dump Analysis](../syllabus/02-java/jvm-internals/memory-leak-diagnosis-and-heap-dump-analysis.md)
- [Object Layout, Headers, and Compressed OOPs](../syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md)
