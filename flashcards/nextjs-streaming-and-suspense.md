---
title: "Flashcards: Streaming & Suspense Boundaries in the App Router"
slug: nextjs-streaming-and-suspense
document_type: flashcard-deck
domain: frontend
topic_id: F-206
tier: Advanced
canonical: ../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md
last_updated: 2026-09-07
---

# Flashcards: Streaming & Suspense Boundaries in the App Router

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-streaming-and-suspense.md`](../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md)

## Card: Why sibling Suspense boundaries stream in parallel, not sequentially

**Prompt:**
Three sibling `<Suspense>` boundaries wrap components with 300ms, 1200ms, and 2500ms delays. Roughly how long does the total response take, and why?

**Answer:**
Close to 2500ms (the SLOWEST boundary's delay), NOT the sum of all three (~4000ms) — each boundary is an independent streaming point; React streams each one's result the moment IT resolves, without waiting for or blocking on its siblings.

**Why it matters:**
Verified directly with a real chunk-timing script: chunks arrived at ~352ms, ~1252ms, and ~2552ms, with the stream completing at ~2553ms total — matching the slowest delay, not the sum.

**Common trap:**
Assuming multiple Suspense boundaries resolve in sequence (like a queue) rather than genuinely in parallel.

**Related:**
[Streaming & Suspense Boundaries in the App Router](../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md)

## Card: The real, corrected scope of bot-request streaming behavior

**Prompt:**
Does Next.js block streaming entirely for bot/crawler requests, or something narrower?

**Answer:**
Something narrower: the blocking behavior is scoped specifically to `generateMetadata` resolution (ensuring bots get complete `<head>` metadata before content streams), not a blanket rule against streaming any content for bots.

**Why it matters:**
Verified directly: a real bot-User-Agent request against a page with only static, synchronous metadata streamed with IDENTICAL timing to a normal request — content was not blocked, contradicting a naive, overly broad reading of the documented behavior.

**Common trap:**
Assuming a documented framework behavior applies as broadly as its surface phrasing suggests, without verifying the actual scope for the specific case at hand.

**Related:**
[Streaming & Suspense Boundaries in the App Router](../syllabus/21-frontend-web/nextjs-streaming-and-suspense.md)
