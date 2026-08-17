import { useState, useCallback, useRef, memo } from 'react';

// F-107b: React.memo skips a child's re-render if its props are shallowly
// equal to last time -- but an inline arrow function `() => {...}` creates a
// BRAND NEW function reference on every parent render, which is never equal
// to the previous one, defeating memo entirely. useCallback fixes this by
// returning the SAME function reference across renders (as long as its own
// deps don't change).

const renderCounts = { withInline: 0, withCallback: 0 };

const ChildWithInlineHandler = memo(function ChildWithInlineHandler({ onClick }) {
  renderCounts.withInline += 1;
  return <span>Child A (inline handler) rendered {renderCounts.withInline} times</span>;
});

const ChildWithCallbackHandler = memo(function ChildWithCallbackHandler({ onClick }) {
  renderCounts.withCallback += 1;
  return <span>Child B (useCallback handler) rendered {renderCounts.withCallback} times</span>;
});

export default function MemoizedChildDemo() {
  const [unrelated, setUnrelated] = useState(0);

  // A brand new arrow function is created on EVERY render of this parent.
  const inlineHandler = () => console.log('clicked A');

  // useCallback returns the SAME function reference across renders, since
  // its dependency array is empty.
  const stableHandler = useCallback(() => console.log('clicked B'), []);

  return (
    <div className="demo-block">
      <h3>F-107b: React.memo defeated by an inline function prop, fixed by useCallback</h3>
      <button type="button" onClick={() => setUnrelated((u) => u + 1)}>
        Trigger parent re-render ({unrelated}) — watch Child A's count climb, Child B's stay put
      </button>
      <div>
        <ChildWithInlineHandler onClick={inlineHandler} />
      </div>
      <div>
        <ChildWithCallbackHandler onClick={stableHandler} />
      </div>
    </div>
  );
}
