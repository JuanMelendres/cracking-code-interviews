---
title: "Design Exercise — JVM Sizing and Diagnostics Playbook for a Real-Time Pricing Service"
week: 16
document_type: study-pack-design-exercise
status: draft
last_reviewed: 2026-07-31
---

# Design Exercise — JVM Sizing and Diagnostics Playbook for a Real-Time Pricing Service

**Format:** 45 minutes, whiteboard or written. Produce a JVM sizing and diagnostics playbook for the service described below, applying all five of this week's topics explicitly.

## The scenario

Your team owns a real-time pricing service: it maintains a large, frequently-updated in-memory cache of current prices (updated on every market tick), evaluates a pricing strategy per incoming request (currently one implementation, but a second strategy is about to roll out behind a gradual flag), and runs on Kubernetes (EKS) with a CPU request/limit noticeably smaller than the team's old fixed-VM deployment. The team has been burned before by a memory leak that went undiagnosed for a week before someone thought to check, and wants a documented playbook this time, not tribal knowledge.

## Design this

1. **Region sizing:** How do you reason about heap vs. metaspace vs. thread-stack sizing for this service, and what's your first diagnostic step if any one of them is exhausted in production?
2. **Container ergonomics:** Given the smaller CPU limit than the old VM deployment, what change in GC behavior should the team expect, and is it a problem?
3. **G1 remembered-set awareness:** The price cache is exactly the "hot, frequently-mutated cross-region structure" pattern from this week's material. What do you watch for, and what's the mitigation if it becomes a real problem?
4. **Leak diagnosis runbook:** Write the exact, ordered steps someone should follow if memory looks like it's climbing — not "profile it," the actual commands and decision points.
5. **Deoptimization-aware rollout:** The second pricing strategy is rolling out behind a flag at a previously-monomorphic call site. What do you expect during rollout, and what would you do about it if the cost matters?

Work through your answer before reading the reference solution below.

---

## Reference Solution

**1. Region sizing.** Heap sized via the default `MaxRAMPercentage` ergonomic against the container's memory limit (per `04-jvm-flags-and-container-ergonomics.md`) unless a specific, measured reason exists to override it. Metaspace capped explicitly (`-XX:MaxMetaspaceSize`) at a conservative multiple of the class count actually loaded at steady state, specifically because this service's pricing-strategy flag rollout is exactly the kind of dynamic-class-generation-adjacent change (per `03-jvm-memory-layout-and-runtime-regions.md`) worth guarding against with an explicit cap rather than leaving metaspace unbounded. First diagnostic step on any region exhaustion: read the exact error message/region name (`Java heap space`, `Metaspace`, or `StackOverflowError`) before choosing a remediation — each points at a different region with a different fix, and none of them are fixed by adjusting a different region's flag.

**2. Container ergonomics.** Expect fewer GC threads and correspondingly different (likely somewhat longer) individual pause times than the old VM deployment, for the same heap size — this is the JVM correctly detecting the smaller CPU limit (per `04-jvm-flags-and-container-ergonomics.md`'s measured `CPUs: {total} total, {available} available` evidence) and sizing `ParallelGCThreads` accordingly, not a misconfiguration. Whether it's "a problem" depends on whether the new pause profile still meets the service's latency SLO — confirm via `-Xlog:gc+init`'s CPU-detection line before concluding anything is wrong, and only raise the CPU limit if the new pause profile genuinely fails the SLO.

**3. G1 remembered-set awareness.** The price cache, updated on every tick with fresh (young-gen) price objects written into a long-lived (old-gen, promoted) container, is precisely the cross-region write pattern this week's `01-g1-remembered-sets-and-write-barriers.md` measured producing disproportionate dirty-card/RSet activity (~1,841x versus a low-cross-region-write baseline in the chapter's demo). Watch for pause time growing while heap occupancy stays flat — the specific diagnostic signature — via `-Xlog:gc+phases=debug`'s `Merge Heap Roots` phase duration and `Dirty Cards`/`Scanned Cards` sums. If it becomes a real problem, the mitigation is partitioning the price cache (e.g., sharded by instrument symbol) rather than resizing the heap, spreading the card-dirtying load across more regions instead of concentrating it.

**4. Leak diagnosis runbook.**
```
1. Sample `jmap -histo:live <pid>` at least 3 times, spaced 10-30 minutes apart.
2. If one class's count grows across ALL samples with no plateau -> likely leak.
   If it plateaus -> likely a warming cache, not a leak; stop here.
3. Once a growing class is identified, capture a targeted heap dump:
   `jcmd <pid> GC.heap_dump /path/to/dump.hprof`
4. Open the dump in Eclipse MAT / VisualVM, find the "path to GC roots" for
   an instance of the growing class.
5. The path names the specific accidental reference (a missing unregister,
   an unbounded cache, a ThreadLocal never cleared) -- fix that reference,
   not "add more heap."
6. Confirm the fix: re-run steps 1-2 against the patched service and verify
   the previously-growing class's count now plateaus or stays flat.
```
Enable `-XX:+HeapDumpOnOutOfMemoryError` in production as a standing safety net, so an actual OOM captures the exact evidence needed without requiring a live reproduction.

**5. Deoptimization-aware rollout.** Expect a brief, real, one-time latency cost the moment the flag first exposes the second pricing strategy to the (previously monomorphic) call site — measured in this week's `05-jit-tiered-compilation-and-deoptimization.md` demo at roughly 2x on the first affected call versus the same call re-run after recompilation. If that one-time cost matters for this service's latency SLO during rollout, pre-warm a canary instance against BOTH pricing strategies (not just the currently-live one) before the flag reaches meaningful production traffic, so the deoptimization-and-recompilation cycle happens during controlled warmup rather than live rollout traffic.

## Self-Check

- [ ] Named the specific region (heap/metaspace/stack) implicated by each type of failure, not "memory" generically
- [ ] Connected the smaller container CPU limit explicitly to expected GC thread count and pause-time changes, and proposed confirming it via `-Xlog:gc+init` rather than assuming
- [ ] Identified the price cache as a G1 RSet-cost risk specifically, with the correct mitigation (partition/shard) rather than a generic "tune GC" answer
- [ ] Wrote a runbook with concrete commands and an explicit plateau-check step, not "profile it"
- [ ] Connected the flag rollout to deoptimization specifically, with a concrete pre-warming mitigation, not just "expect it to be a bit slow"
