---
title: "Frontend Mid → Senior, Week 1 — The Server/Client Boundary and Data Fetching"
document_type: study-pack
week: 1
track: frontend-mid-to-senior
status: draft
estimated_hours: 7
---

# Week 1 — The Server/Client Boundary and Data Fetching

## Weekly Outcome

By the end of this week you can state, without hedging, exactly which code runs where in a Next.js App Router component tree, why a server secret can never leak into the client bundle even by accident, and what each `fetch()` caching option (`force-cache`, `no-store`, `revalidate`) actually does under the hood.

## Why This Week Matters

[`syllabus/00-overview/learning-paths/frontend-mid-to-senior.md`](../../../syllabus/00-overview/learning-paths/frontend-mid-to-senior.md) opens with this pair deliberately: the Server/Client boundary (Topic 1) is "the single most-tested modern Next.js concept," and data fetching (Topic 2) "builds directly on Topic 1's Server/Client boundary." Every later Next.js-specific week in this pack assumes both are solid.

## Prerequisites

[Frontend Junior → Mid](../../../syllabus/00-overview/learning-paths/frontend-junior-to-mid.md) complete — basic App Router usage (file-based routing, layouts) is assumed, not retaught.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | Server Components vs. Client Components (F-203) |
| Wed–Thu | Data Fetching in the App Router: Caching Semantics (F-204) |
| Fri–Sat | Hands-on exercises below |
| Sun | Review checklist below |

## Required Reading

- [`syllabus/21-frontend-web/nextjs-server-vs-client-components.md`](../../../syllabus/21-frontend-web/nextjs-server-vs-client-components.md) (F-203) — read through its Decision Framework and Interview Answer Framework sections.
- [`syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md`](../../../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md) (F-204).

## Hands-On Exercises

Both chapters cite real, verified demos in the same Next.js 16.3.1 App Router app, [`practice/frontend/react-nextjs-fundamentals/`](../../../practice/frontend/react-nextjs-fundamentals/) (not re-verified here; each chapter's own "Real Verified Demos" section names the exact files and captured evidence):

- F-203: `app/components/ServerSecretDemo.js`, `app/components/ClientCounter.js`, `app/server-vs-client/page.js`.
- F-204: `app/data-fetching/default/page.js`, `app/data-fetching/no-store/page.js`, `app/data-fetching/force-cache/page.js` (plus `app/components/RevalidateButton.js` and `app/actions.js`).

Run the app locally per its own README (`npm install`, then `npm run dev` / `npm run build && npm start`) and reproduce at least one contrast from each topic — e.g., grep the built client bundle for the server-secret value (F-203) and diff the responses from `/data-fetching/default` versus `/data-fetching/no-store` across repeated requests (F-204).

## Production Cookbook Cross-Reference

No frontend-specific entry exists yet in [`production-cookbook/`](../../../production-cookbook/README.md) for this week's topics — the cookbook's current contents are backend-focused (auth/filter-chain, locking, latency incidents). State this honestly rather than fabricating a link.

## Interview Answer Drills

Answer, out loud, before checking the chapters' own expected answers: "why can't a Server Component use `useState`?" and "if I don't pass any `cache` option to `fetch()`, what actually happens in this Next.js version?"

## Coding Problems

None dedicated this week — this pack is domain-depth focused, not DSA pattern practice.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week — behavioral preparation lives in [`syllabus/20-interview-preparation/behavioral/`](../../../syllabus/20-interview-preparation/behavioral/) as its own track.

## Mock Interview

Self-check: given a snippet mixing Server and Client Components with a prop passed from one to the other, identify which file needs `"use client"` and explain what would break if it were missing — cold, in under 5 minutes.

## Review Checklist

- [ ] Read both chapters through their Decision Framework sections.
- [ ] Ran `practice/frontend/react-nextjs-fundamentals/` locally and reproduced at least one demo per topic.
- [ ] Can explain the async-Client-Component runtime-error finding F-203's own chapter documents.

## Completion Criteria

- [ ] Can state the Server/Client boundary rule unprompted, with a correct example of what breaks when it's violated.
- [ ] Can name all four `fetch()` caching behaviors covered by F-204 and when each applies.

## Retrospective

Note whether the Server/Client boundary was already intuitive from Junior → Mid's basic App Router usage, or whether it required rereading — this is the foundation the rest of the Next.js-specific weeks in this pack build on.

## Next Week

[Week 2 — Rendering Strategies and Route Handlers](../week-02/README.md).
