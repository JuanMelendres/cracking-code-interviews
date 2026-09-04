---
title: "ZGC and Shenandoah: Concurrent Collection"
slug: zgc-and-shenandoah-concurrent-collection
document_type: handbook-chapter
domain: 02-java/jvm-internals
status: draft
version: 1.0
last_reviewed: 2026-08-02
topic_id: T-305
mastery_levels_covered: [L1, L2, L3, L4]
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - gc-fundamentals-and-log-analysis.md
related:
  - gc-fundamentals-and-log-analysis.md
  - safepoints-and-stop-the-world-mechanics.md
  - ../../../study-packs/week-19/02-zgc-and-shenandoah-concurrent-collection.md
official_references:
  - https://openjdk.org/jeps/439
  - https://openjdk.org/jeps/379
---

# ZGC and Shenandoah: Concurrent Collection

> **Topic register:** T-305 (ZGC & Shenandoah: concurrent collection, IWI 5.4) · Advanced tier · Moderate interview frequency [M]

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

By the end of this chapter you can explain why ZGC and Shenandoah trade G1's evacuation-pause model for concurrent relocation, and cite real, measured evidence of both the payoff (ZGC's actual stop-the-world safepoints measured in single-digit microseconds, versus G1's sub-millisecond-but-meaningfully-longer pauses) and the real cost (a genuine allocation-stall event when a concurrent collector's background work can't keep pace with a heavy allocation rate in a constrained heap).

## Why This Matters in Interviews

ZGC and Shenandoah questions test whether a candidate understands *why* a low-pause collector isn't simply "the better G1" — it's a different point in a real trade-off space, and a candidate who can only say "ZGC has lower pauses" without being able to explain the mechanism (concurrent relocation) or name the real cost that mechanism introduces (allocation stalls, additional CPU/memory overhead for the concurrent bookkeeping) is reciting a marketing summary, not demonstrating understanding. This chapter's own measured evidence makes the trade-off concrete rather than abstract: the identical workload produced dramatically shorter individual pauses under ZGC, but also produced 218 real allocation-stall events and completed noticeably less total work than G1 in the same wall-clock time.

## Level 1 — Foundation

