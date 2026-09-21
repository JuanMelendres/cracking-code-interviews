---
title: "Cheat Sheet: Service Workers and PWA"
slug: service-workers-and-pwa-caching-strategies
document_type: cheat-sheet
domain: 21-frontend-web
topic_id: F-404
canonical: ../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md
last_updated: 2026-09-21
---

# Service Workers and PWA: Caching Strategies and Offline Support

**Canonical chapter:** [`syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md`](../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md)

## Core Mental Model

A service worker is a programmable proxy between the page and the network, not an automatic cache the browser manages for you. Cache-first and network-first answer different real freshness trade-offs.

## Essential Definitions

- **Service worker** — a script, in its own execution context, that intercepts requests from pages in its scope via a `fetch` event handler.
- **Cache-first** — check cache, serve on hit, only fetch network on a genuine miss. Correct for rarely-changing assets.
- **Network-first** — try network first, fall back to cache only on failure. Correct for data that should stay fresh, degrading gracefully offline.

## Decision Table

| Resource type | Strategy | Why |
|---|---|---|
| Static assets (CSS, fonts, logos) | Cache-first | Rarely changes; zero network cost once cached |
| Live/changing data (API responses) | Network-first | Should reflect current server state whenever reachable |
| First page load, worker still installing | Neither — goes straight to network | Not controlled yet unless `clients.claim()` is used |
| Resource never fetched/cached | Neither applies | Genuinely fails offline — no fallback exists |

## Common Pitfalls

- Assuming a service worker registering on a page's first load automatically controls that same load's requests.
- Applying cache-first to frequently-changing data — serves stale content indefinitely with no re-check.
- Claiming "the app works offline" without having tested every route, including ones with no caching strategy.

## Interview Answer Skeleton

**30-sec:** A service worker intercepts requests for pages in its scope. Cache-first checks cache before network; network-first tries network first and falls back to cache only on failure. Offline support only works for requests a strategy actually caches.

**2-min:** Add: a real demo proves both strategies directly — cache-first served 3 repeated fetches with zero new server requests, network-first hit the server every time online, and in genuine offline mode a cached asset succeeded while a never-cached resource genuinely failed (`TypeError: Failed to fetch`), in the identical offline context.

**Staff-level framing:** Choosing which resources need offline support, and which strategy each needs, is a real product/engineering trade-off — weigh the service worker's real lifecycle complexity against the actual offline/performance value for the specific app.

## Related

- syllabus/21-frontend-web/websocket-and-server-sent-events-for-realtime-ui.md
- syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md
