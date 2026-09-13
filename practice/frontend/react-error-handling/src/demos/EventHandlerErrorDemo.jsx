import { useEffect, useState } from 'react';
import ErrorBoundary from '../components/ErrorBoundary';

function ThrowsInHandler() {
  function handleClick() {
    // Thrown from an EVENT HANDLER, not during render. Error boundaries
    // only catch errors during render, in lifecycle methods, and in
    // constructors of the tree below them — this will NOT be caught by
    // the wrapping ErrorBoundary, a common and genuine interview gotcha.
    throw new Error('Thrown from onClick, not from render');
  }

  return (
    <div>
      <button onClick={handleClick}>Throw in event handler (uncaught by boundary)</button>
      <p data-testid="handler-still-mounted">This paragraph is still here — the boundary never triggered.</p>
    </div>
  );
}

function HandledClick() {
  const [error, setError] = useState('');

  function handleClick() {
    try {
      throw new Error('Same error, but caught locally with try/catch');
    } catch (e) {
      setError(e.message);
    }
  }

  return (
    <div>
      <button onClick={handleClick}>Throw in event handler (handled manually)</button>
      <p data-testid="handled-error">{error || '(no error yet)'}</p>
    </div>
  );
}

// Real, observed proof that error boundaries do NOT catch event-handler
// errors: a global `window` error listener captures the uncaught error
// (proving it escaped React entirely) while the boundary's own fallback
// never renders and the sibling paragraph stays mounted throughout.
export default function EventHandlerErrorDemo() {
  const [windowCaughtMessage, setWindowCaughtMessage] = useState('');

  useEffect(() => {
    function onWindowError(event) {
      setWindowCaughtMessage(event.message);
      event.preventDefault();
    }
    window.addEventListener('error', onWindowError);
    return () => window.removeEventListener('error', onWindowError);
  }, []);

  return (
    <section className="demo">
      <h2>3. Event-handler errors — NOT caught by error boundaries</h2>
      <ErrorBoundary
        fallback={(error, reset) => (
          <div data-testid="handler-boundary-fallback">
            <p>Boundary fallback shown: {error.message}</p>
            <button onClick={reset}>Reset</button>
          </div>
        )}
      >
        <ThrowsInHandler />
      </ErrorBoundary>
      <p data-testid="window-caught-message">
        caught by window 'error' listener (NOT the boundary): {windowCaughtMessage || '(none yet)'}
      </p>
      <HandledClick />
    </section>
  );
}
