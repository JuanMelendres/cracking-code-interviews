---
title: "Cheat Sheet: React State Management Landscape"
slug: react-state-management
document_type: cheat-sheet
domain: frontend
topic_id: F-120
tier: Advanced
canonical: ../handbook/frontend/react-state-management.md
last_updated: 2026-09-03
---

# React State Management Landscape

**Canonical chapter:** [`syllabus/21-frontend-web/react-state-management.md`](../syllabus/21-frontend-web/react-state-management.md)

## Core Mental Model

There are two fundamentally different kinds of state, and conflating them is the single most common state-management mistake: CLIENT state (owned by the browser session — UI toggles, form input, a shopping cart) and SERVER state (owned by a remote source, merely cached/mirrored on the client — a user profile, a product list). Context, Redux Toolkit, and Zustand are all client-state tools, differing mainly in HOW GRANULARLY a component subscribes to change. TanStack Query is a server-state tool solving an entirely different problem (staleness, caching, deduplication) that none of the client-state tools address at all.

## Essential Definitions

- **React Context** — passes a value down a tree without prop drilling; solves a PLUMBING problem, not a subscription-granularity one. Every `useContext` consumer re-renders whenever the Provider's value changes, regardless of which part it reads.
- **Redux Toolkit** — centralized store, action-based updates, SELECTOR-based subscriptions (`useSelector`); a component only re-renders when its own selected value changes.
- **Zustand** — the same selector-based subscription guarantee as Redux, without the ceremony (no action types, no reducers-as-formal-concept, no `Provider`).
- **TanStack Query** — manages SERVER state: caching, deduplicating, and background-refreshing data from a remote source; a problem none of the three client-state tools were designed to handle.

## Decision Table

| Question | Answer |
|---|---|
| State owned by a remote server, even if cached locally? | TanStack Query — do not hand-manage as Redux/Zustand/Context state |
| State genuinely local to one component or a small co-located subtree? | `useState`/`useReducer` — no global tool needed |
| Shared client state that changes rarely or has few consumers? | Context is fine |
| Shared client state that changes frequently or has many consumers, where Context's whole-value re-render is a MEASURED problem? | Redux Toolkit or Zustand |
| Between Redux Toolkit and Zustand? | Redux Toolkit for DevTools/middleware/large-team conventions; Zustand for the same guarantee with far less setup |

**Tool comparison:**

| Concern | Context | Redux Toolkit | Zustand | TanStack Query |
|---|---|---|---|---|
| Problem solved | Prop-drilling avoidance | Centralized client state, strong tooling | Minimal client state, same selectivity | Server state (caching, staleness, dedup) |
| Re-render granularity | None — whole-provider-value | Per-selector | Per-selector | Per-query-key (request dedup, not render granularity) |
| Setup cost | Very low | Moderate | Low | Low-moderate |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- Context: bundling `count` and `name` in one context value, a `count`-only update moved BOTH consumers' render counters from 2 to 4 — the `name` consumer, which never reads `count`, re-rendered anyway.
- Redux Toolkit / Zustand: the identical two-slice setup with selector-based subscriptions — after the same "+1 count" click, the count consumer moved 2→4 but the name consumer stayed at exactly 2, in both libraries.
- TanStack Query: two independently mounted `TodoViewer` components both calling `useQuery({ queryKey: ['todo', 1], ... })` produced exactly ONE `GET /api/todos/1` network request, confirmed via a real network trace.

## Common Pitfalls

- Reaching for Context by default for ALL shared state, including frequently-changing values, without measuring whether whole-provider-value re-render is actually a problem at the current scale.
- Modeling server-owned data as hand-managed Redux/Zustand/Context state, reimplementing caching/deduplication/staleness logic TanStack Query already solves.
- Choosing Redux Toolkit reflexively ("that's just what we use") where Zustand provides the identical re-render guarantee with a fraction of the setup.
- One giant Context bundling every piece of shared app state "for simplicity" — guarantees any update anywhere re-renders every consumer of any part of it.
- Hand-rolling `useEffect` + `useState` fetch-and-cache logic repeatedly for the same server data across sibling components.

## Interview Answer Skeleton

**30-sec:** Client state (Context, Redux Toolkit, Zustand) and server state (TanStack Query) solve different problems. Context has no selective subscription — every consumer re-renders on any provider value change. Redux Toolkit and Zustand both provide selector-based subscriptions — only the consumer of the changed slice re-renders. TanStack Query solves caching/deduplication/staleness for server-owned data, which none of the client-state tools address.

**2-min:** Cite the real Context evidence (bundled `count`/`name`, a count-only update doubled BOTH consumers' render counts). Cite the real Redux/Zustand evidence (identical setup, selector-based, unrelated consumer's count stayed unchanged). Note Zustand's dramatically smaller setup for the same guarantee. Close with TanStack Query's real network trace: one request served two independent consumers of the same query key.

**Whiteboard:** A Provider box with two consumer arrows ("reads count," "reads name"). A lightning bolt hits only "count," but BOTH consumer boxes flash — annotate with the real numbers (2→4, both). Beside it: a selector-based store version — same bolt on "count," only that consumer flashes (2→4 vs. staying at 2). Separately: two components both labeled `queryKey: ['todo', 1]` pointing into ONE shared cache-entry box, one arrow out to "1 real network request."

**Senior-level framing:** Distinguishes client state from server state unprompted, and explains Context's re-render behavior via its actual mechanism (whole-value subscription, no per-field tracking) rather than a vague "it's slower" claim.

## Production Warning Signs

- A real-time dashboard bundling a fast-updating value (a price ticker) with rarely-changing values (a watchlist, a theme toggle) into one Context — the entire dashboard re-renders in lockstep with the ticker, including unrelated components; fix by moving only the fast-updating piece to its own selector-based store.
- Two sibling components independently fetching the identical server data via `useEffect`/`useState` — no deduplication, no shared cache, no consistent staleness strategy; replace with a server-state library keyed by the same query key.
- Recommending "switch everything to Redux" in response to a Context performance complaint without first checking whether the actual problem is bundling unrelated state into one context value (splitting into narrower contexts may be sufficient on its own).

## Related

- `syllabus/21-frontend-web/react-typescript.md`
- `syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`
- `syllabus/21-frontend-web/react-performance.md`
