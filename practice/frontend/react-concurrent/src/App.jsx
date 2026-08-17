import './App.css';
import TransitionDemo from './demos/TransitionDemo';
import DeferredValueDemo from './demos/DeferredValueDemo';
import SuspenseDataDemo from './demos/SuspenseDataDemo';

// Real demo app backing handbook/frontend/react-concurrent-rendering.md
// (F-113). Verified live in a browser, including the Suspense fallback
// window and the isPending/isStale transition logs.
function App() {
  return (
    <div className="app-root">
      <h1>Concurrent React — F-113</h1>
      <TransitionDemo />
      <DeferredValueDemo />
      <SuspenseDataDemo />
    </div>
  );
}

export default App;
