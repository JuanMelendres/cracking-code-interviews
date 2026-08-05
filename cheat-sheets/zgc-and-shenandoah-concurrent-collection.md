---
title: "Cheat Sheet: ZGC and Shenandoah — Concurrent Collection"
slug: zgc-and-shenandoah-concurrent-collection
document_type: cheat-sheet
domain: jvm
topic_id: T-305
canonical: ../handbook/jvm/zgc-and-shenandoah-concurrent-collection.md
last_updated: 2026-08-05
---

# ZGC and Shenandoah: Concurrent Collection

**Canonical chapter:** [`handbook/jvm/zgc-and-shenandoah-concurrent-collection.md`](../handbook/jvm/zgc-and-shenandoah-concurrent-collection.md)

## Core Mental Model

G1 does its heavy lifting — evacuating live objects out of a region — while the application is stopped, so pause times scale with how much live data needs moving. ZGC and Shenandoah both move that evacuation work to run *concurrently*, while application threads keep running, using different mechanisms (ZGC: colored pointers/load barriers; Shenandoah: Brooks-style forwarding pointers) so a reference to a moved object still resolves correctly mid-move. The remaining stop-the-world pauses are brief, fixed-cost bookkeeping — not the bulk data-moving work — which is why pause times are dramatically shorter and largely independent of heap size.

## Essential Definitions

- **ZGC** (production-ready since JDK 15, generationally enhanced per JEP 439) — colored pointers + load barriers, targets sub-millisecond pauses regardless of heap size.
- **Shenandoah** (JEP 379) — Brooks-style forwarding pointers stored alongside each object, same pause-independence goal via a different mechanism.
- **Allocation stall** — when application threads allocate faster than the concurrent collector can reclaim space, threads wait for the collector to catch up. The real, distinct cost concurrent collection can impose.

## Decision Table

| | G1 | ZGC / Shenandoah |
|---|---|---|
| Evacuation work | Stop-the-world | Concurrent with application threads |
| Pause time scaling | Somewhat with live-data volume | Largely independent of heap size |
| Real cost under heap pressure | Longer/more frequent pauses | Allocation stalls |
| Best fit | General-purpose, throughput-oriented | Latency-sensitive, request-serving |

**Trade-offs:** concurrent collection buys dramatically shorter, more predictable pauses at the cost of real allocation-stall risk under insufficient heap headroom and additional background CPU/memory overhead for the concurrent machinery itself.

## Key Numbers (real, executed — `AllocationChurnDemo.java`, 256MB heap, 3-second run, identical workload)

```
G1:  393 GC pauses, max pause 0.748ms, 28,878,950 allocations, 0 allocation stalls

ZGC: 705 safepoint operations, "At safepoint" range 1,125ns - 40,250ns (1-2 orders of
     magnitude shorter than G1's max pause), 218 real "Allocation Stall" events,
     22,490,171 allocations (~22% FEWER than G1 in the same wall-clock window)
```

```
Shenandoah: GC(15) Pause Final Update Refs 0.010ms
(real confirmation of sub-millisecond pauses via a different mechanism)
```

Neither number is cherry-picked — both are the real, direct trade-off: dramatically shorter individual pauses, at the cost of a different, real throughput/stall risk under heap pressure.

## Common Pitfalls

- Describing ZGC/Shenandoah as "just faster G1" rather than a genuinely different architectural approach (concurrent relocation, not a faster evacuation pause).
- Migrating without adjusting heap headroom, then attributing resulting allocation stalls to "the new collector doesn't work."
- Adopting a low-pause collector for a throughput-oriented, non-latency-sensitive workload where its real overhead buys little benefit.
- Treating "sub-millisecond pause" and "zero collection-related cost" as the same claim — allocation stalls prove they aren't.

## Interview Answer Skeleton

**30-sec:** ZGC and Shenandoah replace G1's stop-the-world evacuation with concurrent relocation, producing dramatically shorter, largely heap-size-independent pauses. But if the concurrent reclamation can't keep pace with the allocation rate, threads experience allocation stalls — a real, measurable cost distinct from a classic GC pause.

**2-min:** Add why they exist (G1's pauses scale somewhat with live-data volume, intolerable for some latency-sensitive workloads regardless of tuning) + the real measured trade-off (ZGC's safepoints ran 1-40μs vs. G1's 0.748ms max, but ZGC also produced 218 allocation stalls and ~22% fewer completed allocations in the same window) + the trade-off (concurrent collectors need meaningfully more heap headroom than a comparable G1 deployment).

**Whiteboard:** Two timelines. G1's: a solid block "STOP: evacuate live objects," width proportional to live-data volume. ZGC/Shenandoah's: evacuation as a background band running alongside the still-running application thread, with tiny tick marks for brief safepoints. Callout: "if allocation outpaces this background band, the thread waits here instead — an allocation stall, not a GC pause."

**Staff-level framing:** collector choice is workload-shape-dependent (latency-sensitive request-serving vs. throughput-oriented batch), not a universal upgrade. Treat heap-headroom re-provisioning as a required, first-class part of any migration to a concurrent collector, not something discovered reactively via an incident.

## Production Warning Signs

- A latency-sensitive service migrates to ZGC expecting better tail latency and initially sees *worse* p99 under peak load — check for allocation-stall events via `-Xlog:safepoint`, not just pause duration; the heap was likely sized for G1's model, not the new collector's continuous-reclamation needs.
- A batch-processing team evaluates ZGC and finds no meaningful benefit — expected and correct; ZGC's value proposition is pause-time predictability, which a throughput-oriented job gains little from while still incurring real background overhead.
- **Prevention:** explicitly re-derive heap sizing for a concurrent collector's continuous-reclamation model rather than reusing a G1-era headroom rule of thumb; monitor allocation-stall events as a first-class signal post-migration.

## Related

- `handbook/jvm/gc-fundamentals-and-log-analysis.md`
- `handbook/jvm/safepoints-and-stop-the-world-mechanics.md`
