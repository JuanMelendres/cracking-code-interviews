# Concurrent React demo app (F-113)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-concurrent-rendering.md`](../../../syllabus/21-frontend-web/react-concurrent-rendering.md).

## Run it

```bash
npm install
npm run dev
```

Three sections:

1. **`useTransition`** — marking a state update as low-priority so an urgent update (the input echoing keystrokes) is never blocked by it. A pending-flip log proves `isPending` really goes `true` then `false` around the deferred update.
2. **`useDeferredValue`** — the same "let something lag behind" idea without an explicit `startTransition` call: the value itself lags, exposed via `query !== deferredQuery`.
3. **Suspense + `use()`** — a component that suspends on a real (simulated 3s network) promise, rendering the nearest `<Suspense>` fallback until it resolves.

## Captured evidence (real browser session)

### useTransition
Typed `"item-5"` into the transition-search input. Real result:
```
pending log: pending: true -> pending: false
results: 20   (filtered list updated after the transition committed)
```
The `isPending` flag's own history — not just the final list — proves React ran the list update as an interruptible, lower-priority commit while the input value updated immediately.

### useDeferredValue
Typed `"entry-5"` into the deferred-search input. Real result:
```
stale log: stale: true -> stale: false
query: entry-5
deferredQuery: entry-5   (caught up after the expensive filter finished)
```

### Suspense + use()
Clicked "Load user" and polled the DOM from inside a single `javascript_exec` call (to avoid round-trip latency between separate tool calls hiding the fallback window):
```
immediate (+100ms): "Loading user..."
after (+3.2s):       "Loaded: User 2 (id 2)"
```
The fallback text is real, observed DOM content during the pending promise — not inferred.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors throughout, including after every interaction.
- `npm run build` — clean production build, zero errors/warnings.
- Suspense fallback window confirmed via a single async `javascript_exec` poll (separate sequential tool calls were too slow relative to the original 800ms delay to reliably observe the fallback, so the simulated fetch delay was raised to 3000ms and the click+poll were combined into one execution context).
