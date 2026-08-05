---
title: "Week 8 Study Pack — Manifest"
week: 8
plan: B
last_reviewed: 2026-07-31
---

# Week 8 Study Pack — Manifest

**Topics:** T-701, T-702, T-703, T-704, T-705 · **Plan:** B, first messaging-systems week
**Files:** 12 (+ this manifest) · **Total words:** 6,283 (real count, `wc -w` over all 12 files; updated 2026-08-04 after `09-design-exercise-notification-system.md` was slimmed to a per-phase summary + link, per the new `architecture-atlas/notification-system.md` — see `CHANGELOG.md`)

---

## Files

| # | File | Purpose | Words |
|---|---|---|---|
| 1 | `README.md` | Objective, dependency graph, schedule, exit criteria | 664 |
| 2 | `01-kafka-architecture-fundamentals.md` | T-701 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/kafka/kafka-architecture-fundamentals.md` | 590 |
| 3 | `02-producer-semantics-and-partition-keys.md` | T-702/705 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/kafka/producer-semantics-and-partition-keys.md` | 578 |
| 4 | `03-consumer-groups-and-rebalancing.md` | T-703 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/kafka/consumer-groups-and-rebalancing.md` | 558 |
| 5 | `04-delivery-semantics-and-exactly-once.md` | T-704 — slimmed to a per-section summary + link; full chapter now canonical at `handbook/kafka/delivery-semantics-and-exactly-once.md` | 599 |
| 6 | `05-java-coding-practice.md` | LC 70, 198, 322, 300, all compiled and run | 826 |
| 7 | `06-flashcards.md` | 14 cards | 479 |
| 8 | `07-kafka-guarantees-deliverable.md` | Guarantee table + full worked exit-criteria answers | 785 |
| 9 | `08-week-8-mock-interview.md` | 45-min messaging deep-dive | 312 |
| 10 | `09-design-exercise-notification-system.md` | Slimmed to a per-phase summary + link; full design now canonical at `architecture-atlas/notification-system.md` | 343 |
| 11 | `10-week-8-checklist.md` | Day-by-day checklist | 257 |
| 12 | `resources.md` | Sources classified PRIMARY/BOOK/TOOL/SECONDARY | 292 |

---

## Verification

| Item | Status |
|---|---|
| Java — Kafka | **Executed.** `apache/kafka:3.7.0` single-broker KRaft cluster in Docker, `kafka-clients` 3.7.0 plain jar (no Spring/Maven). Real partition-routing trace (same key → same partition, 6/6 times); real consumer-group rebalance trace (4→2/2 split, then full reassignment to a solo consumer); real at-least-once (36 deliveries for 18 records) and at-most-once (0 of 18 processed) traces from an actual simulated-crash sequence against live offset commits. Source: `practice/java/week-08/kafka/` |
| Java — DP | **Executed.** OpenJDK 21.0.12. `14/14` assertions pass, including a 200-trial randomized cross-check of the O(n log n) LIS solution against an O(n²) reference (seeded, reproducible). Source: `practice/java/week-08/dp/` |
| Multi-broker ISR shrink/expand | **Not executed, stated explicitly.** The practice cluster is a single broker (Docker resource budget for this pack); ISR shrinkage and unclean leader election are described from `00-project/knowledge-architecture-blueprint.md` §5.8 rather than demonstrated live. Named directly in `01-kafka-architecture-fundamentals.md` rather than presented as observed. |
| Interview statistics | None invented anywhere in this pack |

## Errata / defects addressed this week

None. `CHANGELOG.md`'s errata register has no open items scoped to Kafka or this week's topics.

## Integrity note

Every number in this manifest was computed directly this session (`wc -w`, real `javac`/`java` runs against a live Docker Kafka broker). See `study-packs/week-01/MANIFEST.md` for why this convention exists.
