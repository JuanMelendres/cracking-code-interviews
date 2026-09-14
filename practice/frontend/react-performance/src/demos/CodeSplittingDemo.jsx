import { Suspense, lazy, useState } from 'react';

// Real dynamic import — Vite/Rollup emits HeavyPanel as its OWN chunk file
// (verified in the app's own README against real `npm run build` output),
// and the browser only requests that chunk when this component actually
// renders, not at initial page load.
const HeavyPanel = lazy(() => import('./HeavyPanel'));

export default function CodeSplittingDemo() {
  const [showHeavy, setShowHeavy] = useState(false);

  return (
    <section className="demo">
      <h2>3. Code-splitting with React.lazy — real, separate chunk</h2>
      <button onClick={() => setShowHeavy(true)}>Load heavy panel</button>
      {showHeavy && (
        <Suspense fallback={<p data-testid="heavy-fallback">Loading heavy panel...</p>}>
          <HeavyPanel />
        </Suspense>
      )}
    </section>
  );
}
