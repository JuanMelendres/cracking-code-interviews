---
title: "Cheat Sheet: Data Partitioning and Consistent Hashing"
slug: data-partitioning-and-consistent-hashing
document_type: cheat-sheet
domain: system-design
topic_id: T-806
canonical: ../handbook/system-design/data-partitioning-and-consistent-hashing.md
last_updated: 2026-08-03
---

# Data Partitioning and Consistent Hashing

**Canonical chapter:** [`syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md`](../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md)

## Core Mental Model

A hash-based partitioning scheme is only as good as how little it disturbs when the node count changes — and the naive approach disturbs almost everything. `hash(key) % N` ties every key's assignment to the *current* value of N, so changing N by one changes nearly every key's remainder. Consistent hashing breaks that coupling: nodes and keys both map onto a fixed ring, and a key is owned by the first node found walking clockwise from it — independent of node count.

## Essential Definitions

- **Consistent hashing** — maps nodes and keys onto the same ring; adding/removing a node only affects keys immediately adjacent to it on the ring, because no other key's ring position changed.
- **Virtual nodes** — mapping each physical node to many ring points (150 in the chapter's demo) instead of one; the law of large numbers pulls each physical node's owned arc length close to an even `1/N` share.
- **Directory-based mapping** — an explicit lookup table of key→node; full control and easy rebalancing, but the directory itself becomes a bottleneck.

## Decision Table

| Node-count change | Naive `hash % N` | Consistent hashing |
|---|---|---|
| Remove 1 of 10 nodes | ~90%+ of keys remap | ~10% of keys remap |
| Add 1 node to 10 | Similarly disruptive | ~1/11 of keys remap to the new node |

**Trade-offs:**

| Choice | Benefit | Cost |
|---|---|---|
| `hash(key) % N` | Trivial, O(1) | Remaps nearly everything on any N change (measured 92.5%) |
| Consistent hashing, 1 point/node | Only the affected node's keys move | Uneven load distribution |
| Consistent hashing, many virtual nodes | Even distribution, close to ideal (measured 9.2% vs. 10% ideal) | More memory, more hash computations |
| Directory-based mapping | Full control, easy rebalancing | Directory becomes a bottleneck |

## Key Numbers (real, executed — `ConsistentHashingDemo.java`, 10,000 keys, 10 nodes)

```
Naive hash % N:            removed 1 of 10 nodes -> 9247/10000 keys (92.5%) remapped
Consistent hashing (150vn): removed 1 of 10 nodes ->  920/10000 keys (9.2%) remapped
                             (theoretical ideal ~10.0%)
```

Ring implementation: a `TreeMap` with 1,500 entries for just 10 physical nodes (150 virtual nodes × 10).

## Common Pitfalls

- Believing resharding/rebalancing is routine and cheap regardless of the hashing scheme in use
- Too few virtual nodes per physical node → surprised by uneven load
- Conflating "consistent hashing minimizes rebalancing cost" with "consistent hashing prevents hot keys" — different problems, different fixes

## Interview Answer Skeleton

**30-sec:** Naive `hash % N` remaps nearly all keys on any node-count change (measured 92.5% for removing 1 of 10). Consistent hashing bounds remapping to ~1/N (measured 9.2%, close to the 10% ideal). Virtual nodes are required for even load distribution.

**2-min:** Add why it exists (rebalancing cost, not just lookup cost) + the ring mechanism + the single-point-per-node vs. virtual-node trade-off + the 92.5%-vs-9.2% measured gap as the concrete justification.

**Whiteboard:** Draw the ring, place a key, arrow clockwise to its node. Erase one node, redraw — only the adjacent keys move, everything else stays put.

**Staff-level framing:** the 92.5%-vs-9.2% gap is why DynamoDB, Cassandra, and most CDN/load-balancer designs default to consistent hashing — a number worth having memorized and being able to derive from first principles. Consistent hashing does NOT fix hot keys from a monotonic key; that needs a compound key, which trades away efficient range queries (mirrors the Kafka partition-key chapter).

## Production Warning Signs

- **Real incident pattern:** a cache cluster scales 10→11 nodes using naive `hash(key) % N`; cache hit-rate drops near-total immediately after scaling, origin database takes a severe sustained overload — a scaling operation triggers exactly the outage it was meant to prevent.
- Diagnosis path: rule out node misconfiguration and added-capacity-causing-more-DB-traffic before landing on mass cache-key remapping (cache-stampede-like effect from the divisor change).
- Prevention: review any hash-based node-selection scheme specifically for its behavior on node-count change, not just steady-state lookup cost.

## Related

- [Distributed Transactions: Saga and Outbox](distributed-transactions-saga-and-outbox.md)
- `syllabus/09-messaging-event-driven/producer-semantics-and-partition-keys.md`
- [Table Partitioning and Sharding Strategies](table-partitioning-and-sharding-strategies.md)
