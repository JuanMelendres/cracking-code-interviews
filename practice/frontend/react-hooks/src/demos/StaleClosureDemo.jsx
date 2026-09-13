import { useState, useEffect, useRef } from 'react';

// F-105c: the classic stale-closure bug. An effect with `[]` deps runs its
// setup function EXACTLY ONCE. Any function created inside that effect
// closes over the state values as they were AT THAT MOMENT -- it never sees
// later re-renders' values, because the effect itself never re-runs to
// create a fresh closure.

function BuggyLogger({ count }) {
  const [log, setLog] = useState([]);
  useEffect(() => {
    // Empty deps: this setInterval callback is created ONCE, at mount, and
    // permanently closes over whatever `count` was AT MOUNT TIME (0).
    const id = setInterval(() => {
      setLog((prev) => [...prev, count].slice(-5));
    }, 400);
    return () => clearInterval(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []); // <- the bug: `count` is used inside but not listed as a dependency
  return (
    <div>
      <strong>Buggy (stale) logger:</strong> [{log.join(', ')}]
    </div>
  );
}

function FixedLogger({ count }) {
  const [log, setLog] = useState([]);
  const countRef = useRef(count);
  countRef.current = count; // always kept current, read fresh inside the interval

  useEffect(() => {
    const id = setInterval(() => {
      setLog((prev) => [...prev, countRef.current].slice(-5));
    }, 400);
    return () => clearInterval(id);
  }, []); // still empty deps -- but reads via a ref, so no staleness
  return (
    <div>
      <strong>Fixed (ref-based) logger:</strong> [{log.join(', ')}]
    </div>
  );
}

export default function StaleClosureDemo() {
  const [count, setCount] = useState(0);

  return (
    <div className="demo-block">
      <h3>F-105c: stale closures — a real, captured bug</h3>
      <p>
        Click "Increment" a few times over a couple seconds. Watch both
        loggers below: the buggy one keeps logging the count from when it
        mounted; the fixed one logs the real current count every tick.
      </p>
      <button type="button" onClick={() => setCount((c) => c + 1)}>
        Increment count (currently {count})
      </button>
      <BuggyLogger count={count} />
      <FixedLogger count={count} />
    </div>
  );
}
