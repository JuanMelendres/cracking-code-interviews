---
title: "Cheat Sheet: G1 Internals: Remembered Sets and Write Barriers"
slug: g1-remembered-sets-and-write-barriers
document_type: cheat-sheet
domain: jvm
topic_id: T-304
canonical: ../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md
last_updated: 2026-09-12
---

# G1 Internals: Remembered Sets and Write Barriers

**Canonical chapter:** [`syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md`](../syllabus/02-java/jvm-internals/g1-remembered-sets-and-write-barriers.md)

## Mental Model

Every heap region is a locked room. To collect a room safely, G1 needs a complete guest list of everyone with a claim on something inside it, without touring every other room in the building. Each region keeps its own guest list (its remembered set). Whenever code writes a reference from one room into another, a doorman posted at every write (the write barrier) notes the crossing on a shared log (the card table). That log is periodically filed into the correct room's guest list — collecting one room is then a matter of reading its own guest list, not searching every other room.

## Decision Table

| Concept | One-line definition |
|---|---|
| Remembered Set (RSet) | Per-region record of incoming references from other regions |
| Write barrier | Code inserted after reference stores to dirty the affected card |
| Card | 512-byte heap chunk; the unit the write barrier marks |
| Merge Heap Roots | Pause-time phase merging dirty cards into RSets (JDK 17+ name for "Update RS") |
| Diagnostic signal | Pause time up, heap occupancy flat → suspect write-barrier/RSet pressure |
| Fix | Partition/shard the hot mutable structure; don't resize the heap |

## Decision Framework

Not a tunable a candidate configures directly (write barriers aren't optional in G1) — the practical decision is diagnostic: when GC pauses grow disproportionately to heap size, check `Merge Heap Roots` / `Scanned Cards` in `gc+phases=debug` output for write-barrier/RSet pressure before reaching for heap-size or GC-algorithm changes that won't address a mutation-pattern problem.

## Common Pitfalls

- Describing remembered sets without mentioning write barriers, or vice versa — two halves of one mechanism.
- Assuming RSet/write-barrier cost scales with allocation volume or heap size, rather than cross-region reference-write volume specifically.
- Citing "Update RS" / "Scan RS" as current terminology — JDK 17+ renamed these to card-table-centric names (`Merge Heap Roots`, `Merged Cards`, `Scanned Cards`).

## Interview Answer Skeleton

30 seconds: G1 avoids whole-heap scans per region collection via remembered sets (incoming-reference records) kept accurate by write barriers on every cross-region store. 2 minutes: add the card-table mechanism and the JDK 17+ terminology shift. Deep dive: walk the diagnostic signal — pause time growing while heap occupancy stays flat points at write-barrier/RSet pressure from a hot mutable structure, fixed by sharding it, not by resizing the heap.

## Production Warning Signs

- GC pause time climbing while heap occupancy stays flat — check cross-region write volume before touching heap size.
- A single hot, frequently-mutated shared structure spanning many regions — a common source of RSet/write-barrier pressure.
- Log analysis citing outdated "Update RS"/"Scan RS" phase names against a JDK 17+ runtime.

## Related

- [JVM Memory Layout and Runtime Regions](../syllabus/02-java/jvm-internals/jvm-memory-layout-and-runtime-regions.md)
- [GC Fundamentals and Log Analysis](../syllabus/02-java/jvm-internals/gc-fundamentals-and-log-analysis.md)
