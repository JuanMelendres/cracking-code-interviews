---
title: "Flashcards: React Component Patterns"
slug: react-component-patterns
document_type: flashcard-deck
domain: frontend
topic_id: F-111
tier: Advanced
canonical: ../syllabus/21-frontend-web/react-component-patterns.md
last_updated: 2026-09-07
---

# Flashcards: React Component Patterns

**Canonical chapter:** [`syllabus/21-frontend-web/react-component-patterns.md`](../syllabus/21-frontend-web/react-component-patterns.md)

## Card: HOC hidden indirection

**Prompt:**
What's structurally different about a HOC-wrapped component versus a custom-hook version of the same behavior?

**Answer:**
The HOC creates and mounts a separate, hidden wrapper component instance that injects the value as a prop from a non-obvious source. The hook introduces zero extra components — the value is a local variable from an explicit function call.

**Why it matters:**
Verified directly: all three (HOC/render-prop/hook) implementations were functionally identical across real resizes, but only the code structure differs — which is the actual point of comparing them.

**Common trap:**
Dismissing HOCs as simply "bad" instead of naming the specific structural cost.

**Related:**
[React Component Patterns](../syllabus/21-frontend-web/react-component-patterns.md)

## Card: What compound components solve

**Prompt:**
What problem do compound components solve that's different from what HOCs/render props/hooks solve?

**Answer:**
HOCs/render props/hooks share stateful BEHAVIOR across unrelated components. Compound components share state IMPLICITLY among a FIXED set of components always used together (like Tabs/Tabs.List/Tabs.Tab), via a Context scoped to just that group.

**Why it matters:**
Confusing the two problem categories is the most common conceptual mistake in this topic.

**Common trap:**
Treating compound components as "another way to do hooks."

**Related:**
[React Component Patterns](../syllabus/21-frontend-web/react-component-patterns.md)
