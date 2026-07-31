---
title: "Week 16 Flashcards — JVM Internals Depth"
week: 16
document_type: study-pack-flashcards
status: draft
last_reviewed: 2026-07-31
---

# Week 16 Flashcards — JVM Internals Depth

15 cards, three per topic, each naming the misconception it catches.

## Card 1

**Prompt:** What two mechanisms let G1 collect a subset of regions without scanning the whole heap?
**Answer:** Remembered sets (per-region incoming-reference records) and write barriers (which keep them accurate by dirtying cards on relevant stores).
**Why it matters:** This is the actual mechanism behind "collect the regions with the most garbage first," not just a slogan.
**Common trap:** Naming only one of the two mechanisms, or describing RSets as updated synchronously on every store.
**Related:** `01-g1-remembered-sets-and-write-barriers.md`

## Card 2

**Prompt:** Does G1 write-barrier/RSet cost scale with allocation volume or cross-region write volume?
**Answer:** Cross-region write volume — measured ~1,841x dirty-card difference between volume-matched low- and high-cross-region-write workloads.
**Why it matters:** Explains why pause times can grow while heap occupancy stays flat.
**Common trap:** Assuming more allocation always means more RSet/write-barrier cost.
**Related:** `01-g1-remembered-sets-and-write-barriers.md`

## Card 3

**Prompt:** What's the JDK 17+ log phase name for what used to be called "Update RS"?
**Answer:** "Merge Heap Roots" (with "Merged Cards" / "Scanned Cards" sub-metrics).
**Why it matters:** Citing pre-JDK-17 terminology in a version-aware interview reads as outdated.
**Common trap:** Using "Update RS"/"Scan RS" as if still current.
**Related:** `01-g1-remembered-sets-and-write-barriers.md`

## Card 4

**Prompt:** What makes an object a "leak" in Java specifically, versus a native-language leak?
**Answer:** It's still reachable from a GC root — an accidental reference, not missing/dangling memory.
**Why it matters:** Explains why "add more heap" never fixes a real leak, only delays the OOM.
**Common trap:** Treating a Java leak as a memory-accounting bug rather than a reference-graph bug.
**Related:** `02-memory-leak-diagnosis-and-heap-dump-analysis.md`

## Card 5

**Prompt:** What does `:live` add to `jmap -histo:live` that plain `jmap -histo` lacks?
**Answer:** It forces a GC before counting, so the histogram reflects genuinely-reachable objects, not garbage that just hasn't been collected yet.
**Why it matters:** Without it, a single sample can look identical for a real leak and a not-yet-collected batch of garbage.
**Common trap:** Using plain `jmap -histo` and trusting a single sample as leak evidence.
**Related:** `02-memory-leak-diagnosis-and-heap-dump-analysis.md`

## Card 6

**Prompt:** How do you distinguish a real leak from a warming cache using histogram sampling?
**Answer:** Sample 3+ times spaced apart — a real leak's count never plateaus; a warming cache's count does.
**Why it matters:** The most common false-positive in leak diagnosis.
**Common trap:** Concluding "leak" from just two samples showing growth.
**Related:** `02-memory-leak-diagnosis-and-heap-dump-analysis.md`

## Card 7

**Prompt:** Does `-Xmx` control total JVM memory usage?
**Answer:** No — only the heap. Metaspace, thread stacks, and the JIT code cache are all sized independently.
**Why it matters:** Explains why `StackOverflowError` and metaspace OOM are unaffected by raising `-Xmx`.
**Common trap:** Treating `-Xmx` as a universal memory dial.
**Related:** `03-jvm-memory-layout-and-runtime-regions.md`

## Card 8

**Prompt:** What replaced PermGen in Java 8, and what changed?
**Answer:** Metaspace — native-memory-backed rather than a fixed heap region, effectively unbounded unless `-XX:MaxMetaspaceSize` caps it.
**Why it matters:** A different error message (`Metaspace`, not `PermGen space`) and different default behavior.
**Common trap:** Citing "PermGen" as if it's still the current mechanism.
**Related:** `03-jvm-memory-layout-and-runtime-regions.md`

