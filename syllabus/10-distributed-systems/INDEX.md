---
title: "Distributed Systems — Domain Index"
document_type: syllabus-domain-index
domain: 10-distributed-systems
status: 5 of 5 mapped chapters physically relocated (Phase 3, 2026-09-03); L1/L2 retrofit complete (Phase 5, 2026-09-04) — domain fully L1-L4
last_updated: 2026-09-04
---

# Distributed Systems

The theory: CAP/PACELC, consistent hashing, replication, distributed transactions, and failure modes — "what is true about distributed systems," independent of any specific design exercise. Prerequisite to `11-system-design`, per the plan's Section 3.3 distinction.

> **Phase 3 update (2026-09-03).** This domain's full existing content (5 chapter(s)) has physically relocated via `git mv`, preserving file history. See the repository-root `CHANGELOG.md` for the full batch account.
>
> **Phase 5 update (2026-09-04) — domain complete.** All 5 chapters gained a new "Level 1 — Foundation" and "Level 2 — Working Knowledge" section, inserted between "Why This Matters in Interviews" and "Mental Model" per the plan's additive retrofit method (§2.4) — a pure insertion on every chapter, verified by diff. Each pair is grounded in that chapter's own real subject (a mailed-invitation-plus-text analogy for the outbox pattern and Saga, a classroom-locker-assignment analogy for naive modulo hashing vs. a ring for consistent hashing, a two-library-branches analogy for CAP, a personal-backup analogy for RPO/RTO plus a two-people-both-watering-the-plants analogy for split-brain, and an unanswered-text-message analogy for the general network-ambiguity problem behind retries/idempotency/fencing). Every chapter also gained `topic_id`/`mastery_levels_covered: [L1, L2, L3, L4]` front matter. **`10-distributed-systems` is now fully L1–L4 (5/5)** — the eighth fully-retrofitted domain in the syllabus.

## Topics

| Topic ID | Title | Mastery levels covered today | Current location |
|---|---|---|---|
| T-618 | Distributed Transactions: Saga, Outbox, and 2PC | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/distributed-transactions-saga-and-outbox.md` |
| T-806 | Data Partitioning and Consistent Hashing | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md` |
| T-807 | CAP Theorem and Consistency Models | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/cap-theorem-and-consistency-models.md` |
| T-814 | Multi-Region, Failover, and Disaster Recovery | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md` |
| T-909 | Distributed Systems Failure Modes | L1, L2, L3, L4 — fully written (Phase 5, 2026-09-04) | `syllabus/10-distributed-systems/distributed-systems-failure-modes.md` |

## Where this domain's boundary comes from

See `00-project/syllabus-transformation-plan.md` Sections 3.2–3.3 for the full reasoning, and `00-project/migration-mapping.md` for the exhaustive, verified file-by-file mapping this index was generated from.
