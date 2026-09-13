import { useState, useEffect } from 'react';

// F-105a: the dependency array controls WHEN an effect re-runs, not whether
// it runs at all. React compares each dependency to its previous render's
// value (Object.is comparison) -- if none changed, the effect body is
// skipped entirely on that render.

export default function EffectDependencyDemo() {
  const [count, setCount] = useState(0);
  const [unrelated, setUnrelated] = useState(0);
  const [log, setLog] = useState([]);

  useEffect(() => {
    // Runs on mount, and again ONLY when `count` changes -- NOT when
    // `unrelated` changes, even though this component re-renders either way.
    setLog((prev) => [...prev, `effect ran, count=${count}`]);
  }, [count]);

  return (
    <div className="demo-block">
      <h3>F-105a: dependency array — effect runs only when its deps change</h3>
      <button type="button" onClick={() => setCount((c) => c + 1)}>
        Increment count (triggers effect)
      </button>
      <button type="button" onClick={() => setUnrelated((u) => u + 1)}>
        Increment unrelated ({unrelated}) — re-renders, does NOT trigger effect
      </button>
      <p>count: {count}</p>
      <ul data-testid="effect-log">
        {log.map((entry, i) => (
          <li key={i}>{entry}</li>
        ))}
      </ul>
    </div>
  );
}
