---
title: "Senior → Staff, Week 5 — What to Store, What It Costs"
document_type: study-pack
week: 5
track: senior-to-staff
status: draft
estimated_hours: 6
---

# Week 5 — What to Store, What It Costs

## Weekly Outcome

By the end of this week you can choose a storage engine by starting from the access pattern rather than the technology's reputation, and can argue a scaling decision with a quantified cost number instead of a qualitative "it's more efficient."

## Why This Week Matters

The learning path calls [Storage Selection Trade-offs](../../../syllabus/11-system-design/storage-selection-tradeoffs.md) "the access-pattern-first method this whole path's other trade-off decisions reuse," and [Cloud Cost and Scaling Economics](../../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md) as introducing "cost as an explicit, quantified architectural dimension — a frequently-missing Staff signal." Every prior week in this pack has argued a trade-off in latency, consistency, or organizational terms; this week adds the dollar figure a real budget conversation actually needs.

## Prerequisites

Weeks 1–4 of this pack. [Mid → Senior](../../mid-to-senior/README.md) Week 4 (Database internals) — the access-pattern method assumes you already know what an index and a join actually cost.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Storage Selection Trade-offs](../../../syllabus/11-system-design/storage-selection-tradeoffs.md) — read through its Staff-Level Discussion |
| Wed–Thu | [Cloud Cost and Scaling Economics](../../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md) — read through its Staff-Level Discussion |
| Fri–Sat | Read both cross-referenced cookbook entries below |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Storage Selection Trade-offs | [`syllabus/11-system-design/storage-selection-tradeoffs.md`](../../../syllabus/11-system-design/storage-selection-tradeoffs.md) |
| 2 | Cloud Cost and Scaling Economics | [`syllabus/15-cloud/cloud-cost-and-scaling-economics.md`](../../../syllabus/15-cloud/cloud-cost-and-scaling-economics.md) |

## Hands-On Exercises

Neither chapter links a dedicated `practice/` demo of its own. Instead, use [`practice/mock-interviews/data-modelling-and-storage-tradeoffs-round.md`](../../../practice/mock-interviews/data-modelling-and-storage-tradeoffs-round.md) — a real 30-minute mock interview round whose stated competencies include "PostgreSQL vs. DynamoDB, argued both ways," directly matching Topic 1. Predates this pack's construction; not re-verified here.

## Production Cookbook Cross-Reference

- [`document-store-blocking-a-later-cross-order-transaction-need.md`](../../../production-cookbook/document-store-blocking-a-later-cross-order-transaction-need.md) — a catalog service built on a document store for its flexible per-category schema, which later needed a cross-order transaction the store couldn't give it. Pairs directly with Topic 1.
- [`dynamodb-migration-blocking-a-later-ad-hoc-query-need.md`](../../../production-cookbook/dynamodb-migration-blocking-a-later-ad-hoc-query-need.md) — a migration from RDS to DynamoDB evaluated against the service's then-current access pattern, which didn't survive a later ad-hoc query need. Pairs directly with Topic 1.
- [`reserved-capacity-purchase-increasing-cloud-spend.md`](../../../production-cookbook/reserved-capacity-purchase-increasing-cloud-spend.md) — a 1-year reserved capacity commitment sized to peak instance count that ended up increasing, not reducing, cloud spend. Pairs directly with Topic 2.

Read all three after finishing their matching chapter and confirm you can restate each diagnosis without looking.

## Interview Answer Drills

Answer, out loud, unprompted: "how do you decide between a relational store and a document store for a new service?" and "when does a reserved-capacity commitment save money, and when does it backfire?" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Take the `dynamodb-migration-blocking-a-later-ad-hoc-query-need` scenario and write the access-pattern analysis that should have preceded the migration decision — name the specific future query need that would have changed the choice, per Topic 1's own Decision Framework section.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Run the storage trade-offs round in [`practice/mock-interviews/data-modelling-and-storage-tradeoffs-round.md`](../../../practice/mock-interviews/data-modelling-and-storage-tradeoffs-round.md), timing yourself against its stated 30-minute duration.

## Review Checklist

- [ ] Completed both chapters' own Staff-Level Mastery Checklists.
- [ ] Ran (or attempted) the mock interview round above.
- [ ] Read all three cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can choose a storage engine starting from a stated access pattern rather than a technology's reputation.
- [ ] Produced the access-pattern analysis above.
- [ ] Can state a specific condition under which reserved capacity backfires.

## Retrospective

Note whether you've seen a storage decision made on reputation ("everyone uses DynamoDB for scale") rather than a stated access pattern — this is the single most common mistake both this week's cookbook entries share.

## Next Week

[Week 6 — Provisioning Ahead of Load, Multiplying the Team](../week-06/README.md).
