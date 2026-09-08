---
title: "Senior → Staff, Week 7 — Moving an Organization"
document_type: study-pack
week: 7
track: senior-to-staff
status: draft
estimated_hours: 7
---

# Week 7 — Moving an Organization

## Weekly Outcome

By the end of this week you can describe how you'd drive a technical direction in a room where you have no formal authority over the people you need to convince, and how you'd sequence a large-scale technical migration across multiple teams with the risk explicitly managed rather than hoped away.

## Why This Week Matters

The learning path calls [Cross-Team Influence Without Authority](../../../syllabus/19-leadership-staff/cross-team-influence-without-authority.md) "driving direction in rooms with no formal authority — a defining Senior/Staff differentiator," and [Leading Migrations and Large-Scale Technical Change](../../../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md) "sequencing and risk management at organizational scale, directly building on Topic 4" — Week 2's Strangler Fig pattern, now applied at the scale of multiple teams instead of one system.

## Prerequisites

Weeks 1–6 of this pack, in particular Week 2 (Strangler Fig) and Week 6 (Mentoring, the first Leadership & Staff topic).

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Cross-Team Influence Without Authority](../../../syllabus/19-leadership-staff/cross-team-influence-without-authority.md) — read through its L4 section |
| Wed–Thu | [Leading Migrations and Large-Scale Technical Change](../../../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md) — read through its L4 section |
| Fri | Read the cross-referenced cookbook entry below |
| Sat | Behavioral chapters below, draft both stories |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Cross-Team Influence Without Authority | [`syllabus/19-leadership-staff/cross-team-influence-without-authority.md`](../../../syllabus/19-leadership-staff/cross-team-influence-without-authority.md) |
| 2 | Leading Migrations and Large-Scale Technical Change | [`syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md`](../../../syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md) |

## Hands-On Exercises

Neither chapter links a dedicated `practice/` demo of its own (confirmed by grepping each chapter's own body for `practice/` references before writing this week) — both topics are organizational, not code-executable, practices.

## Production Cookbook Cross-Reference

- [`shared-customer-entity-forcing-a-three-team-migration-for-one-field.md`](../../../production-cookbook/shared-customer-entity-forcing-a-three-team-migration-for-one-field.md) — Billing, Support, and Marketing built directly against one shared `Customer` JPA entity with no context boundary between the three teams' actually-different needs, forcing a coordinated multi-team migration to change a single field. Pairs directly with Topic 2, and illustrates exactly the cross-team coordination problem Topic 1 addresses.

Read it after finishing both chapters and confirm you can restate the diagnosis without looking.

## Interview Answer Drills

Answer, out loud, unprompted: "you need three other teams to change their integration with your service, and none of them report to you — how do you get it done?" and "how do you sequence a migration that spans multiple teams so a partial rollback is still possible?" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Take the `shared-customer-entity` scenario and design the bounded-context split that should replace the shared entity, naming which team owns which resulting model and how the other two would consume it going forward.

## Behavioral Exercise

Read [Cross-Team Influence Without Authority](../../../syllabus/20-interview-preparation/behavioral/09-cross-team-influence-without-authority.md) and [Migrations and Large Technical Change](../../../syllabus/20-interview-preparation/behavioral/10-migrations-and-large-technical-change.md) — the behavioral-narrative counterparts to this week's two chapters. Draft or identify a real or realistic story for each, and fill both slots in your competency matrix per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: tell both stories from the Behavioral Exercise above out loud, in the 2-minute STAR length, then check each against [STAR Framework and Delivery Mechanics](../../../syllabus/20-interview-preparation/behavioral/01-star-framework-and-delivery.md)'s own delivery checklist.

## Review Checklist

- [ ] Completed both chapters' own L4 Mastery Checklists.
- [ ] Read the cross-referenced cookbook entry and can restate its diagnosis without looking.
- [ ] Have both stories filled in your story portfolio, not placeholders.

## Completion Criteria

- [ ] Can describe a concrete tactic for driving direction without formal authority (not just "build trust").
- [ ] Produced the bounded-context split design above.
- [ ] Have both real or realistic stories ready in the 2-minute STAR length, per this week's Behavioral Exercise.

## Retrospective

Note which of this week's two stories was harder to find a real example for — cross-team influence stories are frequently the thinnest slot in an engineer's portfolio precisely because the work is often invisible by design (it looks like the other team's idea if it worked).

## Next Week

[Week 8 — Advocacy and the Review Process Itself](../week-08/README.md).
