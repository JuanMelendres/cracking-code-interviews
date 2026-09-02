---
title: "Cheat Sheet: CopyOnWriteArrayList and Copy-on-Write Trade-offs"
slug: copyonwritearraylist-and-copy-on-write-tradeoffs
document_type: cheat-sheet
domain: collections
topic_id: T-206
canonical: ../handbook/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md
last_updated: 2026-09-02
---

# CopyOnWriteArrayList and Copy-on-Write Trade-offs

**Canonical chapter:** [`handbook/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md`](../handbook/collections/copyonwritearraylist-and-copy-on-write-tradeoffs.md)

## Core Mental Model

Every write throws away the old array and builds an entirely new one — reads never coordinate with writes because they're always just reading one, complete, immutable array reference via a plain `volatile` read.

## Essential Definitions

- **Write path** — allocate a new array, copy every existing element, apply the one change, atomically swap the `volatile` reference. Real O(n) cost, every time, even for `set(index, value)`.
- **Read path** — read the current `volatile` array reference (no lock), index directly. Always sees one complete, consistent array.
- **Design intent** — a deliberate trade-off matching read-heavy, write-rare workloads (listener lists, rarely-changing config snapshots) — not a general-purpose thread-safe list.

## Decision Table

| Question | Answer |
|---|---|
| Read far more often than written (listener lists, config snapshots)? | `CopyOnWriteArrayList` — real, measured trade-off matches |
| Written frequently, or roughly as often as read? | Actively the wrong choice — O(n) write cost dominates |
| Readers need a stable, fixed snapshot during iteration? | `CopyOnWriteArrayList`'s iterator gives exactly that |
| Large collection with non-trivial write frequency? | Measure real per-write cost at actual expected size before adopting |

## Key Numbers

- Write cost scales with size: 0.37µs (1K elements) → 82.33µs (500K elements) — real ~222x cost increase for ~500x size increase, consistent with O(n).
- `ArrayList` write cost stays flat (~0.03-0.11µs) across the same size range — real O(1) amortized.
- 8 threads × 2,000,000 reads, zero writers: `CopyOnWriteArrayList` 13ms vs `Collections.synchronizedList` 577ms — real ~44.38x faster reads.

## Common Pitfalls

- Choosing `CopyOnWriteArrayList` for a write-heavy or write-moderate workload — real O(n) write cost dominates.
- Assuming `Collections.synchronizedList()` is a safer/"more standard" default without recognizing its read-path lock cost, even with zero writers.
- Forgetting `set(index, value)` still copies the *entire* array, not just the changed slot.

## Interview Answer Skeleton

**30-sec:** Every write copies the entire backing array — real, measured O(n) cost — so reads are entirely lock-free, needing only a `volatile` reference read. Measured: write cost scales with size; reads measured ~44x faster than `synchronizedList`'s lock-serialized reads under concurrency. Right choice specifically for read-heavy, write-rare collections.

**2-min:** Add the real production regression: swapping `CopyOnWriteArrayList` for `synchronizedList` "for consistency" on a hot listener-dispatch path introduced real, measurable lock contention on every read — reverted once profiled.

**Whiteboard:** Write path (allocate → copy all N → apply change → swap) beside read path (read volatile reference → index directly, no lock). Annotate "O(n), every time" and "lock-free, every time."

**Staff-level framing:** Any design making the common case cheap by making the rare case expensive (copy-on-write snapshots, append-only logs, versioned immutable structures) is only a good trade if the "rare" assumption genuinely holds — and that can silently become false as a system evolves. Treat such trade-offs as requiring periodic re-validation, not a one-time decision.

## Production Warning Signs

- Event-dispatch latency increases after a refactor swaps `CopyOnWriteArrayList` for `Collections.synchronizedList()` on a rarely-mutated, frequently-iterated listener registry — profiling shows lock contention on every dispatch.

## Related

- `handbook/collections/concurrenthashmap-internals.md`
- `handbook/collections/collection-selection-decision-matrix.md`
- `handbook/collections/fail-fast-vs-weakly-consistent-iterators.md`
