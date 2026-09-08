---
title: "Senior → Staff, Week 4 — Manifest"
week: 4
track: senior-to-staff
last_reviewed: 2026-09-08
---

# Week 4 — Manifest

**Topics:** Multi-Region, Failover, and Disaster Recovery; Data Partitioning and Consistent Hashing. **Track:** Senior → Staff, Week 4 of 8.
**Files:** 1 (+ this manifest) — no chapter content duplicated, per this repository's no-duplication rule.

## Files

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | Weekly outcome, schedule, required reading, hands-on exercises, cookbook cross-reference, system design exercise, review checklist |

## Verification

| Item | Location | Status |
|---|---|---|
| Docker Compose DR/split-brain demo (`rpo-demo.sh`, `rpo-archive-demo.sh`, `splitbrain-demo.sh`, `run-all-demos.sh`) | `practice/sql/multi-region-failover-and-dr/` | Predates this pack's construction; not re-executed here |
| `ConsistentHashingDemo.java` | `practice/java/week-10/consistent-hashing/` | Predates this pack's construction; not re-executed here |
| `split-brain-from-promoting-a-standby-without-fencing-the-old-primary.md` | `production-cookbook/` | Confirmed to exist at manifest write time (2026-09-08); content not re-verified beyond that |
| `log-shipping-dr-silently-missing-its-configured-rpo-target.md` | `production-cookbook/` | Confirmed to exist at manifest write time (2026-09-08); content not re-verified beyond that |
| `launch-day-shard-key-becoming-an-18-month-scaling-bottleneck.md` | `production-cookbook/` | Confirmed to exist at manifest write time (2026-09-08); content not re-verified beyond that |
| `naive-hash-mod-n-cache-scaling-causing-a-database-overload.md` | `production-cookbook/` | Confirmed to exist at manifest write time (2026-09-08); content not re-verified beyond that |

## Scope note

This week's two chapters and their practice demos predate this study pack. The four cookbook cross-references are new: chosen by matching this week's topics to real, existing cookbook filenames confirmed to exist, not fabricated. This is the pack's heaviest cross-reference week — both topics had multiple strong, unambiguous cookbook matches.

## Integrity note

No assertion counts are cited in this manifest, since this week's practice demos were not re-executed or re-verified during this pack's construction — the README links directly to the chapters and demos as the authoritative source.
