import { useState } from 'react';
import ErrorBoundary from '../components/ErrorBoundary';

function Widget({ label, shouldCrash }) {
  if (shouldCrash) {
    throw new Error(`${label} crashed`);
  }
  return <p data-testid={`widget-${label}`}>{label}: OK</p>;
}

function widgetFallback(error, reset) {
  return (
    <div>
      <p>Widget crashed: {error.message}</p>
      <button onClick={reset}>Reset this widget</button>
    </div>
  );
}

// Real, side-by-side contrast: TOP ROW gives each widget its OWN boundary
// (crashing one leaves its siblings rendering normally); BOTTOM ROW wraps
// all three widgets in a SINGLE shared boundary (crashing one blanks the
// entire row, including widgets that never threw).
export default function GranularBoundariesDemo() {
  const [granularCrash, setGranularCrash] = useState({ A: false, B: false, C: false });
  const [granularKeys, setGranularKeys] = useState({ A: 0, B: 0, C: 0 });
  const [sharedCrash, setSharedCrash] = useState({ A: false, B: false, C: false });
  const [sharedKey, setSharedKey] = useState(0);

  return (
    <section className="demo">
      <h2>2. Granular vs. shared boundaries — real blast-radius contrast</h2>

      <div className="field">
        <h3>Granular (one boundary per widget)</h3>
        <button onClick={() => setGranularCrash((s) => ({ ...s, A: true }))}>
          Crash Widget A (granular)
        </button>
        <div data-testid="granular-row" style={{ display: 'flex', gap: '12px' }}>
          {['A', 'B', 'C'].map((id) => (
            <ErrorBoundary
              key={`${id}-${granularKeys[id]}`}
              fallback={widgetFallback}
              onReset={() => {
                setGranularCrash((s) => ({ ...s, [id]: false }));
                setGranularKeys((k) => ({ ...k, [id]: k[id] + 1 }));
              }}
            >
              <Widget label={id} shouldCrash={granularCrash[id]} />
            </ErrorBoundary>
          ))}
        </div>
      </div>

      <div className="field">
        <h3>Shared (one boundary for all three)</h3>
        <button onClick={() => setSharedCrash((s) => ({ ...s, A: true }))}>
          Crash Widget A (shared)
        </button>
        <div data-testid="shared-row">
          <ErrorBoundary
            key={sharedKey}
            fallback={(error, reset) => (
              <div data-testid="shared-fallback">
                <p>Entire row crashed: {error.message}</p>
                <button
                  onClick={() => {
                    setSharedCrash({ A: false, B: false, C: false });
                    setSharedKey((k) => k + 1);
                    reset();
                  }}
                >
                  Reset row
                </button>
              </div>
            )}
          >
            <div style={{ display: 'flex', gap: '12px' }}>
              {['A', 'B', 'C'].map((id) => (
                <Widget key={id} label={id} shouldCrash={sharedCrash[id]} />
              ))}
            </div>
          </ErrorBoundary>
        </div>
      </div>
    </section>
  );
}
