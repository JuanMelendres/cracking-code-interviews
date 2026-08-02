---
title: "Week 19 Flashcards — JVM Domain Full Closure"
week: 19
document_type: study-pack-flashcards
status: draft
last_reviewed: 2026-08-02
---

# Week 19 Flashcards — JVM Domain Full Closure

18 cards, three per topic, each naming the misconception it catches.

## Card 1

**Prompt:** What are GC roots, concretely?
**Answer:** The fixed set of reference locations treated as inherently alive — active thread stacks, static fields, JNI references, and a few JVM-internal roots.
**Why it matters:** Reachability is a graph-traversal property from these roots, not a vague "not used" notion.
**Common trap:** Describing GC eligibility as "no longer referenced" without naming what makes a reference count as a root.
**Related:** `01-gc-roots-reachability-and-reference-strength.md`

## Card 2

**Prompt:** What's the key behavioral difference between `WeakReference` and `SoftReference`?
**Answer:** Weak clears immediately upon otherwise-unreachability, no memory-pressure consideration; soft is retained under normal conditions, cleared only under real memory pressure (guaranteed before OOM).
**Why it matters:** Explains why `WeakHashMap` is not a pressure-aware cache.
**Common trap:** Using `WeakHashMap` expecting cache-like retention behavior.
**Related:** `01-gc-roots-reachability-and-reference-strength.md`

## Card 3

**Prompt:** Why can't `PhantomReference.get()` ever return the referent?**
**Answer:** A deliberate design choice preventing object resurrection through the cleanup mechanism — phantom refs exist purely for post-collection notification via a `ReferenceQueue`.
**Why it matters:** The modern, hazard-free replacement for `finalize()`.
**Common trap:** Relying on `finalize()` for deterministic resource cleanup.
**Related:** `01-gc-roots-reachability-and-reference-strength.md`

## Card 4

**Prompt:** What's the core architectural difference between ZGC/Shenandoah and G1?
**Answer:** Concurrent relocation — the expensive evacuation work runs alongside application threads via a reference-remapping mechanism, not during a stop-the-world pause.
**Why it matters:** Explains why their pauses are dramatically shorter and largely heap-size-independent.
**Common trap:** Describing them as "just faster G1."
**Related:** `02-zgc-and-shenandoah-concurrent-collection.md`

## Card 5

**Prompt:** What real cost can a concurrent collector impose even when individual GC pauses are excellent?
**Answer:** Allocation stalls — application threads waiting for reclamation to catch up when background work can't keep pace with the allocation rate.
**Why it matters:** A distinct mechanism from classic GC pauses, requiring more heap headroom to avoid.
**Common trap:** Assuming "sub-millisecond pause" means "zero collection-related cost to the application."
**Related:** `02-zgc-and-shenandoah-concurrent-collection.md`

## Card 6

**Prompt:** Why does migrating to ZGC/Shenandoah typically require more heap headroom than a comparable G1 deployment?
**Answer:** To give the concurrent reclamation work enough room to keep pace with allocation and avoid allocation stalls — G1's evacuation-pause model has different headroom needs.
**Why it matters:** A common, real post-migration surprise if not planned for.
**Common trap:** Reusing a G1-era heap-sizing rule of thumb after migrating collectors.
**Related:** `02-zgc-and-shenandoah-concurrent-collection.md`

## Card 7

**Prompt:** Is every stop-the-world pause a GC pause?
**Answer:** No — GC is the most common safepoint operation in practice, but thread dumps, deoptimization, class redefinition, and others use the identical safepoint mechanism.
**Why it matters:** An unexplained pause with no GC log entry can still be a real, legitimate safepoint event.
**Common trap:** Assuming a pause must be unrelated to the JVM entirely just because the GC log is silent.
**Related:** `03-safepoints-and-stop-the-world-mechanics.md`

## Card 8

**Prompt:** What's the difference between "reaching safepoint" and "at safepoint" cost?
**Answer:** Reaching safepoint depends on what each thread is doing when requested (thread-dependent); at safepoint is the operation's own execution cost (operation-dependent).
**Why it matters:** Different causes require different investigation approaches when either is abnormally large.
**Common trap:** Treating "safepoint pause" as one undifferentiated number.
**Related:** `03-safepoints-and-stop-the-world-mechanics.md`

## Card 9

**Prompt:** What real cost gap did Week 19's demo measure between a deadlock check and a full GC's "at safepoint" phase?
**Answer:** Roughly 1,500x — about 1 microsecond for `FindDeadlocks` versus about 1.59 milliseconds for `G1CollectFull`, from the same real run.
**Why it matters:** Direct proof that "at safepoint" cost is entirely operation-specific.
**Common trap:** Assuming all safepoint operations have comparable cost.
**Related:** `03-safepoints-and-stop-the-world-mechanics.md`

