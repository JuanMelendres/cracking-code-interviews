---
title: "Cheat Sheet: Next.js Rendering Strategies"
slug: nextjs-rendering-strategies
document_type: cheat-sheet
domain: frontend
topic_id: F-205
tier: Intermediate
canonical: ../handbook/frontend/nextjs-rendering-strategies.md
last_updated: 2026-09-03
---

# Next.js Rendering Strategies

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-rendering-strategies.md`](../syllabus/21-frontend-web/nextjs-rendering-strategies.md)

The topic register frames this as "a direct analogue to the backend's caching-strategies cheat sheet."

## Core Mental Model

Every rendering strategy answers the same question — WHEN does this page's HTML actually get generated — and that answer determines its performance/freshness trade-off. SSR generates HTML at REQUEST time, every time. SSG generates HTML at BUILD time, once. ISR is SSG plus a revalidation mechanism, so content can refresh without a full redeploy.

## Essential Definitions

- **SSR** — triggered by a Request-time API (`cookies()`, `headers()`, `searchParams`) or `cache: 'no-store'`; route marked `ƒ` (Dynamic); genuinely re-executes per request.
- **SSG** — triggered by `generateStaticParams` (or simply no dynamic dependency); real build manifests show a THIRD, distinct marker `●` for `generateStaticParams`-driven routes, separate from plain static `○`.
- **ISR** — SSG plus `next.revalidate` (time-based) or `revalidateTag`/`revalidatePath` (on-demand); proven in the F-204 chapter, cited here rather than re-demonstrated.
- **`dynamicParams` (default `true`)** — governs unlisted dynamic param values: NOT a 404, generated on-demand on first request, then cached.

## Decision Table

| Concern | SSR | SSG (`generateStaticParams`) | ISR |
|---|---|---|---|
| When HTML is generated | Every request (measured: differing User-Agent echo) | Once, at build time (measured: frozen timestamp) | Once, then scheduled or on-demand (proven in F-204) |
| Freshness | Always current | Fixed until next deploy | Bounded staleness or instant |
| Per-request server cost | Real compute every request | None — static file | None between regenerations |
| Best fit | Genuinely per-request/per-user data | Same for every visitor, rarely updated | Changes occasionally or on a known event |

## Key Numbers (real, curl'd against a clean production build)

- SSR route: two curl requests with different `User-Agent` headers rendered two genuinely different values (`FakeBrowserOne/1.0` then `FakeBrowserTwo/2.0`).
- SSG route (`id=1`, listed): **identical** timestamp across two requests — build-time-frozen.
- SSG route (`id=999`, unlisted): a **different** timestamp from `id=1` (generated later, on first request) but **identical** across its own two requests — on-demand-then-cache.

## Common Pitfalls

- Choosing SSR for an entire route because one small part needs request-specific data, instead of isolating just that part.
- Assuming an unlisted dynamic-route param 404s, rather than the real default behavior (on-demand generation, then caching).
- Treating "SSG" and "plain static rendering" as synonyms — the build tooling itself distinguishes them (`●` vs. `○`).

## Interview Answer Skeleton

**30-sec:** SSR renders fresh HTML per request (real per-request User-Agent echoes prove genuine re-execution). SSG renders once at build time via `generateStaticParams` (real frozen timestamp), marked with a distinct `●`. ISR adds time-based or on-demand revalidation on top of SSG. An unlisted dynamic param still works — generated on first request, then cached.

**2-min:** Cite the real SSR evidence (two different User-Agent values → two different renders, confirmed by the `ƒ` marker), the real SSG evidence (`id=1` frozen, `id=999` generated later but then stable), and the distinct `●` manifest marker. Close with the Decision Framework: choose per-route, illustrated by a Production Scenario splitting one strategy into three for three page types.

**Whiteboard:** Three columns — SSR / SSG / ISR — each with a clock icon: "every request" / "once, at build" / "once, then scheduled or on-demand." Under SSR, two different inputs produce two different outputs. Under SSG, one build-time render produces a frozen timestamp, with a branch for an unlisted param generating its own frozen timestamp on first request.

**Staff-level framing:** Rendering-strategy choice is a per-route (or per-component) cost/freshness optimization, not a single whole-application default chosen once — revisit it as traffic patterns and update frequency evolve, the same discipline F-204 applied to fetch caching one layer down.

## Production Warning Signs

- An entire product catalog shipped as SSR because of one small personalization feature that barely changes rendered output for 95% of visitors — every page view now pays a full server-render cost unnecessarily.
- The fix: SSG + on-demand `revalidateTag` for high-traffic pages, ISR with a longer window for the long tail, and an isolated Suspense-boundaried Client Component for the genuinely personalized widget — three strategies, not one.
- Listing every possible dynamic param value in `generateStaticParams` for an unbounded param space, incurring a massive, mostly wasted build-time cost.

## Related

- `syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md`
- `syllabus/21-frontend-web/nextjs-streaming-and-suspense.md`
- `syllabus/11-system-design/caching-strategies-and-invalidation.md`
- `00-project/frontend-topic-register.md`
