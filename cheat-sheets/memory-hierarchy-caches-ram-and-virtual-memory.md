---
title: "Cheat Sheet: Memory Hierarchy: Caches, RAM, and Virtual Memory"
slug: memory-hierarchy-caches-ram-and-virtual-memory
document_type: cheat-sheet
domain: 01-computer-science-foundations
topic_id: T-2006
canonical: ../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md
last_updated: 2026-09-20
---

# Memory Hierarchy: Caches, RAM, and Virtual Memory

**Canonical chapter:** [`syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md`](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md)

## Core Mental Model

Memory is not one uniform resource — it's a hierarchy of physically distinct storage tiers (registers → L1 → L2/L3 → RAM → disk), each larger and slower than the one above it. Big-O's "O(1) array access" counts operations, not their cost; which tier actually serves a given access can change that cost by close to two orders of magnitude.

## Essential Definitions

- **Cache line** — the fixed-size chunk (typically 64 bytes) a cache actually stores and fetches; reading one `int` pulls its whole line in, making the next ~15 `int`s in the same array free.
- **Spatial locality / prefetch** — sequential access reads consecutive cache lines in order, letting the hardware prefetcher fetch ahead; random access gives the prefetcher nothing to predict.
- **Page** — a fixed-size chunk (commonly 4 KiB) of virtual address space, the unit virtual memory manages.
- **Page fault** — the CPU trapping into the OS when a program touches a virtual page with no physical backing resolved yet; the OS resolves it and resumes the program.
- **Pointer-chasing benchmark** — the measurement technique (random single-cycle permutation, e.g. Sattolo's algorithm) that defeats both prefetching and out-of-order execution, revealing genuine per-tier access latency.

## Decision Table

| Working set size (this machine, Apple M4) | Tier | Real measured ns/access |
|---|---|---|
| 4 KiB – 64 KiB | L1 | ~1.39 |
| 128 KiB – 256 KiB | L1→L2 boundary | 1.48 → 3.64 |
| 512 KiB – 8 MiB | L2 | 4.9 → 8.8 |
| 16 MiB → 32 MiB | L2→RAM boundary | 17.06 → 55.95 |
| 64 MiB – 256 MiB | RAM | 80.8 → 95.3 |

## Common Pitfalls

- Treating O(1) as "always the same number of nanoseconds" — it means "doesn't grow with input size," not "constant cost."
- Assuming `LinkedList` and `ArrayList` perform comparably for sequential iteration because both are O(n) — the real gap is a pure locality effect, invisible to Big-O.
- Reaching for "add more RAM" to fix a cache-locality problem — cache capacity is a much smaller, different resource than total system memory.
- Assuming memory mapping is always faster than ordinary reads — its real advantage concentrates in random/scattered access, not sequential.

## Interview Answer Skeleton

**30-sec:** Memory is a hierarchy, not one resource — registers, L1/L2/L3 cache, RAM, disk, each larger and slower than the last. A pointer-chasing benchmark on this repo's own hardware measured ~1.4ns for an L1-resident access vs. ~95ns once the working set no longer fits anywhere but RAM — a ~68x range for what Big-O calls the identical O(1) array access.

**2-min:** Add the mechanism: caches move data in fixed-size lines (64 bytes), so sequential access benefits from spatial locality and hardware prefetch while random access doesn't. Add virtual memory: process isolation via per-process address translation, with page faults as the real, measurable cost of a not-yet-resident page's first touch — the exact mechanism behind memory-mapped I/O's real, measured ~28x random-access speedup over ordinary reads.

**Staff-level framing:** "Memory" is not one undifferentiated resource, any more than "JVM memory" is — a workload slow because its working set doesn't fit in cache will not get faster from more total RAM, more heap, or a bigger instance type, because none of those changes the cache capacity the hot loop actually needs.

## Production Warning Signs

- A profiler shows real time in cache-miss or TLB-miss hardware counters — a specific, actionable signal pointing at data layout or access pattern, not generic system noise.
- Matrix code with identical Big-O runs measurably slower in one traversal order than another — a row-major-vs-column-major locality mismatch, not an algorithm defect.
- A hot path built on scattered, pointer-linked objects (e.g., `LinkedList`, tree-of-node-objects) underperforms an array-backed equivalent with the same asymptotic complexity.

## Real Measured Numbers

- Pointer-chase latency, OpenJDK 21.0.12, Apple M4: 1.39ns (4 KiB, L1) → 3.64ns (256 KiB, crossing L1) → 8.80ns (8 MiB, L2) → 55.95ns (32 MiB, crossing L2's real reported 16 MiB capacity) → 95.31ns (256 MiB, RAM) — a real ~68x range.
- The largest single measured cliff (16 MiB → 32 MiB) lands exactly at this machine's own `sysctl hw.perflevel0.l2cachesize` value: 16,777,216 bytes.
- Real, in-repo applied consequences: [False Sharing and Cache-Line Contention](../syllabus/16-performance-jvm/false-sharing-and-cache-line-contention.md) (~16x measured), [Memory-Mapped Files and Zero-Copy I/O](../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md) (~28x measured).
