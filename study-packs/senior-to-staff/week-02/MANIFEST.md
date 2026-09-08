---
title: "Senior → Staff, Week 2 — Manifest"
week: 2
track: senior-to-staff
last_reviewed: 2026-09-08
---

# Week 2 — Manifest

**Topics:** CQRS: Read/Write Separation; Strangler Fig, Anti-Corruption Layer, and Migration Patterns. **Track:** Senior → Staff, Week 2 of 8.
**Files:** 1 (+ this manifest) — no chapter content duplicated, per this repository's no-duplication rule.

## Files

| # | File | Purpose |
|---|---|---|
| 1 | `README.md` | Weekly outcome, schedule, required reading, hands-on exercises, cookbook cross-reference, system design exercise, review checklist |

## Verification

| Item | Location | Status |
|---|---|---|
| CQRS demos (`EventualConsistencyLagDemo.java`, `StaleReadDuringLagDemo.java`, `QueryComplexityComparisonDemo.java`) | `practice/java/architecture/cqrs-read-write-separation/` | Predates this pack's construction; not re-verified here |
| Strangler Fig demos (`IncrementalCutoverDemo.java`, `RollbackSafetyDemo.java`) | `practice/java/architecture/strangler-fig-and-migration-patterns/` | Predates this pack's construction; not re-verified here |
| `dashboard-query-re-deriving-an-aggregate-the-write-model-already-knew.md` | `production-cookbook/` | Confirmed to exist at manifest write time (2026-09-08); content not re-verified beyond that |
| `rollback-runbook-no-longer-viable-by-the-time-it-was-needed.md` | `production-cookbook/` | Confirmed to exist at manifest write time (2026-09-08); content not re-verified beyond that |

## Scope note

This week's two chapters and their practice demos predate this study pack. The cookbook cross-references are new: chosen by matching this week's topics to real, existing cookbook filenames confirmed to exist, not fabricated.

## Integrity note

No assertion counts are cited in this manifest, since this week's practice demos were not re-executed or re-verified during this pack's construction — the README links directly to the chapters and demos as the authoritative source.
