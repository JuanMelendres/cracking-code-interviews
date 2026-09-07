---
title: "Flashcards: The Metadata API and SEO Fundamentals in Next.js"
slug: nextjs-metadata-api-and-seo
document_type: flashcard-deck
domain: frontend
topic_id: F-209
tier: Intermediate
canonical: ../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md
last_updated: 2026-09-07
---

# Flashcards: The Metadata API and SEO Fundamentals in Next.js

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md`](../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md)

## Card: What really happens when `metadataBase` is missing?

**Prompt:**
Documentation prose says a relative URL-based metadata field without `metadataBase` "will cause a build error." Is that what actually happens?

**Answer:**
No. Verified directly: the build succeeds, producing only a console warning. For a statically prerendered page, the actual, wrong consequence is worse — a hardcoded `http://localhost:3000` fallback is baked permanently into the page's real, production HTML.

**Why it matters:**
This is a real, silent production risk (broken OG image previews) with no build failure to catch it — a CI check on the warning string is the only real safeguard.

**Common trap:**
Trusting a documentation claim ("will cause a build error") as proof something is safely caught, without testing it directly.

**Related:**
[The Metadata API and SEO Fundamentals in Next.js](../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md) [Route Handlers: Building a Backend-for-Frontend Layer in Next.js](../syllabus/21-frontend-web/nextjs-route-handlers.md)

## Card: Do bots and browsers experience streaming metadata the same way?

**Prompt:**
For a dynamic page with a slow `generateMetadata`, does a bot's request behave the same as a normal browser's request?

**Answer:**
No. Verified with two real, contrasted chunk-timing traces: a normal request received page content in the first chunk at 44ms, well before a 1200ms-delayed `generateMetadata` resolved. The SAME route, hit with `Twitterbot/1.0`, didn't return response HEADERS until 1246ms — the entire response blocked until metadata was ready.

**Why it matters:**
Completes F-206's earlier finding (bot-blocking is scoped to `generateMetadata`) with the genuinely slow, dynamic case F-206's own demos never actually exercised.

**Common trap:**
Assuming streaming benefits everyone, missing that bots specifically get routed to the slower, blocking path.

**Related:**
[The Metadata API and SEO Fundamentals in Next.js](../syllabus/21-frontend-web/nextjs-metadata-api-and-seo.md) [Streaming & Suspense Boundaries in the App Router](../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md)
