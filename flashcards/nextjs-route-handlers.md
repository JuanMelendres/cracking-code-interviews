---
title: "Flashcards: Route Handlers: Building a Backend-for-Frontend Layer in Next.js"
slug: nextjs-route-handlers
document_type: flashcard-deck
domain: frontend
topic_id: F-207
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-route-handlers.md
last_updated: 2026-09-07
---

# Flashcards: Route Handlers: Building a Backend-for-Frontend Layer in Next.js

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-route-handlers.md`](../syllabus/21-frontend-web/nextjs-route-handlers.md)

## Card: Are Route Handlers cached by default?

**Prompt:**
Is a Route Handler's `GET` response cached by default in the App Router?

**Answer:**
No. Verified with a real build manifest: five sibling `route.js` files all showed `ƒ` (Dynamic/uncached) except one with an explicit `export const dynamic = 'force-static'`, which showed `○` (Static) — and that one froze at build time, confirmed by a real runtime mutation it never picked up.

**Why it matters:**
This is the OPPOSITE default from a page's own `fetch()`, which F-204 showed can end up statically cached by default when nothing else forces dynamic rendering — conflating the two is an easy, real mistake.

**Common trap:**
Assuming Route Handler caching works the same way as page-level `fetch()` caching, without checking the actual build manifest marker.

**Related:**
[Route Handlers: Building a Backend-for-Frontend Layer in Next.js](../syllabus/21-frontend-web/nextjs-route-handlers.md) [Data Fetching in the App Router: fetch Caching Semantics, revalidate, and cache: 'no-store'](../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md)

## Card: Why fetching your own app's Route Handler from a Server Component is risky

**Prompt:**
Why is it a real problem for a Server Component to fetch data by calling that SAME app's own Route Handler, instead of calling the underlying data source directly?

**Answer:**
For a Server Component prerendered at build time, the fetch fails — no server is listening yet during `next build`, the same class of chicken-and-egg failure F-204 captured directly as a real `ECONNREFUSED`. Even when not prerendered, it adds a real, unnecessary extra HTTP round trip.

**Why it matters:**
This is a documented, named caveat in this version's own Backend-for-Frontend guide, not a hypothetical edge case.

**Common trap:**
Treating "route everything through one API layer" as always correct, without exempting this app's own Server Components from that rule.

**Related:**
[Route Handlers: Building a Backend-for-Frontend Layer in Next.js](../syllabus/21-frontend-web/nextjs-route-handlers.md)
