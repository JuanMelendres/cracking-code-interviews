---
title: "Cheat Sheet: Native Memory, Direct Buffers, and Off-Heap"
slug: native-memory-direct-buffers-and-off-heap
document_type: cheat-sheet
domain: jvm
topic_id: T-311
canonical: ../handbook/jvm/native-memory-direct-buffers-and-off-heap.md
last_updated: 2026-08-05
---

# Native Memory, Direct Buffers, and Off-Heap

**Canonical chapter:** [`syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md`](../syllabus/02-java/jvm-internals/native-memory-direct-buffers-and-off-heap.md)

## Core Mental Model

`-Xmx` is a budget for one specific room in a house (the Java heap), not the whole house. Direct `ByteBuffer`s, thread stacks, metaspace, and JIT-compiled code all live in *other rooms*, each with their own separate budget — and a process's actual total memory footprint is the sum of every room, not just the one `-Xmx` governs. Direct buffers exist because moving data between the JVM and OS I/O layer is faster when that data already lives in a memory region the OS can access directly, without a copy out of the managed, garbage-collected heap.

## Essential Definitions

- **Native memory** — any memory a JVM process uses outside the managed heap: thread stacks, metaspace, JIT code cache, JNI/native-library allocations, direct buffers.
- **Direct (off-heap) `ByteBuffer`** — `ByteBuffer.allocateDirect()`, backing storage outside the heap in native memory, so OS I/O can access it directly without an intermediate copy.
- **`-XX:MaxDirectMemorySize`** — the separate, dedicated budget for direct-buffer allocations, entirely independent of `-Xmx`.

## Decision Table

| Memory region | Governing flag | Visible in a heap dump? |
|---|---|---|
| Java heap | `-Xmx` | Yes |
| Direct buffers | `-XX:MaxDirectMemorySize` | No — use NMT or `BufferPoolMXBean` |
| Thread stacks | `-Xss` × thread count | No |
| Metaspace | `-XX:MaxMetaspaceSize` | No |
| JIT code cache | `-XX:ReservedCodeCacheSize` | No |

**Trade-offs:** direct buffers give a real, measurable I/O performance advantage (eliminating a heap-to-native copy) at the cost of that memory being outside GC's normal `-Xmx`-bounded management, requiring its own budget and its own monitoring approach.

## Key Numbers (real, executed — `DirectBufferDemo.java`, `-Xmx32m -XX:MaxDirectMemorySize=256m`)

```
allocated 32MB...64MB...256MB of DIRECT memory (heap -Xmx is only 32MB --
  this is already impossible on-heap, proving the budgets are separate)

CAUGHT OutOfMemoryError after allocating ~256MB direct memory
message: Cannot reserve 8388608 bytes of direct buffer memory
  (allocated: 268435456, limit: 268435456)   <- exactly the 256m limit, not 32m
```

Real Native Memory Tracking (`-Xmx64m -XX:MaxDirectMemorySize=256m`, 10x10MB buffers):

```
Java Heap (reserved=65536KB, committed=65536KB)   <- exactly 64MB, unaffected
Other     (reserved=102400KB, committed=102400KB) <- exactly 100MB, malloc=#10
```

## Common Pitfalls

- Assuming `-Xmx` bounds a JVM process's total memory usage, leading to container memory limits set exactly equal to `-Xmx` with no headroom.
- Using direct buffers for data never handed to an OS-level I/O call, gaining none of the copy-elimination benefit while adding off-heap complexity.
- Diagnosing a direct-buffer OOM with heap-focused tools (heap dumps, histograms), which are structurally blind to it.

## Interview Answer Skeleton

**30-sec:** `-Xmx` bounds only the Java heap — total process memory also includes thread stacks, metaspace, JIT code cache, and direct buffers, each with its own separate budget. A service can exhaust direct memory and throw a distinct `OutOfMemoryError` while heap usage shows plenty of headroom.

**2-min:** Add why direct buffers are faster for I/O (the GC can move heap objects, but an OS I/O call needs a fixed address for its duration — direct buffers are already fixed) + the real measured evidence (a 32MB-heap process allocating a full 256MB of direct memory before a distinct OOM at exactly the separate limit) + the trade-off (the benefit is I/O-specific; non-I/O data gains nothing from direct allocation).

**Whiteboard:** A large box "JVM process total memory." Inside, a clearly-bounded sub-box "Java Heap (-Xmx)." Several *other*, separately-bounded boxes alongside it — "Direct buffers," "Thread stacks," "Metaspace," "JIT code cache" — none nested inside the heap box. Annotate the outer box: "container memory limit must cover ALL of these."

**Staff-level framing:** container memory-limit sizing requires explicit, measured accounting for every non-heap region a workload actually uses, not a generic rule of thumb — enable Native Memory Tracking proactively for any service where off-heap usage is a plausible concern, rather than adding it reactively during an incident.

## Production Warning Signs

- A Kubernetes-deployed service gets OOMKilled periodically with heap usage, per GC logs, never approaching `-Xmx` — the container limit was set equal to `-Xmx` with no headroom for thread stacks, metaspace, code cache, or direct buffers; diagnose via `jcmd VM.native_memory summary`.
- An NIO-based library throws `OutOfMemoryError: Direct buffer memory` under load while heap-focused monitoring shows no problem at all — standard heap tooling is structurally blind to this; check `BufferPoolMXBean` or NMT specifically.
- **Prevention:** always set `-XX:MaxDirectMemorySize` explicitly for any service using direct buffers meaningfully; size container memory limits with explicit headroom above `-Xmx` informed by real NMT measurement, never equal to `-Xmx` alone.

## Related

- `syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md`
- `syllabus/02-java/jvm-internals/gc-roots-reachability-and-reference-strength.md`
- `syllabus/02-java/jvm-internals/jvm-flags-and-container-ergonomics.md`
