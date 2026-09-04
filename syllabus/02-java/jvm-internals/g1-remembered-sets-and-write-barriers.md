---
title: "G1 Internals: Remembered Sets and Write Barriers"
slug: g1-remembered-sets-and-write-barriers
document_type: handbook-chapter
domain: 02-java/jvm-internals
status: draft
version: 1.0
last_reviewed: 2026-07-31
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
  - jvm-memory-layout-and-runtime-regions.md
  - ../../../study-packs/week-16/01-g1-remembered-sets-and-write-barriers.md
official_references:
  - https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html
---

# G1 Internals: Remembered Sets and Write Barriers

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

By the end of this chapter you can explain, from first principles, why G1 can collect one region without scanning the entire heap, name the two mechanisms (write barriers, remembered sets) that make that possible, and cite real measured card-table activity showing the cost is tied to cross-region reference-write volume, not allocation volume.

## Why This Matters in Interviews

"Why is G1 a regional collector and how does it avoid a full-heap scan on every pause" is a standard follow-up once a candidate states they understand G1 collects "the regions with the most garbage first" (`gc-fundamentals-and-log-analysis.md` §Core Concepts). Most candidates can name that fact but cannot explain the mechanism that makes it *safe* — a region can only be collected in isolation if the collector already knows every reference pointing into it from every other region, without re-scanning the whole heap to find them. That's what remembered sets are for, and write barriers are how they stay accurate in real time. This is a genuine Senior/Staff differentiator: naming "remembered sets" without being able to explain what dirties them, or why the cost scales with a specific kind of write rather than with heap size, reads as memorized vocabulary rather than understanding.

## Level 1 — Foundation

