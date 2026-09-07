---
title: "Flashcards: React Error Boundaries and Error Handling Strategy"
slug: react-error-boundaries
document_type: flashcard-deck
domain: frontend
topic_id: F-115
tier: Intermediate
canonical: ../syllabus/21-frontend-web/react-error-boundaries.md
last_updated: 2026-09-07
---

# Flashcards: React Error Boundaries and Error Handling Strategy

**Canonical chapter:** [`syllabus/21-frontend-web/react-error-boundaries.md`](../syllabus/21-frontend-web/react-error-boundaries.md)

## Card: What error boundaries catch vs. don't

**Prompt:**
Precisely, what do React error boundaries catch, and what do they NOT catch?

**Answer:**
Catch: errors thrown during rendering, in lifecycle methods, and in constructors of the tree below the boundary. Do NOT catch: event-handler errors, async callback errors (`fetch`/`.then()`/`setTimeout`), errors thrown in the boundary itself, and SSR errors.

**Why it matters:**
Verified directly: a `throw` inside `onClick` never triggered the boundary's fallback — only a global `window` error listener caught it, proving the escape.

**Common trap:**
Assuming any error anywhere inside a wrapped subtree will be caught, regardless of when/how it's thrown.

**Related:**
[React Error Boundaries and Error Handling Strategy](../syllabus/21-frontend-web/react-error-boundaries.md)

## Card: Boundary granularity's real cost

**Prompt:**
What's the actual, measured difference between a shared boundary and per-section granular boundaries when one section crashes?

**Answer:**
A shared boundary's ENTIRE subtree is replaced by the fallback when any part of it crashes — unrelated sibling sections stop rendering too. A granular, per-section boundary contains the crash to just that section; siblings keep rendering normally.

**Why it matters:**
Verified directly, side-by-side: crashing Widget A left Widgets B and C fully intact under granular boundaries, but caused the ENTIRE row (including B and C) to be replaced under a shared boundary.

**Common trap:**
Treating a single top-level boundary as sufficient "error handling" without recognizing its near-total blast radius.

**Related:**
[React Error Boundaries and Error Handling Strategy](../syllabus/21-frontend-web/react-error-boundaries.md)
