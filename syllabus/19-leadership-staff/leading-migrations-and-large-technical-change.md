---
title: "Leading Migrations and Large-Scale Technical Change"
slug: leading-migrations-and-large-technical-change
document_type: syllabus-topic
domain: 19-leadership-staff
topic_id: T-1903
status: draft
version: 1.0
last_updated: 2026-09-04
mastery_levels_covered: [L1, L2, L3, L4]
prerequisites:
  - cross-team-influence-without-authority.md
related:
  - ../17-architecture/strangler-fig-and-migration-patterns.md
  - technical-debt-prioritization-and-advocacy.md
  - ../20-interview-preparation/behavioral/10-migrations-and-large-technical-change.md
practice: []
production_scenarios:
  - ../../production-cookbook/shared-customer-entity-forcing-a-three-team-migration-for-one-field.md
interview_paths: [senior-to-staff]
official_references: []
source_history: []
---

# Leading Migrations and Large-Scale Technical Change

Assigned **T-1903** in this domain's reserved `T-1900`–`T-1999` range. This chapter deliberately does **not** duplicate [Strangler Fig and Migration Patterns](../17-architecture/strangler-fig-and-migration-patterns.md), which owns the *technical* mechanics of a migration — the strangler-fig pattern, dual-write and dark-launch techniques, cutover strategies. This chapter assumes that technical skill and covers the layer above it: the organizational and communication work of actually driving a multi-team, multi-quarter migration to completion — sequencing, stakeholder alignment, status communication, and the specific failure mode of a migration that stalls at 90% complete. It is also the engineering-practice counterpart to [Migrations and Large Technical Change](../20-interview-preparation/behavioral/10-migrations-and-large-technical-change.md), which teaches how to narrate a migration story in an interview.

## 1. Why This Matters

The technical design of a migration is rarely the reason it fails — a well-designed strangler-fig plan with a sound dual-write strategy still stalls if the three teams who need to move in sequence don't have aligned incentives, if status isn't visible enough for stakeholders to trust it's progressing, or if the last, hardest 10% of callers never gets prioritized once the visible bulk of the work is done. Leading a migration well is a distinct, teachable skill from designing one well, and it is one of the most common ways a Staff engineer's impact becomes visible at an organizational scale.

## 2. Prerequisites

[Cross-Team Influence Without Authority](cross-team-influence-without-authority.md) — a migration crossing team boundaries requires the stakeholder-mapping and priority-framing skills from that chapter throughout, not just at kickoff.

## 3. Foundation (L1)

**A migration, at the organizational level, is a sequence of decisions made by multiple people over an extended period, not a single technical execution.** Its central leadership challenge is that the people who benefit from the migration's completion (often the whole organization, diffusely) are frequently not the same people who bear its cost (the specific engineers whose sprint capacity it consumes) — which means a migration with a strong technical case can still fail to get sustained priority unless someone actively manages that misalignment.

**The "last 10%" problem is close to universal in large migrations**: the bulk of callers, usages, or data typically migrates relatively quickly, while a long tail of edge cases, rarely-touched call sites, and "we'll get to it later" stragglers consumes a disproportionate share of the total calendar time. A migration plan that doesn't explicitly account for this tail from the start routinely and predictably runs over its estimated timeline.

## 4. Core Concepts (L2)

**Expand-contract (also called parallel change)** is the standard pattern for making a breaking change safely across many callers without a single flag-day cutover: first *expand* the system to support both the old and new interface simultaneously, migrate callers one at a time onto the new interface while the old one still works, then *contract* by removing the old interface only once every caller has moved. The technical mechanics of this belong to [Strangler Fig and Migration Patterns](../17-architecture/strangler-fig-and-migration-patterns.md); the leadership concept that matters here is that the *contract* phase is where migrations actually die — once the expand phase has made the new path available and most callers have moved, there is no more forcing function compelling the last stragglers to move, and removing the old path (the only step that actually finishes the migration and captures its promised benefit, such as deleting the old code path or decommissioning old infrastructure) requires deliberate, continued push.

**A visible, shared tracking artifact is a leadership tool, not just a status report.** A dashboard or tracked list showing exactly which callers have migrated and which haven't converts an invisible, easy-to-deprioritize background task into a concrete, comparably-visible piece of work — teams that can see they are the last remaining unmigrated caller on a shared list behave differently than teams who have no visibility into how their inaction compares to everyone else's progress.

