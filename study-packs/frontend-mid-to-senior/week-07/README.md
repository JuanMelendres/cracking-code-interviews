---
title: "Frontend Mid → Senior, Week 7 — The State-Management Decision Framework, and Streaming the App Router"
document_type: study-pack
week: 7
track: frontend-mid-to-senior
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 7 — The State-Management Decision Framework, and Streaming the App Router

## Weekly Outcome

By the end of this week you can decide whether a given state genuinely needs a global store (Context, Redux Toolkit, Zustand, or server state via React Query/TanStack Query) or not, and implement a streaming Suspense boundary in the App Router correctly.

## Why This Week Matters

State management is the "when do you actually need a global store" question — one of the highest-frequency Senior frontend interview topics. Streaming/Suspense builds directly on Weeks 1–2's Server/Client boundary and rendering-strategy material.

## Prerequisites

Weeks 1–2 (Server/Client boundary, rendering strategies).

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [React State Management Landscape](../../../syllabus/21-frontend-web/react-state-management.md) (F-120) |
| Thu–Sat | [Streaming and Suspense Boundaries in the App Router](../../../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md) (F-206) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | React State Management Landscape (F-120) | [`syllabus/21-frontend-web/react-state-management.md`](../../../syllabus/21-frontend-web/react-state-management.md) |
| 2 | Streaming and Suspense Boundaries (F-206) | [`syllabus/21-frontend-web/nextjs-streaming-and-suspense.md`](../../../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md) |

## Hands-On Exercises

F-120's real demo exists at [`practice/frontend/react-state-management/`](../../../practice/frontend/react-state-management/) (predates this pack; not re-verified here). F-206's demo is cited from the chapter's own "Real Verified Demos" section, likely within the shared [`practice/frontend/react-nextjs-fundamentals/`](../../../practice/frontend/react-nextjs-fundamentals/) app.

## Interview Answer Drills

Answer, out loud: "when do you NOT need Redux/Zustand, even for a moderately complex app?" and "what does a Suspense boundary actually unlock in App Router streaming that a normal loading state doesn't?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described app (a dashboard with server data, form state, and a UI theme toggle), assign each piece of state to the correct management approach and justify it in under 3 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Can name the decision criteria between Context, Redux Toolkit, Zustand, and server state.
- [ ] Implemented a working Suspense boundary with a real slow-loading component.

## Completion Criteria

- [ ] Correctly assigned 3 different types of state to the right management approach.
- [ ] Can explain what streaming actually sends to the browser before the whole page is ready, unprompted.

## Retrospective

Note any past project where you reached for a global store when local state (or Context) would have been simpler — this is the single most common state-management interview follow-up.

## Next Week

[Week 8 — Edge-Level Request Logic and Real Authentication](../week-08/README.md).
