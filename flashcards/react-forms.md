---
title: "Flashcards: React Forms: Controlled vs. Uncontrolled, Validation Strategy, and React Hook Form / Zod"
slug: react-forms
document_type: flashcard-deck
domain: frontend
topic_id: F-114
tier: Intermediate
canonical: ../syllabus/21-frontend-web/react-forms.md
last_updated: 2026-09-07
---

# Flashcards: React Forms: Controlled vs. Uncontrolled, Validation Strategy, and React Hook Form / Zod

**Canonical chapter:** [`syllabus/21-frontend-web/react-forms.md`](../syllabus/21-frontend-web/react-forms.md)

## Card: Controlled vs. uncontrolled re-render cost

**Prompt:**
What's the actual, measurable re-render difference between a controlled and an uncontrolled input while typing?

**Answer:**
A controlled input re-renders its component once per keystroke (React must reassert the `value` prop). An uncontrolled input causes zero re-renders from typing — the DOM holds the value until something explicitly reads it via a ref.

**Why it matters:**
Verified directly: 3 controlled keystrokes moved a render counter from 2 to 8 (+6); 3 uncontrolled keystrokes left the counter unchanged.

**Common trap:**
Defaulting every field to controlled regardless of whether the live value is actually needed anywhere.

**Related:**
[React Forms: Controlled vs. Uncontrolled, Validation Strategy, and React Hook Form / Zod](../syllabus/21-frontend-web/react-forms.md)

## Card: Why react-hook-form has fewer re-renders

**Prompt:**
Why does `react-hook-form` produce fewer re-renders than a hand-rolled, fully controlled form?

**Answer:**
It registers inputs largely as uncontrolled DOM elements internally (via refs from `register()`), reading values directly from the DOM on validation/submit rather than storing every keystroke in React state — only triggering a re-render when something visible actually changes, like an error appearing.

**Why it matters:**
Verified directly: typing into RHF-registered fields didn't move the chapter's render counter the way the fully controlled demo field did.

**Common trap:**
Assuming a form library must be "more React-y" and therefore more controlled/re-render-heavy — it's actually the opposite.

**Related:**
[React Forms: Controlled vs. Uncontrolled, Validation Strategy, and React Hook Form / Zod](../syllabus/21-frontend-web/react-forms.md)
