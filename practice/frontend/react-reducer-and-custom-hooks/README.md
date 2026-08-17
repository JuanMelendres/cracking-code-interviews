# React useReducer & Custom Hooks demo app (F-109 useReducer, F-110 custom hooks)

Real Vite + React 19 app backing [`handbook/frontend/react-usereducer-and-custom-hooks.md`](../../../handbook/frontend/react-usereducer-and-custom-hooks.md).

## Run it

```bash
npm install
npm run dev
```

Four sections:

1. **F-109a Reset bug** — a `useState`-based form that forgets to reset a newly-added field, next to a `useReducer` version where reset is structurally impossible to get wrong.
2. **F-109b Stale derived state** — deriving one field from another under a deterministic double-update, `useState` vs `useReducer`.
3. **F-110a `useToggle`** — the simplest custom hook, two independent instances.
4. **F-110b `useDebouncedValue`** — a real, useful custom hook, reusing this cluster's own `useEffect` cleanup discipline.

## Captured evidence (real browser session)

### F-109a — a real reset bug, reproduced
Sequence: "Fill sample data" then "Reset" on both forms.

```
useState version after reset:  name="", email="", phone="555-0100"   <- BUG: phone leaked through
useReducer version after reset: name="", email="", phone=""          <- correctly reset
```

### F-109b — stale derived state, reproduced deterministically
Rather than relying on click-speed timing (unreliable for browser automation), the bug is made deterministic by calling the increment logic twice inside one handler — guaranteed to expose the stale closure on the very first click.

```
useState version, after "Double increment": count=2, lastAction="incremented to 1"   <- WRONG, off by one
useReducer version, after "Double increment": count=2, lastAction="incremented to 2"  <- correct
```

### F-110a — independent hook instances
Clicked Panel A's toggle once: `Panel A: OPEN`, `Panel B: CLOSED` — unaffected, confirming `useToggle`'s state lives with the calling instance, same lesson as `useState` in `react-fundamentals`.

### F-110b — debounce lag captured mid-flight, not just before/after
1. Typed "hello" via the browser tool — by the time it was checked, the 500ms window had already elapsed: `raw="hello"`, `debounced="hello"`, `commits=1`.
2. Set the input directly to "burst-test" via a native input-value setter + `input` event (to control timing precisely) and read immediately, with zero wait: `raw="burst-test"`, `debounced` **still "hello"** (genuinely lagging, mid-flight), `commits` still 1.
3. After a further 1-second wait: `raw="burst-test"`, `debounced="burst-test"` (caught up), `commits=2`.

This captured all three states of the debounce lifecycle — immediate raw update, transient lag, and eventual single settled commit — not just a before/after snapshot.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors.
- Every number above read directly from the live DOM (`textContent`), not estimated or assumed.
- `npm run build` — clean production build, zero errors/warnings.
