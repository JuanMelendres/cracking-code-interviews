---
title: "Flashcards: Proxy (formerly Middleware) & the Edge Runtime in Next.js 16"
slug: nextjs-proxy-and-edge-runtime
document_type: flashcard-deck
domain: frontend
topic_id: F-208
tier: Advanced
canonical: ../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md
last_updated: 2026-09-07
---

# Flashcards: Proxy (formerly Middleware) & the Edge Runtime in Next.js 16

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md`](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md)

## Card: Is "Middleware" still the correct term in Next.js 16?

**Prompt:**
Is "Middleware" still the current file convention/terminology in Next.js 16?

**Answer:**
No — deprecated as of v16. The current convention is a project-root `proxy.js`/`proxy.ts` file exporting a `proxy` function. Verified directly: this version's own `next build` summary output prints `ƒ Proxy (Middleware)`, leading with the new name and keeping the old one only as a parenthetical.

**Why it matters:**
A candidate stating "Middleware" as current, unqualified terminology in a v16 context is describing a deprecated convention as if it were current.

**Common trap:**
Assuming a rename this significant would be purely cosmetic, missing that a leftover `middleware.ts` file silently STOPS RUNNING after an upgrade (no build error, just missing behavior).

**Related:**
[Proxy (formerly Middleware) & the Edge Runtime in Next.js 16](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md) [Route Handlers: Building a Backend-for-Frontend Layer in Next.js](../syllabus/21-frontend-web/nextjs-route-handlers.md)

## Card: Can Proxy use the Edge Runtime in Next.js 16?

**Prompt:**
Can a `proxy.js` file in Next.js 16 opt into the Edge Runtime?

**Answer:**
No. A real, captured `next build` attempt with `export const runtime = "edge"` inside `proxy.js` produced a hard build FAILURE: "Route segment config is not allowed in Proxy file... Proxy always runs on Node.js runtime." The SAME line on an ordinary Route Handler instead produced only a real deprecation WARNING, and that build succeeded.

**Why it matters:**
This is the exact, precise, two-sided correction of a common pre-v16 assumption ("Middleware runs on the Edge") — not just "the Edge runtime is deprecated" as a single blanket fact.

**Common trap:**
Treating the Edge Runtime's deprecated status as uniform everywhere in the app, missing that Proxy specifically has a HARDER, build-breaking constraint than ordinary routes.

**Related:**
[Proxy (formerly Middleware) & the Edge Runtime in Next.js 16](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md)
