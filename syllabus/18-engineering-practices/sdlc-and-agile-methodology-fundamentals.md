---
title: "SDLC and Agile Methodology Fundamentals"
slug: sdlc-and-agile-methodology-fundamentals
document_type: syllabus-topic
domain: 18-engineering-practices
topic_id: T-2212
status: draft
version: 1.0
last_updated: 2026-09-08
mastery_levels_covered: [L1, L2]
prerequisites: []
related:
  - working-with-legacy-code.md
  - ../19-leadership-staff/leading-migrations-and-large-technical-change.md
  - ../08-testing/test-strategy-and-test-doubles.md
practice: []
production_scenarios: []
interview_paths: [junior-to-mid, interview-emergency-sprint]
official_references:
  - https://agilemanifesto.org/
  - https://scrumguides.org/scrum-guide.html
source_history: []
---

# SDLC and Agile Methodology Fundamentals

This is **T-2212**, a Junior Fundamentals addition to `18-engineering-practices` (reserved range `T-2200`–`T-2299`). A 2026-09-08 review of a connected Notion knowledge base — used read-only, per `CLAUDE.md`'s Non-Negotiable Notion Safety Rules, nothing written or modified — confirmed "what is the SDLC," "what is Agile vs. Waterfall," and "what is a sprint" as real, commonly-asked questions with zero coverage anywhere in this domain: this repository's existing engineering-practices chapters ([Refactoring Discipline](refactoring-discipline.md), [Working with Legacy Code](working-with-legacy-code.md), [Code Review Standards](code-review-standards-and-practice.md), [Git Internals and Collaboration Workflows](git-internals-and-collaboration-workflows.md)) all assume the reader already knows what phase of a development process they're operating in — none of them teach the process model itself.

## 1. Why This Matters

Every engineer, at any level, works inside some development process — and "walk me through the SDLC" or "what's the difference between Agile and Waterfall" is a real, common early-career interview question precisely because it's assumed too basic to ever get taught explicitly. A candidate who has been doing daily standups and two-week sprints for years can still struggle to name the SDLC's phases in order, or explain what Waterfall actually was and why most teams moved away from it, simply because "the process" is usually absorbed by osmosis, not studied directly.

## 2. Prerequisites

None — this is a true entry point, independent of any specific programming language or technology.

## 3. Foundation (L1)

**The Software Development Life Cycle (SDLC)** is the sequence of phases a piece of software moves through from initial idea to retirement. The phases, in their classic order, are: **Requirements** (what does the software need to do), **Design** (how will it be built, at a high level and low level), **Implementation** (writing the actual code), **Testing** (verifying it does what was intended), **Deployment** (releasing it to real users), and **Maintenance** (fixing bugs, adding small improvements, and eventually retiring the system). Every software process model — Waterfall, Agile, and everything between — is a different answer to "in what order, and how repeatedly, do we move through these same six phases."

**The Waterfall model** runs through all six SDLC phases exactly once, strictly in order, with each phase fully completing before the next begins — Requirements are entirely finalized before Design starts, Design is entirely finalized before Implementation starts, and so on. It is called "Waterfall" because progress flows in one direction, like water over a series of steps, with no expected return to an earlier phase.

