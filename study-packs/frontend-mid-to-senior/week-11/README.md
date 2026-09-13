---
title: "Frontend Mid → Senior, Week 11 — Security, Real-Time UI, and Multi-Team Architecture"
document_type: study-pack
week: 11
track: frontend-mid-to-senior
status: draft
estimated_hours: 8
last_reviewed: 2026-09-12
---

# Week 11 — Security, Real-Time UI, and Multi-Team Architecture

## Weekly Outcome

By the end of this week you can explain a real reflected-XSS execution and the escaping/CSP defenses that close it, implement resilient real-time UI with `WebSocket`/`EventSource` (including reconnect behavior), and reason about when a frontend outgrows a single deployable bundle enough to justify Module Federation.

## Why This Week Matters

These three chapters (F-401, F-402, F-403) are the newest additions to `21-frontend-web` and had never been scheduled in this path until a direct front-matter audit found the gap. All three are Advanced-tier, cross-cutting concerns (security, real-time data, multi-team scale) that apply regardless of which rendering strategy or state-management tool earlier weeks chose.

## Prerequisites

Week 1 (Server/Client boundary) — F-401's escaping and CSP concerns apply to both; general comfort with this pack's Weeks 1–10 is otherwise sufficient.

## Schedule

| Day | Focus |
|---|---|
| Mon–Tue | [Frontend Security: XSS, CSRF, and CSP](../../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md) (F-401) |
| Wed–Thu | [WebSocket and Server-Sent Events for Real-Time UI](../../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md) (F-402) |
| Fri–Sat | [Micro-Frontends and Module Federation](../../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md) (F-403) |
| Sun | Review checklist below |

## Required Reading

| # | Topic | Canonical Chapter |
|---|---|---|
| 1 | Frontend Security: XSS, CSRF, and CSP (F-401) | [`syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md`](../../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md) |
| 2 | WebSocket and Server-Sent Events for Real-Time UI (F-402) | [`syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md`](../../../syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md) |
| 3 | Micro-Frontends and Module Federation (F-403) | [`syllabus/21-frontend-web/micro-frontends-and-module-federation.md`](../../../syllabus/21-frontend-web/micro-frontends-and-module-federation.md) |

## Hands-On Exercises

[`practice/frontend/frontend-security-xss-and-csp/`](../../../practice/frontend/frontend-security-xss-and-csp/README.md) (a real reflected-XSS payload executing in real headless Chromium, then a real CSP header blocking it). [`practice/frontend/websocket-and-sse-realtime/`](../../../practice/frontend/websocket-and-sse-realtime/README.md) (a real Node `ws` server plus real headless Chromium, measuring reconnection behavior). [`practice/frontend/microfrontends-module-federation/`](../../../practice/frontend/microfrontends-module-federation/README.md) (two genuinely separate Webpack 5 builds composed at runtime, with a real rebuild-one-without-the-other test).

## Production Cookbook Cross-Reference

- [`reflected-xss-from-a-bypassed-default-escaping-path.md`](../../../production-cookbook/reflected-xss-from-a-bypassed-default-escaping-path.md)
- [`websocket-dashboard-silently-freezing-after-a-dropped-connection.md`](../../../production-cookbook/websocket-dashboard-silently-freezing-after-a-dropped-connection.md)
- [`two-teams-colliding-on-release-schedules-for-one-shared-frontend-bundle.md`](../../../production-cookbook/two-teams-colliding-on-release-schedules-for-one-shared-frontend-bundle.md)

## Interview Answer Drills

Answer, out loud: "walk through exactly how a reflected-XSS payload executes, and name two independent layers that would each stop it" and "what happens to a raw `WebSocket` when its connection drops, and what's the minimum fix?" before checking each chapter's own Interview Answer Framework.

## Coding Problems

None dedicated this week.

## System Design Exercise

Design a real-time collaborative feature (e.g., a shared cursor/presence indicator) choosing `WebSocket` or SSE explicitly, defending the choice, and specify your reconnect/backoff strategy using F-402's own real, measured evidence rather than assuming the browser handles it.

## Behavioral Exercise

None this week.

## Mock Interview

Self-check: given a described "one team's release keeps getting blocked by another team's unrelated bug" scenario, explain whether Module Federation would actually help and what the real, new operational costs of adopting it would be, out loud, in under 10 minutes.

## Review Checklist

- [ ] Completed all three chapters' own Mastery Checklists.
- [ ] Reproduced all three real demos (XSS/CSP, WebSocket/SSE reconnection, Module Federation rebuild test).
- [ ] Read all three cross-referenced cookbook entries and can restate each diagnosis without looking.

## Completion Criteria

- [ ] Can explain a real reflected-XSS execution and both the escaping fix and the CSP second layer.
- [ ] Implemented or reproduced a reconnect wrapper for a dropped `WebSocket` connection.
- [ ] Can state the specific structural condition (a shared build artifact) that makes Module Federation the right fix, versus when it's premature.

## Retrospective

Note whether any app you've worked on ever had a "raw HTML" escape hatch used for one legitimate reason and then reused elsewhere by habit — this is the exact pattern the XSS cookbook entry traces back to, not an exotic attack.

## Next Week

This is the last week of the Frontend Mid → Senior pack. There is no further pack in the frontend ladder — return to [`syllabus/00-overview/learning-paths/`](../../../syllabus/00-overview/learning-paths/) for the full set of paths, or continue the Java backend track with [Senior → Staff](../../../syllabus/00-overview/learning-paths/senior-to-staff.md) for full-stack dual-track depth.
