---
title: "Flashcards: React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management"
slug: react-accessibility
document_type: flashcard-deck
domain: frontend
topic_id: F-116
tier: Intermediate
canonical: ../syllabus/21-frontend-web/react-accessibility.md
last_updated: 2026-09-07
---

# Flashcards: React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management

**Canonical chapter:** [`syllabus/21-frontend-web/react-accessibility.md`](../syllabus/21-frontend-web/react-accessibility.md)

## Card: Why div-based buttons fail keyboard users

**Prompt:**
A `<div onClick>` styled to look exactly like a button works fine with a mouse. What's actually broken, and why does it matter?

**Answer:**
It's not in the default tab order (no `tabIndex`) and has no keyboard activation (no `onKeyDown` for Enter/Space) — a `<div>` has neither by default, unlike a native `<button>`. It's completely unreachable to a keyboard-only or screen-reader user, despite looking and working identically for a mouse user.

**Why it matters:**
Verified directly: a real Tab-key test skipped the div entirely, jumping straight to the next element, while the div's own click handler still fired correctly on a mouse click.

**Common trap:**
Assuming "it works when I click it" is sufficient evidence the widget is generally accessible.

**Related:**
[React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management](../syllabus/21-frontend-web/react-accessibility.md)

## Card: The three separate parts of modal focus management

**Prompt:**
What are the three separate, independently-verifiable behaviors that make up "good focus management" for a modal?

**Answer:**
(1) Focus moves INTO the modal automatically when it opens. (2) Tab is trapped at the modal's boundaries — cycling within it, never escaping to the page behind. (3) Focus is explicitly RETURNED to the element that opened the modal when it closes.

**Why it matters:**
Verified independently: each behavior was confirmed with its own real `document.activeElement` check — a modal that gets one or two of these right but not all three is still a genuinely broken experience for keyboard users.

**Common trap:**
Treating "the trap works" as proof that focus management overall is correct, without separately checking entry and return.

**Related:**
[React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management](../syllabus/21-frontend-web/react-accessibility.md)
