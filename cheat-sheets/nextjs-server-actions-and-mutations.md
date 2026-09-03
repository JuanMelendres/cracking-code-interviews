---
title: "Cheat Sheet: Next.js Server Actions and Mutations"
slug: nextjs-server-actions-and-mutations
document_type: cheat-sheet
domain: frontend
topic_id: F-212
tier: Advanced
canonical: ../handbook/frontend/nextjs-server-actions-and-mutations.md
last_updated: 2026-09-03
---

# Next.js Server Actions and Mutations

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md`](../syllabus/21-frontend-web/nextjs-server-actions-and-mutations.md)

## Core Mental Model

A Server Action is a public POST endpoint wearing a function's clothes. `'use server'` doesn't create a private RPC channel — it compiles the function into server-side code plus a client-side encrypted action reference that POSTs back to whatever route rendered the form. Anyone who can construct that POST can invoke the action, with or without ever loading the page. The framework protects the TRANSPORT (a real CSRF Origin check) but not the DECISION — authorization is entirely the action's own responsibility.

## Essential Definitions

- **Server Action** — an async function marked `'use server'`, invocable from `<form action={...}>`, `formAction`, or via `startTransition`.
- **Single-roundtrip response** — when an action calls `revalidatePath`/`updateTag`/`redirect`/mutates cookies, the SAME response also carries a freshly rendered RSC payload for the current route.
- **Progressive enhancement** — a raw bound action reference (`action.bind(null, id)`) renders real, working hidden form fields and functions as a plain HTML form without JavaScript.
- **CSRF (Origin check)** — the framework compares `Origin` against `Host`/`X-Forwarded-Host` before the action runs at all; a mismatch is a real, separate rejection from authorization.

## Decision Table

| Question | Choice |
|---|---|
| Only this app's own UI triggers the mutation, no optimistic UI needed? | A Server Action bound as a raw reference — real progressive enhancement for free |
| Mutation needs instant, optimistic UI feedback? | `useOptimistic` + closure-wrapped action — but budget for the real cost: that form's no-JS fallback is gone |
| A non-browser client/another service/a separate backend needs to call this mutation too? | A Route Handler (F-207), not a Server Action — its endpoint shape is a stable contract; a Server Action's is not |
| Any Server Action, regardless of the above | Write and test its own authorization check as if the page-level gate does not exist |

## Key Numbers (real, disk-verified and curl'd tests)

- A raw POST to a page-gated action, no session cookie, with the action's own check temporarily removed: a real new row committed to `data/notes.json` — while the HTTP response was a plain `307` that looks exactly like a rejection.
- A real curl POST with `Origin: https://evil.example.com`: real `500` — `"x-forwarded-host" ... does not match "origin" ... Aborting the action."`
- A raw multipart POST reproducing a bound-reference delete form's real hidden fields (no JS, no fetch): genuinely deleted the target note.
- The optimistic add-form's rendered `action` attribute: `javascript:throw new Error(...)` — a dead fallback without JS.

## Common Pitfalls

- Assuming a Server Action rendered only on an authenticated page is itself protected — the write can commit before any page-level redirect fires.
- Assuming the framework's CSRF protection is the same thing as authorization — it stops a different attack and runs before the action's own code.
- Wrapping a Server Action in a client closure for `useOptimistic` without noticing the no-JS fallback silently becomes a dead `javascript:` URL.

## Interview Answer Skeleton

**30-sec:** A Server Action collapses fetch-plus-API-route-plus-database into one function with a real single-roundtrip response. But it's a public POST endpoint the instant it exists — verified with a disk-confirmed bypass where an anonymous POST wrote real data even though the response looked like a rejected redirect.

**2-min:** Cover the no-API-layer CRUD shape with the real network-trace proof, the central disk-verified finding (write commits before the visible rejection), and the CSRF-vs-authorization distinction with the exact captured error message.

**Whiteboard:** Anonymous POST direct to a gated page URL → CSRF check (Origin vs Host): mismatch → real 500. Match → action's own auth check: present → real error, no write; removed → real write happens, THEN a separate later step (page re-render's DAL redirect) → real 307. Circle the gap: "attacker sees only this box, but the row above it already exists."

**Staff-level framing:** Treat "does the response look right" as insufficient evidence for a mutation's security — ask "what does the underlying store show" instead. Push for automated tests that assert against the DATA, not just the HTTP status, for every mutation endpoint.

## Production Warning Signs

- A support ticket reports a user "couldn't submit" (saw a sign-in redirect) but the record changed anyway — a raw request with an invalid session reproduces the same redirect while the underlying store shows the write succeeded.
- Root cause: the action's own authorization check is missing or fails open (a caught exception that doesn't `return` early).
- Fix: audit every Server Action for an explicit, early-return auth check before any write.

## Related

- `syllabus/21-frontend-web/nextjs-authentication-patterns.md`
- `syllabus/21-frontend-web/nextjs-route-handlers.md`
- `syllabus/12-security/owasp-top-10-for-backend-services.md`
