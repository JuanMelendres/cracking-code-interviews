---
title: "Cheat Sheet: Next.js Fundamentals"
slug: nextjs-fundamentals
document_type: cheat-sheet
domain: frontend
topic_id: F-201
tier: Beginner
canonical: ../handbook/frontend/nextjs-fundamentals.md
last_updated: 2026-09-03
---

# Next.js Fundamentals

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-fundamentals.md`](../syllabus/21-frontend-web/nextjs-fundamentals.md)

## Core Mental Model

A meta-framework is React plus the decisions React deliberately doesn't make: how a URL maps to a component, how that component's output actually renders, and how the result ships. Next.js's file-based routing makes the FILE SYSTEM the source of truth for routes, which lets the framework reason about the whole route set at BUILD time (a real route manifest) in a way a runtime-configured router cannot.

## Essential Definitions

- **File-based routing** — a `page.js` file's location inside `app/` IS the route; no router import, no registration step.
- **Dynamic segment** — a folder named `[segment]` (square brackets) lets one file serve every URL value for that position; `params` is a `Promise` that must be `await`ed in the current App Router.
- **Layout** — `layout.js` wraps every route beneath it and persists across sibling navigation (measured: a mount counter stayed unchanged across three real `<Link>` navigations).
- **Meta-framework** — a framework built on top of a view library (React) that makes routing, rendering-strategy, and bundling decisions for you, trading flexibility for consistency.

## Decision Table

| Concern | Plain React + Vite (+ router library) | Next.js (App Router) |
|---|---|---|
| Routing setup | Manual route table, kept in sync by hand | Automatic — file location IS the route |
| Rendering strategy | Client-side only by default; SSR/SSG needs extra tooling | Built-in, per-route choice |
| Build-time route awareness | None | Full — `next build` produces a real manifest |
| Best fit | Small widget/library with no real "pages" | Content-driven or SEO-relevant apps |

## Common Pitfalls

- Describing file-based routing as "just a convenience" instead of naming the concrete consequence: a real, build-time-generated route manifest a runtime router cannot produce.
- Assuming any client-side navigation library avoids full reloads without verifying it (this chapter confirmed `<Link>` via the Navigation Timing API: one entry across three transitions).
- Treating "Next.js vs. plain React" as one global company-wide choice instead of a per-project decision.

## Interview Answer Skeleton

**30-sec:** File location is the entire routing config — proven with a real `next build` manifest, zero router code. Layouts persist across sibling navigation (measured, unchanged mount counter). Beyond routing, a meta-framework also decides rendering strategy and bundling, which is the real "why a meta-framework" answer.

**2-min:** Add the real evidence: `○ /`, `○ /about`, `ƒ /blog/[slug]` from an actual build; the mount-counter proof of layout persistence; the Navigation Timing check proving `<Link>` is genuinely client-side. Close with the trade-off: consistency and eliminated configuration drift, at the cost of opinionation.

**Whiteboard:** File tree on the left, arrows straight to a URL list on the right — no "router config" box in between. Beside it, a persisted layout box with a `{children}` slot; three different page contents swap inside it while a counter inside the layout never moves.

**Senior-level framing:** Explain the build-time vs. runtime distinction concretely (the real manifest, the configuration-drift argument) rather than "it's more convenient." This Beginner-tier chapter does not push further into Staff-level framing — see the Decision Framework's brief note that choosing a meta-framework is a per-project, not company-wide, decision.

## Production Warning Signs

- A manually maintained route table (in a CRA + React Router app) silently drifting from actual page files after refactors — stale `<Route>` entries pointing at deleted components, sometimes only caught by a runtime blank page.
- Reflexively reaching for Next.js on a small internal tool or component library with no routing/SEO concerns, absorbing its opinionation for no benefit.
- A parallel, manually maintained "route registry" built inside a Next.js app, reintroducing the exact drift risk file-based routing exists to eliminate.

## Related

- `syllabus/21-frontend-web/react-state-management.md`
- `syllabus/21-frontend-web/nextjs-app-router-fundamentals.md`
- `00-project/frontend-topic-register.md`
