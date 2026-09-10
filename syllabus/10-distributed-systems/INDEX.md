---
title: "Distributed Systems — Domain Index"
document_type: syllabus-domain-index
domain: 10-distributed-systems
status: 5 of 5 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4; 6th chapter added 2026-09-10 (Consensus Algorithms: Raft and Paxos, T-2403 — gap found in a full 22-domain audit)
last_updated: 2026-09-10
---

# Distributed Systems

The theory: CAP/PACELC, consistent hashing, replication, distributed transactions, and failure modes — "what is true about distributed systems," independent of any specific design exercise. Prerequisite to `11-system-design`, per the plan's Section 3.3 distinction.

> **Phase 3 update (2026-09-03).** This domain's full existing content (5 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 5 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a mailed-invitation-plus-text analogy for the outbox pattern and Saga, a classroom-locker-assignment analogy for naive modulo hashing vs. a ring for consistent hashing, a two-library-branches analogy for CAP, a personal-backup analogy for RPO/RTO plus a two-people-both-watering-the-plants analogy for split-brain, and an unanswered-text-message analogy for the general network-ambiguity problem behind retries/idempotency/fencing). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`10-distributed-systems` is now fully L1–L4 (5/5)** — the eighth fully-retrofitted domain in the syllabus.
>
> **Gap found and closed: Consensus Algorithms, plus a real PACELC fix (2026-09-10).** A full repository-wide 22-domain gap audit found two real gaps here. First, a structural one: this domain's own top-level description already names "CAP/PACELC," but `cap-theorem-and-consistency-models.md` never covered PACELC at all — closed with a real, cited addition (Daniel Abadi's original PACELC proposal, verified against its real source) covering the latency-vs-consistency trade-off that exists even absent a partition, classifying real systems (DynamoDB as PA/EL, Google Cloud Spanner as PC/EC) on both axes. Second, a full topic gap: zero coverage of consensus algorithms (Raft, Paxos) — a Staff-level distributed-systems interview staple underlying `etcd`/Consul/CockroachDB. Closed with [Consensus Algorithms: Raft and Paxos](consensus-algorithms-raft-and-paxos.md) (T-2403), backed by a real deterministic Java simulation (`practice/java/consensus-raft/`) of Raft's actual `RequestVote` vote-granting rule, proving a real network partition's minority side can never reach quorum, a real genuine split vote (2-of-5 vs. 2-of-5, one node's messages lost to both candidates) producing no leader for that term, and a real stale leader stepping down the instant it observes a higher term. Vector clocks and quorum-based (Dynamo-style) replication, also flagged by the same audit, remain open for a follow-up pass.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-618 | Distributed Transactions: Saga, Outbox, and 2PC | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/distributed-transactions-saga-and-outbox.md` |
| T-806 | Data Partitioning and Consistent Hashing | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md` |
| T-807 | CAP Theorem and Consistency Models | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md` |
| T-814 | Multi-Region, Failover, and Disaster Recovery | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md` |
| T-909 | Distributed Systems Failure Modes | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/distributed-systems-failure-modes.md` |
| T-2403 | Consensus Algorithms: Raft and Paxos | L1, L2, L3, L4 — fully written, real demo (2026-09-10) | `syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
