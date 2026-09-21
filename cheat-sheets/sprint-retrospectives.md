---
title: "Cheat Sheet: Sprint Retrospectives"
slug: sprint-retrospectives
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-1806
canonical: ../syllabus/18-engineering-practices/sprint-retrospectives.md
last_updated: 2026-09-21
---

# Sprint Retrospectives: Structure, Facilitation, and Avoiding Retro Theater

**Canonical chapter:** [`syllabus/18-engineering-practices/sprint-retrospectives.md`](../syllabus/18-engineering-practices/sprint-retrospectives.md)

## Core Mental Model

A retrospective has three real parts: look back, decide to change, follow up. Retro theater is skipping the third part — problems get named, nothing changes, sprint after sprint.

## Essential Definitions

- **Retro theater** — a retrospective that produces the ritual of naming problems without the substance of fixing them; recognizable by the same complaints recurring sprint after sprint.
- **Structured formats** — Start/Stop/Continue, Glad/Sad/Mad, 4Ls: a repeatable shape that produces specific, comparable observations instead of an open-ended drift.
- **Owner + Deadline** — the two required fields on every real action item; missing either means the item is nobody's job by default.

## Decision Table

| Situation | Correct approach |
|---|---|
| Action item proposed in a retro | Assign an explicit owner and deadline before the retro ends |
| Starting the next retro | Review the previous retro's action items first — done, still open, or dropped (and why) |
| Same complaint recurs 3+ sprints running | Real signal of retro theater — check whether prior action items ever had real owners |

## Common Pitfalls

- Treating the retro as a venting session with no concrete next step.
- Writing action items with no owner or deadline — the exact retro-theater pattern.
- Never revisiting the previous retro's action items, breaking the feedback loop.

## Interview Answer Skeleton

**30-sec:** Retro theater is a retrospective that names problems without fixing them — recognizable by the same complaints recurring sprint after sprint, usually because action items had no real owner or deadline.

**2-min:** Add: real demo proof — a real mechanical checker (`check_retro_action_items.py`) verifying every action item names an explicit Owner and Deadline, passing against a complete real example and failing against an incomplete one, naming exactly the missing fields.

**Staff-level framing:** A team whose retros never produce real action items is showing an early team-health symptom worth surfacing before it shows up as attrition or a missed commitment.

## Related

- syllabus/18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md
- syllabus/18-engineering-practices/estimation-and-story-points.md
- syllabus/18-engineering-practices/architecture-decision-records-and-technical-writing.md
