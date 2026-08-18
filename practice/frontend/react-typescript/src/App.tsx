import { useState } from 'react';
import './App.css';
import TypedComponents from './demos/TypedComponents';
import { GenericListDemo } from './demos/GenericList';
import Alert from './demos/VariantAlert';

// Real demo app backing handbook/frontend/react-typescript.md (F-119).
// Every claim in the chapter is verified against real `tsc -b` output —
// see README.md for the captured compiler errors this app's demos were
// deliberately misused to produce, then fixed.
function App() {
  const [showError, setShowError] = useState(true);

  return (
    <div className="app-root">
      <h1>React + TypeScript — F-119</h1>

      <section>
        <h2>Typed props, state, hooks (incl. exhaustive useReducer)</h2>
        <TypedComponents />
      </section>

      <section>
        <h2>Generic component (same List, two different T)</h2>
        <GenericListDemo />
      </section>

      <section>
        <h2>Discriminated union props (variant-specific requirements)</h2>
        <Alert variant="info" message="Saved." />
        <Alert variant="success" message="Deployed." />
        {showError && (
          <Alert variant="error" message="Failed to save." onRetry={() => setShowError(false)} />
        )}
      </section>
    </div>
  );
}

export default App;
