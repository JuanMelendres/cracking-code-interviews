---
title: "Flashcards: React Memoization and Context: useMemo, useCallback, useContext"
slug: react-usememo-usecallback-and-usecontext
document_type: flashcard-deck
domain: frontend
topic_id: F-107/F-108
tier: Intermediate
canonical: ../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md
last_updated: 2026-09-07
---

# Flashcards: React Memoization and Context: useMemo, useCallback, useContext

**Canonical chapter:** [`syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`](../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md)

## Card: Two causes of Context re-renders

**Prompt:**
A memo()'d Context consumer still re-renders when an unrelated field changes. What's the likely context-related cause?

**Answer:**
The context's value is a single combined object; even though the specific field the consumer reads didn't change, the object REFERENCE did (recreated on every render, or because a sibling field changed) — `useContext` sees a "new" value and re-renders regardless of `memo`.

**Why it matters:**
This chapter's central, real, caught modeling mistake — verified with real before/after render counts.

**Common trap:**
Assuming `memo()` alone should have prevented this.

**Related:**
[React Memoization and Context: useMemo, useCallback, useContext](../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md)

## Card: useCallback without memo

**Prompt:**
Does wrapping a function in useCallback help if the component receiving it as a prop isn't wrapped in React.memo?

**Answer:**
No — with no `memo()` boundary, the child re-renders whenever its parent re-renders regardless of prop reference stability. `useCallback` alone provides zero measurable benefit without a `memo`'d consumer.

**Why it matters:**
The single most common real-world misuse of `useCallback`.

**Common trap:**
Treating `useCallback` as inherently beneficial rather than conditionally useful.

**Related:**
[React Memoization and Context: useMemo, useCallback, useContext](../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md)
