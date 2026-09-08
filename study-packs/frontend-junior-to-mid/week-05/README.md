---
title: "Frontend Junior → Mid, Week 5 — What Actually Happens When You Run npm run dev"
document_type: study-pack
week: 5
track: frontend-junior-to-mid
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 5 — What Actually Happens When You Run `npm run dev`

## Weekly Outcome

By the end of this week you can explain what a bundler actually does (module resolution, transformation, bundling, dev-server hot reload) and choose a styling approach (CSS Modules, Tailwind, or CSS-in-JS) for a stated project with a real trade-off reason.

## Why This Week Matters

Weeks 2–4 used tooling (Vite, Next.js's compiler) without ever explaining what it does. This week demystifies it — directly building on Week 1's CSS fundamentals for the styling half.

## Prerequisites

Weeks 1–4.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [Build Tooling: Vite vs. Next.js's Turbopack](../../../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md) (F-301) |
| Thu–Fri | [Styling Approaches: CSS Modules, Tailwind, and CSS-in-JS](../../../syllabus/21-frontend-web/nextjs-styling-approaches.md) (F-302) |
| Sat | Practice exercises from both chapters |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Build Tooling (F-301) | [`syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md`](../../../syllabus/21-frontend-web/nextjs-build-tooling-vite-vs-turbopack.md) |
| 2 | Styling Approaches (F-302) | [`syllabus/21-frontend-web/nextjs-styling-approaches.md`](../../../syllabus/21-frontend-web/nextjs-styling-approaches.md) |

## Hands-On Exercises

Both chapters cite real, verified demos — [`practice/frontend/build-tooling-comparison/`](../../../practice/frontend/build-tooling-comparison/) and [`practice/frontend/styling-approaches-comparison/`](../../../practice/frontend/styling-approaches-comparison/) (predate this pack; not re-verified here — see each chapter's own "Real Verified Demos" section). Run each locally and inspect the real build output (`dist/`) each one produces.

## Interview Answer Drills

Answer, out loud: "what does a bundler actually do to your source files before the browser sees them?" and "when would you choose Tailwind over CSS Modules, and what does that choice cost you?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described project (e.g., a small internal admin tool vs. a public marketing site with strict design-system constraints), pick a styling approach and defend it in under 2 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Inspected the real build output from both practice directories.
- [ ] Can name at least 2 real trade-offs between the three styling approaches.

## Completion Criteria

- [ ] Can explain module resolution and hot module replacement at a basic level, unprompted.
- [ ] Chose and justified a styling approach for 2 different stated project types.

## Retrospective

Note which styling approach you'd personally default to and why — this is a real, defensible interview answer if you can state the trade-off, not just a preference.

## Next Week

[Week 6 — Production-Shaped UI](../week-06/README.md).
