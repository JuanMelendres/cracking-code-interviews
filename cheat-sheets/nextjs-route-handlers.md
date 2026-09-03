---
title: "Cheat Sheet: Next.js Route Handlers"
slug: nextjs-route-handlers
document_type: cheat-sheet
domain: frontend
topic_id: F-207
tier: Intermediate
canonical: ../handbook/frontend/nextjs-route-handlers.md
last_updated: 2026-09-03
---

# Next.js Route Handlers

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-route-handlers.md`](../syllabus/21-frontend-web/nextjs-route-handlers.md)

Flagged in the register as relevant to a Java-backend-plus-Next.js full-stack setup — "where does logic live."

## Core Mental Model

A Route Handler is a real, standalone HTTP endpoint — not a data-fetching helper for this app's own pages. It speaks the plain Web `Request`/`Response` API, lives at `route.js`, and is reachable by anyone who can reach the app's URL. Two properties contradict a naive mental model borrowed from page-level caching: `GET` is NOT cached unless explicitly opted in, and when opted in, the cached response is frozen at BUILD time.

## Essential Definitions

- **Route Handler** — a `route.js`/`route.ts` file exporting async functions named after HTTP methods, at any segment without a `page.js`.
- **Backend-for-Frontend (BFF)** — a Route Handler that calls an upstream service server-side and returns a reshaped, minimal response, hiding the upstream's URL/credentials/shape from the client.
- **`export const dynamic = 'force-static'`** — the one explicit opt-in that makes a `GET` handler run once at build time and freeze its response.
- **`params` in a dynamic segment handler** — a `Promise`, must be awaited, consistent with the app's other dynamic routes.

## Decision Table

| Question | Choice |
|---|---|
| Caller is this app's own Server Component rendering a page? | Fetch the data source directly — do NOT route through this app's own Route Handler |
| Caller is outside this app's render path (browser JS, webhook, mobile client, other service)? | Route Handler is the right tool |
| Response needs to hide/reshape an upstream service's URL/credentials/shape? | Route Handler acting as a BFF proxy |
| Does this `GET` response change per request, or is it stable enough to freeze at build? | Default (uncached) for per-request data; `force-static` only for genuinely build-stable data |

## Key Numbers (real, curl'd against a clean production server)

- Real build manifest: 4 of 5 sibling `route.js` handlers showed `ƒ` (Dynamic/uncached); only the one with `force-static` showed `○` (Static) — same marker as a statically rendered page.
- `cached-count` handler returned `{"count":2}` before AND after a real `POST` mutation added a third widget elsewhere in the same app — the build-time snapshot never changed.
- `PUT /api/widgets` (unexported method) → real automatic `405`. `OPTIONS /api/widgets` → real automatic `204` with `allow: GET, HEAD, OPTIONS, POST`.

## Common Pitfalls

- Assuming Route Handlers are cached like a page's `fetch()` sometimes is by default — the real default is the opposite: uncached.
- Having a Server Component fetch its own app's Route Handler as a data source instead of the underlying data source directly — a real, documented, build-breaking chicken-and-egg failure (no server listening yet at build time).
- Hand-writing a `405` response or an `OPTIONS` handler "to be safe" — both are real, automatic framework behaviors needing no code.

## Interview Answer Skeleton

**30-sec:** Route Handlers turn a Next.js app into a real, public HTTP API. They're NOT cached by default (verified: real `ƒ` marker for every handler except an explicit `force-static` opt-in, which then freezes at build time). They exist for callers outside this app's own render path, not as a detour for its own Server Components to fetch through.

**2-min:** Cover the file convention and automatic `405`/`OPTIONS`, the caching model contrasted against page-level `fetch()` (F-204), the CRUD demo (200/201/400/404/204), and the Backend-for-Frontend pattern as the seam where a Java-backend-plus-Next.js system decides where logic lives.

**Whiteboard:** Browser box → Next.js server box ("GET /api/widgets"). A decision diamond "method exported?" branching to "run handler" (real 200/201/400/404/204) vs. "405, automatic." A second box "force-static?" branching to per-request (default) vs. frozen at build. A third box off to the side: an external service, reached only from the server — the BFF seam.

**Staff-level framing:** Deciding whether a piece of logic lives in the Java service or in a Next.js Route Handler is a real architectural call with organizational weight — a thin BFF proxy keeps frontend-shaped concerns close to the frontend team without duplicating business logic, but letting substantive logic accumulate in Route Handlers risks splitting a system's rules across two codebases with two deploy cycles.

## Production Warning Signs

- A Server Component fetching its own app's `/api/...` Route Handler causes sporadic `next build` failures — the same class of chicken-and-egg problem F-204 found for same-server `force-cache` fetches.
- `force-static` used on a `GET` handler whose data changes at runtime, producing silent, indefinite staleness with no automatic refresh.
- Fix for both: call the underlying data source directly from Server Components; pair `force-static` with `revalidateTag`/`revalidatePath` if the data can actually change.

## Related

- `syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md`
- `syllabus/21-frontend-web/nextjs-proxy-and-edge-runtime.md`
- `syllabus/07-api-design/api-design.md`
