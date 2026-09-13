---
title: "Senior → Staff, Week 10 — Refactoring Discipline and Legacy Code"
document_type: study-pack
week: 10
track: senior-to-staff
status: draft
estimated_hours: 7
---

# Week 10 — Refactoring Discipline and Legacy Code

## Weekly Outcome

By the end of this week you can plan and execute a structural refactor without changing observable behavior, at a scale and risk tolerance appropriate to a Staff-level change, and you can design a safe-change strategy for code with no tests and no clear owner.

## Why This Week Matters

Refactoring Discipline — Topic 19 — is the mechanical discipline (behavior-preserving structural change) underneath every migration this path's earlier weeks discuss at the architectural level. Working with Legacy Code — Topic 20 — directly extends Week 2's Strangler Fig migration reasoning down to the code level: how do you change something safely when you can't yet trust it.

## Prerequisites

Week 2 of this pack (Strangler Fig and migration patterns) — this week applies the same incremental-safety reasoning at a smaller, code-level scale.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Refactoring Discipline](../../../syllabus/18-engineering-practices/refactoring-discipline.md) — read through its Staff-Level Discussion |
| Wed–Thu | [Working with Legacy Code](../../../syllabus/18-engineering-practices/working-with-legacy-code.md) — read through its Staff-Level Discussion |
| Fri | Reproduce both chapters' own practice demos |
| Sat | Interview answer drills below |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Refactoring Discipline | [`syllabus/18-engineering-practices/refactoring-discipline.md`](../../../syllabus/18-engineering-practices/refactoring-discipline.md) |
| 2 | Working with Legacy Code | [`syllabus/18-engineering-practices/working-with-legacy-code.md`](../../../syllabus/18-engineering-practices/working-with-legacy-code.md) |

## Hands-On Exercises

[`practice/java/engineering-practices/refactoring-discipline/`](../../../practice/java/engineering-practices/refactoring-discipline/) and [`practice/java/engineering-practices/legacy-code/`](../../../practice/java/engineering-practices/legacy-code/) — follow the link from each chapter to its own matching demo.

## Production Cookbook Cross-Reference

None. Both chapters state `production_scenarios: []` in their own front matter — confirmed directly, not assumed — and no `production-cookbook/` entry currently matches either topic. Stated honestly rather than forcing a loose citation, per this pack's own established convention (see Week 8's equivalent note).

## Interview Answer Drills

Answer, out loud: "how do you refactor a large method with no existing tests, without introducing a regression you can't detect?" and "what's your first concrete step when handed a legacy service nobody on the current team fully understands?" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Describe a real or realistic legacy system you'd need to safely modify (untested, unowned, business-critical) and lay out your first three concrete actions, in order, defending why that order minimizes risk.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: present your three-action plan from the System Design Exercise to a skeptical peer (real or imagined) and defend why you didn't start with a full rewrite, cold, in under 10 minutes.

## Review Checklist

- [ ] Completed both chapters' own Staff-Level Mastery Checklists.
- [ ] Reproduced both chapters' own practice demos.
- [ ] Confirmed directly (not assumed) that no cookbook cross-reference exists for either topic yet.

## Completion Criteria

- [ ] Can describe a behavior-preserving refactor plan for a stated large method with no existing tests.
- [ ] Produced the three-action legacy-system plan above with a defended ordering.
- [ ] Can name the specific risk each of your three actions is designed to reduce.

## Retrospective

Note whether the "no clear owner" condition in Working with Legacy Code describes any system you've personally touched — this is one of the most common real conditions a Staff engineer is expected to operate under, not a rare edge case.

## Next Week

[Week 11 — Git Internals and Collaboration Workflows](../week-11/README.md).
