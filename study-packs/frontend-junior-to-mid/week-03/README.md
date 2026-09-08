---
title: "Frontend Junior → Mid, Week 3 — The Hooks Every Real App Needs"
document_type: study-pack
week: 3
track: frontend-junior-to-mid
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 3 — The Hooks Every Real App Needs

## Weekly Outcome

By the end of this week you can use `useEffect` correctly (including cleanup and dependency arrays), avoid the most common `useMemo`/`useCallback`/`useContext` misuses, and choose between `useState` and `useReducer` for a stated scenario with a reason.

## Why This Week Matters

Week 2 covered React's floor (`useState`, props, JSX). This week covers the three hook families every real app needs beyond that: side effects, performance/context primitives, and reducer-based state — all building directly on Week 1's closures material (stale closures are the single most common `useEffect` bug).

## Prerequisites

Weeks 1–2 — comfortable with closures and basic component/state usage.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [React Hooks: useEffect and useRef](../../../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md) (F-105/F-106) |
| Wed–Thu | [React Memoization and Context: useMemo, useCallback, useContext](../../../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md) (F-107/F-108) |
| Fri | [React useReducer and Custom Hooks](../../../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md) (F-109/F-110) |
| Sat | Practice exercises from all three chapters |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | useEffect and useRef (F-105/F-106) | [`syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md`](../../../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md) |
| 2 | useMemo, useCallback, useContext (F-107/F-108) | [`syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`](../../../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md) |
| 3 | useReducer and Custom Hooks (F-109/F-110) | [`syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md`](../../../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md) |

## Hands-On Exercises

Each chapter cites its own real, verified demo — [`practice/frontend/react-hooks/`](../../../practice/frontend/react-hooks/), [`practice/frontend/react-memo-and-context/`](../../../practice/frontend/react-memo-and-context/), [`practice/frontend/react-reducer-and-custom-hooks/`](../../../practice/frontend/react-reducer-and-custom-hooks/) (all predate this pack; not re-verified here — see each chapter's own "Real Verified Demos" section). Run each locally and reproduce the stale-closure bug in F-105/F-106's demo specifically — it is the week's single most interview-relevant bug.

## Interview Answer Drills

Answer, out loud: "what does an empty dependency array actually mean for `useEffect`, and what's the risk?" and "when does `useMemo` NOT help, even though it looks like it should?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a `useEffect` with a missing dependency (provided by the chapter's own exercises or one you write yourself), find the bug and fix it, cold, in under 10 minutes.

## Review Checklist

- [ ] Completed all three chapters' own Mastery Checklists.
- [ ] Reproduced the stale-closure bug and its fix.
- [ ] Can state, from memory, when `useReducer` is the better choice over `useState`.

## Completion Criteria

- [ ] Wrote a `useEffect` with correct cleanup for a subscription/timer-shaped example, from a blank file.
- [ ] Can explain the stale-closure bug's mechanism, not just that it happens.
- [ ] Extracted at least one custom hook from repeated logic, correctly.

## Retrospective

Note which hook (useEffect, useMemo/useCallback, or useReducer) took longest to feel natural — this predicts which one deserves a second pass before Week 4.

## Next Week

[Week 4 — From "a React App" to a Real, Shippable One](../week-04/README.md).
