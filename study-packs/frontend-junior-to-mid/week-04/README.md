---
title: "Frontend Junior → Mid, Week 4 — From \"a React App\" to a Real, Shippable One"
document_type: study-pack
week: 4
track: frontend-junior-to-mid
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 4 — From "a React App" to a Real, Shippable One

## Weekly Outcome

By the end of this week you can explain why a real app reaches for a meta-framework instead of plain React/Vite, and build a small multi-page Next.js App Router app using nested layouts and route groups.

## Why This Week Matters

Weeks 2–3 built real React skill inside a single-page Vite app. This week is the transition to a genuinely shippable structure — file-based routing, layouts, and route groups are the first Next.js-specific concepts this pack introduces, and every later Next.js week (and the entire Frontend Mid → Senior pack) assumes this foundation.

## Prerequisites

Weeks 1–3 — solid core React (components, props, state, the three hook families).

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Next.js's Role: File-Based Routing and Why a Meta-Framework](../../../syllabus/21-frontend-web/nextjs-fundamentals.md) (F-201) |
| Wed–Fri | [Next.js App Router Fundamentals: Nested Layouts and Route Groups](../../../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md) (F-202) |
| Sat | Build a small 3–4 page app using both chapters' patterns |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Next.js's Role (F-201) | [`syllabus/21-frontend-web/nextjs-fundamentals.md`](../../../syllabus/21-frontend-web/nextjs-fundamentals.md) |
| 2 | App Router Fundamentals (F-202) | [`syllabus/21-frontend-web/nextjs-app-router-fundamentals.md`](../../../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md) |

## Hands-On Exercises

Both chapters cite real, verified demos in [`practice/frontend/react-nextjs-fundamentals/`](../../../practice/frontend/react-nextjs-fundamentals/) (predates this pack; not re-verified here — see each chapter's own "Real Verified Demos" section for the exact routes/files). Run the app locally (`npm install`, `npm run dev`) and trace how a URL maps to a file in `app/`.

## Interview Answer Drills

Answer, out loud: "why would a team choose Next.js over plain React with Vite for a real product?" and "what's the difference between a route group and a normal folder in the App Router?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: sketch the file structure for a small app with a marketing home page, a dashboard section with its own layout, and an auth route group — without running it, then verify against the App Router's actual conventions.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Ran the real Next.js demo locally and traced at least 2 routes to their files.
- [ ] Built the 3–4 page mock-interview app above without copying from the chapter.

## Completion Criteria

- [ ] Can explain file-based routing's mapping rule unprompted.
- [ ] Built a working nested layout (e.g., a shared header/sidebar across a route group).
- [ ] Can state, with a reason, one concrete case where plain React/Vite is still the right choice over Next.js.

## Retrospective

Note any assumption from plain React (Weeks 2–3) that didn't carry over cleanly to the App Router's file-based model — this is the friction point most Junior-to-Mid readers hit first.

## Next Week

[Week 5 — What Actually Happens When You Run `npm run dev`](../week-05/README.md).
