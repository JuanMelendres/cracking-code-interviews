---
title: "Senior → Staff, Week 9 — Applying Patterns Judiciously, Reviewing Code at Scale"
document_type: study-pack
week: 9
track: senior-to-staff
status: draft
estimated_hours: 7
---

# Week 9 — Applying Patterns Judiciously, Reviewing Code at Scale

## Weekly Outcome

By the end of this week you can defend when a design pattern earns its complexity versus when it's premature over-engineering (with a real, demonstrated example of each), and you can shape code quality across a team through review standards and practice, not just your own commits.

## Why This Week Matters

Design Patterns, Applied — Topic 17 — is the Staff-level half of pattern knowledge: judicious application, including recognizing a pattern that shouldn't have been reached for. Code Review Standards and Practice — Topic 18 — is the technical counterpart to Week 8's organizational review practice (design reviews/RFCs): shaping quality through the review process itself, at the level of individual pull requests rather than architectural proposals.

## Prerequisites

[Mid → Senior](../../mid-to-senior/README.md) Week 11 (Software Design), for SOLID/OOD fundamentals this week's judicious-application judgment builds on.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Design Patterns, Applied](../../../syllabus/04-software-design/design-patterns-applied.md) — read through its Staff-Level Discussion |
| Wed–Thu | [Code Review Standards and Practice](../../../syllabus/18-engineering-practices/code-review-standards-and-practice.md) — read through its Staff-Level Discussion |
| Fri | Reproduce the four pattern demos below |
| Sat | Read both cross-referenced cookbook entries |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Design Patterns, Applied | [`syllabus/04-software-design/design-patterns-applied.md`](../../../syllabus/04-software-design/design-patterns-applied.md) |
| 2 | Code Review Standards and Practice | [`syllabus/18-engineering-practices/code-review-standards-and-practice.md`](../../../syllabus/18-engineering-practices/code-review-standards-and-practice.md) |

## Hands-On Exercises

[`practice/java/design-patterns/applied-gof/src/`](../../../practice/java/design-patterns/applied-gof/src/) — `StrategyDemo.java`, `BuilderDemo.java`, `DecoratorDemo.java`, `SingletonPitfallsDemo.java` (real, executed output from OpenJDK 21.0.12). Reproduce all four, paying specific attention to `SingletonPitfallsDemo.java`'s demonstrated failure mode.

## Production Cookbook Cross-Reference

- [`naive-lazy-singleton-double-constructing-a-connection-pool-at-cold-start.md`](../../../production-cookbook/naive-lazy-singleton-double-constructing-a-connection-pool-at-cold-start.md) — pairs directly with Topic 1's `SingletonPitfallsDemo.java`.
- [`adrs-asserting-decisions-without-citing-tested-evidence.md`](../../../production-cookbook/adrs-asserting-decisions-without-citing-tested-evidence.md) — cited directly in Topic 2's own front matter as its production scenario; already read in Week 3 from the ADR side, revisit it here specifically from the review-standards angle (what a reviewer should have caught).

## Interview Answer Drills

Answer, out loud: "describe a pattern you reached for that turned out to be premature, and how you'd recognize that earlier next time" and "what makes a code review comment actually change the codebase's trajectory, versus a comment that's technically correct but ineffective?" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Re-read `bad-example-missing-consequences.md` (from Week 3) as a reviewer would, applying Topic 2's own review standards, and write the specific review comments you'd leave — this connects code-review practice directly to the ADR-quality judgment Week 3 already built.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: given a described pull request adding a new Strategy-pattern abstraction for a case with only one real implementation, argue both sides (ship it for future flexibility vs. reject it as premature) and state which side you'd actually take and why, out loud, in under 10 minutes.

## Review Checklist

- [ ] Completed both chapters' own Staff-Level Mastery Checklists.
- [ ] Reproduced all four pattern demos, including the singleton pitfall.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can name a real or realistic example of a pattern earning its complexity and one of a pattern being premature.
- [ ] Can explain the singleton double-construction pitfall and its fix.
- [ ] Wrote the reviewer-perspective comments for `bad-example-missing-consequences.md` above.

## Retrospective

Note whether any pattern in a codebase you've worked on resembles the singleton pitfall — a lazily-initialized shared resource with no thread-safety guarantee is one of the most common real instances of this exact class of bug.

## Next Week

[Week 10 — Refactoring and Legacy Code](../week-10/README.md).