**Sequencing decisions should be made explicitly, not left to whichever team moves first.** Choosing to migrate the lowest-risk caller first (to validate the approach cheaply) versus the highest-value caller first (to capture benefit sooner) versus the caller with the most organizational pressure to move (to build momentum) are different, defensible strategies — the mistake is not choosing one deliberately and instead letting sequencing happen by whichever team happens to have spare capacity first, which optimizes for nothing in particular.

## 5. How It Works Internally (L3)

**Migrations compete for prioritization against feature work on every single team they touch, every single sprint, for the migration's entire duration** — this is the structural reason a migration with a technically sound plan can still take three times longer than estimated: the estimate usually models the engineering effort correctly but doesn't model the repeated, ongoing prioritization negotiation required to actually get that effort scheduled, sprint after sprint, against competing feature demands that have more immediate, visible stakeholders.

**Status communication has a specific decay problem in long migrations**: a migration announced with fanfare at kickoff, then only reported on in occasional, easy-to-miss updates, loses stakeholder attention exactly during the long, unglamorous middle phase — which is precisely when sustained prioritization pressure is most needed, since the initial momentum has faded and the finish line isn't visible yet. Regular, lightweight, concrete status updates (not a large ceremonial review) are the mechanism that keeps a migration from silently losing priority during this phase.

**Fred Brooks's core observation in *The Mythical Man-Month*** — that adding people to an already-late, coordination-heavy effort tends to make it later, not faster, because coordination overhead grows faster than the raw capacity added — applies directly to a migration that has fallen behind: the instinct to "throw more engineers at it" to hit an original deadline usually backfires for a coordination-bound, multi-team migration in the same way it backfires for a single late software project, since the bottleneck is decision and sequencing capacity, not raw engineering hours.

## 6. Practical Usage

- **Build a visible tracking artifact from day one** (Section 4) — even a simple shared spreadsheet listing every caller and its migration status is enough to convert invisible progress into a comparable, socially visible one.
- **Choose and state a sequencing strategy explicitly** (Section 4) — lowest-risk-first, highest-value-first, or momentum-first — rather than letting sequence happen passively by whichever team has spare capacity.
- **Plan for the contract phase's forcing-function gap from the start** (Section 4) — decide in advance what will actually compel the last stragglers to move (a stated deprecation date, a leadership-backed mandate, or removing support entirely on a fixed date) rather than discovering at 90% complete that nothing is compelling the remaining 10%.

## 7. Examples

A minimal migration tracking artifact, illustrating Section 4's visibility principle:

```
Migration: replace deprecated OrderEventV1 schema with OrderEventV2
Deadline for old schema removal: end of Q3

Consumer            Status              Owner        Blocker
------------------------------------------------------------------
billing-service     Migrated           Team Billing  none
fulfillment-service Migrated           Team Fulfill  none
analytics-pipeline  In progress (60%)  Team Data     needs schema
                                                      registry update
reporting-service   Not started        Team Reports  no capacity
                                                      allocated yet
```

The concrete leadership action this artifact enables: `reporting-service`'s "not started, no capacity allocated" status is now a specific, visible, comparable fact rather than an invisible background gap — it can be raised directly with Team Reports' lead, referencing that every other consumer has already moved, rather than as a vague "please prioritize this sometime" request with no comparative pressure behind it.

## 8. Common Mistakes

- **Estimating a migration's timeline from engineering effort alone**, without modeling the repeated cross-team prioritization negotiation required every sprint (Section 5) — the single most common cause of large migrations running well past their original estimate.
- **No plan for the contract phase's forcing function** (Section 4) — the migration achieves 90% adoption and then stalls indefinitely, since expand-phase momentum provided no mechanism to compel the last stragglers.
- **Letting sequencing happen passively** rather than choosing a strategy deliberately (Section 4).
- **Communicating status only at kickoff and at completion**, losing stakeholder attention during the long middle phase precisely when sustained prioritization pressure matters most (Section 5).

## 9. Edge Cases

- **A straggler team that is not being difficult, but is genuinely under-resourced to complete their portion** — the correct response is surfacing a real resourcing gap to leadership (Section 13), not continued pressure on a team that structurally cannot comply regardless of motivation; conflating the two is a common, costly misdiagnosis.
- **A migration whose original technical rationale has become stale partway through** (the system it was replacing is no longer the bottleneck it once was) — worth explicitly re-validating the business case at a checkpoint rather than continuing to push a migration on inertia alone; killing a migration that no longer earns its cost is a legitimate, sometimes underused leadership decision.
- **A migration under genuine deadline pressure** (a hard external forcing function, such as a vendor's own end-of-life date) — Section 5's Brooks's-Law caution against adding people still applies to coordination-bound work, but a hard external deadline may legitimately justify accepting a narrower migration scope (migrating only what's required to meet the deadline, deferring the rest) rather than either missing the deadline or adding ineffective headcount.

