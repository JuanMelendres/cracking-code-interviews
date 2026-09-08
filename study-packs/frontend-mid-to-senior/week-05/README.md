---
title: "Frontend Mid → Senior, Week 5 — Concurrent Rendering and Applying Internals to Real Performance Problems"
document_type: study-pack
week: 5
track: frontend-mid-to-senior
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 5 — Concurrent Rendering and Applying Internals to Real Performance Problems

## Weekly Outcome

By the end of this week you can use `useTransition`/`useDeferredValue`/Suspense for data correctly, and profile a real React app to find and fix an unnecessary re-render or an unvirtualized long list.

## Why This Week Matters

Builds directly on Week 4's fiber model — concurrent rendering is fiber's scheduling flexibility made visible to the developer, and performance profiling is where all of Weeks 1–4's internals knowledge gets applied to a measured, real problem.

## Prerequisites

Week 4 — the fiber architecture.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [Concurrent React: Transitions, Deferred Values, and Suspense for Data](../../../syllabus/21-frontend-web/react-concurrent-rendering.md) (F-113) |
| Thu–Sat | [React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting](../../../syllabus/21-frontend-web/react-performance.md) (F-117) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Concurrent React (F-113) | [`syllabus/21-frontend-web/react-concurrent-rendering.md`](../../../syllabus/21-frontend-web/react-concurrent-rendering.md) |
| 2 | React Performance (F-117) | [`syllabus/21-frontend-web/react-performance.md`](../../../syllabus/21-frontend-web/react-performance.md) |

## Hands-On Exercises

Real demos exist at [`practice/frontend/react-concurrent/`](../../../practice/frontend/react-concurrent/) and [`practice/frontend/react-performance/`](../../../practice/frontend/react-performance/) (both predate this pack; not re-verified here). Run each locally and use React DevTools' Profiler to observe at least one real re-render, per F-117's own guidance.

## Interview Answer Drills

Answer, out loud: "what problem does `useTransition` actually solve that debouncing doesn't?" and "walk through how you'd diagnose an unnecessarily slow list render."

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described slow-rendering long list, propose a fix (virtualization, memoization, or code-splitting — correctly chosen, not just named) and explain why the other two wouldn't help as much, in under 5 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Used React DevTools Profiler to observe a real re-render in the practice demos.
- [ ] Can distinguish when virtualization vs. memoization vs. code-splitting is the right fix.

## Completion Criteria

- [ ] Can explain `useDeferredValue` vs. `useTransition`'s different use cases, unprompted.
- [ ] Diagnosed and fixed a real unnecessary re-render using the Profiler, not by guessing.

## Retrospective

Note whether you reached for memoization reflexively before checking if it was actually needed — F-107's own "when useMemo doesn't help" lesson from Junior-to-Mid applies directly here at Advanced depth.

## Next Week

[Week 6 — Testing Habits and Typing React Correctly](../week-06/README.md).
