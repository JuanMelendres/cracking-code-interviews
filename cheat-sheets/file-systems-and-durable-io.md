---
title: "Cheat Sheet: File Systems and Durable I/O"
slug: file-systems-and-durable-io
document_type: cheat-sheet
domain: 01-computer-science-foundations
topic_id: T-2440
canonical: ../syllabus/01-computer-science-foundations/file-systems-and-durable-io.md
last_updated: 2026-09-28
---

# File Systems and Durable I/O

**Canonical chapter:** [`syllabus/01-computer-science-foundations/file-systems-and-durable-io.md`](../syllabus/01-computer-science-foundations/file-systems-and-durable-io.md)

## Core Mental Model

`write()` returning means **in the page cache**, not on disk. Durability is a second, explicit, expensive step.

## The Three Layers

| Call | Moves data | Durable? |
|---|---|---|
| `BufferedWriter.write` | Your buffer | No |
| `flush()` | Your buffer → kernel page cache | **No** |
| `fsync` / `force(true)` | Page cache → device, waits for ack | **Yes** |
| `fdatasync` / `force(false)` | Same, skipping inessential metadata | Yes, cheaper |

## Measured (macOS, APFS on NVMe, JDK 21)

**Durability — 2,000 appends of 256 B:**

| Strategy | Time |
|---|---|
| No fsync | 3 ms |
| fsync per record | **7,704 ms** (~2,000x) |
| fsync every 100 | 91 ms (~30x) |

That third row is group commit: same durability, a little latency, a large throughput gain.

**Buffer size — same 64 MB read:**

| Buffer | Time | `read()` calls |
|---|---|---|
| 512 B | 48 ms | 131,072 |
| 4 KB | 9 ms | 16,384 |
| 64 KB | 5 ms | 1,024 |
| 1 MB | 4 ms | 64 |

**Page cache — same 32 MB file, three reads:** 3.3 ms → 2.6 ms → 2.4 ms.

## Honest Negative Result

Sequential vs random, 20,000 × 4 KB each: ratio **0.68x** — indistinguishable. The 10–100x penalty is a **spinning-disk, cache-miss** claim, not a universal fact. Sequential still wins for readahead, erase-block alignment, and prefetching layers.

## Crash-Safe File Write

```text
write to temp file -> force() -> rename over target -> fsync the parent directory
```

Rename is atomic within a file system; the directory sync makes the rename itself durable. Skipping the last step is how you get "the file exists but is empty."

## Common Pitfalls

- Treating `flush()` as durability.
- Syncing per record by default (~2,000x).
- Benchmarking with a warm page cache and calling it disk performance.
- Ignoring `fsync`'s return value, or retrying and treating success as recovery.
- Drive write caches that acknowledge before persisting — `fsync` returns, data is still volatile.

## Interview Answer Skeleton

**30-sec:** `write()` puts bytes in the page cache and returns; only `fsync` waits for the device. Measured, per-record durability cost ~2,000x an un-synced write, which is why databases batch commits. Buffer size matters separately, because it changes syscall count — 12x measured from 512 B to 1 MB.

## Related

- [Memory Hierarchy](../syllabus/01-computer-science-foundations/memory-hierarchy-caches-ram-and-virtual-memory.md)
- [Java File I/O and NIO.2](../syllabus/02-java/language-core/java-file-io-and-nio2.md)
- [Memory-Mapped Files and Zero-Copy I/O](../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md)
