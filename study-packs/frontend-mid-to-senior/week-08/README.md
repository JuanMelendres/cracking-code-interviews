---
title: "Frontend Mid → Senior, Week 8 — Edge-Level Request Logic and Real Authentication"
document_type: study-pack
week: 8
track: frontend-mid-to-senior
status: draft
estimated_hours: 7
last_reviewed: 2026-09-08
---

# Week 8 — Edge-Level Request Logic and Real Authentication

## Weekly Outcome

By the end of this week you can explain what runs in the Edge runtime versus Node.js and why that boundary matters, and implement an authentication pattern (DAL, JWT sessions, or `unauthorized()`) correctly for both Server Components and Route Handlers.

## Why This Week Matters

Where request-level logic runs (Topic 15) and how a request is authenticated (Topic 16) are both genuinely Senior-level Next.js concerns — this week directly cross-references the backend's own security domain for a full-stack reader.

## Prerequisites

Weeks 1–2 (Server/Client boundary, Route Handlers).

## Schedule

| Day | Focus |
|---|---|
| Mon–Wed | [Proxy (formerly Middleware) and the Edge Runtime](../../../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md) (F-208) |
| Thu–Sat | [Authentication Patterns: DAL, JWT Sessions, and unauthorized()](../../../syllabus/21-frontend-web/nextjs-authentication-patterns.md) (F-211) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Proxy and the Edge Runtime (F-208) | [`syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md`](../../../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md) |
| 2 | Authentication Patterns (F-211) | [`syllabus/21-frontend-web/nextjs-authentication-patterns.md`](../../../syllabus/21-frontend-web/nextjs-authentication-patterns.md) |

## Hands-On Exercises

Both chapters cite their own real demos — likely within the shared [`practice/frontend/react-nextjs-fundamentals/`](../../../practice/frontend/react-nextjs-fundamentals/) app (which includes a real `proxy.js` file at its root, per that directory's structure). Not re-verified independently here — follow each chapter's own "Real Verified Demos" section.

## Production Cookbook Cross-Reference

For a cross-domain view of authentication failures, see the backend's own [`syllabus/12-security/authn-authz-rbac-vs-abac.md`](../../../syllabus/12-security/authn-authz-rbac-vs-abac.md) and any matching `production-cookbook/` entries on the backend side (e.g. credential/session-handling incidents) — no frontend-specific cookbook entry exists yet for this week's topics.

## Interview Answer Drills

Answer, out loud: "what can't you do in the Edge runtime that you can in Node.js, and why?" and "where should a session check live — in the Data Access Layer, a Server Component, or a Route Handler — and why does it matter?"

## Coding Problems

None dedicated this week.

## System Design Exercise

None this week.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: design the auth-check placement for a described protected dashboard route (which layer checks the session, what happens on failure), narrating your reasoning, in under 5 minutes.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Can name at least 2 real Edge-runtime API restrictions.
- [ ] Can explain the DAL pattern's benefit over checking auth ad hoc in every route.

## Completion Criteria

- [ ] Implemented a working auth check protecting at least one Server Component and one Route Handler.
- [ ] Can explain `unauthorized()`'s role in the App Router's error-boundary model, unprompted.

## Retrospective

Note whether you'd previously conflated "middleware" and "the Edge runtime" as the same thing — Next.js 16 explicitly renamed and reframed this, and the distinction is a real, current interview trap.

## Next Week

[Week 9 — Mutations Without an API Layer, and Real Deployment Trade-offs](../week-09/README.md).
