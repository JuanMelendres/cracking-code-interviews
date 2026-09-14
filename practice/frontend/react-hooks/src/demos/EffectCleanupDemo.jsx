import { useState, useEffect } from 'react';

// F-105b: a REAL, measurable interval leak. `activeIntervalCount` is a
// module-level counter (not React state -- deliberately outside React, so it
// survives unmounts and gives us ground truth about real setInterval calls
// still running in the browser).
let activeIntervalCount = 0;

function LeakyTicker() {
  const [ticks, setTicks] = useState(0);
  useEffect(() => {
    activeIntervalCount++;
    setInterval(() => setTicks((t) => t + 1), 200);
    // BUG: no cleanup function returned -- this interval keeps running
    // forever, even after this component unmounts.
  }, []);
  return <span>Leaky ticker: {ticks}</span>;
}

function CleanTicker() {
  const [ticks, setTicks] = useState(0);
  useEffect(() => {
    activeIntervalCount++;
    const id = setInterval(() => setTicks((t) => t + 1), 200);
    // The cleanup function: React calls this on unmount (and before every
    // re-run of the effect), guaranteeing the interval actually stops.
    return () => {
      clearInterval(id);
      activeIntervalCount--;
    };
  }, []);
  return <span>Clean ticker: {ticks}</span>;
}

export default function EffectCleanupDemo() {
  const [showLeaky, setShowLeaky] = useState(false);
  const [showClean, setShowClean] = useState(false);
  const [, forceRender] = useState(0);

  return (
    <div className="demo-block">
      <h3>F-105b: cleanup functions — a real, measurable interval leak</h3>
      <p>
        Toggle each ticker on/off a few times, then click "Refresh counter"
        without toggling anything. The leaky ticker's count keeps climbing
        even while hidden; the clean ticker's count returns to 0 every time
        it's hidden.
      </p>
      <button type="button" onClick={() => setShowLeaky((s) => !s)}>
        {showLeaky ? 'Hide' : 'Show'} leaky ticker
      </button>
      <button type="button" onClick={() => setShowClean((s) => !s)}>
        {showClean ? 'Hide' : 'Show'} clean ticker
      </button>
      <button type="button" onClick={() => forceRender((n) => n + 1)}>
        Refresh counter (no toggling)
      </button>
      <p data-testid="active-interval-count">
        Real active interval count (module-level, ground truth): {activeIntervalCount}
      </p>
      {showLeaky && <LeakyTicker />}
      {showClean && <CleanTicker />}
    </div>
  );
}
