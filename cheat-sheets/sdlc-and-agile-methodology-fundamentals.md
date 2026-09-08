---
title: "Cheat Sheet: SDLC and Agile Methodology Fundamentals"
slug: sdlc-and-agile-methodology-fundamentals
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-2212
canonical: ../syllabus/18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md
last_updated: 2026-09-08
---

# SDLC and Agile Methodology Fundamentals

**Canonical chapter:** [`syllabus/18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md`](../syllabus/18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md)

## Core Mental Model

The SDLC's 6 phases (Requirements, Design, Implementation, Testing, Deployment, Maintenance) are shared by every process model. Waterfall runs through them once, in order. Agile runs through them repeatedly in short sprints, each producing a working increment — shortening the feedback loop, not skipping the phases.

## Essential Definitions

- **SDLC** — Requirements → Design → Implementation → Testing → Deployment → Maintenance.
- **Waterfall** — one strict pass through all phases; each fully completes before the next starts.
- **Agile** — repeated short cycles (sprints) through the same phases, each yielding a working increment.
- **Scrum** — the most common Agile framework: sprints, daily standup, sprint review/retrospective, Product/Sprint Backlog, Increment.

## Decision Table

| Context | Better fit |
|---|---|
| Requirements genuinely stable, high cost of late change, regulated | Waterfall |
| Requirements likely to evolve, value in frequent feedback | Agile / Scrum |
| Continuous flow, no fixed iteration length | Kanban (a different Agile framework) |

## Common Pitfalls

- Treating "Agile" as "no process" — Scrum has real, defined roles/artifacts/ceremonies.
- Running sprints without ever showing the increment to a real stakeholder — discards Agile's actual risk-reduction benefit.
- Assuming Waterfall is simply obsolete — it's a real, defensible choice for stable requirements with high cost of late change.
- Confusing Scrum (a specific framework) with Agile (the broader philosophy).

## Interview Answer Skeleton

**30-sec:** SDLC has 6 phases. Waterfall runs through once; Agile runs through repeatedly in short sprints, each producing a working increment.

**2-min:** Add why the shift happened (shorter feedback loop catches wrong assumptions weeks sooner, not months) and name Scrum's core artifacts/roles.

**Whiteboard:** Draw the 6 SDLC phases as boxes in a row; below them, one arrow looping back to the start (Agile) vs. one arrow running straight through once (Waterfall).

**Staff-level framing:** Process-model choice is a real organizational-risk decision tied to the actual work's risk profile, not an unquestioned default.

## Related

- syllabus/18-engineering-practices/refactoring-discipline.md
- syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md
- syllabus/08-testing/test-strategy-and-test-doubles.md
