---
title: "Senior → Staff, Week 3 — Debt as an Economic Decision, Decisions as Organizational Memory"
document_type: study-pack
week: 3
track: senior-to-staff
status: draft
estimated_hours: 6
---

# Week 3 — Debt as an Economic Decision, Decisions as Organizational Memory

## Weekly Outcome

By the end of this week you can frame technical debt in economic terms (an interest rate, not a moral failing) and back that framing with a fitness function rather than a one-time cleanup sprint, and you can write an Architecture Decision Record that survives past the meeting where the decision was made.

## Why This Week Matters

The learning path calls [Technical Debt and Evolutionary Architecture](../../../syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md) "the economic-framing reflex a Staff engineer is expected to have, backed by fitness functions as the durable mechanism," and [Architecture Decision Records](../../../syllabus/17-architecture/architecture-decision-records.md) "organizational memory — decisions surviving past the meeting where they were made." Week 8 revisits technical debt from the advocacy side once this week has established the technical framing.

## Prerequisites

Weeks 1–2 of this pack. No new prerequisites beyond [Mid → Senior](../../mid-to-senior/README.md).

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Technical Debt and Evolutionary Architecture](../../../syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md) — read through its Staff-Level Discussion |
| Wed–Thu | [Architecture Decision Records](../../../syllabus/17-architecture/architecture-decision-records.md) — read through its Staff-Level Discussion |
| Fri | Reproduce the fitness-function demo below and read all three real ADR examples |
| Sat | Read both cross-referenced cookbook entries |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Technical Debt and Evolutionary Architecture | [`syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md`](../../../syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md) |
| 2 | Architecture Decision Records | [`syllabus/17-architecture/architecture-decision-records.md`](../../../syllabus/17-architecture/architecture-decision-records.md) |

## Hands-On Exercises

Predates this pack's construction; not re-verified here.

- [`practice/java/architecture/technical-debt-and-evolutionary-architecture/`](../../../practice/java/architecture/technical-debt-and-evolutionary-architecture/) — `CouplingFitnessFunction.java`, with `before/` and `after/` states to compare.
- [`practice/architecture/adr-examples/`](../../../practice/architecture/adr-examples/) — three real, worked ADRs (`adr-001-cqrs-for-order-reporting.md`, `adr-002-streaming-replication-for-dr.md`, `adr-003-backward-compatibility-for-orders-topic.md`) plus one labeled counter-example (`bad-example-missing-consequences.md`) showing what a weak ADR looks like.

## Production Cookbook Cross-Reference

- [`gradual-coupling-erosion-turning-a-core-class-into-a-release-bottleneck.md`](../../../production-cookbook/gradual-coupling-erosion-turning-a-core-class-into-a-release-bottleneck.md) — a checkout `OrderProcessor` class accumulating collaborator dependencies one reasonable pull request at a time across eighteen months. Pairs directly with Topic 1.
- [`adrs-asserting-decisions-without-citing-tested-evidence.md`](../../../production-cookbook/adrs-asserting-decisions-without-citing-tested-evidence.md) — uses this repository's own three worked ADRs above as the evidence for what separates a citation-backed decision record from an asserted one. Pairs directly with Topic 2.

Read both after finishing their matching chapter and confirm you can restate each diagnosis without looking.

## Interview Answer Drills

Answer, out loud, unprompted: "how would you convince a product manager to invest in paying down technical debt without a dedicated cleanup sprint?" and "what makes an ADR useful eighteen months after it was written, versus useless?" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Read `bad-example-missing-consequences.md` and rewrite it as a strong ADR, citing a specific, testable piece of evidence (a benchmark, a real incident, a measured coupling metric) the way `adr-001-cqrs-for-order-reporting.md` does.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: present your rewritten ADR from the System Design Exercise to a skeptical peer (real or imagined) and defend the specific evidence you cited, cold, in under 10 minutes.

## Review Checklist

- [ ] Completed both chapters' own Staff-Level Mastery Checklists.
- [ ] Reproduced the fitness-function demo and read all four ADR examples.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can state technical debt's economic framing (interest rate, not moral failing) and name a fitness function that would catch it recurring.
- [ ] Produced the rewritten ADR above with cited, testable evidence.
- [ ] Can explain what specifically made `bad-example-missing-consequences.md` weak.

## Retrospective

Note whether any ADR you've personally written (or seen) resembled the weak example more than the strong ones — asserting a decision without evidence is the single most common ADR failure mode this chapter identifies.

## Next Week

[Week 4 — Failure and Scale Across Regions](../week-04/README.md).
