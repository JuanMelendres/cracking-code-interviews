---
title: "Cheat Sheet: React Performance (Profiling, Memoization, Virtualization, Code-Splitting)"
slug: react-performance
document_type: cheat-sheet
domain: frontend
topic_id: F-117
tier: Advanced
canonical: ../handbook/frontend/react-performance.md
last_updated: 2026-09-03
---

# React Performance (Profiling, Memoization, Virtualization, Code-Splitting)

**Canonical chapter:** [`syllabus/21-frontend-web/react-performance.md`](../syllabus/21-frontend-web/react-performance.md)

## Core Mental Model

Every technique here attacks a different cost: memoization reduces WORK PER RENDER by skipping unnecessary re-computation or re-rendering; virtualization reduces the NUMBER OF DOM NODES that exist at all for a large list; code-splitting reduces the amount of JAVASCRIPT DOWNLOADED AND PARSED before the user can interact with a given part of the page. They are not interchangeable and not a checklist — each is a response to a SPECIFIC, MEASURED cost. Applying one where a different cost is the actual bottleneck (or applying any without ever measuring) is itself the common mistake.

## Essential Definitions

- **Memoization** (`React.memo`, `useMemo`, `useCallback`) — reuses a previous render's output/value/reference when inputs haven't meaningfully changed; only works when the comparison (a shallow reference check) actually reflects "did anything relevant change."
- **Virtualization** — renders only the visible subset of a large list (plus overscan), tracking scroll position to swap which subset renders; DOM nodes are expensive regardless of whether React re-renders them.
- **Code-splitting** (`React.lazy` + dynamic `import()`) — breaks one bundle into chunks loaded on demand, so initial load isn't delayed by code the current session doesn't need.

## Decision Table

| Question | Answer |
|---|---|
| Have you actually measured the problem (profiler, render counts, DOM counts, bundle size)? | Measure first — none of these techniques are free |
| Cost is "this component re-renders/recomputes more than necessary"? | Memoization — but verify prop/dependency reference stability |
| Cost is "this list has so many DOM nodes that scroll/layout/memory is the bottleneck"? | Virtualization — confirm with an actual DOM node count |
| Cost is "user waits too long for JS before a feature they may not use is ready"? | Code-splitting that feature/route — confirm with build output AND a network trace |

**Tool comparison:**

| Concern | Memoization | Virtualization | Code-splitting |
|---|---|---|---|
| What it reduces | Work per render | DOM node count | JS downloaded/parsed |
| Silent-failure risk | High — depends entirely on stable references | Low — visibly broken if wrong | Low — chunk either loads or doesn't |
| Best fit | Expensive renders with stable, meaningful inputs | Lists large enough that node count is the bottleneck (hundreds+) | Routes/features not needed on initial load |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- `React.memo` silent failure: an inline object prop caused a memoized child's render count to climb 2→4→...→10 across repeated unrelated parent updates; a `useMemo`-stabilized version of the identical prop stayed frozen at 2.
- Virtualization: a naive 5,000-item list mounted exactly 5,000 DOM row nodes; a windowed implementation mounted exactly 15. Scrolling to `scrollTop=2000` kept the count at 15 but shifted the rendered rows to 68–82, confirming a genuine moving window, not a fixed cap.
- Code-splitting: `npm run build` produced a separate chunk file (`HeavyPanel-6npn3ADo.js`); a live network trace showed zero request for it before a button click and a real `GET ... 200 OK` immediately after.

## Common Pitfalls

- Adding `React.memo` without verifying the props passed actually have stable references — the single most common way memoization silently does nothing.
- Applying performance techniques reflexively/preemptively without measuring whether the targeted cost is real in the specific case.
- Assuming a `React.lazy` component is "code-split" without confirming, in real build output, that it produced a separate chunk.
- Wrapping every component in `React.memo` "just in case," adding a real comparison cost for components whose props were never a source of expensive re-renders.
- Building custom virtualization for a list that's realistically only tens of items long — the complexity isn't paying for itself below a meaningful threshold.

## Interview Answer Skeleton

**30-sec:** Memoization skips unnecessary work but only works with stable references — an inline object/array/function prop defeats `React.memo`'s shallow comparison every time. Virtualization renders only the visible window of a large list, reducing DOM node count directly. Code-splitting via `React.lazy` defers loading a feature's JS until needed, verifiable in build output and network requests. All three should be applied in response to a measured cost, not speculatively.

**2-min:** State the three-distinct-costs mental model. Cite the real memoization evidence (render count 2→10 with an unstable prop vs. frozen at 2 with `useMemo`). Cover virtualization's real DOM-node-count proof (5,000 vs. 15, window confirmed to track scroll). Close with code-splitting's real chunk, confirmed both in build output and a live network trace.

**Whiteboard:** Three boxes — "Memoization," "Virtualization," "Code-splitting" — each with an arrow to a different cost: "re-render work," "DOM node count," "JS download/parse time." Under Memoization: an object literal `{}` with "new reference every render" pointing at a comparison that always says "changed"; a `useMemo` version with "same reference" pointing at "unchanged."

**Senior-level framing:** Explains precisely WHY `React.memo` can fail (reference instability) and proposes a concrete verification method (a render counter) rather than trusting the presence of `memo()` in the code.

## Production Warning Signs

- A team adds `React.memo` to a dozen list-item components in response to a vague "feels slow" complaint, and the complaint persists — check for a fresh inline handler (e.g., `onClick={() => handleClick(item.id)}`) being passed as a prop, defeating the memoization silently.
- A component "is code-split" per the source but the network trace shows its chunk requested immediately on page load — the lazy component is rendering unconditionally on mount, providing zero loading-deferral benefit despite being split at build time.
- Reaching for virtualization before considering whether pagination or server-side filtering would eliminate the oversized data set entirely.

## Related

- `syllabus/21-frontend-web/react-accessibility.md`
- `syllabus/21-frontend-web/react-concurrent-rendering.md`
- `syllabus/21-frontend-web/react-reconciliation-and-fiber.md`
