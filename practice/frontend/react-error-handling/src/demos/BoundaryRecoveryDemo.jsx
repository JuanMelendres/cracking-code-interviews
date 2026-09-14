import { useState } from 'react';
import ErrorBoundary from '../components/ErrorBoundary';

const CRASH_THRESHOLD = 3;

function CrashingCounter() {
  const [count, setCount] = useState(0);

  if (count >= CRASH_THRESHOLD) {
    // A real render-phase throw — this is exactly what error boundaries
    // are designed to catch: an error thrown while React is rendering.
    throw new Error(`Count exceeded safe threshold (reached ${count})`);
  }

  return (
    <div>
      <p data-testid="counter-value">count: {count}</p>
      <button onClick={() => setCount((c) => c + 1)}>Increment</button>
    </div>
  );
}

// Real, catchable render-phase error, a real fallback, and a real recovery
// path via `key` remount (bumping `resetKey` forces React to treat the
// subtree as brand new, discarding the crashed instance's state).
export default function BoundaryRecoveryDemo() {
  const [resetKey, setResetKey] = useState(0);
  const [lastCaughtMessage, setLastCaughtMessage] = useState('');

  return (
    <section className="demo">
      <h2>1. Error boundary catch + reset — real render-phase crash</h2>
      <ErrorBoundary
        key={resetKey}
        onError={(error) => setLastCaughtMessage(error.message)}
        onReset={() => setResetKey((k) => k + 1)}
        fallback={(error, reset) => (
          <div data-testid="boundary-fallback">
            <p>Something went wrong: {error.message}</p>
            <button onClick={reset}>Reset</button>
          </div>
        )}
      >
        <CrashingCounter />
      </ErrorBoundary>
      <p data-testid="last-caught-message">
        last error caught by boundary: {lastCaughtMessage || '(none yet)'}
      </p>
    </section>
  );
}
