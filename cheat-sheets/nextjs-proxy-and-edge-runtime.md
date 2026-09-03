---
title: "Cheat Sheet: Next.js Proxy and Edge Runtime"
slug: nextjs-proxy-and-edge-runtime
document_type: cheat-sheet
domain: frontend
topic_id: F-208
tier: Advanced
canonical: ../handbook/frontend/nextjs-proxy-and-edge-runtime.md
last_updated: 2026-09-03
---

# Next.js Proxy and Edge Runtime

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md`](../syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md)

The register calls this "Middleware & the Edge runtime" — both halves of that name are stale in Next.js 16: Middleware was renamed to Proxy, and the Edge Runtime is deprecated, with Proxy specifically forbidden from opting into it.

## Core Mental Model

Proxy is a single, project-wide gatekeeper that runs BEFORE a matched request reaches its destination (page, Route Handler, anything) — not a general-purpose backend layer, and not a composable chain (unlike Express-style middleware, the exact confusion the rename is meant to correct). It should be a fast, coarse check (a redirect, a header rewrite, an optimistic auth gate), never a place for real business logic or slow data fetching.

## Essential Definitions

- **Proxy** — `proxy.js`/`proxy.ts` at the project root, exporting a `proxy` function; the direct, renamed replacement for "Middleware" (functionality unchanged, name and file changed).
- **Edge Runtime** — a restricted JS execution environment Proxy used to default to; now deprecated everywhere and hardcoded-forbidden inside Proxy specifically.
- **`matcher`** — a build-time-statically-analyzed path filter controlling which requests invoke the `proxy` function at all.
- **Execution order** — `next.config.js` headers/redirects, then Proxy, then the actual filesystem route (even a fully static, cached page).

## Decision Table

| Question | Choice |
|---|---|
| Needs to run before every (or a broad, path-matched set of) request? | Proxy |
| Fast, coarse decision (redirect, rewrite, cookie presence check)? | Proxy is the right tool |
| Needs to be the AUTHORITATIVE authorization check? | NOT Proxy alone — verify inside the destination Route Handler/Server Action |
| Considering `runtime = 'edge'` anywhere in a v16 app? | Don't — deprecated everywhere, hard build failure specifically in Proxy |

## Key Numbers (real, curl'd against a clean production server)

- `GET /` (fully static, cached page) still returned `x-proxy-hit: true` — Proxy ran before even a static route was served.
- `runtime = "edge"` in `proxy.js` → real, hard `next build` FAILURE: "Proxy always runs on Node.js runtime." The SAME line in an ordinary Route Handler → only two real WARNINGS, and the build succeeded.
- `curl -i .../favicon.ico` → no `x-proxy-hit` header anywhere — the negative-lookahead `matcher` genuinely excluded it.

## Common Pitfalls

- Writing (or leaving, post-upgrade) a `middleware.ts` file in a v16 project — it silently STOPS RUNNING, no build error, just a missing behavior.
- Assuming Proxy still defaults to (or can use) the Edge runtime — it's hardcoded to Node.js; setting `runtime` at all is now build-breaking.
- Treating a Proxy-level cookie check as sufficient authorization on its own rather than a fast, optimistic first layer.

## Interview Answer Skeleton

**30-sec:** What used to be Middleware is now Proxy — a single `proxy.js` file exporting a `proxy` function, running before a matched request reaches its route. The Edge Runtime, which Middleware used to default to, is now deprecated everywhere and flatly forbidden inside Proxy — a real, named build error, not a warning.

**2-min:** Cover the rename and its real motivation (avoiding confusion with composable middleware chains), the three real measured behaviors (header injection even on a static page, a redirect, a cookie gate), and the central, precisely two-sided Edge Runtime finding: hard error in Proxy, soft warning elsewhere. Close with the `matcher`'s real, verified exclusion.

**Whiteboard:** Request arrives → "next.config.js headers/redirects" → matcher gate (one path skips around it, annotated with the real favicon exclusion) → Proxy box ("Node.js ONLY, runtime config REJECTED") → three outgoing arrows (redirect / header+continue / direct response) → matched route (where Edge is still technically allowed, but deprecated with a warning).

**Staff-level framing:** The Middleware-to-Proxy rename and Edge Runtime deprecation together signal the framework's direction: away from broad, Edge-distributed request interception, toward a single, clearly-scoped, Node.js-only gatekeeper paired with a richer Route Handler layer for anything substantive — read this as guidance about where logic should live going forward, not a cosmetic naming change.

## Production Warning Signs

- A team upgrades to v16 and an old `middleware.ts`-based redirect silently stops firing — check the real `next build` summary for the `ƒ Proxy (Middleware)` line; its absence means the file isn't recognized under the current name.
- The framework ships a codemod for exactly this pitfall: `npx @next/codemod@canary middleware-to-proxy .`
- Relying on Proxy's `matcher` as the entire security boundary for a section of an app, without a matching authorization check inside the routes it's meant to protect.

## Related

- `syllabus/21-frontend-web/nextjs-route-handlers.md`
- `syllabus/12-security/authn-authz-rbac-vs-abac.md`
- `syllabus/21-frontend-web/nextjs-authentication-patterns.md`
