# React Memoization & Context demo app (F-107 useMemo/useCallback, F-108 useContext)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md`](../../../syllabus/21-frontend-web/react-usememo-usecallback-and-usecontext.md).

## Run it

```bash
npm install
npm run dev
```

Four sections:

1. **F-107a `useMemo`** — an expensive computation, memoized vs. not, with real recompute counts.
2. **F-107b `React.memo` + `useCallback`** — a memoized child defeated by an inline handler, fixed by `useCallback`.
3. **F-108a Prop drilling vs. Context** — the same value delivered two structurally different ways.
4. **F-108b Context re-render cost** — a combined context vs. two split contexts, both with memoized consumers.

## Captured evidence (real browser session)

### F-107a — memoized vs. non-memoized recompute counts
Sequence: 2 clicks on "Trigger unrelated re-render", then 1 click on "Change n".

```
Memoized version actually computed: 4 times   (mount: 2, then +2 ONLY on the n-change click)
Non-memoized version actually computed: 8 times (mount: 2, +2 per unrelated click x2, +2 on n-change)
```

The memoized version's count only moved on the one click that actually changed its dependency; the non-memoized version recomputed on every single render, including the two clicks that had nothing to do with it.

### F-107b — `React.memo` defeated by an inline handler, protected by `useCallback`
Sequence: 2 clicks on "Trigger parent re-render".

```
Child A (inline handler) rendered 6 times   (mount 2, +2 per click x2 -- memo never helped)
Child B (useCallback handler) rendered 2 times (unchanged -- memo fully protected it)
```

### F-108b — a real, caught modeling mistake, fixed
The first draft of this demo memoized nothing. Result: incrementing `count` re-rendered **every** consumer in **both** the combined-context and the "split-context" scenarios — because with no `memo()` anywhere, any child re-renders whenever its parent re-renders, entirely independent of Context. That made the context-splitting "fix" look like it did nothing, which was not the intended lesson. Fixed by wrapping every consumer in `memo()`. Real result after the fix, one click on "Increment count":

```
Scenario A (combined context, both consumers memoized):
  CountConsumer rendered 4x, count=1
  FlagConsumer  rendered 4x, flag=true   <- re-rendered even though flag never changed

Scenario B (two separate contexts, both consumers memoized):
  CountConsumer rendered 4x, count=1
  FlagConsumer  rendered 2x, flag=true   <- UNCHANGED -- correctly isolated
```

`memo()` alone isolates a component from its *parent's* re-renders; splitting the context is what isolates it from an *unrelated field's* re-renders once wrapped in `memo()`. Neither alone is sufficient — both real facts, both caught by running the demo, not assumed.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors.
- Every number above read directly from the live DOM after real clicks, not estimated.
- `npm run build` — clean production build, zero errors/warnings.
