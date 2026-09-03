---
title: "Cheat Sheet: Next.js Authentication Patterns"
slug: nextjs-authentication-patterns
document_type: cheat-sheet
domain: frontend
topic_id: F-211
tier: Advanced
canonical: ../handbook/frontend/nextjs-authentication-patterns.md
last_updated: 2026-09-03
---

# Next.js Authentication Patterns

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-authentication-patterns.md`](../syllabus/21-frontend-web/nextjs-authentication-patterns.md)

Upgrades F-208's original naive Proxy cookie-presence check into a real, cryptographically verified one.

## Core Mental Model

Authentication is a layered system, and skipping a layer creates a real, reproducible gap. Proxy (F-208) does a FAST, OPTIMISTIC check — cookie present and cryptographically valid, no DB round trip. The DAL (`lib/dal.js`) does the REAL, authoritative check, used by every Server Component, Route Handler, and Server Action. A naive Proxy check (presence-only) lets a tampered token through; only the DAL's real signature verification catches it.

## Essential Definitions

- **Stateless JWT session** — signed with `jose`, stored in an `httpOnly`/`secure`/`sameSite: 'lax'` cookie; no server-side session store, but no instant revocation before expiry either.
- **DAL (Data Access Layer)** — `getSession()` (raw check) / `verifySession()` (redirects on failure); centralizes authorization so it isn't re-implemented per route.
- **`unauthorized()`** — experimental primitive (needs `authInterrupts: true`) that throws a special, framework-recognized error, parallel to `notFound()`.
- **Real HTTP status depends on timing** — whether `unauthorized()` resolves before or after streaming has started determines if the actual status code can still be set.

## Decision Table

| Question | Choice |
|---|---|
| Building a real production app? | Use a maintained auth library (Auth.js/NextAuth, Clerk) — this hand-rolled DAL is an educational baseline with no revocation before expiry |
| Need a fast, coarse, project-wide auth gate? | Proxy — but only with REAL signature verification, not presence-checking |
| Need the real, authoritative check? | The DAL, called from every Server Component/Route Handler/Server Action |
| Need a genuine `401` status (not just 401-looking content)? | Call `unauthorized()` somewhere that hasn't started streaming — a Route Handler, or before any Suspense boundary opens |

## Key Numbers (real, reproduced tests)

- A real, tampered JWT (one byte flipped) sent to `/dashboard` with the naive `.has('session')` Proxy check: Proxy let it through (`x-proxy-hit: true` present); the DAL caught it, redirecting to `/login` instead of Proxy's `/`.
- The same tampered token against an UPGRADED Proxy (real `decrypt()` call): rejected outright with a real `307` to `/`.
- `unauthorized()` three-way test: no flag → generic error, real `200`. Flag + inside Suspense → correct digest/noindex/UI, but STILL real `200`. Flag + in a Route Handler (not streamed) → genuine real `401`.
- `document.cookie` during an active, authenticated session: empty string — `httpOnly` genuinely works.

## Common Pitfalls

- Treating Proxy's optimistic check as sufficient authorization on its own.
- Assuming `unauthorized()` always returns a real `401` once `authInterrupts` is enabled — it stays `200` specifically when the check happens inside an already-streaming Suspense boundary.
- Forgetting `unauthorized()` does nothing special without `authInterrupts` explicitly enabled — a generic, unhelpful error results instead.

## Interview Answer Skeleton

**30-sec:** A real auth setup layers a fast, optimistic Proxy check with an authoritative DAL check. Verified with a reproduced bypass: downgrading Proxy to presence-only let a tampered JWT through, caught only by the DAL. `unauthorized()` returns a genuine `401` only when the check runs before streaming starts.

**2-min:** Cover the JWT session mechanism (sign, `httpOnly` cookie, verify) with the real empty-`document.cookie` proof, the layered-defense reproduced bypass (naive vs. upgraded Proxy), and the three-way `unauthorized()` status-code nuance.

**Whiteboard:** Request → Proxy (`decrypt(cookie)`) → invalid: real 307 to `/`. Valid → DAL (`verifySession()`) inside the page → invalid: real redirect to `/login` (callout: `x-proxy-hit: true` still present, proving Proxy let it through). Separate diagram: three `unauthorized()` boxes (no flag / flag+Suspense / flag+Route Handler) with their three real outcomes.

**Staff-level framing:** The stateless-JWT-vs-database-session trade-off is a first-class architectural decision — JWT sessions scale reads to zero DB cost but cannot be revoked instantly. Weigh this against the org's actual incident-response requirements before choosing a session strategy, rather than defaulting to whichever pattern a tutorial demonstrated first.

## Production Warning Signs

- A security review flags that `proxy.js` only checks cookie PRESENCE, not validity — any expired, forged, or tampered cookie named `session` passes.
- The DAL may save this specific case, but only because every protected route correctly calls `verifySession()` — a new route added later without that call is silently exposed.
- Fix: upgrade Proxy to real signature verification using the same `decrypt()` the DAL uses, so the optimistic layer provides genuine value.

## Related

- `syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md`
- `syllabus/21-frontend-web/nextjs-route-handlers.md`
- `syllabus/12-security/oauth2-oidc-and-jwt.md`
- `syllabus/12-security/authn-authz-rbac-vs-abac.md`
