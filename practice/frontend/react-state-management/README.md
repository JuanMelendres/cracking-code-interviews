# React State Management Landscape demo app (F-120)

Real Vite + React 19 app backing [`handbook/frontend/react-state-management.md`](../../../handbook/frontend/react-state-management.md).

## Run it

```bash
npm install
npm run dev    # app at http://localhost:5197
npm run build
```

Four demos, each pairing a "count" consumer and a "name" consumer so the same click (change count only) can be checked against BOTH consumers' render counters:

1. **Context** (`ContextDemo.jsx`) — no selector; both consumers re-render on any provider value change.
2. **Redux Toolkit** (`ReduxDemo.jsx` + `store/reduxStore.js`) — `useSelector`-based; only the changed slice's consumer re-renders.
3. **Zustand** (`ZustandDemo.jsx` + `store/zustandStore.js`) — same selective-re-render guarantee, far less setup code.
4. **TanStack Query** (`QueryDemo.jsx`) — server state; two independent consumers of the same query key share one cached fetch.

## Captured evidence (real browser session)

### Context: changing `count` re-renders the `name` consumer too

Real render counters, before and after clicking Context's "+1 count" button once:

```
Before: Count: 0 (renders: 2) | Name: anon (renders: 2)
After:  Count: 1 (renders: 4) | Name: anon (renders: 4)
```

Both consumers went from 2 to 4 renders (StrictMode double-invokes in dev, so one real click = +2) — the `name` consumer re-rendered despite never reading `count`, because Context has no concept of "which field did this consumer actually use." Any change to the provider's value re-renders every consumer subscribed to that context.

### Redux Toolkit and Zustand: the `name` consumer is untouched

Real render counters, before and after clicking each store's "+1 count" button once:

```
Redux   — before: Count: 0 (renders: 2) | Name: anon (renders: 2)
Redux   — after:  Count: 1 (renders: 4) | Name: anon (renders: 2)   <- unchanged
Zustand — before: Count: 0 (renders: 2) | Name: anon (renders: 2)
Zustand — after:  Count: 1 (renders: 4) | Name: anon (renders: 2)   <- unchanged
```

Both libraries' selector-based subscription model (`useSelector`/`useAppStore(selector)`) only re-renders a component when ITS selected value actually changes — updating the counter slice never touches the name slice, so the `name` consumer's render count stayed exactly where it started, in both libraries. Zustand achieves the identical guarantee with a fraction of Redux Toolkit's setup code (no slices, no `Provider`, no action creators — one `create()` call).

### TanStack Query: two independent consumers, one real network request

Two `TodoViewer` components independently call `useQuery({ queryKey: ['todo', 1], queryFn: fetchTodo })`. Real network trace:

```
[65021.33] GET http://localhost:5197/api/todos/1 → 200 OK
```

Exactly ONE request, despite two independently mounted components both requesting the same query key — TanStack Query's cache deduplicates identical in-flight/cached requests across every consumer, rather than each component fetching independently. (There's no real backend here, so the dev server's SPA fallback serves `index.html`, which fails JSON parsing — both viewers correctly show an error state. That failure is expected and irrelevant to the dedup claim, which is about request COUNT, not response content.)

## Verification performed

- Live browser session: clicked each store's "+1 count" button and read every consumer's render counter via direct DOM queries (`document.querySelector(...).textContent`) before and after, for all three client-state approaches.
- `read_network_requests` confirmed exactly one `/api/todos/1` request despite two independently mounted `TodoViewer` consumers.
- `npm run build` — clean production build, zero errors/warnings.