## 10. Performance Implications

Not applicable in the runtime sense; the organizational equivalent is calendar time to completion — Section 5's two mechanisms (repeated per-sprint prioritization competition, and status-attention decay during the unglamorous middle phase) are the two largest, most controllable levers on how long a migration actually takes relative to its engineering-effort estimate alone.

## 11. Trade-offs

| Sequencing strategy | Gains | Costs |
|---|---|---|
| Lowest-risk caller first | Validates approach cheaply, builds confidence | Slower to capture the migration's actual value |
| Highest-value caller first | Captures benefit soonest | Higher risk of an early, high-visibility failure before the approach is proven |
| Momentum-first (whichever team is most willing) | Fastest early progress, easiest early wins | Can leave the hardest, most important callers for last, aggravating the last-10% problem |
| A stated hard deprecation date for the old path | Creates a genuine forcing function for the contract phase | Real risk of disruption if some caller still isn't ready when the date arrives |

## 12. Senior-Level Considerations (L3)

A Senior engineer leading a migration within their own team builds the visible tracking artifact (Section 4/6) from the start, communicates status on a regular, lightweight cadence through the unglamorous middle phase (Section 5), and explicitly names a sequencing strategy rather than letting it happen passively. The Senior-level judgment call is recognizing early when a straggler's non-movement is a resourcing problem rather than a prioritization problem (Section 9), since the correct intervention differs completely between the two.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, leading a migration means owning the prioritization negotiation across every team the migration touches for its entire duration, not just designing the technical plan once — which is why Section 5's per-sprint competing-priorities dynamic is the actual, ongoing job, not a one-time kickoff conversation. A Staff engineer is also the person best positioned to plan the contract phase's forcing function *before* the expand phase begins (Section 4) — securing a leadership-backed deprecation date up front is far easier to obtain when the migration hasn't started yet and no one has a specific reason to resist it, than trying to obtain the same commitment once 90% adoption has been reached and the remaining stragglers have specific, concrete objections to a firm date. Staff engineers should also be willing to recommend killing a migration that has lost its original justification (Section 9) — sustaining a migration on inertia alone, past the point its business case still holds, is an organizational cost that a Staff engineer's broader system view is specifically positioned to notice before others do.

## 14. Production Scenarios

- **[Shared Customer Entity Requiring a Three-Team Migration for One New Field](../../production-cookbook/shared-customer-entity-forcing-a-three-team-migration-for-one-field.md)** — while filed primarily as an architecture-boundary incident, its resolution required exactly this chapter's sequencing and coordination discipline: freezing the shared entity across three teams required an explicit, visible cross-team agreement (Section 4/6), and the underlying context-splitting migration that followed needed sequencing decisions (which bounded context to extract first) made deliberately rather than passively.

## 15. Interview Questions

### Question 1 — Tell me about a migration or large technical change you led. What made it hard, and how did you keep it moving?

**Why interviewers ask it.** Tests whether the candidate understands migrations as an organizational leadership problem (Sections 4–5) or only describes the technical design, which is a different and, at Staff level, a less differentiating skill on its own.

**Expected answer.** Names a specific organizational obstacle (competing team priorities, a stalled last-mile caller, status attention decay) and the specific intervention used to address it — a visible tracking artifact, a renegotiated sequencing strategy, a secured deprecation date — distinguishing what the candidate specifically did from the migration simply happening over time.

**Minimum acceptable answer.** Describes a real migration and some coordination effort involved, even without naming a specific mechanism.

**Strong Senior answer.** Explicitly separates the technical plan from the organizational execution challenge, and names a concrete tool (a tracking dashboard, a stated sequencing rationale) used to manage it.

**Staff-level extension.** Discusses how the contract-phase forcing function (Section 4) was secured, ideally before the migration started, and whether the migration's continued justification was re-validated partway through rather than assumed.

**Common mistakes.** A story that is entirely about the technical migration pattern (dual-write, cutover), with no account of the cross-team prioritization or communication work — this misses what the question is actually probing for at Staff level.

