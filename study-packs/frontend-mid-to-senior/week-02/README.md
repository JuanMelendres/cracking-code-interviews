---
title: "Frontend Mid → Senior, Week 2 — Rendering Strategies and Route Handlers"
document_type: study-pack
week: 2
track: frontend-mid-to-senior
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 2 — Rendering Strategies and Route Handlers

## Weekly Outcome

By the end of this week you can choose between SSR, SSG, and ISR for a stated page with a defensible reason, and decide where a piece of backend-for-frontend logic should live — a Next.js Route Handler versus a separate backend service.

## Why This Week Matters

Week 1 established the Server/Client boundary and data-fetching caching semantics. This week applies both to the two decisions every real Next.js app has to make: how a page gets rendered, and where request-handling logic lives.

## Prerequisites

Week 1 — the Server/Client boundary and `fetch()` caching semantics.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [Rendering Strategies: SSR, SSG, and ISR](../../../syllabus/21-frontend-web/nextjs-rendering-strategies.md) (F-205) |
| Thu–Sat | [Route Handlers: Building a Backend-for-Frontend Layer](../../../syllabus/21-frontend-web/nextjs-route-handlers.md) (F-207) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Rendering Strategies (F-205) | [`syllabus/21-frontend-web/nextjs-rendering-strategies.md`](../../../syllabus/21-frontend-web/nextjs-rendering-strategies.md) |
| 2 | Route Handlers (F-207) | [`syllabus/21-frontend-web/nextjs-route-handlers.md`](../../../syllabus/21-frontend-web/nextjs-route-handlers.md) |

## Hands-On Exercises

Both chapters cite their own real, verified demos — follow the link from each chapter's own "Real Verified Demos" section directly (likely within the shared [`practice/frontend/react-nextjs-fundamentals/`](../../../practice/frontend/react-nextjs-fundamentals/) app used across several weeks in this pack; not re-verified independently here).

## Production Cookbook Cross-Reference

No frontend-specific `production-cookbook/` entry exists yet for either topic — stated honestly rather than fabricated.

## Interview Answer Drills

Answer, out loud: "you're building a product page for an e-commerce site — SSR, SSG, or ISR, and why?" and "when should backend-for-frontend logic live in a Next.js Route Handler versus a separate Java/Spring service?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given 3 different page types (a real-time dashboard, a marketing blog post, a product catalog page updated hourly), assign each the correct rendering strategy and defend it in under 3 minutes total.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Can name all three rendering strategies' real trade-offs (freshness vs. cost vs. latency).
- [ ] Can state a concrete case where a Route Handler is the wrong choice.

## Completion Criteria

- [ ] Correctly assigned a rendering strategy to 3 different stated page types with a reason each.
- [ ] Can explain ISR's revalidation mechanism at a basic level, unprompted.

## Retrospective

Note whether your rendering-strategy choices matched the chapter's own recommendations — a mismatch here is worth revisiting before Week 3 builds further production concerns on top.

## Next Week

[Week 3 — Production Concerns: Discoverability and Real Performance Metrics](../week-03/README.md).
