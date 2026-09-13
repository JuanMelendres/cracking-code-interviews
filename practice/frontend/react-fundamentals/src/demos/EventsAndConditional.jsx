import { useState } from 'react';

// F-104 (part 1): synthetic events and conditional rendering. React wraps
// native DOM events in a SyntheticEvent for cross-browser consistency, but the
// handler-call semantics are otherwise ordinary JS functions.
export default function EventsAndConditional() {
  const [status, setStatus] = useState('idle');

  return (
    <div className="demo-block">
      <h3>F-104a: Events and conditional rendering</h3>
      <button type="button" onClick={() => setStatus('loading')}>
        Start
      </button>
      <button type="button" onClick={() => setStatus('done')}>
        Finish
      </button>
      <button type="button" onClick={() => setStatus('idle')}>
        Reset
      </button>

      {/* Conditional rendering: a plain JS expression, no special syntax */}
      {status === 'idle' && <p data-testid="status">Idle — click Start.</p>}
      {status === 'loading' && <p data-testid="status">Loading…</p>}
      {status === 'done' && <p data-testid="status">Done!</p>}
    </div>
  );
}
