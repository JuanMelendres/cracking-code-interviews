import { createContext, useContext, useState, useRef, useMemo, memo } from 'react';

// F-108b: two SEPARATE re-render costs, easy to conflate.
//
// (1) By default, EVERY descendant re-renders whenever an ancestor
//     re-renders, REGARDLESS of Context -- wrapping a consumer in `memo()`
//     is what actually blocks that (memo skips a re-render triggered by a
//     re-rendering parent, as long as props are shallow-equal -- these
//     components take no props, so memo alone would fully insulate them
//     from parent re-renders).
// (2) But a memoized component that calls useContext() will STILL re-render
//     whenever that context's VALUE changes -- context reads bypass memo's
//     prop check entirely. So a combined context whose value object is
//     `{ count, flag }` re-renders EVERY consumer whenever count changes,
//     because the value object itself is a new reference every time --
//     even a memoized consumer that only reads `flag`.
//
// This file's first draft memoized nothing, and BOTH scenarios (combined
// and "split") showed every consumer re-rendering -- because with no
// memo() anywhere, cause (1) alone was enough to re-render everything,
// making the context-splitting fix look like it did nothing. That was a
// real, caught modeling mistake, not the intended lesson -- fixed by adding
// memo() to every consumer below, which isolates cause (2) as the only
// thing left that can force a re-render.

// ---- Scenario A: one combined context, one value object ----
const CombinedContext = createContext(null);

const CountConsumerCombined = memo(function CountConsumerCombined() {
  const { count } = useContext(CombinedContext);
  const renders = useRef(0);
  renders.current += 1;
  return <span data-testid="combined-count-renders">CountConsumer (combined ctx) rendered {renders.current}x, count={count}</span>;
});

const FlagConsumerCombined = memo(function FlagConsumerCombined() {
  const { flag } = useContext(CombinedContext);
  const renders = useRef(0);
  renders.current += 1;
  return <span data-testid="combined-flag-renders">FlagConsumer (combined ctx) rendered {renders.current}x, flag={String(flag)} — reads ONLY flag, never touches count</span>;
});

// ---- Scenario B: two separate, narrow contexts ----
const CountContext = createContext(null);
const FlagContext = createContext(null);

const CountConsumerSplit = memo(function CountConsumerSplit() {
  const count = useContext(CountContext);
  const renders = useRef(0);
  renders.current += 1;
  return <span data-testid="split-count-renders">CountConsumer (split ctx) rendered {renders.current}x, count={count}</span>;
});

const FlagConsumerSplit = memo(function FlagConsumerSplit() {
  const flag = useContext(FlagContext);
  const renders = useRef(0);
  renders.current += 1;
  return <span data-testid="split-flag-renders">FlagConsumer (split ctx) rendered {renders.current}x, flag={String(flag)} — subscribes ONLY to FlagContext</span>;
});

export default function ContextRerenderCostDemo() {
  const [count, setCount] = useState(0);
  const [flag] = useState(true); // never changes in this demo -- the point is COUNT changing

  // Even memoized this way, this object is a NEW reference every time count
  // changes (by design -- count is one of its fields), so it correctly
  // still re-renders both combined consumers when count changes. It's
  // included so the value object isn't ALSO recreated for unrelated
  // reasons (there are none here, but this mirrors real code).
  const combinedValue = useMemo(() => ({ count, flag }), [count, flag]);

  return (
    <div className="demo-block">
      <h3>F-108b: a Provider's value change re-renders its memoized consumers — split contexts to isolate that</h3>
      <button type="button" onClick={() => setCount((c) => c + 1)}>
        Increment count ({count}) — flag never changes
      </button>

      <p><strong>Scenario A — one combined context (both consumers memoized):</strong></p>
      <CombinedContext.Provider value={combinedValue}>
        <div><CountConsumerCombined /></div>
        <div><FlagConsumerCombined /></div>
      </CombinedContext.Provider>

      <p><strong>Scenario B — two separate contexts (both consumers memoized):</strong></p>
      <CountContext.Provider value={count}>
        <FlagContext.Provider value={flag}>
          <div><CountConsumerSplit /></div>
          <div><FlagConsumerSplit /></div>
        </FlagContext.Provider>
      </CountContext.Provider>
    </div>
  );
}
