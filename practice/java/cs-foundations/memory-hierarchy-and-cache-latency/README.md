# Memory Hierarchy and Cache Latency — Real, Executed Evidence

Evidence base for [Memory Hierarchy: Caches, RAM, and Virtual Memory](../../../../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md) (T-2006). One real, compiled-and-run pointer-chasing benchmark measuring memory-access latency as the working set grows from 4 KiB to 256 MiB.

Environment: OpenJDK 21.0.12 (Homebrew build), Apple M4, macOS. Real hardware cache sizes for this machine, read directly via `sysctl`:

```text
$ sysctl -n machdep.cpu.brand_string
Apple M4
$ sysctl hw.perflevel0.l1dcachesize hw.perflevel0.l2cachesize hw.l1dcachesize hw.l2cachesize
hw.perflevel0.l1dcachesize: 131072     # 128 KiB -- performance core L1 data cache
hw.perflevel0.l2cachesize: 16777216    # 16 MiB  -- performance-core cluster L2 (shared, 4 cores)
hw.l1dcachesize: 65536                 # 64 KiB  -- efficiency core L1 data cache
hw.l2cachesize: 4194304                # 4 MiB   -- efficiency-core cluster L2 (shared)
```

## The technique: pointer chasing over a random single-cycle permutation

`src/MemoryLatencyDemo.java` builds an `int[]` of size *n* holding a **random permutation that is a single cycle covering all *n* indices** (Sattolo's algorithm — a strictly-less-than variant of Fisher-Yates), then repeatedly follows it: `idx = next[idx]`.

This specific technique — not just "read the array in a loop" — matters:

- **A random cycle defeats the hardware prefetcher.** Sequential access (`for i in 0..n: sum += arr[i]`) lets the CPU predict the next address and fetch it ahead of time, which would measure the prefetcher's effectiveness, not raw access latency. A random permutation gives the prefetcher nothing to predict.
- **Pointer chasing (each access depends on the previous one's result) defeats out-of-order execution's ability to overlap independent loads.** The next address to read is only known once the current read completes, so each step is a genuine, serialized round trip to wherever that cache line currently lives.

This is the same technique behind `lat_mem_rd` (LMbench) and the benchmark in Ulrich Drepper's *What Every Programmer Should Know About Memory*.

## Running it

```bash
javac -d out src/MemoryLatencyDemo.java
java -Xmx1g -cp out MemoryLatencyDemo
```

`-Xmx1g` — the largest working set tested is a 256 MiB `int[]`; the default heap on some configurations is too small to hold it plus the permutation-construction overhead.

Each working-set size gets its own warm-up pass (touching every element at least once, so the timed pass measures steady-state cache/RAM latency, not one-time page-fault cost) before 20,000,000 timed pointer-chase steps.

## Real captured output

```text
size_bytes,size_elements,ns_per_access
4096,1024,1.390
8192,2048,1.400
16384,4096,1.389
32768,8192,1.392
65536,16384,1.391
131072,32768,1.483
262144,65536,3.641
524288,131072,4.877
1048576,262144,5.805
2097152,524288,5.583
4194304,1048576,7.478
8388608,2097152,8.796
16777216,4194304,17.059
33554432,8388608,55.954
67108864,16777216,80.821
134217728,33554432,91.042
268435456,67108864,95.311
```

## Reading the staircase

| Working set | ns/access | What's happening |
|---|---|---|
| 4 KiB – 64 KiB | ~1.39 (flat) | Fits entirely in L1 data cache — real hardware L1 on this machine is 64 KiB (efficiency core) to 128 KiB (performance core) per `sysctl`, matching the flat region's upper bound almost exactly |
| 128 KiB – 256 KiB | 1.48 → 3.64 | The L1 boundary — first real cliff, roughly 2.5x, right where the working set exceeds even the larger (128 KiB) L1 |
| 512 KiB – 8 MiB | 4.9 → 8.8 (gently rising) | L2 territory — slower than L1 but still a large, real jump below RAM, gently rising as the set approaches L2's own capacity |
| 16 MiB → 32 MiB | 17.06 → 55.95 | The single largest cliff in the whole run (~3.3x) — and it lands exactly where `sysctl` reports the performance-core L2 as 16,777,216 bytes. This is not a coincidence this demo asserts; it is the L2 capacity this exact machine reports, and the cliff sits precisely at that boundary |
| 64 MiB – 256 MiB | 80.8 → 95.3 (flattening) | Working set now far larger than any on-chip cache — this plateau is real DRAM latency, an order of magnitude above the L1 number this same table started at |

**Honest reading.** The demo does not assert an L3 boundary distinctly from the L2-to-RAM transition — Apple Silicon's memory hierarchy (per-cluster L2, then a shared system-level cache, then DRAM) doesn't map cleanly onto the classic three-level x86 L1/L2/L3 model this chapter's text also describes for that reason, and this benchmark's own data is consistent with that: one clean L1 cliff, then a single large step from L2 capacity into what is likely a blend of system-level cache and DRAM rather than two cleanly separated further cliffs. The overall conclusion the numbers do support cleanly and repeatably: **going from the smallest to the largest working set here costs roughly 95 / 1.39 ≈ 68x more time per single memory access**, entirely explained by which physical storage tier that access is actually served from — nothing about the Java code accessing it changed at all.