## Card 10

**Prompt:** Does an object with zero declared fields occupy zero memory?
**Answer:** No — every object still carries a fixed header (12-16 bytes), regardless of declared field count.
**Why it matters:** A naive field-size-sum memory estimate systematically undercounts real usage.
**Common trap:** Estimating memory footprint by summing only declared field sizes.
**Related:** `04-object-layout-headers-and-compressed-oops.md`

## Card 11

**Prompt:** What mechanism does compressed oops use to represent a reference in 32 bits instead of 64?
**Answer:** It exploits object-alignment guarantees — an object's low address bits are always zero and don't need storing, letting 32 bits address a wider range via an implicit shift.
**Why it matters:** A substantial, essentially-free memory-footprint optimization, enabled by default.
**Common trap:** Assuming a reference field always costs a fixed size regardless of configuration.
**Related:** `04-object-layout-headers-and-compressed-oops.md`

## Card 12

**Prompt:** What happens to reference-field memory cost when a heap grows past compressed oops' ~32GB ceiling?
**Answer:** The JVM silently falls back to full 64-bit references — every reference field's cost doubles.
**Why it matters:** A real, structural cost of that specific scaling decision, not a purely additive one.
**Common trap:** Treating "more heap" as always a simple, additive capacity change.
**Related:** `04-object-layout-headers-and-compressed-oops.md`

## Card 13

**Prompt:** Does `-Xmx` bound a JVM process's total memory usage?
**Answer:** No — it bounds only the Java heap; thread stacks, metaspace, code cache, and direct buffers all live outside it with their own separate budgets.
**Why it matters:** The single most consequential misconception driving OOMKilled incidents on constrained infrastructure.
**Common trap:** Setting a container memory limit equal to `-Xmx` with no headroom for other regions.
**Related:** `05-native-memory-direct-buffers-and-off-heap.md`

## Card 14

**Prompt:** Why are direct buffers faster for I/O specifically?
**Answer:** OS I/O calls need a fixed, stable address; heap memory can move during GC, requiring a copy to native memory first — direct buffers are already fixed, eliminating that copy.
**Why it matters:** The benefit is I/O-specific, not a general off-heap-storage advantage.
**Common trap:** Using direct buffers for data that's never passed to an OS I/O call.
**Related:** `05-native-memory-direct-buffers-and-off-heap.md`

## Card 15

**Prompt:** What tool would show direct-buffer memory usage when a heap dump won't?
**Answer:** Native Memory Tracking (`jcmd VM.native_memory summary`) or JMX's `BufferPoolMXBean` — both report direct-buffer usage specifically.
**Why it matters:** Standard heap-focused tooling is structurally blind to off-heap memory.
**Common trap:** Diagnosing a direct-buffer-related OOM using heap histograms or heap dumps.
**Related:** `05-native-memory-direct-buffers-and-off-heap.md`

## Card 16

**Prompt:** Does escape analysis eliminate only allocation cost, or GC cost too?
**Answer:** Both — a scalar-replaced object is never actually allocated on the heap, so it produces zero garbage and needs zero future collection.
**Why it matters:** "Every `new` allocates real heap memory" is a simplified teaching model, not a guarantee.
**Common trap:** Assuming a heap-allocation cost model applies regardless of JIT optimization.
**Related:** `06-escape-analysis-and-scalar-replacement.md`

## Card 17

**Prompt:** Does escape analysis apply to interpreted (not-yet-compiled) code?
**Answer:** No — it's a JIT-compilation-time optimization; interpreted execution allocates every object for real.
**Why it matters:** Connects allocation-elimination benefits to the broader tiered-compilation warmup story.
**Common trap:** Assuming escape analysis is a universal, always-active JVM behavior.
**Related:** `06-escape-analysis-and-scalar-replacement.md`

## Card 18

**Prompt:** What real, measured GC-pause-count contrast demonstrates escape analysis's effect?
**Answer:** Zero GC pauses across 600 million allocation attempts with it enabled, versus 362 real pauses for the identical workload with it disabled.
**Why it matters:** A dramatic, unambiguous demonstration that the same source code's real allocation behavior depends entirely on this optimization firing.
**Common trap:** Manually avoiding small, clearly non-escaping allocations without measuring whether the JIT already eliminates the cost.
**Related:** `06-escape-analysis-and-scalar-replacement.md`
