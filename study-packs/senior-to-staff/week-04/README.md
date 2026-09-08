---
title: "Senior → Staff, Week 4 — Failure and Scale Across Regions"
document_type: study-pack
week: 4
track: senior-to-staff
status: draft
estimated_hours: 7
---

# Week 4 — Failure and Scale Across Regions

## Weekly Outcome

By the end of this week you can pick a disaster-recovery pattern and defend its RPO/RTO trade-off with a concrete cost justification, explain the split-brain risk in an automated failover, and choose a partitioning scheme that won't become an 18-month scaling bottleneck.

## Why This Week Matters

The learning path frames [Multi-Region, Failover, and Disaster Recovery](../../../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md) around "RPO/RTO trade-offs and split-brain risk at the scale a Staff engineer is expected to own," and [Data Partitioning and Consistent Hashing](../../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md) as "the scaling decision underneath most 'how would this handle 10x load' follow-ups." Both topics share the same Staff signal: a decision made under today's constraints has to survive tomorrow's scale without a rewrite.

## Prerequisites

Weeks 1–3 of this pack. [Mid → Senior](../../mid-to-senior/README.md) Week 7 (Distributed systems) — CAP theorem and consistency models are assumed, not retaught.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Multi-Region, Failover, and Disaster Recovery](../../../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md) — read through its Staff-Level Discussion |
| Wed–Thu | [Data Partitioning and Consistent Hashing](../../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md) — read through its Staff-Level Discussion |
| Fri–Sat | Reproduce both hands-on demos below |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Multi-Region, Failover, and Disaster Recovery | [`syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md`](../../../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md) |
| 2 | Data Partitioning and Consistent Hashing | [`syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md`](../../../syllabus/10-distributed-systems/data-partitioning-and-consistent-hashing.md) |

## Hands-On Exercises

Predates this pack's construction; not re-verified here.

- [`practice/sql/multi-region-failover-and-dr/`](../../../practice/sql/multi-region-failover-and-dr/) — a real, runnable Docker Compose environment with an RPO demo (`rpo-demo.sh`, `rpo-archive-demo.sh`) and a genuine network-partition split-brain demo (`splitbrain-demo.sh`); run via `run-all-demos.sh`.
- [`practice/java/week-10/consistent-hashing/`](../../../practice/java/week-10/consistent-hashing/) — `ConsistentHashingDemo.java`.

## Production Cookbook Cross-Reference

- [`split-brain-from-promoting-a-standby-without-fencing-the-old-primary.md`](../../../production-cookbook/split-brain-from-promoting-a-standby-without-fencing-the-old-primary.md) — a real network partition (not a killed process) where an automated controller promotes a standby without confirming the old primary is dead. Pairs directly with Topic 1, and with the split-brain demo above.
- [`log-shipping-dr-silently-missing-its-configured-rpo-target.md`](../../../production-cookbook/log-shipping-dr-silently-missing-its-configured-rpo-target.md) — choosing between hot standby and log-shipping DR against a stated RPO target and its infrastructure cost. Pairs directly with Topic 1.
- [`launch-day-shard-key-becoming-an-18-month-scaling-bottleneck.md`](../../../production-cookbook/launch-day-shard-key-becoming-an-18-month-scaling-bottleneck.md) — a multi-tenant SaaS product sharded by `customer_id` at launch, matching the dominant query pattern at the time. Pairs directly with Topic 2.
- [`naive-hash-mod-n-cache-scaling-causing-a-database-overload.md`](../../../production-cookbook/naive-hash-mod-n-cache-scaling-causing-a-database-overload.md) — a caching layer using naive `hash(key) % N` node selection. Pairs directly with Topic 2, and with the consistent-hashing demo above.

Read all four after finishing their matching chapter and confirm you can restate each diagnosis without looking.

## Interview Answer Drills

Answer, out loud, unprompted: "how do you prevent split-brain in an automated failover, and what does fencing actually mean?" and "why does `hash(key) % N` break when you add a node, and how does consistent hashing fix it?" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Given the `launch-day-shard-key` scenario above, design a re-partitioning migration plan that avoids repeating the mistake — name the query pattern you'd validate against before committing to a shard key this time, per Topic 2's own Decision Framework section.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: run the `splitbrain-demo.sh` demo, then explain out loud, from what you observed (not from memory of the chapter), exactly why fencing would have prevented the outcome.

## Review Checklist

- [ ] Completed both chapters' own Staff-Level Mastery Checklists.
- [ ] Ran the multi-region Docker Compose demos and the consistent-hashing demo.
- [ ] Read all four cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can defend an RPO/RTO choice with a concrete cost justification.
- [ ] Can explain fencing and why promotion without it causes split-brain.
- [ ] Produced the re-partitioning migration plan above.

## Retrospective

Note which of this week's two topics felt more abstract without having run the demos — both are the kind of failure that's easy to state correctly in an interview and easy to miss in a design review without hands-on exposure to what the failure actually looks like.

## Next Week

[Week 5 — What to Store, What It Costs](../week-05/README.md).
