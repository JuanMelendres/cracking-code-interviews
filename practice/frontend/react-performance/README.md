# React Performance demo app (F-117)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-performance.md`](../../../syllabus/21-frontend-web/react-performance.md).

## Run it

```bash
npm install
npm run dev
```

Three sections:

1. **Virtualization** — a real, from-scratch windowed list (no library) next to a naive full-render list, both over the same 5,000 items.
2. **Memoization strategy** — a real, reproducible case where `React.memo` alone silently fails to prevent re-renders, contrasted with the `useMemo`-stabilized fix.
3. **Code-splitting** — a real `React.lazy()`-loaded component, verified against real build output and real network requests.

## Captured evidence (real browser session)

### Virtualization
```
DOM node count (querySelectorAll):
  naive:       5000 row nodes
  virtualized: 15 row nodes

Scrolled the virtualized list to scrollTop = 2000:
  still 15 row nodes, but now showing "Row 68" through "Row 82"
  (the window itself moved — not a fixed first-N cap)
```
Same 5,000 logical items in both lists; a direct DOM node count shows the real difference in what's actually mounted, and scrolling proves the visible window genuinely tracks scroll position rather than being capped.

### Memoization strategy
Across several clicks of each "Trigger unrelated parent re-render" button:
```
memo() alone, unstable object prop:  child render count kept climbing (2 -> 4 -> ... -> 10)
memo() + useMemo, stable reference:  child render count stayed frozen at 2 the entire time
```
Real, reproducible proof that `React.memo`'s shallow comparison is defeated by a new object reference on every render, and that `useMemo` fixes it by giving the prop a stable identity.

### Code-splitting with React.lazy
```
npm run build output:
  dist/assets/HeavyPanel-6npn3ADo.js    0.24 kB   <- its own separate chunk

Real network trace (dev server):
  BEFORE clicking "Load heavy panel": no request for HeavyPanel.jsx
  AFTER clicking:                     GET /src/demos/HeavyPanel.jsx -> 200 OK
```
Confirmed both in the production build (a genuinely separate chunk file) and in a live network trace (the chunk is fetched only on demand, not at initial page load).

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors throughout, including after every interaction.
- `npm run build` — clean production build, zero errors/warnings, with `HeavyPanel` confirmed as its own chunk.
