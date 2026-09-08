---
title: "Frontend Mid → Senior, Week 4 — Component Design Patterns, Then What's Actually Happening Underneath"
document_type: study-pack
week: 4
track: frontend-mid-to-senior
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 4 — Component Design Patterns, Then What's Actually Happening Underneath

## Weekly Outcome

By the end of this week you can choose between composition, render props, compound components, and HOCs for a stated design problem, and explain how React's fiber architecture actually schedules and diffs updates.

## Why This Week Matters

This is the first genuinely internals-depth week in the pack — Topic 8 (fiber) is the mechanism every later performance and concurrent-rendering topic (Weeks 5) builds on directly.

## Prerequisites

Weeks 1–3 — comfortable with the App Router; this week shifts focus back to core React at Advanced depth.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [React Component Patterns](../../../syllabus/21-frontend-web/react-component-patterns.md) (F-111) |
| Wed–Sat | [React Reconciliation and the Fiber Architecture](../../../syllabus/21-frontend-web/react-reconciliation-and-fiber.md) (F-112) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | React Component Patterns (F-111) | [`syllabus/21-frontend-web/react-component-patterns.md`](../../../syllabus/21-frontend-web/react-component-patterns.md) |
| 2 | Reconciliation and the Fiber Architecture (F-112) | [`syllabus/21-frontend-web/react-reconciliation-and-fiber.md`](../../../syllabus/21-frontend-web/react-reconciliation-and-fiber.md) |

## Hands-On Exercises

Real demos exist at [`practice/frontend/react-component-patterns/`](../../../practice/frontend/react-component-patterns/) and [`practice/frontend/react-reconciliation-and-fiber/`](../../../practice/frontend/react-reconciliation-and-fiber/) (both predate this pack; not re-verified here — see each chapter's own "Real Verified Demos" section).

## Interview Answer Drills

Answer, out loud: "why does React explicitly favor composition over inheritance?" and "what is a fiber, and why did React move away from the old stack-based reconciler?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: draw the fiber tree's reconciliation process on paper for a small update (a list re-render), narrating the diffing algorithm's key rule (same type + same key = reuse), cold, in under 10 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Reproduced both real demos.
- [ ] Can explain the fiber tree's whiteboard-level model unprompted.

## Completion Criteria

- [ ] Correctly chose a component pattern (composition/render props/compound/HOC) for 2 different stated design problems.
- [ ] Can explain reconciliation's diffing rule and why keys matter at this depth (not just "React needs them"), unprompted.

## Retrospective

Note whether the fiber-architecture material changed how you think about a re-render you've previously debugged in real code.

## Next Week

[Week 5 — Concurrent Rendering and Applying Internals to Real Performance Problems](../week-05/README.md).
