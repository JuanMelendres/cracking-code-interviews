---
title: "Frontend Junior → Mid, Week 2 — The Actual Floor of React"
document_type: study-pack
week: 2
track: frontend-junior-to-mid
status: draft
estimated_hours: 8
last_reviewed: 2026-09-08
---

# Week 2 — The Actual Floor of React

## Weekly Outcome

By the end of this week you can build a small, correct React component from scratch using JSX, props, `useState`, event handlers, conditional rendering, and lists with a correct `key` — and explain, unprompted, why using an array index as `key` is a real bug, not a style preference.

## Why This Week Matters

This is the first React week in the pack, deliberately placed after Week 1's JavaScript/TypeScript floor — every concept here (destructured props, arrow-function event handlers, array `.map()` for lists) is plain JavaScript syntax applied to React's specific rendering model, not new syntax.

## Prerequisites

Week 1 — comfortable with JavaScript functions, destructuring, and array methods before this week.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [React Fundamentals: JSX, Components, Props, and State](../../../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md) (F-101–F-104) — read in full |
| Thu–Fri | Reproduce the chapter's real demo, then build one small component of your own from a blank file |
| Sat | Practice exercises from the chapter's own Interview Questions section |
| Sun | Review checklist below |

## Required Reading

[`syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`](../../../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md) (F-101–F-104) — read through its Decision Framework and Interview Answer Framework sections.

## Hands-On Exercises

The chapter cites a real, verified React app at [`practice/frontend/react-fundamentals/`](../../../practice/frontend/react-fundamentals/) (predates this pack; not re-verified here — see the chapter's own "Real Verified Demos" section for the exact files and captured evidence, including the real, measured list-key bug it demonstrates). Run it locally (`npm install`, `npm run dev`) and reproduce the list-key bug and its fix yourself.

## Interview Answer Drills

Answer, out loud, before checking the chapter: "why does React need a stable `key` for list items, and what actually breaks when you use the array index instead?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week — see Week 1's note on where behavioral preparation lives in this repository.

## Mock Interview

Self-check: build a small "to-do list" component from a blank file — text input, add button, rendered list with a correct `key`, a working delete button — timed at 30 minutes, narrating your choices out loud.

## Review Checklist

- [ ] Completed the chapter's own Mastery Checklist.
- [ ] Reproduced the real list-key bug and its fix.
- [ ] Built the to-do-list mock-interview exercise above without copying from the chapter.

## Completion Criteria

- [ ] Can explain props vs. state, unprompted, with a correct example of each.
- [ ] Can write a component using conditional rendering (`&&` or ternary) and a list with a correct, stable `key`.
- [ ] Can state why array-index keys break under reordering/insertion, with a concrete failure scenario.

## Retrospective

Note whether the list-key bug was intuitive or surprising — this exact bug recurs constantly in real production code and is one of the most common early-career React interview questions.

## Next Week

[Week 3 — The Hooks Every Real App Needs](../week-03/README.md).
