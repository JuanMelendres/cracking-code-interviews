---
title: "Cheat Sheet: Vector Clocks and Quorum-Based Replication"
slug: vector-clocks-and-quorum-based-replication
document_type: cheat-sheet
domain: 10-distributed-systems
topic_id: T-2407
canonical: ../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md
last_updated: 2026-09-11
---

# Vector Clocks and Quorum-Based Replication

**Canonical chapter:** [`syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md`](../syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md)

## Core Mental Model

A vector clock answers "is X causally before, after, or independent of Y?" via per-replica write counts, no synchronized clocks needed. A read/write quorum answers "how many replicas must overlap?" via simple counting arithmetic. Neither prevents divergence — both make it *detectable and boundable*, the realistic goal for an AP system.

## Essential Definitions

- **`DOMINATES`** — one version's vector clock causally supersedes another's.
- **`CONCURRENT`** — neither version's vector clock dominates the other — a genuine, undetected-by-timestamps conflict.
- **`W + R > N`** — the quorum-overlap rule guaranteeing a read sees the latest acknowledged write.

## Decision Table

| Need | Mechanism |
|---|---|
| Detect version X causally supersedes version Y | Vector clock comparison: `DOMINATES` |
| Detect a genuine concurrent write conflict | Vector clock comparison: `CONCURRENT` |
| Guarantee a read sees the latest acknowledged write | `W + R > N` |
| A common, unsafe misconception | `W + R = N` "feels" safe but structurally is not |
| Stay available when a home replica is briefly unreachable | Sloppy quorum + hinted handoff |
| Resolve a detected vector-clock conflict | Application-specific merge (e.g., set union) — not automatic |

## Common Pitfalls

- Believing `W + R = N` is "close enough" to `W + R > N` — a real, named counterexample shows a zero-overlap quorum pair exists at exactly `W + R = N`.
- Assuming a vector clock automatically resolves a `CONCURRENT` conflict — it only detects it; resolution is an application-specific merge.
- Confusing wall-clock timestamps with vector clocks — timestamps can't distinguish "later" from "concurrent, on a different replica."

## Interview Answer Skeleton

**30-sec:** Vector clocks detect causal ordering versus genuine conflict without synchronized clocks; quorum overlap (`W + R > N`) guarantees a read sees the latest write — neither prevents divergence, both make it detectable/boundable.

**2-min:** Add: a real, exhaustive enumeration of every write/read-quorum pair for a 5-replica set proves `W+R>N` holds in all 100 cases while `W+R=N` yields a real, named zero-overlap counterexample — direct, rigorous proof `W+R=N` is genuinely insufficient.

**Staff-level framing:** These are the mechanisms an AP system (per CAP) uses to make an explicit trade — availability over strict consistency — survivable and reasoned-about, not evidence that consistency doesn't matter.

## Related

- syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md
- syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md
