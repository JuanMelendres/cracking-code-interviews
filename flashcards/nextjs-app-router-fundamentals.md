---
title: "Flashcards: Next.js App Router Fundamentals: Nested Layouts and Route Groups"
slug: nextjs-app-router-fundamentals
document_type: flashcard-deck
domain: frontend
topic_id: F-202
tier: Beginner
canonical: ../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md
last_updated: 2026-09-07
---

# Flashcards: Next.js App Router Fundamentals: Nested Layouts and Route Groups

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-app-router-fundamentals.md`](../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md)

## Card: When a nested layout unmounts vs. persists

**Prompt:**
A layout is declared at `app/dashboard/layout.js`. Under what specific condition does it unmount on navigation, versus persist?

**Answer:**
It persists when the destination route still shares it as an ancestor in the file tree (any route under `app/dashboard/`). It unmounts when the destination route does NOT share it as an ancestor (any route outside `app/dashboard/`) — React has no matching tree position to reuse, so the component instance is genuinely removed.

**Why it matters:**
Verified directly: navigating within `/dashboard/*` left the layout's mount counter unchanged; navigating to `/about` made `document.querySelector('[data-testid="dashboard-layout"]')` return `null` — a real, observed unmount, not an assumption.

**Common trap:**
Assuming layout persistence is a global, unconditional guarantee rather than scoped to shared ancestry.

**Related:**
[Next.js App Router Fundamentals: Nested Layouts and Route Groups](../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md)

## Card: What a route group actually does

**Prompt:**
What specifically does wrapping a folder name in parentheses (e.g. `(marketing)`) do in the Next.js App Router?

**Answer:**
It excludes that folder segment from the resulting URL, while still fully including its `layout.js` in that route's layout composition. `app/(marketing)/pricing/page.js` resolves to `/pricing`, not `/marketing/pricing`, but still renders through `app/(marketing)/layout.js`.

**Why it matters:**
Verified directly two independent ways: `window.location.pathname === "/pricing"` in a live session, and a real `next build` route manifest listing `/pricing` with no `/marketing` segment anywhere.

**Common trap:**
Assuming a route group changes routing/matching behavior beyond URL-exclusion and layout-scoping — it doesn't.

**Related:**
[Next.js App Router Fundamentals: Nested Layouts and Route Groups](../syllabus/21-frontend-web/nextjs-app-router-fundamentals.md)
