---
title: "Flashcards: Data Fetching in the App Router: fetch Caching Semantics, revalidate, and cache: 'no-store'"
slug: nextjs-data-fetching-and-caching
document_type: flashcard-deck
domain: frontend
topic_id: F-204
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md
last_updated: 2026-09-07
---

# Flashcards: Data Fetching in the App Router: fetch Caching Semantics, revalidate, and cache: 'no-store'

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md`](../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md)

## Card: Why "uncached by default" doesn't guarantee a fresh page

**Prompt:**
`fetch()` with no `cache` option is documented as "not cached by default." Does that mean the page will always serve fresh data?

**Answer:**
Not necessarily. If nothing else in the route forces dynamic rendering, Next's separate route-level Full Route Cache can still statically cache the whole page, producing the same result for everyone — verified directly: a default fetch behaved identically to `force-cache` (same value, two real requests), not like explicit `no-store` (genuinely different values each time).

**Why it matters:**
This is the single most common source of "why is my page showing stale data even though I didn't cache anything" bugs.

**Common trap:**
Treating "fetch is uncached by default" as settling whether the whole page is fresh, without checking the route's actual static/dynamic classification.

**Related:**
[Data Fetching in the App Router: fetch Caching Semantics, revalidate, and cache: 'no-store'](../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md)

## Card: Why a same-deployment `force-cache` fetch can fail the build

**Prompt:**
A `force-cache`-eligible page fetches from an API route in the SAME Next.js deployment. What real failure can this cause, and why?

**Answer:**
A real `ECONNREFUSED` build failure — `force-cache` makes the route eligible for static generation, so Next attempts that fetch DURING `next build`, before the deployment's own server exists to serve the same-deployment API route.

**Why it matters:**
Verified directly: this exact failure was captured live, with the real error naming the specific page, when a same-server API route was used as the fetch target for a `force-cache` demo.

**Common trap:**
Assuming `force-cache`/default fetches only execute at request time, missing that static-generation-eligible fetches run during the build itself.

**Related:**
[Data Fetching in the App Router: fetch Caching Semantics, revalidate, and cache: 'no-store'](../syllabus/21-frontend-web/nextjs-data-fetching-and-caching.md)