## Card 9

**Prompt:** Is thread-stack capacity affected by heap size?
**Answer:** No — measured directly, recursion depth scaled from 1,479 to 413,005 purely from changing `-Xss`, heap size held constant throughout.
**Why it matters:** `StackOverflowError` with plenty of free heap is expected behavior, not a contradiction.
**Common trap:** Proposing to raise `-Xmx` in response to a `StackOverflowError`.
**Related:** `03-jvm-memory-layout-and-runtime-regions.md`

## Card 10

**Prompt:** Does `Runtime.availableProcessors()` reflect the host's real core count or the container's CPU limit?
**Answer:** The container's cgroup CPU quota — measured directly, a 10-core host reported "2 available" or "6 available" depending on `--cpus`.
**Why it matters:** GC thread counts and default `ForkJoinPool` sizing scale off this number, so misunderstanding it has downstream effects.
**Common trap:** Assuming the JVM sees and uses the host's real core count inside a container.
**Related:** `04-jvm-flags-and-container-ergonomics.md`

## Card 11

**Prompt:** What percentage of detected container memory becomes the heap cap by default?
**Answer:** 25% (`-XX:MaxRAMPercentage`, default 25.0) — measured directly, a fixed 1GB container's heap cap scaled from 247MB to 742MB purely from raising the flag to 75.
**Why it matters:** The heap cap is a tunable percentage, not the container memory limit read directly.
**Common trap:** Expecting a doubled container memory limit to double the heap cap by a fixed absolute amount rather than proportionally.
**Related:** `04-jvm-flags-and-container-ergonomics.md`

## Card 12

**Prompt:** Why might GC pause behavior change after migrating a service from a fixed VM to a smaller-CPU-limit container, even with matched memory limits?
**Answer:** Container-aware ergonomics size GC thread counts off detected available CPUs — a smaller CPU limit means fewer GC threads for the same heap size, which can lengthen individual pauses.
**Why it matters:** This is correct, expected behavior, not a misconfiguration.
**Common trap:** Assuming GC pause behavior is purely a function of heap/memory configuration.
**Related:** `04-jvm-flags-and-container-ergonomics.md`

## Card 13

**Prompt:** What's the difference between "made not entrant" and a true deoptimization?
**Answer:** "Made not entrant" is routine — an older compiled version retired because a better-tier one exists. A true deoptimization is a runtime assumption (e.g., monomorphic dispatch) getting violated, forcing an in-flight bailout.
**Why it matters:** Most "made not entrant" log lines are not evidence of a problem.
**Common trap:** Treating every "made not entrant" line as a deoptimization.
**Related:** `05-jit-tiered-compilation-and-deoptimization.md`

## Card 14

**Prompt:** Why does speculative optimization exist if it can cause deoptimization?
**Answer:** It's the source of C2's biggest wins (e.g., inlining a call site assumed monomorphic) — the trade is common-case peak performance for a real but occasional recompilation cost when the assumption is violated.
**Why it matters:** Deoptimization is a necessary consequence of a good trade-off, not a bug.
**Common trap:** Treating deoptimization as evidence something is broken.
**Related:** `05-jit-tiered-compilation-and-deoptimization.md`

## Card 15

**Prompt:** Measured directly, roughly how much steady-state speedup did tiered JIT compilation give over pure interpretation (`-Xint`)?
**Answer:** ~9.6x (about 330 ns/op interpreted vs. ~34 ns/op tiered-compiled, same workload).
**Why it matters:** Quantifies "JIT warmup" as a real, measured effect rather than a vague claim.
**Common trap:** Citing "JIT warmup" without a concrete sense of the magnitude involved.
**Related:** `05-jit-tiered-compilation-and-deoptimization.md`
