---
title: "Flashcards: React Fundamentals: JSX, Components, Props, and State"
slug: react-fundamentals-jsx-components-props-and-state
document_type: flashcard-deck
domain: frontend
topic_id: F-101-F-104
tier: Beginner
canonical: ../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md
last_updated: 2026-09-07
---

# Flashcards: React Fundamentals: JSX, Components, Props, and State

**Canonical chapter:** [`syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`](../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md)

## Card: Why index keys break on list changes

**Prompt:**
Why does using the array index as a React list `key` cause bugs when items are removed or reordered?

**Answer:**
The key ties DOM-node identity to POSITION, not the underlying data. When positions shift, React reuses the DOM node for "the same index" even though the data there is now different — carrying over any DOM-owned state (like an uncontrolled input's typed value) to the wrong item.

**Why it matters:**
The single most common React interview red flag; measured directly in this chapter's demo.

**Common trap:**
Reciting "it's bad practice" without explaining the mechanism.

**Related:**
[React Fundamentals: JSX, Components, Props, and State](../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md)

## Card: useState and component instances

**Prompt:**
If three components render the same function definition and each calls `useState(0)`, do they share state?

**Answer:**
No — each JSX usage is a separate component instance with its own state slot. State belongs to the instance's position in the render tree, not the function definition.

**Why it matters:**
Explains why reusable components work correctly without manual state isolation.

**Common trap:**
Assuming `useState` behaves like a module-level or closure-shared variable.

**Related:**
[React Fundamentals: JSX, Components, Props, and State](../syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md)
