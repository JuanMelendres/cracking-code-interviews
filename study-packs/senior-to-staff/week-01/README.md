---
title: "Senior → Staff, Week 1 — Should We Split It?"
document_type: study-pack
week: 1
track: senior-to-staff
status: draft
estimated_hours: 7
---

# Week 1 — Should We Split It?

## Weekly Outcome

By the end of this week you can answer "should we split this monolith into microservices" with a structured, condition-based argument instead of a reflex, and can describe how to keep a single deployable well-organized as the deliberate alternative when the answer is "not yet."

## Why This Week Matters

The learning path names [Microservice Decomposition and the Monolith Trade-off](../../../syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md) as "the single highest-IWI Staff-tier topic in the register — the canonical Staff system-design question," and sequences [The Modular Monolith as a Deliberate Choice](../../../syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md) immediately after it as "the direct follow-up once Topic 1's answer is 'not yet.'" Together they set the pattern this whole pack repeats: name the trade-off, state the condition that flips the answer, defend it to a skeptical peer.

## Prerequisites

[Mid → Senior](../../mid-to-senior/README.md) complete, in particular Week 3 (Spring internals) and Week 7 (Distributed systems) — decomposition trade-offs assume you already understand what a network hop and an independent deployment actually cost.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Microservice Decomposition and the Monolith Trade-off](../../../syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md) — read through its Staff-Level Discussion |
| Wed–Thu | [The Modular Monolith as a Deliberate Choice](../../../syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md) — read through its Staff-Level Discussion |
| Fri | Reproduce the modular-monolith boundary-enforcement demo below |
| Sat | Read both cross-referenced cookbook entries |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Microservice Decomposition and the Monolith Trade-off | [`syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md`](../../../syllabus/17-architecture/microservice-decomposition-and-monolith-tradeoff.md) |
| 2 | The Modular Monolith as a Deliberate Choice | [`syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md`](../../../syllabus/17-architecture/modular-monolith-as-a-deliberate-choice.md) |

## Hands-On Exercises

[`practice/java/architecture/modular-monolith-boundary-enforcement/`](../../../practice/java/architecture/modular-monolith-boundary-enforcement/) — a real module-boundary enforcement demo backing Topic 2. Predates this pack's construction; not re-verified here — follow the chapter's own link and README for what it demonstrates.

## Production Cookbook Cross-Reference

- [`premature-microservice-decomposition-doubling-on-call-burden.md`](../../../production-cookbook/premature-microservice-decomposition-doubling-on-call-burden.md) — a four-person team splitting into five independently-deployed, independently-on-call services six months after a "best practices" migration. Pairs directly with Topic 1.
- [`package-naming-convention-alone-failing-to-stop-a-module-boundary-violation.md`](../../../production-cookbook/package-naming-convention-alone-failing-to-stop-a-module-boundary-violation.md) — a modular monolith whose "internal" package convention has no build-level enforcement behind it. Pairs directly with Topic 2.

Read both after finishing their matching chapter and confirm you can restate each diagnosis without looking.

## Interview Answer Drills

Answer, out loud, unprompted: "a team wants to split their monolith into microservices — what do you ask before agreeing?" and "how do you enforce module boundaries in a monolith without a network boundary forcing the issue?" before checking either chapter's own Interview Answer Framework.

## Coding Problems

None — this pack is L4 systemic/organizational judgment, not coding practice.

## System Design Exercise

Take a real or hypothetical monolith you know (or the modular-monolith demo's own domain) and produce a one-page written case for or against decomposition, naming the specific condition (team count, deployment coupling, scaling mismatch) that would flip your answer — per Topic 1's own Decision Framework section.

## Behavioral Exercise

None this week — Weeks 6 through 8 cover the Leadership & Staff domain and carry this pack's real behavioral work, per [Story Portfolio Design](../../../syllabus/20-interview-preparation/behavioral/02-story-portfolio-design.md).

## Mock Interview

Self-check: have someone (or yourself, cold) push back on your Week 1 decomposition case with "but won't this limit our ability to scale the checkout path independently?" and answer without retreating to a generic "it depends."

## Review Checklist

- [ ] Completed both chapters' own Staff-Level Mastery Checklists.
- [ ] Reproduced the modular-monolith boundary-enforcement demo above.
- [ ] Read both cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can defend a decomposition decision to a skeptical peer, naming the specific condition that would change the answer, per each chapter's own Staff-Level Discussion section.
- [ ] Produced the one-page decomposition case above.
- [ ] Can explain why a package-naming convention alone does not enforce a module boundary.

## Retrospective

Note which way your own instinct leans by default — toward splitting or toward staying monolithic — and why; a Staff engineer is expected to argue either side convincingly depending on the actual conditions, not to have a fixed preference.

## Next Week

[Week 2 — Read/Write Separation and Incremental Migration](../week-02/README.md).
