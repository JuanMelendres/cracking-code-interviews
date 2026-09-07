---
title: "Flashcards: React Testing: RTL Philosophy, Mocking, and E2E with Playwright"
slug: react-testing
document_type: flashcard-deck
domain: frontend
topic_id: F-118
tier: Advanced
canonical: ../syllabus/21-frontend-web/react-testing.md
last_updated: 2026-09-07
---

# Flashcards: React Testing: RTL Philosophy, Mocking, and E2E with Playwright

**Canonical chapter:** [`syllabus/21-frontend-web/react-testing.md`](../syllabus/21-frontend-web/react-testing.md)

## Card: Why class-name/DOM-position queries are risky

**Prompt:**
A test queries an element with `container.querySelector('.field-wrap input')`. What's the concrete risk, and what happened when this chapter tested it directly?

**Answer:**
The query is coupled to markup, not user-facing behavior — a purely cosmetic refactor can break it with zero real regression. Verified directly: renaming `.field-wrap` to `.input-group` (plus reordering two fields) made `inputs[0]` `undefined`, producing a real `Unable to fire a "change" event - please provide a DOM element` error — while a role/label-based test on the same component needed zero changes through the same refactor.

**Why it matters:**
This is the concrete mechanism behind "test behavior, not implementation" — not just a stated principle.

**Common trap:**
Treating query style as a stylistic preference rather than a real, demonstrated source of false-negative test failures.

**Related:**
[React Testing: RTL Philosophy, Mocking, and E2E with Playwright](../syllabus/21-frontend-web/react-testing.md)

## Card: What a mock proves beyond a stub, on the frontend

**Prompt:**
`fetchUser` is mocked and a component test asserts the final rendered output. What additional assertion makes this a genuine interaction check, not just a return-value check?

**Answer:**
`expect(fetchUser).toHaveBeenCalledWith(42)` — asserting the mock was called with the CORRECT arguments, not just that it eventually returned something the component rendered. A wrong-id bug could otherwise still render successfully-looking (but wrong) data and pass a return-value-only test.

**Why it matters:**
Directly parallels the backend's Mockito `verify()` point in `test-strategy-and-test-doubles.md` — the interaction itself is often the real bug surface.

**Common trap:**
Mocking a dependency and only checking the resulting UI state, never the call arguments.

**Related:**
[React Testing: RTL Philosophy, Mocking, and E2E with Playwright](../syllabus/21-frontend-web/react-testing.md)
