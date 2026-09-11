---
title: "Cheat Sheet: Consensus Algorithms — Raft and Paxos"
slug: consensus-algorithms-raft-and-paxos
document_type: cheat-sheet
domain: 10-distributed-systems
topic_id: T-2403
canonical: ../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md
last_updated: 2026-09-11
---

# Consensus Algorithms: Raft and Paxos

**Canonical chapter:** [`syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md`](../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md)

## Core Mental Model

One number: the majority threshold, `⌊N/2⌋ + 1`. Every consensus guarantee (no two leaders in the same term, no lost committed entry) reduces to the arithmetic fact that two disjoint groups can't both be a majority of the same total.

## Essential Definitions

- **Majority threshold** — `⌊N/2⌋ + 1`; the number of votes a candidate needs to become leader.
- **Term** — a monotonically increasing election epoch; a stale leader steps down on seeing a higher term.
- **Split vote** — no candidate reaches majority in a term; resolved by retrying in a new term.

## Decision Table

| Question | Answer |
|---|---|
| Why does majority voting prevent two leaders? | Two disjoint majorities of the same fixed group can't both exist — mathematically, not by convention |
| What resolves a stale leader after reconnecting? | Sees a higher term, immediately steps down to follower |
| What happens to a partition's minority side? | Can never reach majority threshold while partitioned — correctly, deliberately unavailable |
| Raft vs. Paxos — different guarantee? | No — identical guarantee; Raft designed to be more understandable, not more correct |
| Odd or even cluster size? | Odd — an even count adds a node without improving fault tolerance |

## Common Pitfalls

- Assuming a partition's minority side can eventually elect a leader by retrying enough times — it structurally cannot, by the majority-arithmetic argument.
- Believing Paxos and Raft provide different consistency guarantees — they're equivalent; Raft only differs in understandability.
- Sizing a cluster with an even node count, gaining no additional fault tolerance for the extra node.

## Interview Answer Skeleton

**30-sec:** Consensus safety reduces to majority arithmetic (`⌊N/2⌋+1`) — two disjoint majorities of the same group can't coexist, which is why only one leader can exist per term and a partition's minority side can never elect one.

**2-min:** Add: a real deterministic simulation proves a partition's minority side caps out short of quorum no matter how many retries, a genuine split vote resolves cleanly next term, and a stale leader steps down the instant it sees a higher term — all direct arithmetic consequences, not separately-coded rules.

**Staff-level framing:** Choosing Raft over Paxos is an understandability/implementability decision, not a correctness or performance one — both provide the identical safety guarantee.

## Related

- syllabus/10-distributed-systems/vector-clocks-and-quorum-based-replication.md
- syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md
