---
title: "Flashcards: React State Management Landscape: Context vs. Redux Toolkit vs. Zustand vs. Server State"
slug: react-state-management
document_type: flashcard-deck
domain: frontend
topic_id: F-120
tier: Advanced
canonical: ../syllabus/21-frontend-web/react-state-management.md
last_updated: 2026-09-07
---

# Flashcards: React State Management Landscape: Context vs. Redux Toolkit vs. Zustand vs. Server State

**Canonical chapter:** [`syllabus/21-frontend-web/react-state-management.md`](../syllabus/21-frontend-web/react-state-management.md)

## Card: Why Context re-renders an unrelated consumer

**Prompt:**
Two components both call `useContext` on the same provider — one reads `count`, the other reads `name`. Why does updating ONLY `count` re-render both?

**Answer:**
`useContext` subscribes a component to the whole Provider VALUE, not to specific fields within it — React has no mechanism to track which property of that value a given consumer actually reads. Any change to the value (even to a field the consumer never uses) triggers a re-render.

**Why it matters:**
Verified directly: clicking a `count`-only update moved BOTH the `count` consumer's AND the `name` consumer's render counters from 2 to 4.

**Common trap:**
Assuming Context is simply "less efficient" without being able to name the actual mechanism (whole-value subscription, no per-field tracking).

**Related:**
[React State Management Landscape: Context vs. Redux Toolkit vs. Zustand vs. Server State](../syllabus/21-frontend-web/react-state-management.md)

## Card: What TanStack Query's cache deduplication actually proves

**Prompt:**
Two independent components both call `useQuery` with the exact same `queryKey`. How many real network requests fire, and why does that matter compared to hand-rolled `useEffect` fetching?

**Answer:**
Exactly one — verified directly with a real network trace showing a single `GET` request despite two independently mounted consumers. TanStack Query's cache is keyed by `queryKey`; a second consumer with the same key subscribes to the existing request/cached result instead of firing its own. Hand-rolled `useEffect` + `useState` fetching provides no such deduplication by default — each component fetches independently.

**Why it matters:**
This is the concrete difference between treating fetched data as ad hoc per-component state versus genuinely shared, cache-managed server state.

**Common trap:**
Fixing duplicate fetches by lifting them into Redux/Zustand instead of a server-state library — this works but requires manually reimplementing caching/deduplication/staleness logic a server-state library already provides.

**Related:**
[React State Management Landscape: Context vs. Redux Toolkit vs. Zustand vs. Server State](../syllabus/21-frontend-web/react-state-management.md)
