---
title: "Cheat Sheet: Memory-Mapped Files and Zero-Copy I/O"
slug: memory-mapped-files-and-zero-copy-io
document_type: cheat-sheet
domain: 16-performance-jvm
topic_id: T-2419
canonical: ../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md
last_updated: 2026-09-20
---

# Memory-Mapped Files and Zero-Copy I/O

**Canonical chapter:** [`syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md`](../syllabus/16-performance-jvm/memory-mapped-files-and-zero-copy-io.md)

## Core Mental Model

Memory mapping doesn't make disk I/O faster — it removes per-access syscall overhead by turning "ask the
kernel" into "just read memory," with actual disk I/O deferred to the OS page cache on first touch of each
page. The win is concentrated where syscall overhead, not disk throughput, dominates: random/scattered
access.

## Essential Definitions

- **Memory-mapped I/O (`MappedByteBuffer`)** — a file's contents mapped directly into the process's virtual address space; reads become ordinary memory access.
- **Page fault** — the real kernel-mode event that brings a not-yet-resident page into memory on first touch; subsequent accesses to that page are pure memory reads.

## Decision Table

| Situation | What to reach for |
|---|---|
| Genuinely sequential file access (full/near-full scans) | Ordinary buffered `FileChannel` reads — near-parity with mapping |
| Genuinely random/scattered file access (index lookups) | Memory-mapped `MappedByteBuffer` — real, measured ~28x advantage |
| Working set much larger than available RAM | Evaluate real memory pressure before assuming mapping keeps data resident |

## Key Numbers (real, executed — OpenJDK 21.0.12, Apple M4, APFS, 512MB file)

```
Sequential full-file scan:
  FileChannel bulk reads:  ~171-183ms
  MappedByteBuffer scan:   ~177-179ms   -- near-parity, honest

Random access (2M reads of 8 bytes each):
  FileChannel positional reads (1 syscall each): ~839-898ms
  MappedByteBuffer random access (0 syscalls):   ~30ms
  Speedup: ~28-30x
```

## Common Pitfalls

- Proposing memory mapping as a universal file-I/O speedup without distinguishing sequential from random access.
- Assuming memory mapping eliminates disk I/O rather than deferring it transparently to the OS page cache.
- Ignoring `MappedByteBuffer` lifecycle management — the mapping holds real OS resources.

## Interview Answer Skeleton

**30-sec:** Memory-mapped files turn reads into ordinary memory access. Measured directly: sequential access
shows near-parity with buffered reads; random access shows a real ~28x speedup, from eliminating one syscall
per access.

**2-min:** Add the mechanism (page-fault-on-first-touch) + both real measured numbers + Kafka's real
production reliance on this exact mechanism for log-segment I/O.

**Staff-level framing:** weigh the working set's size against available memory (heavy page-faulting under
memory pressure erodes the advantage) before adopting mapping at scale.

## Production Warning Signs

- Scattered index-lookup latency is high, but disk utilization metrics show real headroom.
- **Fix:** migrate the access path to `MappedByteBuffer` — real, measured ~28x improvement for this exact access shape.

## Related

- `syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md`
- `syllabus/09-messaging-event-driven/kafka-architecture-fundamentals.md`
