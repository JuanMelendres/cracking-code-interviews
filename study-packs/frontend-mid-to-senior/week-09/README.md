---
title: "Frontend Mid → Senior, Week 9 — Mutations Without an API Layer, and Real Deployment Trade-offs"
document_type: study-pack
week: 9
track: frontend-mid-to-senior
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 9 — Mutations Without an API Layer, and Real Deployment Trade-offs

## Weekly Outcome

By the end of this week you can implement a Server Action with real progressive enhancement (works before JavaScript hydrates), and defend a deployment choice (Vercel-native vs. self-hosting) with real operational trade-offs, not marketing claims.

## Why This Week Matters

Server Actions are the modern alternative to Week 2's Route Handlers for a pure Next.js mutation path — the two topics are deliberately sequenced to invite direct comparison. Deployment models close out the "how does this actually ship" thread this pack has built since Junior-to-Mid's build-tooling week.

## Prerequisites

Week 2 (Route Handlers) — for a direct comparison against this week's Server Actions.

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [Server Actions and Mutations: No API Layer, Real Progressive Enhancement](../../../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md) (F-212) |
| Thu–Sat | [Deployment Models: Vercel-Native vs. Self-Hosting, Verified](../../../syllabus/21-frontend-web/nextjs-deployment-models.md) (F-213) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Server Actions and Mutations (F-212) | [`syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md`](../../../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md) |
| 2 | Deployment Models (F-213) | [`syllabus/21-frontend-web/nextjs-deployment-models.md`](../../../syllabus/21-frontend-web/nextjs-deployment-models.md) |

## Hands-On Exercises

Both chapters cite their own real, verified evidence — follow the link from each chapter's own "Real Verified Demos" section directly (not re-verified independently here; F-213's own title states its deployment claims are "Verified," implying real, tested evidence rather than marketing description).

## Interview Answer Drills

Answer, out loud: "how does a Server Action work if the client's JavaScript hasn't loaded yet?" and "name one real operational cost of Vercel-native deployment that self-hosting avoids, and vice versa."

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described form mutation, implement it as both a Route Handler (Week 2's approach) and a Server Action, and state which you'd actually choose for that scenario and why, in under 5 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Can explain progressive enhancement's real mechanism for a Server Action, not just that it "works without JS."
- [ ] Can name at least 2 real trade-offs between Vercel-native and self-hosted deployment.

## Completion Criteria

- [ ] Implemented a working Server Action with a real form.
- [ ] Chose and defended a deployment model for 2 different stated project scales.

## Retrospective

Note whether you'd default to Server Actions or Route Handlers for a new project going forward, and why — both are valid, and the interview-worthy answer is the reasoning, not the choice itself.

## Next Week

[Week 10 — Where Frontend and Backend Code Live Together, and the Expert-Tier Capstone](../week-10/README.md).
