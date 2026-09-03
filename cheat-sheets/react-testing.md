---
title: "Cheat Sheet: React Testing (RTL Philosophy, Mocking, E2E with Playwright)"
slug: react-testing
document_type: cheat-sheet
domain: frontend
topic_id: F-118
tier: Advanced
canonical: ../handbook/frontend/react-testing.md
last_updated: 2026-09-03
---

# React Testing (RTL Philosophy, Mocking, E2E with Playwright)

**Canonical chapter:** [`handbook/frontend/react-testing.md`](../handbook/frontend/react-testing.md)

## Core Mental Model

Every testing decision answers one question: does this test verify something a USER (or a real dependency) actually cares about, or does it verify an incidental detail of the current implementation? A query by CSS class or DOM position cares about implementation; a query by role or accessible label cares about what a user perceives — the same distinction, at a different layer, as mocking a dependency's INTERFACE (call it, verify it was called correctly) rather than its internal logic. RTL's `getByRole`/`getByLabelText` and Playwright's `page.getByRole` make "test like a user" the path of least resistance, not extra discipline.

## Essential Definitions

- **React Testing Library (RTL)** — "the more your tests resemble the way your software is used, the more confidence they can give you"; deliberately makes implementation details harder to query than user-facing output.
- **Vitest** — Jest-API-compatible, Vite-native test runner; `describe`/`it`/`expect`, `jsdom` environment, `vi.fn()`/`vi.mock()`.
- **Mocking** — replaces a real dependency with a controllable stand-in to isolate the component under test; lets a test assert the interaction (call arguments), not just the return value.
- **E2E testing (Playwright)** — drives a REAL browser against a REAL running app, exercising routing, real network requests, real CSS, real browser APIs that `jsdom` never touches.

## Decision Table

| Question | Answer |
|---|---|
| Querying by something a user/screen reader perceives, or an implementation detail? | Prefer `getByRole`/`getByLabelText`/text; `getByTestId` only as last resort |
| Does the behavior depend on a real network call, timer, or external dependency? | Mock it (`vi.mock`/`vi.fn`); assert behavior AND call arguments when the interaction matters |
| Does the needed confidence only exist when multiple real components/pages/network calls interact? | E2E concern, not component-test concern |
| About to add an E2E test for something a component test could already cover? | Reconsider — E2E is for critical journeys, not exhaustive coverage |

**Component vs. E2E:**

| Concern | Component tests (Vitest + RTL) | E2E (Playwright) |
|---|---|---|
| Environment | `jsdom` — fast, no real browser/network | Real browser, real server |
| What it catches | Component logic, rendering, mocked-dependency interaction | Cross-component integration, real routing/network, browser quirks |
| Flakiness risk | Low — deterministic | Higher — real timing/network |
| Typical volume | Many (base of the pyramid) | Few, critical journeys only |

## Common Pitfalls

- Reaching for `getByTestId` or a CSS-class query as the default rather than the explicit last resort — proven costly when a class-based query broke on a purely cosmetic refactor while a role/label-based one didn't.
- Mocking a dependency and only asserting on the return value/render, never the call arguments — missing wrong-argument bugs entirely.
- Writing E2E tests for logic a component test could verify faster and more deterministically.
- Letting a test glob collide between Vitest and Playwright specs (`*.spec.js` matched by both) — needs an explicit exclude.
- Snapshot-testing entire component trees as the primary strategy — produces the same false-negative noise as implementation-detail queries with even less signal.

## Interview Answer Skeleton

**30-sec:** RTL's philosophy is "test behavior, not implementation" — query by role/label/text, not class names or DOM structure, because implementation-detail queries break on refactors that change nothing a user would notice. Mocking (`vi.mock`) isolates a component and lets you verify both outcome and interaction. E2E tests (Playwright) drive a real browser, catching integration issues isolated component tests structurally cannot see.

**2-min:** Cite the real refactor evidence: renaming `.field-wrap` to `.input-group` (plus reordering fields) broke a class-name-based test with a real `Unable to fire a "change" event` error, while a role/label-based test needed zero changes. Cover mocking: `vi.mock` replaced `fetchUser`, and the suite asserted both resulting UI state and `toHaveBeenCalledWith(42)`. Close with a real Playwright run catching a genuine `strict mode violation` (two `role="alert"` elements on the same page) that no isolated component test could have produced.

**Whiteboard:** A pyramid — many component tests at the base, few E2E at the top. Beside the base: two query styles pointing at the same `<input>` — "role/label" → "survives refactor"; "class name" → "breaks on refactor," annotated with the real captured error string. At the top: a browser icon with two components (`LoginForm`, `UserProfile`) and a shared "role=alert" collision arrow, labeled with the real Playwright error.

**Senior-level framing:** Explains WHY implementation-detail queries are risky with a concrete failure mode, not just "it's best practice," and distinguishes what a mocked component test proves from what only an E2E test can prove.

## Production Warning Signs

- A routine, purely visual refactor (icon-library swap, class rename) breaks dozens of "passing-looking" tests overnight — triage as false negatives if manual testing confirms user-facing behavior is unchanged; rewrite the offending queries to role/label.
- A team stops trusting red CI after repeated false negatives — a real organizational cost, not just a technical inconvenience.
- An E2E locator unexpectedly matches more than one element on a real composed page — scope it more tightly rather than assuming a single component's output.

## Related

- `handbook/frontend/react-forms.md`
- `handbook/frontend/react-typescript.md`
- `handbook/testing/test-strategy-and-test-doubles.md`