**Follow-up questions.** "What would you have done differently in how you sequenced the rollout?" (tests whether the candidate has genuine retrospective insight into Section 4's sequencing trade-offs, not just a successful outcome to report.)

### Question 2 — A migration you're driving has stalled at 90% complete. What do you do?

**Why interviewers ask it.** Directly tests knowledge of the last-10%/contract-phase problem (Section 3–4), one of the most concrete, recognizable failure modes in this domain.

**Expected answer.** Diagnoses whether the remaining stragglers are a prioritization problem (no forcing function exists, Section 4) or a resourcing problem (genuinely blocked, Section 9), and applies the matching intervention — securing a stated deprecation date and leadership backing for the former, escalating a real resourcing gap for the latter — rather than simply repeating the same ask that has already failed to move them for weeks.

**Minimum acceptable answer.** Recognizes that "just ask again" won't work and some different intervention is needed.

**Strong Senior answer.** Names the specific forcing-function mechanism (a firm deprecation date, visible comparative status, leadership-backed mandate) and explains why it works where repeated asking hasn't.

**Staff-level extension.** Discusses planning for this exact moment *before* the migration starts (Section 13) — securing the deprecation date up front, when there's no concrete caller yet resisting it, rather than trying to negotiate one after specific stragglers have specific objections to a firm date.

**Common mistakes.** Proposing to simply escalate immediately without first diagnosing whether the blocker is prioritization or genuine resourcing (Section 9) — the correct intervention differs between the two, and escalating a resourcing problem as if it were a prioritization problem burns relationship capital for nothing.

**Follow-up questions.** "How would you have designed the migration differently from the start to avoid this stall?" (Section 4/13 — securing the forcing function up front.)

## 16. Coding/Practice Exercises

- For a real or hypothetical migration you're aware of, build the tracking-artifact table from Section 7: list every consumer, its status, its owner, and its blocker. Identify which rows would benefit most from a visible, comparative status update to their owning team.
- State, in writing, an explicit sequencing strategy (lowest-risk-first, highest-value-first, or momentum-first) for a migration you're planning or have observed, and the specific reason for that choice over the alternatives (Section 4/11).

## 17. Debugging Exercises

**Symptom:** a migration reported "90% complete" for the last several status updates, with no further visible progress.

**Diagnose:** this is the last-10%/contract-phase problem (Section 3–4) made concrete. Check first whether a genuine forcing function exists for the remaining callers (a stated deprecation date, a leadership mandate) — if none exists, the stall is structurally expected, not surprising, since nothing is compelling the remaining movement. Separately check whether the remaining stragglers are blocked by a real resourcing gap (Section 9) rather than simple inertia — the fix differs completely: securing a deprecation date and leadership backing for the former, escalating a resourcing conversation for the latter.

## 18. Design Exercises

**Design constraint:** you're about to kick off a migration requiring six teams to move off a deprecated internal messaging library within two quarters, with no existing organizational mandate compelling anyone to move.

Design the leadership plan around this chapter's two core levers explicitly: before the expand phase begins, secure an explicit, leadership-backed deprecation date for the old library (Section 4/13) — obtained now, while no specific team yet has a concrete objection to it — and build the shared visibility artifact (Section 4/6) from day one so each team's progress is comparable to the other five's, not private and easy to deprioritize. State the real trade-off: securing a firm deprecation date up front constrains flexibility later (a team with a genuinely legitimate blocker close to the deadline has less room to negotiate), but the alternative — no forcing function at all — reliably produces the last-10% stall this chapter describes; the mitigation is building an explicit, narrow exception process for genuine resourcing blockers (Section 9) rather than either a rigid deadline with no exceptions or no deadline at all.

## 19. Further Reading

- *The Mythical Man-Month*, Fred Brooks — the coordination-overhead argument referenced in Section 5.
- Expand-contract / parallel change — a widely documented pattern (see also [Strangler Fig and Migration Patterns](../17-architecture/strangler-fig-and-migration-patterns.md) for its technical mechanics) referenced in Section 4.
- [Migrations and Large Technical Change](../20-interview-preparation/behavioral/10-migrations-and-large-technical-change.md) — the interview-application sibling to this chapter.

## 20. Mastery Checklist

| Level | You can... | Verify with |
|---|---|---|
| L1 | Explain why a migration is fundamentally an organizational sequencing problem, and describe the last-10% problem | [Section 3](#3-foundation-l1) |
| L2 | Build a visible tracking artifact and state an explicit sequencing strategy for a real migration | [Section 7](#7-examples), [Practice Exercise](#16-codingpractice-exercises) |
| L3 | Explain why migrations run over their engineering-effort estimate, and diagnose a stalled 90%-complete migration to its actual cause | [Section 5](#5-how-it-works-internally-l3), [Debugging Exercise](#17-debugging-exercises) |
| L4 | Plan a contract-phase forcing function before a migration starts, and know when to recommend killing one that has lost its justification | [Section 13](#13-staffsystem-level-considerations-l4), [Design Exercise](#18-design-exercises) |
