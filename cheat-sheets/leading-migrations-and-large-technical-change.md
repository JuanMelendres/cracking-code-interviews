---
title: "Cheat Sheet: Leading Migrations and Large-Scale Technical Change"
slug: leading-migrations-and-large-technical-change
document_type: cheat-sheet
domain: 19-leadership-staff
topic_id: T-1903
canonical: ../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md
last_updated: 2026-09-06
---

# Leading Migrations and Large-Scale Technical Change

**Canonical chapter:** [`syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md`](../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md)

## Core Mental Model

A migration is a sequence of decisions made by multiple people over an extended period, not a single technical execution — it rarely fails on technical design, and instead stalls because the people who benefit (the whole org, diffusely) aren't the people who bear the cost (the specific engineers whose sprint capacity it consumes), and because the "last 10%" loses its forcing function once expand-phase momentum fades.

## Essential Definitions

- **Expand-contract (parallel change)** — expand to support both old and new interfaces simultaneously, migrate callers one at a time, then contract by removing the old interface once every caller has moved. The *contract* phase is where migrations actually die — nothing compels the last stragglers once the new path is available.
- **The last-10% problem** — the bulk of callers migrates quickly; a long tail of edge cases and stragglers consumes a disproportionate share of total calendar time.
- **Visible tracking artifact** — a shared list of who has and hasn't migrated converts invisible background work into comparable, socially visible progress.
- **Sequencing strategy** — lowest-risk-first, highest-value-first, or momentum-first are different, defensible choices; the mistake is letting sequencing happen passively by whichever team has spare capacity.
- **Brooks's Law applied to migrations** — adding people to a coordination-bound, already-late migration tends to make it later, since the bottleneck is decision and sequencing capacity, not raw engineering hours.

## Decision Table — Sequencing Strategy

| Strategy | Gains | Costs |
|---|---|---|
| Lowest-risk caller first | Validates the approach cheaply, builds confidence | Slower to capture the migration's actual value |
| Highest-value caller first | Captures benefit soonest | Higher risk of an early, high-visibility failure before the approach is proven |
| Momentum-first (whichever team is most willing) | Fastest early progress, easiest early wins | Can leave the hardest, most important callers for last, aggravating the last-10% problem |
| Stated hard deprecation date for the old path | Creates a genuine forcing function for the contract phase | Real risk of disruption if a caller isn't ready when the date arrives |

## Common Pitfalls

- Estimating a migration's timeline from engineering effort alone, without modeling the repeated cross-team prioritization negotiation required every sprint — the single most common cause of migrations running past estimate.
- No plan for the contract phase's forcing function — the migration hits 90% adoption and stalls indefinitely.
- Letting sequencing happen passively instead of choosing a strategy deliberately.
- Communicating status only at kickoff and completion, losing stakeholder attention exactly during the unglamorous middle phase when sustained prioritization pressure matters most.

## Interview Answer Skeleton

**30-sec:** Leading a migration is an organizational sequencing and prioritization problem, not a technical execution problem — the two levers are a visible tracking artifact that makes progress comparable across teams, and a secured forcing function (a real deprecation date) for the contract phase, so the last 10% doesn't stall indefinitely.

**2-min:** Add why migrations run over estimate: they compete for prioritization against feature work on every team they touch, every sprint, for the entire duration — the estimate usually models engineering effort correctly but not the repeated negotiation needed to actually get that effort scheduled. Add status-decay: a migration announced with fanfare then only reported occasionally loses stakeholder attention exactly when sustained pressure is most needed. The fix for both is a lightweight, regular status cadence plus a shared tracking artifact from day one.

**Whiteboard:** Draw a simple table with columns Consumer / Status / Owner / Blocker and fill in a few rows, one marked "Not started, no capacity allocated." Say: "this single row is now a specific, comparable fact I can raise directly with that team's lead, referencing that everyone else has already moved — not a vague 'please prioritize this sometime.'"

**Staff-level framing:** At Staff scope, leading a migration means owning the prioritization negotiation across every team it touches for its entire duration, not designing the technical plan once. A Staff engineer is best positioned to secure the contract phase's forcing function — a leadership-backed deprecation date — *before* the expand phase begins, since it's far easier to obtain when no specific team has a concrete objection yet than after 90% adoption when stragglers have specific reasons to resist a firm date. Staff engineers should also be willing to recommend killing a migration that has lost its original business justification rather than sustaining it on inertia.

## Warning Signs

- A migration reported "90% complete" for several consecutive status updates with no further visible progress — check first whether a genuine forcing function exists (a stated deprecation date, a leadership mandate); if none exists, the stall is structurally expected. Separately check whether stragglers are blocked by a real resourcing gap rather than inertia — the fix differs completely: secure a deprecation date for the former, escalate a resourcing conversation for the latter.

## Related

- syllabus/17-architecture/strangler-fig-and-migration-patterns.md
- syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md
- syllabus/20-interview-preparation/behavioral/10-migrations-and-large-technical-change.md
