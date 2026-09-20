---
title: "Cheat Sheet: False Sharing and Cache-Line Contention"
slug: false-sharing-and-cache-line-contention
document_type: cheat-sheet
domain: 16-performance-jvm
topic_id: T-2417
canonical: ../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md
last_updated: 2026-09-20
---

# False Sharing and Cache-Line Contention

**Canonical chapter:** [`syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md`](../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md)

## Core Mental Model

Cache coherence operates on 64-byte cache lines, not individual variables — "no shared logical state" at
the language level does not mean "no shared state" at the hardware level. Any write anywhere in a line
invalidates every other core's cached copy of the whole line.

## Essential Definitions

- **False sharing** — independent variables written by different threads/cores contending for the same 64-byte CPU cache line, purely from memory proximity.
- **Cache line** — the fixed-size block (64 bytes on virtually all mainstream CPUs) that cache-coherence protocols track as a unit.
- **Padding** — deliberately separating hot fields by at least 64 bytes so they can never share a line.

## Decision Table

| Situation | What to reach for |
|---|---|
| Multiple independently-hot, per-thread fields (sharded counters, striped locks) | Pad each to its own cache line (≥64 bytes real separation) |
| Padding an array | Use a primitive array (`long[]`, `int[]`) with index-based padding, not an array of objects |
| Throughput plateaus/regresses as concurrency increases, no logical contention | Suspect false sharing; check memory layout |

## Key Numbers (real, executed — OpenJDK 21.0.12, Apple M4)

```
4 threads, 200M atomic increments each:
  Unpadded (shared cache line): ~5,900ms
  Padded (separate lines):      ~370ms
  Slowdown from false sharing:  ~16x
```

## Common Pitfalls

- Assuming "no shared logical state" rules out contention.
- Padding by field count instead of byte count (must be ≥64 bytes).
- Padding an array of object references (e.g., `AtomicLong[]`) at the array-slot level — doesn't control where the referenced objects themselves land on the heap. Use a primitive array instead.

## Interview Answer Skeleton

**30-sec:** False sharing: independent variables on the same 64-byte cache line, written by different
cores, force real cache-coherence invalidation traffic even though the data is never logically shared.
Measured directly: a real ~16x slowdown, eliminated by padding.

**2-min:** Add the mechanism (line-granularity invalidation) + the byte-level padding-distance proof + the
real array-of-objects-vs-primitive-array pitfall.

**Staff-level framing:** weigh padding's real memory cost against its real throughput benefit for the
specific hot fields that matter; recognize "more parallelism made it slower, no logical contention" as the
specific symptom pattern.

## Production Warning Signs

- A sharded/striped concurrent design's throughput plateaus or *degrades* as more shard threads are added, despite being logically lock-free.
- **Fix:** pad each shard/counter to its own cache line.

## Related

- `syllabus/02-java/concurrency/atomics-cas-and-the-aba-problem.md`
- `syllabus/02-java/jvm-internals/object-layout-headers-and-compressed-oops.md`
- `syllabus/16-performance-jvm/benchmarking-and-jmh-pitfalls.md`
