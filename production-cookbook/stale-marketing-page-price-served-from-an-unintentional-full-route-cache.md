---
title: "Stale Marketing Page Price Served From an Unintentional Full Route Cache"
document_type: production-cookbook-entry
domain: frontend-web
status: draft
last_updated: 2026-09-12
related_handbook:
  - ../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md
source: syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md#production-scenarios
---

# Stale Marketing Page Price Served From an Unintentional Full Route Cache

## Context

A pricing page fetches current prices from an internal pricing service with no `cache` option specified, assuming "fetch is uncached by default" means every visitor gets a fresh price.

## Symptoms

A support ticket reports seeing an old, pre-sale price hours after a price change went live elsewhere.

## Impact

Customers see stale pricing information, risking a real discrepancy between what's advertised and what's actually charged.

## Initial Hypotheses

- A CDN cache serving stale content — checked, the issue persists even bypassing the CDN layer.
- A browser cache — checked, reproduces on a fresh, uncached client request.
- The fetch's default (no `cache` option, no other request-time API) made the route eligible for Next's own Full Route Cache — correct.

## Evidence

`curl`-ing the page's server-rendered HTML directly (bypassing any CDN/browser layer) shows the same stale price on repeated requests — the route itself, not an external cache, is serving a statically-generated result from build time.

## Investigation Timeline

1. Support ticket reports a stale price displayed hours after a live price change.
2. CDN and browser-cache hypotheses ruled out via a direct `curl` against the origin.
3. The stale price confirmed to originate from the route's own build-time-generated output.

## Root Cause

The team's mental model ("fetch defaults to fresh") was the exact naive reading that the framework's actual default (eligible for the Full Route Cache absent an explicit `cache` option) contradicts.

## Immediate Mitigation

Manually trigger a redeploy or on-demand revalidation to refresh the stale page immediately.

## Permanent Fix

Either set `cache: 'no-store'` for genuinely per-request-fresh pricing, or, for a page that doesn't need per-millisecond freshness, use `next: { revalidate: 60 }` with a tag paired with `revalidateTag` called from the price-update flow itself — fast page loads and freshness triggered exactly when prices actually change.

## Alternatives Considered

Disabling caching site-wide as a blanket fix — rejected as discarding a real performance benefit for pages that don't have this specific freshness requirement.

## Trade-offs

`cache: 'no-store'` guarantees freshness but gives up the performance benefit of static generation entirely; `revalidateTag`-driven revalidation keeps most of the performance benefit at the cost of needing the price-update flow to remember to call it.

## Prevention

Treat every `fetch` call's caching behavior as an explicit, reviewed decision (stated `cache` option or `revalidate` value) rather than relying on an assumed default, specifically for any data that has a real freshness requirement.

## Monitoring and Alerts

- A scheduled synthetic check comparing the pricing page's displayed price against the pricing service's actual current value, alerting on drift.
- A code-review checklist item requiring an explicit caching-behavior justification for any `fetch` call touching price, inventory, or other freshness-sensitive data.

## Interview Story

Present as a representative scenario unless personally lived through an equivalent incident.

- **Situation:** a pricing page served an hours-stale price despite an assumption that fetch was uncached by default.
- **Task:** find the actual source of staleness without assuming it was an external caching layer.
- **Action:** `curl`-ed the origin directly, ruling out CDN and browser caches, and confirmed the route itself was serving a build-time-generated result.
- **Result:** added explicit, freshness-appropriate caching behavior (`revalidateTag` triggered from the price-update flow) instead of relying on an assumed default.

## Staff-Level Discussion

The team's mental model was the exact naive reading this framework's real evidence contradicts — a caching default that seems intuitive ("fetch defaults to fresh") can differ from the framework's actual, documented behavior in a way that only surfaces once a real freshness requirement is violated. The organizational lesson is treating every fetch's caching behavior as an explicit, reviewed decision for freshness-sensitive data, rather than trusting an assumed default that was never actually verified against the framework's documentation.

## Related Handbook Chapters

- [Next.js Data Fetching and Caching](../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md) — the canonical Full Route Cache and `revalidateTag` mechanics behind this incident's fix.
