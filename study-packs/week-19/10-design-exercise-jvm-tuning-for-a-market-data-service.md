---
title: "Design Exercise — JVM Tuning Playbook for a Market-Data Service"
week: 19
document_type: study-pack-design-exercise
status: draft
last_reviewed: 2026-08-02
---

# Design Exercise — JVM Tuning Playbook for a Market-Data Service

**Format:** 45 minutes, whiteboard or written. Produce a JVM tuning playbook for the service below, applying all six of this week's topics explicitly.

## The scenario

Your team owns a market-data distribution service: it ingests a high-volume stream of price ticks, maintains an in-memory symbol-to-latest-price cache (currently backed by a `WeakHashMap`, on the assumption this keeps memory bounded automatically), parses each tick using small, short-lived value objects, and publishes updates to subscribers over a socket connection using NIO. The service currently runs on G1 with a container memory limit set exactly equal to `-Xmx`. Leadership wants sub-millisecond p99 publish latency, and a recent capacity review flagged the service's actual memory usage as "higher than expected" without further explanation.

## Design this

1. **The `WeakHashMap` cache:** Is this the right tool for a "keep memory bounded" price cache? What would you check, and what might you recommend instead?
2. **Container memory limit:** Given it's currently set equal to `-Xmx`, what's your concern, and what would you check before the next capacity review?
3. **Sub-millisecond p99 latency requirement:** Is G1 the right collector choice? What would you measure before recommending a change?
4. **Tick-parsing value objects:** Small, short-lived objects created per incoming tick. Do you expect these to be a real GC pressure concern? How would you find out?
5. **NIO publish path:** The service publishes over sockets using NIO. What would you check about how buffers are used here?
6. **An unexplained latency blip:** Ops reports a periodic ~2ms blip with nothing in the GC logs at that timestamp. What would you check?

Work through your answer before reading the reference solution below.

---

## Reference Solution

**1. The `WeakHashMap` cache.** Likely the wrong tool (`01-gc-roots-reachability-and-reference-strength.md`): `WeakHashMap` clears entries immediately once a key becomes otherwise unreachable, with no memory-pressure consideration at all — it's not a "keep memory bounded, evict under pressure" mechanism, and if any part of the code incidentally holds a strong reference to symbol keys elsewhere, entries may persist indefinitely regardless of the map's "weak" name, producing exactly the unpredictable behavior a naive reading of "weak = bounded" wouldn't expect. Recommend a `SoftReference`-backed structure (genuinely pressure-aware) or, more robustly, a purpose-built bounded cache with an explicit eviction policy and size limit — not relying on either reference-strength type as an implicit capacity-management mechanism.

**2. Container memory limit.** `-Xmx` never bounded the process's total memory usage (`05-native-memory-direct-buffers-and-off-heap.md`) — thread stacks, metaspace, code cache, and (given the NIO publish path) very possibly direct-buffer memory all live outside it. Before the next capacity review, run the service under load with `-XX:NativeMemoryTracking=summary` and capture a `jcmd VM.native_memory summary` snapshot, specifically checking the `Other` category for direct-buffer usage from the NIO layer — this is very likely a meaningful, currently-unaccounted contributor to the "higher than expected" memory usage the capacity review flagged.

**3. Collector choice for sub-millisecond p99.** G1's pause times, even well-tuned, scale somewhat with live-data volume (`02-zgc-and-shenandoah-concurrent-collection.md`) — worth measuring G1's actual current p99 GC-attributable latency against the sub-millisecond target before assuming a change is needed, but if G1 genuinely can't meet it, ZGC is the natural next step given its real, measured microsecond-range stop-the-world pauses. Critically: any ZGC migration must come with a re-provisioned heap headroom, informed by real allocation-stall monitoring during a load test, not a straight collector swap with the existing G1-era heap sizing — otherwise the service risks trading GC pauses for allocation stalls, a different but comparably real latency cost.

**4. Tick-parsing value objects.** Likely not a real concern, and worth confirming before "optimizing" (`06-escape-analysis-and-scalar-replacement.md`): if these value objects are created, used to extract primitive fields, and discarded within the parsing method with no reference surviving (never stored, never returned as the object itself, never passed to an un-inlined call), the JIT's escape analysis very likely eliminates the allocation entirely once the hot parsing path is compiled — exactly this week's own zero-vs-362-GC-pause evidence. Confirm via a real GC-pause-count comparison with `-XX:-DoEscapeAnalysis` against the default, the same technique this week's chapter demonstrates, rather than assuming manual pooling is needed without measuring first.

**5. NIO publish path.** Confirm the publish path uses direct `ByteBuffer`s specifically for the socket-write operations (`05-native-memory-direct-buffers-and-off-heap.md`) — this is exactly the I/O-specific scenario where direct buffers provide a real benefit (eliminating a heap-to-native copy before the OS-level write), and if it's already doing so, that's very likely the source of the direct-memory usage flagged in item 2 above, not a bug — it needs its own explicit `-XX:MaxDirectMemorySize` budget and NMT-informed monitoring, not removal.

**6. Unexplained 2ms blip, no GC log entry.** Check `-Xlog:safepoint`, not just `-Xlog:gc` (`03-safepoints-and-stop-the-world-mechanics.md`) — the blip is very plausibly a real, non-GC safepoint operation (a periodic monitoring agent's thread dump, a deoptimization from a polymorphic call site somewhere in the publish path, or similar), invisible to GC-log-only monitoring. Once the safepoint log identifies the specific operation, the investigation redirects to that operation's actual trigger (auditing monitoring-tool cadence, or investigating a deoptimization's cause per the JIT chapter) rather than continuing to search GC logs for a cause that isn't there.

## Self-Check

- [ ] Correctly identified the `WeakHashMap` cache as unsuited for pressure-aware bounded caching, and proposed a specific, better alternative
- [ ] Connected the "higher than expected" memory finding to `-Xmx` not bounding total process memory, and proposed NMT specifically as the diagnostic tool
- [ ] Treated collector choice (G1 vs. ZGC) as a measurement-driven decision, and explicitly named heap-headroom re-provisioning as a required part of any ZGC migration, not an afterthought
- [ ] Proposed measuring (not assuming) whether the tick-parsing objects are a real GC concern, naming the specific `-XX:-DoEscapeAnalysis` comparison technique
- [ ] Connected the NIO publish path to direct-buffer usage as a plausible, expected (not necessarily buggy) source of off-heap memory, requiring its own explicit budget
- [ ] Proposed checking `-Xlog:safepoint` specifically for the unexplained blip, not just continuing to search GC logs
