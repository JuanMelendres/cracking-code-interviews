---
title: "T-806 · Data Partitioning & Consistent Hashing"
topic_id: T-806
domain: DistributedData
tier: Staff
iwi: 7.70
prerequisites: [T-614]
unlocks: []
week: 10
last_reviewed: 2026-07-29
---

# T-806 · Data Partitioning & Consistent Hashing

**IWI 7.70 · Staff tier**

**Verification note:** the redistribution percentages in §3 are real, executed output from `practice/java/week-10/consistent-hashing/src/ConsistentHashingDemo.java` — 10,000 real keys, 10 real nodes, one real removal, measured directly, not approximated.

## Table of Contents

1. [The concept](#1-the-concept)
2. [Why it exists](#2-why-it-exists)
3. [Naive hash % N vs consistent hashing, measured](#3-naive-hash--n-vs-consistent-hashing-measured)
4. [Virtual nodes: why 150, not 1](#4-virtual-nodes-why-150-not-1)
5. [Trade-offs](#5-trade-offs)
6. [Interview questions](#6-interview-questions)
7. [Common mistakes](#7-common-mistakes)
8. [Staff-level discussion](#8-staff-level-discussion)
9. [Summary](#9-summary)
10. [Key Takeaways](#10-key-takeaways)
11. [Cheat Sheet](#11-cheat-sheet)
12. [Flashcards](#12-flashcards)
13. [Practice Exercises](#13-practice-exercises)
14. [Additional Reading](#14-additional-reading)
15. [Official References](#15-official-references)

---

## 1. The concept

Consistent hashing maps both nodes and keys onto the same abstract ring (via a hash function), and assigns each key to the first node found walking clockwise from the key's position. Unlike `hash(key) % N`, adding or removing a node only affects the keys immediately adjacent to that node on the ring — every other key's assignment is completely unaffected, because nothing about their position on the ring changed.

## 2. Why it exists

Any system that distributes data or load across nodes by hashing needs to handle nodes being added or removed (scaling, failure, maintenance) — the question this topic answers is precisely how much data has to move when that happens, and the naive answer (`hash % N`) is bad enough to matter directly, measured below.

## 3. Naive hash % N vs consistent hashing, measured

**Real output**, 10,000 keys, 10 nodes, removing 1 node:

```
== naive hash % N ==
removed 1 of 10 nodes: 9247 of 10000 keys (92.5%) remapped to a different node
(theoretical worst case for hash%N on ANY node-count change: nearly ALL keys remap, because N itself changed, and every key's slot is k.hashCode() % N)

== consistent hashing with 150 virtual nodes per physical node ==
removed 1 of 10 nodes: 920 of 10000 keys (9.2%) remapped to a different node
(theoretical ideal for removing 1 of 10 nodes: ~10.0% -- only that node's own keys should move, to neighbors on the ring)
```

**92.5% vs 9.2% — for removing exactly one node out of ten.** The naive scheme's number isn't a bug in this particular run; it's the mathematically expected outcome of `k.hashCode() % N` when `N` changes from 10 to 9 — nearly every key's modulus result changes, because the divisor itself changed. Consistent hashing's 9.2% is close to the theoretical ideal of exactly `1/10 = 10%` (only the removed node's own keys need to move, to their nearest neighbors on the ring) — this is the entire value proposition of the technique, demonstrated as a real number, not asserted.

## 4. Virtual nodes: why 150, not 1

Mapping each physical node to a single point on the ring produces uneven load distribution in practice — with only 10 points on a ring, the arc length each node "owns" varies significantly by chance, so some nodes end up responsible for far more keys than others. **Virtual nodes** solve this by mapping each physical node to many points on the ring (150 here) instead of one; with 1,500 total points instead of 10, the law of large numbers makes each physical node's total owned arc length converge much closer to an even `1/10` share, which is why the measured 9.2% above lands so close to the theoretical 10% ideal rather than drifting far from it. More virtual nodes per physical node improves this evenness further, at the cost of more ring lookups' worth of memory (the ring here is a `TreeMap` with 1,500 entries for just 10 nodes).

## 5. Trade-offs

| Choice | Benefit | Cost |
|---|---|---|
| `hash(key) % N` | Trivial to implement, O(1) lookup | Any node-count change remaps nearly everything — measured 92.5% here |
| Consistent hashing, 1 point/node | Only affected node's keys move | Uneven load distribution across nodes in practice |
| Consistent hashing, many virtual nodes/node | Even load distribution, close to theoretical ideal (measured 9.2% vs 10% ideal) | More memory for the ring structure; more hash computations per node |
| Directory-based (explicit key→node mapping table) | Full control over placement, easy rebalancing logic | The directory itself becomes a scaling and availability bottleneck |

## 6. Interview questions

### Q1. Add a node — how much data moves?

- **Expected answer:** with naive `hash % N`, effectively all of it (measured 92.5% for a REMOVAL of similar magnitude here; addition is comparably disruptive). With consistent hashing, only the new node's eventual share (~`1/N` of the total) — measured at 9.2%, close to the 10% ideal.
- **Common mistakes:** assuming this cost is fixed regardless of the hashing scheme used.
- **Follow-up questions:** "Why isn't the measured 9.2% exactly 10%?"
- **Senior-level expectations:** correctly names consistent hashing and states the approximate `1/N` cost.
- **Staff-level expectations:** explains the virtual-node mechanism as the reason the measured number approaches the ideal rather than drifting from it, and can reason about the load-distribution-vs-memory trade-off in choosing how many virtual nodes to use.

### Q2. Your shard key is the timestamp. What breaks?

- **Expected answer:** all recent writes hash to nearby points on the ring (or the same range partition), creating a hot node/partition — the opposite problem from key-count redistribution, but related: a hashing/partitioning scheme solves *rebalancing* cost, not *hot-key* distribution, and a monotonically increasing key like a timestamp defeats even a well-distributed hash if consecutive values aren't spread apart by the hash function's output range.
- **Common mistakes:** conflating "consistent hashing solves rebalancing cost" with "consistent hashing solves hot keys" — they're different problems.
- **Follow-up questions:** "How would you fix it while keeping time-based queries efficient?"
- **Senior-level expectations:** identifies the hot-node risk from a monotonic key.
- **Staff-level expectations:** proposes a compound key (timestamp + a distributing prefix) as the fix, and names the trade-off it reintroduces (range queries across the full time span now require fanning out across the distributing prefix's full space) — the same shape of trade-off named in Week 8's Kafka partition-key chapter for a single hot customer.

## 7. Common mistakes

- Believing resharding/rebalancing is a routine, cheap operation regardless of the hashing scheme in use — the blueprint's own named misconception for this topic.
- Using consistent hashing with too few virtual nodes per physical node and being surprised by uneven load.
- Conflating "consistent hashing minimizes rebalancing cost" with "consistent hashing prevents hot keys" — they solve different problems.

## 8. Staff-level discussion

The 92.5%-vs-9.2% gap measured here is the concrete justification for why every major distributed data system (DynamoDB, Cassandra, most CDN/load-balancer designs) uses consistent hashing or a close variant rather than naive modulo hashing — at real scale, "nearly everything moves" on every scaling event isn't just inefficient, it's often operationally infeasible (the data-movement traffic itself can saturate the network during the rebalance). A Staff engineer treats this measured ratio as one of the concrete numbers worth having memorized precisely because it's the kind of thing an interviewer can ask you to derive or explain from first principles — not "name consistent hashing" but "explain why the naive scheme is this much worse, quantitatively."

## 9. Summary

Naive `hash % N` remaps the overwhelming majority of keys on any node-count change — measured at 92.5% for removing 1 of 10 nodes here, matching the mathematically expected near-total-remap outcome. Consistent hashing with virtual nodes measured at 9.2%, close to the theoretical 10% ideal, because only the removed node's own ring positions are affected and virtual nodes keep the load evenly distributed enough that the measured number tracks the theoretical minimum closely.

## 10. Key Takeaways

- Naive `hash % N` remaps nearly all keys on any node-count change — this is mathematically expected, not a rare edge case.
- Consistent hashing bounds remapping to roughly `1/N` of the keys.
- Virtual nodes exist to fix load-distribution unevenness from too few ring points, not to fix the rebalancing-cost problem itself.
- Consistent hashing solves rebalancing cost; it does not by itself solve hot-key/hot-node problems from a poorly chosen key.

## 11. Cheat Sheet

| Node-count change scenario | Naive hash % N | Consistent hashing |
|---|---|---|
| Remove 1 of 10 nodes | ~90%+ of keys remap | ~10% of keys remap |
| Add 1 node to 10 | Similarly disruptive | ~1/11 of keys remap to the new node |

## 12. Flashcards

1. **Q: Why does naive `hash(key) % N` remap nearly all keys when N changes?** A: Because the divisor itself changed — nearly every key's modulus result is different, mathematically expected, not a rare edge case (measured 92.5% here).
2. **Q: What fraction of keys should move when removing 1 of N nodes under consistent hashing?** A: Roughly `1/N` — measured at 9.2% for N=10, close to the 10% theoretical ideal.
3. **Q: Why use many virtual nodes per physical node instead of one?** A: One point per node gives uneven load distribution by chance; many virtual nodes converge each physical node's share closer to an even `1/N`.

(Full week-level deck: `07-flashcards.md`.)

## 13. Practice Exercises

1. Reproduce: `practice/java/week-10/consistent-hashing/src/ConsistentHashingDemo.java`.
2. Rerun with `VIRTUAL_NODES_PER_NODE = 1` instead of 150 and observe how far the measured redistribution percentage drifts from the 10% ideal — quantify the load-distribution cost of too few virtual nodes.
3. Design a compound key for the "shard key is the timestamp" scenario from §6 Q2, and state precisely which query patterns keep working efficiently and which don't.

## 14. Additional Reading

- [Karger et al. — Consistent Hashing and Random Trees (1997), the original paper](https://www.akamai.com/site/en/documents/technical-publication/consistent-hashing-and-random-trees-distributed-caching-protocols-for-relieving-hot-spots-on-the-world-wide-web-technical-publication.pdf)

## 15. Official References

- [Amazon DynamoDB paper (2007) §4.2 — Partitioning](https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf) — a production system built directly on consistent hashing with virtual nodes
