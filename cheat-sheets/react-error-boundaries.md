---
title: "Cheat Sheet: React Error Boundaries and Error Handling Strategy"
slug: react-error-boundaries
document_type: cheat-sheet
domain: frontend
topic_id: F-115
tier: Intermediate
canonical: ../handbook/frontend/react-error-boundaries.md
last_updated: 2026-09-03
---

# React Error Boundaries and Error Handling Strategy

**Canonical chapter:** [`handbook/frontend/react-error-boundaries.md`](../handbook/frontend/react-error-boundaries.md)

## Core Mental Model

An error boundary is a component that wraps a subtree and, if any component within it throws during rendering, replaces the entire subtree with a fallback UI instead of letting the error propagate up and unmount the whole app. It is scoped exactly to the render phase — the only place React is actively executing your component functions and can catch a thrown exception. Anything outside a render call (an event handler running later, a fetch callback resolving later) is just JavaScript React isn't currently "inside," so a boundary has no opportunity to intercept it. This one fact explains every "does it catch X?" question in this topic.

## Essential Definitions

- **Error boundary** — a class component implementing `static getDerivedStateFromError(error)` and/or `componentDidCatch(error, info)`. No hook equivalent exists as of React 19 — a genuine, persistent exception to "hooks can do everything classes can."
- **Blast radius** — the ENTIRE subtree under a boundary is replaced by the fallback when anything in it crashes, not just the failing component.
- **What's caught** — render-phase errors, lifecycle methods, constructors below the boundary.
- **What's NOT caught** — event handlers, async callbacks (`fetch`/`.then()`/`setTimeout`), errors in the boundary itself, SSR errors.

## Decision Table

| Question | Answer |
|---|---|
| Does your app have at least one boundary anywhere? | If not, add one near the root as a baseline safety net |
| Multiple genuinely independent sections (widgets, routed pages, embeds)? | Give each its own boundary |
| Error in an event handler, `fetch`/`then`, `setTimeout`? | No boundary catches it — use local `try`/`catch` + error state |
| Need to log caught errors to a monitoring service? | `componentDidCatch(error, info)` — receives error + `componentStack` |

**Granularity trade-off:**

| Concern | No boundaries | One shared (top-level) | Many granular |
|---|---|---|---|
| Blast radius | Entire app unmounts | Entire subtree under it replaced | Only the crashed section |
| Boilerplate | None | Minimal | One per independently-failable section |
| Best fit | Never | Small apps / last-resort net | Any app with independent sections |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- Real catch/reset: incrementing a counter to 3 triggered a genuine throw; boundary fallback rendered the exact error message; clicking "Reset" (bumping the boundary's `key`, forcing a remount) produced a fresh instance at `count: 0`.
- Granularity contrast: crashing Widget A under per-widget boundaries left `B: OK` and `C: OK` fully intact; crashing Widget A under one shared boundary replaced the ENTIRE row — B and C were not rendered at all.
- Event-handler proof: a `throw` inside `onClick` never triggered the boundary's fallback; a sibling paragraph stayed mounted; a global `window` `'error'` listener caught it instead, confirming it escaped React's boundary mechanism entirely.

## Common Pitfalls

- Assuming a single top-level boundary is "handling errors" for the whole app — its blast radius is nearly as large as having none.
- Assuming a boundary will catch a `throw` inside an event handler it wraps — it will not, proven directly.
- Writing a boundary as a function component with `try`/`catch` around JSX — doesn't work; only class lifecycle methods can intercept React's own internal call to a child during rendering.
- Assuming a parent boundary will catch a rejected promise or an error inside `.then()` — async errors happen outside the render phase entirely.

## Interview Answer Skeleton

**30-sec:** Error boundaries are class components (`getDerivedStateFromError`/`componentDidCatch`, no hook equivalent) that catch render-phase errors in their subtree and show a fallback instead of unmounting the whole app. They do NOT catch event-handler, async, or their-own errors — those need local `try`/`catch`. Boundary granularity is a real, measurable blast-radius trade-off.

**2-min:** State the render-phase-only mental model. Walk through the real catch-and-reset demo (throw → fallback → key-remount reset → genuine recovery). Cover the granularity trade-off with the real measured contrast (shared boundary takes down unrelated siblings; granular contains it). Close with the event-handler gotcha proven by a global error listener catching what the boundary never saw.

**Whiteboard:** A tree with a boundary node near the root and a crashed leaf several levels below, arrow going UP to the boundary labeled "React walks up looking for the nearest ancestor with getDerivedStateFromError." Then the ENTIRE subtree under the boundary (not just the crashed leaf) replaced by one "fallback" box — blast radius is the whole subtree.

**Senior-level framing:** States precisely what boundaries catch and don't catch unprompted, and can produce the correct local `try`/`catch` pattern for what they can't reach.

## Production Warning Signs

- One broken third-party widget (chart/map/embed) blanks an entire dashboard — zero boundaries means the "nearest ancestor" search finds nothing and the whole tree unmounts; fix by wrapping each independent widget in its own boundary.
- A team believes it has "good error handling" with only a single top-level boundary, unaware of its near-total blast radius.
- Uncaught async/event-handler errors surfacing only as unhandled browser exceptions with no user-visible feedback — needs local `try`/`catch` + error state, not a boundary.

## Related

- `handbook/frontend/react-reconciliation-and-fiber.md`
- `handbook/frontend/react-forms.md`
- `handbook/frontend/react-accessibility.md`
