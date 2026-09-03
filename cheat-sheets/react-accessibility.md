---
title: "Cheat Sheet: React Accessibility (Semantic HTML, ARIA, Keyboard, Focus)"
slug: react-accessibility
document_type: cheat-sheet
domain: frontend
topic_id: F-116
tier: Intermediate
canonical: ../handbook/frontend/react-accessibility.md
last_updated: 2026-09-03
---

# React Accessibility (Semantic HTML, ARIA, Keyboard, Focus)

**Canonical chapter:** [`handbook/frontend/react-accessibility.md`](../handbook/frontend/react-accessibility.md)

## Core Mental Model

Accessibility is not a single property you either have or don't — it's a set of independently verifiable technical facts: does this element expose the right ROLE to assistive technology, is it reachable via KEYBOARD alone, does FOCUS move and return in a way a non-visual user can track, and are error/state ASSOCIATIONS (like a form error linked to its field) real, resolvable relationships rather than just visual proximity. Every claim should be proved directly (an accessibility-tree role, a `document.activeElement` check, a resolved `aria-describedby` id), not asserted as an unverifiable "this is accessible."

## Essential Definitions

- **Semantic HTML** — elements used for their intended purpose (`<button>`, `<nav>`, `<label>`); browsers/assistive tech derive role, keyboard behavior, and default announcements from the tag, for free.
- **ARIA** — attributes (`role`, `aria-*`) supplementing accessibility info for cases semantic HTML alone can't cover: custom widgets, or associating supplementary info (an error) with a control.
- **Keyboard navigation** — full usability via Tab/Shift+Tab/Enter/Space/arrows, no mouse.
- **Focus management** — deliberate control of keyboard focus as the UI changes; for a keyboard/screen-reader user, current focus position IS their entire sense of "where am I."

## Decision Table

| Question | Answer |
|---|---|
| Does a native HTML element already do what you need? | Use it directly — don't rebuild accessibility from a styled `div` |
| Building something genuinely custom with no native equivalent? | Follow WAI-ARIA Authoring Practices; verify role, keyboard reachability, and activation keys independently |
| Introducing a modal/drawer that should temporarily own focus? | Implement AND separately verify: entry, trap, and return |
| Showing a validation error tied to a field? | Real `aria-describedby`/`aria-invalid` with a resolvable id — verify it resolves to actual content |

**Native vs. custom widget:**

| Concern | Native semantic element | Custom `div`-based widget |
|---|---|---|
| Keyboard accessibility | Free, by default | Manual (`tabIndex`, key handlers, `role`) |
| Risk of subtle bugs | Low — platform-tested | Real — easy to miss an edge case (e.g., only Enter, not Space) |
| Best fit | Anything with a native equivalent | Genuinely novel widgets, with real tested parity |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- Semantic vs. div: accessibility tree showed the real `<button>` as `role: "button"`; the styled `<div onClick>` as `role: "generic"`. A real Tab-key test skipped the div entirely (focus jumped straight to the next element), while a mouse click on the div still worked.
- Focus trap modal: opening moved focus to the first focusable element automatically; Tab from the last element wrapped back to the first (trap held); Escape closed the modal AND returned focus to the exact trigger element.
- ARIA form error: after an invalid blur, `aria-invalid="true"`, `aria-describedby="username-error"`, and resolving that id via `document.getElementById` returned the real error text.

## Common Pitfalls

- Building interactive custom widgets from `div`/`span` without `tabIndex`, keyboard handlers, and the correct `role` — silently, completely unreachable via keyboard despite working with a mouse.
- Implementing a modal's focus trap or auto-focus but never verifying the FULL set (entry, trap, AND return) — a trap that never returns focus is a genuine, disorienting bug.
- Displaying a form error visually near its field with no `aria-describedby` link — sighted users see the association, screen-reader users get nothing extra.
- Adding `role="button"` without also adding `tabIndex={0}` and key handlers — now announced as a button but still unreachable via keyboard, a half-fix.

## Interview Answer Skeleton

**30-sec:** Accessibility is a set of independently verifiable facts, not a vague quality: right role exposed, keyboard-reachable, focus moves/returns correctly, error associations real and resolvable. Native semantic HTML gets keyboard accessibility for free; custom `div`-based widgets need role, `tabIndex`, and key handlers added manually and are easy to get subtly wrong.

**2-min:** Cite the real semantic-HTML evidence (div exposed as `role: generic`, proven unreachable via a real Tab test despite working for a mouse). Cover focus management as three separate, separately-verified behaviors (entry, trap, return) via real `document.activeElement` traces. Close with the ARIA form-error demo proving `aria-describedby` resolves to real content, not just visual proximity.

**Whiteboard:** Two boxes — "Native `<button>`" with three checkmarked arrows to "role: button," "in tab order," "Enter/Space activates," all "automatic." "`<div onClick>`" with three X'd-out arrows to the same three properties, each "must be added manually."

**Senior-level framing:** Names the specific technical mechanisms (accessibility tree, tab order, ARIA id resolution) rather than vague "make it accessible" statements, and proposes concrete verification methods for each claim.

## Production Warning Signs

- A support ticket describing a user "stuck" on a page unable to reach a button — reproduce with a real keyboard-only test (`document.activeElement` trace); a custom `div`-based widget with no `tabIndex` is the likely culprit.
- A hand-rolled focus trap that never releases focus once entered, genuinely stranding keyboard users.
- Treating accessibility as a final "audit pass" applied after a feature ships — retrofitting real ARIA + keyboard parity onto a shipped custom widget is genuinely more work than starting with the native element.

## Related

- `handbook/frontend/react-error-boundaries.md`
- `handbook/frontend/react-forms.md`
- `handbook/frontend/react-performance.md`
