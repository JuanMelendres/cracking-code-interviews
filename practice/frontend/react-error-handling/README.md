# React Error Handling demo app (F-115)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-error-boundaries.md`](../../../syllabus/21-frontend-web/react-error-boundaries.md). Includes a real, from-scratch class-component `ErrorBoundary` (no library) — error boundaries have no hook equivalent as of React 19.

## Run it

```bash
npm install
npm run dev
```

Three sections:

1. **Boundary catch + reset** — a real render-phase crash, a real fallback, a real recovery via key-remount.
2. **Granular vs. shared boundaries** — a real, side-by-side blast-radius contrast: one boundary per widget vs. one shared boundary for three.
3. **Event-handler errors** — real, observed proof that error boundaries do NOT catch errors thrown from event handlers, contrasted with a manually try/catch-handled version.

## Captured evidence (real browser session)

### Boundary catch + reset
```
Clicked "Increment" three times (count 0 -> 1 -> 2 -> 3):
  At count 3, CrashingCounter throws during render.
  Fallback shown: "Something went wrong: Count exceeded safe threshold (reached 3)"
  last error caught by boundary: Count exceeded safe threshold (reached 3)

Clicked "Reset":
  count: 0 (fresh CrashingCounter instance, key-remounted)
  Increment button back, fallback gone
```

### Granular vs. shared boundaries
```
Clicked "Crash Widget A (granular)":
  Widget crashed: A crashed / Reset this widget
  B: OK   <- untouched
  C: OK   <- untouched

Clicked "Crash Widget A (shared)":
  Entire row crashed: A crashed / Reset row
  (B and C are no longer rendered at all — the whole shared boundary's
  subtree was replaced by the single fallback)
```
Same crash, two different real blast radii — direct, measured proof of why boundary granularity is a real architectural decision, not a stylistic one.

### Event-handler errors
```
Clicked "Throw in event handler (uncaught by boundary)":
  Boundary fallback: never shown
  "This paragraph is still here — the boundary never triggered." <- still rendered
  caught by window 'error' listener (NOT the boundary): Uncaught Error: Thrown from onClick, not from render

Clicked "Throw in event handler (handled manually)":
  Same error, but caught locally with try/catch
```
Real, observed proof that the boundary's `componentDidCatch` was never invoked for this error — only a global `window` `error` listener caught it, confirming it escaped React's error-boundary mechanism entirely.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed the exact expected dev-mode console entries (React logs every error it hands to a boundary, even when successfully caught and handled — these are expected, not bugs) and no unexpected errors.
- `npm run build` — clean production build, zero errors/warnings.