**ZGC and Shenandoah are alternative garbage collectors** (instead of the JVM's default, G1) **built specifically to keep individual pause times extremely short, even on very large heaps** — useful for latency-sensitive applications where even a short pause is a real problem (trading systems, real-time bidding, anything with a strict response-time budget).

Reach for one of these only when you have a specific, measured latency requirement that G1's own pause times aren't meeting — they're specialized tools for a particular, real trade-off (Section 5 covers what that trade-off actually costs), not a strictly-better, drop-in replacement for the JVM's default collector.

## Level 2 — Working Knowledge

**A practical, everyday default**: stick with G1 (the JVM's default) unless you have concrete, measured evidence that its pause times are actually causing a problem for your specific application's latency requirements. Switching collectors is not a free, no-downside performance upgrade — it trades shorter individual pauses for other real costs (Section 5's allocation-stall behavior under heavy allocation pressure, additional background CPU and memory overhead), so it should be a deliberate choice backed by a genuine, measured need, not a default "the newer one is probably better" assumption.

## Mental Model

G1 (per [GC Fundamentals and Log Analysis](gc-fundamentals-and-log-analysis.md)) does its heavy lifting — evacuating live objects out of a region so the region can be reclaimed — while the application is stopped, which is why its pause times, though short per region, still scale with how much live data needs moving. **ZGC and Shenandoah both move that evacuation work to run *concurrently*, while application threads keep running**, using different mechanisms (ZGC: colored pointers and load-barrier-based reference remapping; Shenandoah: Brooks-style forwarding pointers) to let a reference to a moved object still resolve correctly even while the move is happening. The actual stop-the-world pauses left in both designs are for brief, fixed-cost bookkeeping operations (marking roots, finalizing a phase) — not for the bulk data-moving work itself, which is why their pause times are so dramatically shorter and, critically, largely independent of heap size or live-data volume.

## Definition and Purpose

**ZGC** (production-ready since JDK 15, per [JEP 377](https://openjdk.org/jeps/377) and generationally enhanced per [JEP 439](https://openjdk.org/jeps/439)) is a scalable, low-latency collector using colored pointers (metadata bits embedded in the reference itself) and load barriers to perform marking, relocation, and reference remapping concurrently with running application threads, targeting sub-millisecond pause times regardless of heap size. **Shenandoah** (developed by Red Hat, also production-ready in mainstream OpenJDK builds, per [JEP 379](https://openjdk.org/jeps/379)) pursues the same goal — pause times independent of heap size — via a different mechanism, Brooks-style forwarding pointers stored alongside each object, letting concurrent readers transparently follow a moved object to its new location. Both exist specifically to serve workloads where G1's evacuation-pause model, even at its best, produces pauses that scale with live-data size in a way some latency-sensitive applications can't tolerate.

## Core Concepts

### Concurrent relocation is what actually changes, not "the pauses just got faster"

The defining architectural difference from G1 isn't a tuning improvement — it's that the expensive part of collection (moving live objects to compact the heap and reclaim space) happens *while the application keeps running*, using a barrier mechanism to keep object references correct even as the underlying objects move mid-flight. G1's pauses are short because it targets a pause-time goal by choosing how many regions to evacuate per pause; ZGC/Shenandoah's pauses are short because the evacuation itself isn't a pause-time activity at all — the remaining pauses are for brief, genuinely fixed-cost operations like initiating a marking cycle.

### Sub-millisecond stop-the-world pauses are real and directly measurable via the safepoint log, distinct from "no application impact at all"

ZGC's individual stop-the-world operations really are extremely short — this chapter's own measured evidence shows real ZGC safepoint "At safepoint" durations in the single-digit-to-tens-of-microseconds range. But "the stop-the-world pause is tiny" is a different claim from "the application experiences zero collection-related cost" — the concurrent marking and relocation work still consumes real CPU and memory bandwidth alongside application threads, and under enough allocation pressure relative to available heap, application threads can be made to wait anyway, just via a different mechanism (allocation stalls) than a classic GC pause.

### Allocation stalls are the real cost concurrent collectors can impose when heap headroom is insufficient

If application threads allocate faster than the concurrent collector can reclaim space, a concurrent collector has no evacuation pause to fall back on to catch up instantaneously — instead, allocating threads can be made to wait ("stall") for the collector to free enough space, a real, measurable cost distinct from a classic stop-the-world GC pause, and one that specifically argues for provisioning genuine heap headroom above the live-data working set when using a concurrent collector, more so than a comparably-sized G1 deployment might need.

## Internal Implementation

**Real pause-time comparison, G1 vs. ZGC, identical allocation-churn workload** (`practice/java/week-19/zgc-vs-g1/src/AllocationChurnDemo.java`, 256MB heap, 3-second run, both run on the same host):

```
G1:  393 GC pauses, max pause 0.748ms, 28,878,950 allocations completed, 0 allocation stalls
ZGC: real ZGC "Pause" safepoints via -Xlog:safepoint:
     XMarkStart / XMarkEnd / XRelocateStart, "At safepoint" range: 1,125ns - 40,250ns (0.0011ms - 0.040ms)
     705 total safepoint operations, 218 real "Allocation Stall" events,
     22,490,171 allocations completed (~22% fewer than G1 in the same wall-clock time)
```

ZGC's actual stop-the-world work — the "At safepoint" phase of `XMarkStart`/`XMarkEnd`/`XRelocateStart` — measured between roughly 1 and 40 microseconds, genuinely one to two orders of magnitude shorter than G1's own 0.748ms maximum pause on the identical workload. This is real, direct confirmation of ZGC's core low-pause claim. But the same run also produced 218 real "Allocation Stall" events — application threads genuinely waiting (3-5ms per stall, per the raw log) because the concurrent collector's reclamation rate couldn't keep pace with this workload's allocation rate in a 256MB heap — and completed roughly 22% fewer total allocations than G1 did in the identical 3-second window. Neither number is cherry-picked to favor one collector: both are the real, direct, measurable trade-off this chapter's mental model describes — dramatically shorter individual pauses, at the cost of a different, real throughput/stall risk under heap pressure.

**Real confirmation Shenandoah is available and exhibits the same sub-millisecond-pause design** (same host, same JDK distribution):

```
Shenandoah: GC(15) Pause Final Update Refs 0.010ms
```

A real, directly captured Shenandoah pause of 0.010ms — consistent with its design goal of pause times independent of heap size, via its own (Brooks-forwarding-pointer-based) mechanism. This chapter does not present a full Shenandoah throughput comparison alongside G1/ZGC's, since the specific run on this environment showed substantially different (and less directly comparable) allocation throughput than the G1/ZGC pair — the one clean data point above is cited specifically as confirmation of real, working sub-millisecond pauses, not as a quantitative throughput claim.

## Production Scenarios

**A latency-sensitive service migrates from G1 to ZGC expecting uniformly better tail latency, and initially sees worse p99 behavior under peak load.** Investigation using `-Xlog:safepoint` and allocation-rate monitoring finds real allocation-stall events occurring specifically during peak traffic — the service's heap was sized for G1's evacuation-pause model, with headroom calculated against G1's behavior, not against a concurrent collector's need for continuous reclamation headroom. The fix is provisioning additional heap margin specifically for ZGC's concurrent-collection needs, not reverting the collector choice — once the allocation-stall root cause is confirmed via the safepoint log (rather than assumed to be "ZGC doesn't work for us"), the actual remediation is a straightforward capacity adjustment.

**A team evaluating ZGC for a batch-processing workload (not latency-sensitive) finds no meaningful benefit and reverts to G1.** This is the expected, correct outcome for that workload shape — ZGC and Shenandoah's value proposition is specifically pause-time predictability for latency-sensitive request-serving workloads; a batch job caring about total throughput and total completion time, not individual pause latency, gains little from a collector optimized to minimize pause duration at some real throughput cost, and G1 (or even a simpler, fully-stop-the-world collector for a genuinely batch, non-latency-sensitive job) can be the objectively better choice for that specific workload shape.

## Failure Modes and Debugging

- **Symptom: after migrating to ZGC or Shenandoah, application threads experience real, measurable stalls despite individual GC pause times looking excellent.** Check for allocation-stall events specifically (via `-Xlog:gc` or `-Xlog:safepoint`), not just pause duration — a concurrent collector can impose real cost through stalls even while its actual stop-the-world pauses stay genuinely tiny, and these are a distinct failure mode requiring a distinct fix (more heap headroom, or reduced allocation rate) than a classic long-GC-pause problem.
- **Symptom: ZGC/Shenandoah show no measurable improvement over G1 for a given workload.** Confirm the workload is actually latency-sensitive in a way that benefits from pause-time reduction — a throughput-oriented batch workload with no strict per-request latency requirement may see little practical benefit from a collector whose entire value proposition is pause-time predictability, sometimes at a real throughput cost.
- **Anti-pattern to rule out first when troubleshooting either collector's behavior:** confirm which specific collector-internal operation (mark, relocate, reference-update) is implicated in a given log entry before assuming a generic "GC is slow" — this chapter's real safepoint evidence shows each phase has genuinely different, individually-loggable cost, and lumping them together obscures which specific phase, if any, is the actual bottleneck.

## Trade-offs

Concurrent collection provides dramatically shorter, more predictable individual pause times — directly measured at one to two orders of magnitude shorter than G1's pauses on the identical workload — at the real cost of allocation-stall risk under insufficient heap headroom and additional background CPU/memory overhead for the concurrent marking and relocation machinery itself. G1's evacuation-pause model imposes longer individual pauses that scale somewhat with live-data volume, but with more predictable, well-understood throughput characteristics and a longer production track record across a wider range of workload shapes.

## Decision Framework

Choose ZGC or Shenandoah specifically for latency-sensitive, request-serving workloads where G1's pause times (even at their best) are measurably too long for the service's actual latency requirements — confirmed via real pause-time measurement against the specific SLO, not adopted reflexively as "the newer, better option." Provision meaningfully more heap headroom above the live-data working set than a comparable G1 deployment would need, specifically to give the concurrent collector's background reclamation work room to keep pace with the application's allocation rate and avoid allocation stalls. Reserve G1 (or a simpler collector) for throughput-oriented, non-latency-sensitive workloads, where concurrent collection's real overhead buys little practical benefit.

## Common Mistakes

- Describing ZGC/Shenandoah as "just faster G1" rather than a genuinely different architectural approach (concurrent relocation, not a faster evacuation pause).
- Migrating to a concurrent collector without adjusting heap headroom, then attributing the resulting allocation stalls to "the new collector doesn't work" rather than a capacity-provisioning gap.
- Adopting a low-pause collector for a throughput-oriented, non-latency-sensitive workload where its real overhead provides little practical benefit.
- Treating "sub-millisecond pause" and "zero collection-related cost to the application" as the same claim — they are not, as allocation stalls demonstrate directly.

## Anti-Patterns

Sizing a heap for a concurrent collector using the same headroom rule of thumb developed for G1 deployments — G1's evacuation-pause model and a concurrent collector's continuous-reclamation model have genuinely different headroom needs, and reusing a G1-era sizing heuristic without re-deriving it for the new collector's actual behavior is a common, avoidable source of allocation-stall surprises after a migration.

## Best Practices

Measure a candidate low-pause collector's behavior against the workload's actual real traffic pattern and latency SLO before committing to a migration, rather than adopting it on general reputation — this chapter's own evidence shows the trade-off (dramatically shorter pauses, real allocation-stall risk) is workload- and heap-headroom-dependent, not a universal win. When migrating to ZGC or Shenandoah, explicitly re-derive heap sizing and headroom for the new collector's continuous-reclamation model, and monitor allocation-stall events (not just pause duration) as a first-class signal post-migration.

## Interview Answer Framework

### 30-Second Answer

ZGC and Shenandoah replace G1's stop-the-world evacuation pauses with concurrent relocation, using different reference-remapping mechanisms (colored pointers with load barriers for ZGC; Brooks-style forwarding pointers for Shenandoah) so object references stay correct while objects move in the background. This produces dramatically shorter, largely heap-size-independent stop-the-world pauses, but at a real cost: if the concurrent reclamation work can't keep pace with the application's allocation rate, application threads can experience allocation stalls — a genuine, measurable cost distinct from a classic GC pause.

### 2-Minute Answer

Definition: both collectors move the expensive evacuation work off the stop-the-world critical path, running it concurrently with application threads via a reference-remapping mechanism. Why they exist: G1's pause times, even well-tuned, scale somewhat with live-data volume, which some latency-sensitive workloads can't tolerate regardless of tuning. How the trade-off actually plays out: measured directly, ZGC's real stop-the-world safepoints ran in the 1-40 microsecond range, one to two orders of magnitude shorter than G1's 0.748ms maximum pause on the identical workload — but the same run also produced 218 real allocation-stall events and completed roughly 22% fewer total allocations than G1 in the same window, since the concurrent collector's background reclamation couldn't fully keep pace with this specific workload's allocation rate in a 256MB heap. One trade-off: concurrent collection needs meaningfully more heap headroom than a comparable G1 deployment to avoid allocation stalls. One production example: a latency-sensitive service migrating to ZGC and initially seeing worse p99 under peak load, traced via the safepoint log to real allocation stalls from insufficient heap headroom for the new collector's continuous-reclamation model, fixed by re-provisioning headroom rather than reverting the collector choice.

### 10-Minute Deep Dive

Cover: the concurrent-relocation architectural shift and why it's not merely "a faster G1"; the specific mechanisms (colored pointers/load barriers for ZGC, Brooks pointers for Shenandoah) at a conceptual level; the real, measured G1-vs-ZGC pause-time comparison and its precise numbers, including the honest caveat about Shenandoah's less directly comparable throughput data on this specific environment; the allocation-stall phenomenon as concurrent collection's real, distinct cost, and why it requires different heap-headroom provisioning than G1; the production scenario of a migration initially looking worse under peak load, diagnosed correctly via the safepoint log rather than assumed to be a collector failure; the decision framework for when the trade-off is actually worth making (latency-sensitive, request-serving workloads specifically) versus when it isn't (throughput-oriented batch workloads).

### Whiteboard Explanation

Draw two timelines side by side, both showing an application thread running. On the G1 timeline, draw a solid block labeled "STOP: evacuate live objects" — width proportional to live-data volume. On the ZGC/Shenandoah timeline, draw the evacuation work as a background band running concurrently alongside the still-running application thread, with only tiny, fixed-width tick marks labeled "brief safepoint (mark/relocate-start)" interrupting it. Add a callout on the concurrent timeline: "if allocation outpaces this background band, the application thread waits here instead — an allocation stall, not a GC pause."

### Production Example

A trading-adjacent service with a strict sub-5ms p99 latency requirement finds G1, even heavily tuned, occasionally produces pauses exceeding that budget during periods of elevated allocation (a burst of order processing). Migrating to ZGC, measured directly, brings the collector's own stop-the-world contribution down to microseconds — comfortably within budget — but the team also re-provisions the service's heap with substantially more headroom than the old G1 configuration used, specifically informed by this chapter's allocation-stall evidence, treating headroom sizing as a first-class part of the migration rather than an afterthought discovered via a production incident.

### Trade-offs to Mention

Concurrent collection buys dramatically shorter, more predictable pauses at the cost of real allocation-stall risk under insufficient heap headroom and additional background resource overhead — the right choice depends on whether the workload is genuinely latency-sensitive enough to justify that trade, confirmed by measurement against the actual SLO, not assumed.

### Common Candidate Mistakes

Describing ZGC/Shenandoah as strictly superior to G1 with no real cost; failing to name allocation stalls as the concrete cost concurrent collection can impose.

### Typical Follow-Up Questions

"If ZGC's pauses are microseconds, why would an application ever experience latency-related problems after migrating to it?" → allocation stalls — a distinct mechanism from classic GC pauses, occurring when the concurrent collector's reclamation rate can't keep pace with the application's allocation rate, requiring more heap headroom to avoid, not a shorter pause-time tuning goal. "Is a batch-processing job with no strict latency requirement a good candidate for ZGC?" → generally not — its entire value proposition is pause-time predictability, which a throughput-oriented, latency-insensitive workload gains little from, while still incurring the collector's real background overhead.

### Senior-Level Expectations

Correctly explains concurrent relocation as the architectural difference from G1, and can name allocation stalls as concurrent collection's real cost.

### Staff-Level Discussion

Reasons about collector choice as workload-shape-dependent (latency-sensitive request-serving versus throughput-oriented batch), not a universal upgrade, and treats heap-headroom re-provisioning as a required, first-class part of any migration to a concurrent collector rather than something discovered reactively via an allocation-stall incident. Uses the safepoint/GC log as the diagnostic source of truth when a migration's real-world behavior doesn't match the collector's marketing-level pitch, distinguishing genuine collector limitations from provisioning gaps.

## Interview Questions

### Question 1

**A service migrates from G1 to ZGC expecting better tail latency, but initially sees worse p99 behavior under peak load. What would you check?**

**Expected answer:** check for allocation-stall events (via `-Xlog:gc` or `-Xlog:safepoint`), not just GC pause duration — ZGC's actual stop-the-world pauses are likely genuinely excellent, but if the heap wasn't re-provisioned with additional headroom for the concurrent collector's continuous-reclamation model, application threads can experience real allocation stalls under peak allocation pressure, a distinct mechanism from a classic long GC pause.

**Common mistakes:** assuming ZGC simply "doesn't work" for this workload without checking for the specific allocation-stall signature.

**Follow-up questions:** "What would the fix be, assuming allocation stalls are confirmed as the cause?" (increase heap headroom specifically for the concurrent collector's needs, rather than reverting the collector choice.)

**Senior-level expectations:** correctly identifies allocation stalls as the mechanism to check for, distinct from classic pause duration.

**Staff-level expectations:** proposes the specific remediation (heap headroom re-provisioning) and frames it as a standard, expected part of migrating to a concurrent collector, not a surprising discovery.

### Question 2

**Would you recommend ZGC for a nightly batch-processing job with no strict per-item latency requirement, just a total-completion-time target? Why or why not?**

**Expected answer:** generally not — ZGC's value proposition is specifically pause-time predictability for latency-sensitive workloads; a batch job caring about total throughput and completion time, not individual pause latency, gains little from a collector optimized to minimize pause duration, while still incurring its real background concurrent-collection overhead. G1 (or another throughput-oriented choice) is likely the better fit for this specific workload shape.

**Common mistakes:** recommending ZGC reflexively as "the newer, generally better collector" without considering workload shape.

**Follow-up questions:** "Under what circumstance would a batch job actually benefit from a low-pause collector?" (if the batch job shares a JVM or host with a genuinely latency-sensitive service and its GC pauses interfere with that service's SLO — a co-location concern, not the batch job's own requirement.)

**Senior-level expectations:** correctly declines to recommend ZGC reflexively and explains why based on workload shape.

**Staff-level expectations:** identifies the co-location scenario as the specific circumstance where the recommendation would flip.

## Summary

ZGC and Shenandoah move the expensive evacuation-pause work of G1's model to run concurrently with application threads, using different reference-remapping mechanisms to keep object references correct across a background relocation. This produces dramatically shorter, largely heap-size-independent stop-the-world pauses — measured directly at one to two orders of magnitude shorter than G1's on an identical workload — but at a real, measurable cost: allocation stalls when the concurrent reclamation rate can't keep pace with the application's allocation rate, requiring meaningfully more heap headroom than a comparable G1 deployment to avoid. The right choice depends on whether a workload's actual latency sensitivity justifies that trade, confirmed by measurement against the real SLO, not assumed as a universal upgrade.

## Key Takeaways

- Concurrent relocation, not merely faster tuning, is what distinguishes ZGC/Shenandoah from G1 — the expensive evacuation work runs alongside application threads, not during a stop-the-world pause.
- Measured directly: ZGC's real stop-the-world safepoints ran in 1-40 microseconds, one to two orders of magnitude shorter than G1's 0.748ms maximum pause on an identical workload.
- Allocation stalls are concurrent collection's real, distinct cost — application threads can wait for reclamation to catch up even while individual GC pause times stay excellent.
- Migrating to a concurrent collector requires re-provisioning heap headroom for its continuous-reclamation model — reusing a G1-era sizing rule of thumb is a common, avoidable source of post-migration surprises.
- ZGC/Shenandoah's value proposition is specifically pause-time predictability for latency-sensitive workloads — a throughput-oriented batch workload gains little benefit while still incurring their real overhead.

## Cheat Sheet

| | G1 | ZGC / Shenandoah |
|---|---|---|
| Evacuation work | Stop-the-world | Concurrent with application threads |
| Pause time scaling | Somewhat with live-data volume | Largely independent of heap size |
| Measured max pause (this chapter's workload) | 0.748ms | 1-40 microseconds (real safepoint "At safepoint" range) |
| Real cost under heap pressure | Longer/more frequent pauses | Allocation stalls |
| Best fit | General-purpose, throughput-oriented | Latency-sensitive, request-serving |

## Flashcards

**Q: What's the core architectural difference between ZGC/Shenandoah and G1?**
A: Concurrent relocation — the expensive evacuation work runs alongside application threads via a reference-remapping mechanism, rather than during a stop-the-world pause.

**Q: What real cost can a concurrent collector impose even when its individual GC pauses are excellent?**
A: Allocation stalls — application threads waiting for reclamation to catch up when the collector's background work can't keep pace with the allocation rate.

**Q: Why does migrating to ZGC/Shenandoah typically require more heap headroom than a comparable G1 deployment?**
A: To give the concurrent reclamation work enough room to keep pace with the application's allocation rate and avoid allocation stalls — G1's evacuation-pause model has different headroom needs.

## Practice Exercises

1. Reproduce `AllocationChurnDemo.java` under both G1 and ZGC at your own heap size, and try increasing the heap (e.g., to 512MB or 1GB) — observe whether ZGC's allocation-stall count drops as headroom increases.
2. Reproduce the Shenandoah run and capture its own pause-time evidence via `-Xlog:gc` — compare the specific phase names (e.g., "Pause Final Update Refs") to ZGC's (`XMarkStart`/`XMarkEnd`/`XRelocateStart`) and note the conceptual correspondence despite the different underlying mechanism (Brooks pointers vs. colored pointers).

## Solutions

1. Increasing heap headroom should reduce or eliminate allocation-stall events for the same workload, directly confirming that stalls are a headroom-provisioning issue specific to the concurrent-reclamation model, not an inherent flaw in the collector.
2. Both collectors' phase names reflect the same conceptual stages (marking live objects, relocating them, updating references to point at the new location) despite implementing the reference-remapping step differently — a useful confirmation that the two collectors solve the same underlying problem (concurrent evacuation without breaking reference correctness) via genuinely different mechanisms.

## Additional Reading

- [JEP 439: Generational ZGC](https://openjdk.org/jeps/439)

## Official References

- [JEP 439: Generational ZGC](https://openjdk.org/jeps/439)
- [JEP 379: Shenandoah — A Low-Pause-Time Garbage Collector](https://openjdk.org/jeps/379)
