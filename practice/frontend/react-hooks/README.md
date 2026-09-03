# React Hooks demo app (F-105 useEffect, F-106 useRef)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md`](../../../syllabus/21-frontend-web/react-hooks-useeffect-and-useref.md).

## Run it

```bash
npm install
npm run dev
```

Five sections:

1. **F-105a Dependency array** — an effect that runs only when `count` changes, not when an unrelated state re-renders the component.
2. **F-105b Cleanup functions** — a real, measurable `setInterval` leak (module-level ground-truth counter) contrasting a leaky ticker (no cleanup) against a clean one.
3. **F-105c Stale closures** — a buggy logger (empty deps, closes over mount-time state) next to a fixed one (reads via a ref).
4. **F-106a DOM access** — `useRef` to imperatively focus an input.
5. **F-106b Mutable ref value** — clicking increments a ref repeatedly with zero re-renders; a separate button reveals the real accumulated value.

## Captured evidence (real browser session, not hypothetical)

### F-105a — dependency filtering
Clicked "Increment unrelated" twice, then "Increment count" once. Result: `count: 1`, "unrelated (2)", and the effect log grew by exactly **one** new entry (`effect ran, count=1`) — not two, confirming `unrelated`'s changes never triggered the effect.

### F-105b — the leak, with an unplanned extra finding
Toggled the leaky ticker on/off/on (net: shown), the clean ticker on/off/on/off (net: hidden), then clicked "Refresh counter" without touching anything else:

```
Real active interval count (module-level, ground truth): 4
Leaky ticker: <climbing>
(clean ticker: unmounted, contributes 0)
```

**Real finding, not designed in advance:** 2 "Show leaky ticker" clicks produced **4** leaked intervals, not 2. This is React 19's `<StrictMode>` (used by this app, like the Vite template default) intentionally double-invoking effects in development — mount → cleanup → mount again — specifically to surface exactly this class of missing-cleanup bug fast. Since `LeakyTicker`'s effect returns no cleanup function, each of the two synthetic StrictMode remounts leaves its own orphaned `setInterval` running: 2 clicks × 2 StrictMode-driven mounts = 4 real, live intervals. `CleanTicker`'s effect *does* return a cleanup, so its own StrictMode double-mount nets to zero leftover every time — confirmed by the ground-truth counter never crediting it with a residual interval.

### F-105c — stale closure, captured over 3 real increments
```
Increment count (currently 2)
Buggy (stale) logger:      [0, 0, 0, 0, 0]
Fixed (ref-based) logger:  [2, 2, 2, 2, 2]
```
The buggy logger's `setInterval` callback was created once, at mount, closing over `count=0` forever — three real clicks on "Increment count" never changed a single value it logs. The fixed logger reads through a ref kept current every render, so it reflects the real live value.

### F-106a — real DOM focus
After clicking "Focus the input", `document.activeElement` was verified (via direct JS inspection) to be the actual `<input data-testid="ref-target-input">` DOM node — a genuine `.focus()` call through the ref, not a simulated result.

### F-106b — ref mutation vs. render, and a second StrictMode finding
Clicked "Increment ref" 4 times: `render-count` stayed at its prior value throughout (verified via DOM inspection after each click), proving ref mutation triggers no re-render. Clicking "Reveal ref's real current value" once afterward:
```
renderCount: "4"        <- jumped from 2 to 4 after a SINGLE state update
revealedValue: "Last revealed ref value: 4"   <- matches all 4 real clicks
```
The render count jumping by 2 (not 1) per actual committed render is StrictMode again: React 19 intentionally invokes a component's render *function body* twice per commit in development (only one result is actually committed to the DOM) specifically to help surface impure renders. `renderCountRef.current += 1` sits directly in the render body, so it counts both invocations — a real, verified, secondary StrictMode effect, distinct from the effect-double-invocation in F-105b.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors (an earlier RefMutableValueDemo draft caused a real "Maximum update depth exceeded" infinite loop from a dependency-less `useEffect` calling `setState` — caught live, fixed by moving the render counter into the render body via a ref instead of an effect).
- Every claim above verified via direct DOM/JS inspection (`document.activeElement`, `input.value`, `textContent`), not just visual reading.
- `npm run build` — clean production build, zero errors/warnings (565ms, 3 output files).