**The Agile model** runs through the same six phases repeatedly, in short cycles (commonly called **sprints**, typically 1–4 weeks), each cycle producing a small, working increment of the software rather than waiting for the entire system to be complete. **Scrum** is the most widely used Agile framework in industry: it organizes work into fixed-length sprints, with a **sprint planning** meeting at the start, a **daily standup** to surface blockers, and a **sprint review**/**retrospective** at the end.

## 4. Core Concepts (L2)

**Why the industry moved from Waterfall toward Agile**: Waterfall's single, sequential pass assumes requirements can be fully and correctly specified up front — a real requirement discovered or changed during Implementation forces an expensive return to an earlier, supposedly finished phase. Agile's repeated short cycles are a deliberate response to that risk: each sprint delivers a working increment that can be shown to real stakeholders, so a wrong assumption is caught within weeks, not discovered only at the end of a months-long Requirements-to-Deployment sequence.

**Waterfall is not simply "the old, bad way"** — it genuinely fits contexts where requirements really are stable and well-understood up front and the cost of late-discovered change is extremely high (certain regulated, safety-critical, or fixed-contract engagements), which is why it hasn't disappeared entirely even though most commercial software teams have moved to some Agile variant.

**The four values of the Agile Manifesto** (the actual 2001 source document every "Agile" framework traces back to) are stated as preferences, not absolutes: *individuals and interactions* over processes and tools; *working software* over comprehensive documentation; *customer collaboration* over contract negotiation; *responding to change* over following a plan. Each pairing explicitly says the item on the right still has value — "while there is value in the items on the right, we value the items on the left more."

**Scrum's core artifacts and roles**: a **Product Backlog** (the full, prioritized list of everything that might be built), a **Sprint Backlog** (the subset pulled into the current sprint), and an **Increment** (the real, working software produced by the sprint) — maintained by a **Product Owner** (prioritizes the backlog), a **Scrum Master** (facilitates the process and removes blockers), and the **Development Team** (builds the increment).

## 5. How It Works Internally (L3)

Agile's real mechanism for reducing risk is the **feedback loop length**, not any specific ceremony — a 2-week sprint means a wrong assumption about what users actually need is discovered, at worst, 2 weeks after it was made, versus potentially months in a Waterfall project's single Requirements-to-Deployment pass. This is why Agile teams treat "we finished this sprint's work but never showed it to anyone" as a process failure even if every ticket was technically completed — the entire value of the short cycle depends on the increment actually being reviewed by someone who can say "that's not quite what we needed" while there's still time to adjust cheaply.

## 6. Practical Usage

Use the SDLC's six phases as a shared vocabulary regardless of which process model a team follows — "we're still in Design" or "this needs to go back to Requirements" communicates precisely even on a team that has never used the word "Waterfall." When joining a new team, identify which process model is actually in use (many teams run something they call "Agile" that's really closer to short, undisciplined Waterfall cycles) before assuming standard Scrum ceremonies and artifacts apply exactly as documented.

## 7. Examples

This chapter is intentionally conceptual — there is no Java demo to compile, since SDLC/Agile is a process model, not executable code. The representative scenario below illustrates the same feature request handled two ways, to make the risk difference concrete rather than abstract:

**Representative scenario, not a real incident:** a team is asked to build a "recommend related products" feature. Under a strict Waterfall approach, Requirements are fully specified (exact recommendation algorithm, exact UI placement, exact data sources) before any code is written; six weeks later, at Deployment, stakeholders see it for the first time and realize the recommendations need to reflect real-time inventory, not the daily batch feed the Requirements phase assumed — forcing a return to Requirements and Design after most of the implementation budget is already spent. Under an Agile approach, the first sprint ships a minimal version using the daily batch feed, stakeholders see it working in week 2, immediately flag the real-time inventory need, and the very next sprint addresses it — the same discovery happens, but 4 weeks in in instead of 6, with far less implementation work to rework.

## 8. Common Mistakes

- **Treating "Agile" as a synonym for "no planning" or "no process"** — Scrum has real, defined ceremonies and artifacts; skipping all of them isn't Agile, it's just unstructured.
- **Running 2-week sprints without ever actually showing the increment to a real stakeholder** — this discards the entire risk-reduction benefit Agile is built around (Section 5) while keeping only the ceremony overhead.
- **Assuming Waterfall is simply obsolete** — it remains a defensible choice for genuinely stable, well-understood requirements with a high cost of late change.
- **Confusing Scrum (a specific framework) with Agile (the broader philosophy the Manifesto describes)** — Scrum is one popular way to be Agile, not the definition of Agile itself; Kanban is a real, common alternative.

## 9. Edge Cases

- A team can technically complete every planned sprint task while still failing at Agile's actual purpose, if the increment is never reviewed by a real stakeholder who could catch a wrong assumption early (Section 8).
- Some organizations run a hybrid ("Water-Scrum-Fall"): Waterfall-style upfront requirements and a final formal release-approval gate, with Agile-style sprints in between for the actual implementation — a real, common compromise, not a pure form of either model.

## 10. Performance Implications

Not applicable — this is a process-and-organizational topic, not a runtime-performance one.

## 11. Trade-offs

| Model | When it helps | When it hurts |
|---|---|---|
| Waterfall | Requirements genuinely stable, high cost of late change, regulated/fixed-contract context | Requirements likely to change; late discovery of a wrong assumption is expensive |
| Agile/Scrum | Requirements likely to evolve, value in frequent stakeholder feedback | Requires real stakeholder engagement every sprint to deliver its actual benefit; ceremony overhead if run without discipline |

## 12. Senior-Level Considerations (L3)

A Senior engineer should be able to diagnose when a team's stated process ("we do Scrum") doesn't match its actual behavior (no real stakeholder review, backlog never re-prioritized, sprints that are really just 2-week Waterfall cycles) — and to explain, concretely, which specific missing practice is undermining which specific benefit, rather than offering a generic "we should be more Agile" observation.

## 13. Staff/System-Level Considerations (L4)

At Staff scope, process-model choice is itself an organizational-risk decision connected directly to [Leading Migrations and Large-Scale Technical Change](../19-leadership-staff/leading-migrations-and-large-technical-change.md): a large, multi-team migration with genuinely well-understood, stable requirements and a hard regulatory deadline may be better served by a more Waterfall-like staged plan with formal gates than by treating every team's independent sprint cadence as automatically superior — the right process model is chosen for the actual risk profile of the work, not applied as an unquestioned organizational default.

## 14. Production Scenarios

Not applicable in the code-incident sense this deliverable otherwise uses — this topic is a development-process model, not a runtime system.

## 15. Interview Questions

### Question 1: What are the phases of the SDLC, and how does Agile change how a team moves through them?

**Expected answer:** Requirements, Design, Implementation, Testing, Deployment, Maintenance. Waterfall runs through them once, in strict order; Agile runs through the same phases repeatedly in short sprints, each producing a working increment.

**Common mistakes:** naming Agile/Scrum ceremonies (standup, retrospective) as if they were SDLC phases themselves, rather than a process model layered on top of the same underlying phases.

**Follow-up questions:** "why did the industry move toward Agile?" (Expected: shorter feedback loops catch wrong assumptions earlier and cheaper — Section 4.)

**Senior-level expectations:** can name a real context where Waterfall is still the defensible choice.

**Staff-level expectations:** connects process-model choice to actual risk profile and organizational context, not treated as a default preference.

### Question 2: What's the difference between Agile and Scrum?

**Expected answer:** Agile is the broader philosophy (the Manifesto's four values); Scrum is one specific, widely used framework for practicing it, with its own defined roles (Product Owner, Scrum Master, Development Team), artifacts (Product Backlog, Sprint Backlog, Increment), and ceremonies (sprint planning, daily standup, review, retrospective). Kanban is a real, different Agile framework.

**Minimum acceptable answer:** knows Scrum is a specific framework, not a synonym for Agile itself.

**Strong Senior answer:** can name at least the Scrum artifacts and roles and briefly explain what each one is for.

**Staff-level extension:** can diagnose a team practicing "Scrum in name only" and identify the specific missing practice undermining it.

**Common mistakes:** treating "Agile," "Scrum," and "sprint" as fully interchangeable terms.

**Likely follow-ups:** "what's Kanban, and how does it differ from Scrum?" (Kanban uses a continuous flow with work-in-progress limits rather than fixed-length sprints — a real, distinct Agile framework, not covered in depth by this chapter.)

**Evaluation criteria:** correctly distinguishes the philosophy from the specific framework, and names at least 2 real Scrum artifacts/roles/ceremonies accurately.

## 16. Coding/Practice Exercises

Not applicable — this is a process-methodology topic with no coding component.

## 17. Debugging Exercises

Given a described team that "does 2-week sprints" but has never once shown a real stakeholder the resulting increment, diagnose which specific Agile benefit is being lost and why (Section 8's first mistake).

## 18. Design Exercises

Design a lightweight process for a small, 4-person team building a new internal tool with genuinely well-understood, stable requirements and no external stakeholder pressure — decide, in writing, whether a Waterfall-style single pass, a full Scrum implementation, or a lighter hybrid is the better fit, and justify the choice against Section 11's trade-off table rather than defaulting to whichever model is currently fashionable.

## 19. Further Reading

- [Refactoring Discipline](refactoring-discipline.md), [Working with Legacy Code](working-with-legacy-code.md) — engineering practices that apply inside any SDLC/process model, once you're in the Implementation phase.
- [Leading Migrations and Large-Scale Technical Change](../19-leadership-staff/leading-migrations-and-large-technical-change.md) — the Staff-level process-model-choice discussion referenced in Section 13.
- [Test Strategy, the Pyramid, and Test Doubles](../08-testing/test-strategy-and-test-doubles.md) — the Testing phase, covered at real depth.
- [Agile Manifesto](https://agilemanifesto.org/) — the actual 2001 source document.
- [Scrum Guide](https://scrumguides.org/scrum-guide.html) — the official, current Scrum framework definition.

## 20. Mastery Checklist

- [ ] Can name all six SDLC phases in order, from memory.
- [ ] Can explain the real difference between Waterfall and Agile in terms of feedback-loop length, not just "Agile is more modern."
- [ ] Can name Scrum's core artifacts (Product Backlog, Sprint Backlog, Increment) and roles (Product Owner, Scrum Master, Development Team).
- [ ] Can state a real, defensible context where Waterfall is still the better choice.
- [ ] Can diagnose a team practicing "Scrum in name only" and name the specific missing practice.
