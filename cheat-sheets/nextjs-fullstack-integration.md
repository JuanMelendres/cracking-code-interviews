---
title: "Cheat Sheet: Next.js Full-Stack Integration"
slug: nextjs-fullstack-integration
document_type: cheat-sheet
domain: frontend
topic_id: F-214
tier: Expert
canonical: ../handbook/frontend/nextjs-fullstack-integration.md
last_updated: 2026-09-03
---

# Next.js Full-Stack Integration

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-fullstack-integration.md`](../syllabus/21-frontend-web/nextjs-fullstack-integration.md)

Closes D-F2. The register names this as the topic that "most directly serves a full-stack Java+React developer specifically" — verified against two real, separately-running processes: Next.js (port 5198) and a real Spring Boot backend (port 8080).

## Core Mental Model

Two real processes, two real origins, and exactly one of them should ever be visible to the browser. The browser only ever talks to Next.js, which holds the user's session (an `httpOnly` cookie it cannot even read). Next.js's own server — never the browser — is the only thing that talks to the separate Spring backend, carrying a completely different credential the browser never sees.

## Essential Definitions

- **CORS** — a browser-enforced mechanism restricting which origins a page's own JavaScript may READ responses from; it does not restrict who can send the request.
- **BFF (backend-for-frontend)** — a server-side layer (Next.js Route Handlers) between the browser and one or more backends, so the browser has exactly one trusted party to authenticate to.
- **`Access-Control-Allow-Origin`** — a real response header, but not itself readable by `fetch()`'s `Headers` object by default, unless `Access-Control-Expose-Headers` names it.
- **Two independent gates** — CORS (browser-only) and application-level authorization (a shared secret, a session check) — neither substitutes for the other.

## Decision Table

| Question | Choice |
|---|---|
| Does the browser ever need to call the separate backend directly? | If no, skip CORS entirely for those endpoints — a real, additional layer of protection |
| A public, non-sensitive endpoint the browser genuinely needs to reach directly? | Real, explicit CORS naming the actual frontend origin — never a wildcard for anything credentialed |
| Where should session/auth logic live? | The BFF (Next.js Route Handlers), reusing the existing DAL — never duplicated into the separate backend |
| Debugging "works in curl, fails in browser"? | Check the browser console for a CORS error first — curl has no concept of CORS at all |

## Key Numbers (real, captured against two live processes)

- A Spring endpoint with no CORS config, called from the browser: real `TypeError: Failed to fetch`, with the console naming the missing `Access-Control-Allow-Origin` header. Fixed with an explicit allowlist and retested to a real `200`.
- The same fixed endpoint's `res.headers.get('access-control-allow-origin')` from the browser: `null` — present on the wire (confirmed via curl) but not JS-readable by default.
- A curl request to a shared-secret-protected endpoint with no `Origin` header: real `403` without the correct key, real `200` with it — CORS was never in this code path at all.
- Unauthenticated request to the BFF's own proxy route: real `401` — Spring's endpoint was never even called.

## Common Pitfalls

- Treating CORS as an authentication mechanism — curl bypasses it entirely, and a correctly-CORS-configured endpoint with no independent auth check is exposed to any non-browser caller.
- Assuming a successful CORS-permitted `fetch()` means every response header is now readable in JavaScript.
- Duplicating session-verification logic into a separate backend service instead of centralizing it in a BFF.

## Interview Answer Skeleton

**30-sec:** CORS is browser-only enforcement — curl bypasses it entirely while a real browser fetch genuinely failed until the backend explicitly allowlisted the frontend's origin. It is not authentication: a CORS-configured endpoint with no independent credential check is exposed to any non-browser caller. The right architecture is a BFF: the browser only ever talks to Next.js, which separately authenticates server-to-server to the backend.

**2-min:** Cover the real captured CORS failure and fix, the subtle header-readability finding (`null` from `fetch()` despite being present on the wire via curl), and the BFF pattern holding both credentials simultaneously, tested with a real live authenticated session.

**Whiteboard:** Browser, Next.js, Spring as three boxes. Real arrow Browser→Spring labeled "blocked (real CORS error) OR needs a secret the browser can't have." Browser→Next.js labeled "httpOnly session, invisible to JS." Next.js→Spring labeled "shared secret, server-only, never sent to browser." Annotate: CORS only applies to the deliberately-avoided direct arrow.

**Staff-level framing:** The BFF's real cost (an extra hop, extra infrastructure) is a deliberate trade for a simpler security model: exactly ONE origin the browser needs a credential for, and exactly ONE place that needs to understand every backend's own auth format — versus an N-services-times-M-credential-types matrix if every backend accepted the browser's session directly.

## Production Warning Signs

- A frontend team adds CORS headers to a backend and assumes the integration is now secure — a security review finds a curl request with no `Origin` header still reaches the endpoint fine.
- CORS was mistakenly treated as authentication; the endpoint had no independent credential check.
- Fix: a real shared-secret or session/token check independent of CORS — CORS is only ever relevant to endpoints truly meant for direct browser access.

## Related

- `syllabus/21-frontend-web/nextjs-authentication-patterns.md`
- `syllabus/21-frontend-web/nextjs-route-handlers.md`
- `syllabus/12-security/owasp-top-10-for-backend-services.md`
- `syllabus/12-security/authn-authz-rbac-vs-abac.md`
