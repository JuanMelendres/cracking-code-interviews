---
title: "Flashcards: Next.js's Role: File-Based Routing and Why a Meta-Framework Over Plain React/Vite"
slug: nextjs-fundamentals
document_type: flashcard-deck
domain: frontend
topic_id: F-201
tier: Beginner
canonical: ../syllabus/21-frontend-web/nextjs-fundamentals.md
last_updated: 2026-09-07
---

# Flashcards: Next.js's Role: File-Based Routing and Why a Meta-Framework Over Plain React/Vite

**Canonical chapter:** [`syllabus/21-frontend-web/nextjs-fundamentals.md`](../syllabus/21-frontend-web/nextjs-fundamentals.md)

## Card: What actually creates a Next.js App Router route

**Prompt:**
What specifically creates the `/about` route in a Next.js App Router project — what's the minimum required?

**Answer:**
A single file at `app/about/page.js`, default-exporting a React component. Nothing else — no router import, no route registration, no configuration file. The file's LOCATION in the `app/` tree is the entire routing mechanism.

**Why it matters:**
Verified directly: a real `next build` produced a route manifest listing `/about` derived purely from that file's location, with zero router configuration anywhere in the project.

**Common trap:**
Describing file-based routing as a vague convenience rather than being able to state precisely what minimal file/location produces a given route.

**Related:**
[Next.js's Role: File-Based Routing and Why a Meta-Framework Over Plain React/Vite](../syllabus/21-frontend-web/nextjs-fundamentals.md)

## Card: How to prove a layout doesn't remount on navigation

**Prompt:**
A teammate claims Next.js layouts "don't remount" when navigating between sibling pages. How would you actually verify this, rather than trusting the documentation?

**Answer:**
Put a `useRef`-based counter inside a client component in the layout, incremented once inside a `useEffect` with an empty dependency array (so it only fires on genuine mount). Navigate between sibling routes via real `<Link>` clicks, then check the counter's value before and after — if it's unchanged, the layout genuinely didn't remount.

**Why it matters:**
Verified directly: the counter stayed at its initial value across three real navigations (Home → About → Blog), while each page's own content changed on every transition — a measured proof, not a documentation quote.

**Common trap:**
Assuming a framework's documented behavioral guarantee holds without a concrete way to verify it when it actually matters (e.g., before building a feature — a persistent audio player, an in-progress multi-step form — that depends on that guarantee).

**Related:**
[Next.js's Role: File-Based Routing and Why a Meta-Framework Over Plain React/Vite](../syllabus/21-frontend-web/nextjs-fundamentals.md)
