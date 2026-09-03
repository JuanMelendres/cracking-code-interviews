---
title: "Cheat Sheet: React useReducer and Custom Hooks"
slug: react-usereducer-and-custom-hooks
document_type: cheat-sheet
domain: frontend
topic_id: F-109/F-110
tier: Intermediate
canonical: ../handbook/frontend/react-usereducer-and-custom-hooks.md
last_updated: 2026-09-03
---

# React useReducer and Custom Hooks

**Canonical chapter:** [`syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md`](../syllabus/21-frontend-web/react-usereducer-and-custom-hooks.md)

## Core Mental Model

`useReducer` exists for one structural reason: it gives you one place — the reducer function — where the ENTIRE next state is computed from the ENTIRE previous state and one action, atomically. Several independent `useState` calls give you several independent places, each seeing only its own previous value — exactly where bugs creep in when two pieces of state need to agree with each other. A custom hook is just a plain function that calls other hooks; it inherits its state-per-instance behavior directly from whatever it's built on.

## Essential Definitions

- **`useReducer(reducer, initialState)`** — returns `[state, dispatch]`; `dispatch(action)` triggers `reducer(currentState, action)` to compute the next state.
- **Custom hook** — any function whose name starts with `use` and which itself calls one or more hooks; exists purely for reuse of *stateful* logic (a pure computation needs no `use` prefix or hook treatment).
- **`useState` internally** — a special case of a reducer (a "replace" reducer) in React's own source — the transition-logic difference is where it lives (one function vs. scattered handlers), not a different mechanism.
- **Rules of Hooks apply transitively** — a custom hook's internal `useState`/`useEffect` calls are bookkept by React exactly as if called directly in the component, at that position in the call sequence.

## Decision Table

| Question | Answer |
|---|---|
| State fields genuinely independent (updating one never needs another's current value)? | `useState`, one per field — simpler |
| Does an update need to read another field's current/just-changing value to compute correctly? | `useReducer` |
| Does "reset to known state" need to stay reliably complete as fields are added over time? | `useReducer` — `return initialState` is structurally complete |
| Stateful logic (not just a calculation) needed identically in 2+ components? | Extract a custom hook |

**`useState` vs `useReducer`:**

| Concern | Several `useState` | `useReducer` |
|---|---|---|
| Reset completeness | Must enumerate every field manually — easy to forget a new one | `return initialState` — trivially complete |
| Cross-field consistency | Error-prone — each setter sees only its own previous value | Correct by construction |
| Testability of transitions | Scattered across handlers | One pure, unit-testable function |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- Reset bug: `useState` version left `phone: "555-0100"` behind after Reset (handler never updated for a later-added field); `useReducer` version correctly cleared everything via `return initialFormState`.
- Stale-derivation bug (deterministic, via a double-call in one handler): `useState` version showed `count: 2` but `lastAction: "incremented to 1"` (wrong — stale closure); `useReducer` version showed `count: 2, lastAction: "incremented to 2"` (correct).
- `useToggle` instance isolation: toggling Panel A produced `Panel A: OPEN, Panel B: CLOSED`.
- `useDebouncedValue`: a burst of typing produced one immediate `raw` update, a mid-flight lagging `debounced` value, then after ~1s a settled commit (counter 1→2) — one commit per burst, not per keystroke.

## Common Pitfalls

- Reaching for `useReducer` reflexively for simple, independent state — real ceremony (action types, switch) for no benefit.
- Deriving one `useState` value from another inside the same handler — both setters see independent, potentially-stale closures (this chapter's central bug).
- Assuming a functional updater (`setX(x => ...)`) fixes cross-field staleness — it only protects a setter's OWN value, never a sibling setter's pending update.
- Forgetting to update a `useState`-based reset handler when a new field is added.
- Naming a function `use...` when it calls no hooks internally (or the reverse) — breaks the Rules-of-Hooks linter's ability to verify it.
- A custom hook secretly sharing state via a module-level variable instead of `useState`, silently breaking per-instance expectations.

## Interview Answer Skeleton

**30-sec:** `useReducer` consolidates transitions into one function computing the entire next state from the entire previous state plus an action — useful when multiple `useState` calls would need each other's just-updated values, which they structurally can't see. A custom hook is a `use`-prefixed function calling other hooks, existing for reuse — each caller gets its own independent state instance.

**2-min:** Walk through the reset-bug demo (forgotten field in a `useState` reset handler vs. `useReducer`'s complete `initialState` return) and the stale-derivation demo (two setters in one handler, one reading the other's pre-update closure, made deterministic via a double-call). Then define a custom hook precisely and give the `useDebouncedValue` example reusing the `useEffect` cleanup pattern.

**Whiteboard:** Two boxes "useState A" / "useState B," each with an arrow labeled "sees only its own previous value," dotted line between them labeled "NO visibility into each other's pending update." Below: one box "reducer(state, action)" with one arrow in (full previous state + action), one arrow out (full next state) — "one function, one atomic computation."

**Senior-level framing:** Correctly rejects "just use functional updates" as a fix for cross-setter staleness and names the specific structural property (atomic whole-state computation) that `useReducer` provides instead.

## Production Warning Signs

- A checkout/form total that's occasionally one step behind after a rapid multi-field update (e.g., applying a promo code) — likely two `useState` setters in one handler reading each other's stale closures; consolidate into one `useReducer`.
- A reset button that leaves one field un-cleared after a form grows a new field — the `useState` reset handler almost certainly wasn't updated to match.
- A custom hook with a missing cleanup for something it subscribes to/schedules — the leak is one layer removed from the component using it, making it easier to miss.

## Related

- `syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md`
- `syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`
- `syllabus/21-frontend-web/react-component-patterns.md`
