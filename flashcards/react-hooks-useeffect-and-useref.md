---
title: "Flashcards: React Hooks: useEffect and useRef"
slug: react-hooks-useeffect-and-useref
document_type: flashcard-deck
domain: frontend
topic_id: F-105/F-106
tier: Intermediate
canonical: ../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md
last_updated: 2026-09-07
---

# Flashcards: React Hooks: useEffect and useRef

**Canonical chapter:** [`syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md`](../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md)

## Card: Effect cleanup and leaks

**Prompt:**
What happens if an effect that starts a `setInterval` returns no cleanup function?

**Answer:**
The interval is never cleared. Every mount (including remounts on navigation, and StrictMode's dev-only double-mount) leaves its own running interval — measured directly in this chapter: 2 mounts produced 4 real leaked intervals due to StrictMode.

**Why it matters:**
The single most common `useEffect` production bug class.

**Common trap:**
Assuming a missing cleanup is "just a lint warning" rather than a real, accumulating resource leak.

**Related:**
[React Hooks: useEffect and useRef](../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md)

## Card: Stale closures in effects

**Prompt:**
Why does a value read inside a `setInterval` callback created in a `useEffect([])` never update?

**Answer:**
The effect's setup function runs once, at mount, and the callback passed to `setInterval` permanently closes over whatever the state equaled at that single moment — it's not re-created on later renders.

**Why it matters:**
Verified directly: a buggy logger stayed `[0,0,0,0,0]` through 3 real state updates.

**Common trap:**
Believing the callback somehow "sees" future state changes because it's inside a component.

**Related:**
[React Hooks: useEffect and useRef](../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md)
