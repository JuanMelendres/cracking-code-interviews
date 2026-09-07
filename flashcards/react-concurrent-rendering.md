---
title: "Flashcards: Concurrent React: Transitions, Deferred Values, and Suspense for Data"
slug: react-concurrent-rendering
document_type: flashcard-deck
domain: frontend
topic_id: F-113
tier: Advanced
canonical: ../syllabus/21-frontend-web/react-concurrent-rendering.md
last_updated: 2026-09-07
---

# Flashcards: Concurrent React: Transitions, Deferred Values, and Suspense for Data

**Canonical chapter:** [`syllabus/21-frontend-web/react-concurrent-rendering.md`](../syllabus/21-frontend-web/react-concurrent-rendering.md)

## Card: `useTransition` vs. `useDeferredValue`

**Prompt:**
When would you reach for `useTransition` over `useDeferredValue`, and vice versa?

**Answer:**
`useTransition` when you own the specific `setState` call for the expensive update — wrap it in `startTransition` and get an explicit `isPending` flag. `useDeferredValue` when you only have a value (a prop, something derived, no setter you control) that feeds expensive rendering.

**Why it matters:**
Verified directly: both demos produce the same kind of "pending then resolved" history, but from two different entry points into the same underlying scheduling mechanism.

**Common trap:**
Assuming they're interchangeable regardless of whether you control the setter — the deciding factor is exactly that.

**Related:**
[Concurrent React: Transitions, Deferred Values, and Suspense for Data](../syllabus/21-frontend-web/react-concurrent-rendering.md)

## Card: What Suspense actually waits for

**Prompt:**
What kind of "not ready yet" does Suspense handle, and how is that different from what `useTransition` handles?

**Answer:**
Suspense handles genuinely asynchronous readiness — a real pending promise (data fetching, code-splitting) — where nothing exists to render until external work resolves. `useTransition` reprioritizes already-available, purely synchronous rendering work; nothing is actually "pending" externally, React is just choosing when to run it.

**Why it matters:**
Verified directly: the Suspense demo showed a real fallback during an actual 3-second pending promise, not a synchronous computation being deprioritized.

**Common trap:**
Treating transitions and Suspense as interchangeable "loading state" tools instead of recognizing the sync-vs-async distinction.

**Related:**
[Concurrent React: Transitions, Deferred Values, and Suspense for Data](../syllabus/21-frontend-web/react-concurrent-rendering.md)
