import { useState, useRef } from 'react';

// F-112c: React batches multiple setState calls made within the same event
// handler into a SINGLE render + commit, not one per call. `commitCount`
// (incremented directly in the render body, the same idempotent-under-
// StrictMode pattern established in earlier chapters) gives real ground
// truth for how many times this component actually re-rendered.

export default function BatchingDemo() {
  const [a, setA] = useState(0);
  const [b, setB] = useState(0);
  const [c, setC] = useState(0);
  const commitCount = useRef(0);
  commitCount.current += 1;

  return (
    <div className="demo-block">
      <h3>F-112c: multiple setState calls in one handler → one commit, not three</h3>
      <p>a={a}, b={b}, c={c}</p>
      <p data-testid="commit-count">Commits so far: {commitCount.current}</p>
      <button
        type="button"
        onClick={() => {
          // Three separate state updates, same handler, same tick.
          setA((v) => v + 1);
          setB((v) => v + 1);
          setC((v) => v + 1);
        }}
      >
        Update all three at once (batched — expect ONE new commit)
      </button>
      <button type="button" onClick={() => setA((v) => v + 1)}>
        Update only "a" (one update — also one commit, for comparison)
      </button>
    </div>
  );
}
