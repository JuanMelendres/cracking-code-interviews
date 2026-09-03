---
title: "Cheat Sheet: React Memoization and Context (useMemo, useCallback, useContext)"
slug: react-usememo-usecallback-and-usecontext
document_type: cheat-sheet
domain: frontend
topic_id: F-107/F-108
tier: Intermediate
canonical: ../handbook/frontend/react-usememo-usecallback-and-usecontext.md
last_updated: 2026-09-03
---

# React Memoization and Context (useMemo, useCallback, useContext)

**Canonical chapter:** [`handbook/frontend/react-usememo-usecallback-and-usecontext.md`](../handbook/frontend/react-usememo-usecallback-and-usecontext.md)

## Core Mental Model

`useMemo`/`useCallback` are not performance hooks in general — they are referential-equality hooks: they make React return the SAME object/function reference across renders when nothing relevant changed, which only matters because something downstream (an expensive recomputation, or `React.memo`'s prop comparison) cares about that reference staying stable. `useContext` is not a performance tool at all — it's plumbing, an alternative to prop drilling; whether a consumer re-renders unnecessarily is governed by two entirely separate mechanisms.

## Essential Definitions

- **`useMemo(fn, deps)`** — re-invokes `fn` only when a `deps` value changed (`Object.is`); avoids re-running expensive computations.
- **`useCallback(fn, deps)`** — same idea for function identity; internally a thin wrapper over `useMemo(() => fn, deps)`.
- **`React.memo(Component)`** — skips re-rendering when props are shallowly equal to last time; the actual consumer of the reference stability the two hooks above provide.
- **`useContext(Context)`** — reads the nearest `<Context.Provider>` value, re-rendering the reading component whenever that value changes; this read **bypasses `memo`'s prop comparison entirely**.

## Decision Table

| Question | Answer |
|---|---|
| Computation genuinely expensive AND something downstream depends on result stability? | `useMemo` |
| Function passed to a `memo()`'d component or into another hook's dep array? | `useCallback` — otherwise a plain inline function is simpler |
| Re-renders actually measured as a problem (Profiler, not a guess)? | Only then consider `memo()` |
| Value needed several levels deep through components that don't use it? | `useContext`, primarily for structure — not a performance fix by itself |

**Unwanted consumer re-render — two SEPARATE causes, both must be fixed:**

| Cause | Fix |
|---|---|
| Non-memoized component + parent re-rendered | `memo()` |
| Subscribed context value's reference changed | Split the context into narrower pieces |

## Key Numbers (real, verified in a running React 19.2.8 + Vite app)

- `useMemo`: memoized computation counter moved 2→4 across 3 clicks (only the relevant one); unmemoized moved 2→8 (every render).
- `React.memo` defeated by inline handler: Child A (inline `onClick`) render count 2→6; Child B (`useCallback`'d handler) stayed at 2.
- Context re-render mistake, caught by running the demo: with NO consumers memoized, splitting context into two had zero effect — every consumer re-rendered on an unrelated `count` change regardless. After wrapping consumers in `memo()`: combined-context consumer still went 2→4 (shared value object is a new reference each time); split-context consumer stayed at 2.

## Common Pitfalls

- Wrapping values in `useMemo`/`useCallback` "for performance" with no `memo()`'d consumer or genuine expense downstream — pure overhead, zero benefit.
- Assuming `React.memo` alone protects against Context-driven re-renders — a `useContext` read inside a memoized component re-renders it regardless of the memo wrapper.
- Assuming splitting a Context fixes re-renders without also memoizing consumers — this chapter's own first-draft demo made exactly this mistake and measured zero effect.
- Passing a fresh object/array literal as a Context's `value` every render — even unrelated context changes look like "a new value" to every consumer.

## Interview Answer Skeleton

**30-sec:** `useMemo` skips recomputing when deps are unchanged; `useCallback` does the same for function references, mattering specifically because it lets `memo()`'d children skip re-rendering. `useContext` avoids prop drilling but isn't itself a performance optimization — a consumer re-renders on its own subscribed value changing, and `memo()` is separately needed to stop it re-rendering just because its parent did.

**2-min:** Walk through the referential-equality framing with the real counter proof (2→4 vs 2→8), then explain Context's two separate re-render causes (parent propagation, fixed by `memo()`; subscribed-value change, fixed by splitting contexts) and how this chapter's own demo initially fixed only one, producing a result that looked like context-splitting "did nothing" until `memo()` was added.

**Whiteboard:** Tree: `Parent` → `Provider` → `ConsumerA`/`ConsumerB`. Arrow from `Parent`'s state to itself re-rendering, then straight down to both consumers labeled "always, unless memo()'d" (cause #1, unrelated to Context). Separate arrow from `Provider`'s value only to `ConsumerA` labeled "only if its context read changed" (cause #2). Two arrows, two fixes, both needed.

**Senior-level framing:** Separates the two distinct causes of an unwanted re-render unprompted and states which fix addresses which; doesn't blame "Context" alone.

## Production Warning Signs

- A component still re-renders after `React.memo` is applied — check for (1) an inline function/object prop being passed a fresh reference every render, or (2) a `useContext` read whose value is genuinely changing (bypasses memo entirely).
- A team "fixes" re-renders by splitting a Context and sees no measurable change — the components downstream likely aren't memoized; profile before assuming context granularity is the cause.
- Reflexive `useMemo`/`useCallback` on cheap values — the comparison/storage overhead can exceed the cost of just recomputing.

## Related

- `handbook/frontend/react-hooks-useeffect-and-useref.md`
- `handbook/frontend/react-usereducer-and-custom-hooks.md`
- `00-project/frontend-topic-register.md`
