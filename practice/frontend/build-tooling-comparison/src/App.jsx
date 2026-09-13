import { Suspense, lazy, useState } from "react";
import { add } from "./mathUtils";

const LazyPanel = lazy(() => import("./LazyPanel.jsx"));

export default function App() {
  const [showLazy, setShowLazy] = useState(false);

  return (
    <main>
      <h1>build-tooling-comparison (F-301)</h1>
      <p data-testid="sum">2 + 3 = {add(2, 3)}</p>
      <button onClick={() => setShowLazy(true)}>Load lazy panel</button>
      {showLazy && (
        <Suspense fallback={<p>Loading...</p>}>
          <LazyPanel />
        </Suspense>
      )}
    </main>
  );
}
