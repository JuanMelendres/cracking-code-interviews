---
title: "Migrations and Large Technical Change"
slug: migrations-and-large-technical-change
document_type: behavioral-handbook-chapter
domain: 20-interview-preparation/behavioral
status: draft
version: 1.0
last_updated: 2026-09-04
source_history:
  - behavioral-handbook/10-migrations-and-large-technical-change.md
topic_id: T-1510
mastery_levels_covered:
  - L1
  - L2
  - L3
  - L4
difficulty:
  - advanced
target_levels:
  - senior
  - staff
prerequisites:
  - 01-star-framework-and-delivery.md
related:
  - ../../10-distributed-systems/distributed-transactions-saga-and-outbox.md
official_references: []
---

# Migrations and Large Technical Change

## Table of Contents

- [Learning Objectives](#learning-objectives)
- [Why This Matters in Interviews](#why-this-matters-in-interviews)
- [Level 1 — Foundation](#level-1--foundation)
- [Level 2 — Working Knowledge](#level-2--working-knowledge)
- [Mental Model: The Story Is About Sequencing and Risk Management, Not the Destination](#mental-model-the-story-is-about-sequencing-and-risk-management-not-the-destination)
- [The Migration Story Structure](#the-migration-story-structure)
- [Illustrative Example](#illustrative-example)
- [The Rollback Question, Prepared in Advance](#the-rollback-question-prepared-in-advance)
- [Interview Question: "Tell me about a large technical migration you led."](#interview-question-tell-me-about-a-large-technical-migration-you-led)
- [Common Mistakes](#common-mistakes)
- [Self-Review Checklist](#self-review-checklist)
- [Summary](#summary)
- [Related](#related)

## Learning Objectives

After this chapter, you can tell a migration story that demonstrates sequencing judgment and risk management under real operational constraints, rather than a story that just describes the technical destination of the migration.

## Why This Matters in Interviews

Large migrations (a database engine change, a monolith decomposition, a major framework upgrade, a data model change touching many consumers) are among the highest-risk categories of engineering work — they touch live systems, often can't be fully tested in advance, and frequently can't be trivially rolled back once partially complete. Interviewers use this story type to assess whether a candidate can manage that risk deliberately (staged rollout, reversibility built in from the start, clear go/no-go criteria) rather than simply executing a plan and hoping nothing breaks.

## Level 1 — Foundation

Think about a surgeon rerouting blood flow around a blocked vessel. They don't cut the old vessel and splice in the new one in a single irreversible motion — they clamp one segment, build and test the bypass while the old pathway still carries blood, and only remove the original vessel once the new route is proven to work under real blood pressure. A migration story is judged the same way: the destination (the new database, the new architecture) is almost incidental; what's actually being assessed is whether you kept a working pathway alive while building and proving the new one, rather than betting everything on one irreversible cut.

## Level 2 — Working Knowledge

At this level, the working distinction to make — and the one this chapter names directly — is between early-stage and late-stage rollback, exactly like a surgeon's own risk profile changes the moment the old vessel is actually removed. Before that point, reversing course is simple: stop routing through the new pathway, the old one is still intact. After it, reversing means finding a new old pathway, which is often much harder or impossible — which is why this chapter's own illustrative example runs weeks of dual-write and reconciliation before ever cutting reads over, the equivalent of proving the bypass works under real pressure before touching the original vessel at all.

The working question to prepare for, because it's asked almost every time, is the surgeon's own version of "what if the bypass had failed mid-procedure": what would you have done if this specific stage had gone wrong? A credible answer names a specific stage and a specific fallback, the same way a surgeon has a specific contingency for a bypass that doesn't take, rather than a vague "we would have figured it out."

## Mental Model: The Story Is About Sequencing and Risk Management, Not the Destination

A weak migration story describes the before-state and after-state technically ("we moved from X to Y") with the actual migration process compressed into "and then we migrated." A strong migration story spends most of its Action on the *sequencing* — what order things happened in, and specifically why that order, what safety mechanisms existed at each stage, and what would have triggered stopping or reversing partway through. The destination architecture is almost incidental; the judgment being assessed lives entirely in how the risk of getting from A to B was managed.

## The Migration Story Structure

| STAR component | Migration-specific content |
|---|---|
| Situation | What was being migrated and why, what made the migration necessary (not optional) at that specific time |
| Task | What was the candidate's specific role — the person who designed the migration plan, led execution, or both |
| Action | The staging strategy (big-bang vs. incremental, dual-write vs. cutover), what safety mechanisms existed at each stage (feature flags, canary rollout, dual-write reconciliation), what the go/no-go criteria were at each checkpoint |
| Result | What shipped, how long it took, any issues encountered mid-migration and how they were handled, and — ideally — what would be done differently on the next large migration |

## Illustrative Example

This example is illustrative — a representative scenario, not a real candidate's actual experience — built around the dual-write and reconciliation pattern this program's [Distributed Transactions, Saga, and Outbox](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md) chapter covers on its technical merits.

*"We needed to migrate our primary orders table from a single Postgres instance to a sharded setup, because write throughput had grown past what vertical scaling could sustain, and the migration had to happen with zero downtime since it's a customer-facing, revenue-critical table. Rather than a big-bang cutover, I designed a three-phase plan: first, dual-write to both the old and new systems while reads still came exclusively from the old system, giving us weeks of production data flowing into the new sharded setup without it being load-bearing yet. Second, we ran a continuous reconciliation job comparing records between old and new, which caught a subtle bug in our sharding key derivation — about 0.3% of orders were landing on the wrong shard due to an edge case in how we handled a specific customer ID format — before any customer-facing traffic depended on it. Third, once reconciliation showed zero discrepancies for two consecutive weeks, we cut reads over to the new system behind a feature flag, rolled out to 1% of traffic, then 10%, then 100%, with automatic rollback configured if error rates exceeded a threshold at any stage. The full migration took about ten weeks, considerably longer than the two-week estimate I'd originally given — the reconciliation-driven bug discovery alone added three weeks — but we had zero customer-facing incidents during the entire migration, and the pattern of dual-write-plus-reconciliation-before-cutover became the standard approach our team used for every subsequent major data migration."*

## The Rollback Question, Prepared in Advance

Almost every migration story gets a follow-up asking specifically about rollback — "what would you have done if it had gone wrong partway through?" This question deserves a genuinely prepared answer, not an improvised one, since a candidate who hasn't thought through their own rollback plan in advance is implicitly admitting the real migration may not have had one either. The strongest answers distinguish between *early-stage* rollback (before the new system carries any load-bearing traffic — usually trivial, just stop dual-writing) and *late-stage* rollback (after some traffic has cut over — usually much harder, and worth being honest about exactly how hard, rather than claiming a clean rollback was always possible at every stage).

## Interview Question: "Tell me about a large technical migration you led."

**What the interviewer is assessing:** risk-management judgment under real operational constraints — staging strategy, safety mechanisms, and go/no-go decision-making — not just technical knowledge of the source and destination systems.

**Weak answer characteristics:** the story describes the technical before/after state in detail but compresses the actual migration process into a single sentence; no staging strategy or safety mechanism is mentioned; no account of what would have happened if something had gone wrong partway through.

**Strong answer structure:** S/T/A/R with Action dominated by sequencing and risk-management detail — the staging strategy, safety mechanisms at each stage, explicit go/no-go criteria.

**Staff-level expectations:** honesty about what took longer than expected or what went wrong mid-migration, and what was learned — a migration story with zero surprises and a perfectly-executed original plan is less credible than one that names a real complication and how it was handled.

**Probing follow-ups:** "What would you have done if [a specific stage] had failed?" (tests whether rollback was genuinely planned, not assumed); "How did you decide on the staging strategy — what made you choose incremental over big-bang, or vice versa?"; "What would you do differently next time?"

**Self-review checklist:**
- [ ] Action is dominated by sequencing and risk-management detail, not just the technical destination
- [ ] A specific safety mechanism (feature flag, canary, reconciliation, dual-write) is named
- [ ] A rollback plan is described, distinguishing early-stage from late-stage rollback difficulty
- [ ] At least one real complication or surprise is named honestly, with how it was handled

## Common Mistakes

- Compressing the actual migration process into "and then we migrated" while spending most of the story on the before/after technical states.
- No staging strategy — implying a big-bang cutover without acknowledging the risk that implies, or without explaining why big-bang was actually the right choice given the constraints.
- Claiming a clean rollback was possible at every single stage, which is rarely true for late-stage migration failures and reads as unprepared for the rollback follow-up question.
- A migration story with no real complications or surprises — either an unusually smooth migration (possible, but worth being specific about why) or an incomplete telling.

## Self-Review Checklist

- [ ] The staging strategy is named and justified, not just described as a fact
- [ ] At least one concrete safety mechanism is named
- [ ] Rollback difficulty at different stages is honestly distinguished, not glossed over
- [ ] A real complication is named, with how it was actually handled

## Summary

A migration story is evaluated on sequencing and risk-management judgment — the staging strategy, safety mechanisms at each checkpoint, and explicit go/no-go criteria — far more than on the technical destination architecture. Prepare the rollback follow-up question specifically in advance, distinguishing early-stage (usually simple) from late-stage (usually hard) rollback difficulty honestly, since an unprepared answer to this near-universal follow-up undermines the credibility of the whole story.

## Related

- [STAR Framework and Delivery Mechanics](01-star-framework-and-delivery.md) — the base structure this chapter specializes for migration narratives.
- [Distributed Transactions, Saga, and Outbox](../../10-distributed-systems/distributed-transactions-saga-and-outbox.md) — the canonical technical chapter covering the dual-write and reconciliation pattern this chapter's illustrative example draws its shape from.
