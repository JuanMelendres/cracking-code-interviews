import { useState, useMemo, useRef } from 'react';

// F-107a: useMemo skips recomputation when its dependencies haven't changed.
// `computeCount` is a plain ref, incremented directly inside the expensive
// function itself -- real ground truth for how many times the computation
// actually ran, independent of how many times the component rendered.

function expensiveSumOfSquares(n, counterRef) {
  counterRef.current += 1;
  let total = 0;
  for (let i = 0; i < n; i++) {
    total += i * i;
  }
  return total;
}

export default function ExpensiveMemoDemo() {
  const [n, setN] = useState(2_000_000);
  const [unrelated, setUnrelated] = useState(0);
  const memoComputeCount = useRef(0);
  const plainComputeCount = useRef(0);

  // Memoized: only re-runs expensiveSumOfSquares when `n` changes.
  const memoizedResult = useMemo(
    () => expensiveSumOfSquares(n, memoComputeCount),
    [n]
  );

  // NOT memoized: runs on EVERY render, regardless of what changed.
  const plainResult = expensiveSumOfSquares(n, plainComputeCount);

  return (
    <div className="demo-block">
      <h3>F-107a: useMemo — skipping recomputation when deps are unchanged</h3>
      <button type="button" onClick={() => setUnrelated((u) => u + 1)}>
        Trigger unrelated re-render ({unrelated})
      </button>
      <button type="button" onClick={() => setN((v) => v + 1)}>
        Change n (currently {n}) — the ONLY thing memoized computation depends on
      </button>
      <p data-testid="memo-compute-count">
        Memoized version actually computed: <strong>{memoComputeCount.current}</strong> times
      </p>
      <p data-testid="plain-compute-count">
        Non-memoized version actually computed: <strong>{plainComputeCount.current}</strong> times
      </p>
      <p>Result (both agree): {memoizedResult === plainResult ? memoizedResult : 'MISMATCH'}</p>
    </div>
  );
}
