---
title: "Week 10 Study Pack — Manifest"
week: 10
plan: B
last_reviewed: 2026-07-29
---

# Week 10 Study Pack — Manifest

**Topics:** T-618, T-614, T-806, T-515, T-616 · **Plan:** B, Distributed Data + Resilience
**Files:** 13 (+ this manifest) · **Total words:** 8,807 (real count, `wc -w` over all 13 files; updated 2026-07-30 after all five T-topics were slimmed to a summary + canonical-chapter link — see `CHANGELOG.md`)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, exit criteria | 747 |
| 2 | `01-saga-outbox-and-distributed-transactions.md` | T-618 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/system-design/distributed-transactions-saga-and-outbox.md` | 754 |
| 3 | `02-sharding-and-partitioning-strategies.md` | T-614 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/databases/table-partitioning-and-sharding-strategies.md` | 608 |
| 4 | `03-consistent-hashing.md` | T-806 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/system-design/data-partitioning-and-consistent-hashing.md` | 608 |
| 5 | `04-resilience-patterns.md` | T-515 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/system-design/resilience-patterns.md` | 681 |
| 6 | `05-zero-downtime-migration.md` | T-616 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/databases/zero-downtime-schema-migration.md` | 611 |
| 7 | `06-java-coding-practice.md` | LC 215/347/23/295 (heaps), all compiled and run | 855 |
| 8 | `07-flashcards.md` | 16 cards | 556 |
| 9 | `08-outbox-implementation-deliverable.md` | The week's named deliverable — full working outbox walkthrough | 1,089 |
| 10 | `09-week-10-mock-architecture-round.md` | 60-min architecture round | 412 |
| 11 | `10-design-exercise-distributed-cache.md` | Full six-phase design | 1,214 |
| 12 | `11-week-10-checklist.md` | Day-by-day checklist | 292 |
| 13 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 378 |

---

## Verification

| Item | Status |
|---|---|
| Java + SQL — Transactional outbox | **Executed.** Real Postgres 16 (Docker) + real single-broker Kafka (Docker). Real dual-write hazard (order committed, event permanently lost). Real working outbox: 3 orders written atomically, poller crashed mid-cycle, restarted, redelivered — 4 Kafka messages for 3 orders (1 measured duplicate), 0 lost. Source: `practice/java/week-10/outbox-publisher/`, `practice/sql/week-10/outbox/` |
| SQL — Sharding/partitioning | **Executed.** Real Postgres 16 hash-partitioned table, 40,000 rows across 4 partitions. Real `EXPLAIN (ANALYZE)`: 1-partition scan (0.727ms) when filtering by the partition key vs. all-4-partition scan (2.667ms) otherwise. Source: `practice/sql/week-10/sharding/` |
| Java — Consistent hashing | **Executed.** Real 10,000-key, 10-node measurement: naive `hash % N` remaps 92.5% of keys on a node removal; consistent hashing with 150 virtual nodes/node remaps 9.2%, close to the 10% theoretical ideal. Source: `practice/java/week-10/consistent-hashing/` |
| Java — Resilience patterns | **Executed.** Real circuit breaker cycling all three states (CLOSED→OPEN→HALF_OPEN→CLOSED) against a genuinely recovering downstream; measured 5 of 20 calls converted from 200ms cost to ~0ms while open. Real retry-jitter measurement: 5 clients synchronized to the exact same delay without jitter, spread across the full window with it. Source: `practice/java/week-10/resilience/` |
| SQL — Zero-downtime migration | **Executed.** Real 2-million-row Postgres table. Plain `CREATE INDEX` blocked a concurrent `INSERT` for 1943ms (the full build duration, confirmed by output ordering); `CREATE INDEX CONCURRENTLY` let the same `INSERT` complete in 84ms while the build was still running. Source: `practice/sql/week-10/zero-downtime-migration/` |
| Java — coding (heaps) | **Executed.** `9/9` assertions pass, including a 500-trial randomized cross-check of `MedianFinder` against a sorted-list reference. Source: `practice/java/week-10/heaps/` |
| Interview statistics | None invented anywhere in this pack |

## Errata / defects addressed this week

None. `CHANGELOG.md`'s errata register has no open items scoped to this week's topics (distributed transactions, sharding, resilience patterns, zero-downtime migration).

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs, real Postgres 16 and Kafka 3.7.0 Docker containers, real wall-clock timing via `python3` for millisecond precision). Two known implementation gaps are stated explicitly rather than glossed over — see `08-outbox-implementation-deliverable.md` §6 (polling vs. CDC, no poller lease/lock, no dead-letter handling). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
