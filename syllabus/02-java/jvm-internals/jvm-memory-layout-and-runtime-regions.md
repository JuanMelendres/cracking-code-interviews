---
title: "JVM Memory Layout and Runtime Regions"
slug: jvm-memory-layout-and-runtime-regions
document_type: handbook-chapter
domain: 02-java/jvm-internals
status: draft
version: 1.0
last_reviewed: 2026-07-31
mastery_levels_covered: [L1, L2, L3, L4]
difficulty:
  - intermediate
  - advanced
target_levels:
  - senior
  - staff
prerequisites: []
related:
  - gc-fundamentals-and-log-analysis.md
  - memory-leak-diagnosis-and-heap-dump-analysis.md
  - object-layout-headers-and-compressed-oops.md
  - native-memory-direct-buffers-and-off-heap.md
  - ../language-core/classloaders-and-class-initialization.md
  - ../../../study-packs/week-16/03-jvm-memory-layout-and-runtime-regions.md
official_references:
  - https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5
---

# JVM Memory Layout and Runtime Regions

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
14. [Best Practices](#best-practices)
15. [Interview Answer Framework](#interview-answer-framework)
16. [Interview Questions](#interview-questions)
17. [Summary](#summary)
18. [Key Takeaways](#key-takeaways)
19. [Cheat Sheet](#cheat-sheet)
20. [Flashcards](#flashcards)
21. [Practice Exercises](#practice-exercises)
22. [Solutions](#solutions)
23. [Additional Reading](#additional-reading)
24. [Official References](#official-references)

---

## Learning Objectives

By the end of this chapter you can name every runtime data area the JVM Specification defines, explain which are shared across all threads and which are per-thread, and prove with real, measured evidence that heap, metaspace, and thread stacks are genuinely independent regions — each exhaustible on its own, each sized by its own flag, none of them "just more memory" interchangeably.

## Why This Matters in Interviews

"`-Xmx` controls how much memory my Java process uses" is the single most common oversimplification a Senior candidate makes, and it fails the moment an interviewer asks about a `StackOverflowError` or a `Metaspace` OOM happening on a process with heap to spare. The JVM's memory is not one undifferentiated pool — it is a fixed set of distinct runtime data areas (JVM Specification §2.5), each with its own sizing flag, its own failure mode, and its own diagnostic signature. A candidate who can correctly route "why did this specific error happen" to the right region, with real numbers, reads as someone who has actually operated a JVM in production, not someone reciting `-Xmx`/`-Xms` from a tutorial.

## Level 1 — Foundation

**A running Java program doesn't use one big pool of memory — it uses several separate regions, each with its own limit**, and `-Xmx` (the flag most engineers know) only controls one of them: the heap, where your objects live. Thread stacks, class metadata, and other bookkeeping each have their own, separate budget.

The everyday, practical consequence: an "out of memory" error can mean different things depending on which region ran out, and "increase `-Xmx`" only helps if the heap specifically is the one that's full. A `StackOverflowError` from deep recursion, for instance, has nothing to do with how much heap is available — it means one thread's own stack ran out, a completely separate, per-thread budget.

## Level 2 — Working Knowledge

The three flags a working engineer encounters most often: **`-Xmx`** caps the heap (where objects live); **`-Xss`** caps each individual thread's stack size (relevant if you see `StackOverflowError`s, or if you're running a very large number of threads, since the total stack memory scales with thread count); **`-XX:MaxMetaspaceSize`** caps class metadata (relevant mainly for applications that generate or load unusually many classes at runtime).

**A practical diagnostic habit**: when you see an `OutOfMemoryError`, read its specific message before assuming it's a heap problem — `OutOfMemoryError: Java heap space` means the heap; `OutOfMemoryError: Metaspace` means class metadata; a `StackOverflowError` (a different error entirely) means one thread's stack. Each points to a different region and a different fix, and treating all of them as "just add more memory" wastes time on the wrong lever.

## Mental Model

Think of a JVM process as a building with several separately-metered utilities, not one shared meter. The heap is the building's shared warehouse — one space every tenant (thread) can put objects into, sized by `-Xmx`. Metaspace is the building's blueprint archive — one shared room holding the structural definition (class metadata) of every type ever loaded, sized separately by `-XX:MaxMetaspaceSize`. Each thread's stack is that tenant's own private hallway for call-frame bookkeeping — sized per-thread by `-Xss`, and running out of *hallway space* (deep recursion) has nothing to do with how full the shared warehouse is. Running out of any one of these produces a different, specific failure — not a generic "out of memory."

## Definition and Purpose

The **JVM Specification (§2.5)** defines a fixed set of runtime data areas created at JVM startup or per-thread-creation: the **heap** (shared, holds all objects and arrays, garbage-collected), the **method area / metaspace** (shared, holds per-class structural data — runtime constant pool, field/method metadata, and in HotSpot specifically since Java 8, allocated from native memory rather than the heap), the **JVM stack** (per-thread, holds frames for method invocation — local variables, operand stack, return addresses), the **PC register** (per-thread, tracks the current bytecode instruction), and the **native method stack** (per-thread, used for native/JNI calls). Each region exists because it serves a genuinely different access pattern and lifetime — object data is shared and needs collection; class metadata is shared but essentially permanent; call-frame state is inherently per-thread and needs to be cheap to allocate/deallocate on every call.

## Core Concepts

### Metaspace holds class metadata, not objects — and is separate from the heap since Java 8

Before Java 8, class metadata lived in a fixed-size heap region called PermGen, a frequent source of `OutOfMemoryError: PermGen space` from applications that generated many classes at runtime (heavy reflection, dynamic proxies, some ORM/DI frameworks). Java 8 replaced PermGen with metaspace, allocated from native memory and, by default, unbounded except by available system memory — but `-XX:MaxMetaspaceSize` can cap it explicitly, and exceeding that cap produces a distinct `OutOfMemoryError: Metaspace`, separate from a heap OOM.

### Each thread pays for its own stack, sized independently of the heap

`-Xss` sets the stack size *per thread*, not a shared pool. A recursion depth that overflows one thread's stack has no relationship to heap occupancy — a process can have gigabytes of free heap and still throw `StackOverflowError` on a single thread with a small `-Xss` and deep enough recursion. This also means total memory reserved for stacks scales with thread count (`threads × -Xss`), which matters directly for sizing decisions on high-thread-count workloads (thread-pool sizing, virtual-thread carrier pools).

### Native Memory Tracking (NMT) makes every region's real size directly observable

`-XX:NativeMemoryTracking=summary` plus `jcmd <pid> VM.native_memory summary` gives an exact, categorized breakdown of every region's committed and reserved memory — Java Heap, Class (metaspace), Thread (stacks), Code (JIT-compiled code cache), GC (collector bookkeeping), and more — as real numbers from the live process, not documentation.

## Internal Implementation

**Real NMT summary output**, OpenJDK 21.0.12, a near-idle process (`-XX:NativeMemoryTracking=summary`, sampled via `jcmd <pid> VM.native_memory summary`):

```
-                 Java Heap (reserved=6299648KB, committed=397312KB)
-                     Class (reserved=1048650KB, committed=202KB)
                            (classes #531)
                            (  Metadata:   reserved=65536KB, committed=192KB, used=76KB)
                            (  Class space: reserved=1048576KB, committed=128KB, used=7KB)
-                    Thread (reserved=37130KB, committed=37130KB)
                            (threads #18)
                            (stack: reserved=37080KB, committed=37080KB, peak=37080KB)
-                      Code (reserved=247733KB, committed=7621KB)
-                        GC (reserved=175172KB, committed=60068KB)
```

Every region is separately reserved and committed — a JVM starting up with default settings has already reserved gigabytes of *address space* (`reserved`) while actually using a small fraction of it (`committed`), and each row is its own accounting, not a slice of one shared pool.

**Real metaspace exhaustion, heap held essentially constant** (`practice/java/week-16/memory-layout/MetaspaceExhaustionDemo.java`, `-Xmx512m -XX:MaxMetaspaceSize=32m`): each loop iteration generates a genuinely distinct class at runtime (a fresh `ClassLoader` plus a dynamic proxy) and retains a strong reference so it's never unloaded — class metadata accumulates in metaspace with essentially no heap cost.

```
generated 2000 classes | heap used ~12MB / 512MB
generated 4000 classes | heap used ~38MB / 512MB

CAUGHT java.lang.OutOfMemoryError, message:
Metaspace
Total classes generated before OOM:
5275
Heap used at OOM (MB):
18
Heap max (MB):
512
```

**A real `OutOfMemoryError: Metaspace` at 5,275 generated classes, with heap usage at 18MB out of a 512MB max at the moment of failure** — direct, measured proof that metaspace exhaustion is entirely independent of heap occupancy; a process with 494MB of free heap still fails, because the exhausted resource is a different region entirely.

**Real thread-stack-depth scaling, heap held constant across all three runs** (`practice/java/week-16/memory-layout/StackDepthDemo.java`, unbounded recursion, `-Xmx512m` fixed):

| `-Xss` | Recursion depth reached before `StackOverflowError` |
|---|---|
| 256k | 1,479 |
| 1m | 19,988 |
| 8m | 413,005 |

Same heap size in every run; only `-Xss` changed, and recursion depth scaled by roughly two orders of magnitude across the range — direct, measured proof that stack capacity is governed entirely by its own flag, with zero dependency on heap size.

## Production Scenarios

**A service throws `StackOverflowError` on one specific request path, but the process's heap and overall memory usage look completely normal.** This is the direct production signature of a per-thread stack limit being hit by unusually deep recursion (a recursive parser on deeply-nested input, a recursive data-structure traversal on an adversarially deep tree) — not a heap sizing problem at all. The fix is either bounding the recursion depth (validate input nesting depth before processing) or, if the recursion is legitimate and bounded but simply deep, increasing `-Xss` for the affected thread pool specifically (via `Thread` constructor's stack-size parameter, or the JVM-wide `-Xss` flag if acceptable for all threads) — never by increasing `-Xmx`, which has no effect on stack capacity.

**A service that does heavy reflection, dynamic proxying, or hot class-reloading (some dependency-injection frameworks, some plugin architectures) throws `OutOfMemoryError: Metaspace` after running for a while, with heap occupancy staying low the entire time.** This is the metaspace-specific failure demonstrated above — the fix is finding and eliminating the source of unbounded dynamic class generation (often a caching bug where generated proxy/adapter classes should be cached and reused per-type but are instead regenerated per-call), not raising `-Xmx`.

## Failure Modes and Debugging

- **Symptom: `StackOverflowError`, heap and overall process memory look fine.** Confirm it's genuinely a stack-capacity issue (not a true infinite-recursion bug) by checking whether the recursion depth is data-dependent and unusually deep for legitimate input — then decide between bounding the recursion or raising `-Xss` for the specific thread pool involved.
- **Symptom: `OutOfMemoryError: Metaspace`, heap occupancy stays low.** Use `jcmd <pid> VM.native_memory summary` (with NMT enabled) or `jcmd <pid> GC.class_stats` to find which class-loading source is generating unbounded distinct classes; the fix is almost always caching/reusing generated classes rather than regenerating them, since raising `-XX:MaxMetaspaceSize` only delays the same failure.
- **Anti-pattern to rule out first:** treating any `OutOfMemoryError` as automatically a heap problem — the JVM Specification defines multiple independently-exhaustible regions, and the error message (`Metaspace`, `unable to create native thread`, plain `Java heap space`) tells you exactly which one.

## Trade-offs

A small `-Xss` conserves memory per thread (important at high thread counts, since total stack reservation scales with `threads × -Xss`) at the cost of a lower ceiling on safe recursion depth. A larger `-Xmx` gives the heap more room but does nothing for metaspace or stack capacity — sizing each region correctly requires understanding they are independent budgets, not one combined "give the JVM more memory" lever.

## Decision Framework

When an `OutOfMemoryError` or `StackOverflowError` occurs, read the exact error message and/or region name first — `Metaspace`, `Java heap space`, `unable to create native thread`, and `StackOverflowError` each point at a different, specific region and require a different fix. Only after confirming which region is exhausted should sizing or code-level remediation be chosen — raising the wrong flag (e.g., `-Xmx` for a metaspace or stack problem) has no effect and wastes a deploy cycle confirming that.

## Common Mistakes

- Treating `-Xmx` as controlling total JVM memory usage, rather than specifically the heap — metaspace, thread stacks, and the JIT code cache are all sized independently and are not bounded by `-Xmx`.
- Raising `-Xmx` in response to a `StackOverflowError` or `Metaspace` OOM, which has zero effect on either.
- Not accounting for `threads × -Xss` when sizing a high-thread-count workload — a large thread pool with a generous `-Xss` can reserve a surprising amount of address space before any object is ever allocated.
- Assuming PermGen-era terminology ("PermGen space" errors) still applies on modern JDKs — Java 8+ uses metaspace, native-memory-backed, with a different error message and different default behavior (effectively unbounded unless capped).

## Anti-Patterns

Sizing every JVM flag by trial-and-error increases to a single number ("just bump the memory") without first identifying which specific region the failure occurred in — this wastes deploy cycles and, worse, can mask the real underlying issue (e.g., a metaspace leak from unbounded dynamic class generation) behind a temporarily-larger cap that simply delays the same failure.

## Best Practices

Enable Native Memory Tracking (`-XX:NativeMemoryTracking=summary`) in any environment where memory-region diagnosis might be needed — the overhead is small and it turns "which region is actually the problem" from a guess into a `jcmd` command away from an exact answer.

## Interview Answer Framework

### 30-Second Answer

The JVM has several distinct runtime memory regions, not one pool: the heap (shared, objects, sized by `-Xmx`), metaspace (shared, class metadata, sized by `-XX:MaxMetaspaceSize`, native-memory-backed since Java 8), and per-thread stacks (call frames, sized by `-Xss`). Each is independently exhaustible with its own specific error, and raising `-Xmx` fixes exactly one of those failure modes.

### 2-Minute Answer

Definition: the JVM Specification (§2.5) defines the heap, method area/metaspace, per-thread JVM stacks, PC registers, and native method stacks as distinct runtime data areas. Why it exists: object data, class metadata, and call-frame bookkeeping have genuinely different access patterns and lifetimes, so treating them as one pool would be both wasteful and wouldn't let each be tuned or diagnosed independently. How it works: each region has its own sizing flag and its own specific failure — `Java heap space` OOM from `-Xmx`, `Metaspace` OOM from `-XX:MaxMetaspaceSize`, `StackOverflowError` from `-Xss` per thread. One trade-off: a large `-Xss` on a high-thread-count pool reserves address space proportional to `threads × -Xss`, independent of heap size. One production example: measured directly, a metaspace-exhaustion demo threw a real `OutOfMemoryError: Metaspace` after 5,275 dynamically-generated classes with heap usage at only 18MB of a 512MB max at the moment of failure — proof the two regions are genuinely independent.

### 10-Minute Deep Dive

Cover: the full JVM Specification §2.5 list of runtime data areas and which are shared vs. per-thread; the PermGen-to-metaspace history (Java 8) and why it matters (native-memory-backed, effectively unbounded unless capped, different error message); the measured metaspace-exhaustion evidence (5,275 classes, 18MB heap at OOM out of 512MB); the measured stack-depth-vs-`-Xss` scaling (1,479 / 19,988 / 413,005 depth at 256k/1m/8m, heap held constant); the NMT tooling (`-XX:NativeMemoryTracking=summary` + `jcmd VM.native_memory summary`) as the direct, exact way to see every region's real committed/reserved size on a live process; the production diagnostic discipline of reading the specific error message/region name before choosing a fix, since the wrong flag has zero effect on the wrong region.

### Whiteboard Explanation

Draw one large box labeled "Heap (shared, -Xmx)" and one medium box labeled "Metaspace (shared, -XX:MaxMetaspaceSize, native memory)" side by side — explicitly separate, not nested. Then draw two or three small parallel boxes labeled "Thread N stack (-Xss)" next to each other, one per thread, to show stacks are per-thread and don't share capacity with each other or with the heap/metaspace boxes. Narrate: "each of these fails independently, with its own specific error, and sizing one flag never affects the others."

### Production Example

A backend service using a plugin architecture that dynamically generates a proxy class per plugin configuration change (rather than caching and reusing proxy classes per plugin type) accumulates distinct classes in metaspace over weeks of configuration churn, eventually throwing `OutOfMemoryError: Metaspace` while heap and general memory metrics look completely normal — exactly the shape measured in this chapter's demo. The fix is caching generated proxy classes keyed by plugin type rather than regenerating one per configuration change.

### Trade-offs to Mention

`-Xss` sizing trades per-thread memory reservation against safe recursion-depth ceiling; at high thread counts, total stack reservation (`threads × -Xss`) can itself become a meaningful memory line item independent of heap size.

### Common Candidate Mistakes

Treating `-Xmx` as a universal memory dial; not knowing metaspace is native-memory-backed and independent of heap since Java 8; citing "PermGen" as if it's still the current mechanism.

### Typical Follow-Up Questions

"What's the difference between PermGen and metaspace, concretely?" → PermGen was a fixed-size heap region (pre-Java-8); metaspace is native-memory-backed and effectively unbounded unless capped. "If a thread pool has 200 threads and `-Xss` is 8MB, what's the total stack reservation?" → up to 1.6GB of reserved address space, independent of heap size. "How do you find out exactly how much memory each region is using on a live process?" → Native Memory Tracking (`-XX:NativeMemoryTracking=summary` + `jcmd VM.native_memory summary`).

### Senior-Level Expectations

Correctly names the distinct regions and their sizing flags, and routes a given error message to the right region without hesitation.

### Staff-Level Discussion

Connects region sizing to capacity-planning decisions at the fleet level — e.g., recognizing that a migration to a much-higher-thread-count concurrency model (a large platform-thread pool, as opposed to virtual threads which use much smaller carrier-thread stacks) has a real, calculable memory cost from `threads × -Xss` that should be modeled explicitly, not discovered via an incident. Treats "which specific region failed" as the first diagnostic question for any JVM memory-related production issue, as a matter of discipline, not something worked out ad hoc under incident pressure.

## Interview Questions

### Question 1

**A process throws `StackOverflowError` on one endpoint, but heap usage and overall process memory look completely normal. What's happening, and what do you check?**

**Expected answer:** stack capacity is per-thread and independent of heap; check whether the recursion depth is legitimately data-dependent and deep, then either bound the recursion or raise `-Xss` for the affected thread pool — never raise `-Xmx`, which has no effect.

**Common mistakes:** proposing to increase heap size; not recognizing stack and heap as independent regions.

**Follow-up questions:** "What does raising `-Xss` actually cost, at scale?" (memory proportional to `threads × -Xss`)

**Senior-level expectations:** correctly identifies stack capacity as the issue and names the right flag.

**Staff-level expectations:** quantifies the `threads × -Xss` cost trade-off for a specific thread-count scenario.

### Question 2

**A service throws `OutOfMemoryError: Metaspace` after running for a while, but heap occupancy has stayed low the whole time. Diagnose it.**

**Expected answer:** metaspace holds class metadata and is exhausted independently of heap; suspect unbounded dynamic class generation (reflection, dynamic proxies, hot-reload) not being cached/reused per type; confirm with NMT or class-loading diagnostics; fix by caching generated classes, not by raising `-Xmx` or even `-XX:MaxMetaspaceSize` alone (which only delays the same failure if the root cause isn't fixed).

**Common mistakes:** raising `-Xmx`; not recognizing metaspace as a distinct, native-memory-backed region since Java 8.

**Follow-up questions:** "How would you confirm this is metaspace and not heap, from the error alone?" (the error message literally says `Metaspace`, distinct from `Java heap space`)

**Senior-level expectations:** correctly diagnoses metaspace as the exhausted region and proposes the caching fix.

**Staff-level expectations:** explains why merely raising the metaspace cap without fixing the root cause only delays the same failure.

## Summary

The JVM defines several genuinely independent runtime memory regions — heap, metaspace, and per-thread stacks foremost among them — each with its own sizing flag and its own specific failure mode. Measured directly: a metaspace-exhaustion demo threw a real OOM after 5,275 dynamically-generated classes with heap usage at only 18MB of a 512MB max; a stack-depth demo showed recursion capacity scaling from 1,479 to 413,005 across `-Xss` values from 256k to 8m, with heap held constant throughout. Diagnosing any JVM memory failure starts with reading which specific region the error names, since the wrong flag has zero effect on the wrong region.

## Key Takeaways

- The JVM Specification defines distinct runtime data areas (heap, metaspace, per-thread stacks, PC registers, native method stacks) — not one memory pool.
- `-Xmx` controls only the heap; it has zero effect on `StackOverflowError` or metaspace OOM.
- Metaspace is native-memory-backed since Java 8 (replacing PermGen), effectively unbounded unless `-XX:MaxMetaspaceSize` caps it.
- Stack capacity is per-thread (`-Xss`), and total stack reservation scales with `threads × -Xss`, independent of heap size.
- Native Memory Tracking (`-XX:NativeMemoryTracking=summary` + `jcmd VM.native_memory summary`) gives exact, per-region committed/reserved numbers on a live process.
- Diagnose by reading the specific error/region name first — raising the wrong flag has no effect and wastes a deploy cycle.

## Cheat Sheet

| Region | Sizing flag | Shared or per-thread | Failure mode |
|---|---|---|---|
| Heap | `-Xmx` / `-Xms` | Shared | `OutOfMemoryError: Java heap space` |
| Metaspace | `-XX:MaxMetaspaceSize` | Shared | `OutOfMemoryError: Metaspace` |
| JVM stack | `-Xss` | Per-thread | `StackOverflowError` |
| Diagnostic tool | `-XX:NativeMemoryTracking=summary` + `jcmd VM.native_memory summary` | — | Exact per-region committed/reserved bytes |

## Flashcards

**Q: Does `-Xmx` control total JVM memory usage?**
A: No — only the heap. Metaspace, thread stacks, and the JIT code cache are all sized independently.

**Q: What replaced PermGen in Java 8, and what changed?**
A: Metaspace — native-memory-backed rather than a fixed heap region, effectively unbounded unless `-XX:MaxMetaspaceSize` caps it.

**Q: Is thread-stack capacity affected by heap size?**
A: No — measured directly, recursion depth scaled from 1,479 to 413,005 purely from changing `-Xss`, with heap size held constant throughout.

## Practice Exercises

1. Reproduce `practice/java/week-16/memory-layout/MetaspaceExhaustionDemo.java` yourself at a different `-XX:MaxMetaspaceSize`. Confirm heap usage at the moment of OOM stays low regardless of the cap chosen.
2. Reproduce `StackDepthDemo.java` at your own chosen `-Xss` values and confirm the depth-vs-stack-size relationship holds. Then calculate: for a 500-thread pool at your measured `-Xss`, what's the total reserved stack memory?

## Solutions

1. Heap usage at OOM should stay in the tens-of-MB range regardless of the metaspace cap, since the demo generates class metadata (metaspace), not heap objects — confirming the two regions are exhausted independently.
2. `500 × -Xss` (e.g., at 1m, that's 500MB of reserved stack address space) — a real, calculable capacity-planning number, illustrating why thread-pool sizing and `-Xss` sizing are linked decisions, not independent ones.

## Additional Reading

- [Oracle — Troubleshooting Memory Leaks (Native Memory Tracking)](https://docs.oracle.com/en/java/javase/21/troubleshoot/diagnostic-tools.html)
- [ClassLoaders and Class Initialization](../language-core/classloaders-and-class-initialization.md) — the real mechanism that defines a class's metadata before it ever occupies the memory regions covered in this chapter.

## Official References

- [The Java Virtual Machine Specification, §2.5 — Runtime Data Areas](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-2.html#jvms-2.5)
