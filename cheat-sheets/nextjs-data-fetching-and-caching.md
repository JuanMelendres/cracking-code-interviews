---
title: "Cheat Sheet: Next.js Data Fetching and Caching"
slug: nextjs-data-fetching-and-caching
document_type: cheat-sheet
domain: frontend
topic_id: F-204
tier: Intermediate
canonical: ../handbook/frontend/nextjs-data-fetching-and-caching.md
last_updated: 2026-09-03
---

# Next.js Data Fetching and Caching

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md`](../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md)

Covers this Next.js version's "Previous Model" (`fetch`'s own `cache`/`next.revalidate`/`next.tags` options) — the app's `next.config.mjs` does not set `cacheComponents: true`.

## Core Mental Model

Two separate caching layers exist, and conflating them is the single most common mistake: the FETCH layer (does this specific `fetch()` call reuse a previous response) and the ROUTE layer (does the entire page render once and get reused, or render fresh per request). `fetch()` defaulting to no HTTP-level reuse does not, by itself, force the surrounding route to render per-request — if nothing else forces dynamic rendering, Next's Full Route Cache can still statically cache the whole page regardless of the fetch's own option.

## Essential Definitions

- **`cache: 'no-store'`** — forces fresh data every request; makes the route dynamic.
- **`cache: 'force-cache'`** — the route becomes eligible for static generation; the fetch is attempted AT BUILD TIME.
- **`next: { revalidate: N }`** — time-based ISR: stale-while-revalidate; the first request after the window serves the old value while regenerating in the background.
- **`revalidateTag` / `revalidatePath`** — on-demand invalidation from a Server Action or Route Handler, immediate, no timer dependency.

## Decision Table

| Concern | `no-store` | `force-cache` / default | `revalidate: N` |
|---|---|---|---|
| Freshness | Always fresh (measured) | Stale until redeploy/revalidation (measured: same value repeatedly) | Fresh within N seconds, then stale-while-revalidate |
| Performance | Slowest — real network call every request | Fastest — static shell | Fast, occasional background regen |
| Build-time behavior | Not attempted at build | Attempted AT BUILD TIME (measured: real ECONNREFUSED against an unreachable target) | Same as force-cache initially |
| Best fit | Per-user or rapidly-changing data | Rarely-changing content | Occasional, unpredictable changes with acceptable staleness |

## Key Numbers (real, curl'd against a clean `next start` production server)

- Default fetch (no `cache` option): **same** response ID on two real requests — behaved identically to `force-cache`, not `no-store`.
- Explicit `cache: 'no-store'`: **different** response ID on every real request.
- `next: { revalidate: 5 }`: stable at t=0s/1s/2s; changed only after a real 7-second sleep crossed the 5-second window.
- `revalidateTag` clicked via a Server Action: cached value changed with **zero wait**.

## Common Pitfalls

- Assuming "fetch is uncached by default" means the whole page is always freshly rendered — a default fetch with no other dynamic dependency in the route can still be route-level statically cached.
- Using `force-cache` (or default) for a same-server dependency without realizing the fetch runs AT BUILD TIME, before the app's own server exists — a real, reproducible `ECONNREFUSED` failure mode.
- Reaching only for time-based revalidation when data changes in response to a known event — `revalidateTag` called from that event's own handler invalidates instantly instead of waiting out a window.

## Interview Answer Skeleton

**30-sec:** `fetch()` defaults to no HTTP-level reuse, but that alone doesn't make a route dynamic — if nothing else forces per-request rendering, the whole route can still be statically cached, verified with real curl evidence showing default behaving identically to `force-cache`. Explicit `no-store` reliably forces freshness. `revalidate: N` gives time-based staleness; `revalidateTag` invalidates on-demand with zero wait.

**2-min:** Cite the central finding (default fetch matched `force-cache`'s repeated value, only explicit `no-store` differed per request), the real build-time-execution discovery (ECONNREFUSED against a same-deployment target), and the timed proof (stable at 2s, changed after 7s) plus the zero-wait `revalidateTag` click.

**Whiteboard:** Two layers — "FETCH layer" and "ROUTE layer." Fetch layer box: "no cache option — HTTP reuse: no." Arrow down: "route has no other dynamic dependency → still statically cached." A separate `no-store` path skips the route layer entirely, going straight to fresh-every-request. A small timeline for `revalidate: 5`: flat, then a jump past 5s.

**Staff-level framing:** On-demand revalidation trades freshness for a real coupling cost — every code path that changes the data must remember to call `revalidateTag`, versus time-based revalidation's simpler "eventually correct within N seconds" guarantee needing no such discipline. Weigh that coupling cost against the freshness requirement explicitly.

## Production Warning Signs

- A "static" marketing/pricing page silently serving one visitor's stale value to everyone, hours after an update, because no `cache` option was set and the team assumed "fetch defaults to fresh."
- `curl`-ing the server-rendered HTML directly (bypassing CDN/browser caches) shows the same value on repeated requests — the ROUTE itself is cached, not an external layer.
- A same-deployment API route used as a `force-cache` fetch target, causing a real build failure because the fetch runs before the server exists.

## Related

- `syllabus/21-frontend-web/nextjs-server-vs-client-components.md`
- `syllabus/21-frontend-web/nextjs-rendering-strategies.md`
- `syllabus/11-system-design/caching-strategies-and-invalidation.md`
- `00-project/frontend-topic-register.md`
