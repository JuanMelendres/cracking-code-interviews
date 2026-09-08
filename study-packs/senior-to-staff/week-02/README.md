---
title: "Senior → Staff, Week 2 — Read/Write Separation and Incremental Migration"
document_type: study-pack
week: 2
track: senior-to-staff
status: draft
estimated_hours: 7
---

# Week 2 — Read/Write Separation and Incremental Migration

## Weekly Outcome

By the end of this week you can state the specific condition under which CQRS earns its complexity (and recognize the far more common case where it doesn't), and can lay out an incremental, rollback-safe migration off a legacy system using the Strangler Fig pattern instead of proposing a rewrite.

## Why This Week Matters

The learning path frames [CQRS: Read/Write Separation](../../../syllabus/17-architecture/cqrs-read-write-separation.md) as "a named judgment trap — the expected Staff answer is knowing when *not* to reach for it," and [Strangler Fig, Anti-Corruption Layer, and Migration Patterns](../../../syllabus/17-architecture/strangler-fig-and-migration-patterns.md) as "the standard follow-up to any legacy-system design question." Both topics test the same underlying Staff judgment as Week 1: naming a pattern is not the skill, knowing exactly when it pays for itself is.

## Prerequisites

Week 1 of this pack. [Mid → Senior](../../mid-to-senior/README.md) Week 4 (Database internals) — CQRS's eventual-consistency trade-off assumes solid isolation-level and replication reasoning already.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [CQRS: Read/Write Separation](../../../syllabus/17-architecture/cqrs-read-write-separation.md) — read through its Staff-Level Discussion |
| Wed–Thu | [Strangler Fig, Anti-Corruption Layer, and Migration Patterns](../../../syllabus/17-architecture/strangler-fig-and-migration-patterns.md) — read through its Staff-Level Discussion |
| Fri–Sat | Reproduce both hands-on demos below |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | CQRS: Read/Write Separation | [`syllabus/17-architecture/cqrs-read-write-separation.md`](../../../syllabus/17-architecture/cqrs-read-write-separation.md) |
| 2 | Strangler Fig, Anti-Corruption Layer, and Migration Patterns | [`syllabus/17-architecture/strangler-fig-and-migration-patterns.md`](../../../syllabus/17-architecture/strangler-fig-and-migration-patterns.md) |

## Hands-On Exercises

Real demos exist and predate this pack's construction; not re-verified here.

- [`practice/java/architecture/cqrs-read-write-separation/`](../../../practice/java/architecture/cqrs-read-write-separation/) — includes `EventualConsistencyLagDemo.java`, `StaleReadDuringLagDemo.java`, and `QueryComplexityComparisonDemo.java`.
- [`practice/java/architecture/strangler-fig-and-migration-patterns/`](../../../practice/java/architecture/strangler-fig-and-migration-patterns/) — includes `IncrementalCutoverDemo.java` and `RollbackSafetyDemo.java`.

## Production Cookbook Cross-Reference

- [`dashboard-query-re-deriving-an-aggregate-the-write-model-already-knew.md`](../../../production-cookbook/dashboard-query-re-deriving-an-aggregate-the-write-model-already-knew.md) — a "total spend per customer" report recomputed by summing every order's line items on every request against the normalized write schema. Pairs directly with Topic 1.
- [`rollback-runbook-no-longer-viable-by-the-time-it-was-needed.md`](../../../production-cookbook/rollback-runbook-no-longer-viable-by-the-time-it-was-needed.md) — a payments migration whose stated "rollback to legacy" runbook stopped being viable by the time it was actually needed. Pairs directly with Topic 2.

Read both after finishing their matching chapter and confirm you can restate each diagnosis without looking.

## Interview Answer Drills

Answer, out loud, unprompted: "when would you reach for CQRS, and what's the first thing that breaks if you adopt it without needing it?" and "walk me through migrating a legacy billing system to a new one without a big-bang cutover" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Take the `dashboard-query-re-deriving-an-aggregate` scenario above and design the CQRS read model that would fix it — name the projector's failure modes and how you'd detect projection lag in production, per Topic 1's own Failure Modes and Debugging section.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: given a legacy monolith and a stated deadline pressure to "just rewrite it," argue for Strangler Fig instead, cold, in under 10 minutes, naming at least one concrete rollback-safety mechanism.

## Review Checklist

- [ ] Completed both chapters' own Staff-Level Mastery Checklists.
- [ ] Reproduced at least one demo from each hands-on directory above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can name the specific condition under which CQRS's complexity is worth paying for, and the more common case where it isn't.
- [ ] Produced the CQRS read-model design above, including a projection-lag detection mechanism.
- [ ] Can describe an incremental, rollback-safe Strangler Fig migration plan for a stated legacy scenario.

## Retrospective

Note whether you've seen CQRS reached for on a project without the read/write asymmetry that justifies it — this is one of the most common "pattern applied without the condition that earns it" mistakes at Staff-level architecture reviews.

## Next Week

[Week 3 — Debt as an Economic Decision, Decisions as Organizational Memory](../week-03/README.md).
