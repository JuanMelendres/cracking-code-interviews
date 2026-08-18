import { memo, useMemo, useRef, useState } from 'react';

const ExpensiveChild = memo(function ExpensiveChild({ config }) {
  const renderCount = useRef(0);
  renderCount.current += 1;
  return (
    <p>
      child render count: {renderCount.current} (config: {config.label})
    </p>
  );
});

// A common, genuine gotcha: React.memo does a SHALLOW prop comparison. An
// inline object literal (`{ label: 'x' }`) is a NEW reference every parent
// render, so memo's comparison always sees "different props" and re-renders
// the child anyway — memo alone does nothing here.
function UnstablePropParent() {
  const [unrelatedCount, setUnrelatedCount] = useState(0);
  return (
    <div className="field">
      <h3>memo() alone, with an unstable object prop</h3>
      <button onClick={() => setUnrelatedCount((c) => c + 1)}>
        Trigger unrelated parent re-render
      </button>
      <p>unrelated count: {unrelatedCount}</p>
      <ExpensiveChild config={{ label: 'static' }} />
    </div>
  );
}

// The fix: useMemo gives the object a STABLE reference across renders
// (until its own dependencies change), so memo's shallow comparison now
// correctly sees "same props" and skips the child re-render.
function StablePropParent() {
  const [unrelatedCount, setUnrelatedCount] = useState(0);
  const config = useMemo(() => ({ label: 'static' }), []);
  return (
    <div className="field">
      <h3>memo() + useMemo — stable reference</h3>
      <button onClick={() => setUnrelatedCount((c) => c + 1)}>
        Trigger unrelated parent re-render
      </button>
      <p>unrelated count: {unrelatedCount}</p>
      <ExpensiveChild config={config} />
    </div>
  );
}

export default function MemoizationStrategyDemo() {
  return (
    <section className="demo">
      <h2>2. Memoization strategy — real proof memo() alone can silently fail</h2>
      <UnstablePropParent />
      <StablePropParent />
    </section>
  );
}
