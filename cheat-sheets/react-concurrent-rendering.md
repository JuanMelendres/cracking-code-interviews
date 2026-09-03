---
title: "Cheat Sheet: Concurrent React (Transitions, Deferred Values, Suspense)"
slug: react-concurrent-rendering
document_type: cheat-sheet
domain: frontend
topic_id: F-113
tier: Advanced
canonical: ../handbook/frontend/react-concurrent-rendering.md
last_updated: 2026-09-03
---

# Concurrent React (Transitions, Deferred Values, Suspense)

**Canonical chapter:** [`syllabus/21-frontend-web/react-concurrent-rendering.md`](../syllabus/21-frontend-web/react-concurrent-rendering.md)

## Core Mental Model

Concurrent rendering does not make React's rendering multi-threaded — it makes rendering *interruptible*, so React can start preparing a low-priority update, pause partway through if something more urgent (a keystroke, a click) comes in, handle the urgent thing first, then resume or restart the paused work. This is only possible because of Fiber's linked-list unit-of-work structure. Every concurrent-feature hook is a different way of telling React "this update is allowed to be interrupted, deprioritized, or shown as still-loading" — the diffing heuristic and commit-phase guarantees are unchanged underneath.

## Essential Definitions

- **Concurrent rendering** — React 18+'s render phase is no longer guaranteed to run synchronously to completion; React can pause, abandon, or reprioritize in-progress work.
- **`useTransition`** — marks a `setState` call as low-priority/interruptible, exposing an `isPending` flag.
- **`useDeferredValue`** — gives a "lagging" version of any value (not a setter) during expensive re-renders — for values you don't control the update source of (e.g., a prop).
- **Suspense + `use()`** — a component declares "not ready yet" (a pending promise); the nearest `<Suspense fallback>` renders in its place while the rest of the tree is unaffected.

## Decision Table

| Question | Answer |
|---|---|
| You own the specific `setState` call for the expensive update, want an explicit pending flag? | `useTransition` |
| You only have a value (a prop, something derived) feeding an expensive render? | `useDeferredValue` |
| Waiting for genuinely asynchronous work (a fetch, a dynamic import), not just an expensive synchronous computation? | `Suspense` + `use()` — transitions don't make you wait for a promise |
| About to wrap a broad `<Suspense>` boundary around fast-ready and slow-loading children together? | Reconsider placement — one slow child hides everything else in that boundary |

**Tool comparison:**

| Concern | `useTransition` | `useDeferredValue` | Suspense + `use()` |
|---|---|---|---|
| What you control | The setter call | Any value, including props you don't own | Whether a component "isn't ready" |
| Pending signal | Explicit `isPending` | Indirect (`value !== deferredValue`) | The nearest fallback renders |
| Best fit | You own the expensive state update | You only have the value | Data fetching, code-splitting |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- `useTransition`: filtering 20,000 items, a real logged `isPending` history showed `true → false` while the input itself never lagged (its own update was never wrapped in the transition).
- `useDeferredValue`: a real `isStale` history showed `true → false`, with `query` and `deferredQuery` converging once the expensive filter caught up.
- Suspense + `use()`: a real 3-second simulated network delay — `+100ms` showed `"Loading user..."` in the DOM; `+3.2s` showed the resolved `"Loaded: User 2 (id 2)"` — a genuinely observed fallback window, not inferred from docs.

## Common Pitfalls

- Using `useTransition`/`useDeferredValue` to try to speed up an expensive computation — they change scheduling priority, not the actual cost of the work.
- Believing `useDeferredValue` requires a special setter — it doesn't; it wraps any value.
- Placing a `<Suspense>` boundary too broadly around independent pieces of UI, so one slow child hides everything else behind the fallback.
- Wrapping every `setState` call in `startTransition` "just in case," including genuinely urgent ones (like the input's own value) — reintroduces the exact lag transitions exist to solve.
- Conflating transitions/deferred values (synchronous reprioritization) with Suspense (genuinely asynchronous readiness) — related but distinct problems.

## Interview Answer Skeleton

**30-sec:** Concurrent rendering makes render work interruptible, not parallel. `useTransition` marks a specific state update low-priority with an explicit `isPending` flag; `useDeferredValue` gives the same "let it lag" behavior for a value you don't control the setter of; Suspense with `use()` lets a component declare it isn't ready and shows a fallback for just its nearest boundary.

**2-min:** Start from interruptibility built on Fiber. Walk through `useTransition`'s real demo (input updates instantly, expensive filter wrapped in `startTransition`, real `isPending` history proving no block). Contrast `useDeferredValue` for values you don't own the setter of. Close with Suspense: a component suspends on a real promise via `use()`, only the nearest boundary shows a fallback — proven with an actually observed loading window.

**Whiteboard:** Timeline with two lanes, "urgent" and "transition." Mark a keystroke on urgent with immediate commit; mark its transition-wrapped filter update getting interrupted by a second keystroke's urgent work, then resuming. Separately: a subtree with an "X" (suspended) feeding into the nearest `<Suspense>` box, whose fallback renders in its place while the rest of the page renders normally.

**Senior-level framing:** Correctly distinguishes "interruptible, not parallel"; picks `useTransition` vs. `useDeferredValue` based on whether they own the setter; states precisely what a Suspense boundary does to the surrounding tree.

## Production Warning Signs

- A search-as-you-type input feels "sticky" on lower-end devices even though filtering is purely client-side (not a network issue) — profile for a synchronous render block covering both the input echo and an expensive results-grid re-filter; fix by wrapping the grid update in `startTransition`.
- A team treats `useTransition`/`useDeferredValue` as a performance fix and ships a still-slow computation that merely "feels" less broken — they redistribute *when* work happens, not its cost.
- An entire page wrapped in one top-level `<Suspense>` boundary — a single slow feature nested inside degrades UX for every unrelated team's content in that boundary.

## Related

- `syllabus/21-frontend-web/react-reconciliation-and-fiber.md`
- `syllabus/21-frontend-web/react-performance.md`
- `syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md`
