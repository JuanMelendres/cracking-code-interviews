---
title: "Flashcards: React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting"
slug: react-performance
document_type: flashcard-deck
domain: frontend
topic_id: F-117
tier: Advanced
canonical: ../syllabus/21-frontend-web/react-performance.md
last_updated: 2026-09-07
---

# Flashcards: React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting

**Canonical chapter:** [`syllabus/21-frontend-web/react-performance.md`](../syllabus/21-frontend-web/react-performance.md)

## Card: Why `React.memo` can silently do nothing

**Prompt:**
`React.memo` is applied to a component, but it still re-renders every time its parent does. What's the most likely cause?

**Answer:**
One of its props (an object, array, or function) is being created fresh — a new reference — on every parent render. `React.memo`'s comparison is reference-based, so even identical CONTENT still reads as "changed" if the reference differs.

**Why it matters:**
Verified directly: an inline `{ label: 'static' }` prop caused the child's render count to climb every unrelated parent update (2 -> 4 -> ... -> 10), while a `useMemo`-stabilized version of the exact same prop stayed frozen at 2.

**Common trap:**
Assuming `React.memo`'s mere presence in the code means memoization is actually happening — it requires stable references to work at all.

**Related:**
[React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting](../syllabus/21-frontend-web/react-performance.md)

## Card: What virtualization actually reduces, and how to verify it

**Prompt:**
What specific cost does list virtualization reduce, and how would you verify an implementation is actually working (not just capping the list)?

**Answer:**
It reduces the number of real DOM nodes mounted for a large list, regardless of React's own re-render behavior. Verify by counting DOM nodes directly (not visually), AND by scrolling to a non-zero position and re-checking that both the count stays low AND the rendered content/identities actually shifted — proving it's a genuine moving window, not a fixed first-N cap.

**Why it matters:**
Verified directly: 5,000 naive DOM nodes vs. 15 virtualized; after scrolling to `scrollTop=2000`, still 15 nodes, but now showing rows 68-82 instead of 0-14.

**Common trap:**
Confirming only that the initial render has fewer nodes, without checking that scrolling actually updates which items are rendered.

**Related:**
[React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting](../syllabus/21-frontend-web/react-performance.md)
