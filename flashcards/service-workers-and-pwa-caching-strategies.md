---
title: "Flashcards: Service Workers and PWA"
slug: service-workers-and-pwa-caching-strategies
document_type: flashcard-deck
domain: 21-frontend-web
topic_id: F-404
canonical: ../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md
last_updated: 2026-09-21
---

# Flashcards: Service Workers and PWA

**Canonical chapter:** [`syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md`](../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md)

## Card: Why the page's own first load isn't controlled

**Prompt:**
If a page registers a service worker for the first time, does that same page load's own resource requests get intercepted by it?

**Answer:**
No, not by default — per spec, the page that triggers a service worker's first registration isn't "controlled" by it during that same load; its requests go straight to the network. `self.clients.claim()` in the worker's `activate` handler is the real mechanism that takes control of already-open pages immediately, without a reload. This chapter's own demo relies on it explicitly to avoid a reload mid-test.

**Why it matters:**
A very common source of confusion when a service worker "doesn't seem to be working" on first load — it's working correctly, just not controlling that specific load yet.

**Common trap:**
Assuming a reload is always required to see a service worker's effect.

**Related:**
[Core Concepts](../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md#core-concepts)

## Card: Cache-first vs. network-first, the real trade-off

**Prompt:**
What's the actual difference in trade-off between cache-first and network-first, not just the mechanical order of operations?

**Answer:**
Cache-first prioritizes speed and zero network cost, at the risk of staleness — correct for assets that rarely change. Network-first prioritizes freshness whenever possible, at the cost of a real network round-trip on every request, while still degrading gracefully via a cache fallback when the network genuinely fails.

**Why it matters:**
Choosing the wrong strategy for a resource's actual freshness needs is a real, common production bug.

**Common trap:**
Treating the two strategies as interchangeable "caching" rather than deliberate trade-offs for different resource types.

**Related:**
[Production Scenarios](../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md#production-scenarios)

## Card: Real measured evidence — offline is selective, not automatic

**Prompt:**
What did a real demo prove about offline support with a service worker?

**Answer:**
In genuine offline mode (`browserContext.setOffline(true)`), a cached asset (`/app.css`) loaded successfully, and a network-first endpoint (`/api/data`) fell back to its last cached value — but a resource that was never fetched or cached (`/never-cached.txt`) genuinely failed with `TypeError: Failed to fetch`, in the identical offline context, at the same moment. Server request counters stayed unchanged throughout, confirming no real network request reached the server.

**Why it matters:**
Directly disproves the common assumption that "having a service worker" means "the whole app works offline."

**Common trap:**
Claiming full offline support without having tested every route, including ones with no caching strategy applied.

**Related:**
[Internal Implementation](../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md#internal-implementation)
