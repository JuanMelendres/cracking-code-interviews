---
title: "Frontend Mid → Senior, Week 10 — Where Frontend and Backend Code Live Together, and the Expert-Tier Capstone"
document_type: study-pack
week: 10
track: frontend-mid-to-senior
status: draft
estimated_hours: 9
last_reviewed: 2026-09-08
---

# Week 10 — Where Frontend and Backend Code Live Together, and the Expert-Tier Capstone

## Weekly Outcome

By the end of this week you can state the real, measured cost of skipping workspace tooling in a multi-app repository, and correctly place CORS configuration, the BFF pattern, and auth/session logic in a full-stack Next.js + Java/Spring system — the closing, Expert-tier signal of this entire frontend domain.

## Why This Week Matters

This is the pack's final week. F-303 (Monorepo Layout) and F-214 (Full-Stack Integration) are sequenced together deliberately: F-303 explains where code lives together sanely, and F-214 is the single chapter that most directly serves a full-stack Java+React developer — the reader this whole repository is built for. Both chapters ground every claim in real evidence from THIS repository's own actual apps, not a hypothetical example.

## Prerequisites

Weeks 1–9, plus [Frontend Junior → Mid](../../../syllabus/00-overview/learning-paths/frontend-junior-to-mid.md) and, for the Java/Spring side of this week specifically, the backend's own [REST API Fundamentals](../../../syllabus/07-api-design/rest-api-fundamentals.md) (T-2205) and [Spring MVC Fundamentals](../../../syllabus/05-spring/spring-mvc-fundamentals.md) (T-2203).

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Monorepo and Full-Stack Repo Layout](../../../syllabus/21-frontend-web/nextjs-monorepo-layout.md) (F-303) |
| Wed–Sat | [Full-Stack Integration: Next.js with a Separate Java/Spring Backend](../../../syllabus/21-frontend-web/nextjs-fullstack-integration.md) (F-214) — read in full, reproduce the real CORS failure and its fix |
| Sun | System Design Exercise below, then the full-pack retrospective |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Monorepo and Full-Stack Repo Layout (F-303) | [`syllabus/21-frontend-web/nextjs-monorepo-layout.md`](../../../syllabus/21-frontend-web/nextjs-monorepo-layout.md) |
| 2 | Full-Stack Integration with a Java/Spring Backend (F-214) | [`syllabus/21-frontend-web/nextjs-fullstack-integration.md`](../../../syllabus/21-frontend-web/nextjs-fullstack-integration.md) |

## Hands-On Exercises

- [`practice/frontend/monorepo-layout-demo/`](../../../practice/frontend/monorepo-layout-demo/) — a real npm workspaces monorepo proving a shared package updates via a real symlink (`node_modules/shared-utils -> ../packages/shared-utils`), not a copy, plus a real, measured `du -sh` cost of NOT using workspace tooling, drawn from four of this repository's own actual `practice/frontend/` apps (435MB, 39MB, 60MB, and 55MB of duplicated `node_modules`, each with an identical `react@19.2.8`).
- [`practice/frontend/react-nextjs-fundamentals/`](../../../practice/frontend/react-nextjs-fundamentals/) (port 5198) and [`practice/java/full-stack-integration-backend/`](../../../practice/java/full-stack-integration-backend/) (port 8080) — two real, separately-running processes proving F-214's claims: a real captured browser CORS failure against `PublicController.java` before any CORS configuration existed, the real fix via `CorsConfig.java`, and a real, live authenticated browser session proving the BFF pattern's full credential chain through [`app/api/backend-proxy/route.js`](../../../practice/frontend/react-nextjs-fundamentals/app/api/backend-proxy/route.js), which holds both the Next.js session credential and a shared secret for `InternalController.java` (deliberately NOT CORS-allowlisted).

Reproduce the exercise from F-214's own Section: temporarily change `CorsConfig.java`'s `allowedOrigins` to a different port, rebuild, restart, and confirm the same CORS failure reproduces exactly — then revert.

## Interview Answer Drills

Answer, out loud: "you have a Next.js frontend and a separate Spring Boot API — walk me through exactly why a browser `fetch()` call fails with no CORS configuration, and where you'd fix it" and "why would you put a shared secret behind a Next.js Route Handler instead of calling the internal endpoint directly from the browser?"

## Coding Problems

None dedicated this week.

## System Design Exercise

**Full mock, this week specifically (unlike every other week in this pack):** design the auth/CORS/BFF boundary for a described full-stack app — a Next.js frontend, a separate Java/Spring backend with both a public and an internal API, and a requirement that the internal API's credentials never reach the browser. State: which origins get CORS-allowlisted, where session validation happens (DAL vs. Route Handler vs. Spring Security filter chain), and how the BFF pattern protects the internal credential — narrated out loud, timed at 30 minutes, as if presenting to a Staff-level panel.

## Behavioral Exercise

None this week — see Week 1's note on where behavioral preparation lives in this repository.

## Mock Interview

Use the System Design Exercise above as this week's mock — self-assess against F-214's own Decision Framework and Staff-Level Discussion sections (if present) for what a strong answer covers.

## Review Checklist

- [ ] Completed both chapters' own Mastery Checklists.
- [ ] Reproduced the real CORS failure-then-fix from F-214.
- [ ] Inspected the real symlink and measured the real `node_modules` duplication cost from F-303.
- [ ] Completed the System Design Exercise above.

## Completion Criteria

- [ ] Reproduced F-214's CORS failure and fix on your own machine, with both processes genuinely running separately.
- [ ] Can state, from memory, the real measured cost of skipping workspace tooling (not an estimate — the actual `du -sh` numbers this chapter captured).
- [ ] Completed the full-stack System Design Exercise, correctly placing CORS/BFF/auth logic.

## Retrospective

This is the pack's final retrospective: review every prior week's retrospective note (list-key intuition through fiber internals through testing philosophy) and confirm each is resolved. This closes the Frontend Mid → Senior pack — for a full-stack reader, continue in parallel with the Java backend's own [Senior → Staff](../../../syllabus/00-overview/learning-paths/senior-to-staff.md) pack.

## Next Week

This is the last week of the Frontend Mid → Senior pack. There is no further pack in the frontend ladder — return to [`syllabus/00-overview/learning-paths/`](../../../syllabus/00-overview/learning-paths/) for the full set of paths, or continue the Java backend track with [Senior → Staff](../../../syllabus/00-overview/learning-paths/senior-to-staff.md) for full-stack dual-track depth.
