---
title: "Native Memory, Direct Buffers, and Off-Heap"
slug: native-memory-direct-buffers-and-off-heap
document_type: handbook-chapter
domain: 02-java/jvm-internals
status: draft
version: 1.0
last_reviewed: 2026-08-02
topic_id: T-311
mastery_levels_covered: [L1, L2, L3, L4]
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - jvm-memory-layout-and-runtime-regions.md
related:
  - jvm-memory-layout-and-runtime-regions.md
  - gc-roots-reachability-and-reference-strength.md
  - jvm-flags-and-container-ergonomics.md
  - ../concurrency/foreign-function-and-memory-api.md
  - ../../../study-packs/week-19/05-native-memory-direct-buffers-and-off-heap.md
official_references:
  - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html
---

# Native Memory, Direct Buffers, and Off-Heap

> **Topic register:** T-311 (Native memory, direct buffers, off-heap, IWI 4.7) · Advanced tier · Occasional interview frequency [O]

## Table of Contents

1. [Learning Objectives](#learning-objectives)
2. [Why This Matters in Interviews](#why-this-matters-in-interviews)
3. [Level 1 — Foundation](#level-1--foundation)
4. [Level 2 — Working Knowledge](#level-2--working-knowledge)
5. [Mental Model](#mental-model)
6. [Definition and Purpose](#definition-and-purpose)
7. [Core Concepts](#core-concepts)
8. [Internal Implementation](#internal-implementation)
9. [Production Scenarios](#production-scenarios)
10. [Failure Modes and Debugging](#failure-modes-and-debugging)
11. [Trade-offs](#trade-offs)
12. [Decision Framework](#decision-framework)
13. [Common Mistakes](#common-mistakes)
14. [Anti-Patterns](#anti-patterns)
15. [Best Practices](#best-practices)
16. [Interview Answer Framework](#interview-answer-framework)
17. [Interview Questions](#interview-questions)
18. [Summary](#summary)
19. [Key Takeaways](#key-takeaways)
20. [Cheat Sheet](#cheat-sheet)
21. [Flashcards](#flashcards)
22. [Practice Exercises](#practice-exercises)
23. [Solutions](#solutions)
24. [Additional Reading](#additional-reading)
25. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can explain why direct (off-heap) `ByteBuffer`s exist and what specifically makes them faster for I/O, and cite real, measured evidence that direct memory is governed by a completely separate budget from `-Xmx` — including a real `OutOfMemoryError` distinct from a heap OOM, and real Native Memory Tracking output showing direct-buffer memory tracked under its own category, entirely separate from Java Heap.

## Why This Matters in Interviews

"Off-heap" and "native memory" questions test whether a candidate understands that `-Xmx` is not actually a ceiling on a JVM process's total memory usage — a genuinely common, genuinely costly misconception when it drives container memory-limit sizing. A candidate who sizes a container's memory limit exactly equal to `-Xmx`, unaware that direct buffers, thread stacks, metaspace, JIT code cache, and native library allocations all live outside the heap and outside `-Xmx`'s accounting entirely, sets up a real, predictable OOMKilled failure mode the moment any of those other regions grow — this chapter provides the concrete, measured evidence for exactly why that assumption is wrong.

## Level 1 — Foundation

**`-Xmx` only limits the Java heap — it does not limit how much total memory your Java process can use.** A Java application's real, total memory footprint also includes thread stacks, class metadata, and other native-memory usage that live entirely outside the heap and outside `-Xmx`'s accounting.

The everyday practical consequence: a process can genuinely use noticeably more memory than `-Xmx` alone suggests, which matters directly when deciding how much memory to give a container running that process — sizing a container's memory limit to exactly match `-Xmx` leaves no room for everything else the process also needs.

## Level 2 — Working Knowledge

**The practical, everyday sizing rule**: when deploying a Java application in a container (Docker, Kubernetes), always give the container meaningfully more memory than `-Xmx` alone — a common, safe starting habit is reserving real headroom (not just a token few percent) for thread stacks, class metadata, and other native-memory usage on top of the heap. Skipping this is one of the most common, avoidable causes of a container being killed for exceeding its memory limit (`OOMKilled`) even when the JVM's own heap looked perfectly healthy.

If you don't recognize the specific term "direct `ByteBuffer`" or "off-heap" in your own codebase, that's fine at this level — the one practical takeaway that matters for everyday container sizing is simply: **heap size is not the same as total process memory usage**, and container memory limits should always account for the difference.

## Mental Model

Think of `-Xmx` as a budget for one specific room in a house (the Java heap), not a budget for the whole house. Direct `ByteBuffer`s, thread stacks, metaspace, and JIT-compiled code all live in *other rooms*, each with their own separate budget (`-XX:MaxDirectMemorySize`, per-thread `-Xss` times thread count, `-XX:MaxMetaspaceSize`, code-cache limits respectively) — and a process's actual total memory footprint is the sum of every room, not just the one `-Xmx` happens to govern. Direct buffers specifically exist because moving data between the JVM and the operating system's I/O layer is faster when that data already lives in a memory region the OS can access directly, without the JVM first having to copy it out of the managed, garbage-collected heap into a native-accessible location.

## Definition and Purpose

**Native memory** is any memory a JVM process uses outside the managed Java heap — thread stacks, metaspace, JIT code cache, internal JVM data structures, and any memory a native library (via JNI) or `Unsafe`/direct-buffer allocation obtains directly from the operating system. **Direct (off-heap) `ByteBuffer`s** (`ByteBuffer.allocateDirect()`) allocate their backing storage outside the Java heap, in native memory, specifically so operating-system I/O operations (socket reads/writes, file I/O) can access that memory directly without an intermediate copy through heap-managed memory — a real, measurable performance advantage for I/O-heavy code, at the cost of that memory being outside the garbage collector's normal, automatic-and-bounded-by-`-Xmx` management. **`-XX:MaxDirectMemorySize`** is the separate, dedicated budget specifically for direct-buffer allocations, entirely independent of `-Xmx`.

## Core Concepts

### Direct buffers exist to eliminate a copy in I/O operations, not as a general-purpose memory-saving trick

A heap-allocated `ByteBuffer`'s contents must be copied to a native, non-moving memory location before the operating system can perform an actual I/O operation on it (since the garbage collector can move heap objects, but the OS-level I/O call needs a fixed address for its duration) — a direct buffer's backing memory is already in exactly such a native, non-moving location, eliminating that copy. This benefit is specific to I/O-heavy code; allocating direct buffers for data that's never handed to an OS-level I/O call provides no such advantage and simply moves memory-management complexity outside the heap's automatic bounds for no real benefit.

### `-Xmx` genuinely does not bound total JVM process memory usage

This is the single most consequential practical implication of this chapter's topic: a JVM process's real, total memory footprint includes the heap (bounded by `-Xmx`) plus everything native-memory-resident — direct buffers (bounded separately by `-XX:MaxDirectMemorySize`), thread stacks (`-Xss` times live thread count, effectively unbounded without a thread-count limit), metaspace, JIT code cache, and any native-library allocations. Sizing a container's memory limit to exactly `-Xmx` is a common, real misconfiguration that ignores every one of these other regions.

### Direct memory has its own separate OOM failure mode, distinct from a heap OOM

`OutOfMemoryError: Direct buffer memory` is a real, distinct exception from `OutOfMemoryError: Java heap space` — it fires when `-XX:MaxDirectMemorySize` is exceeded, regardless of how much headroom remains in the actual Java heap, and diagnosing it requires checking direct-memory usage specifically (via Native Memory Tracking or JMX), not heap-focused tooling like a heap histogram or heap dump, which won't show direct-buffer allocations at all since they're not heap objects.

## Internal Implementation

**Real evidence that direct memory is governed by a completely separate budget from `-Xmx`** (`practice/java/week-19/native-memory/src/DirectBufferDemo.java`, `-Xmx32m -XX:MaxDirectMemorySize=256m`):

```
allocated 32MB of DIRECT memory so far (heap -Xmx is only 32MB, so this is already impossible on-heap)
allocated 64MB of DIRECT memory so far ...
...
allocated 256MB of DIRECT memory so far ...

CAUGHT OutOfMemoryError after allocating ~256MB direct memory
message: Cannot reserve 8388608 bytes of direct buffer memory (allocated: 268435456, limit: 268435456)
```

A process with `-Xmx32m` successfully allocates a full 256MB of direct memory — 8 times the heap ceiling — before hitting a real, distinct `OutOfMemoryError` at exactly the `-XX:MaxDirectMemorySize=256m` limit specified, not the 32MB heap limit. This is direct, unambiguous proof that direct-buffer memory is accounted against its own separate budget entirely, not the heap.

**Real Native Memory Tracking evidence that direct-buffer memory is tracked under its own distinct category, never under "Java Heap"** (`-Xmx64m -XX:MaxDirectMemorySize=256m -XX:NativeMemoryTracking=summary`, 10 direct buffers of 10MB each allocated, `jcmd <pid> VM.native_memory summary`):

```
- Java Heap (reserved=65536KB, committed=65536KB)
- Other  (reserved=102400KB, committed=102400KB)
           (malloc=102400KB #10)
```

`Java Heap` shows exactly 65536KB (64MB, matching `-Xmx64m` precisely) — unaffected by the direct-buffer allocations. `Other` shows exactly 102400KB (100MB, matching the 10×10MB direct buffers exactly) with `#10` malloc calls, matching the 10 buffers allocated one-for-one — real, precise confirmation that direct-buffer memory lives entirely outside the Java Heap category, tracked separately, and would be completely invisible to any tool that only reports on-heap memory usage.

## Production Scenarios

**A service is deployed to Kubernetes with a container memory limit set exactly equal to `-Xmx`, and gets OOMKilled periodically despite heap usage (visible via GC logs or heap dumps) never approaching the configured maximum.** The gap is memory the container limit accounts for that `-Xmx` never bounded in the first place — thread stacks (especially under an unexpectedly large thread-pool configuration), metaspace, JIT code cache, or direct-buffer usage from an I/O-heavy library. The fix requires provisioning the container limit with real headroom above `-Xmx` specifically for these other regions (per [JVM Flags and Container Ergonomics](jvm-flags-and-container-ergonomics.md)'s container-sizing guidance), and using Native Memory Tracking to identify exactly which non-heap region is consuming the unaccounted memory, rather than assuming the heap configuration alone determines the container's real memory needs.

**A high-throughput networking library that uses direct buffers internally (common in NIO-based frameworks) causes a `OutOfMemoryError: Direct buffer memory` under load, while heap-focused monitoring shows no problem at all.** Standard heap-monitoring tooling (heap histograms, heap dumps, `-Xmx`-based alerting) is structurally blind to this failure mode, since direct buffers aren't heap objects — diagnosing it requires checking direct-memory usage specifically, via Native Memory Tracking's `Other` category (as this chapter's evidence shows directly) or JMX's `BufferPoolMXBean`, and the fix typically involves either raising `-XX:MaxDirectMemorySize` with real justification, or investigating whether the library is leaking direct buffers (not releasing them promptly) rather than simply needing more budget.

## Failure Modes and Debugging

- **Symptom: a container gets OOMKilled with heap usage well under `-Xmx`.** Check total process memory usage against the container limit, and specifically audit thread count, metaspace, JIT code cache, and direct-buffer usage via Native Memory Tracking — `-Xmx` alone does not bound what a container-level OOM killer sees, and any of these other regions can be the actual cause.
- **Symptom: `OutOfMemoryError: Direct buffer memory` with heap usage showing plenty of headroom.** This is expected, not contradictory — direct memory has its own separate budget (`-XX:MaxDirectMemorySize`), and heap headroom provides zero protection against exceeding it; check direct-memory usage specifically via NMT or `BufferPoolMXBean`, not heap tooling.
- **Anti-pattern to rule out first when sizing a container memory limit for a JVM workload:** confirm the limit includes real headroom above `-Xmx` for non-heap regions (thread stacks, metaspace, code cache, direct buffers, native library usage) rather than setting the limit equal to `-Xmx` — this is one of the single most common, most avoidable sources of OOMKilled incidents for JVM workloads on constrained infrastructure.

## Trade-offs

Direct buffers provide a real, measurable I/O performance advantage (eliminating a heap-to-native copy) at the cost of that memory being outside the garbage collector's normal, `-Xmx`-bounded management — requiring its own separate budget (`-XX:MaxDirectMemorySize`) and its own separate monitoring approach (NMT, `BufferPoolMXBean`), since standard heap-focused tooling is structurally blind to it. For I/O-light code, this trade-off buys nothing, since the copy-elimination benefit only matters when the buffer is actually handed to an OS-level I/O call.

## Decision Framework

Use direct buffers specifically for I/O-heavy code paths (socket or file I/O) where the copy-elimination benefit is real — not as a general-purpose "off-heap storage" mechanism for data that's never handed to an OS-level I/O call, where the benefit doesn't apply and the memory-management complexity isn't worth it. Always set `-XX:MaxDirectMemorySize` explicitly (rather than relying on its JVM-version-dependent default, which is not always simply "unlimited" or always simply "equal to `-Xmx`") for any service using direct buffers meaningfully, so the budget is a deliberate capacity decision, not an accident. Size container memory limits with explicit headroom above `-Xmx` covering thread stacks, metaspace, code cache, and direct-buffer usage — never set the limit equal to `-Xmx` alone.

## Common Mistakes

- Assuming `-Xmx` bounds a JVM process's total memory usage, leading to container memory limits set exactly equal to `-Xmx` with no headroom for non-heap regions.
- Using direct buffers for data that's never handed to an OS-level I/O call, gaining none of the copy-elimination benefit while still incurring off-heap memory-management complexity.
- Diagnosing a direct-buffer-related OOM using heap-focused tools (heap dumps, heap histograms), which are structurally blind to direct-buffer memory since it isn't heap-resident.
- Leaving `-XX:MaxDirectMemorySize` at its default without understanding what that default actually is for the JVM version in use, rather than setting it deliberately.

## Anti-Patterns

Sizing a container's memory limit purely from `-Xmx`, with no explicit accounting for thread stacks, metaspace, code cache, or direct-buffer usage — this is one of the single most common, most avoidable causes of OOMKilled incidents for JVM workloads on Kubernetes or other memory-constrained container platforms, and it's avoidable specifically by understanding, as this chapter demonstrates directly, that `-Xmx` was never a bound on total process memory in the first place.

## Best Practices

Provision container memory limits with explicit, deliberate headroom above `-Xmx` for every non-heap region a specific workload actually uses — informed by real measurement (Native Memory Tracking) rather than a generic rule of thumb, since the right headroom depends on actual thread count, metaspace usage, and direct-buffer usage for that specific service. Enable Native Memory Tracking (`-XX:NativeMemoryTracking=summary`) as a standard diagnostic capability for any service where off-heap memory usage is a plausible concern, so a real `Other`-category or `Thread`-category breakdown is available when investigating an unexplained memory-footprint gap, rather than needing to add this instrumentation reactively during an incident.

## Interview Answer Framework

### 30-Second Answer

`-Xmx` bounds only the Java heap — a JVM process's total memory usage also includes thread stacks, metaspace, JIT code cache, and direct (off-heap) `ByteBuffer`s, each with its own separate budget. Direct buffers specifically exist to eliminate a heap-to-native copy for I/O operations, governed by `-XX:MaxDirectMemorySize`, entirely independent of `-Xmx` — a service can exhaust direct memory and throw a distinct `OutOfMemoryError` while heap usage shows plenty of headroom.

### 2-Minute Answer

Definition: native memory is everything a JVM process uses outside the managed heap; direct buffers specifically allocate their backing storage in native memory to eliminate a copy for OS-level I/O operations. Why direct buffers exist: OS I/O calls need a fixed, non-moving memory address, which heap memory (movable by the GC) can't directly provide without an intermediate copy — direct buffers are already in a suitable location. How the budgets work: `-Xmx` bounds heap only; `-XX:MaxDirectMemorySize` bounds direct-buffer memory separately; thread stacks, metaspace, and code cache have their own budgets too. One trade-off: direct buffers' I/O benefit is real but specific to I/O-heavy code — using them for non-I/O data gains nothing while adding off-heap management complexity. One production example: measured directly, a process with `-Xmx32m` successfully allocated a full 256MB of direct memory (8x the heap limit) before hitting a distinct `OutOfMemoryError: Direct buffer memory` at exactly the separately-configured `-XX:MaxDirectMemorySize=256m` limit — real, unambiguous proof the two budgets are entirely independent.

### 10-Minute Deep Dive

Cover: why `-Xmx` was never a total-process-memory bound, and the full list of regions that live outside it; the specific mechanism making direct buffers faster for I/O (eliminating the movable-heap-to-fixed-native copy); the real, measured evidence of the 256MB-on-a-32MB-heap allocation and its distinct OOM message, confirming the separate-budget claim unambiguously; the real Native Memory Tracking evidence showing direct-buffer memory tracked under a distinct `Other` category with an exact byte-for-byte and allocation-count match to the real buffers allocated, confirming standard heap tooling is structurally blind to this memory; the container-memory-limit-equals-`-Xmx` anti-pattern as the single most consequential practical implication, and the concrete remediation (explicit headroom, informed by NMT measurement) this chapter's evidence directly supports.

### Whiteboard Explanation

Draw a large box labeled "JVM process total memory." Inside it, draw a clearly-bounded sub-box labeled "Java Heap (bounded by -Xmx)." Draw several *other*, separately-bounded boxes alongside it at the same level — "Direct buffers (-XX:MaxDirectMemorySize)," "Thread stacks (-Xss × thread count)," "Metaspace," "JIT code cache" — each with its own label, none nested inside the heap box. Annotate the outer box: "container memory limit must cover ALL of these, not just the heap box."

### Production Example

A high-throughput API gateway service is containerized with a memory limit set exactly equal to its configured `-Xmx`, and experiences periodic OOMKilled restarts under peak load despite GC logs showing heap occupancy consistently well under the configured maximum. Using Native Memory Tracking during a load test reproduces the issue in staging, revealing the gateway's underlying NIO framework allocates a growing pool of direct buffers under high connection concurrency, consuming memory entirely outside `-Xmx`'s accounting. The remediation sets an explicit `-XX:MaxDirectMemorySize` calibrated to the framework's real peak usage (measured via NMT under representative load) and raises the container memory limit to cover heap plus that explicit direct-memory budget plus a smaller margin for thread stacks and metaspace — eliminating the OOMKilled pattern by correctly accounting for every memory region the process actually uses, not just the heap.

### Trade-offs to Mention

Direct buffers' I/O performance benefit is real and specific to I/O-heavy code, not a general memory-management improvement; the cost is real operational complexity (a separate budget, separate monitoring, a distinct OOM failure mode) that only pays off when the copy-elimination benefit actually applies.

### Common Candidate Mistakes

Assuming `-Xmx` bounds total JVM process memory usage; not knowing direct buffers have their own separate `OutOfMemoryError` and budget, distinct from the heap's.

### Typical Follow-Up Questions

"Why can't a heap-allocated buffer be used directly for OS-level I/O without a copy?" → the garbage collector can move heap objects during collection, but an in-progress OS I/O call needs a fixed, stable memory address for its duration — a heap buffer's address isn't guaranteed stable, so a copy to a fixed, native location is required unless the buffer is already off-heap. "What tooling would you use to diagnose a suspected direct-memory issue, given heap dumps won't show it?" → Native Memory Tracking (`jcmd VM.native_memory summary`, as this chapter demonstrates) or JMX's `BufferPoolMXBean`, both of which report direct-buffer usage specifically, unlike heap-focused tools.

### Senior-Level Expectations

Correctly explains that `-Xmx` bounds only the heap, and names at least direct buffers and thread stacks as examples of memory outside it.

### Staff-Level Discussion

Treats container memory-limit sizing as requiring explicit, measured accounting for every non-heap region a specific workload uses, not a generic rule of thumb, and proactively enables Native Memory Tracking for services where off-heap usage is a plausible concern rather than adding it reactively during an incident. Recognizes direct buffers' I/O-specific benefit precisely, avoiding both the misuse of direct buffers for non-I/O data and the container-sizing misconception that treats `-Xmx` as a total-memory bound.

## Interview Questions

### Question 1

**A Kubernetes-deployed service's container memory limit is set exactly equal to `-Xmx`, and it gets OOMKilled periodically despite heap usage, per GC logs, never approaching the configured maximum. What's the likely issue, and how would you diagnose it?**

**Expected answer:** `-Xmx` bounds only the Java heap, not the JVM process's total memory usage — thread stacks, metaspace, JIT code cache, and direct-buffer usage all live outside it and outside the container limit's accounting gap this creates. Diagnose via Native Memory Tracking (`jcmd VM.native_memory summary`) to identify which specific non-heap region is consuming the unaccounted memory, rather than assuming the heap configuration alone determines real memory needs.

**Common mistakes:** assuming the heap configuration alone should determine the container limit, without accounting for other regions.

**Follow-up questions:** "If NMT shows a large 'Other' category, what would that likely indicate?" (direct-buffer usage, per this chapter's evidence — worth checking `-XX:MaxDirectMemorySize` and what in the application or its libraries is allocating direct buffers.)

**Senior-level expectations:** correctly identifies that `-Xmx` doesn't bound total process memory and proposes NMT as the diagnostic tool.

**Staff-level expectations:** proposes the specific remediation (explicit container headroom informed by real NMT measurement) as a standing practice, not a one-off fix.

### Question 2

**Why are direct `ByteBuffer`s faster for I/O operations than heap-allocated ones, specifically?**

**Expected answer:** OS-level I/O calls need a fixed, stable memory address for the duration of the operation, but the garbage collector can move heap objects during collection — a heap-allocated buffer's contents must therefore be copied to a fixed, native-accessible location before an actual I/O operation can use it. A direct buffer's backing memory is already in such a fixed, native location, eliminating that copy entirely.

**Common mistakes:** describing the benefit vaguely ("it's off-heap so it's faster") without naming the specific movable-heap-versus-fixed-native-address mechanism.

**Follow-up questions:** "Would a direct buffer provide any benefit for data that's never passed to an OS I/O call?" (No — the entire benefit is specific to eliminating the I/O-time copy; for non-I/O data, direct allocation only adds off-heap management complexity with no corresponding advantage.)

**Senior-level expectations:** correctly names the movable-heap-versus-fixed-native-address mechanism as the specific reason.

**Staff-level expectations:** correctly identifies that the benefit is I/O-specific and doesn't generalize to non-I/O use cases.

## Summary

`-Xmx` bounds only the Java heap — a JVM process's real total memory footprint also includes thread stacks, metaspace, JIT code cache, and direct-buffer memory, each governed by its own separate budget. Direct buffers specifically eliminate a heap-to-native copy for OS-level I/O operations, a real, I/O-specific performance benefit, governed by `-XX:MaxDirectMemorySize` entirely independent of `-Xmx`. Measured directly: a process with `-Xmx32m` allocated a full 256MB of direct memory — 8 times the heap limit — before hitting a distinct `OutOfMemoryError: Direct buffer memory` at exactly its separately-configured limit, and Native Memory Tracking confirmed direct-buffer memory tracked under an entirely separate `Other` category, with an exact byte-for-byte match to the real allocations, invisible to standard heap-focused tooling. Sizing a container memory limit to exactly `-Xmx`, ignoring these other regions, is a common, real, avoidable source of OOMKilled incidents.

## Key Takeaways

- `-Xmx` bounds only the Java heap, not a JVM process's total memory usage — thread stacks, metaspace, code cache, and direct buffers all live outside it, each with their own separate budget.
- Direct buffers exist specifically to eliminate a heap-to-native copy for OS-level I/O operations — a real, I/O-specific benefit, not a general off-heap-storage advantage.
- Measured directly: a 32MB-heap process allocated 256MB of direct memory before hitting a distinct, separate `OutOfMemoryError`, unambiguously confirming the independent budgets.
- Real NMT evidence confirmed direct-buffer memory tracked entirely separately from Java Heap, with an exact match to actual allocations — invisible to heap-focused diagnostic tools.
- Sizing a container memory limit to exactly `-Xmx`, with no headroom for non-heap regions, is a common, avoidable cause of OOMKilled incidents for JVM workloads.

## Cheat Sheet

| Memory region | Governing flag | Visible in a heap dump? |
|---|---|---|
| Java heap | `-Xmx` | Yes |
| Direct buffers | `-XX:MaxDirectMemorySize` | No — use NMT or `BufferPoolMXBean` |
| Thread stacks | `-Xss` × thread count | No |
| Metaspace | `-XX:MaxMetaspaceSize` | No |
| JIT code cache | `-XX:ReservedCodeCacheSize` | No |

## Flashcards

**Q: Does `-Xmx` bound a JVM process's total memory usage?**
A: No — it bounds only the Java heap; thread stacks, metaspace, code cache, and direct buffers all live outside it, with their own separate budgets.

**Q: Why are direct buffers faster for I/O specifically?**
A: OS I/O calls need a fixed, stable memory address; heap memory can move during GC, requiring a copy to native memory before I/O — direct buffers are already in a fixed native location, eliminating that copy.

**Q: What tool would show direct-buffer memory usage, when a heap dump won't?**
A: Native Memory Tracking (`jcmd VM.native_memory summary`) or JMX's `BufferPoolMXBean` — both report direct-buffer memory specifically, unlike heap-focused tools.

## Practice Exercises

1. Reproduce `DirectBufferDemo.java` at your own `-Xmx`/`-XX:MaxDirectMemorySize` values, and confirm the OOM occurs at the direct-memory limit regardless of how small or large `-Xmx` is set.
2. Reproduce the NMT evidence with a different number and size of direct buffers, and confirm the `Other` category's reported bytes and allocation count match your specific allocation pattern exactly.

## Solutions

1. The `OutOfMemoryError: Direct buffer memory` message's reported limit should always match your `-XX:MaxDirectMemorySize` value exactly, regardless of `-Xmx` — direct confirmation the two budgets are independent, not proportional to each other in any way.
2. NMT's `Other` category should report `reserved`/`committed` bytes matching your total direct-buffer allocation exactly, and a `malloc` count matching your specific number of `allocateDirect()` calls — confirming NMT's accounting is precise and traceable to actual allocations, not an estimate.

## Additional Reading

- [`java.nio.ByteBuffer` documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html)

## Official References

- [`java.nio.ByteBuffer` documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/ByteBuffer.html)
