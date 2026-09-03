---
title: "Cheat Sheet: React Hooks (useEffect and useRef)"
slug: react-hooks-useeffect-and-useref
document_type: cheat-sheet
domain: frontend
topic_id: F-105/F-106
tier: Intermediate
canonical: ../handbook/frontend/react-hooks-useeffect-and-useref.md
last_updated: 2026-09-03
---

# React Hooks (useEffect and useRef)

**Canonical chapter:** [`syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md`](../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md)

## Core Mental Model

An effect is React's answer to "run this after the DOM has updated, and clean up before it updates again or the component goes away" — the dependency array is the entire mechanism by which React decides whether "again" has happened, not a performance optimization. A ref is a plain mutable box that lives outside React's render machinery entirely — reading or writing `.current` never schedules anything, which is why it's the tool for values that must persist without participating in "what should the UI show."

## Essential Definitions

- **`useEffect`** — registers a function to run after the browser paints the current render; deps compared via `Object.is`; unchanged deps skip both the previous cleanup and the new effect body.
- **Cleanup function** — the function an effect returns; runs before the next effect run or on unmount. Required for any effect that opens/subscribes/sets something — no exceptions.
- **Stale closure** — a callback created inside an effect (e.g. inside `setInterval`) permanently closes over whatever the state equaled at that single execution, not later renders.
- **`useRef`** — returns `{ current: value }`, the *same object identity* every render; mutating `.current` is invisible to React's re-render decision.
- **`<StrictMode>` (dev only)** — double-invokes effect mount/cleanup cycles AND double-invokes render function bodies, specifically to surface missing cleanup and impure renders immediately. Neither happens in production builds.

## Decision Table

| Question | Answer |
|---|---|
| Does the effect create a subscription, timer, connection, or listener? | Needs a cleanup function — not optional |
| Does a callback that outlives one render (interval/timeout/listener) need the latest state? | Include it as a dependency (effect re-runs) OR read it via a ref kept current every render |
| Need to store a value that changes but shouldn't cause a re-render? | `useRef` |
| Need to store a value the UI must display? | `useState`, not a ref |

**Effect/ref trade-offs:**

| Concern | Effect w/ cleanup | Effect w/o cleanup | Ref value | State value |
|---|---|---|---|---|
| Subscriptions/timers | Correct, released | Leaks every remount | N/A | N/A |
| Triggers re-render | N/A | N/A | Never | Always |
| Visible in UI immediately | N/A | N/A | Only after another render | Immediately |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- Missing-cleanup leak: 2 toggle-on clicks produced 4 real leaked intervals (StrictMode double-mounts each: 2 clicks × 2 mounts).
- Stale closure: after 3 real clicks (count=2), the buggy closure-based logger read `[0,0,0,0,0]`; the ref-based logger read `[2,2,2,2,2]`.
- Ref mutation causing zero re-renders: 4 ref-increment clicks left a displayed render count unchanged; a later real state update revealed `4` was faithfully accumulated.

## Common Pitfalls

- Treating the dependency array as a performance hint rather than the mechanism controlling correctness — a missing dependency silently uses stale values, not just "re-runs less often."
- Forgetting a cleanup function for anything that creates a resource — a real, measured leak, not theoretical.
- Assuming a value read inside an interval/timeout/listener callback created in an effect will "see" later state updates — it won't unless kept fresh via a ref or the effect re-runs.
- Expecting the UI to update after mutating a ref — it won't, by design.
- Suppressing `exhaustive-deps` lint warnings instead of understanding why they fire.

## Interview Answer Skeleton

**30-sec:** `useEffect` runs side effects after commit; its dependency array (compared via `Object.is`) controls exactly when it re-runs; anything it creates needs a cleanup function or it leaks. `useRef` gives a mutable value that survives renders without ever causing one — used for DOM handles and non-rendering values.

**2-min:** Explain the dependency array as the real re-run mechanism, walk through the measured leak (missing `clearInterval`), mention stale closures as empty-deps-plus-outliving-callback, and land on `useRef`'s two uses (DOM access, mutable non-rendering value) via the one rule: mutating `.current` never schedules a render.

**Whiteboard:** Timeline "render → commit → paint → effect runs"; a second effect run further along with an arrow labeled "cleanup" pointing back — "cleanup always runs before the next effect, or on unmount." Separately: "ref.current += 1" → arrow to nowhere ("no render scheduled") vs. "setState(x)" → loop back to "render."

**Senior-level framing:** States the dependency-array mechanism and cleanup requirement unprompted; explains WHY a closure goes stale (creation-time snapshot), not just that it does.

## Production Warning Signs

- A WebSocket/subscription feature reported as "stops updating after a while" that turns out to be an accumulating leak from a `useEffect([])` with no `.close()` cleanup — often misdiagnosed as a backend issue first.
- "Maximum update depth exceeded" — check for a dependency-less `useEffect` calling `setState` on every run.
- A value inside a `setInterval`/`setTimeout` created in an effect that never updates — stale closure, not a state bug.

## Related

- `syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`
- `syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`
- `00-project/frontend-topic-register.md`
