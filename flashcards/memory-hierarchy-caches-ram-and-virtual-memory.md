---
title: "Flashcards: Memory Hierarchy: Caches, RAM, and Virtual Memory"
slug: memory-hierarchy-caches-ram-and-virtual-memory
document_type: flashcard-deck
domain: 01-computer-science-foundations
topic_id: "T-2006"
canonical: ../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md
last_updated: 2026-09-20
---

# Flashcards: Memory Hierarchy: Caches, RAM, and Virtual Memory

**Canonical chapter:** [`syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md`](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md)

## Card: Why isn't O(1) array access always equally fast?

**Prompt:**
Big-O calls `array[i]` O(1) regardless of array size. Why can the same operation still take dramatically different real time depending on context?

**Answer:**
Big-O counts operations, not their physical cost. Memory is a hierarchy of tiers (registers, L1/L2/L3 cache, RAM, disk) with very different real latencies; which tier serves a given access depends on the working set's size and access pattern, not the algorithm's complexity. Measured directly: ~1.39ns when the data fits in L1, ~95ns once it no longer fits anywhere but RAM — a ~68x range for the identical "O(1)" operation.

**Why it matters:**
A gap invisible to algorithmic-complexity analysis alone, but one of the most common real causes of "this should be fast, why isn't it" in production.

**Common trap:**
Treating O(1) as "always the same number of nanoseconds" rather than "doesn't grow with input size."

**Related:**
[Foundation](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md#3-foundation-l1)

## Card: What is a cache line, and why does it matter for access pattern?

**Prompt:**
What does a CPU cache actually store, and why does that make sequential access faster than random access to the same data?

**Answer:**
A cache stores fixed-size chunks called cache lines (typically 64 bytes), not individual bytes. Reading one `int` pulls its whole 64-byte line in — the next ~15 `int`s in the same array are already cached for free. Sequential access benefits from this and from hardware prefetch; random access gets neither, since the next address isn't predictable and rarely reuses an already-cached line.

**Why it matters:**
Explains why an `ArrayList` iterated in order routinely outperforms a `LinkedList` of the same logical size, despite both being O(n) to iterate.

**Common trap:**
Assuming two structures with the same Big-O for iteration will perform comparably in practice.

**Related:**
[Core Concepts](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md#4-core-concepts-l2)

## Card: How does the pointer-chasing benchmark measure genuine latency?

**Prompt:**
Why does this chapter's benchmark use a random single-cycle permutation and pointer chasing, instead of a simple sequential-read loop, to measure cache/RAM latency?

**Answer:**
A sequential loop would measure the hardware prefetcher's effectiveness, not raw latency. A random permutation defeats the prefetcher (the next index isn't predictable); chasing a dependent pointer chain (each read's address depends on the previous read's result) also defeats out-of-order execution's ability to overlap loads — the measured time per step is close to the honest, serialized cost of one real memory access.

**Why it matters:**
The real technique behind `lat_mem_rd`/LMbench and Drepper's "What Every Programmer Should Know About Memory" — a legitimate, citable measurement method, not an ad hoc benchmark.

**Common trap:**
Measuring memory latency with a sequential scan and mistaking prefetch effectiveness for raw access latency.

**Related:**
[How It Works Internally](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md#5-how-it-works-internally-l3)

## Card: Real measured cache cliffs match real hardware cache sizes

**Prompt:**
This chapter's pointer-chase benchmark found its largest single latency cliff between 16 MiB and 32 MiB working sets. Was that coincidental?

**Answer:**
No — `sysctl hw.perflevel0.l2cachesize` on the same machine (Apple M4) reports exactly 16,777,216 bytes (16 MiB) for the performance-core L2 cache. The measured cliff lands precisely at that real, hardware-reported boundary.

**Why it matters:**
Cache capacity is not an abstraction — it's a real, discoverable number on the machine code actually runs on, and crossing it has a real, measured cost.

**Common trap:**
Treating cache-hierarchy sizes as fixed textbook numbers (32KB/256KB/8MB) rather than checking the real, current machine.

**Related:**
[Performance Implications](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md#10-performance-implications)

## Card: What is a page fault?

**Prompt:**
What happens, mechanically, the first time a program touches a virtual memory page with no physical backing resolved yet?

**Answer:**
The CPU traps into the operating system (a page fault) — the OS finds or allocates a physical page, updates the per-process virtual-to-physical address translation, and resumes the program. Every subsequent access to that same page is an ordinary, cache-hierarchy-governed memory read, not another trap.

**Why it matters:**
The exact mechanism behind memory-mapped I/O's real, measured ~28x random-access speedup over ordinary positional reads — mmap pays this cost once per page instead of a syscall on every access.

**Common trap:**
Confusing a page fault (an OS-level trap) with a cache miss (a hardware-only event with no OS involvement).

**Related:**
[Core Concepts](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md#4-core-concepts-l2)

## Card: Why doesn't "add more RAM" fix a cache-locality problem?

**Prompt:**
A hot loop is slow because its working set doesn't fit in cache. Does adding more total system RAM fix it?

**Answer:**
No. Cache capacity (KB-to-tens-of-MB scale) is a much smaller, physically different resource than total system RAM (GB scale). More RAM doesn't change how often the hot loop's working set exceeds L1 or L2 — the same Staff-level pattern as adding more threads to an already CPU-bound system: identify which specific physical resource is actually exhausted before adding more of an unrelated one.

**Why it matters:**
A real, recurring capacity-planning mistake — throwing an abundant, cheap resource (RAM) at a problem caused by exhausting a scarce, different one (cache).

**Common trap:**
Treating "memory" as one undifferentiated resource, the same mistake this repository's own "JVM memory" chapters warn against for stack vs. heap.

**Related:**
[Staff/System-Level Considerations](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md#13-staffsystem-level-considerations-l4)