**G1 (the JVM's default garbage collector) divides the heap into many smaller regions and can clean up a subset of them at a time, rather than scanning the whole heap on every pause** — this chapter is about the specific bookkeeping trick that makes cleaning up just one region safe, without having to check the entire rest of the heap first.

This is genuinely background, internals-level knowledge for most working engineers — you don't configure or tune this mechanism directly. The value at this level is simply recognizing it exists: G1 doing "the regions with the most garbage first," a fact many engineers already know at a surface level, actually depends on this specific tracking machinery working correctly underneath.

## Level 2 — Working Knowledge

Since this mechanism isn't something you tune directly, the practical, everyday takeaway is a design-awareness one: **storing a reference from a long-lived object into another region — especially repeatedly, into an old, promoted data structure — has a real, ongoing bookkeeping cost beyond the memory the reference itself occupies** (Section 5 measures this directly). A large, frequently-updated cache or shared long-lived collection that's constantly rewritten with fresh cross-region references can meaningfully add to this bookkeeping overhead, which is worth knowing if you're ever investigating unexpectedly high GC-phase time in a service with this kind of access pattern.

## Mental Model

Think of every heap region as a locked room. To collect a room safely, G1 needs a complete guest list of everyone who has a claim on something inside it, without touring every other room in the building to check. Each region keeps its own guest list (its remembered set). Whenever code writes a reference from one room into another, a doorman posted at every write (the write barrier) notes down the crossing on a shared log (the card table). Periodically, that log is filed into the correct room's guest list. Collecting one room, then, is a matter of reading its own guest list — not searching every other room for connections to it.

## Definition and Purpose

A **remembered set (RSet)** is, per region, a data structure recording every reference into that region from objects located in *other* regions. A **write barrier** is a small piece of code the JIT inserts after every reference-field store, whose job is to detect when that store might have created (or removed) a cross-region reference and mark it for later RSet update. Together they let G1 evacuate a subset of regions during a pause without scanning the entire heap for incoming pointers — the specific mechanism that makes "collect the regions with the most garbage" (G1's defining strategy) tractable at pause-time-bounded latency.

## Core Concepts

### The card table is the write barrier's logbook, not the RSet itself

G1 divides the heap into fixed-size 512-byte **cards**. The write barrier doesn't update a remembered set directly on every store — that would be too expensive to run on every single mutation. Instead it cheaply marks ("dirties") the card containing the modified field. Dirtying a card is a single conditional store, not a data-structure update — this is what keeps the write barrier's per-mutation cost low enough to run on every reference write in the program, including ones that turn out not to matter (a write from region A to another object also in region A still dirties a card; that card is filtered out later without ever becoming an RSet entry).

### RSets are built by *merging* dirty cards, not by scanning live objects

At the start of each pause's "Merge Heap Roots" phase (JDK 17+ log terminology — older JDKs call this "Update RS"), G1 processes accumulated dirty cards and merges genuinely cross-region ones into the target region's remembered set. The subsequent "Scan Cards" work then walks only the objects those merged cards point at, to find their roots for evacuation. Neither step touches the live object graph of regions that weren't written to since the last pause — this is the entire efficiency argument for a two-phase (mark cheaply at write-time, resolve lazily at pause-time) design over updating RSets synchronously on every store.

### The cost is proportional to cross-region write volume, not to allocation volume or heap size

This is the fact most candidates get backwards. Allocating a million short-lived objects that are never stored into another region's field produces almost no card-table activity — a young object dying before promotion never accumulates a durable cross-region pointer. Conversely, a comparatively small number of writes into a long-lived, promoted array — each one storing a fresh reference into an old-generation slot — produces disproportionate dirty-card and RSet-scan activity, because *every one of those writes* is exactly the pattern write barriers exist to catch. §Internal Implementation below measures this directly.

## Internal Implementation

**Two real workloads, same heap, same iteration count order of magnitude — measured with `-Xlog:gc+phases=debug`:**

- **Low cross-region writes**: allocate 30M short-lived `int[8]` arrays in a tight loop; never write into any long-lived structure.
- **High cross-region writes**: allocate 30M boxed `Integer`s, storing each one into a slot of a 200,000-element array that was deliberately promoted to old-gen before the measured loop starts — every store is a fresh young-gen object written into an old-gen container, the canonical cross-region write pattern.

```java
// LOW: no durable cross-region pointer ever created
for (int i = 0; i < iterations; i++) {
    int[] shortLived = new int[8];
    shortLived[0] = i;
    sink += shortLived[0];
}

// HIGH: every iteration stores a fresh (young) object into a promoted (old) array slot
for (int i = 0; i < iterations; i++) {
    Integer freshObject = i;
    longLivedTable[i % longLivedTable.length] = freshObject; // write barrier fires here
}
```

**Real measured totals across all GC pauses in each run** (`practice/java/week-16/g1-remembered-sets/`, `-Xmx128m`, OpenJDK 21.0.12):

| Metric (summed across all pauses) | Low cross-region | High cross-region | Ratio |
|---|---|---|---|
| Young-collection pauses | 4 | 32 | 8x |
| Dirty Cards | 13 | 23,938 | ~1,841x |
| Scanned Cards | 13 | 23,929 | ~1,841x |
| Merged Cards | 64 | 1,211 | ~19x |

The 8x difference in pause count alone (more live data survives in the high scenario, forcing more frequent young collections) does not come close to explaining a ~1,841x difference in dirty/scanned cards — the card-table activity is driven by the *write pattern*, not the collection frequency. This is the direct, measured proof that RSet/write-barrier cost tracks cross-region reference writes specifically, not general allocation pressure.

**Reading the raw log line** (`-Xlog:gc+phases=debug`, JDK 17+ terminology):

```
GC(1)   Merge Heap Roots: 0.01ms
GC(1)     Prepare Merge Heap Roots: 0.00ms
GC(1)       Merged Cards       Min: 16, Avg: 16.0, Max: 16, Sum: 48, Workers: 3
GC(1)       Dirty Cards:       Min: 0,  Avg: 52.3, Max: 157, Sum: 157, Workers: 3
GC(1)       Scanned Cards:     Min: 28, Avg: 51.7, Max: 66,  Sum: 155, Workers: 3
GC(1)         Redirtied Cards: Min: 0,  Avg: 53.0, Max: 159, Sum: 159, Workers: 3
```

`Dirty Cards` is how many cards the write barriers marked since the last pause. `Merged Cards` is how many of those were folded into a target region's RSet (the rest were same-region writes, filtered out). `Scanned Cards` is how many card-referenced objects were walked to find roots. `Redirtied Cards` — cards marked dirty again *during* the pause itself, because the collector's own copying work can itself trigger further barrier-tracked writes.

## Production Scenarios

**A hot, frequently-mutated cache backed by a single large array or map, holding references to constantly-churning young objects, causes measurably longer G1 pauses than the live-data size alone would predict.** The mechanism is exactly the "high" scenario above: every cache update is a cross-region write, and RSet merge/scan work for that one hot region grows with mutation rate, not with the cache's own size. A team seeing "GC pauses got worse but our heap occupancy didn't" should suspect write-barrier/RSet pressure from a specific hot mutable structure before suspecting a general sizing problem — `gc+phases=debug` logging (as measured above) is the direct way to confirm it, by comparing `Merge Heap Roots` phase duration and `Scanned Cards` sums across pauses against overall heap growth.

## Failure Modes and Debugging

- **Symptom: pause times climb while heap occupancy stays flat.** Enable `-Xlog:gc+phases=debug`, sum `Dirty Cards` / `Scanned Cards` across a representative window, and correlate against deploys or feature flags that changed a hot mutable structure's update rate — not against allocation-rate metrics, which won't show this pattern.
- **Symptom: "Merge Heap Roots" phase dominates pause time in the detailed log breakdown.** This directly indicates RSet merge/scan cost, not evacuation-copy cost — the fix is reducing cross-region write volume into hot structures (e.g., partitioning a single giant cache array into per-shard arrays reduces the RSet fan-in on any one region), not resizing the heap.
- **Anti-pattern to rule out first:** confusing this with a straightforward allocation-rate problem (which shows up as more *frequent* pauses of similar individual cost) versus a write-barrier/RSet problem (which shows up as *longer individual pauses* with flat or modestly-increased frequency).

## Trade-offs

Write barriers impose a small, constant per-mutation cost on every reference store in the entire program, in exchange for making regional (rather than whole-heap) collection possible at all. A collector without write barriers and remembered sets would have to scan the entire heap for incoming references on every collection — trading a large, unavoidable per-pause cost for a tiny, near-invisible per-mutation cost is the correct trade for almost every workload, which is why every generational and regional collector (not just G1) uses some form of this mechanism (card tables, in G1's case; different structures in other collectors).

## Decision Framework

This is not a tunable trade-off a candidate is expected to configure directly (write barriers are not optional in G1) — the practical decision it informs is *diagnostic*: when GC pauses grow disproportionately to heap size, decide whether the cause is write-barrier/RSet pressure (check `Merge Heap Roots` / `Scanned Cards` in `gc+phases=debug` output) before reaching for heap-size or GC-algorithm changes that won't address a mutation-pattern problem.

## Common Mistakes

- Describing remembered sets without mentioning write barriers, or vice versa — they are two halves of one mechanism, not independent features.
- Assuming RSet/write-barrier cost scales with allocation volume or heap size, rather than with cross-region reference-write volume specifically.
- Treating "Update RS" / "Scan RS" as the current log terminology — JDK 17+ renamed these to card-table-centric names (`Merge Heap Roots`, `Merged Cards`, `Scanned Cards`) reflecting an internal implementation change; citing the old names in a version-aware interview reads as outdated knowledge.

## Anti-Patterns

Building a single giant, frequently-mutated shared structure (one big cache map, one big pending-work array) that every part of an application writes into indiscriminately — this concentrates cross-region write-barrier activity onto whichever regions currently hold it, producing exactly the "pauses grow without heap growing" symptom, and is avoidable by partitioning the hot structure so writes spread their card-dirtying load across more regions.

## Best Practices

Reach for `-Xlog:gc+phases=debug`'s `Merge Heap Roots` breakdown specifically (not just the summary pause line) whenever pause duration and heap occupancy diverge — it's the direct, real evidence for or against a write-barrier/RSet explanation, rather than guessing from aggregate metrics alone.

## Interview Answer Framework

### 30-Second Answer

G1 collects individual regions instead of the whole heap. To do that safely it needs to know, per region, every reference pointing in from other regions — that's the remembered set. Write barriers are the mechanism that keeps remembered sets accurate: every reference store is intercepted to mark ("dirty") the card it touched, and dirty cards get merged into the target region's RSet at the start of the next pause.

### 2-Minute Answer

Definition, then why it exists: G1's whole strategy is collecting the regions with the most garbage first, which requires evacuating individual regions in isolation. That's only safe if G1 already knows every incoming cross-region reference without scanning the entire heap to find them — remembered sets are that per-region incoming-reference record. How it works: write barriers dirty a card (not update the RSet directly) on every reference store; at the start of a pause, G1 merges genuinely cross-region dirty cards into RSets and scans them for roots. One trade-off: this imposes a small constant cost on every reference write in the program, in exchange for avoiding a full-heap scan per pause — worth it for essentially every real workload. One production example: a hot, frequently-mutated shared cache produces disproportionate RSet/write-barrier activity relative to its own size, because every update is a cross-region write — measured directly, a workload doing 30M cross-region writes produced ~1,841x more dirty-card activity than an equivalent-volume workload with no durable cross-region references.

### 10-Minute Deep Dive

Cover: the card-table mechanism as the write barrier's cheap logbook (not the RSet itself); the two-phase design (cheap mark at write-time, resolve lazily at pause-time) and why that split exists (updating RSets synchronously on every store would be too expensive to run on every mutation); the JDK 17+ terminology rename (`Update RS`/`Scan RS` → `Merge Heap Roots`/`Merged Cards`/`Scanned Cards`); the measured 1,841x dirty-card ratio between a low- and high-cross-region-write workload at similar iteration volume, and why that ratio — not the more modest 8x pause-count difference — is the real evidence the cost tracks write pattern specifically; the production diagnostic implication (pauses growing while heap occupancy stays flat → suspect a hot mutable structure, confirm via `gc+phases=debug`, fix by partitioning rather than resizing).

### Whiteboard Explanation

Draw three or four boxes as heap regions. In one region, draw an object with an arrow pointing into another region's object — label the arrow "cross-region reference." Draw a small "write barrier" gate at the point where that arrow's underlying store happens, feeding into a shared "card table" log. Draw an arrow from the card table into the *target* region's own small "remembered set" list, labeled "merged at next pause." Narrate: "to collect the target region alone, G1 only has to read its own RSet list — not search every other region for pointers into it — and the write barrier plus card table is what keeps that RSet list accurate without doing full work on every single mutation."

### Production Example

A trending-content service keeps one large in-memory array mapping content IDs to freshly-fetched metadata objects, refreshed continuously as new content trends. GC pauses grow noticeably worse during trending spikes even though total heap occupancy barely changes — every metadata refresh is a fresh (young) object written into the long-lived (promoted) array, a textbook cross-region write-barrier pattern. `gc+phases=debug` shows `Merge Heap Roots` dominating pause time. The fix is sharding the array by content-ID hash bucket across several smaller structures, spreading the card-dirtying load instead of concentrating it on the regions backing one giant array.

### Trade-offs to Mention

Write barriers add a small, constant, unavoidable per-mutation cost to every reference store in the JVM — accepted universally because the alternative (full-heap scans per pause) doesn't scale.

### Common Candidate Mistakes

Naming "remembered sets" as a vocabulary term without connecting it to write barriers or explaining what triggers RSet updates; assuming cost scales with allocation rate rather than cross-region write rate.

### Typical Follow-Up Questions

"What specifically dirties a card?" → any reference-field store, filtered later to genuinely cross-region ones. "Why not update the RSet directly on every store instead of a two-phase card-table design?" → too expensive to run synchronously on every mutation; the card table defers the expensive part to pause-time. "What production symptom points at this specifically, versus a plain sizing issue?" → pause duration growing while heap occupancy stays flat, confirmed via `Merge Heap Roots` / `Scanned Cards` in `gc+phases=debug`.

### Senior-Level Expectations

Correctly names both mechanisms and their relationship, and can state the measured-cost-driver distinction (write pattern, not allocation volume) when asked.

### Staff-Level Discussion

Recognizes this as a general pattern in regional/generational collector design (not G1-specific trivia) — any collector that wants to evacuate less than the whole heap per pause needs some equivalent of a write barrier plus a per-region incoming-reference record, and the specific implementation (card tables here) is an engineering choice about where to spend the fixed cost, not the only possible design. Connects the production diagnostic (partition hot mutable structures) to a broader principle: shared mutable state that's written from many places concentrates whatever bookkeeping cost the runtime pays for mutation, independent of which runtime or language is involved.

## Interview Questions

### Question 1

**Why can G1 collect a subset of regions without scanning the whole heap for incoming references, and what two mechanisms make that safe?**

**Expected answer:** remembered sets record cross-region incoming references per region; write barriers keep them accurate by dirtying cards on relevant stores, merged into RSets at pause-time.

**Common mistakes:** naming only one of the two mechanisms; describing the RSet as being updated synchronously on every store rather than via the two-phase card-table design.

**Follow-up questions:** "What's the actual per-mutation cost of a write barrier?" "Why defer RSet updates to pause-time instead of updating immediately?"

**Senior-level expectations:** correctly names and relates both mechanisms.

**Staff-level expectations:** explains the two-phase design as a deliberate cost-deferral choice, and generalizes it beyond G1.

### Question 2

**Your GC pause times have grown noticeably but heap occupancy hasn't. What do you suspect, and how would you confirm it?**

**Expected answer:** suspect write-barrier/RSet pressure from a hot, frequently-mutated cross-region structure; confirm via `-Xlog:gc+phases=debug`, checking `Merge Heap Roots` duration and `Scanned Cards`/`Dirty Cards` sums against heap growth.

**Common mistakes:** jumping straight to "increase the heap" or "tune the pause-time goal" without first distinguishing a sizing problem from a write-pattern problem.

**Follow-up questions:** "If confirmed, what's the actual fix?" (partition the hot structure, not resize the heap)

**Senior-level expectations:** names the diagnostic direction (mutation pattern vs. sizing) and the specific log evidence to check.

**Staff-level expectations:** proposes the structural fix (partitioning/sharding) and explains why resizing wouldn't address a write-pattern-driven cost.

## Summary

G1 collects individual regions by relying on remembered sets (per-region records of incoming cross-region references) kept accurate by write barriers (which cheaply dirty cards on relevant stores, merged into RSets at pause-time). The cost of this mechanism tracks cross-region reference-write volume specifically — measured directly, a workload with heavy cross-region writes produced ~1,841x more dirty-card activity than a volume-matched workload without them, while pause count only differed 8x. Production pause-time growth with flat heap occupancy is the diagnostic signature; the fix is reducing/partitioning cross-region write concentration, not resizing the heap.

## Key Takeaways

- Remembered sets (per-region incoming-reference records) plus write barriers (which keep them accurate) are what let G1 collect one region without scanning the whole heap.
- The card table is a cheap intermediate log, not the RSet itself — write barriers dirty cards; RSets are built by merging dirty cards at pause-time.
- Cost scales with cross-region reference-write volume, not allocation volume or heap size — measured 1,841x dirty-card difference on volume-matched workloads.
- JDK 17+ renamed the log terminology from "Update RS"/"Scan RS" to "Merge Heap Roots"/"Merged Cards"/"Scanned Cards".
- Pause time growing while heap occupancy stays flat is the production signature of write-barrier/RSet pressure; fix by partitioning the hot mutable structure, not by resizing the heap.

## Cheat Sheet

| Concept | One-line definition |
|---|---|
| Remembered Set (RSet) | Per-region record of incoming references from other regions |
| Write barrier | Code inserted after reference stores to dirty the affected card |
| Card | 512-byte heap chunk; the unit the write barrier marks |
| Merge Heap Roots | Pause-time phase merging dirty cards into RSets (JDK 17+ name for "Update RS") |
| Diagnostic signal | Pause time up, heap occupancy flat → suspect write-barrier/RSet pressure |
| Fix | Partition/shard the hot mutable structure; don't resize the heap |

## Flashcards

**Q: What two mechanisms let G1 collect one region without scanning the whole heap?**
A: Remembered sets (per-region incoming-reference records) and write barriers (which keep them accurate).

**Q: Does RSet/write-barrier cost scale with allocation volume or cross-region write volume?**
A: Cross-region write volume — measured ~1,841x dirty-card difference between volume-matched low/high cross-region-write workloads.

**Q: What's the JDK 17+ log phase name for what used to be called "Update RS"?**
A: "Merge Heap Roots" (with "Merged Cards" / "Scanned Cards" sub-metrics).

## Practice Exercises

1. Reproduce `practice/java/week-16/g1-remembered-sets/` yourself with a different heap size and iteration count. Confirm the dirty-card ratio between scenarios stays disproportionate to the pause-count ratio.
2. Modify the "high" scenario to write into 10 separate smaller arrays (round-robin) instead of one 200,000-element array. Predict, then measure, whether `Merge Heap Roots` phase duration per pause changes.

## Solutions

1. The ratio should remain far larger than the pause-count ratio across reasonable heap sizes — the mechanism is not heap-size-dependent, only write-pattern-dependent.
2. Spreading writes across more (smaller) long-lived structures spreads card-dirtying across more regions' RSets rather than concentrating it, which should reduce the per-pause `Merge Heap Roots` cost concentrated on any single region, illustrating the partitioning fix from §Failure Modes and Debugging directly.

## Additional Reading

- [OpenJDK Wiki — G1 Garbage Collector](https://wiki.openjdk.org/display/HotSpot/G1+Garbage+Collector)

## Official References

- [Oracle — G1 Garbage Collector tuning guide](https://docs.oracle.com/en/java/javase/21/gctuning/garbage-first-g1-garbage-collector1.html)
