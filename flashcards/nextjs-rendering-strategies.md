---
title: "Flashcards: Rendering Strategies: SSR, SSG, and ISR — Mechanics and When to Choose Each"
slug: nextjs-rendering-strategies
document_type: flashcard-deck
domain: frontend
topic_id: F-205
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-rendering-strategies.md
last_updated: 2026-09-07
---

# Flashcards: Rendering Strategies: SSR, SSG, and ISR — Mechanics and When to Choose Each

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-rendering-strategies.md`](../syllabus/21-frontend-web/nextjs-rendering-strategies.md)

## Card: What happens for a dynamic route param not in `generateStaticParams`

**Prompt:**
A dynamic route uses `generateStaticParams` returning `["1", "2"]`. A user requests `/product/999`. What happens?

**Answer:**
It is NOT a 404. `dynamicParams` defaults to `true`, so Next generates that page on-demand on its first real request, then caches the result for subsequent requests to that same param value.

**Why it matters:**
Verified directly: `id=999`'s rendered timestamp differed from `id=1`'s (generated later, on-demand) but stayed IDENTICAL across its own two repeated requests (cached after first generation).

**Common trap:**
Assuming every possible dynamic param value must be explicitly enumerated in `generateStaticParams` for the route to work at all.

**Related:**
[Rendering Strategies: SSR, SSG, and ISR — Mechanics and When to Choose Each](../syllabus/21-frontend-web/nextjs-rendering-strategies.md)

## Card: The real, distinct SSG build-manifest marker

**Prompt:**
Does a real `next build` distinguish routes generated via `generateStaticParams` from routes that are simply static because they have no dynamic dependency at all?

**Answer:**
Yes — a real captured build showed `generateStaticParams`-driven routes marked `●` (SSG), a DIFFERENT symbol from the `○` (Static) marker used for routes with no dynamic dependency at all.

**Why it matters:**
This is a genuine, build-tool-level confirmation that SSG is its own mechanical category, not just a synonym for "static" — useful when reading a real route manifest to understand exactly why a given route ended up static.

**Common trap:**
Treating "SSG" and "static rendering" as interchangeable terms without realizing the tooling itself distinguishes them.

**Related:**
[Rendering Strategies: SSR, SSG, and ISR — Mechanics and When to Choose Each](../syllabus/21-frontend-web/nextjs-rendering-strategies.md)
