import { useState } from 'react';

// F-112b: the flip side of node reuse -- when an element's TYPE changes at
// a given position (a <div> becoming a <span>, or one component becoming a
// different component), React cannot diff the old and new subtrees against
// each other at all. It destroys the entire old subtree (unmounting every
// component in it, discarding all their state) and builds a brand new one
// from scratch. Same-type prop changes never do this; type changes always do.

function StatefulCounter() {
  const [count, setCount] = useState(0);
  return (
    <button type="button" onClick={() => setCount((c) => c + 1)} data-testid="stateful-counter">
      Inner counter: {count} (click to increment)
    </button>
  );
}

export default function TypeChangeRemountDemo() {
  const [wrapWithDiv, setWrapWithDiv] = useState(true);
  const [highlighted, setHighlighted] = useState(false);

  return (
    <div className="demo-block">
      <h3>F-112b: changing an element's TYPE force-remounts its subtree</h3>
      <p>
        Click the inner counter a few times to raise it above 0. Then click
        "Toggle wrapper type" — the wrapper changes from <code>&lt;div&gt;</code> to{' '}
        <code>&lt;section&gt;</code> (a genuine type change at this position), and the counter
        resets to 0: React unmounted the old subtree and mounted a fresh one.
        Compare with "Toggle highlight," which changes a PROP on the same{' '}
        <code>&lt;div&gt;</code> type — the counter's state survives.
      </p>
      <button type="button" onClick={() => setWrapWithDiv((w) => !w)}>
        Toggle wrapper type (currently {wrapWithDiv ? '<div>' : '<section>'})
      </button>
      <button type="button" onClick={() => setHighlighted((h) => !h)}>
        Toggle highlight (same type, just a prop — currently {highlighted ? 'ON' : 'OFF'})
      </button>
      {wrapWithDiv ? (
        <div style={{ background: highlighted ? '#fff3cd' : 'transparent', padding: 8 }}>
          <StatefulCounter />
        </div>
      ) : (
        <section style={{ background: highlighted ? '#fff3cd' : 'transparent', padding: 8 }}>
          <StatefulCounter />
        </section>
      )}
    </div>
  );
}
